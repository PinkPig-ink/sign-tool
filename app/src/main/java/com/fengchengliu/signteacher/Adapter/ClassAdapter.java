package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.fengchengliu.signteacher.HomeActivity;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.entity.Classes;

import java.util.List;

public class ClassAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    List<Classes> list;
    private OnItemDeleteListener listener = null;
    private Context mContext;
    public ClassAdapter(Context context, List<Classes> list){
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassItemVH holder;
        if(convertView==null){
            holder = new ClassItemVH();
            convertView = inflater.inflate(R.layout.item,null);
            holder.classCode = convertView.findViewById(R.id.classCode);
            holder.className = convertView.findViewById(R.id.className);
            holder.classNumber = convertView.findViewById(R.id.classNumber);
            holder.classDelete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        }else{
            holder = (ClassItemVH)convertView.getTag();
        }
        // 数据源的赋值
        holder.className.setText(list.get(position).getClassName());
        holder.classCode.setText(list.get(position).getClassKey());
        holder.classNumber.setText(Integer.toString(list.get(position).getClassNumber()));
        holder.classDelete.setTag(position);
        holder.classDelete.setOnClickListener(v->{
            if (listener != null) {
                // dismiss the class by its classKey
                listener.onDelete(list.get(position).getClassKey());
            }
            Toast.makeText(mContext,"点击了删除",Toast.LENGTH_SHORT).show();
        });
        // Log.d("test" , holder.toString());

        return convertView;
    }
    public interface OnItemDeleteListener {
        void onDelete(String classKey);
    }

    public void OnItemDeleteListener (OnItemDeleteListener listener) {
        this.listener = listener;
    }

}
