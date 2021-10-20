package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.Object.Classes;

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
        ClassItemVH holder;
        if (convertView == null ) {
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
            showPupMenu(mContext,holder.classMenu,type);
        });
        return convertView;
    }
    // 弹出式菜单
    public void showPupMenu(Context context, View view, int type){
        PopupMenu popupMenu = new PopupMenu(context, view);
        if (type == 1)
            popupMenu.getMenuInflater().inflate(R.menu.item_teacher_menu, popupMenu.getMenu());
        else if (type == 0)
            popupMenu.getMenuInflater().inflate(R.menu.item_student_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String info = "";
            switch (item.getItemId()) {
                case R.id.item_sign:
                    startSign();
                    info = "发起签到..";
                    break;
                case R.id.item_show_stus:
                    info = "显示学生";
                    break;
                case R.id.item_dismiss:
                    info = "解散班级";
                    break;
                case R.id.item_stu_sign:
                    info = "学生签到";
                    break;
                case R.id.item_quit:
                    info = "学生退出班级";
                    break;
            }
            Toast.makeText(mContext, "信息"+info,
                    Toast.LENGTH_LONG).show();
            return true;
        });
        popupMenu.show();
    }

    private void startSign() {
        // 签到流程：生成签到码，把学生的签到位置零，在把签到码显示在顶部
        // 开始一个活动，显示这个班所有学生的签到情况
    }


}
