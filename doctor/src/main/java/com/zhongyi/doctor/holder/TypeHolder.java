package com.zhongyi.doctor.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.zhongyi.doctor.R;
import com.zhongyi.doctor.bean.Mark;

import java.util.List;

public class TypeHolder extends BaseViewHolder<Mark> {
    private Context context;
    TextView type_name;

    //判断是否点击
    private onClick onClick;
    private String choosed_lables;


    public TypeHolder(View view, Context context, String choosed_lables) {
        super(view);
        this.context = context;
        this.choosed_lables = choosed_lables;
        type_name = (TextView) view.findViewById(R.id.type_name);
    }


    @Override
    public void setData(final Mark data) {
        super.setData(data);
        type_name.setText(data.getContent());
        if (choosed_lables!=null&&choosed_lables.contains(data.getId())&&!data.isFlag()){
            data.setFlag(true);
        }

        if (data.isFlag()){
            type_name.setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
        }else{
            type_name.setBackgroundColor(context.getResources().getColor(R.color.line_color));
        }

        type_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.isFlag()){
                    type_name.setBackgroundColor(context.getResources().getColor(R.color.line_color));
                    onClick.clickItem(data.getId(),2,data.getContent());
                    data.setFlag(false);
                }else{
                    type_name.setBackgroundColor(context.getResources().getColor(R.color.yellow_color));
                    onClick.clickItem(data.getId(),1,data.getContent());
                    data.setFlag(true);
                }
            }
        });
    }

    public interface onClick{
        void clickItem(String id, int type,String content);
    }

    public TypeHolder.onClick getOnClick() {
        return onClick;
    }

    public void setOnClick(TypeHolder.onClick onClick) {
        this.onClick = onClick;
    }
}