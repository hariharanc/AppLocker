package com.hari.locker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hari.adapter.AppListAdapter;
import com.hari.dbhelper.DatabaseHandler;
import com.hari.util.AppDetails;

public class MainActivity extends AppCompatActivity implements
		OnItemClickListener {

	private ListView mListView;
	private Toolbar toolbar;

	private AppListAdapter mApListAdapter;

	private ArrayList<AppDetails> mArrayAppList;
	private DatabaseHandler mDatabaseHandler;
	private ArrayList<AppDetails> mArrAppDetails;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Please Wait!!!");
		mDatabaseHandler = new DatabaseHandler(this);
		mArrayAppList = new ArrayList<AppDetails>();
		new AppDataAysnc().execute();

		try {
			Intent serviceIntent = new Intent(this, LockerService.class);
			// Start service
			this.startService(serviceIntent);
		} catch (Exception e) {
			Log.e(MainActivity.class.toString(), "MainActivity Exception is"
					+ e.getMessage());
		}

		initViews();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if (bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if (drawable.getIntrinsicWidth() <= 0
				|| drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] arr = baos.toByteArray();
		String result = Base64.encodeToString(arr, Base64.DEFAULT);
		return result;
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.list_view);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		Log.i(MainActivity.class.toString(), "MainActivity size Array"
				+ mArrayAppList.size());
		mListView.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		Log.i(MainActivity.class.toString(), "MainActivity Pack Name"
				+ mArrayAppList.get(position).getAppPackName());

		final CharSequence[] items = { "Lock", "Unlock" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select your option");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					mDatabaseHandler.insertAppLock(mArrayAppList.get(position)
							.getAppName(), mArrayAppList.get(position)
							.getAppPackName(), mArrayAppList.get(position)
							.getAppIconBitMap(), "1");
				} else if (item == 1) {
					if (mArrayAppList.get(position).getAppLockStates()
							.equalsIgnoreCase("1")) {
						String value = mDatabaseHandler
								.delLockApp(mArrayAppList.get(position)
										.getAppPackName());
						if (Integer.parseInt(value) > 0) {
							Toast.makeText(getApplicationContext(),
									"App unLocked successfully",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Oops Its Already in unlocked state!!!",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// mEditor.putString("processName", mArrayAppList.get(position)
	// .getAppPackName());
	// mEditor.commit();

	public class AppDataAysnc extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// if (!mProgressDialog.isShowing()) {
			// mProgressDialog.show();
			// }
		}

		@Override
		protected String doInBackground(String... params) {

			mArrAppDetails = mDatabaseHandler.getAllLockApp();
			final PackageManager pm = getPackageManager();

			// get a list of installed apps.
			List<ApplicationInfo> packages = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);

			for (ApplicationInfo packageInfo : packages) {
				// Log.d(MainActivity.class.toString(), "Installed package :"
				// + packageInfo.packageName);
				// Log.d(MainActivity.class.toString(), "Source dir : "
				// + packageInfo.sourceDir);
				// Log.d(MainActivity.class.toString(),
				// "Launch Activity :"
				// + pm.getLaunchIntentForPackage(packageInfo.name));
				String appName = packageInfo.loadLabel(
						getApplicationContext().getPackageManager()).toString();
				Log.i(MainActivity.class.toString(), "Application Name is :"
						+ appName);

				String processName = packageInfo.processName;
				Log.i(MainActivity.class.toString(), "Process Name is :"
						+ processName);
				Drawable app_icon = packageInfo
						.loadIcon(getApplicationContext().getPackageManager());
				AppDetails appDetails = new AppDetails(appName, processName,
						BitMapToString(drawableToBitmap(app_icon)), "0");
				mArrayAppList.add(appDetails);
				// if (mArrAppDetails != null && mArrAppDetails.size() > 0) {
				// for (int i = 0; i < mArrAppDetails.size(); i++) {
				// if (mArrAppDetails.get(i).getAppPackName()
				// .equalsIgnoreCase(processName)) {
				// AppDetails appDetails = new AppDetails(appName,
				// processName,
				// BitMapToString(drawableToBitmap(app_icon)), "1");
				// mArrayAppList.add(appDetails);
				// } else {
				// AppDetails appDetails = new AppDetails(appName,
				// processName,
				// BitMapToString(drawableToBitmap(app_icon)), "0");
				// mArrayAppList.add(appDetails);
				// }
				// }
				// }
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			// if (mProgressDialog.isShowing()) {
			// mProgressDialog.dismiss();
			// }
			mApListAdapter = new AppListAdapter(getApplicationContext(),
					mArrayAppList);
			mListView.setAdapter(mApListAdapter);
		}

	}

}
