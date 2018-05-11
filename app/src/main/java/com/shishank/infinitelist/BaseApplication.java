package com.shishank.infinitelist;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by abhishek
 * on 05/04/16.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
