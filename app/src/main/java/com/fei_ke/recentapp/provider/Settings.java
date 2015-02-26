package com.fei_ke.recentapp.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by 杨金阳 on 16/2/2015.
 */
public class Settings {
    public static final String KEY_APP_COUNT = "app_count";
    private SharedPreferences mSharedPreferences;
    public static boolean switchOn;

    public Settings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getAppCount() {
        String count = mSharedPreferences.getString(KEY_APP_COUNT, "15");
        return Integer.valueOf(count);
    }

    public static boolean isSwitchOn() {
        return switchOn;
    }

    public static void setSwitchOn(boolean switchOn) {
        Settings.switchOn = switchOn;
    }
}
