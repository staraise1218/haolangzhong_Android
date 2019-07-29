package com.zhongyi.zhongyi;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.zhongyi.adapter.TypeAdapter;
import com.zhongyi.zhongyi.adapter.ZYZJAdapter;
import com.zhongyi.zhongyi.bean.City;
import com.zhongyi.zhongyi.bean.ClassfiyBean;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Fellow;
import com.zhongyi.zhongyi.bean.Mark;
import com.zhongyi.zhongyi.bean.User;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.FellowBean;
import com.zhongyi.zhongyi.bean.temp.CancelFellowTemp;
import com.zhongyi.zhongyi.bean.temp.CityTemp;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.bean.temp.FellowTemp;
import com.zhongyi.zhongyi.bean.temp.MarkTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.ClassfiySeletView;
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

public class ZYZJActivity extends BaseActivity implements RecyclerArrayAdapter.OnItemClickListener, ZYZJHolder.AskListener, ZYZJHolder.FellowListener, View.OnClickListener, TypeHolder.onClick, ClassfiySeletView.OnItemClickListener {
    private EasyRecyclerView recyclerView;
    private ImageView title_back;
    private TextView title_content;

    //tab标签
    private RelativeLayout fellow_video;
    private RelativeLayout fellow_expert;
    private TextView fellow_video_tv;
    private TextView fellow_expert_tv;
    private TextView fellow_video_line;
    private TextView fellow_expert_line;


    //全部标签
    private RelativeLayout type_all_rl;
    private EasyRecyclerView recyclerView_all;
    private TextView search_sure_tv;

    //位置
    RelativeLayout idllClassfiySeletView;
    private static final String TAG = "PigActivity";
    private List<ClassfiyBean> mClassfiyBeanList;
    private List<City> cities;
    private ClassfiySeletView id_csv;


    private int page = 1;
    private final int pageNumber = 10;
    private Handler handler = new Handler();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Mark> marks;
    private ZYZJAdapter adapter;
    private TypeAdapter typeAdapter;
    private List<String> lables = new ArrayList<>();
    private Fellow fellow;

    //判断是否是好郎中医院
    private boolean isHZY = false;
    //默认的连接
    private String baseUrl = NetConstant.BASE_URL+NetConstant.DOCTOR_INFO;
    //是不是采用城市来进行获取数据
    private boolean isCity = false;
    private String cityId;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_zyzj);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("中医专家");

        //tab切换
        fellow_video = findViewById(R.id.fellow_video);
        fellow_expert = findViewById(R.id.fellow_expert);
        fellow_video.setOnClickListener(this);
        fellow_expert.setOnClickListener(this);

        fellow_video_tv = findViewById(R.id.fellow_video_tv);
        fellow_expert_tv = findViewById(R.id.fellow_expert_tv);
        fellow_video_line = findViewById(R.id.fellow_video_line);
        fellow_expert_line = findViewById(R.id.fellow_expert_line);

        //医生列表
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ZYZJAdapter(this, this, this));
        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, ZYZJActivity.this));
        recyclerView.addItemDecoration(itemDecoration);
        adapter.setOnItemClickListener(ZYZJActivity.this);

        //更多加载
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                page = page + 1;
                if (isCity){
                    getData2(page,pageNumber,cityId);
                }else{
                    getData(baseUrl,page, pageNumber);
                }

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
                if (isCity){
                    getData2(1,pageNumber,cityId);
                }else{
                    getData(baseUrl,page, pageNumber);
                }
            }
        });

        //全部标签
        type_all_rl = findViewById(R.id.type_all_rl);
        recyclerView_all = findViewById(R.id.recyclerView_all);
        typeAdapter = new TypeAdapter(this, this);
        typeAdapter.setHasStableIds(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(typeAdapter.obtainGridSpanSizeLookUp(2));
        recyclerView_all.setLayoutManager(gridLayoutManager);
        recyclerView_all.setAdapter(typeAdapter);

        SpaceDecoration itemDecoration_all = new SpaceDecoration((int) Utils.convertDpToPixel(8, ZYZJActivity.this));
        recyclerView_all.addItemDecoration(itemDecoration_all);
        search_sure_tv = findViewById(R.id.search_sure_tv);
        search_sure_tv.setOnClickListener(this);

        idllClassfiySeletView = (RelativeLayout) findViewById(R.id.id_ll_ClassfiySeletView);
        id_csv = (ClassfiySeletView) findViewById(R.id.id_csv);
        List<ClassfiyBean> classfiyBeanList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ClassfiyBean classfiyBean = new ClassfiyBean();
            classfiyBean.setID(i);
            classfiyBean.setBeanID(ClassfiySeletView.FIX_KEY_DEFAULT + i);
            classfiyBean.setName("选项" + i);
            classfiyBean.setChildClassfiyBeanList(null);
            classfiyBeanList.add(classfiyBean);
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        Log.d(TAG, "onCreate: screenHeight:" + screenHeight);
        id_csv.setListMaxHeight(screenHeight);
        id_csv.setOnContentViewChangeListener(new ClassfiySeletView.OnContentViewChangeListener() {
            @Override
            public void onContentViewShow() {
                Log.d(TAG, "onContentViewShow: ");
                recyclerView.setVisibility(View.GONE);
                if (type_all_rl.getVisibility()==View.VISIBLE){
                    type_all_rl.setVisibility(View.GONE);
                }
                changeBgColor(idllClassfiySeletView, false);
            }

            @Override
            public void onContentViewDismiss() {
                Log.d(TAG, "onContentViewDismiss: ");
                recyclerView.setVisibility(View.VISIBLE);
                changeBgColor(idllClassfiySeletView, true);
            }
        });

        id_csv.setOnItemClickListener(this);

        isHZY = getIntent().getBooleanExtra("isHLZ", false);
        //获取数据
        getData(baseUrl,page, pageNumber);
        //获取职位信息
        getWeizhi();
    }

    private void getWeizhi() {
        String url = NetConstant.BASE_URL + NetConstant.CITY;
        String json = "{ \"token\":\"" + (String) SharedPreferencesUtils.getParam("token", "") + "\"}";
        //设置回调方法
        NetUtils.setCallback(cityCallback);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //获取医生列表
    private void getData(String str ,int page, int pageNumber) {
        String url;
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        //设置回调方法
        NetUtils.setCallback(doctorCallback);

        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setToken(token);
        doctorBean.setPageNum(page + "");
        doctorBean.setPageSize(pageNumber + "");

        if (isHZY) {
            url = NetConstant.BASE_URL + NetConstant.DOCTOR_INFO_OWN;
        } else {
            url = str;
            doctorBean.setRecommend("desc");
        }
        if(lables!=null&&lables.size()>0){
            doctorBean.setLabels(lables);
        }
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
                }, 1000);
            }
        }
    };

    //城市回调
    Callback cityCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                CityTemp tempBean = JSON.parseObject(response.body().string(), CityTemp.class);
                if (tempBean.getCode() == 0) {
                    cities = tempBean.getData().getAreaInfoList();
                    //更新UI界面
                    handler.post(cityrunnableUi);
                } else if (tempBean.getCode() == 400) {


                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable cityrunnableUi = new Runnable() {
        @Override
        public void run() {
            //
            mClassfiyBeanList = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                City catesBean = cities.get(i);
                ClassfiyBean classfiyBean = new ClassfiyBean();
                classfiyBean.setID(i);
                classfiyBean.setBeanID(catesBean.getId() + "");
                classfiyBean.setName(catesBean.getName());
                classfiyBean.setSelected(false);
//
                List<City> childrenBeanList = catesBean.getChild();
                List<ClassfiyBean.ChildClassfiyBean> cccs = new ArrayList<>();
                for (int j = 0; j < childrenBeanList.size(); j++) {//Math.random():获取0~1随机数
                    City childrenBean = childrenBeanList.get(j);
                    ClassfiyBean.ChildClassfiyBean ccc = new ClassfiyBean.ChildClassfiyBean();
                    ccc.setID(j);
                    ccc.setBeanID(childrenBean.getId() + "");
                    ccc.setName(childrenBean.getName());
                    ccc.setCount("" + (int) (Math.random() * 500 + 1));//Math.random():获取0~1随机数
                    cccs.add(ccc);
                    classfiyBean.setSelected(false);
                }
                classfiyBean.setChildClassfiyBeanList(cccs);
                mClassfiyBeanList.add(classfiyBean);
            }
            //设置备选数据
            id_csv.setupClassfiyBeanList(mClassfiyBeanList);
        }
    };


    @Override
    public void onItemClick(int position) {
        Doctor doctor = adapter.getItem(position);
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        Intent intent = new Intent(ZYZJActivity.this, ZhuanJiaWebActivity.class);
        intent.putExtra("url", NetConstant.INTRODUCTION_ZHAO + "?token=" + token + "&docid=" + doctor.getId());
        startActivity(intent);
    }

    @Override
    public void ask(String id, String cost) {
        Intent intent = new Intent(ZYZJActivity.this, ZXWZ_ZJActivity.class);
        intent.putExtra("doctorId", id);
        intent.putExtra("cost", cost);
        startActivity(intent);
    }

    @Override
    public void fellow(boolean type, String id) {
        if (type) {
            fellowData(id);
        } else {
            cancelFellowData(id);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                ZYZJActivity.this.finish();
            }
            break;
            case R.id.fellow_video: {
                page = 1;
                adapter.clear();
                baseUrl = NetConstant.BASE_URL+NetConstant.DOCTOR_INFO;
                isCity = false;
                getData(baseUrl,page, pageNumber);
                fellow_video_tv.setTextColor(getResources().getColor(R.color.yellow_color));
                fellow_video_line.setTextColor(getResources().getColor(R.color.yellow_color));
                fellow_expert_tv.setTextColor(getResources().getColor(R.color.black));
                fellow_expert_line.setTextColor(getResources().getColor(R.color.black));
                recyclerView.setVisibility(View.VISIBLE);
                type_all_rl.setVisibility(View.GONE);
                fellow_video_line.setVisibility(View.VISIBLE);
                fellow_expert_line.setVisibility(View.GONE);

            }
            break;
            case R.id.fellow_expert: {
                fellow_video_tv.setTextColor(getResources().getColor(R.color.black));
                fellow_video_line.setTextColor(getResources().getColor(R.color.black));
                fellow_expert_tv.setTextColor(getResources().getColor(R.color.yellow_color));
                fellow_expert_line.setTextColor(getResources().getColor(R.color.yellow_color));
                recyclerView.setVisibility(View.GONE);
                type_all_rl.setVisibility(View.VISIBLE);
                fellow_video_line.setVisibility(View.GONE);
                fellow_expert_line.setVisibility(View.VISIBLE);
                if (marks!=null&&marks.size()>0){

                }else{
                    //获取全部标签
                    getAllMarkData(3);
                }
            }
            break;
            case R.id.search_sure_tv: {
                page = 1;
                adapter.clear();
                baseUrl = NetConstant.BASE_URL+NetConstant.DOCTOR_INFO_QUERY;
                isCity = false;
                getData(baseUrl,page, pageNumber);
                recyclerView.setVisibility(View.VISIBLE);
                type_all_rl.setVisibility(View.GONE);
            }
            break;
        }
    }

    private void getAllMarkData(int type) {
        String token = "{" + (String) SharedPreferencesUtils.getParam("token", "") + "}";
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_LABLE;
        NetUtils.setCallback(markCallback);
        NetUtils.postJson(url, token);
    }

    //全部标签回调
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
                    marks = tempBean.getData();
                    //更新UI界面
                    handler.post(marksRunnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(ZYZJActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable marksRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (marks != null && marks.size() > 0) {
                recyclerView_all.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        typeAdapter.clear();
                        typeAdapter.addAll(marks);
                    }
                }, 1000);
            }
        }
    };

    @Override
    public void clickItem(String id, int type) {
        if (type == 1) {
            lables.add(id);
        } else {
            //这块存有bug
            if (lables.contains(id)) {
                lables.remove(id);
            }
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
                } else {
                    Looper.prepare();
                    Toast.makeText(ZYZJActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable fellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ZYZJActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
        }
    };

    private void cancelFellowData(String id) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");

        String url = NetConstant.BASE_URL + NetConstant.CANCEL_FELLOW + userid + "/" + id + "/" + token;
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
                } else {
                    Looper.prepare();
                    Toast.makeText(ZYZJActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable cancelFellowRunnableUi = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ZYZJActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeBgColor(View view, boolean isBack) {
        int defaultColor_start = 0xff818080;
        int defaultColor_end = 0xffffffff;
        int startColor = isBack ? defaultColor_start : defaultColor_end;//0xffff0000
        int endColor = isBack ? defaultColor_end : defaultColor_start;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ObjectAnimator anim = ObjectAnimator.ofArgb(view, "backgroundColor", startColor, endColor);
            anim.setDuration(200);
            anim.start();
        } else {
            view.setBackgroundColor(endColor);
        }
    }

    @Override
    public void onItemClick(String cateId, String subCateId) {
        isCity = true;
        recyclerView.setVisibility(View.VISIBLE);
        if (subCateId.equals("-1")){
            cityId = cateId;
            getData2(1,pageNumber,cateId);
        }else {
            cityId = subCateId;
            getData2(1,pageNumber,subCateId);
        }

        adapter.clear();
    }

    //获取医生列表
    private void getData2(int page, int pageNumber, String cityId) {
        String url;
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        //设置回调方法
        NetUtils.setCallback(doctorCallback);

        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setToken(token);
        doctorBean.setPageNum(page + "");
        doctorBean.setPageSize(pageNumber + "");
        url = NetConstant.BASE_URL + NetConstant.DOCTOR_INFO;
        doctorBean.setCityid(cityId);

        String json = JSON.toJSONString(doctorBean);

        //获取网络数据
        NetUtils.postJson(url, json);
    }
}
