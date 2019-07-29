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
import com.zhongyi.zhongyi.bean.PayResult;
import com.zhongyi.zhongyi.bean.PayTemp;
import com.zhongyi.zhongyi.bean.ask.Pay;
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

public class RegisterWebActivity extends BaseActivity {
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

        WebSettings mWebSettings = webView.getSettings();

        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebSettings.setSupportZoom(false);//是否可以缩放，默认true
        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
        webView.getSettings().setDomStorageEnabled(true);

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void goHome() {
                Intent intent = new Intent(RegisterWebActivity.this, HomeActivity.class);
                RegisterWebActivity.this.startActivity(intent);
                RegisterWebActivity.this.finish();
            }
        }, "goHome");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void back() {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        }, "back");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @JavascriptInterface
            public void pay(String total_fee,String name,String orderNo,String type) {
                RegisterWebActivity.this.type = type;
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
                    Toast.makeText(RegisterWebActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if ("0".equals(type)){
                WX_Pay pay = new WX_Pay(RegisterWebActivity.this);
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
            PayTask alipay = new PayTask(RegisterWebActivity.this);
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
                Intent intent =new Intent(RegisterWebActivity.this, PayFinishActivity.class);
                intent.putExtra("name","支付宝支付");
                RegisterWebActivity.this.startActivity(intent);
                RegisterWebActivity.this.finish();
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                showAlert(RegisterWebActivity.this, "支付失败" + payResult);
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
}
