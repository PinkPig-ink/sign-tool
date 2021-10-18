package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.StudentItemVH;
import com.fengchengliu.signteacher.entity.User;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    List<User> listItem;

    public StudentAdapter(Context context, List<User> listItem){
        this.inflater = LayoutInflater.from(context);
        this.listItem = listItem;

    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StudentItemVH holder;
        if(convertView==null){
            holder = new StudentItemVH();
            convertView = inflater.inflate(R.layout.item_student,null);
            holder.studentName =  (TextView) convertView.findViewById(R.id.studentName);
            holder.signStatus = (TextView)convertView.findViewById(R.id.signStatus);

            convertView.setTag(holder);
        }else{
            holder = (StudentItemVH)convertView.getTag();
        }
        if(listItem==null||listItem.get(position)==null)
            Log.d("StudentAdapter","null");
        holder.studentName.setText((String)listItem.get(position).getUserName());
        holder.studentAccount.setText(String.valueOf(listItem.get(position).getUserAccount()));
        holder.signStatus.setText("未签到");

        return convertView;
    }
}
