package com.redant.codeland.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.redant.codeland.R;
import com.redant.codeland.entity.SavingRecord;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<SavingRecord> {
    private int resourceId;
    public RecordAdapter(Context context, int textViewResourceId, List<SavingRecord> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SavingRecord savingRecord=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.recordName=(TextView)view.findViewById(R.id.record_name);
            viewHolder.recordDate=(TextView)view.findViewById(R.id.record_date);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.recordName.setText(savingRecord.getSavingName());
        viewHolder.recordDate.setText(savingRecord.getSavingDate());
        return view;
    }


//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        SavingRecord savingRecord=getItem(position);
//        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//        TextView recordName=(TextView)view.findViewById(R.id.record_name);
//        TextView recordDate=(TextView)view.findViewById(R.id.record_date);
//        recordName.setText(savingRecord.getSavingName());
//        recordDate.setText(savingRecord.getSavingDate());
//        return view;
//    }

    class ViewHolder{
        TextView recordName;
        TextView recordDate;
    }
}
