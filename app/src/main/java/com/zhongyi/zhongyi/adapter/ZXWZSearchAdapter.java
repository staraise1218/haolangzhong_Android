package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.holder.ZXWZSearchHolder;
import com.zhongyi.zhongyi.holder.ZYZJHolder;


public class ZXWZSearchAdapter extends RecyclerArrayAdapter<Doctor> {
    private ZXWZSearchHolder.AskListener askListener;
    private ZXWZSearchHolder.FellowListener fellowListener;

    public ZXWZSearchAdapter(Context context, ZXWZSearchHolder.AskListener askListener,ZXWZSearchHolder.FellowListener fellowListener) {
        super(context);
        this.askListener = askListener;
        this.fellowListener = fellowListener;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zxzj_search, null);
        ZXWZSearchHolder holder = new ZXWZSearchHolder(itemView);
        holder.setAskListener(askListener);
        holder.setFellowListener(fellowListener);
        return holder;
    }
}

