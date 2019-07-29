package com.zhongyi.zhongyi.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.zhongyi.zhongyi.HomeActivity;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.constant.NetConstant;
import com.zhongyi.zhongyi.control.GlideCircleTransform;

public class ZYZJHolder extends BaseViewHolder<Doctor> {
    ImageView zj_img;
    TextView zj_name;
    TextView zj_level;
    TextView zj_price;
    TextView zj_content;
    TextView zj_memo;
    ImageView zj_fellow;
    ImageView zj_fellow_1;
    TextView zj_zx;
    TextView zj_tiaoli;
    TextView zj_jinqi;

    private AskListener askListener;
    private FellowListener fellowListener;


    public ZYZJHolder(View view) {
        super(view);
        zj_img = (ImageView) view.findViewById(R.id.zj_img);
        zj_name = (TextView) view.findViewById(R.id.zj_name);
        zj_level = (TextView) view.findViewById(R.id.zj_level);
        zj_price = (TextView) view.findViewById(R.id.zj_price);
        zj_content = (TextView) view.findViewById(R.id.zj_content);
        zj_memo = (TextView) view.findViewById(R.id.zj_memo);
        zj_fellow = view.findViewById(R.id.zj_fellow);
        zj_fellow_1 = view.findViewById(R.id.zj_fellow_1);
        zj_zx = view.findViewById(R.id.zj_zx);
        zj_tiaoli = view.findViewById(R.id.zj_tiaoli);
        zj_jinqi = view.findViewById(R.id.zj_jinqi);
    }


    @Override
    public void setData(final Doctor data) {
        super.setData(data);
        zj_img.setImageDrawable(getContext().getDrawable(R.mipmap.ic_launcher));
        Glide.with(getContext()).load(NetConstant.BASE_IMGE_URL + data.getIcon()).transform(new GlideCircleTransform(getContext())).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                zj_img.setImageDrawable(resource); //显示图片
            }
        });
        zj_name.setText(data.getName());
        zj_level.setText(data.getStarlv()+"星级");
        zj_price.setText("￥" + data.getCost());
        zj_content.setText(data.getAgenum()+"岁  "+data.getProfessional()+"  从医"+data.getWorkyear()+"年");
        zj_memo.setText("咨询次数：" + data.getTimes());
        zj_tiaoli.setText("调理次数：" + data.getTiaolicount());
        zj_jinqi.setText("锦旗数：" + data.getGifts());
        zj_fellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关注
                fellowListener.fellow(true, data.getId());
                zj_fellow_1.setVisibility(View.VISIBLE);
                zj_fellow.setVisibility(View.GONE);
            }

        });


        zj_fellow_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关注
                fellowListener.fellow(false, data.getId());
                zj_fellow.setVisibility(View.VISIBLE);
                zj_fellow_1.setVisibility(View.GONE);
            }

        });

        zj_zx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askListener.ask(data.getId(),data.getCost());
            }
        });

    }

    public interface AskListener {
        void ask(String id,String price);
    }

    public void setAskListener(AskListener askListener) {
        this.askListener = askListener;
    }

    public interface FellowListener {
        void fellow(boolean type, String id);
    }

    public void setFellowListener(FellowListener fellowListener) {
        this.fellowListener = fellowListener;
    }
}