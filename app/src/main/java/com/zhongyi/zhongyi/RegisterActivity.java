package com.zhongyi.zhongyi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.zhongyi.bean.Register;
import com.zhongyi.zhongyi.bean.ask.RegisterBean;
import com.zhongyi.zhongyi.bean.ask.VerifCode;
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

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText register_phone_et;
    private EditText forget_verif_et;
    private EditText login_pwd_et;
    private EditText login_pwd_et_1;
    private EditText share_et;
    private LinearLayout fwxy_ll;

    private TextView register_btn;
    private LinearLayout register_go_login;
    private TextView get_verif;
    private ImageView yanjing;
    private ImageView yanjing_1;

    private Handler handler = new Handler();
    private Timer timer;
    private int time = 60;
    private boolean isShow = false;
    private boolean isShow_1 = false;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_register);

        register_phone_et = findViewById(R.id.register_phone_et);
        forget_verif_et = findViewById(R.id.forget_verif_et);
        login_pwd_et = findViewById(R.id.login_pwd_et);
        login_pwd_et_1 = findViewById(R.id.login_pwd_et_1);
        yanjing = findViewById(R.id.yanjing);
        yanjing_1 = findViewById(R.id.yanjing_1);
        share_et  = findViewById(R.id.share_et);
        fwxy_ll = findViewById(R.id.fyxy_ll);
        fwxy_ll.setOnClickListener(this);
        yanjing.setOnClickListener(this);
        yanjing_1.setOnClickListener(this);

        get_verif = findViewById(R.id.get_verif);
        get_verif.setOnClickListener(this);

        register_go_login = findViewById(R.id.register_go_login);
        register_go_login.setOnClickListener(this);

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
              String phone;
        switch (view.getId()){
            case R.id.register_btn:{
               phone =  register_phone_et.getText().toString().trim();
               String verif = forget_verif_et.getText().toString().trim();
               String pwd = login_pwd_et_1.getText().toString().trim();
               String pwd_sure = login_pwd_et.getText().toString().trim();
               String shareCode = share_et.getText().toString().trim();

               if (!TextUtils.isEmpty(phone)){
                   if (!TextUtils.isEmpty(verif)){
                       if (!TextUtils.isEmpty(pwd)){
                           if (!TextUtils.isEmpty(pwd_sure)){
                               if (pwd.equals(pwd_sure)){
                                   //注册
                                   register(phone,verif,pwd,pwd_sure,shareCode);
                               }else {
                                   Toast.makeText(RegisterActivity.this,"两次输入的密码不一致",Toast.LENGTH_LONG).show();
                               }
                           }else{
                               Toast.makeText(RegisterActivity.this,"请输入确认密码",Toast.LENGTH_LONG).show();
                           }
                       }else{
                           Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
                       }
                   }else {
                       Toast.makeText(RegisterActivity.this,"请输入验证码",Toast.LENGTH_LONG).show();
                   }
               }else{
                   Toast.makeText(RegisterActivity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
               }
            }
               break;
            case R.id.register_go_login:{
                RegisterActivity.this.finish();
            }
                break;
            case R.id.get_verif:{
                register_phone_et.clearFocus();
                forget_verif_et.requestFocus();
                phone =  register_phone_et.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    getVerif(phone);
                    //设置倒计时
                    RunTimer();
                    Toast.makeText(RegisterActivity.this,"验证码已发送",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.yanjing:{
                if (!isShow){
                    //如果选中，显示密码
                    login_pwd_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow = true;
                }else{
                    //否则隐藏密码
                    login_pwd_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow = false;
                }
            }
                break;
            case R.id.yanjing_1:{
                if (!isShow_1){
                    //如果选中，显示密码
                    login_pwd_et_1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow_1 = true;
                }else{
                    //否则隐藏密码
                    login_pwd_et_1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow_1 = false;
                }
            }
            break;
            case R.id.fyxy_ll:{
                Intent intent = new Intent(RegisterActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.XIE_YI);
                intent.putExtra("title","用户协议");
                startActivity(intent);
            }
                break;
        }
    }

    //注册
    private void register(String phone,String verif,String password,String password_sure,String shareCode) {
        String url = NetConstant.BASE_URL + NetConstant.REGISTER;
        RegisterBean registerBean = new RegisterBean(phone,password,password_sure,verif,"1",shareCode);
        String json = JSON.toJSONString(registerBean);
        NetUtils.setCallback(registerCallback);
        //获取网络数据
        NetUtils.postJson(url,json);
    }

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

    //登录回调
    Callback registerCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                RegisterTemp tempBean = JSON.parseObject(response.body().string(), RegisterTemp.class);
                if (tempBean.getCode() == 0) {
                    //更新UI界面
                    handler.post(runnableUi);
                }else{
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            RegisterActivity.this.finish();
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
}
