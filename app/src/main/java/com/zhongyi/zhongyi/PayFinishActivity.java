package com.zhongyi.zhongyi;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

public class PayFinishActivity extends BaseActivity implements View.OnClickListener {
    private TextView pay_way;
    private TextView submit;
    //购买商品
    private final String BUY_GOODS = "1";
    //购买挂号
    private final String BUY_REGISTER = "2";
    //购买调理
    private final String BUY_SERVICE = "3";
    //购买会员
    private final String BUY_MEMBER = "4";
    //购买会员
    private final String BUY_FLAG = "5";

    private String payType;
    private String token;
    private String userid;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_pay_finish);
        pay_way = findViewById(R.id.pay_way);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        String name = getIntent().getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            pay_way.setText(name);
        }

        payType = (String) SharedPreferencesUtils.getParam("payType", "");
        token = (String) SharedPreferencesUtils.getParam("token", "");
        userid = (String) SharedPreferencesUtils.getParam("user_id", "");
    }

    @Override
    public void onClick(View v) {
        if (BUY_REGISTER.equals(payType)) {
            Intent intent = new Intent(PayFinishActivity.this, GuaHaoActivity.class);
            startActivity(intent);
            PayFinishActivity.this.finish();
        } else if (BUY_GOODS.equals(payType)) {
            String token = (String) SharedPreferencesUtils.getParam("token", "");
            String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
            Intent intent = new Intent(PayFinishActivity.this, MyOrderWebActivity.class);
            intent.putExtra("url", NetConstant.MY_ORDER + "?token=" + token + "&userid=" + userid);
            intent.putExtra("title", "我的订单");
            startActivity(intent);
            PayFinishActivity.this.finish();
        }else if (BUY_SERVICE.equals(payType)){
            Intent intent = new Intent(PayFinishActivity.this, MineWebActivity.class);
            intent.putExtra("url", NetConstant.TIAOLI + "?token=" + token + "&userid=" + userid);
            intent.putExtra("title", "我的调理");
            startActivity(intent);
            PayFinishActivity.this.finish();
        }else if (BUY_MEMBER.equals(payType)){
            //购买成功后，跳转到我的页面
            Intent intent = new Intent(PayFinishActivity.this, MineActivity.class);
            startActivity(intent);
            PayFinishActivity.this.finish();
        }else{
            PayFinishActivity.this.finish();
        }
    }
}
