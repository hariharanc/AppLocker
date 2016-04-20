package com.hari.locker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBrodCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Create Intent

		Log.i(BootBrodCast.class.toString(), "BootBrodCast onReceive start...");
		Intent serviceIntent = new Intent(context, LockerService.class);
		// Start service
		context.startService(serviceIntent);

	}

}