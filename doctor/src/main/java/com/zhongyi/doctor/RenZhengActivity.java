package com.zhongyi.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.doctor.bean.UploadFile;
import com.zhongyi.doctor.bean.ask.RenZhengBean;
import com.zhongyi.doctor.bean.temp.RenZhengTemp;
import com.zhongyi.doctor.bean.temp.UploadFileTemp;
import com.zhongyi.doctor.constant.LogUtils;
import com.zhongyi.doctor.constant.NetCode;
import com.zhongyi.doctor.constant.NetConstant;
import com.zhongyi.doctor.control.SelectPicPopupWindow;
import com.zhongyi.doctor.utils.CommonUtils;
import com.zhongyi.doctor.utils.NetUtils;
import com.zhongyi.doctor.utils.PermissionUtils;
import com.zhongyi.doctor.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RenZhengActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout name_ll;
    private LinearLayout phone_ll;
    private LinearLayout shenfen_ll;
    //private LinearLayout address_ll;
    private ImageView pic_1, pic_2;
    private TextView submit;
    private TextView title;
    private ImageView back;

    //内容展示
    private TextView name_tv;
    private TextView phone_tv;
    private TextView shenfen_tv;
    private TextView address_tv;

    private ImageView title_share;

    //自定义的弹出框类
    SelectPicPopupWindow menuWindow;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_OPEN_REQUEST_CODE = 3;
    private static final int GALLERY_OPEN_REQUEST_CODE = 4;
    private static final int CROP_IMAGE_REQUEST_CODE = 5;
    private static final int EDIT_NAME = 6;
    private static final int EDIT_SHENFEN = 7;
    private static final int EDIT_ADDRESS = 8;
    private String mCameraFilePath = "";
    private String mCropImgFilePath = "";
    private boolean isClickRequestCameraPermission = false;
    private Activity mActivity;
    private Context mContext;

    //信息
    private String nickName;
    private String phone;
    private String shenfen;
    private String address;
    private Handler handler = new Handler();


    //图片
    private List<File> fileList = new ArrayList<>();
    private File file;
    private List<UploadFile> uploadFile;
    //当前展示的图片位置
    private int current_pic = 1;



    @Override
    protected void setView() {
        setContentView(R.layout.activity_renzheng);

        back = findViewById(R.id.title_back);
        back.setOnClickListener(this);
        title = findViewById(R.id.title_content);
        title.setText("认证审核");
        title_share = findViewById(R.id.title_other);
        title_share.setVisibility(View.GONE);

        name_ll = findViewById(R.id.name_ll);
        phone_ll = findViewById(R.id.phone_ll);
        shenfen_ll = findViewById(R.id.shenfen_ll);
       // address_ll = findViewById(R.id.address_ll);
        pic_1 = findViewById(R.id.pic_1);
        pic_2 = findViewById(R.id.pic_2);
        submit = findViewById(R.id.submit);

        name_ll.setOnClickListener(this);
        phone_ll.setOnClickListener(this);
        shenfen_ll.setOnClickListener(this);
      //  address_ll.setOnClickListener(this);
        pic_1.setOnClickListener(this);
        pic_2.setOnClickListener(this);
        //pic_3.setOnClickListener(this);
        submit.setOnClickListener(this);

        //个人信息
        name_tv = findViewById(R.id.name_tv);
        phone_tv = findViewById(R.id.phone_tv);
        shenfen_tv = findViewById(R.id.shenfen_tv);
        address_tv = findViewById(R.id.address_tv);

        phone = getIntent().getStringExtra("phone");
        phone_tv.setText(phone);

        mActivity = this;
        mContext = this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:{
                RenZhengActivity.this.finish();
                break;
            }
            case R.id.name_ll: {
                Intent intent = new Intent(RenZhengActivity.this, EditNameActivity.class);
                startActivityForResult(intent, EDIT_NAME);
            }
            break;
            case R.id.phone_ll: {

            }
            break;
            case R.id.shenfen_ll: {
                Intent intent = new Intent(RenZhengActivity.this, EditShenFenActivity.class);
                startActivityForResult(intent, EDIT_SHENFEN);
            }
            break;
            case R.id.address_ll: {
                Intent intent = new Intent(RenZhengActivity.this, EditAddressActivity.class);
                startActivityForResult(intent, EDIT_ADDRESS);
            }
            break;
            case R.id.pic_1: {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(RenZhengActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(RenZhengActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.pic_2: {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(RenZhengActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(RenZhengActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.pic_3: {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(RenZhengActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(RenZhengActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.submit: {
                if (fileList.size() == 2) {
                    saveFile(fileList);
                } else {
                    //saveData(nickName, phone, shenfen, address, uploadFile);
                    Toast.makeText(RenZhengActivity.this,"请提交身份证正反照片",Toast.LENGTH_LONG).show();
                }

            }
            break;
        }
    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

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
                        BitmapFactory.Options mOptions = getBitampOptions(mCameraFilePath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mCameraFilePath,
                                mCropImgFilePath,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                pic_1.getWidth(),
                                pic_1.getHeight(),
                                CROP_IMAGE_REQUEST_CODE);
                    } else {
                        Bundle mBundle = data.getExtras();
                    }
                    break;
                case GALLERY_OPEN_REQUEST_CODE:
                    if (data == null) {

                    } else {
                        String mGalleryPath = CommonUtils.parseGalleryPath(mContext, data.getData());
                        BitmapFactory.Options mOptions = getBitampOptions(mGalleryPath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mGalleryPath,
                                mCropImgFilePath,
                                4032,
                                3016,
                                pic_1.getWidth(),
                                pic_1.getHeight(),
                                CROP_IMAGE_REQUEST_CODE);
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
                    if (current_pic == 1) {
                        pic_1.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        pic_2.setBackgroundResource(R.mipmap.wenzhe_pic);
                    } else if (current_pic == 2) {
                        pic_2.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                    }
                    //图片位置变量
                    current_pic++;

                    file = new File(mCropImgFilePath);
                    fileList.add(file);

                    break;
                case EDIT_NAME: {
                    if (data != null || data.getExtras() != null) {
                        nickName = data.getStringExtra("name");
                        name_tv.setText(nickName);
                    }
                }
                break;
                case EDIT_SHENFEN: {
                    if (data != null || data.getExtras() != null) {
                        shenfen = data.getStringExtra("shenfen");
                        shenfen_tv.setText(shenfen);
                    }
                }
                break;
                case EDIT_ADDRESS: {
                    if (data != null || data.getExtras() != null) {
                        address = data.getStringExtra("address");
                        address_tv.setText(address);
                    }
                }
                break;
            }
        }
    }

    private void saveFile(List<File> files) {
        String url = NetConstant.BASE_URL + NetConstant.UPLOAD;
        //设置回调方法
        NetUtils.setCallback(uploadCallback);
        Map<String, String> keymap = new HashMap<>();
        //获取网络数据
        NetUtils.post_file(url, keymap, files);
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
                    uploadFile = tempBean.getData();
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
            if (uploadFile != null) {
                if (TextUtils.isEmpty(nickName)||TextUtils.isEmpty(shenfen)){
                    Toast.makeText(RenZhengActivity.this,"审核信息均为必填项，请检查后填写",Toast.LENGTH_LONG).show();
                }else{
                    saveData(nickName, phone, shenfen,  uploadFile);
                }
            }
        }
    };


    /**
     * 修改昵称
     */
    private void saveData(String nickName, String phone, String shenfen, List<UploadFile> uploadFiles) {
        if (nickName != null || file != null) {
            String token = (String) SharedPreferencesUtils.getParam("token", "");
            String id = (String) SharedPreferencesUtils.getParam("user_id", "");

            String url = NetConstant.BASE_URL + NetConstant.REN_ZHENG;
            //设置回调方法
            NetUtils.setCallback(userCallback);
            RenZhengBean renZhengBean = new RenZhengBean();
            renZhengBean.setId(id);
            renZhengBean.setToken(token);
            renZhengBean.setName(nickName);
            renZhengBean.setTelephone(phone);
            renZhengBean.setIdcard(shenfen);
            switch (uploadFiles.size()) {
                case 3:
                    renZhengBean.setCertificate3(uploadFiles.get(2).getId());
                case 2:
                    renZhengBean.setCertificate2(uploadFiles.get(1).getId());
                case 1:
                    renZhengBean.setCertificate1(uploadFiles.get(0).getId());
                    break;
            }
            String json = JSON.toJSONString(renZhengBean);
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
                RenZhengTemp tempBean = JSON.parseObject(response.body().string(), RenZhengTemp.class);
                if (tempBean.getCode() == 0) {
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
            Toast.makeText(RenZhengActivity.this, "认证审核中", Toast.LENGTH_LONG).show();
            RenZhengActivity.this.finish();
        }
    };

}
