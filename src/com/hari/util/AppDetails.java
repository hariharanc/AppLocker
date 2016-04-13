package com.hari.util;

public class AppDetails {
	String appName, appPackName, appIconBitMap, appLockStates;

	public String getAppLockStates() {
		return appLockStates;
	}

	public void setAppLockStates(String appLockStates) {
		this.appLockStates = appLockStates;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppPackName() {
		return appPackName;
	}

	public void setAppPackName(String appPackName) {
		this.appPackName = appPackName;
	}

	public String getAppIconBitMap() {
		return appIconBitMap;
	}

	public void setAppIconBitMap(String appIconBitMap) {
		this.appIconBitMap = appIconBitMap;
	}

	public AppDetails(String appName, String appPackName, String appIconBitMap,
			String appLockStates) {
		this.appName = appName;
		this.appPackName = appPackName;
		this.appIconBitMap = appIconBitMap;
		this.appLockStates = appLockStates;
	}

	public AppDetails() {

	}

}
