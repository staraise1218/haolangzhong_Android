package com.zhongyi.zhongyi;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.zhongyi.adapter.FellowAdapter;
import com.zhongyi.zhongyi.adapter.TypeAdapter;
import com.zhongyi.zhongyi.adapter.ZYZJAdapter;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Fellow;
import com.zhongyi.zhongyi.bean.Mark;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.FellowBean;
import com.zhongyi.zhongyi.bean.ask.MyFellowBean;
import com.zhongyi.zhongyi.bean.temp.CancelFellowTemp;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.bean.temp.FellowTemp;
import com.zhongyi.zhongyi.bean.temp.MarkTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.holder.TypeHolder;
import com.zhongyi.zhongyi.holder.ZYZJHolder;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FellowActivity extends BaseActivity implements RecyclerArrayAdapter.OnItemClickListener, ZYZJHolder.AskListener, ZYZJHolder.FellowListener, View.OnClickListener {
    private EasyRecyclerView recyclerView;
    private ImageView title_back;
    private TextView title_content;

    private int page = 1;
    private final int pageNumber = 2;
    private Handler handler = new Handler();
    private List<Doctor> doctors;
    private FellowAdapter adapter;
    private List<String> lables = new ArrayList<>();
    private Fellow fellow;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_fellow);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("我的关注");


        //医生列表
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new FellowAdapter(this, this, this));

        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, FellowActivity.this));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(FellowActivity.this);

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


        //获取数据
        getData(page, pageNumber);
    }

    //获取医生列表
    private void getData(int page, int pageNumber) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
        String url = NetConstant.BASE_URL + NetConstant.USER_COLLECTION;
        //设置回调方法
        NetUtils.setCallback(doctorCallback);

        MyFellowBean fellowBean = new MyFellowBean();
        fellowBean.setPageNum(page + "");
        fellowBean.setPageSize(pageNumber + "");
        fellowBean.setUserid(userid);
        fellowBean.setToken(token);
        String json = JSON.toJSONString(fellowBean);

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
                }, 1000);
            }
        }
    };

    @Override
    public void onItemClick(int position) {
        Doctor doctor = adapter.getItem(position);
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        Intent intent = new Intent(FellowActivity.this, ZhuanJiaWebActivity.class);
        intent.putExtra("url", NetConstant.EXPERT+"?token="+token+"&docid="+doctor.getId());
        startActivity(intent);
    }

    @Override
    public void ask(String id,String price) {
        Intent intent = new Intent(FellowActivity.this,ZXWZ_ZJActivity.class);
        intent.putExtra("doctorId",id);
        intent.putExtra("cost",price);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                FellowActivity.this.finish();
            }
            break;
        }
    }

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
                    Toast.makeText(FellowActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable fellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(FellowActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FellowActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable cancelFellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(FellowActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
        }
    };
}
