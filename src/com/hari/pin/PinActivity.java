package com.hari.pin;

import com.hari.dbhelper.DatabaseHandler;
import com.hari.locker.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PinActivity extends AppCompatActivity implements OnClickListener {
	private EditText mEdtPin;
	private Button mBtnDone;
	private static String pin = "12345";
	private SharedPreferences mSharedPreferences;
	private Editor mEditor;
	private String packName;
	private DatabaseHandler mDatabaseHandler;

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
		
		}
		initViews();

	}

	private void initViews() {
		mEdtPin = (EditText) findViewById(R.id.edit_pin);
		mBtnDone = (Button) findViewById(R.id.btn_sbt);
		mBtnDone.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sbt:
			if (mEdtPin.getText().toString().equalsIgnoreCase("1234")) {
				mDatabaseHandler.upDateLockApp(packName, "0");
				mEditor.putString("packName", packName);
				mEditor.commit();
				
				
				finish();
				System.exit(0);
			}
			break;

		default:
			break;
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();
			
	}

}
