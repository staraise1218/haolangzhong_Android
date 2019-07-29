package com.zhongyi.zhongyi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.zhongyi.zhongyi.bean.SplishData;
import com.zhongyi.zhongyi.bean.Version;
import com.zhongyi.zhongyi.bean.ask.VersionBean;
import com.zhongyi.zhongyi.bean.temp.HomeDataTemp;
import com.zhongyi.zhongyi.bean.temp.SplishTemp;
import com.zhongyi.zhongyi.bean.temp.VersionTemp2;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.VersionUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplishActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageView;
    private TextView skip_tv;


    private String token;
    private SplishData splishData;
    private Handler handler = new Handler();
    private int time = 3;
    private Timer timer;

    private Version versionBean;
    private String versionName;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_splish);

        imageView = findViewById(R.id.webview);
        imageView.setOnClickListener(this);
        skip_tv = findViewById(R.id.skip_tv);
        versionName =  VersionUtils.getVersionName(this);
        getData();
        //getVersionData();
        RunTimer();
    }

    private void getData() {
        token = (String) SharedPreferencesUtils.getParam("token", "");
        String url = NetConstant.BASE_URL + NetConstant.HOME_PAGE;
        String json = "{}";
        NetUtils.setCallback(homeCallback);
        NetUtils.postJson(url,json);
    }

    //登录回调
    Callback homeCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                SplishTemp tempBean = JSON.parseObject(response.body().string(), SplishTemp.class);
                if (tempBean.getCode() == 0) {
                    splishData = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else{
                    Looper.prepare();
                    Toast.makeText(SplishActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (splishData != null) {
                //头像
                Glide.with(SplishActivity.this).load(NetConstant.BASE_IMGE_URL +splishData.getPicid()).into(imageView);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.skip_tv:{
                boolean isLogin = (boolean) SharedPreferencesUtils.getParam("isLogin",false);
                if (isLogin){
                    Intent intent = new Intent(SplishActivity.this, HomeActivity.class);
                    startActivity(intent);
                    SplishActivity.this.finish();
                }else {
                    Intent intent = new Intent(SplishActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplishActivity.this.finish();
                }
            }
                break;
            case R.id.imageView:{
                Intent intent = new Intent(SplishActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.BASE_URL_WEB_2+splishData.getSkipUrl());
                intent.putExtra("title","广告");
                startActivity(intent);
            }
                break;
        }
    }

    public void RunTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                time--;
                Message msg = handler2.obtainMessage();
                msg.what = 1;
                handler2.sendMessage(msg);

            }
        };

        timer.schedule(task, 100, 1000);
    }

    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:

                    if (time > 0) {
                        skip_tv.setEnabled(false);
                        skip_tv.setText(time + "秒后跳过");
                        skip_tv.setTextSize(14);
                    } else {
                        timer.cancel();
//                        skip_tv.setText("跳过");
//                        skip_tv.setEnabled(true);
//                        skip_tv.setTextSize(14);
//                        skip_tv.setOnClickListener(SplishActivity.this);
                        boolean isLogin = (boolean) SharedPreferencesUtils.getParam("isLogin",false);
                        if (isLogin){
                            Intent intent = new Intent(SplishActivity.this, HomeActivity.class);
                            startActivity(intent);
                            SplishActivity.this.finish();
                        }else {
                            Intent intent = new Intent(SplishActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SplishActivity.this.finish();
                        }
                    }

                    break;

                default:
                    break;
            }
        };
    };

    private void getVersionData() {
        token = (String) SharedPreferencesUtils.getParam("token", "");
        String url = NetConstant.BASE_URL2 + NetConstant.VERSION;
        //设置回调方法
        NetUtils.setCallback(versionCallback);
        VersionBean doctorBean = new VersionBean();
        doctorBean.setToken(token);
        doctorBean.setType("0");
        String json = JSON.toJSONString(doctorBean);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback versionCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                VersionTemp2 tempBean = JSON.parseObject(response.body().string(), VersionTemp2.class);
                if (tempBean.getResult().getCode() == 0) {
                    versionBean = tempBean.getResult().getData();
                    //更新UI界面
                    handler.post(runnabVersionleUi);
                } else if (tempBean.getResult().getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(SplishActivity.this, tempBean.getResult().msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnabVersionleUi = new Runnable() {
        @Override
        public void run() {
            if (versionBean!=null){
                if (versionName.equals(versionBean.getAppVersion().getVersion())){
                    Toast.makeText(SplishActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                }else {
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplishActivity.this);
                    //    设置Title的图标
                    builder.setIcon(R.mipmap.icon);
                    //    设置Title的内容
                    builder.setTitle("版本升级");
                    builder.setCancelable(false);
                    //    设置Content来显示一个信息
                    builder.setMessage("是否跳转到应用市场进行下载更新？");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
//                            Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-123456789"));//跳转到拨号界面，同时传递电话号码
//                            startActivity(dialIntent);
                        }
                    });
                    //    设置一个NegativeButton
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Toast.makeText(HomeActivity.this, "negative: " + which, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //    显示出该对话框
                    builder.show();
                }
            }
        }
    };

}
