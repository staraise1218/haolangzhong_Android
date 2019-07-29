package com.zhongyi.zhongyi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.zhongyi.zhongyi.bean.OrderCheck;
import com.zhongyi.zhongyi.bean.PayResult;
import com.zhongyi.zhongyi.bean.PayTemp;
import com.zhongyi.zhongyi.bean.ask.OrderRegisterBean;
import com.zhongyi.zhongyi.bean.ask.Pay;
import com.zhongyi.zhongyi.bean.temp.OrderCheckTemp;
import com.zhongyi.zhongyi.bean.temp.PayBean;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.WX_Pay;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MyOrderWebActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar pg1;

    private String token;
    private String userid;
    private String url;

    private String tempUrl;
    private PayBean payBean;
    private String type;
    private Handler handler = new Handler();
    private static final int SDK_PAY_FLAG = 1;
    private String orderNo;
    private String payType;
    private OrderCheck orderCheck;
    //购买商品
    private final String BUY_GOODS = "1";
    //购买挂号
    private final String BUY_REGISTER = "2";
    //购买调理
    private final String BUY_SERVICE = "3";
    //购买会员
    private final String BUY_MEMBER = "4";

    @Override
    protected void setView() {
        setContentView(R.layout.activity_web_shangcheng);
        webView = findViewById(R.id.webview);

        url = getIntent().getStringExtra("url");
        initData(url);
    }


    protected void initData(String url) {
        webView.loadUrl(url);//加载url
        webView.clearCache(true);
        pg1 = findViewById(R.id.progressBar1);

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

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void back() {
                MyOrderWebActivity.this.finish();
            }
        }, "back");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void goHome() {
                Intent intent = new Intent(MyOrderWebActivity.this, HomeActivity.class);
                MyOrderWebActivity.this.startActivity(intent);
                MyOrderWebActivity.this.finish();
            }
        }, "goHome");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @JavascriptInterface
            public void pay(String total_fee,String name,String orderNo,String type,String payType) {
                Log.e("test0","调用支付"+payType);
                //购买的服务内容
                SharedPreferencesUtils.setParam("payType",payType);
                MyOrderWebActivity.this.type = type;
                MyOrderWebActivity.this.orderNo = orderNo;
                MyOrderWebActivity.this.payType = payType;
                //调用获取订单的方法
                getData(total_fee,name,orderNo,type);
            }
        }, "pay");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //保存当前的url信息
                tempUrl = url;
                view.loadUrl(url);
                return true;
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

                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                return true;
            }
        });
    }

    //使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true

        if (tempUrl != null && tempUrl.contains("airPharmacy")) {
            webView.loadUrl("javascript:closeWindow()");
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webView.destroy();
        webView = null;
    }


    private void getData(String total_fee,String body,String orderNo,String type) {
        token = (String) SharedPreferencesUtils.getParam("token", "");
        String url;
        if ("0".equals(type)){
            url = NetConstant.TEST;
        }else if ("1".equals(type)){
            url =  NetConstant.TEST_AL;
        }else{
            url = "";
        }
        NetUtils.setCallback(homeCallback);
        Pay pay = new Pay();
        pay.setToken(token);
        pay.setTotal_fee(total_fee);
        pay.setBody(body);
        pay.setOrderNo(orderNo);
        String json = JSON.toJSONString(pay);

        //获取网络数据
        NetUtils.postJson(url, json);
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
                PayTemp tempBean = JSON.parseObject(response.body().string(), PayTemp.class);
                if (tempBean.getCode() == 0) {
                    payBean = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(MyOrderWebActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if ("0".equals(type)){
                WX_Pay pay = new WX_Pay(MyOrderWebActivity.this);
                pay.pay(payBean);
            }else if ("1".equals(type)){
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }else{
                url = "";
            }
        }
    };

    Runnable payRunnable = new Runnable() {

        @Override
        public void run() {
            String orderInfo = payBean.getOrderInfo();
            PayTask alipay = new PayTask(MyOrderWebActivity.this);
            Map<String, String> result = alipay.payV2(orderInfo, true);

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                Intent intent =new Intent(ShangChengWebActivity.this, PayFinishActivity.class);
//                intent.putExtra("name","支付宝支付");
//                ShangChengWebActivity.this.startActivity(intent);
                //ShangChengWebActivity.this.finish();

                if (!TextUtils.isEmpty(orderNo)){
                    Log.e("ShangChengWebActivity","进入订单支付查询页面");
                    getData(orderNo,payType);
                }else{
                    Log.e("ShangChengWebActivity","orderNo为空");
                }

            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                showAlert(MyOrderWebActivity.this, "支付失败" + payResult);
            }
        };
    };

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确认", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    private void getData(String orderNo,String payType) {
        String url = "";
        if (payType.equals(BUY_GOODS)){
            url = NetConstant.BASE_URL2 + NetConstant.GET_ORDER_SUCCESS;
        }else if (payType.equals(BUY_REGISTER)){
            url = NetConstant.BASE_URL2 + NetConstant.REGISTERB_ORDER;
        }else if(payType.equals(BUY_SERVICE)){
            url = NetConstant.BASE_URL2 + NetConstant.GET_TIAO_LI_ORDER_SUCCESS;
        }else{
            url =  NetConstant.BASE_URL2 + NetConstant.GET_MEMBER_ORDER_SUCCESS;
        }
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        //设置回调方法
        NetUtils.setCallback(orderCallback);
        OrderRegisterBean guaHao = new OrderRegisterBean();
        guaHao.setToken(token);
        guaHao.setOrderNo(orderNo);
        String json = JSON.toJSONString(guaHao);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback orderCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                OrderCheckTemp tempBean = JSON.parseObject(response.body().string(), OrderCheckTemp.class);
                if (tempBean.getCode() == 0) {
                    orderCheck = tempBean.getData();
                    //更新UI界面
                    handler.post(orderCheckRunnableUi);
                } else if (tempBean.getCode() == 400) {

                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable orderCheckRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (orderCheck != null&&orderCheck.getCode()==200103) {
                //支付成功跳转到支付完成页面
                Intent intent =new Intent(MyOrderWebActivity.this, PayFinishActivity.class);
                intent.putExtra("name","支付宝支付");
                MyOrderWebActivity.this.startActivity(intent);
                MyOrderWebActivity.this.finish();
            }
        }
    };
}
