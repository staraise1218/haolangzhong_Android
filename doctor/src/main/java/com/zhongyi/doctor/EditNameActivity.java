package com.zhongyi.doctor;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditNameActivity extends BaseActivity implements View.OnClickListener {
    private ImageView title_lift;
    private ImageView title_right;
    private TextView title;
    private ImageView title_share;

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
        title.setText("姓名编辑");
        title_share = findViewById(R.id.title_other);
        title_share.setOnClickListener(this);
        title_share.setVisibility(View.GONE);

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
