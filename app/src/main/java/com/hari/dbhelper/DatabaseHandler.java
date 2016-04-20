package com.hari.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hari.util.AppDetails;

public class DatabaseHandler extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "LockerDb.db";
	public static final String TABLE_NAME = "locker_table";
	public static final String KEY_ID = "id";
	public static final String APP_NAME = "appName";
	public static final String APP_PACKNAME = "packName";
	public static final String APP_ICON = "appIcon";
	public static final String APP_LOCK_STATE = "lockState";
	SQLiteDatabase db;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
				+ APP_NAME + " TEXT," + APP_PACKNAME + " TEXT," + APP_ICON
				+ " TEXT," + APP_LOCK_STATE + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
		Log.i(DatabaseHandler.class.toString(), "DatabaseHandler Create Table");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS locker_table");
		this.onCreate(db);
	}

	public String insertAppLock(String appName, String packName,
			String appIcon, String lockState) {
		String response = null;
		try {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler inserEmployeeData is" + appName);
			db = this.getWritableDatabase();
			ContentValues insertEmpData = new ContentValues();
			insertEmpData.put(APP_NAME, appName);
			insertEmpData.put(APP_PACKNAME, packName);
			insertEmpData.put(APP_ICON, appIcon);
			insertEmpData.put(APP_LOCK_STATE, lockState);

			response = String.valueOf(db
					.insert(TABLE_NAME, null, insertEmpData));

			if (db != null && db.isOpen()) {
				db.close();
			}

		} catch (Exception e) {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler Exception is" + e.getMessage());
		}
		if (response != null && response.equalsIgnoreCase("-1")) {
			return "error";
		} else {
			return "success";
		}

	}

	public ArrayList<String> getAllLockApp() {
		try {
			ArrayList<String> list = new ArrayList<String>();
			AppDetails appDetails = new AppDetails();
			db = this.getWritableDatabase();
			String countQuery = "SELECT  * FROM " + TABLE_NAME;
			Cursor c = db.rawQuery(countQuery, null);

			if (c.moveToFirst()) {
				do {
//					appDetails.setAppName();
//					appDetails.setAppPackName(c.getString(c
//							.getColumnIndex(APP_PACKNAME)));
//					appDetails.setAppLockStates(c.getString(c
//							.getColumnIndex(APP_LOCK_STATE)));
					list.add(c.getString(c
							.getColumnIndex(APP_NAME)));
					// Do something Here with values
				} while (c.moveToNext());
			}
			c.close();
			if (db != null && db.isOpen()) {
				db.close();
			}

			return list;
		} catch (Exception e) {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler selecedtEmpData Exp is" + e.getMessage());
		}
		return null;

	}

	public ArrayList<AppDetails> getUnlockApp(String processName) {
		try {
			ArrayList<AppDetails> list = new ArrayList<AppDetails>();
			AppDetails appDetails = new AppDetails();
			db = this.getWritableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ APP_LOCK_STATE + "= ?", new String[] { "0" });

			if (c.moveToFirst()) {
				do {
					appDetails.setAppName(c.getString(c
							.getColumnIndex(APP_NAME)));
					appDetails.setAppPackName(c.getString(c
							.getColumnIndex(APP_PACKNAME)));
					appDetails.setAppLockStates(c.getString(c
							.getColumnIndex(APP_LOCK_STATE)));
					list.add(appDetails);
					// Do something Here with values
				} while (c.moveToNext());
			}
			c.close();
			if (db != null && db.isOpen()) {
				db.close();
			}
			return list;
		} catch (Exception e) {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler selecedtEmpData Exp is" + e.getMessage());
		}
		return null;

	}

	public ArrayList<AppDetails> getLockAppDetails(String appState) {
		try {
			ArrayList<AppDetails> list = new ArrayList<AppDetails>();
			AppDetails appDetails = new AppDetails();
			db = this.getWritableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ APP_LOCK_STATE + "= ?", new String[] { appState });

			if (c.moveToFirst()) {
				do {
					appDetails.setAppName(c.getString(c
							.getColumnIndex(APP_NAME)));
					appDetails.setAppPackName(c.getString(c
							.getColumnIndex(APP_PACKNAME)));
					appDetails.setAppLockStates(c.getString(c
							.getColumnIndex(APP_LOCK_STATE)));
					list.add(appDetails);
					// Do something Here with values
				} while (c.moveToNext());
			}
			c.close();
			// if (db != null && db.isOpen()) {
			// db.close();
			// }
			return list;
		} catch (Exception e) {

			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler selecedtEmpData Exp is" + e.getMessage());
		}
		return null;

	}

	public ArrayList<AppDetails> getUnLockeAppData(String packName,
			String appState) {
		try {
			ArrayList<AppDetails> list = new ArrayList<AppDetails>();
			AppDetails appDetails = new AppDetails();
			db = this.getWritableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ APP_PACKNAME + "= ?" + " AND " + APP_LOCK_STATE + "= ?",
					new String[] { packName, appState });

			if (c.moveToFirst()) {
				do {
					appDetails.setAppName(c.getString(c
							.getColumnIndex(APP_NAME)));
					appDetails.setAppPackName(c.getString(c
							.getColumnIndex(APP_PACKNAME)));
					appDetails.setAppLockStates(c.getString(c
							.getColumnIndex(APP_LOCK_STATE)));
					list.add(appDetails);
					// Do something Here with values
				} while (c.moveToNext());
			}
			c.close();
			// if (db != null && db.isOpen()) {
			// db.close();
			// }
			return list;
		} catch (Exception e) {

			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler selecedtEmpData Exp is" + e.getMessage());
		}
		return null;

	}

	public String upDateLockApp(String processName, String lockState) {
		String response = null;
		try {
			db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(APP_LOCK_STATE, lockState);
			response = String.valueOf(db.update(TABLE_NAME, values,
					APP_PACKNAME + " = ?", new String[] { processName }));
			Log.i(DatabaseHandler.class.toString(),
					"DatabaseHandler Number of Affected Row" + response);
			if (db != null && db.isOpen()) {
				db.close();
			}
		} catch (Exception e) {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler upDateMobNo Excep is" + e.getMessage());
		}

		return response;

	}

	public String delLockApp(String packageName) {
		try {
			db = this.getWritableDatabase();
			String response = String.valueOf(db.delete(TABLE_NAME, APP_PACKNAME
					+ "=?", new String[] { packageName }));
			if (db != null && db.isOpen()) {
				db.close();
			}
			return response;

		} catch (Exception e) {
			Log.e(DatabaseHandler.class.toString(),
					"DatabaseHandler Delete Exception" + e.getMessage());
			return "error";
		}
	}
}
