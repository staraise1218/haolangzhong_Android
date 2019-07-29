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
import com.zhongyi.zhongyi.bean.Artical;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.constant.NetConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZHFWHolder extends BaseViewHolder<Artical> {
    ImageView artical_img;
    TextView artical_name;
    TextView artical_time;


    public ZHFWHolder(View view) {
        super(view);
        artical_img = (ImageView) view.findViewById(R.id.artical_img);
        artical_name = (TextView) view.findViewById(R.id.artical_name);
        artical_time = (TextView) view.findViewById(R.id.artical_time);
    }


    @Override
    public void setData(final Artical data) {
        super.setData(data);

        Glide.with(getContext()).load(NetConstant.BASE_IMGE_URL+data.getPic()).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                artical_img.setImageDrawable(resource); //显示图片
            }
        });
        artical_name.setText(data.getTitle());
        String format = "yyyy-MM-dd";//日期格式
        String curDate= "2018-05-22";//当前日期

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(data.getCreate_date());
                artical_time.setText(date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}