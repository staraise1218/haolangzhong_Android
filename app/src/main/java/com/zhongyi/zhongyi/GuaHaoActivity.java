package com.zhongyi.zhongyi;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.zhongyi.bean.ask.DoctorBean;
import com.zhongyi.zhongyi.bean.ask.GuaHaoBean;
import com.zhongyi.zhongyi.bean.temp.DoctorTemp;
import com.zhongyi.zhongyi.bean.temp.GuaHao;
import com.zhongyi.zhongyi.bean.temp.GuaHaoTemp;
import com.zhongyi.zhongyi.bean.temp.GuaHaoTemp2;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.DateUtils;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GuaHaoActivity extends BaseActivity implements View.OnClickListener {
    private EditText your_disease;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText name_ev;
    private EditText phone_ev;
    private EditText age_ev;
    private ImageView title_back;
    private TextView title_content;

    private GuaHao guaHao;

    private Button submit;
    private Handler handler = new Handler();

    private String gender = "1";

    @Override
    protected void setView() {
        setContentView(R.layout.activity_gh);
        name_ev = findViewById(R.id.name_ev);
        phone_ev = findViewById(R.id.phone_ev);
        age_ev = findViewById(R.id.age_ev);
        datePicker = findViewById(R.id.dpPicker);
        timePicker  = findViewById(R.id.tpPicker);
        your_disease = findViewById(R.id.your_disease);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_content.setText("预约挂号");

        RadioGroup radgroup = (RadioGroup) findViewById(R.id.radioGroup);
        //第一种获得单选按钮值的方法
        //为radioGroup设置一个监听器:setOnCheckedChanged()
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
                if ("男".equals(radbtn.getText())){
                    gender = "1";
                }else{
                    gender = "2";
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                submitData();
                break;
            case R.id.title_back: {
                GuaHaoActivity.this.finish();
            }
            break;
        }
    }

    //获取医生列表
    private void submitData() {
        String url = NetConstant.BASE_URL2 + NetConstant.HOSPITAL_REGISTER;
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userId = (String) SharedPreferencesUtils.getParam("user_id","");
        String docid = (String) SharedPreferencesUtils.getParam("docid", "");
        //设置回调方法
        NetUtils.setCallback(ghCallback);
        String name = name_ev.getText().toString().trim();
        String phone = phone_ev.getText().toString().trim();
        String date = datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
        String time = (timePicker.getCurrentHour()>10?timePicker.getCurrentHour():"0"+timePicker.getCurrentHour())+":"+(timePicker.getCurrentMinute()>10?timePicker.getCurrentMinute():"0"+timePicker.getCurrentMinute())+":00";
        String info = your_disease.getText().toString().trim();
        String age = age_ev.getText().toString().trim();

        GuaHaoBean guaHaoBean = new GuaHaoBean();
        guaHaoBean.setName(name);
        guaHaoBean.setPhone(phone);
        guaHaoBean.setToken(token);
        guaHaoBean.setAge(age);
        guaHaoBean.setContent(info);
        guaHaoBean.setGenderType(gender);
        guaHaoBean.setGender(gender.equals("1")?"男":"女");
        guaHaoBean.setMakeDates(date+" "+time);
        guaHaoBean.setUserId(userId);
        guaHaoBean.setDoctorUserId(docid);
        String json = JSON.toJSONString(guaHaoBean);

        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback ghCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                GuaHaoTemp2 tempBean = JSON.parseObject(response.body().string(), GuaHaoTemp2.class);
                if (tempBean.getResult().getCode() == 0) {
                    guaHao = tempBean.getResult().getData();
                    //更新UI界面
                    handler.post(runnableUi);

                } else if (tempBean.getResult().getCode() == 400) {

                }
            }
        }
    };


    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (guaHao != null&&guaHao.getCode()==0) {
                Toast.makeText(GuaHaoActivity.this,"预约成功",Toast.LENGTH_LONG).show();
                GuaHaoActivity.this.finish();
            }else{
                Toast.makeText(GuaHaoActivity.this,"预约失败",Toast.LENGTH_LONG).show();
            }
        }
    };

}
