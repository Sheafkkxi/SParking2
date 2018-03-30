package com.sheaf.sparking.common;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Sheaf on 2018/3/20.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    private static Context mGolableContext;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        mGolableContext = getApplicationContext();


    }
    public static MyApplication getInstance() {
        return instance;
    }
    public static Context getmGolableContext() {
        return mGolableContext;
    }
}
