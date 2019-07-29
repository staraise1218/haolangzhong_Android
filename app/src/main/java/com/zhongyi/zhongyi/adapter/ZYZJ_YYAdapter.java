package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.holder.ZYZJ_YYHolder;


public class ZYZJ_YYAdapter extends RecyclerArrayAdapter<Doctor> {
    private ZYZJ_YYHolder.AskListener askListener;
    private ZYZJ_YYHolder.FellowListener fellowListener;

    public ZYZJ_YYAdapter(Context context, ZYZJ_YYHolder.AskListener askListener, ZYZJ_YYHolder.FellowListener fellowListener) {
        super(context);
        this.askListener = askListener;
        this.fellowListener = fellowListener;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yy, null);
        ZYZJ_YYHolder holder = new ZYZJ_YYHolder(itemView);
        holder.setAskListener(askListener);
        holder.setFellowListener(fellowListener);
        return holder;
    }
}

