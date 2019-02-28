package com.renrairah.bukalock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.BaseKeyListener;
import android.util.Log;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;
import static android.support.constraint.Constraints.TAG;
import static java.lang.Math.abs;

public class EmailBackgroundService extends Service {
    private boolean emailRegistered;
    private BackgroundMail format;
    private String useremail;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "On bind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        useremail = currentUser.getEmail();

        format = formatEmail(useremail, EmailBackgroundService.this);

        final DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = dbRefUsers.orderByChild("email").equalTo(useremail);
        Log.d(TAG, "Email : " + useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Ada user data snapshot");
                    emailRegistered = true;
                } else {
                    Log.d(TAG, "Tidak ada user data snapshot");
                    emailRegistered = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        super.onCreate();
        sendWarningEmail();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service on start command");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
    }

    private void sendWarningEmail(){
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("history");
        Query query = dbRef.orderByKey().limitToLast(2);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                History newHistory = dataSnapshot.getValue(History.class);
                String myKey = dataSnapshot.getKey();
                Date now = new Date();
                Date date = new Date(newHistory.getDate());
                long difference = abs(date.getTime() - now.getTime());
                Log.d(TAG, "Beda waktu : " + difference);
                Log.d(TAG, myKey);
                if (prevChildKey == null || myKey == prevChildKey){
                    Log.d(TAG, "Not kirim email");
                } else {
                    Log.d(TAG, prevChildKey);
                    int status = newHistory.getStatus();
                    if (status == 0 && difference < 5000 && emailRegistered) {
                        Log.d(TAG, "Harus kirim email");
                        //format.send();
                    } else {
                        Log.d(TAG, "Not kirim email");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public BackgroundMail formatEmail(String useremail, Context ctx){
        Log.d(TAG, "Masuk sendEmail");
        BackgroundMail bm = new BackgroundMail(ctx);
        bm.setGmailUserName("ifbukalock@gmail.com");
        bm.setGmailPassword("bukalock135");
        bm.setMailTo(useremail);
        bm.setFormSubject("BukaLock Warning!");
        bm.setFormBody("Warning! Someone unauthorized just trying to open your lock!");
        return bm;
    }
}
