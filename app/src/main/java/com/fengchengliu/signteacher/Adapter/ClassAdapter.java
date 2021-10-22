package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fengchengliu.signteacher.Activity.HomeActivity;
import com.fengchengliu.signteacher.Activity.HomeStudentActivity;
import com.fengchengliu.signteacher.Activity.SignStateActivity;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Utils.RandomNum;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.Object.Classes;
import com.fengchengliu.signteacher.ViewHolder.StudentItemVH;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_GET_NUM_SUCCESS;

public class ClassAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    List<Classes> list;
    private final Context mContext;
    private final int type;
    private final String account;
    private String location;
    private String rightCode = null;
    public int[] classNum = {1, 2, 3, 4, 5, 6, 7, 8};

    public ClassAdapter(Context context, List<Classes> list, int type, String account) {
        this.account = account;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.type = type;
    }

    public ClassAdapter(Context context, List<Classes> list, int type, String account, String location) {
        this.location = location;
        this.account = account;
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
        // setClassNum(holder.classCode.getText().toString());
        holder.classNumber.setText(classNum[position] + "人");

        holder.classMenu.setTag(position);
        // 选项栏设置
        holder.classMenu.setOnClickListener(v -> {
            showPupMenu(mContext, holder.classMenu, type, holder.classCode.getText().toString());
        });
        return convertView;
    }

    // 卡片上的弹出式菜单
    public void showPupMenu(Context context, View view, int type, String classKey) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        if (type == 1)
            popupMenu.getMenuInflater().inflate(R.menu.item_teacher_menu, popupMenu.getMenu());
        else if (type == 0)
            popupMenu.getMenuInflater().inflate(R.menu.item_student_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String info = "";
            switch (item.getItemId()) {
                case R.id.item_sign:
                    startSign(classKey);
                    info = "老师进入班级签到页面..";
                    break;
                case R.id.item_dismiss:
                    info = "老师解散班级";
                    disMissClass(classKey);
                    break;
                // 下面是学生端的卡片菜单响应
                case R.id.item_stu_sign:
                    info = "进入签到";
                    signClass(classKey);
                    break;
                case R.id.item_quit:
                    info = "学生退出班级";
                    quitClass(classKey);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    private void disMissClass(String classKey) {
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("⚠️警告️\n您真的要解散这个班级吗？这会丢失所有的数据");
        inputDialog.setPositiveButton("确定", ((dialog, which) -> {
            new Thread(() -> {
                try {
                    final OkHttpClient client = new OkHttpClient();
                    String url = "http://116.63.131.15:9001/dismissClass";
                    RequestBody formBody = new FormBody.Builder()
                            .add("classKey", classKey)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    Call call = client.newCall(request);
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            int position = 0;
            for (Classes classes : list) {
                if (classes.getClassKey().equals(classKey))
                    break;
                position++;
            }
            list.remove(position);
            notifyDataSetChanged();
            Toast.makeText(mContext, "✅操作成功", Toast.LENGTH_SHORT).show();
        })).setNegativeButton("取消", null);
        inputDialog.show();
    }

    private void quitClass(String classKey) {
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("⚠️警告️\n您真的要退出这个班级吗？");
        inputDialog.setPositiveButton("确定", ((dialog, which) -> {
            new Thread(() -> {
                try {
                    final OkHttpClient client = new OkHttpClient();
                    String url = "http://116.63.131.15:9001/quitClass";
                    RequestBody formBody = new FormBody.Builder()
                            .add("classKey", classKey)
                            .add("account", account)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    Call call = client.newCall(request);
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            int position = 0;
            for (Classes classes : list) {
                if (classes.getClassKey().equals(classKey))
                    break;
                position++;
            }
            list.remove(position);
            notifyDataSetChanged();
            Toast.makeText(mContext, "✅操作成功", Toast.LENGTH_SHORT).show();
        })).setNegativeButton("取消", null);
        inputDialog.show();
    }

    // 学生进行签到的方法
    // 请求网络，查看输入的签到码与班级码是否一致，若一样，修改签到位为1，否则提示签到码错误或签到已结束
    private void signClass(String classKey) {

        final EditText editText = new EditText(mContext);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("请输入老师提供的签到码").setView(editText);
        inputDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    // 签到码
                    String signCode = "";
                    if (TextUtils.isEmpty(editText.getText()))
                        signCode = "errorCode";
                    else
                        signCode = editText.getText().toString();
                    getSignCode(classKey);
                    Log.d("查看签到参数：", "rightCode " + rightCode + " + getCode " + signCode);
                    if (signCode.equals(rightCode)) {
                        postSign(classKey, account, location);
                        Log.d("签到", account + classKey + location + "信息");
                        Toast.makeText(mContext, "✅签到成功，您已成功加入本次签到～", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "❌签到失败，可能是签到已结束或您的签到码错误", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 学生发起签到的方法
    private void postSign(String classKey, String account, String location) {
        new Thread(() -> {
            try {
                final OkHttpClient client = new OkHttpClient();
                String url = "http://116.63.131.15:9001/postSign";
                RequestBody formBody = new FormBody.Builder()
                        .add("classKey", classKey)
                        .add("account", account)
                        .add("location", location)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startSign(String classKey) {
        // 签到流程：生成签到码，把学生的签到位置零，在把签到码显示在顶部
        // 开始一个活动，显示这个班所有学生的签到情况
        // 随机生成签到码
        String code = RandomNum.createRandomString(6).toUpperCase();
        Intent intent = new Intent(mContext, SignStateActivity.class);
        intent.putExtra("signCode", code);
        intent.putExtra("classKey", classKey);
        mContext.startActivity(intent);
    }

    // 获取签到码
    private void getSignCode(String classKey) {
        new Thread(() -> {
            String code;
            OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getCode?classKey=" + classKey;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    code = response.body().string();
                    Log.d("网络获取的签到码", "签到码" + code);
                    rightCode = code;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

}
