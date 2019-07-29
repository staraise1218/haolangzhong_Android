package com.zhongyi.zhongyi.holder;

import android.content.Context;
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
import com.zhongyi.zhongyi.bean.Mark;
import com.zhongyi.zhongyi.constant.NetConstant;

public class TypeHolder extends BaseViewHolder<Mark> {
    private Context context;
    TextView type_name;

    //判断是否点击
    //private boolean flag = true;
    private onClick onClick;


    public TypeHolder(View view,Context context) {
        super(view);
        this.context = context;
        type_name = (TextView) view.findViewById(R.id.type_name);
    }


    @Override
    public void setData(final Mark data) {
        super.setData(data);
        type_name.setText(data.getContent());
        type_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.isFlag()){
                    type_name.setBackgroundColor(context.getResources().getColor(R.color.line_color));
                    type_name.setTextColor(context.getResources().getColor(R.color.black));
                    onClick.clickItem(data.getId(),2);
                    //flag = true;
                    data.setFlag(false);

                }else{

                    type_name.setBackgroundColor(context.getResources().getColor(R.color.yellow_low));
                    type_name.setTextColor(context.getResources().getColor(R.color.yellow_color));
                    onClick.clickItem(data.getId(),1);
                    //flag = false;
                    data.setFlag(true);
                }
            }
        });

        if (data.isFlag()){
            type_name.setBackgroundColor(context.getResources().getColor(R.color.yellow_low));
            type_name.setTextColor(context.getResources().getColor(R.color.yellow_color));
        }else{
            type_name.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            type_name.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    public interface onClick{
        void clickItem(String id,int type);
    }

    public TypeHolder.onClick getOnClick() {
        return onClick;
    }

    public void setOnClick(TypeHolder.onClick onClick) {
        this.onClick = onClick;
    }
}