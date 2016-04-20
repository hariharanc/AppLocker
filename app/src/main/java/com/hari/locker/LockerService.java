package com.hari.locker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hari.dbhelper.DatabaseHandler;
import com.hari.pin.PinActivity;
import com.hari.util.AppDetails;

public class LockerService extends Service {

    private DatabaseHandler mDatabaseHandler;
    private ArrayList<AppDetails> mArrAppDetails;
    private ArrayList<AppDetails> mArrUnlockApp;
    private Boolean isBackground = false;
    private static Timer timer = new Timer();
    private static Timer backTimer = new Timer();
    ActivityManager am = null;
    Context context = null;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    private ArrayList<String> arrayPackName = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent arg0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // Toast.makeText(this, "Service was Created",
        // Toast.LENGTH_LONG).show();
        mSharedPreferences = getSharedPreferences("hariPref",
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        _startService();
    }

    private void _startService() {

        timer.scheduleAtFixedRate(

                new TimerTask() {

                    public void run() {

                        try {
                            getForeGroundApp();
                            arrayPackName.clear();
                            Log.i(LockerService.class.toString(),
                                    "LockerService running...");
                        } catch (Exception e) {
                        }

                    }
                }, 300, 2000);

        Log.i(getClass().getSimpleName(),
                "FileScannerService Timer started....");
    }

    private void getForeGroundApp() {
        try {
            isApplicationSentToBackground(getApplicationContext());
            Log.i(LockerService.class.toString(), "LockerService DB"
                    + mDatabaseHandler);
            if (mDatabaseHandler == null) {
                mDatabaseHandler = new DatabaseHandler(
                        this.getApplicationContext());
            }

            am = (ActivityManager) this.getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> appProcesses = am
                    .getRunningAppProcesses();
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    try {
                        Log.i(LockerService.class.toString(),
                                "Froground  Name::" + appProcess.processName);

                        Log.i(LockerService.class.toString(),
                                "Froground Pref Name::"
                                        + mSharedPreferences.getString(
                                        "packName", ""));

                        mArrAppDetails = mDatabaseHandler
                                .getLockAppDetails("1");
                        Log.i(LockerService.class.toString(),
                                "LockService Foreground Name::"
                                        + appProcess.processName);

                        if (mArrAppDetails != null && mArrAppDetails.size() > 0) {
                            for (int i = 0; i < mArrAppDetails.size(); i++) {
                                Log.i(LockerService.class.toString(),
                                        "LockService Foreground name"
                                                + mArrAppDetails.get(i)
                                                .getAppPackName());
                                String packName = mArrAppDetails.get(i)
                                        .getAppPackName();
                                if (packName
                                        .equalsIgnoreCase(appProcess.processName)) {

                                    Intent intent = new Intent(
                                            this.getApplicationContext(),
                                            PinActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("packName",
                                            appProcess.processName);

                                    this.getApplicationContext().startActivity(
                                            intent);



                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(LockerService.class.toString(),
                                "LockerService Exce is" + e.getMessage());
                    }

                }

            }
        } catch (Exception e) {
            Log.i(LockerService.class.toString(),
                    "getForeGroundApp Exc" + e.getMessage());
        }
    }

    public void isApplicationSentToBackground(Context context) {
        try {
            am = (ActivityManager) this.getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> appProcesses = am
                    .getRunningAppProcesses();
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(LockerService.class.toString(), "Background  Name::"
                            + appProcess.processName);
                    arrayPackName.add(appProcess.processName.toString());
                }

            }

            Log.i(LockerService.class.toString(), "LockerService Array first"
                    + arrayPackName.get(0));

            if (mSharedPreferences.getString("packName", "") != null
                    && !mSharedPreferences.getString("packName", "")
                    .equalsIgnoreCase("")
                    && !mSharedPreferences.getString("packName", "")
                    .equalsIgnoreCase(arrayPackName.get(0))) {
                Log.i(LockerService.class.toString(), "Froground Pref Name::"
                        + mSharedPreferences.getString("packName", ""));
                if (mSharedPreferences.getString("packName", "") != null
                        && !mSharedPreferences.getString("packName", "")
                        .equalsIgnoreCase("")) {
                    String unlockPackName = mSharedPreferences.getString(
                            "packName", "");
                    mArrAppDetails = mDatabaseHandler.getUnLockeAppData(
                            unlockPackName, "0");

                    if (mArrAppDetails != null && mArrAppDetails.size() > 0) {
                        for (int i = 0; i < mArrAppDetails.size(); i++) {
                            mDatabaseHandler.upDateLockApp(mArrAppDetails
                                    .get(i).getAppPackName(), "1");
                            mEditor.putString("packName", "");
                            mEditor.commit();
                        }

                    }
                }

            }

        } catch (Exception e) {

        }

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
