package com.zhongyi.zhongyi;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.zhongyi.adapter.ZHFWAdapter;
import com.zhongyi.zhongyi.adapter.ZXWZSearchAdapter;
import com.zhongyi.zhongyi.bean.Artical;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.ask.ArticalBean;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.OrderBean;
import com.zhongyi.zhongyi.bean.temp.ArticalData;
import com.zhongyi.zhongyi.bean.temp.ArticalDataTemp;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;
import com.zhongyi.zhongyi.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ZHFWSearchDetailActivity extends BaseActivity implements View.OnClickListener, RecyclerArrayAdapter.OnItemClickListener {


    private EasyRecyclerView recyclerView;
    private ZHFWAdapter adapter;
    private ImageView title_back;
    private TextView title_content;

    //跳转携带的数据
    private String typeId;
    private String content;

    private int page = 1;
    private final int pageNumber = 10;
    private Handler handler = new Handler();
    private ArticalData articalData;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxwz_search);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("文章列表");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ZHFWAdapter(this));

        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, ZHFWSearchDetailActivity.this));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(ZHFWSearchDetailActivity.this);

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
                articalData=null;
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
        typeId = getIntent().getStringExtra("type");
        content = getIntent().getStringExtra("title");
    }

    //获取医生列表
    private void getData(int page, int pageNumber) {
        String url = NetConstant.BASE_URL + NetConstant.ARTICLE_INFO;
        ArticalBean articalBean = new ArticalBean();
        articalBean.setPageNum(page+"");
        articalBean.setPageSize(pageNumber+"");
        articalBean.setType(typeId);
        articalBean.setTitle(content);
        NetUtils.setCallback(articalCallback);
        String json = JSON.toJSONString(articalBean);
        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback articalCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                ArticalDataTemp tempBean = JSON.parseObject(response.body().string(), ArticalDataTemp.class);
                if (tempBean.getCode() == 0) {
                    articalData = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(ZHFWSearchDetailActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (articalData != null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(articalData.getItems());
                    }
                }, 1000);
            }
        }
    };


    @Override
    public void onItemClick(int position) {
        Artical artical_id = adapter.getItem(position);
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        Intent intent = new Intent(ZHFWSearchDetailActivity.this, ArticalWebActivity.class);
        intent.putExtra("url", NetConstant.ARTICLE_DETAILS+"?token="+token+"&id="+artical_id.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                ZHFWSearchDetailActivity.this.finish();
            }
            break;
        }
    }
}
