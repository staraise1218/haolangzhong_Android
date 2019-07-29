package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.holder.ZYZJHolder;


public class FellowAdapter extends RecyclerArrayAdapter<Doctor> {
    private ZYZJHolder.AskListener askListener;
    private ZYZJHolder.FellowListener fellowListener;

    public FellowAdapter(Context context, ZYZJHolder.AskListener askListener, ZYZJHolder.FellowListener fellowListener) {
        super(context);
        this.askListener = askListener;
        this.fellowListener = fellowListener;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fellow, null);
        ZYZJHolder holder = new ZYZJHolder(itemView);
        holder.setAskListener(askListener);
        holder.setFellowListener(fellowListener);
        return holder;
    }
}

