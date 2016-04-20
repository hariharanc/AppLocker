package com.hari.pin;

import com.hari.dbhelper.DatabaseHandler;
import com.hari.locker.MainActivity;
import com.hari.locker.R;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PinActivity extends AppCompatActivity implements OnClickListener {
    private Toolbar toolbar;
    private EditText mEdtPin;
    private Button mBtnDone;
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;
    private String packName;
    private DatabaseHandler mDatabaseHandler;
    private Boolean isAuthentication = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        mSharedPreferences = getSharedPreferences("hariPref",
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mDatabaseHandler = new DatabaseHandler(this);
        if (getIntent().getStringExtra("packName") != null) {
            packName = getIntent().getStringExtra("packName");
        } else if (getIntent().getStringExtra("authentication") != null && getIntent().getStringExtra("authentication").equalsIgnoreCase("authentication")) {
            isAuthentication = true;
        }
        initViews();

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        mEdtPin = (EditText) findViewById(R.id.edit_pin);
        mBtnDone = (Button) findViewById(R.id.btn_sbt);
        mBtnDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sbt:
                if (mEdtPin.getText().toString().length() == 0) {
                    alertDialog("Please Enter Your PIN!!!");
                } else if (!mEdtPin.getText().toString().equalsIgnoreCase(mSharedPreferences.getString("myPin", ""))) {
                    alertDialog("Invalid PIN!!!");
                } else if (mEdtPin.getText().toString().equalsIgnoreCase(mSharedPreferences.getString("myPin", ""))) {

                    if (isAuthentication == true) {
                        Intent i = new Intent(PinActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        mDatabaseHandler.upDateLockApp(packName, "0");
                        mEditor.putString("packName", packName);
                        mEditor.commit();
                        finish();
                    }


                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(PinActivity.class.toString(), "PinActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(PinActivity.class.toString(), "PinActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(PinActivity.class.toString(), "PinActivity onDestroy");
    }

    private void alertDialog(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    new AlertDialog.Builder(PinActivity.this)
                            .setTitle("Alert")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
            }
        });
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(PinActivity.class.toString(), "PinActivity Focus changed !");

        if (!hasFocus) {
            Log.i(PinActivity.class.toString(), "PinActivity Lost Focus!");
            finish();
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);

        }
    }


}
