package com.zhongyi.zhongyi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.zhongyi.zhongyi.bean.OrderCheck;
import com.zhongyi.zhongyi.bean.PayResult;
import com.zhongyi.zhongyi.bean.PayTemp;
import com.zhongyi.zhongyi.bean.UploadFile;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.OrderRegisterBean;
import com.zhongyi.zhongyi.bean.ask.Pay;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.bean.temp.GuaHao;
import com.zhongyi.zhongyi.bean.temp.OrderCheckTemp;
import com.zhongyi.zhongyi.bean.temp.PayBean;
import com.zhongyi.zhongyi.bean.temp.UploadFileTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.SelectPicPopupWindow;
import com.zhongyi.zhongyi.utils.CommonUtils;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.PermissionUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.WX_Pay;
import com.zhongyi.zhongyi.wxapi.WXPayEntryActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShangChengWebActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar pg1;

    private String token;
    private String userid;
    private String url;

    private String tempUrl;
    private PayBean payBean;
    private String type;
    private String orderNo;
    private String payType;
    private Handler handler = new Handler();
    private static final int SDK_PAY_FLAG = 1;
    private OrderCheck orderCheck;

    //图片相关类
    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_OPEN_REQUEST_CODE = 3;
    private static final int GALLERY_OPEN_REQUEST_CODE = 4;
    private static final int CROP_IMAGE_REQUEST_CODE = 5;
    private String mCameraFilePath = "";
    private String mCropImgFilePath = "";
    private boolean isClickRequestCameraPermission = false;
    private Activity mActivity;
    private Context mContext;
    private int height;
    private int width;

    private List<UploadFile> uploadFiles;

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
        mActivity = this;
        mContext = this;
    }


    protected void initData(String url) {

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
                Intent intent = new Intent(ShangChengWebActivity.this, HomeActivity.class);
                ShangChengWebActivity.this.startActivity(intent);
                ShangChengWebActivity.this.finish();
            }
        }, "goHome");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void back() {
                try{
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                }catch (Exception e){
                    ShangChengWebActivity.this.finish();
                }
            }
        }, "back");

        webView.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @JavascriptInterface
            public void pay(String total_fee,String name,String orderNo,String type,String payType) {
                Log.e("test0","调用支付"+payType);
                //购买的服务内容
                SharedPreferencesUtils.setParam("payType",payType);
                ShangChengWebActivity.this.type = type;
                ShangChengWebActivity.this.orderNo = orderNo;
                ShangChengWebActivity.this.payType = payType;
                //调用获取订单的方法
                getData(total_fee,name,orderNo,type);
            }
        }, "pay");

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void take(int height,int width){
                // Toast.makeText(getApplicationContext(), height+"---"+width, Toast.LENGTH_SHORT).show();
                ShangChengWebActivity.this.height = height;
                ShangChengWebActivity.this.width = width;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(ShangChengWebActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(ShangChengWebActivity.this.findViewById(R.id.content_rl), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }

        }, "test");

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

        webView.loadUrl(url);//加载url
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
                    Toast.makeText(ShangChengWebActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if ("0".equals(type)){
                WX_Pay pay = new WX_Pay(ShangChengWebActivity.this);
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
            PayTask alipay = new PayTask(ShangChengWebActivity.this);
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
                showAlert(ShangChengWebActivity.this, "支付失败" + payResult);
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

    //获取医生列表
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
                Intent intent =new Intent(ShangChengWebActivity.this, PayFinishActivity.class);
                intent.putExtra("name","支付宝支付");
                ShangChengWebActivity.this.startActivity(intent);
                ShangChengWebActivity.this.finish();
            }
        }
    };

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    if (!PermissionUtils.checkCameraPermission(mContext)) {
                        isClickRequestCameraPermission = true;
                        PermissionUtils.requestCameraPermission(mActivity, CAMERA_PERMISSION_REQUEST_CODE);
                    } else {
                        if (!PermissionUtils.checkSDCardPermission(mContext)) {
                            isClickRequestCameraPermission = true;
                            PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                        } else {
                            CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                        }
                    }
                    break;
                case R.id.btn_pick_photo:
                    if (!PermissionUtils.checkSDCardPermission(mContext)) {
                        PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                    } else {
                        CommonUtils.startGallery(mActivity, GALLERY_OPEN_REQUEST_CODE);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private String generateCameraFilePath() {
        String mCameraFileDirPath = Environment.getExternalStorageDirectory() + File.separator + "camera";
        File mCameraFileDir = new File(mCameraFileDirPath);
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
        mCameraFilePath = mCameraFileDirPath + File.separator + System.currentTimeMillis() + ".jpg";
        return mCameraFilePath;
    }

    private String generateCropImgFilePath() {
        String mCameraFileDirPath = Environment.getExternalStorageDirectory() + File.separator + "camera";
        File mCameraFileDir = new File(mCameraFileDirPath);
        if (!mCameraFileDir.exists()) {
            mCameraFileDir.mkdirs();
        }
        mCropImgFilePath = mCameraFileDirPath + File.separator + System.currentTimeMillis() + ".jpg";
        return mCropImgFilePath;
    }

    private BitmapFactory.Options getBitampOptions(String path) {
        BitmapFactory.Options mOptions = new BitmapFactory.Options();
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, mOptions);
        return mOptions;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.checkRequestPermissionsResult(grantResults)) {
                    if (!PermissionUtils.checkSDCardPermission(mContext)) {
                        PermissionUtils.requestSDCardPermission(mActivity, SDCARD_PERMISSION_REQUEST_CODE);
                    } else {
                        isClickRequestCameraPermission = false;
                        CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                    }
                } else {
                    CommonUtils.showMsg(mContext, "打开照相机请求被拒绝!");
                }
                break;
            case SDCARD_PERMISSION_REQUEST_CODE:
                if (PermissionUtils.checkRequestPermissionsResult(grantResults)) {
                    if (isClickRequestCameraPermission) {
                        isClickRequestCameraPermission = false;
                        CommonUtils.startCamera(mActivity, CAMERA_OPEN_REQUEST_CODE, generateCameraFilePath());
                    } else {
                        CommonUtils.startGallery(mActivity, GALLERY_OPEN_REQUEST_CODE);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_OPEN_REQUEST_CODE:
                    if (data == null || data.getExtras() == null) {
                        //mImg.setImageBitmap(BitmapFactory.decodeFile(mCameraFilePath));

                        BitmapFactory.Options mOptions = getBitampOptions(mCameraFilePath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mCameraFilePath,
                                mCropImgFilePath,
                                mOptions.outHeight,
                                mOptions.outWidth,
                                width,
                                height,
                                CROP_IMAGE_REQUEST_CODE);
                    } else {
                        Bundle mBundle = data.getExtras();
//                        DebugUtils.d(TAG, "onActivityResult::CAMERA_OPEN_REQUEST_CODE::data = " + mBundle.get("data"));
                    }
                    break;
                case GALLERY_OPEN_REQUEST_CODE:
                    if (data == null) {
//                        DebugUtils.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data null");
                    } else {
//                        DebugUtils.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mContext, data.getData());
//                        DebugUtils.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        /*
                        mImg.setImageBitmap(BitmapFactory.decodeFile(mGalleryPath));
                        */


                        BitmapFactory.Options mOptions = getBitampOptions(mGalleryPath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mGalleryPath,
                                mCropImgFilePath,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                width,
                                height,
                                CROP_IMAGE_REQUEST_CODE);
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
//                    DebugUtils.d(TAG, "onActivityResult::CROP_IMAGE_REQUEST_CODE::mCropImgFilePath = " + mCropImgFilePath);
                    //Bitmap bitmap = BitmapFactory.decodeFile(mCropImgFilePath);
                    File file = new File(mCropImgFilePath);
                    //将图片进行上传
                    saveData(file);
                    break;
            }
        }
    }

    /**
     * 上传文件
     */
    private void saveData(File file) {
        String url = NetConstant.BASE_URL + NetConstant.UPLOAD;
        //设置回调方法
        NetUtils.setCallback(uploadCallback);
        Map<String, String> keymap = new HashMap<>();
        //获取网络数据
        NetUtils.post_file(url, keymap,file);
    }

    //登录回调
    Callback uploadCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                UploadFileTemp tempBean = JSON.parseObject(response.body().string(), UploadFileTemp.class);
                if (tempBean.getCode() == 0) {
                    uploadFiles = tempBean.getData();
                    //更新UI界面
                    handler.post(upLoadRunnableUi);
                } else if (tempBean.getCode() == 400) {


                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable upLoadRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (uploadFiles!=null&&uploadFiles.size()>0)
                // 通过Handler发送消息
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        // 注意调用的JS方法名要对应上
                        // 调用javascript的callJS()方法
                        webView.loadUrl("javascript:getPicId('"+ uploadFiles.get(0).getId() +"')");
                    }
                });
        }
    };
}
