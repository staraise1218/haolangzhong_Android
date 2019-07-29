package com.zhongyi.doctor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
import com.zhongyi.doctor.bean.DoctorPic;
import com.zhongyi.doctor.bean.Info;
import com.zhongyi.doctor.bean.temp.InfoTemp;
import com.zhongyi.doctor.constant.LogUtils;
import com.zhongyi.doctor.constant.NetCode;
import com.zhongyi.doctor.constant.NetConstant;
import com.zhongyi.doctor.utils.NetUtils;
import com.zhongyi.doctor.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineActivity extends BaseActivity implements View.OnClickListener {

    //底部tab
    private LinearLayout home_ll;
    private LinearLayout mine_ll;

    //title
    private ImageView title_back;
    private TextView title_content;
    private ImageView title_share;

    //横向点击
    private RelativeLayout mine_address_rl;
    private RelativeLayout mine_order_rl;
    private RelativeLayout mine_drug_rl;
    private RelativeLayout mine_feedback;
    private RelativeLayout mine_baoutus;
    private RelativeLayout mine_yuyue_rl;
    private RelativeLayout mine_order_all_rl;

    //用户信息
    private RelativeLayout user_rl;
    private ImageView user_img;
    private TextView user_name;

    private TextView submit;
    //获取用户信息
    private Info info;
    private Handler handler_info = new Handler();


    @Override
    protected void setView() {
        setContentView(R.layout.activity_mine);

        //底部tab
        home_ll = findViewById(R.id.home_ll);
        mine_ll = findViewById(R.id.mine_ll);
        home_ll.setOnClickListener(this);
        mine_ll.setOnClickListener(this);

        //标题
        title_back = findViewById(R.id.title_back);
        title_back.setVisibility(View.GONE);
        title_content = findViewById(R.id.title_content);
        title_content.setText("我的");
        title_share = findViewById(R.id.title_other);
        title_share.setOnClickListener(this);
//        title_share.setVisibility(View.GONE);

        //横向点击
        mine_address_rl = findViewById(R.id.mine_address_rl);
        mine_order_rl = findViewById(R.id.mine_order_rl);
        mine_drug_rl = findViewById(R.id.mine_drug_rl);
        mine_feedback = findViewById(R.id.mine_feedback);
        mine_baoutus = findViewById(R.id.mine_baoutus);
        mine_yuyue_rl = findViewById(R.id.mine_yuyue_rl);
        mine_order_all_rl = findViewById(R.id.mine_order_all_rl);

        mine_address_rl.setOnClickListener(this);
        mine_order_rl.setOnClickListener(this);
        mine_drug_rl.setOnClickListener(this);
        mine_feedback.setOnClickListener(this);
        mine_baoutus.setOnClickListener(this);
        mine_yuyue_rl.setOnClickListener(this);
        mine_order_all_rl.setOnClickListener(this);

        //用户信息
        user_rl = findViewById(R.id.user_rl);
        user_rl.setOnClickListener(this);
        user_img = findViewById(R.id.user_img);
        user_name = findViewById(R.id.user_name);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = (String) SharedPreferencesUtils.getParam("name","");
        if (!TextUtils.isEmpty(name)){
            user_name.setText(name);
        }

        String image = (String) SharedPreferencesUtils.getParam("icon","");
        if (!TextUtils.isEmpty(image)){
            Glide.with(this).load(NetConstant.BASE_IMGE_URL+image).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    user_img.setImageDrawable(resource); //显示图片
                }
            });
        }

        //获取用户信息
        String user_id = (String) SharedPreferencesUtils.getParam("user_id","");
        String token = (String) SharedPreferencesUtils.getParam("token","");
        getInfo(user_id,token);
    }

    @Override
    public void onClick(View view) {
        String token = (String) SharedPreferencesUtils.getParam("token", "");
        String userid = (String) SharedPreferencesUtils.getParam("user_id", "");
        String shareCode = (String) SharedPreferencesUtils.getParam("shareCode", "");
        switch (view.getId()) {
            case R.id.mine_order_all_rl: {
                //跳转到首页
                Intent intent = new Intent(MineActivity.this,MainActivity.class);
                intent.putExtra("url", NetConstant.ORDER + "?docid=" + userid);
                startActivity(intent);
            }
            break;
            case R.id.mine_address_rl:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.ORDER_ALL+"?docid="+userid);
                intent.putExtra("title","订单总数");
                startActivity(intent);
            }
                break;
            case R.id.mine_order_rl:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.MY_MONEY+"?token="+token+"&docid="+userid);
                intent.putExtra("title","我的钱包");
                startActivity(intent);
            }
            break;
            case R.id.mine_drug_rl:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.MY_DRUG+"?token="+token+"&docid="+userid);
                intent.putExtra("title","我的评价");
                startActivity(intent);
            }
            break;
            case R.id.mine_feedback:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.FEEDBACK+"?token="+token+"&userid="+userid);
                intent.putExtra("title","意见反馈");
                startActivity(intent);
            }
            break;
            case R.id.mine_baoutus:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.ABOUT_US+"?token="+token+"&userid="+userid);
                intent.putExtra("title","关于我们");
                startActivity(intent);
            }
            break;
            case R.id.submit:{
                showMyDialog();
            }
            break;
            case R.id.user_rl:{
                Intent intent = new Intent(MineActivity.this,UserInfoActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.title_other:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.SHARE+"?token="+token+"&userid="+userid+"&shareCode="+shareCode);
                intent.putExtra("title","分享推荐");
                startActivity(intent);
            }
            break;
            case R.id.mine_yuyue_rl:{
                Intent intent = new Intent(MineActivity.this,MineWebActivity.class);
                intent.putExtra("url",NetConstant.YUYUE+userid);
                intent.putExtra("title","挂我的号");
                startActivity(intent);
            }
            break;
        }
    }

    private void showMyDialog() {
        // 创建退出对话框
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("提示");
        // 设置对话框消息
        isExit.setMessage("确定要退出登录么？");
        // 添加选择按钮并注册监听
        isExit.setButton("确定", listener);
        isExit.setButton2("取消", listener);
        // 显示对话框
        isExit.show();
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    SharedPreferencesUtils.setParam("isLogin", false);
                    Intent intent = new Intent(MineActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MineActivity.this.finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    private void getInfo(String id,String token) {
        String url = NetConstant.BASE_URL + NetConstant.USER_INFO+id+"/"+token;
        NetUtils.setCallback(infoCallback);
        //获取网络数据
        NetUtils.getDataAsync(url);
    }

    //登录回调
    Callback infoCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                InfoTemp tempBean = JSON.parseObject(response.body().string(), InfoTemp.class);
                if (tempBean.getCode() == 0) {
                    info = tempBean.getData();
                    //更新UI界面
                    handler_info.post(runnableUi_info);
                } else {
                    Looper.prepare();
                    Toast.makeText(MineActivity.this, tempBean.msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi_info = new Runnable() {
        @Override
        public void run() {
            //服务配置
            if (info.getDoctorInfo().getCome_flag()!=null) {
                SharedPreferencesUtils.setParam("come_flag", info.getDoctorInfo().getCome_flag());
            }
            if (info.getDoctorInfo().getCome_cost()!=null) {
                SharedPreferencesUtils.setParam("come_cost", info.getDoctorInfo().getCome_cost());
            }
            //服务地址
            if (info.getDoctorInfo().getChange_address()!=null) {
                SharedPreferencesUtils.setParam("come_address", info.getDoctorInfo().getChange_address());
            }

            //荣誉证书
            if (info.getDoctorInfo().getChange_address()!=null) {
                SharedPreferencesUtils.setParam("introduce", info.getDoctorInfo().getIntroduce());
            }
            //标签
            if (info.getDoctorLabel()!=null&&info.getDoctorLabel().size()>0){
                String lables = "";
                String content = "";
                for (int i = 0;i< info.getDoctorLabel().size();i++){
                    if (i==0){
                        lables = info.getDoctorLabel().get(i).getId();
                        content = info.getDoctorLabel().get(i).getContent();
                    }else{
                        lables = lables+","+info.getDoctorLabel().get(i).getId();
                        content = content + ","+ info.getDoctorLabel().get(i).getContent();
                    }
                }
                SharedPreferencesUtils.setParam("lables", lables);
                SharedPreferencesUtils.setParam("content", content);
            }
            //分类
            if (info.getDoctorInfo().getClassify()!=null){
                SharedPreferencesUtils.setParam("cate2", info.getDoctorInfo().getClassify());
            }

            List<DoctorPic> doctorPic = info.getDoctorPic();
            if (doctorPic != null&&doctorPic.size()>0) {
                if (doctorPic.get(0).getIntroducepic1()!=null) {
                    SharedPreferencesUtils.setParam("introducepic1", doctorPic.get(0).getIntroducepic1());
                }

                if (doctorPic.get(0).getIntroducepic2()!=null) {
                    SharedPreferencesUtils.setParam("introducepic2", doctorPic.get(0).getIntroducepic2());
                }

                if (doctorPic.get(0).getIntroducepic3()!=null) {
                    SharedPreferencesUtils.setParam("introducepic3", doctorPic.get(0).getIntroducepic3());
                }


            }
        }
    };
}
