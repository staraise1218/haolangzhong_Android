package com.zhongyi.zhongyi;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.zhongyi.adapter.ZXWZSearchAdapter;
import com.zhongyi.zhongyi.bean.BaseNetBean;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Fellow;
import com.zhongyi.zhongyi.bean.UploadFile;
import com.zhongyi.zhongyi.bean.ZXWZ;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.FellowBean;
import com.zhongyi.zhongyi.bean.ask.OrderBean;
import com.zhongyi.zhongyi.bean.temp.CancelFellowTemp;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.bean.temp.FellowTemp;
import com.zhongyi.zhongyi.bean.temp.UploadFileTemp;
import com.zhongyi.zhongyi.bean.temp.ZXWZTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.holder.ZXWZSearchHolder;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZXWZSearchActivity extends BaseActivity implements RecyclerArrayAdapter.OnItemClickListener, View.OnClickListener, ZXWZSearchHolder.AskListener ,ZXWZSearchHolder.FellowListener{
    private EasyRecyclerView recyclerView;
    private ZXWZSearchAdapter adapter;
    private ImageView title_back;
    private TextView title_content;

    //跳转携带的数据
    private String bodyId;
    private String liaofaId;
    private String content_str;
    private String disease;
    private String file1;
    private String file2;
    private String file3;

    private int page = 1;
    private final int pageNumber = 10;
    private Handler handler = new Handler();
    private List<Doctor> doctors;
    private List<UploadFile> uploadFiles;
    private String doctorId;
    private String price;
    private ZXWZ zxwz;
    private Fellow fellow;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxwz_search);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("匹配专家");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ZXWZSearchAdapter(this, this,this));

        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, ZXWZSearchActivity.this));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(ZXWZSearchActivity.this);

        //更多加载
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                page = page + 1;
                getData(page, pageNumber);
            }

            @Override
            public void onMoreClick() {

            }
        });

        //写刷新事件
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doctors.clear();
                adapter.clear();
                //刷新事件
                page = 1;
                getData(page, pageNumber);
            }
        });

        //获取传递过来的数据
        getIntentData();

        //获取数据
        getData(page, pageNumber);
    }

    private void getIntentData() {
        bodyId = getIntent().getStringExtra("bodyId");
        liaofaId = getIntent().getStringExtra("liaofaId");
        content_str = getIntent().getStringExtra("content_str");
        disease = getIntent().getStringExtra("disease");
        file1 = getIntent().getStringExtra("filePath0");
        file2 = getIntent().getStringExtra("filePath1");
        file3 = getIntent().getStringExtra("filePath2");
    }

    //获取医生列表
    private void getData(int page, int pageNumber) {
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_List;
        //设置回调方法
        NetUtils.setCallback(doctorCallback);

        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setPageNum(page + "");
        doctorBean.setPageSize(pageNumber + "");
        List<String> lables = new ArrayList<>();
        //lables.add(bodyId);
        //lables.add(liaofaId);
        lables.add(disease);
        doctorBean.setLabels(lables);
        String json = JSON.toJSONString(doctorBean);

        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback doctorCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                DoctorTemp tempBean = JSON.parseObject(response.body().string(), DoctorTemp.class);
                if (tempBean.getCode() == 0) {
                    doctors = tempBean.getData().getItems();
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
            if (doctors != null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(doctors);
                    }
                }, 100);
            }
        }
    };

    @Override
    public void onItemClick(int position) {
        Doctor doctor = adapter.getItem(position);
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String url = NetConstant.EXPERT + "?token=" + token + "&docid=" + doctor.getId()+"&disease="+disease+"&content="+content_str;
        if (!TextUtils.isEmpty(file1)){
            url = url+"&pics="+file1;
        }
        if (!TextUtils.isEmpty(file2)){
            url = url+","+file2;
        }
        if (!TextUtils.isEmpty(file3)){
            url = url+","+file3;
        }
        Intent intent = new Intent(ZXWZSearchActivity.this, ZhuanJiaWebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void ask(String id, String price) {
//        doctorId = id;
//        this.price = price;
//        List<File> files = new ArrayList<>();
//        if (file1 != null) {
//            File file1_img = new File(file1);
//            files.add(file1_img);
//        }
//        if (file2 != null) {
//            File file2_img = new File(file2);
//            files.add(file2_img);
//        }
//        if (file3 != null) {
//            File file3_img = new File(file3);
//            files.add(file3_img);
//        }
//        if (files != null && files.size() > 0) {
//            //上传文件
//            saveFile(files);
//        } else {
//            subData(id, price, null);
//        }
    }

    //获取医生列表
    private void subData(String id, String price, List<UploadFile> uploadFiles) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userId = (String) SharedPreferencesUtils.getParam("user_id", "");
        String url = NetConstant.BASE_URL + NetConstant.SUBMIT_DATA;
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
        orderBean.setCreate_by(userId);
        orderBean.setDoctorid(id);
        orderBean.setAmount(price);
        orderBean.setBody(bodyId);
        orderBean.setTherapy(liaofaId);
        orderBean.setToken(token);
        orderBean.setPic(pics);
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
            Toast.makeText(ZXWZSearchActivity.this, "问诊信息已提交", Toast.LENGTH_LONG).show();
            String userId = (String) SharedPreferencesUtils.getParam("user_id", "");
            //进入支付页面
            Intent intent = new Intent(ZXWZSearchActivity.this, ZXWebActivity.class);
            intent.putExtra("url", NetConstant.CONSULTATION_PAY + "?orderId=" + zxwz.getId() + "&userId=" + userId);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                ZXWZSearchActivity.this.finish();
            }
            break;
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
                subData(doctorId, price, uploadFiles);
            }
        }
    };

    @Override
    public void fellow(boolean type, String id) {
        if (type){
            fellowData(id);
        }else{
            cancelFellowData(id);
        }
    }

    private void fellowData(String id) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");

        String url = NetConstant.BASE_URL + NetConstant.FELLOW;
        FellowBean fellowBean = new FellowBean();
        fellowBean.setUserid(userid);
        fellowBean.setToken(token);
        fellowBean.setType("6");
        fellowBean.setCollectionid(id);
        NetUtils.setCallback(fellowCallback);
        String json = JSON.toJSONString(fellowBean);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //全部标签回调
    Callback fellowCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                FellowTemp tempBean = JSON.parseObject(response.body().string(), FellowTemp.class);
                if (tempBean.getCode() == 0) {
                    fellow = tempBean.getData();
                    //更新UI界面
                    handler.post(fellowRunnableUi);
                } else{
                    Looper.prepare();
                    Toast.makeText(ZXWZSearchActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable fellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ZXWZSearchActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
        }
    };

    private void cancelFellowData(String id) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");

        String url = NetConstant.BASE_URL + NetConstant.CANCEL_FELLOW+userid+"/"+id+"/"+token;
        NetUtils.setCallback(cancelFellowCallback);
        //获取网络数据
        NetUtils.getDataAsync(url);
    }

    //全部标签回调
    Callback cancelFellowCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                CancelFellowTemp tempBean = JSON.parseObject(response.body().string(), CancelFellowTemp.class);
                if (tempBean.getCode() == 0) {
                    //更新UI界面
                    handler.post(cancelFellowRunnableUi);
                } else{
                    Looper.prepare();
                    Toast.makeText(ZXWZSearchActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable cancelFellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ZXWZSearchActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
        }
    };
}
