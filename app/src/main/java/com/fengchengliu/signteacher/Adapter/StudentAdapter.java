package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.fengchengliu.signteacher.Object.Classes;
import com.fengchengliu.signteacher.Object.Student;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.ViewHolder.StudentItemVH;
import com.fengchengliu.signteacher.Object.User;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    List<Student> listItem;
    private Context mContext;
    private String classKey;
    public StudentAdapter(Context context, List<Student> listItem, String classKey){
        this.classKey = classKey;
        this.mContext = context;
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
            holder.studentName = convertView.findViewById(R.id.studentName);
            holder.studentAccount = convertView.findViewById(R.id.studentId);
            holder.signStatus = convertView.findViewById(R.id.signStatus);
            holder.signPosition = convertView.findViewById(R.id.signPosition);
            holder.menu = convertView.findViewById(R.id.iv_stu_more);
            convertView.setTag(holder);
        }else{
            holder = (StudentItemVH)convertView.getTag();
        }
        if(listItem==null||listItem.get(position)==null)
            Log.d("StudentAdapter","null");
        holder.studentName.setText((String)listItem.get(position).getName());
        holder.studentAccount.setText(String.valueOf(listItem.get(position).getAccount()));
        if (listItem.get(position).getState()==1) {
            holder.signStatus.setText("✅ 已签到");
            holder.signStatus.setTextColor(Color.GREEN);

        }else {
            holder.signStatus.setText("❌ 未签到");
            holder.signStatus.setTextColor(Color.RED);

        }
        holder.signPosition.setText(listItem.get(position).getLocation());
        holder.menu.setOnClickListener(v-> {
            String account = String.valueOf(listItem.get(position).getAccount());
            showPupMenu(mContext,holder.menu,classKey,account);
        });
        return convertView;
    }
    public void showPupMenu(Context context, View view, String classKey, String account){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.student_sign_option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_come:
                    // 修改为已签到
                    // Toast.makeText(mContext,"功能即将上线",Toast.LENGTH_SHORT).show();
                    setSign(classKey,account);
                    break;
                case R.id.action_no_come:
                    // 修改为未签到
                    // Toast.makeText(mContext,"功能即将上线",Toast.LENGTH_SHORT).show();
                    setUnSign(classKey,account);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    private void setUnSign(String classKey, String account) {
        new Thread(()->{
            OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/setUnSign?classKey="+classKey+"&account="+account;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d("修改签到状态成功","code"+response.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        int position = 0;
        Log.d("ceshi",account+ classKey+ "    sss");
        for (Student student : listItem) {
            if (student.getAccount() == Integer.parseInt(account))
                break;
            position++;
        }
        listItem.get(position).setState(0);
        notifyDataSetChanged();
    }

    private void setSign(String classKey, String account) {
        new Thread(()->{
            OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/setSign?classKey="+classKey+"&account="+account;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d("修改签到状态成功","code"+response.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        int position = 0;
        Log.d("ceshi",account+ classKey+ "    sss");
        for (Student student : listItem) {
            if (student.getAccount() == Integer.parseInt(account))
                break;
            position++;
        }
        listItem.get(position).setState(1);
        notifyDataSetChanged();
    }
}
