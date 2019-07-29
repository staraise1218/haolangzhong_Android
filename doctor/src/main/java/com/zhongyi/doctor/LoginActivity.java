package com.zhongyi.doctor;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.doctor.bean.DoctorInfo;
import com.zhongyi.doctor.bean.DoctorLabel;
import com.zhongyi.doctor.bean.DoctorPic;
import com.zhongyi.doctor.bean.Info;
import com.zhongyi.doctor.bean.User;
import com.zhongyi.doctor.bean.temp.InfoTemp;
import com.zhongyi.doctor.bean.temp.UserTemp;
import com.zhongyi.doctor.constant.LogUtils;
import com.zhongyi.doctor.constant.NetCode;
import com.zhongyi.doctor.constant.NetConstant;
import com.zhongyi.doctor.utils.NetUtils;
import com.zhongyi.doctor.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText login_phone_et;
    private EditText login_pwd_et;
    private TextView login_btn;
    private TextView register_btn;
    private TextView login_forget;
    private ImageView yanjing;

    private String phoneString = "";
    private String pwdString = "";
    private User user;
    private Handler handler = new Handler();


    private boolean isShow = false;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_login);

        login_phone_et = findViewById(R.id.login_phone_et);
        login_pwd_et = findViewById(R.id.login_pwd_et);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        login_forget = findViewById(R.id.login_forget);
        yanjing = findViewById(R.id.yanjing);
        yanjing.setOnClickListener(this);

        login_btn.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        login_forget.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn: {
                phoneString = login_phone_et.getText().toString().trim();
                pwdString = login_pwd_et.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneString)) {
                    if (!TextUtils.isEmpty(pwdString)) {
                        //登录接口
                        login(phoneString, pwdString);
                    } else {
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.register_btn: {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.login_forget: {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
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
        }
    }


    private void login(String phone, String password) {
        String url = NetConstant.BASE_URL + NetConstant.LOGIN;

        User user = new User();
        user.setMobile(phone);
        user.setPassword(password);
        user.setType("2");
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
                    user = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 407) {
                    //更新UI界面
                    handler.post(runnableUi_2);
                } else {
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi_2 = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(LoginActivity.this, "请先完成认证", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RenZhengActivity.class);
            intent.putExtra("phone", phoneString);
            startActivity(intent);
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            //获取用户信息
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            SharedPreferencesUtils.setParam("user_id", user.getId());
            SharedPreferencesUtils.setParam("pwd", user.getPassword());
            SharedPreferencesUtils.setParam("phone", user.getMobile());
            SharedPreferencesUtils.setParam("token", user.getToken());
            SharedPreferencesUtils.setParam("type", user.getType());
            SharedPreferencesUtils.setParam("isLogin", true);
            SharedPreferencesUtils.setParam("name", user.getDoctorInfo().getName());

            if (null!=user.getDoctorInfo().getIdcard()){
                SharedPreferencesUtils.setParam("shenfen", user.getDoctorInfo().getIdcard());
            }
            if (null!=user.getDoctorInfo().getAdress()) {
                SharedPreferencesUtils.setParam("adress", user.getDoctorInfo().getAdress());
            }
            if (user.getIcon() != null) {
                SharedPreferencesUtils.setParam("icon", user.getIcon());
            }

            if (user.getNike_name() != null) {
                SharedPreferencesUtils.setParam("nike_name", user.getNike_name());
            }

            if (user.getDoctorInfo().getAgenum() != null) {
                SharedPreferencesUtils.setParam("ageNum", user.getDoctorInfo().getAgenum());
            }

            if (user.getDoctorInfo().getProfessional() != null) {
                SharedPreferencesUtils.setParam("professional", user.getDoctorInfo().getProfessional());
            }

            if (user.getDoctorInfo().getWorkyear() != null) {
                SharedPreferencesUtils.setParam("workYear", user.getDoctorInfo().getWorkyear());
            }

            if (user.getShare().getShareCode()!=null){
                SharedPreferencesUtils.setParam("shareCode", user.getShare().getShareCode());
            }

            //进入首页
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra("url", NetConstant.ORDER + "?docid=" + user.getId());
//            startActivity(intent);
            Intent intent = new Intent(LoginActivity.this,MineActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    };
}
