package com.zhongyi.zhongyi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.zhongyi.zhongyi.bean.Version;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.VersionBean;
import com.zhongyi.zhongyi.bean.temp.HomeDataTemp;
import com.zhongyi.zhongyi.bean.temp.VersionTemp;
import com.zhongyi.zhongyi.bean.temp.VersionTemp2;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.VersionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView title_back;
    private TextView title_content;
    private TextView version_tv;

    private TextView submit;
    private RelativeLayout version;

    private String token;
    private Version versionBean;
    private String versionName;
    private Handler handler = new Handler();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_setting);
        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("设置");
        version_tv = findViewById(R.id.version_tv);

        version = findViewById(R.id.version);
        version.setOnClickListener(this);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        versionName =  VersionUtils.getVersionName(this);
        version_tv.setText(versionName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                SettingActivity.this.finish();
            }
            break;
            case R.id.submit:{
                showMyDialog();
            }
                break;
            case R.id.version:{
                getData();
            }
            break;
        }
    }

    private void getData() {
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
                    handler.post(runnableUi);
                } else if (tempBean.getResult().getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(SettingActivity.this, tempBean.getResult().msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (versionBean!=null){
                if (versionName.equals(versionBean.getAppVersion().getVersion())){
                    Toast.makeText(SettingActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                }else {
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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

    private void showMyDialog() {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("提示");
        // 设置对话框消息
        isExit.setMessage("确定要退出登录么？");
        // 添加选择按钮并注册监听
        isExit.setButton("确定", listener);
        isExit.setButton2("取消", listener);
        // 显示对话框
        isExit.show();
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    SharedPreferencesUtils.setParam("isLogin", false);
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SettingActivity.this.finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}
