package com.zhongyi.zhongyi.utils;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.zhongyi.zhongyi.ZXWZ_ZJActivity;
import com.zhongyi.zhongyi.ZYZJActivity;

// 继承自Object类
public class AndroidtoJs extends Object {
    private Activity activity;

    public AndroidtoJs(Activity activity) {
        this.activity = activity;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void ask(String id,String cost) {
        System.out.println("JS调用了Android的hello方法");

    }
}