package com.zhongyi.zhongyi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhongyi.zhongyi.bean.HeadPic;
import com.zhongyi.zhongyi.bean.UploadFile;
import com.zhongyi.zhongyi.bean.User;
import com.zhongyi.zhongyi.bean.ask.UserInfoBean;
import com.zhongyi.zhongyi.bean.temp.HeadPicTemp;
import com.zhongyi.zhongyi.bean.temp.UploadFileTemp;
import com.zhongyi.zhongyi.bean.temp.UserTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;
import com.zhongyi.zhongyi.control.SelectPicPopupWindow;
import com.zhongyi.zhongyi.utils.CommonUtils;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.PermissionUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserActivity extends BaseActivity implements View.OnClickListener {
    private ImageView title_lift;
    private TextView title;
    private ImageView mine_img;
    private RelativeLayout user_img_rl;
    private LinearLayout name_ll;
    private TextView name_tv;
    private LinearLayout phone_ll;
    private TextView phone_tv;

    private TextView submit;
    //图片文件
    private File file;
    private String nickName;
    private UploadFile uploadFile;
    private User user;


    //自定义的弹出框类
    SelectPicPopupWindow menuWindow;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_OPEN_REQUEST_CODE = 3;
    private static final int GALLERY_OPEN_REQUEST_CODE = 4;
    private static final int CROP_IMAGE_REQUEST_CODE = 5;
    private static final int EDIT_NAME = 6;
    private String mCameraFilePath = "";
    private String mCropImgFilePath = "";
    private boolean isClickRequestCameraPermission = false;
    private Activity mActivity;
    private Context mContext;

    private Handler handler = new Handler();
    private HeadPic headPic;
    @Override
    protected void setView() {
        setContentView(R.layout.activity_user);

        title_lift = findViewById(R.id.title_back);
        title_lift.setOnClickListener(this);
        title = findViewById(R.id.title_content);
        title.setText("个人信息");

        mine_img = findViewById(R.id.mine_img);
        user_img_rl = findViewById(R.id.user_img_rl);
        user_img_rl.setOnClickListener(this);

        name_ll = findViewById(R.id.name_ll);
        name_tv = findViewById(R.id.name_tv);
        phone_tv = findViewById(R.id.phone_tv);
        name_ll.setOnClickListener(this);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);


        mActivity = this;
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = (String) SharedPreferencesUtils.getParam("nike_name","");
        if (!TextUtils.isEmpty(name)){
            name_tv.setText(name);
        }
        String phone = (String) SharedPreferencesUtils.getParam("phone","");
        if (!TextUtils.isEmpty(phone)){
            phone_tv.setText(phone);
        }
        String image = (String) SharedPreferencesUtils.getParam("icon","");
        if (!TextUtils.isEmpty(image)){
            Glide.with(this).load(NetConstant.BASE_IMGE_URL+image).transform(new GlideCircleTransform(UserActivity.this)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    mine_img.setImageDrawable(resource); //显示图片
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                UserActivity.this.finish();
                break;
            case R.id.user_img_rl: {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(UserActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(UserActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
            break;
            case R.id.name_ll:{
                Intent intent = new Intent(UserActivity.this,EditNameActivity.class);
                startActivityForResult(intent,6);
            }
                break;
            case R.id.submit:{
                if (file!=null) {
                    //保存信息
                    saveFile(file);
                }else{
                    saveData(nickName,null);
                }
            }
                break;
        }
    }

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
                                mOptions.outWidth,
                                mOptions.outHeight,
                                mine_img.getWidth(),
                                mine_img.getHeight(),
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
                                mine_img.getWidth(),
                                mine_img.getHeight(),
                                CROP_IMAGE_REQUEST_CODE);
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
//                    DebugUtils.d(TAG, "onActivityResult::CROP_IMAGE_REQUEST_CODE::mCropImgFilePath = " + mCropImgFilePath);

                    mine_img.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                    file = new File(mCropImgFilePath);

                    break;
                case EDIT_NAME:{
                    if (data != null || data.getExtras() != null){
                        nickName= data.getStringExtra("name");
                        name_tv.setText(nickName);
                    }
                }
                    break;
            }
        }
    }

    private void saveFile(File file){
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
                    uploadFile = tempBean.getData().get(0);
                    //更新UI界面
                    handler.post(uploadRunnableUi);
                } else if (tempBean.getCode() == 400) {


                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable uploadRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (uploadFile!=null){
                saveData(nickName,uploadFile.getId());
            }
        }
    };


    /**
     * 修改昵称
     */
    private void saveData(String nickName,String icon) {
        if (nickName!=null||file!=null){
            String token = (String) SharedPreferencesUtils.getParam("token","");
            String id = (String) SharedPreferencesUtils.getParam("user_id","");

            String url = NetConstant.BASE_URL + NetConstant.SAVE_USER_INFO;
            //设置回调方法
            NetUtils.setCallback(userCallback);
            UserInfoBean userInfoBean = new UserInfoBean();
            if (!TextUtils.isEmpty(icon)) {
                userInfoBean.setIcon(icon);
            }
            userInfoBean.setNike_name(nickName);
            userInfoBean.setToken(token);
            userInfoBean.setId(id);
            String json = JSON.toJSONString(userInfoBean);
            //获取网络数据
            NetUtils.postJson(url, json);
        }

    }

    //登录回调
    Callback userCallback = new Callback() {
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
                } else if (tempBean.getCode() == 400) {


                }
            }
        }
    };


    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(UserActivity.this, "修改成功", Toast.LENGTH_LONG).show();
            if (!TextUtils.isEmpty(user.getIcon())) {
                SharedPreferencesUtils.setParam("icon", user.getIcon());
            }
            SharedPreferencesUtils.setParam("nike_name",user.getNike_name());
            //结束当前页面
            UserActivity.this.finish();
        }
    };

}
