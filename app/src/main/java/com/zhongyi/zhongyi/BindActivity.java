package com.zhongyi.zhongyi;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.zhongyi.bean.Share;
import com.zhongyi.zhongyi.bean.User;
import com.zhongyi.zhongyi.bean.ask.BindBean;
import com.zhongyi.zhongyi.bean.ask.RegisterBean;
import com.zhongyi.zhongyi.bean.ask.ThirdBean;
import com.zhongyi.zhongyi.bean.ask.VerifCode;
import com.zhongyi.zhongyi.bean.temp.BindTemp;
import com.zhongyi.zhongyi.bean.temp.RegisterTemp;
import com.zhongyi.zhongyi.bean.temp.UserTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BindActivity extends BaseActivity implements View.OnClickListener {
    private EditText register_phone_et;
    private EditText forget_verif_et;
    private TextView get_verif;
    private TextView register_btn;

    private Handler handler = new Handler();
    private Handler handler3 = new Handler();
    private Timer timer;
    private int time = 60;
    private User user;

    private String third_type;
    private String user_type = "1";
    private String user_id;
    private String token;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_bind);

        register_phone_et = findViewById(R.id.register_phone_et);
        forget_verif_et = findViewById(R.id.forget_verif_et);
        get_verif = findViewById(R.id.get_verif);
        get_verif.setOnClickListener(this);
        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);

        third_type = getIntent().getStringExtra("third_type");
        user_type = getIntent().getStringExtra("user_type");
        token = getIntent().getStringExtra("token");
        user_id = getIntent().getStringExtra("user_id");
    }

    @Override
    public void onClick(View view) {
        String phone;
        switch (view.getId()){
            case R.id.register_btn:{
                phone =  register_phone_et.getText().toString().trim();
                String verif = forget_verif_et.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    if (!TextUtils.isEmpty(verif)){
                        //注册
                        Log.e("onError",phone+"--"+verif+"---"+user_id+"---"+third_type+"---"+user_type);
                        register(phone,verif,user_id,third_type,user_type);
                    }else {
                        Toast.makeText(BindActivity.this,"请输入验证码",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(BindActivity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.get_verif:{
                register_phone_et.clearFocus();
                forget_verif_et.requestFocus();
                //设置倒计时
                RunTimer();
                Toast.makeText(BindActivity.this,"验证码已发送",Toast.LENGTH_LONG).show();
                phone =  register_phone_et.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    getVerif(phone);
                }else{
                    Toast.makeText(BindActivity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    //注册
    private void register(String phone,String verif,String third_id,String third_type,String user_type) {
        String url = NetConstant.BASE_URL + NetConstant.BIND;
        BindBean registerBean = new BindBean(third_id,third_type,user_type,phone,verif);
        String json = JSON.toJSONString(registerBean);
        NetUtils.setCallback(registerCallback);
        //获取网络数据
        NetUtils.postJson(url,json);
    }

    //登录回调
    Callback registerCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                BindTemp tempBean = JSON.parseObject(response.body().string(), BindTemp.class);
                if (tempBean.getCode() ==0) {
                    //更新UI界面
                    handler.post(runnableUi);
                }else{
                    Looper.prepare();
                    Toast.makeText(BindActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(BindActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
            Log.e("onError",token+"---"+user_id+"---"+third_type+"---"+user_type);
            thirdLogin(token,user_id,third_type,user_type);
        }
    };

    //注册
    private void getVerif(String phone) {
        String url = NetConstant.BASE_URL + NetConstant.CODE;
        VerifCode registerBean = new VerifCode();
        registerBean.setMobile(phone);
        String json = JSON.toJSONString(registerBean);
        NetUtils.setCallback(codeCallback);
        //获取网络数据
        NetUtils.postJson(url,json);
    }

    //登录回调
    Callback codeCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };


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
                        get_verif.setEnabled(false);
                        get_verif.setText(time + "秒后重新发送");
                        get_verif.setTextSize(14);
                    } else {
                        timer.cancel();
                        get_verif.setText("获取验证码");
                        get_verif.setEnabled(true);
                        get_verif.setTextSize(14);
                    }

                    break;

                default:
                    break;
            }
        };
    };

    private void thirdLogin(String token, String third_id,String third_type,String user_type) {
        String url = NetConstant.BASE_URL + NetConstant.THIRD_LOGIN;

        ThirdBean user = new ThirdBean();
        user.setToken(token);
        user.setThird_id(third_id);
        user.setThird_type(third_type);
        user.setUser_type(user_type);
        String json = JSON.toJSONString(user);
        NetUtils.setCallback(loginCallback);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback loginCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                UserTemp tempBean = JSON.parseObject(response.body().string(), UserTemp.class);
                if (tempBean.getCode() == 0) {
                    Log.e("onError","登录成功");
                    user = tempBean.getData();
                    Log.e("onError",user.toString());
                    //更新UI界面
                    handler3.post(loginRunnableUi);
                } else if (tempBean.getCode() == 401) {
                    Looper.prepare();
                    Toast.makeText(BindActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else if(tempBean.getCode() == 409){
                    Log.e("onError", "跳转去绑定页面");
                }else{
                    Log.e("onError","登录失败");
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable loginRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(BindActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            SharedPreferencesUtils.setParam("user_id", user.getId());
            if (user.getPassword() != null) {
                SharedPreferencesUtils.setParam("pwd", user.getPassword());
            }
            SharedPreferencesUtils.setParam("phone", user.getMobile());
            SharedPreferencesUtils.setParam("token", user.getToken());
            SharedPreferencesUtils.setParam("type", user.getType());
            SharedPreferencesUtils.setParam("isLogin", true);
            if (user.getShare()!=null) {
                Share share = user.getShare();
                SharedPreferencesUtils.setParam("isNewRecord", share.isNewRecord());
                SharedPreferencesUtils.setParam("title", share.getTitle());
                SharedPreferencesUtils.setParam("picurl", share.getPicurl());
                SharedPreferencesUtils.setParam("shareurl", share.getShareurl());
                SharedPreferencesUtils.setParam("content", share.getContent());
                SharedPreferencesUtils.setParam("shareCode", share.getShareCode());
            }
            if (user.getIcon() != null) {
                SharedPreferencesUtils.setParam("icon", user.getIcon());
            }
            if (user.getNike_name() != null) {
                SharedPreferencesUtils.setParam("nike_name", user.getNike_name());
            }

            //进入首页
            Intent intent = new Intent(BindActivity.this, HomeActivity.class);
            startActivity(intent);
            BindActivity.this.finish();
        }
    };
}
