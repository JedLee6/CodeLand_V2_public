package com.redant.codeland.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.entity.Coding;
import com.redant.codeland.entity.SavingRecord;

import java.util.List;

public class CodingAdapter extends ArrayAdapter<Coding> {
    private int resourceId;
    public CodingAdapter(Context context, int textViewResourceId, List<Coding> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Coding coding=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.recordName=view.findViewById(R.id.record_name);
            viewHolder.recordDate=view.findViewById(R.id.record_date);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.recordName.setText(coding.getName());
        viewHolder.recordDate.setText(coding.getDate());
        return view;
    }

    class ViewHolder{
        TextView recordName;
        TextView recordDate;
    }
}
