package com.zhongyi.zhongyi;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.zhongyi.adapter.ZHFWAdapter;
import com.zhongyi.zhongyi.adapter.ZYZJAdapter;
import com.zhongyi.zhongyi.bean.Artical;
import com.zhongyi.zhongyi.bean.ask.ArticalBean;
import com.zhongyi.zhongyi.bean.temp.ArticalData;
import com.zhongyi.zhongyi.bean.temp.ArticalDataTemp;
import com.zhongyi.zhongyi.bean.temp.HomeDataTemp;
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

public class ZHFWActivity extends BaseActivity implements View.OnClickListener, RecyclerArrayAdapter.OnItemClickListener {
    //点击菜单
    private LinearLayout huodong_ll;
    private LinearLayout peixun_ll;
    private LinearLayout luntan_ll;
    private LinearLayout shipin_ll;
    private LinearLayout shudichou_ll;

    //搜索框
    private TextView iv_search;
    private EditText edit_query;
    private ImageView iv_back;

    private EasyRecyclerView recyclerView;


    private Handler handler = new Handler();
    private ArticalData articalData;
    private ZHFWAdapter adapter;


    @Override
    protected void setView() {
        setContentView(R.layout.activity_zhfw);
        //点击菜单
        huodong_ll = findViewById(R.id.huodong_ll);
        peixun_ll = findViewById(R.id.peixun_ll);
        luntan_ll = findViewById(R.id.luntan_ll);
        shipin_ll = findViewById(R.id.shipin_ll);
        shudichou_ll = findViewById(R.id.shudichou_ll);

        huodong_ll.setOnClickListener(this);
        peixun_ll.setOnClickListener(this);
        luntan_ll.setOnClickListener(this);
        shipin_ll.setOnClickListener(this);
        shudichou_ll.setOnClickListener(this);

        //搜索框
        iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        edit_query = findViewById(R.id.edit_query);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        //医生列表
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ZHFWAdapter(this));

        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(4, ZHFWActivity.this));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(ZHFWActivity.this);

        //获取综合服务首页数据
        getData();
    }

    private void getData() {
        String url = NetConstant.BASE_URL + NetConstant.ARTICLE_INFO;
        ArticalBean articalBean = new ArticalBean();
        articalBean.setPageNum("1");
        articalBean.setPageSize("10");
        articalBean.setType("1");
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
                    Toast.makeText(ZHFWActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.huodong_ll:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
            }
                break;
            case R.id.peixun_ll:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
            }
            break;
            case R.id.luntan_ll:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","3");
                startActivity(intent);
            }
            break;
            case R.id.shipin_ll:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","4");
                startActivity(intent);
            }
            break;
            case R.id.shudichou_ll:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","5");
                startActivity(intent);
            }
            break;
            case R.id.iv_search:{
                Intent intent = new Intent(ZHFWActivity.this,ZHFWSearchDetailActivity.class);
                String content = edit_query.getText().toString().trim();
                intent.putExtra("title",content);
                startActivity(intent);
            }
            break;
            case R.id.iv_back: {
                Intent intent = new Intent(ZHFWActivity.this, HomeActivity.class);
                startActivity(intent);
                ZHFWActivity.this.finish();
            }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Artical artical_id = adapter.getItem(position);
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        Intent intent = new Intent(ZHFWActivity.this, ArticalWebActivity.class);
        intent.putExtra("url", NetConstant.ARTICLE_DETAILS+"?token="+token+"&id="+artical_id.getId());
        startActivity(intent);
    }
}
