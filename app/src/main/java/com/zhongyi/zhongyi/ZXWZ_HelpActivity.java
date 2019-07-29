package com.zhongyi.zhongyi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.zhongyi.bean.Mark;
import com.zhongyi.zhongyi.bean.UploadFile;
import com.zhongyi.zhongyi.bean.ZXWZ;
import com.zhongyi.zhongyi.bean.ask.OrderBean;
import com.zhongyi.zhongyi.bean.temp.MarkTemp;
import com.zhongyi.zhongyi.bean.temp.UploadFileTemp;
import com.zhongyi.zhongyi.bean.temp.ZXWZTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.SelectPicPopupWindow;
import com.zhongyi.zhongyi.utils.CommonUtils;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.PermissionUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZXWZ_HelpActivity extends BaseActivity implements View.OnClickListener {
    private Spinner body_sp;

    //title
    private ImageView title_back;
    private TextView title_content;
    private TextView title_right;

    //上传的图片
    private ImageView pic_1, pic_2, pic_3;
    private LinearLayout zxwz_pic_ll;
    //上传的内容
    private EditText content;


    private List<Mark> bodys;
    private Handler handler = new Handler();
    private List<File> files = new ArrayList<>();
    private Mark body;

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
    //照片的位置
    private int picNum = 0;

    //
    private String doctorId;
    private String cost;
    private List<UploadFile> uploadFiles;
    private String content_str;
    private String bodyId;
    private ZXWZ zxwz;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxwz_home);
        body_sp = findViewById(R.id.buwei_sp);

        //title
        title_back = findViewById(R.id.title_back);
        title_back.setOnClickListener(this);
        title_content = findViewById(R.id.title_content);
        title_content.setText("求助通道");
        title_right = findViewById(R.id.title_right);
        title_right.setVisibility(View.VISIBLE);
        title_right.setText("提交");
        title_right.setOnClickListener(this);

        //上传的图片
        pic_1 = findViewById(R.id.pic_1);
        pic_2 = findViewById(R.id.pic_2);
        pic_3 = findViewById(R.id.pic_3);
        content = findViewById(R.id.wenzhen_content);
        zxwz_pic_ll = findViewById(R.id.zxwz_pic_ll);
        zxwz_pic_ll.setOnClickListener(this);

        mActivity = this;
        mContext = this;

        //获取身体部位标签
        getData(1);

    }


    private void getData(int type) {
        String token = "{" + (String) SharedPreferencesUtils.getParam("token", "") + "}";
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_LABLE;
        NetUtils.setCallback(markCallback);
        NetUtils.postJson(url, token);
    }


    //身体部位回调
    Callback markCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                MarkTemp tempBean = JSON.parseObject(response.body().string(), MarkTemp.class);
                if (tempBean.getCode() == 0) {
                    bodys = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(ZXWZ_HelpActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };


    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (bodys != null && bodys.size() > 0) {
                List<String> content = new ArrayList<>();
                for (Mark temp : bodys) {
                    content.add(temp.getContent());
                }
                ArrayAdapter adapter = new ArrayAdapter(ZXWZ_HelpActivity.this, R.layout.item, R.id.text, content);
                body_sp.setAdapter(adapter);
                body_sp.setPrompt("身体部位");
                body_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int position = body_sp.getSelectedItemPosition();
                        body = bodys.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                ZXWZ_HelpActivity.this.finish();
            }
            break;
            case R.id.title_right: {
                content_str = content.getText().toString().trim();
                bodyId = body.getId();
                if (files!=null&&files.size()>0) {
                    saveFile(files);
                }else{
                    subData(doctorId, cost, null);
                }
            }
            break;
            case R.id.zxwz_pic_ll: {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(ZXWZ_HelpActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(ZXWZ_HelpActivity.this.findViewById(R.id.content_rl), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
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
                                pic_1.getWidth(),
                                pic_1.getHeight(),
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
                                pic_1.getWidth(),
                                pic_1.getHeight(),
                                CROP_IMAGE_REQUEST_CODE);
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
//                    DebugUtils.d(TAG, "onActivityResult::CROP_IMAGE_REQUEST_CODE::mCropImgFilePath = " + mCropImgFilePath);

                    picNum = ++picNum;
                    switch (picNum) {
                        case 1: {
                            pic_1.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        }
                        break;
                        case 2: {
                            pic_2.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        }
                        break;
                        case 3: {
                            pic_3.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        }
                        break;
                    }

                    //Bitmap bitmap = BitmapFactory.decodeFile(mCropImgFilePath);
                    //将图片进行上传
                    File file = new File(mCropImgFilePath);
                    files.add(file);
                    // saveData(mCropImgFilePath);
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
                    uploadFiles = tempBean.getData();
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
            if (uploadFiles != null && uploadFiles.size() > 0) {
                subData(doctorId, cost, uploadFiles);
            }
        }
    };

    //获取医生列表
    private void subData(String id, String price, List<UploadFile> uploadFiles) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userId = (String) SharedPreferencesUtils.getParam("user_id", "");
        String userName  = (String) SharedPreferencesUtils.getParam("nike_name","");
        Date date = new Date();
        String url = NetConstant.BASE_URL + NetConstant.HELP_ORDER;
        //设置回调方法
        NetUtils.setCallback(orderCallback);
        List<String> pics = new ArrayList<>();
        if (uploadFiles != null && uploadFiles.size() > 0) {
            for (UploadFile temp : uploadFiles) {
                pics.add(temp.getId());
            }
        }
        OrderBean orderBean = new OrderBean();
        orderBean.setContent(content_str);
        orderBean.setCreate_by(userName);
        orderBean.setDisease(bodyId);
        orderBean.setToken(token);
        orderBean.setPic(pics);
        orderBean.setUser_id(userId);
        orderBean.setNum(String.valueOf(date.getTime()));
        String json = JSON.toJSONString(orderBean);
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
                ZXWZTemp tempBean = JSON.parseObject(response.body().string(), ZXWZTemp.class);
                if (tempBean.getCode() == 0) {
                    zxwz = tempBean.getData();
                    //更新UI界面
                    handler.post(orderRunnableUi);
                } else if (tempBean.getCode() == 400) {


                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable orderRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ZXWZ_HelpActivity.this, "求助信息已提交", Toast.LENGTH_LONG).show();
            ZXWZ_HelpActivity.this.finish();
        }
    };

}
