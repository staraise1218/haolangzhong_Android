package com.zhongyi.zhongyi;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ZHFWSearchActivity extends BaseActivity implements View.OnClickListener {

    //搜索
    private TextView iv_search;
    private ImageView iv_back;
    private EditText edit_query;
    private ImageView iv_clear_search;

    //筛选
    private TextView huodong_tv;
    private TextView peixun_tv;
    private TextView shipin_tv;
    private TextView luntan_tv;
    private TextView shudichou_tv;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zhfw_search);

        //搜索
        iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        edit_query = findViewById(R.id.edit_query);
        iv_clear_search = findViewById(R.id.iv_clear_search);
        iv_clear_search.setOnClickListener(this);

        huodong_tv = findViewById(R.id.huodong_tv);
        peixun_tv = findViewById(R.id.peixun_tv);
        shipin_tv = findViewById(R.id.shipin_tv);
        luntan_tv = findViewById(R.id.luntan_tv);
        shudichou_tv = findViewById(R.id.shudichou_tv);
        huodong_tv.setOnClickListener(this);
        peixun_tv.setOnClickListener(this);
        shipin_tv.setOnClickListener(this);
        luntan_tv.setOnClickListener(this);
        shudichou_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_search: {
                edit_query.setText("");
            }
            break;
            case R.id.iv_search: {
                String keyword = edit_query.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    //进行跳转

                } else {
                    Toast.makeText(ZHFWSearchActivity.this, "请输入搜索内容", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.huodong_tv:{
                Intent intent = new Intent(ZHFWSearchActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
            }
                break;
            case R.id.peixun_tv:{
                Intent intent = new Intent(ZHFWSearchActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
            }
            break;
            case R.id.shipin_tv:{
                Intent intent = new Intent(ZHFWSearchActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","3");
                startActivity(intent);
            }
            break;
            case R.id.luntan_tv:{
                Intent intent = new Intent(ZHFWSearchActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","4");
                startActivity(intent);
            }
            break;
            case R.id.shudichou_tv:{
                Intent intent = new Intent(ZHFWSearchActivity.this,ZHFWSearchDetailActivity.class);
                intent.putExtra("type","5");
                startActivity(intent);
            }
            break;
        }
    }
}
