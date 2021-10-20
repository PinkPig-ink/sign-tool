package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.Object.Classes;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ClassAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    List<Classes> list;
    private final Context mContext;
    private final int type;

    public ClassAdapter(Context context, List<Classes> list, int type) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;
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
        if (convertView == null) {
            holder = new ClassItemVH();
            convertView = inflater.inflate(R.layout.item, null);
            holder.classCode = convertView.findViewById(R.id.classCode);
            holder.className = convertView.findViewById(R.id.className);
            holder.classNumber = convertView.findViewById(R.id.classNumber);
            holder.classMenu = convertView.findViewById(R.id.iv_class_menu);
            convertView.setTag(holder);
        } else {
            holder = (ClassItemVH) convertView.getTag();
        }
        // 数据源的赋值
        holder.className.setText(list.get(position).getClassName());
        holder.classCode.setText(list.get(position).getClassKey());
        holder.classNumber.setText(list.size() + "人");
        holder.classMenu.setTag(position);
        // 选项栏设置
        holder.classMenu.setOnClickListener(v -> {
            onBottomSheet();
        });
        return convertView;
    }

    public void onBottomSheet() {

        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        if (this.type == 1)
            dialog.setContentView(R.layout.bottom_sheet);
        else
            dialog.setContentView(R.layout.bottom_sheet_student);
        dialog.show();
    }





}
