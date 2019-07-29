package com.zhongyi.zhongyi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Drug;
import com.zhongyi.zhongyi.bean.Member;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.MemberBean;
import com.zhongyi.zhongyi.bean.temp.HomeData;
import com.zhongyi.zhongyi.bean.temp.HomeDataTemp;
import com.zhongyi.zhongyi.bean.temp.MemberTemp;
import com.zhongyi.zhongyi.bean.temp.UserTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;
import com.zhongyi.zhongyi.utils.GlideImageLoader;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.PermissionUtils;
import com.zhongyi.zhongyi.utils.SH1Utils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private Banner banner;
    private TextView positon_tv;

    //四大模块
    private RelativeLayout zxwz, zyzj, kzyf, zhfw;
    private RelativeLayout haolangzhong;
    //专家详情
    private RelativeLayout zj_rl;
    private RelativeLayout home_drug_rl;

    //专家
    private RelativeLayout zj_more_hint;
    private ImageView zj_img;
    private TextView zj_name;
    private ImageView zj_star_1, zj_star_2, zj_star_3, zj_star_4, zj_star_5;
    private TextView zj_mark_1, zj_mark_2;
    private TextView zj_content;

    //药品
    private RelativeLayout fj_more_hint;
    private ImageView fj_img;
    private TextView fj_name;
    private ImageView fj_star_1, fj_star_2, fj_star_3, fj_star_4, fj_star_5;
    private TextView fj_mark_1, fj_mark_2;
    private TextView fj_content;

    //底部tab
    private LinearLayout home_ll;
    private LinearLayout ask_ll;
    private LinearLayout mine_ll;
    private ImageView home_img;
    private TextView home_name;
    private ImageView ask_img;
    private TextView ask_name;
    private ImageView mine_img;
    private TextView mine_name;

    private RelativeLayout jzfw;
    private RelativeLayout zxt;


    //隐藏web页面
    private WebView webView;

    private String token;
    private HomeData homeData;
    private Doctor doctor;
    private Drug drug;
    private Handler handler = new Handler();

    //顶部消息
    private ImageView msg_img;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //是否是会员
    private Member member;
    private boolean isMember = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void setView() {
        setContentView(R.layout.activity_home);

        positon_tv = findViewById(R.id.positon_tv);


        //底部tab
        home_ll = findViewById(R.id.home_ll);
        ask_ll = findViewById(R.id.ask_ll);
        mine_ll = findViewById(R.id.mine_ll);
        home_ll.setOnClickListener(this);
        ask_ll.setOnClickListener(this);
        mine_ll.setOnClickListener(this);
        home_img = findViewById(R.id.home_img);
        home_name = findViewById(R.id.home_name);
        ask_img = findViewById(R.id.ask_img);
        ask_name = findViewById(R.id.ask_name);
        mine_img = findViewById(R.id.mine_img);
        mine_name = findViewById(R.id.mine_name);

        //四个主入口
        zxwz = findViewById(R.id.zxwz);
        zyzj = findViewById(R.id.zyzj);
        kzyf = findViewById(R.id.kzyf);
        zhfw = findViewById(R.id.zhfw);
        zxwz.setOnClickListener(this);
        zyzj.setOnClickListener(this);
        kzyf.setOnClickListener(this);
        zhfw.setOnClickListener(this);

        haolangzhong = findViewById(R.id.haolangzhong);
        haolangzhong.setOnClickListener(this);

        //专家
        zj_more_hint = findViewById(R.id.zj_more_hint);
        zj_more_hint.setOnClickListener(this);
        zj_img = findViewById(R.id.zj_img);
        zj_name = findViewById(R.id.zj_name);
        zj_star_1 = findViewById(R.id.zj_star_1);
        zj_star_2 = findViewById(R.id.zj_star_2);
        zj_star_3 = findViewById(R.id.zj_star_3);
        zj_star_4 = findViewById(R.id.zj_star_4);
        zj_star_5 = findViewById(R.id.zj_star_5);
        zj_mark_1 = findViewById(R.id.zj_mark_1);
        zj_mark_2 = findViewById(R.id.zj_mark_2);
        zj_content = findViewById(R.id.zj_content);

        //药品
        fj_more_hint = findViewById(R.id.fj_more_hint);
        fj_more_hint.setOnClickListener(this);
        fj_img = findViewById(R.id.fj_img);
        fj_name = findViewById(R.id.fj_name);
        fj_star_1 = findViewById(R.id.fj_star_1);
        fj_star_2 = findViewById(R.id.fj_star_2);
        fj_star_3 = findViewById(R.id.fj_star_3);
        fj_star_4 = findViewById(R.id.fj_star_4);
        fj_star_5 = findViewById(R.id.fj_star_5);
        fj_mark_1 = findViewById(R.id.fj_mark_1);
        fj_mark_2 = findViewById(R.id.fj_mark_2);
        fj_content = findViewById(R.id.fj_content);
        //救助服务
        jzfw = findViewById(R.id.jzfw);
        zxt = findViewById(R.id.zxt);
        jzfw.setOnClickListener(this);
        zxt.setOnClickListener(this);

        //专家详情
        zj_rl = findViewById(R.id.zj_rl);
        zj_rl.setOnClickListener(this);
        home_drug_rl = findViewById(R.id.home_drug_rl);
        home_drug_rl.setOnClickListener(this);

        //顶部消息
        msg_img = findViewById(R.id.msg_img);
        msg_img.setOnClickListener(this);

        //隐藏web页面
        webView = findViewById(R.id.webview);

        banner = findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (homeData.getBanner().size() > 0) {
                    com.zhongyi.zhongyi.bean.Banner banner = homeData.getBanner().get(position);
                    if (!TextUtils.isEmpty(banner.getContent())) {
//                        String url = banner.getAd_link();
//                        //String url =NetConstant.BASE_URL_WEB+"1";
//                        Intent intent = new Intent(HomeActivity.this, WebActivity.class);
//                        intent.putExtra("url", url);
//                        intent.putExtra("title", "广告详情");
//                        startActivity(intent);
                    }
                }
            }
        });

        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
        String url = "http://120.92.10.2:81/hlz/static/empty.html?token=" + token + "&userid=" + userid;
        initHideData(url);
        //获取首页数据
        getData();
        //获取位置
        getPosition();
        //查看会员
        getMemberData(token,userid);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPosition() {
        if (PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
            // mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //获取最近3s内精度最高的一次定位结果：
            mLocationOption.setOnceLocation(true);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //启动定位
            mLocationClient.startLocation();
        }else{
            PermissionUtils.requestPermission(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            getPosition();
        }
    }

    AMapLocationListener mLocationListener = new AMapLocationListener(){
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                      //可在其中解析amapLocation获取相应内容。
                    String country = amapLocation.getCountry();//国家信息
                    String province =  amapLocation.getProvince();//省信息
                    String city =  amapLocation.getCity();//城市信息
                    double lat = amapLocation.getLatitude();//获取纬度
                    double lon = amapLocation.getLongitude();//获取经度
                    Log.e("AmapError","country:"+country+"province:"+province+"city:"+city);
                    positon_tv.setText(city);
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };



    private void getData() {
        token = (String) SharedPreferencesUtils.getParam("token", "");
        String url = NetConstant.BASE_URL + NetConstant.INDEX;
        NetUtils.setCallback(homeCallback);
        NetUtils.getDataAsync(url);
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
                HomeDataTemp tempBean = JSON.parseObject(response.body().string(), HomeDataTemp.class);
                if (tempBean.getCode() == 0) {
                    homeData = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(HomeActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (homeData != null && homeData.getDoctor() != null && homeData.getDoctor().size() > 0) {
                doctor = homeData.getDoctor().get(0);
                //头像
                Glide.with(HomeActivity.this).load(NetConstant.BASE_IMGE_URL + doctor.getIcon()).transform(new GlideCircleTransform(HomeActivity.this)).into(zj_img);
                zj_name.setText(doctor.getName());
                zj_mark_1.setText(doctor.getLabel());
                zj_mark_1.setVisibility(View.GONE);
                zj_content.setText(doctor.getTechnical());
            }

            if (homeData != null && homeData.getAirDrug() != null && homeData.getAirDrug().size() > 0) {
                drug = homeData.getAirDrug().get(0);
                //头像
                Glide.with(HomeActivity.this).load(NetConstant.BASE_IMGE_URL + drug.getPic1()).into(fj_img);
                fj_name.setText(drug.getName());
                fj_mark_1.setText(drug.getLname());
                fj_mark_1.setVisibility(View.GONE);
                fj_content.setText(drug.getMemo());
            }

            if (homeData != null && homeData.getBanner() != null & homeData.getBanner().size() > 0) {
                List<String> images = new ArrayList<>();

                for (com.zhongyi.zhongyi.bean.Banner banner : homeData.getBanner()) {
                    images.add(NetConstant.BASE_IMGE_URL + banner.getPic());
                }
                //设置图片集合
                banner.setImages(images);
                //banner设置方法全部调用完毕时最后调用
                banner.start();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zj_more_hint: {
                Intent intent = new Intent(HomeActivity.this, ZYZJActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fj_more_hint: {
                String token = (String) SharedPreferencesUtils.getParam("token", "");
                String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
                Intent intent = new Intent(HomeActivity.this, ShangChengWebActivity.class);
                intent.putExtra("url", NetConstant.GOODS_LIST + "?token=" + token + "&userid=" + userid);
                startActivity(intent);
            }
            break;
            case R.id.zxwz: {
              if (isMember) {
                    Intent intent = new Intent(HomeActivity.this, ZXWZActivity.class);
                    startActivity(intent);
                }else{
                   showMemberDialog();
             }
            }
            break;
            case R.id.zyzj: {
                if (isMember){
                    Intent intent = new Intent(HomeActivity.this, ZYZJActivity.class);
                    intent.putExtra("isHZY", false);
                    startActivity(intent);
               }else {
                  showMemberDialog();
              }
            }
            break;
            case R.id.kzyf: {
                Intent intent = new Intent(HomeActivity.this, ShangChengWebActivity.class);
                intent.putExtra("url", NetConstant.KZYF);
                startActivity(intent);
            }
            break;
            case R.id.zhfw: {
              if (isMember) {
                    Intent intent = new Intent(HomeActivity.this, ZHFWActivity.class);
                    startActivity(intent);
               }else{
                   showMemberDialog();
                }
            }
            break;
            case R.id.ask_ll: {
                if (isMember){
                    Intent intent = new Intent(HomeActivity.this, ZXWZHomeActivity.class);
                    startActivity(intent);
                }else{
                   showMemberDialog();
               }
            }
            break;
            case R.id.mine_ll: {
                Intent intent = new Intent(HomeActivity.this, MineActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.zj_rl: {
                String token = (String) SharedPreferencesUtils.getParam("token", "");
                Intent intent = new Intent(HomeActivity.this, ZhuanJiaWebActivity.class);
                intent.putExtra("url", NetConstant.EXPERT + "?token=" + token + "&docid=" + doctor.getId());
                startActivity(intent);
            }
            break;

            case R.id.home_drug_rl: {
                String token = (String) SharedPreferencesUtils.getParam("token", "");
                String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
                Intent intent = new Intent(HomeActivity.this, ShangChengWebActivity.class);
                intent.putExtra("url", NetConstant.GOODS_DETAIL + "?token=" + token + "&userid=" + userid + "&drugid=" + drug.getId());
                startActivity(intent);
            }
            break;
            case R.id.msg_img: {
                Intent intent = new Intent(HomeActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.MESSAGE + "?token=" + token);
                intent.putExtra("title", "消息");
                startActivity(intent);
            }
            break;
            case R.id.haolangzhong: {
                Intent intent = new Intent(HomeActivity.this, HLZYYActivity.class);
                intent.putExtra("isHZY", true);
                startActivity(intent);
            }
            break;
            case R.id.jzfw: {
                Intent intent = new Intent(HomeActivity.this, ZXWZ_HelpActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.zxt:{
                //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                //    设置Title的图标
                builder.setIcon(R.mipmap.icon);
                //    设置Title的内容
                builder.setTitle("咨询台");
                //    设置Content来显示一个信息
                builder.setMessage("请拨打咨询电话：010-82038231");
                //    设置一个PositiveButton
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent dialIntent =  new Intent(Intent.ACTION_DIAL,Uri.parse("tel:010-82038231"));//跳转到拨号界面，同时传递电话号码
                        startActivity(dialIntent);
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
            break;
        }
    }

    protected void initHideData(String url) {
        webView.loadUrl(url);//加载url
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        /***打开本地缓存提供JS调用**/
        settings.setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webView.destroy();
        webView = null;
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    //获取医生列表
    private void getMemberData(String token,String userId) {
        String url = NetConstant.BASE_URL2+NetConstant.MEMBER;
        //设置回调方法
        NetUtils.setCallback(memberCallback);

        MemberBean memberBean = new MemberBean();
        memberBean.setToken(token);
        memberBean.setUserId(userId);
        String json = JSON.toJSONString(memberBean);

        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback memberCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                MemberTemp tempBean = JSON.parseObject(response.body().string(), MemberTemp.class);
                if (tempBean.getCode() == 0) {
                    member = tempBean.getData();
                    //更新UI界面
                    handler.post(memberRunnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(HomeActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable memberRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (member!=null){
                if (member.getCode()==200102){
                    isMember = true;
                }else{
                    isMember = false;
                }
            }
        }
    };

    private void showMemberDialog(){
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //    设置Title的图标
        builder.setIcon(R.mipmap.icon);
        //    设置Title的内容
        builder.setTitle("好郎中会员");
        //    设置Content来显示一个信息
        builder.setMessage("您不是会员，暂时无法访问");
        //    设置一个PositiveButton
        builder.setPositiveButton("查看会员", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String shareCode = (String) SharedPreferencesUtils.getParam("shareCode","");
                Intent intent = new Intent(HomeActivity.this,ShangChengWebActivity.class);
                intent.putExtra("url",NetConstant.MEMBER_BUY+"?shareCode="+shareCode);
                startActivity(intent);
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
