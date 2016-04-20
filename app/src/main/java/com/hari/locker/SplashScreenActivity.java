package com.hari.locker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.hari.pin.PinActivity;
import com.hari.pin.SetPinActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ITDept on 06/04/16.
 */
public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences = getSharedPreferences("hariPref",
                Context.MODE_PRIVATE);
        initViews();
    }

    private void initViews() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                if (mSharedPreferences.getString("myPin", "").length() > 3) {
                    Intent pinIntent = new Intent(SplashScreenActivity.this, PinActivity.class);
                    pinIntent.putExtra("authentication", "authentication");
                    startActivity(pinIntent);
                } else {
                    Intent pinIntent = new Intent(SplashScreenActivity.this, SetPinActivity.class);
                    pinIntent.putExtra("pinOption", "setpin");
                    startActivity(pinIntent);
                }
            }
        }, 500);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
