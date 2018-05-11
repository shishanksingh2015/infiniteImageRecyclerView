package com.shishank.infinitelist.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.shishank.infinitelist.BaseApplication;

/**
 * Created by abhishek
 * on 18/03/15.
 */
public class LocalStorage {

    public static final String IMAGE_COUNT = "image_count";

    private final SharedPreferences preferences;

    private static final LocalStorage instance = new LocalStorage();

    public static LocalStorage getInstance() {
        return instance;
    }

    private static final String PREFS_NAME = "com.shishank.infinitelist";

    private LocalStorage() {
        preferences = BaseApplication.getInstance().getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public void storeImageCount(int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(IMAGE_COUNT, value);
        editor.apply();
    }


    public int getImageCount() {
        return preferences.getInt(IMAGE_COUNT, 1);
    }
}
