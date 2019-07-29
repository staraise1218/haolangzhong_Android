package com.zhongyi.doctor;

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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.zhongyi.doctor.bean.UploadFile;
import com.zhongyi.doctor.bean.ask.UserBean;
import com.zhongyi.doctor.bean.temp.ModifyTemp;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private ImageView title_back;
    private TextView title_content;
    private ImageView title_share;
    private LinearLayout address_ll;
    //头像
    private RelativeLayout user_img_rl;
    private ImageView mine_img;

    //基础信息
    private TextView name_tv;
    private EditText age_tv;
    private EditText zc_tv;
    private EditText cy_tv;
    private TextView phone_tv;
    private TextView shenfen_tv;
    private TextView address_tv;
    private TextView zhicheng_tv;

    //分类和技能
    private LinearLayout cate_ll;
    private LinearLayout skill_ll;
    private RelativeLayout show_hint;
    private TextView chooose_cate_tv;
    private TextView chooose_skill_tv;

    //上门服务
    private RadioGroup radioGroup = null;
    private RadioButton radioButton_boy, radioButton_girl;
    private String comeFlag;
    private EditText come_cost_et;
    private LinearLayout zhicheng_ll;

    //服务荣誉
    private EditText wenzhen_content;
    private ImageView pic_1, pic_2, pic_3;
    private String introducepic1;
    private String introducepic2;
    private String introducepic3;

    //城市编码
    private String cityid = "110000";
    private String address = "北京市";
    private String detalAddress = "";

    //提交按钮
    private TextView submit;

    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SDCARD_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_OPEN_REQUEST_CODE = 3;
    private static final int GALLERY_OPEN_REQUEST_CODE = 4;
    private static final int CROP_IMAGE_REQUEST_CODE = 5;
    private static final int CATE = 6;
    private static final int SKILL = 7;
    private static final int CATE2 = 8;
    private static final int EDIT_ADDRESS = 9;
    private String mCameraFilePath = "";
    private String mCropImgFilePath = "";
    private boolean isClickRequestCameraPermission = false;
    private Activity mActivity;
    private Context mContext;

    //图片类型(0为头像，1-3为服务的图片)
    private int current_pic = 0;
    //头像
    private File headPic;
    private String icon;
    //服务图片
    private File file;
    private String cate2;
    private List<String> cate2List = new ArrayList<>();
    private List<String> skillList = new ArrayList<>();
    private List<String> skillContentList = new ArrayList<>();
    private List<String> cateList = new ArrayList<>();
    private Handler handler = new Handler();
    private UploadFile uploadFile;

    String age ;
    String zhicheng ;
    String cyage;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_info);
        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_content.setText("个人信息");
        title_back.setOnClickListener(this);
        title_share = findViewById(R.id.title_other);
        title_share.setVisibility(View.GONE);

        //头像
        user_img_rl = findViewById(R.id.user_img_rl);
        user_img_rl.setOnClickListener(this);
        mine_img = findViewById(R.id.mine_img);
        //基础信息
        name_tv = findViewById(R.id.name_tv);
        phone_tv = findViewById(R.id.phone_tv);
        shenfen_tv = findViewById(R.id.shenfen_tv);
        address_tv = findViewById(R.id.address_tv);
        zhicheng_tv = findViewById(R.id.zhicheng_tv);
        address_ll = findViewById(R.id.address_ll);
        address_ll.setOnClickListener(this);
        age_tv = findViewById(R.id.age_et);
        zc_tv = findViewById(R.id.zc_et);
        cy_tv = findViewById(R.id.cy_age_et);


        //radioGroup
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_sex_id);
        radioButton_boy = (RadioButton) findViewById(R.id.boy_id);
        radioButton_girl = (RadioButton) findViewById(R.id.girl_id);
        radioGroup.setOnCheckedChangeListener(listen);
        come_cost_et = findViewById(R.id.come_cost_et);
        zhicheng_ll = findViewById(R.id.zhicheng_ll);
        zhicheng_ll.setOnClickListener(this);

        //分类和技能
        cate_ll = findViewById(R.id.cate_ll);
        skill_ll =findViewById(R.id.skill_ll);
        show_hint =findViewById(R.id.show_hint);
        cate_ll.setOnClickListener(this);
        skill_ll.setOnClickListener(this);
        show_hint.setOnClickListener(this);
        chooose_skill_tv = findViewById(R.id.chooose_skill_tv);
        chooose_cate_tv = findViewById(R.id.chooose_cate_tv);

        //服务认证
        wenzhen_content =findViewById(R.id.wenzhen_content);
        pic_1 =findViewById(R.id.pic_1);
        pic_2 = findViewById(R.id.pic_2);
        pic_3 =findViewById(R.id.pic_3);
        pic_1.setOnClickListener(this);
        pic_2.setOnClickListener(this);
        pic_3.setOnClickListener(this);
        submit =findViewById(R.id.submit);
        submit.setOnClickListener(this);

        mActivity = this;
        mContext = this;

        showUserInfo();
    }

    private void showUserInfo() {
        String name = (String) SharedPreferencesUtils.getParam("name", "");
        if (!TextUtils.isEmpty(name)) {
            name_tv.setText(name);
        }
        String phone = (String) SharedPreferencesUtils.getParam("phone", "");
        if (!TextUtils.isEmpty(phone)) {
            phone_tv.setText(phone);
        }
        String shenfen = (String) SharedPreferencesUtils.getParam("shenfen", "");
        if (!TextUtils.isEmpty(shenfen)) {
            shenfen_tv.setText(shenfen);
        }
        String address = (String) SharedPreferencesUtils.getParam("adress", "");
        if (!TextUtils.isEmpty(address)) {
            address_tv.setText(address);
        }
        //设置年龄
        String ageNum = (String) SharedPreferencesUtils.getParam("ageNum", "");
        if (!TextUtils.isEmpty(ageNum)) {
            age_tv.setText(ageNum);
        }
        //设置职称
        String professional = (String) SharedPreferencesUtils.getParam("professional", "");
        if (!TextUtils.isEmpty(professional)) {
            zc_tv.setText(professional);
        }
        //设置从业年限
        String workYear = (String) SharedPreferencesUtils.getParam("workYear", "");
        if (!TextUtils.isEmpty(workYear)) {
            cy_tv.setText(workYear);
        }

        //设置地址
        String come_address = (String) SharedPreferencesUtils.getParam("come_address", "");
        if (!TextUtils.isEmpty(come_address)) {
            zhicheng_tv.setText(come_address);
        }
        //设置地址
        String come_flag = (String) SharedPreferencesUtils.getParam("come_flag", "");
        if (!TextUtils.isEmpty(come_flag)) {
            if ("0".equals(come_flag)){
                radioGroup.check(radioButton_girl.getId());
            }else{
                radioGroup.check(radioButton_boy.getId());
            }
        }
        //设置服务费
        String come_cost =  (String) SharedPreferencesUtils.getParam("come_cost", "");
        if (!TextUtils.isEmpty(come_cost)) {
            come_cost_et.setText(come_cost);
        }
        //设置荣誉介绍
        String introduce =  (String) SharedPreferencesUtils.getParam("introduce", "");
        if (!TextUtils.isEmpty(introduce)) {
            wenzhen_content.setText(introduce);
        }
        //设置荣誉照片1
        String introducepic1 =  (String) SharedPreferencesUtils.getParam("introducepic1", "");
        if (!TextUtils.isEmpty(introducepic1)) {
            Glide.with(this).load(NetConstant.BASE_IMGE_URL + introducepic1).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    pic_1.setImageDrawable(resource); //显示图片
                }
            });
        }
        //设置荣誉照片2
        String introducepic2 =  (String) SharedPreferencesUtils.getParam("introducepic2", "");
        if (!TextUtils.isEmpty(introducepic2)) {
            Glide.with(this).load(NetConstant.BASE_IMGE_URL + introducepic2).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    pic_2.setImageDrawable(resource); //显示图片
                }
            });
        }

        //设置荣誉照片3
        String introducepic3 =  (String) SharedPreferencesUtils.getParam("introducepic3", "");
        if (!TextUtils.isEmpty(introducepic3)) {
            Glide.with(this).load(NetConstant.BASE_IMGE_URL + introducepic3).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    pic_3.setImageDrawable(resource); //显示图片
                }
            });
        }


        //设置技能
        String lables =  (String) SharedPreferencesUtils.getParam("lables", "");
        if (!TextUtils.isEmpty(lables)) {
            String[] array = lables.split(",");
            skillList = new ArrayList(Arrays.asList(array));
        }
        //显示技能
        String content =  (String) SharedPreferencesUtils.getParam("content", "");
        if (!TextUtils.isEmpty(content)) {
            String[] array = content.split(",");
            skillContentList = new ArrayList(Arrays.asList(array));
            if (array.length>0){
                if (array.length<=2){
                    chooose_skill_tv.setText(array[0]+","+array[1]);
                }else{
                    chooose_skill_tv.setText(array[0]+","+array[1]+"...");
                }
            }
        }
        //显示分类
        String classify =  (String) SharedPreferencesUtils.getParam("cate2", "");
        if (!TextUtils.isEmpty(content)) {
            if (classify.equals("1")){
                chooose_cate_tv.setText("师承");
            }else if(classify.equals("2")){
                chooose_cate_tv.setText("祖传");
            }else{
                chooose_cate_tv.setText("学院");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        String image = (String) SharedPreferencesUtils.getParam("icon", "");
        if (!TextUtils.isEmpty(image)) {
            Glide.with(this).load(NetConstant.BASE_IMGE_URL + image).into(new SimpleTarget<GlideDrawable>() {
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
        switch (view.getId()) {
            case R.id.title_back: {
                UserInfoActivity.this.finish();
            }
            break;
            case R.id.user_img_rl: {
                current_pic = 0;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(UserInfoActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.pic_1: {
                current_pic = 1;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(UserInfoActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.pic_2: {
                current_pic = 2;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(UserInfoActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.pic_3: {
                current_pic = 3;
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(UserInfoActivity.this.findViewById(R.id.user_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
            break;
            case R.id.cate_ll: {
                Intent intent = new Intent(UserInfoActivity.this, Cate2Activity.class);
                startActivityForResult(intent, CATE2);
            }
            break;
            case R.id.skill_ll: {
                Intent intent = new Intent(UserInfoActivity.this, SkillActivity.class);
                String [] array = skillList.toArray(new String[skillList.size()]);
                intent.putExtra("marks",Arrays.toString(array));
                String [] arrayContent = skillContentList.toArray(new String[skillContentList.size()]);
                intent.putExtra("marks_content",Arrays.toString(arrayContent));
                startActivityForResult(intent, SKILL);
            }
            break;
            case R.id.submit: {
                String wenzhen = wenzhen_content.getText().toString().trim();
                String token = (String) SharedPreferencesUtils.getParam("token", "");
                String docid = (String) SharedPreferencesUtils.getParam("user_id", "");
                String comeCost = come_cost_et.getText().toString();
                age = age_tv.getText().toString().trim();
                zhicheng = zc_tv.getText().toString().trim();
                cyage = cy_tv.getText().toString().trim();
                UserBean userBean = new UserBean();
                userBean.setToken(token);
                userBean.setClassify(cate2);
                userBean.setDoctorid(docid);
                userBean.setIcon(icon);
                userBean.setIntroduce(wenzhen);
                userBean.setIntroducepic1(introducepic1);
                userBean.setIntroducepic2(introducepic2);
                userBean.setIntroducepic3(introducepic3);
                userBean.setIntroducevideo("");
                cateList.addAll(skillList);
                cateList.addAll(cate2List);
                userBean.setLable(cateList);
                userBean.setTechnical("2");
                userBean.setComeFlag(comeFlag);
                userBean.setComeCost(comeCost);
                userBean.setCityid(cityid);
                userBean.setChangeAddress(address);
                userBean.setAdress(detalAddress);
                userBean.setAgenum(age);
                userBean.setProfessional(zhicheng);
                userBean.setWorkyear(cyage);
                modify(userBean);
            }
            break;
            case R.id.zhicheng_ll:{
                selectAddress();
            }
                break;
            case R.id.address_ll:{
                Intent intent = new Intent(UserInfoActivity.this, EditAddressActivity.class);
                startActivityForResult(intent, EDIT_ADDRESS);
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
                        //mImg.setImageBitmap(BitmapFactory.decodeFile(mCameraFilePath));

                        BitmapFactory.Options mOptions = getBitampOptions(mCameraFilePath);
                        generateCropImgFilePath();
                        CommonUtils.startCropImage(
                                mActivity,
                                mCameraFilePath,
                                mCropImgFilePath,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                mOptions.outWidth,
                                mOptions.outHeight,
                                CROP_IMAGE_REQUEST_CODE);
                       // Log.e("OnError:",mOptions.outWidth+":"+ mOptions.outHeight+":"+pic_1.getWidth()+":"+pic_1.getHeight());
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
                                mOptions.outWidth,
                                mOptions.outHeight,
                                CROP_IMAGE_REQUEST_CODE);
                      //  Log.e("OnError:",mOptions.outWidth+":"+ mOptions.outHeight+":"+pic_1.getWidth()+":"+pic_1.getHeight());
                    }
                    break;
                case CROP_IMAGE_REQUEST_CODE:
                    if (current_pic == 0) {
                        //mine_img.setImageDrawable(null);
                        mine_img.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        headPic = new File(mCropImgFilePath);
                        saveFile(headPic);
                    } else if (current_pic == 1) {
                        pic_1.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        pic_2.setBackgroundResource(R.mipmap.wenzhe_pic);
                        //图片位置变量
                        current_pic++;
                        file = new File(mCropImgFilePath);
                        saveFile(file);
                    } else if (current_pic == 2) {
                        pic_2.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        pic_3.setBackgroundResource(R.mipmap.wenzhe_pic);
                        //图片位置变量
                        current_pic++;
                        file = new File(mCropImgFilePath);
                        saveFile(file);
                    } else {
                        pic_3.setImageBitmap(BitmapFactory.decodeFile(mCropImgFilePath));
                        //图片位置变量
                        current_pic++;
                        file = new File(mCropImgFilePath);
                        saveFile(file);
                    }
                    break;
                case CATE: {
                    if (data != null || data.getExtras() != null) {
                        String[] cates = data.getExtras().getStringArray("cate");
                        cate2List = new ArrayList(Arrays.asList(cates));
                    }
                }
                break;
                case SKILL: {
                    if (data != null || data.getExtras() != null) {
                        String[] marks = data.getExtras().getStringArray("marks");
                        String[] marks_content = data.getExtras().getStringArray("marks_content");
                        if (marks_content.length>0){
                            if (marks_content.length<=2){
                                if (marks_content.length==1){
                                    chooose_skill_tv.setText(marks_content[0]);
                                }else{
                                    chooose_skill_tv.setText(marks_content[0]+","+marks_content[1]);
                                }
                            }else{
                                chooose_skill_tv.setText(marks_content[0]+","+marks_content[1]+"...");
                            }
                        }
                        skillList = new ArrayList(Arrays.asList(marks));
                        skillContentList = new ArrayList(Arrays.asList(marks_content));
                }
                }
                break;
                case CATE2: {
                    if (data != null || data.getExtras() != null) {
                        cate2 = data.getExtras().getString("cate2");
                        String content = (String) data.getExtras().get("cate2_content");
                        chooose_cate_tv.setText(content);
                    }
                }
                break;
                case EDIT_ADDRESS: {
                    if (data != null || data.getExtras() != null) {
                        detalAddress = data.getStringExtra("address");
                        address_tv.setText(detalAddress);
                    }
                }
                break;
            }
        }
    }

    private void modify(UserBean userBean) {
        String url = NetConstant.BASE_URL + NetConstant.MODIFY;
        String json = JSON.toJSONString(userBean);
        NetUtils.setCallback(modifyCallback);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback modifyCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                ModifyTemp tempBean = JSON.parseObject(response.body().string(), ModifyTemp.class);
                if (tempBean.getCode() == 0) {
                    //更新UI界面
                    handler.post(runnableUi);
                } else {
                    Looper.prepare();
                    Toast.makeText(UserInfoActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(UserInfoActivity.this, "信息修改成功", Toast.LENGTH_SHORT).show();
            if (icon != null) {
                SharedPreferencesUtils.setParam("icon", icon);
            }
            if (cate2 != null) {
                SharedPreferencesUtils.setParam("cate2", cate2);
            }
            if (cateList != null && cateList.size() > 0) {
                SharedPreferencesUtils.setParam("cate", cateList.toArray());
            }

            if (age != null) {
                SharedPreferencesUtils.setParam("ageNum",age);
            }

            if (zhicheng != null) {
                SharedPreferencesUtils.setParam("professional", zhicheng);
            }

            if (cyage != null) {
                SharedPreferencesUtils.setParam("workYear", cyage);
            }

            UserInfoActivity.this.finish();
        }
    };


    private void saveFile(File file) {
        String url = NetConstant.BASE_URL + NetConstant.UPLOAD;
        //设置回调方法
        NetUtils.setCallback(uploadCallback);
        Map<String, String> keymap = new HashMap<>();
        //获取网络数据
        NetUtils.post_file(url, keymap, file);
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
            if (uploadFile != null) {
                switch (current_pic) {
                    case 0:
                        icon = uploadFile.getId();
                        break;
                    case 2:
                        introducepic1 = uploadFile.getId();
                        break;
                    case 3:
                        introducepic2 = uploadFile.getId();
                        break;
                    case 4:
                        introducepic3 = uploadFile.getId();
                        break;
                }
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listen = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            switch (group.getCheckedRadioButtonId()) {
                case R.id.girl_id:
                    comeFlag = "0";
                    break;
                case R.id.boy_id:
                    comeFlag = "1";
                    break;
                default:
                    break;
            }
        }
    };

    private void selectAddress() {
        CityConfig cityConfig = new CityConfig.Builder()
                .title("选择城市")//标题
                .titleTextSize(18)//标题文字大小
                .titleTextColor("#585858")//标题文字颜  色
                .titleBackgroundColor("#FFFFFF")//标题栏背景色
                .confirTextColor("#585858")//确认按钮文字颜色
                .confirmText("确定")//确认按钮文字
                .confirmTextSize(16)//确认按钮文字大小
                .cancelTextColor("#585858")//取消按钮文字颜色
                .cancelText("取消")//取消按钮文字
                .cancelTextSize(16)//取消按钮文字大小
                .setCityWheelType(CityConfig.WheelType.PRO_CITY)//显示类，只显示省份一级，显示省市两级还是显示省市区三级
                .showBackground(true)//是否显示半透明背景
                .visibleItemsCount(7)//显示item的数量
                .province("北京市")//默认显示的省份
                .city("北京市")//默认显示省份下面的城市
                .district("朝阳区")//默认显示省市下面的区县数据
                .provinceCyclic(true)//省份滚轮是否可以循环滚动
                .cityCyclic(true)//城市滚轮是否可以循环滚动
                .districtCyclic(true)//区县滚轮是否循环滚动
                .setCustomItemLayout(R.layout.item_city)//自定义item的布局
                .setCustomItemTextViewId(R.id.item_city_name_tv)//自定义item布局里面的textViewid
                .drawShadows(false)//滚轮不显示模糊效果
                .setLineColor("#03a9f4")//中间横线的颜色
                .setLineHeigh(5)//中间横线的高度
                .setShowGAT(true)//是否显示港澳台数据，默认不显示
                .build();
        CityPickerView mPicker=new CityPickerView();
        //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
        mPicker.setConfig(cityConfig);
        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);

        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

                //省份province
                //城市city
                //地区district
                if (city.getName().equals("省直辖县级行政单位")){
                    zhicheng_tv.setText(province.getName());
                    cityid = province.getId();
                    address = province.getName();
                }else {
                    zhicheng_tv.setText(province.getName()+city.getName());
                    cityid = city.getId();
                    address = province.getName()+city.getName();
                }

            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(UserInfoActivity.this, "已取消");
            }
        });

        //显示
        mPicker.showCityPicker( );
    }

}
