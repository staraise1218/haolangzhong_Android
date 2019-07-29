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
import com.zhongyi.zhongyi.bean.temp.MarkTemp;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZXWZHomeActivity extends BaseActivity implements View.OnClickListener {
    private Spinner body_sp;

    //title
    private ImageView title_back;
    private TextView title_content;
    private TextView title_right;

    //上传的图片
    private ImageView pic_1,pic_2,pic_3;
    private LinearLayout zxwz_pic_ll;
    //上传的内容
    private EditText content;

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


    private List<Mark> bodys;
    private Handler handler = new Handler();
    private List<File> files = new ArrayList<>();
    private Mark body;
    private Mark liaofa;

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

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxwz);
        body_sp = findViewById(R.id.buwei_sp);

        //title
        title_back = findViewById(R.id.title_back);
        title_back.setVisibility(View.GONE);
        title_content = findViewById(R.id.title_content);
        title_content.setText("在线问诊");
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

        mActivity = this;
        mContext = this;

        //获取身体部位标签
        getData(1);

    }


    private void getData(int type) {
        String token = "{"+(String) SharedPreferencesUtils.getParam("token","")+"}";
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_LABLE;
        NetUtils.setCallback(markCallback);
        NetUtils.postJson(url,token);
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
                    Toast.makeText(ZXWZHomeActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };


    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (bodys !=null&& bodys.size()>0){
                List<String> content = new ArrayList<>();
                for (Mark temp: bodys){
                    content.add(temp.getContent());
                }
                ArrayAdapter adapter = new ArrayAdapter(ZXWZHomeActivity.this,R.layout.item,R.id.text,content);
                body_sp.setAdapter(adapter);
                body_sp.setPrompt("身体部位");
                body_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int position  = body_sp.getSelectedItemPosition();
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
        switch (view.getId()){
            case R.id.title_back:{
                ZXWZHomeActivity.this.finish();
            }
                break;
            case R.id.title_right:{
                String content_str = content.getText().toString().trim();
                String bodyId = body.getId();
                 // String liaofaId = liaofa.getId();
                Intent intent = new Intent(ZXWZHomeActivity.this,ZXWZSearchActivity.class);
                intent.putExtra("bodyId",bodyId);
                //intent.putExtra("liaofaId",liaofaId);
                intent.putExtra("content_str",content_str);
                if (files!=null&&files.size()>0){
                    for (int i =0;i<files.size();i++){
                        intent.putExtra("filePath"+i,files.get(i).getAbsolutePath());
                    }
                }
                startActivity(intent);
            }
            break;
            case R.id.zxwz_pic_ll:{
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(ZXWZHomeActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(ZXWZHomeActivity.this.findViewById(R.id.content_rl), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
                break;
            case R.id.home_ll: {
                Intent intent = new Intent(ZXWZHomeActivity.this,HomeActivity.class);
                startActivity(intent);
                ZXWZHomeActivity.this.finish();
            }
            break;
            case R.id.mine_ll: {
                Intent intent = new Intent(ZXWZHomeActivity.this,MineActivity.class);
                startActivity(intent);
                ZXWZHomeActivity.this.finish();
            }
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
}
