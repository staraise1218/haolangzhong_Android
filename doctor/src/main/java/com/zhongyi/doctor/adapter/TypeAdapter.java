package com.zhongyi.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.doctor.R;
import com.zhongyi.doctor.bean.Mark;
import com.zhongyi.doctor.holder.TypeHolder;

import java.util.ArrayList;
import java.util.List;


public class TypeAdapter extends RecyclerArrayAdapter<Mark> {
    private Context context;
    private TypeHolder.onClick onClick;
    private String choosed_lables;

    public TypeAdapter(Context context, TypeHolder.onClick onClick,String choosed_lables) {
        super(context);
        this.context = context;
        this.onClick = onClick;
        this.choosed_lables = choosed_lables;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, null);
        TypeHolder holder = new TypeHolder(itemView,context,choosed_lables);
        holder.setOnClick(onClick);
        return holder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

