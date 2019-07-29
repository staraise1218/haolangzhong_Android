package com.zhongyi.doctor;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyi.doctor.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class Cate2Activity extends BaseActivity implements View.OnClickListener {
    private ImageView title_back;
    private TextView title_content;
    private ImageView title_share;

    private TextView shicheng_tv;
    private TextView zuchuan_tv;
    private TextView xueyuan_tv;
    private TextView submit;

    //默认为师承
    private String cate = "";
    private String content = "";

    private List<String> cates = new ArrayList<>();
    private List<String> contants = new ArrayList<>();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_cate_2);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_content.setText("分类信息");
        title_back.setOnClickListener(this);
        title_share = findViewById(R.id.title_other);
        title_share.setVisibility(View.GONE);


        shicheng_tv = findViewById(R.id.shicheng_tv);
        zuchuan_tv = findViewById(R.id.zuchuan_tv);
        xueyuan_tv = findViewById(R.id.xueyuan_tv);
        submit = findViewById(R.id.submit);

        shicheng_tv.setOnClickListener(this);
        zuchuan_tv.setOnClickListener(this);
        xueyuan_tv.setOnClickListener(this);
        submit.setOnClickListener(this);

        cate = (String) SharedPreferencesUtils.getParam("cate2", "");
        if (TextUtils.isEmpty(cate)) {
            cate = "1";
        } else {
            if (cate.contains("1")) {
                shicheng_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
            }
            if (cate.contains("2")) {
                zuchuan_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
            }
            if (cate.contains("3")){
                xueyuan_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
            }
        }

        if (!cate.equals("")) {
            String[] array = cate.split(",");
            for (String str : array) {
                str = str.trim();
                cates.add(str);
            }
            cate = "";
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back: {
                Cate2Activity.this.finish();
            }
            break;
            case R.id.shicheng_tv: {
                if (cates.contains("1")) {
                    cates.remove("1");
                    contants.remove("师承");
                    shicheng_tv.setBackgroundColor(getResources().getColor(R.color.line_color));
                } else {
                    cates.add("1");
                    contants.add("师承");
                    shicheng_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
                }
            }
            break;
            case R.id.zuchuan_tv: {
                if (cates.contains("2")) {
                    cates.remove("2");
                    contants.remove("祖传");
                    zuchuan_tv.setBackgroundColor(getResources().getColor(R.color.line_color));
                } else {
                    cates.add("2");
                    contants.add("祖传");
                    zuchuan_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
                }
            }
            break;
            case R.id.xueyuan_tv: {
                if (cates.contains("3")) {
                    cates.remove("3");
                    contants.remove("学院");
                    xueyuan_tv.setBackgroundColor(getResources().getColor(R.color.line_color));
                } else {
                    cates.add("3");
                    contants.add("学院");
                    xueyuan_tv.setBackgroundColor(getResources().getColor(R.color.yellow_color));
                }
            }
            break;
            case R.id.submit: {
                if (cates.size() > 0) {
                    for (String str : cates) {
                        if (!cate.contains(str)) {
                            cate = cate + "," + str;
                        }
                    }
                }
                if (contants.size() > 0) {
                    for (String str : contants) {
                        if (!content.contains(str)){
                            if (TextUtils.isEmpty(content)){
                                content = str;
                            }else{
                                content = content + ","+str;
                            }
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("cate2", cate);
                intent.putExtra("cate2_content", content);
                SharedPreferencesUtils.setParam("cate2", cate);
                Cate2Activity.this.setResult(RESULT_OK, intent);
                Cate2Activity.this.finish();
            }
            break;
        }
    }
}
