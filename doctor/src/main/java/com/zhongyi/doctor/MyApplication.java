package com.zhongyi.doctor;

import android.app.Application;

import com.zhongyi.doctor.utils.SharedPreferencesUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils.intiContext(this);
    }
}
