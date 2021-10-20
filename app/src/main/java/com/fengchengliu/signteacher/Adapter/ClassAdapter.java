package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.fengchengliu.signteacher.HomeActivity;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.entity.Classes;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;

import java.util.List;

public class ClassAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    List<Classes> list;
    private final Context mContext;

    public ClassAdapter(Context context, List<Classes> list) {
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
        holder.classNumber.setText(list.get(position).getClassNumber() + "人");
        holder.classMenu.setTag(position);
        // 选项栏设置
        holder.classMenu.setOnClickListener(v -> {
            // showPupMenu(mContext,holder.classMenu);
            onBottomSheet(holder.classMenu);
        });

        return convertView;
    }

    public void onBottomSheet(View view) {
        BottomSheetDialog dialog=new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.bottom_sheet);
        dialog.show();
    }
    // 弹出式菜单
    public void showPupMenu(Context context, View view){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.item_class_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String info = "";
            switch (item.getItemId()) {
                case R.id.action_startSign:
                    Log.d("itemMenu", "id: " + item.getItemId());
                    info = "开始签到..";

                    break;
                case R.id.action_showStudents:
                    Log.d("itemMenu", "id: " + item.getItemId());
                    info = "显示学生";
                    break;
                case R.id.action_dismissClass:
                    Log.d("itemMenu", "id: " + item.getItemId());
                    info = "解散班级";
                    break;
            }
            Toast.makeText(mContext, info,
                    Toast.LENGTH_LONG).show();
            return true;
        });
        popupMenu.show();
    }

}
