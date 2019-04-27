package com.renrairah.bukalock;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.Timer;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Math.abs;
import static java.sql.Types.NULL;

public class UnlockGestureActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView txtStatus, txtLoc, txtDistance, txtKm;
    private ImageView imgStatus;
    private boolean isBackPressed = false;
    private int status = -1; // -1 = idle, 0 = on the location, 1 = flat position, 2 = accelerated forward and waiting to be rotated, 3 = rotated and unlocked, 4 = failed

    private Vibrator v;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Create");
        setContentView(R.layout.activity_unlock_gesture);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtDistance = (TextView) findViewById(R.id.distance);
        txtKm = (TextView) findViewById(R.id.txtKm);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        final SharedPreferences prefs = this.getSharedPreferences("<com.renrairah.bukalock>", Context.MODE_PRIVATE);
        int permission = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, permission);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            txtLoc.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

                            Location home = new Location("point B");

                            home.setLatitude(-6.900900900900901);
                            home.setLongitude(107.60840298);
                            status = prefs.getInt( "status" , -1);
                            Log.d(TAG, "SharedPref 1: " + prefs.getInt( "status",-1));

                            float distance = home.distanceTo(location)/1000;
                            txtDistance.setText(String.format("%.2f",distance));
                            if ((distance <= 1f) && (status == -1)){
                                status = 0;
                                txtStatus.setText("Lay your phone");
                                txtDistance.setVisibility(View.GONE);
                                txtKm.setVisibility(View.GONE);
                                txtLoc.setVisibility(View.GONE);
                                imgStatus.setImageResource(R.drawable.ic_flattening);
                            } else {
                                if (distance <= 1f){
                                    String text = prefs.getString( "text","");
                                    txtStatus.setText(prefs.getString( "text",""));
                                    txtDistance.setVisibility(View.GONE);
                                    txtKm.setVisibility(View.GONE);
                                    txtLoc.setVisibility(View.GONE);
                                    Log.d(TAG, "SharedPref : " + prefs.getString( "text","" ));
                                    if (text == "Lay your phone"){
                                        imgStatus.setImageResource(R.drawable.ic_flattening);
                                    } else {
                                        if (text == "Move your phone forward"){
                                            imgStatus.setImageResource(R.drawable.ic_forward);
                                        } else {
                                            if (text == "Rotate your phone to the left"){
                                                imgStatus.setImageResource(R.drawable.ic_rotate);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getGyroscope(sensorEvent);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        if (((abs(x) <= 1) && (abs(y) <= 1)) && ((z >= 10) && (status == 0))) {
            status = 1;
            txtStatus.setText("Move your phone forward");
            imgStatus.setImageResource(R.drawable.ic_forward);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }

        if (((y >= 3) && (z >= 10)) && (status == 1)){
            status = 2;
            txtStatus.setText("Rotate your phone to the left");
            imgStatus.setImageResource(R.drawable.ic_rotate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
    }

    private void getGyroscope(SensorEvent event) {
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        if ((y < -2.5f) && (status == 2)){
            SharedPreferences mValid = PreferenceManager.getDefaultSharedPreferences(this);
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            int valid =  mValid.getInt("valid", 0);
            if (valid == 1){
                String historyId = dbRef.child("history").push().getKey();
                History history = new History(1,System.currentTimeMillis(),1);
                dbRef.child("history").child(historyId).setValue(history);
                status = 3;
                txtStatus.setText("Door unlocked!");
                imgStatus.setImageResource(R.drawable.ic_unlock);
            } else {
                String historyId = dbRef.child("history").push().getKey();
                History history = new History(1,System.currentTimeMillis(),0);
                dbRef.child("history").child(historyId).setValue(history);
                status = 4;
                txtStatus.setText("You aren't eligible to unlock this door");
                imgStatus.setImageResource(R.drawable.ic_cancel);
            }
            sensorManager.unregisterListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(1000);
            }
        }
    }

    @Override
    protected void onResume() {
        // register this class as a listener for the orientation and
        // accelerometer sensors
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        if (!(isBackPressed)){
            SharedPreferences prefs = this.getSharedPreferences("<com.renrairah.bukalock>", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            TextView text = txtStatus;
            editor.putString("text", text.getText().toString());
            editor.putInt( "status", status);
            editor.commit();

            isBackPressed = false;
        }
        sensorManager.unregisterListener(this);

        Log.d(TAG, "Pause");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "Accuracy changed");
    }

    public static int getResourseId(Context context, String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }

    @Override
    public void onBackPressed()
    {
        isBackPressed = true;
        // code here to show dialog
        SharedPreferences pref = getSharedPreferences("<com.renrairah.bukalock>", Context.MODE_PRIVATE);
        pref.edit().remove("img").commit();
        pref.edit().remove("status").commit();
        pref.edit().remove("text").commit();
        Log.d(TAG, "SharedPref Remove: " + pref.getString( "text","" ));
        Log.d(TAG, "SharedPref 2 : " + pref.getInt( "status",NULL));
        super.onBackPressed();  // optional depending on your needs
    }
}