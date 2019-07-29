package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Artical;
import com.zhongyi.zhongyi.bean.Doctor;
import com.zhongyi.zhongyi.holder.ZHFWHolder;
import com.zhongyi.zhongyi.holder.ZYZJHolder;


public class ZHFWAdapter extends RecyclerArrayAdapter<Artical> {


    public ZHFWAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artical, null);
        ZHFWHolder holder = new ZHFWHolder(itemView);
        return holder;
    }
}

