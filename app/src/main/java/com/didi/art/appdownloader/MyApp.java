package com.didi.art.appdownloader;

import android.app.Application;

import org.xutils.x;

/**
 * Created by apple on 2019/7/22.
 */

public class MyApp extends Application {

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
