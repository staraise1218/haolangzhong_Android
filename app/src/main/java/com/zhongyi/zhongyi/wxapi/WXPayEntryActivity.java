package com.zhongyi.zhongyi.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhongyi.zhongyi.BaseActivity;
import com.zhongyi.zhongyi.GuaHaoActivity;
import com.zhongyi.zhongyi.HLZYYActivity;
import com.zhongyi.zhongyi.MineActivity;
import com.zhongyi.zhongyi.MineWebActivity;
import com.zhongyi.zhongyi.MyOrderWebActivity;
import com.zhongyi.zhongyi.PayFinishActivity;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, View.OnClickListener {
    private TextView submit;
    private ImageView title_back;
    private TextView title_content;
    private TextView title_right;

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
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

        title_back = findViewById(R.id.title_back);
        title_content = findViewById(R.id.title_content);
        title_back.setOnClickListener(this);
        title_right= findViewById(R.id.title_right);
        title_content.setText("支付成功");
        title_right.setVisibility(View.GONE);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        api = WXAPIFactory.createWXAPI(this, "wx4cac222a8fceba72");
        api.handleIntent(getIntent(), this);

        payType = (String) SharedPreferencesUtils.getParam("payType", "");
        token = (String) SharedPreferencesUtils.getParam("token", "");
        userid = (String) SharedPreferencesUtils.getParam("user_id", "");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back: {
                WXPayEntryActivity.this.finish();
            }
            break;
            case R.id.submit:{
                if (BUY_REGISTER.equals(payType)) {
                    Intent intent = new Intent(WXPayEntryActivity.this, GuaHaoActivity.class);
                    startActivity(intent);
                    WXPayEntryActivity.this.finish();
                } else if (BUY_GOODS.equals(payType)) {
                    String token = (String) SharedPreferencesUtils.getParam("token", "");
                    String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
                    Intent intent = new Intent(WXPayEntryActivity.this, MyOrderWebActivity.class);
                    intent.putExtra("url", NetConstant.MY_ORDER + "?token=" + token + "&userid=" + userid);
                    intent.putExtra("title", "我的订单");
                    startActivity(intent);
                    WXPayEntryActivity.this.finish();
                }else if (BUY_SERVICE.equals(payType)){
                    Intent intent = new Intent(WXPayEntryActivity.this, MineWebActivity.class);
                    intent.putExtra("url", NetConstant.TIAOLI + "?token=" + token + "&userid=" + userid);
                    intent.putExtra("title", "我的调理");
                    startActivity(intent);
                    WXPayEntryActivity.this.finish();
                }else if (BUY_MEMBER.equals(payType)){
                    //购买成功后，跳转到我的页面
                    Intent intent = new Intent(WXPayEntryActivity.this, MineActivity.class);
                    startActivity(intent);
                    WXPayEntryActivity.this.finish();
                }else{
                    WXPayEntryActivity.this.finish();
                }
            }
        }
    }
}
