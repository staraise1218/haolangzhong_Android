package com.zhongyi.doctor;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.zhongyi.doctor.bean.SplishData;
import com.zhongyi.doctor.bean.temp.SplishTemp;
import com.zhongyi.doctor.constant.LogUtils;
import com.zhongyi.doctor.constant.NetCode;
import com.zhongyi.doctor.constant.NetConstant;
import com.zhongyi.doctor.control.GlideCircleTransform;
import com.zhongyi.doctor.utils.NetUtils;
import com.zhongyi.doctor.utils.SharedPreferencesUtils;

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

    @Override
    protected void setView() {
        setContentView(R.layout.activity_splish);

        imageView = findViewById(R.id.webview);
        imageView.setOnClickListener(this);
        skip_tv = findViewById(R.id.skip_tv);
        getData();
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
//                            Intent intent = new Intent(SplishActivity.this, MainActivity.class);
//                            String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
//                            intent.putExtra("url", NetConstant.ORDER + "?docid=" + userid);
                            Intent intent = new Intent(SplishActivity.this,MineActivity.class);
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
}
