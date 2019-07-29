package com.zhongyi.zhongyi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhongyi.zhongyi.bean.Member;
import com.zhongyi.zhongyi.bean.ask.MemberBean;
import com.zhongyi.zhongyi.bean.temp.MemberTemp;
import com.zhongyi.zhongyi.constant.LogUtils;
import com.zhongyi.zhongyi.constant.NetCode;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;
import com.zhongyi.zhongyi.control.RewritePopwindow;
import com.zhongyi.zhongyi.utils.NetUtils;
import com.zhongyi.zhongyi.utils.SharedPreferencesUtils;

import java.io.IOException;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineActivity extends BaseActivity implements View.OnClickListener {

    //底部tab
    private LinearLayout home_ll;
    private LinearLayout ask_ll;
    private LinearLayout mine_ll;
    private ImageView home_img;
    private TextView home_name;
    private ImageView ask_img;
    private TextView ask_name;
    private ImageView mine_img;
    private TextView mine_name;
    private TextView member_tv;

    //title
    private ImageView title_back;
    private TextView title_content;
    private ImageView title_share;

    //横向点击
    private RelativeLayout mine_address_rl;
    private RelativeLayout mine_order_rl;
    private RelativeLayout mine_drug_rl;
    private RelativeLayout mine_money_rl;
    private RelativeLayout mine_wenzhen;
    private RelativeLayout mine_tiaoli;
    private RelativeLayout mine_feedback;
    private RelativeLayout mine_baoutus;
    private RelativeLayout mine_setting_rl;
    private RelativeLayout mine_guahao;

    //关注和收藏
    private LinearLayout mine_guanzhu_ll;
    private LinearLayout mine_shoucang_ll;

    //用户信息
    private RelativeLayout user_rl;
    private ImageView user_img;
    private TextView user_name;

    private RewritePopwindow mPopwindow;

    //是否是会员
    private Member member;
    private boolean isMember = false;
    private Handler handler = new Handler();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_mine);

        //底部tab
        home_ll = findViewById(R.id.home_ll);
        ask_ll = findViewById(R.id.ask_ll);
        mine_ll = findViewById(R.id.mine_ll);
        home_ll.setOnClickListener(this);
        ask_ll.setOnClickListener(this);
        mine_ll.setOnClickListener(this);
        home_img = findViewById(R.id.home_img);
        home_name = findViewById(R.id.home_name);
        ask_img = findViewById(R.id.ask_img);
        ask_name = findViewById(R.id.ask_name);
        mine_img = findViewById(R.id.mine_img);
        mine_name = findViewById(R.id.mine_name);
        member_tv = findViewById(R.id.member_tv);
        member_tv.setOnClickListener(this);

        //标题
        title_back = findViewById(R.id.title_back);
        title_back.setVisibility(View.GONE);
        title_content = findViewById(R.id.title_content);
        title_content.setText("我的");
        title_share = findViewById(R.id.title_other);
        title_share.setVisibility(View.VISIBLE);
        title_share.setOnClickListener(this);

        //横向点击
        mine_address_rl = findViewById(R.id.mine_address_rl);
        mine_order_rl = findViewById(R.id.mine_order_rl);
        mine_drug_rl = findViewById(R.id.mine_drug_rl);
        mine_money_rl = findViewById(R.id.mine_money_rl);
        mine_wenzhen = findViewById(R.id.mine_wenzhen);
        mine_feedback = findViewById(R.id.mine_feedback);
        mine_baoutus = findViewById(R.id.mine_baoutus);
        mine_setting_rl = findViewById(R.id.mine_setting_rl);
        mine_tiaoli = findViewById(R.id.mine_tiaoli);
        mine_guahao = findViewById(R.id.mine_guahao);

        mine_address_rl.setOnClickListener(this);
        mine_order_rl.setOnClickListener(this);
        mine_drug_rl.setOnClickListener(this);
        mine_money_rl.setOnClickListener(this);
        mine_wenzhen.setOnClickListener(this);
        mine_feedback.setOnClickListener(this);
        mine_baoutus.setOnClickListener(this);
        mine_setting_rl.setOnClickListener(this);
        mine_tiaoli.setOnClickListener(this);
        mine_guahao.setOnClickListener(this);

        //收藏
        mine_guanzhu_ll = findViewById(R.id.mine_guanzhu_ll);
        mine_shoucang_ll = findViewById(R.id.mine_shoucang_ll);
        mine_guanzhu_ll.setOnClickListener(this);
        mine_shoucang_ll.setOnClickListener(this);

        //用户信息
        user_rl = findViewById(R.id.user_rl);
        user_rl.setOnClickListener(this);
        user_img = findViewById(R.id.user_img);
        user_name = findViewById(R.id.user_name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");

        String icon = (String) SharedPreferencesUtils.getParam("icon", "");
        String nick_name = (String) SharedPreferencesUtils.getParam("nike_name", "");
        String phone = (String) SharedPreferencesUtils.getParam("phone", "");
        if (icon != null) {
            Glide.with(this).load(NetConstant.BASE_IMGE_URL + icon).transform(new GlideCircleTransform(MineActivity.this)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    user_img.setImageDrawable(resource); //显示图片
                }
            });
        }
        if (nick_name != null) {
            user_name.setText(nick_name);
        } else {
            user_name.setText(phone);
        }
        //查看会员
        getMemberData(token,userid);
    }

    @Override
    public void onClick(View view) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
        switch (view.getId()) {
            case R.id.home_ll: {
                //跳转到首页
                Intent intent = new Intent(MineActivity.this, HomeActivity.class);
                startActivity(intent);
                MineActivity.this.finish();
            }
            break;
            case R.id.ask_ll: {
                if (isMember) {
                    Intent intent = new Intent(MineActivity.this, ZXWZHomeActivity.class);
                    startActivity(intent);
                    MineActivity.this.finish();
                }else{
                    showMemberDialog();
                }
            }
            break;
            case R.id.mine_address_rl: {
                Intent intent = new Intent(MineActivity.this, MyOrderWebActivity.class);
                intent.putExtra("url", NetConstant.ADDRESS + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的地址");
                startActivity(intent);
            }
            break;
            case R.id.mine_order_rl: {
                Intent intent = new Intent(MineActivity.this, MyOrderWebActivity.class);
                intent.putExtra("url", NetConstant.MY_ORDER + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的订单");
                startActivity(intent);
            }
            break;
            case R.id.mine_drug_rl: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.MY_DRUG + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的药方");
                startActivity(intent);
            }
            break;
            case R.id.mine_money_rl: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.MY_MONEY + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的红包");
                startActivity(intent);
            }
            break;
            case R.id.mine_wenzhen: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.MY_WENZHEN + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的问诊");
                startActivity(intent);
            }
            break;
            case R.id.mine_tiaoli: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.TIAOLI + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的调理");
                startActivity(intent);
            }
            break;
            case R.id.mine_guahao: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.GUA_HAO_MY + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的挂号");
                startActivity(intent);
            }
            break;
            case R.id.mine_feedback: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.FEEDBACK + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "意见反馈");
                startActivity(intent);
            }
            break;
            case R.id.mine_baoutus: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.ABOUT_US + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "关于我们");
                startActivity(intent);
            }
            break;
            case R.id.mine_setting_rl: {
                Intent intent = new Intent(MineActivity.this, SettingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.mine_guanzhu_ll: {
                Intent intent = new Intent(MineActivity.this, FellowActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.mine_shoucang_ll: {
                Intent intent = new Intent(MineActivity.this, MineWebActivity.class);
                intent.putExtra("url", NetConstant.MY_SHOUCANG + "?token=" + token + "&userid=" + userid);
                intent.putExtra("title", "我的收藏");
                startActivity(intent);
            }
            break;
            case R.id.user_rl: {
                Intent intent = new Intent(MineActivity.this, UserActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.title_other: {
                mPopwindow = new RewritePopwindow(MineActivity.this, itemsOnClick);
                mPopwindow.showAtLocation(view,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
            break;
            case R.id.member_tv:{
                if (!isMember){
                    Intent intent = new Intent(MineActivity.this,ShangChengWebActivity.class);
                    intent.putExtra("url",NetConstant.MEMBER_BUY);
                    startActivity(intent);
                }else{
                    Toast.makeText(MineActivity.this,"您已是会员",Toast.LENGTH_LONG).show();
                }
            }
                break;
        }
    }

    //获取医生列表
    private void getMemberData(String token,String userId) {
        String url = NetConstant.BASE_URL2+NetConstant.MEMBER;
        //设置回调方法
        NetUtils.setCallback(memberCallback);

        MemberBean memberBean = new MemberBean();
        memberBean.setToken(token);
        memberBean.setUserId(userId);
        String json = JSON.toJSONString(memberBean);

        //获取网络数据
        NetUtils.postJson(url, json);
    }

    //登录回调
    Callback memberCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                MemberTemp tempBean = JSON.parseObject(response.body().string(), MemberTemp.class);
                if (tempBean.getCode() == 0) {
                    member = tempBean.getData();
                    //更新UI界面
                    handler.post(memberRunnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(MineActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable memberRunnableUi = new Runnable() {
        @Override
        public void run() {
            if (member!=null){
                if (member.getCode()==200102){
                    isMember = true;
                    member_tv.setText("会员");
                }else{
                    isMember = false;
                }
            }
        }
    };

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.backgroundAlpha(MineActivity.this, 1f);
            switch (v.getId()) {
                case R.id.weixinghaoyou:
                    showShare(Wechat.NAME);
                    break;
                case R.id.pengyouquan:
                    showShare(WechatMoments.NAME);
                    break;
                default:
                    break;
            }
        }
    };

    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        String titile = (String) SharedPreferencesUtils.getParam("title","");
        oks.setTitle(titile);
        String picurl = (String) SharedPreferencesUtils.getParam("picurl","");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        String shareurl = (String) SharedPreferencesUtils.getParam("shareurl","");
        String shareCode = (String) SharedPreferencesUtils.getParam("shareCode","");
        shareurl = shareurl+"?shareCode="+shareCode;
        oks.setTitleUrl(shareurl);
        // text是分享文本，所有平台都需要这个字段
        String content = (String) SharedPreferencesUtils.getParam("content","");
        oks.setText(content);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博

        oks.setImageUrl(picurl);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        //启动分享
        oks.show(this);
    }

    private void showMemberDialog(){
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(MineActivity.this);
        //    设置Title的图标
        builder.setIcon(R.mipmap.icon);
        //    设置Title的内容
        builder.setTitle("好郎中会员");
        //    设置Content来显示一个信息
        builder.setMessage("您不是会员，暂时无法访问");
        //    设置一个PositiveButton
        builder.setPositiveButton("查看会员", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(MineActivity.this,ShangChengWebActivity.class);
                intent.putExtra("url",NetConstant.MEMBER_BUY);
                startActivity(intent);
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Toast.makeText(HomeActivity.this, "negative: " + which, Toast.LENGTH_SHORT).show();
            }
        });
        //    显示出该对话框
        builder.show();

    }
}
