package com.hari.pin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hari.locker.MainActivity;
import com.hari.locker.R;

/**
 * Created by ITDept on 11/04/16.
 */
public class SetPinActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEdtNewPin, mEdtConfirmPin, mEdtCurrentPin;
    private Button mBtnConfirm;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Toolbar toolbar;
    private String mStrPinOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra("pinOption") != null) {
            mStrPinOption = getIntent().getStringExtra("pinOption");
        }
        setContentView(R.layout.activity_setpin);
        mSharedPreferences = getSharedPreferences("hariPref",
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);


        mEdtCurrentPin = (EditText) findViewById(R.id.edt_current_pin);
        mEdtNewPin = (EditText) findViewById(R.id.edt_new_pin);
        mEdtConfirmPin = (EditText) findViewById(R.id.edt_confirm_pin);
        mBtnConfirm = (Button) findViewById(R.id.btn_set_pin);
        mBtnConfirm.setOnClickListener(this);

        if (mStrPinOption != null && mStrPinOption.equalsIgnoreCase("setpin")) {
            mEdtCurrentPin.setVisibility(View.GONE);
            mBtnConfirm.setText("SET PIN");
            toolbar.setTitle("SET PIN");
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        } else if (mStrPinOption != null && mStrPinOption.equalsIgnoreCase("resetpin")) {
            mEdtCurrentPin.setVisibility(View.VISIBLE);
            mBtnConfirm.setText("RESET PIN");
            toolbar.setTitle("SET PIN");
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_pin:
                setPinOptions(mStrPinOption);

                break;
            default:
                break;
        }
    }

    private void setPinOptions(String options) {
        Log.i(SetPinActivity.class.toString(), "setPinOptions option is" + options);
        if (options.equalsIgnoreCase("resetpin")) {
            if ((mEdtCurrentPin.getText().toString().length() == 0) && (mEdtNewPin.getText().toString().length() == 0) && (mEdtConfirmPin.getText().toString().length() == 0)) {
                alertDialog("Please Enter Your PIN!!!");
            } else if (mEdtCurrentPin.getText().toString().length() == 0) {
                alertDialog("Please Enter Your Current PIN!!!");
            } else {
                commonField();
            }
        } else {
            commonField();
        }


    }

    private void commonField() {
        if ((mEdtNewPin.getText().toString().length() == 0) && (mEdtConfirmPin.getText().toString().length() == 0)) {
            alertDialog("Please Enter Your PIN!!!");

        } else if (mEdtNewPin.getText().toString().length() == 0) {
            alertDialog("Please Enter Your New PIN!!!");

        } else if (mEdtConfirmPin.getText().toString().length() == 0) {
            alertDialog("Please Enter Your Confirm PIN!!!");

        } else if (mEdtNewPin.getText().toString().length() < 4) {
            alertDialog("Please Enter Minimum 4 Digit Locks PIN!!");
        } else if (mEdtConfirmPin.getText().toString().length() < 4) {
            alertDialog("Please Enter Minimum 4 Digit Locks PIN!!");
        } else if (!mEdtNewPin.getText().toString().equalsIgnoreCase(mEdtConfirmPin.getText().toString())) {
            alertDialog("Your New PIN && Confirm PIN Not Matched!!!");
        } else {
            if (mStrPinOption.equalsIgnoreCase("resetpin")) {
                if (!mEdtCurrentPin.getText().toString().equalsIgnoreCase(mSharedPreferences.getString("myPin", ""))) {
                    alertDialog("Invalid Current PIN!!!");
                } else {
                    mEditor.putString("myPin", mEdtConfirmPin.getText().toString());
                    mEditor.commit();
                    alertDialog("PIN Updated Successfully!!!");
                }
            } else {
                mEditor.putString("myPin", mEdtConfirmPin.getText().toString());
                mEditor.commit();
                alertDialog("PIN Set Successfully!!!");
            }
        }
    }

    private void alertDialog(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    new AlertDialog.Builder(SetPinActivity.this)
                            .setTitle("Alert")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (message.equalsIgnoreCase("PIN Set Successfully!!!") ||
                                            message.equalsIgnoreCase("PIN Updated Successfully!!!")) {
                                        // Intent mainIntent=new Intent(SetPinActivity.this, MainActivity.class);
                                        // startActivity(mainIntent);
                                        finish();
                                    }

                                }
                            }).create().show();
                }
            }
        });
    }
}
