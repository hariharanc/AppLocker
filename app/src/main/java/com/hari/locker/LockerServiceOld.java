package com.hari.locker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hari.dbhelper.DatabaseHandler;
import com.hari.pin.PinActivity;
import com.hari.util.AppDetails;

public class LockerServiceOld extends Service {

	private DatabaseHandler mDatabaseHandler;
	private ArrayList<AppDetails> mArrAppDetails;
	private Boolean isBackground = false;
	private static Timer timer = new Timer();
	ActivityManager am = null;
	Context context = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Let it continue running until it is stopped.
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		Log.i(LockerServiceOld.class.toString(), "LockerService Service Start...");

		new CheckRunningActivity(context).start();
		return START_STICKY;
	}

	// private void startService(final Context context) {
	// this.context = context;
	// int delay = 70;
	// int period = 4500;
	// timer.scheduleAtFixedRate(new TimerTask() {
	//
	// public void run() {
	// getForgroundAppData(context);
	//
	// }
	// }, delay, period);
	//
	// }

	protected void getForgroundAppData(Context context) {

		try {
			mDatabaseHandler = new DatabaseHandler(this.getApplicationContext());
			am = (ActivityManager) this.getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = am
					.getRunningAppProcesses();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					try {
						mArrAppDetails = mDatabaseHandler
								.getLockAppDetails("1");
						Log.i(LockerServiceOld.class.toString(),
								"LockService Foreground"
										+ appProcess.processName);

						if (mArrAppDetails != null) {
							for (int i = 0; i < mArrAppDetails.size(); i++) {
								Log.i(LockerServiceOld.class.toString(),
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
						Log.e(LockerServiceOld.class.toString(),
								"LockerService Exce is" + e.getMessage());
					}

				}

			}
		} catch (Exception e) {
			Log.e(LockerServiceOld.class.toString(), "LockerService Exception is"
					+ e.getMessage());
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
	}

	public class CheckRunningActivity extends Thread {

		public CheckRunningActivity(Context con) {
			context = con;

		}

		public void run() {
			Looper.prepare();
			while (true) {
				ActivityManager activityManager = (ActivityManager) getApplication()
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> appProcesses = activityManager
						.getRunningAppProcesses();
				for (RunningAppProcessInfo appProcess : appProcesses) {
					if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

						try {
							mArrAppDetails = mDatabaseHandler
									.getLockAppDetails("1");
							Log.i(LockerServiceOld.class.toString(),
									"LockService Foreground"
											+ appProcess.processName);
							if (mArrAppDetails != null) {
								for (int i = 0; i < mArrAppDetails.size(); i++) {
									Log.i(LockerServiceOld.class.toString(),
											"LockService Foreground name"
													+ mArrAppDetails.get(i)
															.getAppPackName());
									String packName = mArrAppDetails.get(i)
											.getAppPackName();
									if (packName
											.equalsIgnoreCase(appProcess.processName)) {
										mDatabaseHandler.upDateLockApp(
												appProcess.processName, "0");
										Intent intent = new Intent(context,
												PinActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("packName",
												appProcess.processName);
										context.startActivity(intent);
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

					}

					if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
						try {
							isBackground = true;
							if (isBackground == true) {

								if (mArrAppDetails != null
										&& mArrAppDetails.size() > 0) {
									for (int i = 0; i < mArrAppDetails.size(); i++) {
										Log.i(LockerServiceOld.class.toString(),
												"LockerService back Name process"
														+ mArrAppDetails
																.get(i)
																.getAppPackName());
									}
								}
								// Log.i(LockerService.class.toString(),
								// "LockerService back first"
								// + mArrAppDetails.size());
							}

							if (mArrAppDetails != null
									&& mArrAppDetails.size() > 0) {
								for (int i = 0; i < mArrAppDetails.size(); i++) {

									String name = mArrAppDetails.get(i)
											.getAppPackName();

									if (name.equalsIgnoreCase(appProcess.processName)) {
										String response = mDatabaseHandler
												.upDateLockApp(
														appProcess.processName,
														"1");
										Log.i(LockerServiceOld.class.toString(),
												"LockerService  back update response"
														+ response);
									}
								}
							}
						} catch (Exception e) {

						}

					}
					isBackground = false;
				}

			}
		}
	}

}
