package com.zhongyi.zhongyi.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.constant.NetConstant;

public class ZHFWolder extends BaseViewHolder<Doctor> {
    ImageView artical_img;
    TextView artical_name;
    TextView artical_time;

    public ZHFWolder(View view) {
        super(view);
        artical_img = (ImageView) view.findViewById(R.id.artical_img);
        artical_name = (TextView) view.findViewById(R.id.artical_name);
        artical_time = (TextView) view.findViewById(R.id.artical_time);
    }


    @Override
    public void setData(final Doctor data) {
        super.setData(data);

        Glide.with(getContext()).load(NetConstant.BASE_IMGE_URL+data.getIcon()).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                artical_img.setImageDrawable(resource); //显示图片
            }
        });
        artical_name.setText(data.getName());
        //zj_level.setText(data.getLabel());
        artical_time.setText(data.getTimes());

    }
}