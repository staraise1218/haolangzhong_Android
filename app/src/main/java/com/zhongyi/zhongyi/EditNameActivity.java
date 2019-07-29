package com.zhongyi.zhongyi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditNameActivity extends BaseActivity implements View.OnClickListener {
    private ImageView title_lift;
    private ImageView title_right;
    private TextView title;

    private Button submit;
    private EditText editText;
    private String name = "";

    private Handler handler = new Handler();


    @Override
    protected void setView() {
        setContentView(R.layout.activity_nickname_edit);
        title_lift = findViewById(R.id.title_back);
        title_lift.setOnClickListener(this);
        title = findViewById(R.id.title_content);
        title.setText("个人信息");

        editText = findViewById(R.id.et_name);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
            {
                EditNameActivity.this.finish();
            }
                break;
            case R.id.submit:{
                name = editText.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("name",name);
                EditNameActivity.this.setResult(RESULT_OK,intent);
                EditNameActivity.this.finish();
            }
                break;
        }
    }
}
