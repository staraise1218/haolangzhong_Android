package com.zhongyi.doctor;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.zhongyi.doctor.adapter.TypeAdapter;
import com.zhongyi.doctor.bean.Mark;
import com.zhongyi.doctor.bean.temp.MarkTemp;
import com.zhongyi.doctor.constant.LogUtils;
import com.zhongyi.doctor.constant.NetCode;
import com.zhongyi.doctor.constant.NetConstant;
import com.zhongyi.doctor.holder.TypeHolder;
import com.zhongyi.doctor.utils.NetUtils;
import com.zhongyi.doctor.utils.SharedPreferencesUtils;
import com.zhongyi.doctor.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SkillActivity extends BaseActivity implements TypeHolder.onClick, View.OnClickListener {
    private ImageView title_back;
    private TextView title_content;
    private ImageView title_share;

    private EasyRecyclerView recyclerView_all;
    private EasyRecyclerView recyclerView_shoufa;
    private TextView submit;


    private List<Mark> marks;
    private List<Mark> marks_shoufa;
    private Handler handler = new Handler();
    private TypeAdapter typeAdapter;
    private TypeAdapter typeAdapter_shoufa;
    private List<String> lables = new ArrayList<>();
    private List<String> lables_content = new ArrayList<>();
    private String choosed_lables;
    private String choosed_content;
    @Override
    protected void setView() {
        setContentView(R.layout.activity_skill);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_content.setText("技能分类");
        title_back.setOnClickListener(this);
        title_share = findViewById(R.id.title_other);
        title_share.setVisibility(View.GONE);

        choosed_lables = getIntent().getStringExtra("marks");
        choosed_lables = choosed_lables.substring(1,choosed_lables.length()-1);
        if (!choosed_lables.equals("")) {
            String[] array = choosed_lables.split(",");
            for (String str : array) {
                str = str.trim();
                lables.add(str);
            }
        }

        choosed_content = getIntent().getStringExtra("marks_content");
        choosed_content = choosed_content.substring(1,choosed_content.length()-1);
        if (!choosed_content.equals("")) {
            String[] array = choosed_content.split(",");
            for (String str : array) {
                str = str.trim();
                lables_content.add(str);
            }
        }

        //全部标签
        recyclerView_all = findViewById(R.id.recyclerView);
        typeAdapter = new TypeAdapter(this,this,choosed_lables);
        typeAdapter.setHasStableIds(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(typeAdapter.obtainGridSpanSizeLookUp(2));
        recyclerView_all.setLayoutManager(gridLayoutManager);
        recyclerView_all.setAdapter(typeAdapter);

        SpaceDecoration itemDecoration_all = new SpaceDecoration((int) Utils.convertDpToPixel(8, SkillActivity.this));
        recyclerView_all.addItemDecoration(itemDecoration_all);


        //全部标签
        recyclerView_shoufa = findViewById(R.id.recyclerView_shoufa);
        typeAdapter_shoufa = new TypeAdapter(this,this,choosed_lables);
        typeAdapter_shoufa.setHasStableIds(true);
        GridLayoutManager gridLayoutManager_shoufa = new GridLayoutManager(this, 2);
        gridLayoutManager_shoufa.setSpanSizeLookup(typeAdapter_shoufa.obtainGridSpanSizeLookUp(2));
        recyclerView_shoufa.setLayoutManager(gridLayoutManager_shoufa);
        recyclerView_shoufa.setAdapter(typeAdapter_shoufa);

        SpaceDecoration itemDecoration_shoufa = new SpaceDecoration((int) Utils.convertDpToPixel(8, SkillActivity.this));
        recyclerView_shoufa.addItemDecoration(itemDecoration_shoufa);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);


        //获取身体、疗法全部标签
        getbodyMarkData(1);
        getshoufaMarkData(2);
    }

    private void getbodyMarkData(int type) {
        String token = "{"+(String) SharedPreferencesUtils.getParam("token","")+"}";
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_LABLE;
        NetUtils.setCallback(markCallback);
        NetUtils.postJson(url,token);
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
                    Toast.makeText(SkillActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable marksRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (marks !=null&& marks.size()>0){
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



    private void getshoufaMarkData(int type) {
        String url = NetConstant.BASE_URL + NetConstant.DOCTER_MARK+type;
        NetUtils.setCallback(shoufaCallback);
        NetUtils.getDataAsync(url);
    }

    //全部标签回调
    Callback shoufaCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                MarkTemp tempBean = JSON.parseObject(response.body().string(), MarkTemp.class);
                if (tempBean.getCode() == 0) {
                    marks_shoufa = tempBean.getData();
                    //更新UI界面
                    handler.post(shoufaRunnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(SkillActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable shoufaRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (marks_shoufa !=null&& marks_shoufa.size()>0){
                recyclerView_shoufa.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        typeAdapter_shoufa.clear();
                        typeAdapter_shoufa.addAll(marks_shoufa);
                    }
                }, 1000);
            }
        }
    };



    @Override
    public void clickItem(String id, int type,String content) {
        if (type==1) {
            lables.add(id);
            lables_content.add(content);
        }else{
            lables.remove(id);
            lables_content.remove(content);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit: {
                Intent intent = new Intent();
                intent.putExtra("marks", lables.toArray(new String[lables.size()]));
                intent.putExtra("marks_content",lables_content.toArray(new String[lables_content.size()]));
                SkillActivity.this.setResult(RESULT_OK, intent);
                SkillActivity.this.finish();
            }
            break;
            case R.id.title_back: {
                SkillActivity.this.finish();
            }
            break;
        }

    }
}
