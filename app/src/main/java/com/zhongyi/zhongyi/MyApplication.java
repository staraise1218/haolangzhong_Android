package com.zhongyi.zhongyi;

import android.app.Application;

import com.mob.MobSDK;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils.intiContext(this);
        MobSDK.init(this);
    }
}
