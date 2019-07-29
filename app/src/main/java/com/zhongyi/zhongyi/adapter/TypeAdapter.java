package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.bean.Mark;
import com.zhongyi.zhongyi.holder.TypeHolder;
import com.zhongyi.zhongyi.holder.ZYZJHolder;

import java.util.Map;


public class TypeAdapter extends RecyclerArrayAdapter<Mark> {
    private Context context;
    private TypeHolder.onClick onClick;

    public TypeAdapter(Context context,TypeHolder.onClick onClick) {
        super(context);
        this.context = context;
        this.onClick = onClick;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent,false);
        TypeHolder holder = new TypeHolder(itemView,context);
        holder.setOnClick(onClick);
        return holder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

