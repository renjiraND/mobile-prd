package com.renrairah.bukalock;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

import static java.lang.Math.abs;

public class UnlockGestureActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView txtStatus;
    private ImageView imgStatus;
    private int status = 0; // 0 = idle, 1 = flat position, 2 = accelerated forward and waiting to be rotated, 2 = rotated and unlocked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_gesture);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
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
        }

        if (((y >= 3) && (z >= 10)) && (status == 1)){
            status = 2;
            txtStatus.setText("Rotate your phone to the left");
            imgStatus.setImageResource(R.drawable.ic_rotate);
        }
    }

    private void getGyroscope(SensorEvent event) {
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        if ((y < -2.5f) && (status == 2)){
            status = 3;
            txtStatus.setText("Door unlocked!");
            imgStatus.setImageResource(R.drawable.ic_unlock);
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
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
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
