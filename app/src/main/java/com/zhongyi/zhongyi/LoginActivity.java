package com.zhongyi.zhongyi;

import android.app.Activity;
import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhongyi.zhongyi.bean.Share;
import com.zhongyi.zhongyi.bean.User;
import com.zhongyi.zhongyi.bean.ask.ThirdBean;
import com.zhongyi.zhongyi.bean.temp.UserTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {
    private EditText login_phone_et;
    private EditText login_pwd_et;
    private TextView login_btn;
    private TextView register_btn;
    private TextView login_forget;
    private ImageView yanjing;
    private ImageView qq_login;
    private ImageView weixin_login;

    private String phoneString = "";
    private String pwdString = "";
    private User user;
    private Handler handler = new Handler();

    private boolean isShow = false;

    private String third_type = "1";
    private String user_type = "1";
    private String user_id;
    private String token;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_login);

        login_phone_et = findViewById(R.id.login_phone_et);
        login_pwd_et = findViewById(R.id.login_pwd_et);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        login_forget = findViewById(R.id.login_forget);
        yanjing = findViewById(R.id.yanjing);
        weixin_login = findViewById(R.id.weixin_login);
        weixin_login.setOnClickListener(this);
        qq_login = findViewById(R.id.qq_login);
        qq_login.setOnClickListener(this);

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
            case R.id.yanjing: {
                if (!isShow) {
                    //如果选中，显示密码
                    login_pwd_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow = true;
                } else {
                    //否则隐藏密码
                    login_pwd_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow = false;
                }
            }
            break;
            case R.id.qq_login: {
                third_type = "2";
                login(QQ.NAME);
                break;
            }
            case R.id.weixin_login: {
                third_type = "1";
                login(Wechat.NAME);
                break;
            }
        }
    }

    private void login(String name) {
        Platform plat = ShareSDK.getPlatform(name);
        plat.removeAccount(true); //移除授权状态和本地缓存，下次授权会重新授权
//        plat.SSOSetting(false); //SSO授权，传false默认是客户端授权，没有客户端授权或者不支持客户端授权会跳web授权
        plat.setPlatformActionListener(this);//授权回调监听，监听oncomplete，onerror，oncancel三种状态
//        if(plat.isClientValid()){
//            //判断是否存在授权凭条的客户端，true是有客户端，false是无
//        }
//        if(plat.isAuthValid()){
//            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
//            Toast.makeText(this, "已经授权过了", Toast.LENGTH_LONG).show();
//            //plat.removeAccount(true);//执行此操作就可以移除掉本地授权状态和授权信息
//            //return;
//        }
        ShareSDK.setActivity(this);//抖音登录适配安卓9.0
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
        //plat.authorize();
    }


    private void login(String phone, String password) {
        String url = NetConstant.BASE_URL + NetConstant.LOGIN;

        User user = new User();
        user.setMobile(phone);
        user.setPassword(password);
        user.setType("1");
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
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 401) {
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else if (tempBean.getCode() == 409) {
                    Log.e("onError", "跳转去绑定页面");
                    handler.post(bindRunnableUi);
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable bindRunnableUi = new Runnable() {
        @Override
        public void run() {
            //进入首页
            Intent intent = new Intent(LoginActivity.this, BindActivity.class);
            intent.putExtra("user_id", user_id);
            intent.putExtra("third_type", third_type);
            intent.putExtra("token", token);
            intent.putExtra("user_type", user_type);

            startActivity(intent);
            LoginActivity.this.finish();
        }
    };

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        //Log.e("onError", "onComplete");
        //通过打印res数据看看有哪些数据是你想要的
//        if (action == Platform.ACTION_USER_INFOR) {
        PlatformDb platDB = platform.getDb();//获取数平台数据DB
        //通过DB获取各种数据
        ;
//        platDB.getUserGender();
//        platDB.getUserIcon();
//        platDB.getUserName();
        //}
        token = platDB.getToken();
        user_id = platDB.getUserId();
        user_type = "1";
        thirdLogin(token, user_id, third_type, user_type);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e("onError", throwable.getMessage());
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(this, "onCancel", Toast.LENGTH_LONG).show();
    }

    private void thirdLogin(String token, String third_id, String third_type, String user_type) {
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
}
