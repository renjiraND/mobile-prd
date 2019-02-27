package com.renrairah.bukalock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

public class EmailBackgroundService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendWarningEmail(){
        BackgroundMail.newBuilder(this)
                .withUsername("ifbukalock@gmail.com")
                .withPassword("bukalock135")
                .withMailto("@gmail.com")
                .withSubject("BukaLock Warning!")
                .withBody("Warning! Someone unauthorized just trying to open your home!")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }
}
