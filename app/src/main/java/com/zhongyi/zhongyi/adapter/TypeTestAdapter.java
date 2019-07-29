package com.zhongyi.zhongyi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhongyi.zhongyi.R;
import com.zhongyi.zhongyi.bean.Mark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeTestAdapter extends RecyclerView.Adapter<TypeTestAdapter.TypeTestHolder> {

    private Context context;
    private List<Mark>  marks;
    private Map<Integer, Boolean> map = new HashMap<>();

    public TypeTestAdapter(Context context,List<Mark> marks) {
        this.marks = marks;
        this.context = context;
        initMap();
    }

    private void initMap() {
        for (int i = 0; i < marks.size(); i++) {
            map.put(i, true);
        }
    }

    @NonNull
    @Override
    public TypeTestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_type,parent,false);
        return new TypeTestHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TypeTestHolder holder, int position) {
        holder.type_name.setText(marks.get(position).getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (flag){
//                    type_name.setBackgroundColor(context.getResources().getColor(R.color.yellow_low));
//                    type_name.setTextColor(context.getResources().getColor(R.color.yellow_color));
//                    onClick.clickItem(data.getId(),1);
//                    flag = false;
//                }else{
//                    type_name.setBackgroundColor(context.getResources().getColor(R.color.line_color));
//                    type_name.setTextColor(context.getResources().getColor(R.color.black));
//                    onClick.clickItem(data.getId(),2);
//                    flag = true;
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TypeTestHolder extends RecyclerView.ViewHolder{

        //标签名称
        private TextView type_name;

        public TypeTestHolder(View itemView) {
            super(itemView);
            type_name = itemView.findViewById(R.id.type_name);
        }
    }
}
