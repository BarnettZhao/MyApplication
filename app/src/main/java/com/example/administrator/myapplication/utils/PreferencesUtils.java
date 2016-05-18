package com.example.administrator.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kkkkk on 2016/4/1.
 */
public class PreferencesUtils {
	private static String SHARED_NAME = "ailafei";
	private static PreferencesUtils preferencesUtils;

	public PreferencesUtils() {
	}

	public static PreferencesUtils getInstance () {
		if (preferencesUtils == null) {
			preferencesUtils = new PreferencesUtils();
		}
		return preferencesUtils;
	}

	public void writeSharedString (Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String readSharedString (Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, value);
	}
	public void writeSharedBoolean (Context context, String key, Boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public Boolean readSharedBoolean (Context context, String key, Boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, value);
	}
}
