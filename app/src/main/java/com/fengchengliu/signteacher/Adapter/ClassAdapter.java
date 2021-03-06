package com.fengchengliu.signteacher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
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
import com.fengchengliu.signteacher.Object.Student;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Utils.RandomNum;
import com.fengchengliu.signteacher.ViewHolder.ClassItemVH;
import com.fengchengliu.signteacher.Object.Classes;
import com.fengchengliu.signteacher.ViewHolder.StudentItemVH;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kotlin.reflect.KVariance;
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
    private final HashMap<String, Integer> hashMap = new HashMap<>();
    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 1:
                rightCode = (String) msg.obj;
                Log.d("??????","????????????"+rightCode);
                break;
        }
       return false;
    });
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
        // ??????????????????
        holder.className.setText(list.get(position).getClassName());
        // setClassNum(holder.classCode.getText().toString());
        String classKey = list.get(position).getClassKey();
        new Thread(()->{
            setClassNum(classKey);
        }).start();

        int num;
        if (hashMap.get(classKey) == null)
            num = 0;
        else
            num = hashMap.get(classKey);
        holder.classNumber.setText(num + "???");
        holder.classCode.setText(classKey);
        Log.d("hashMap", hashMap.toString() + "test");
        holder.classMenu.setTag(position);
        // ???????????????
        holder.classMenu.setOnClickListener(v -> {
            showPupMenu(mContext, holder.classMenu, type, holder.classCode.getText().toString());
        });
        return convertView;
    }

    // ???????????????????????????
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
                    info = "??????????????????????????????..";
                    break;
                case R.id.item_dismiss:
                    info = "??????????????????";
                    disMissClass(classKey);
                    break;
                // ???????????????????????????????????????
                case R.id.item_stu_sign:
                    info = "????????????";
                    signClass(classKey);
                    break;
                case R.id.item_quit:
                    info = "??????????????????";
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
        inputDialog.setTitle("???????????????\n???????????????????????????????????????????????????????????????");
        inputDialog.setPositiveButton("??????", ((dialog, which) -> {
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
            Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
        })).setNegativeButton("??????", null);
        inputDialog.show();
    }

    private void quitClass(String classKey) {
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("???????????????\n????????????????????????????????????");
        inputDialog.setPositiveButton("??????", ((dialog, which) -> {
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
            Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
        })).setNegativeButton("??????", null);
        inputDialog.show();
    }

    // ???????????????????????????
    // ????????????????????????????????????????????????????????????????????????????????????????????????1????????????????????????????????????????????????
    private void signClass(String classKey) {

        final EditText editText = new EditText(mContext);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("?????????????????????????????????").setView(editText);
        Runnable runnable = () -> getSignCode(classKey);
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        runnable.run();
        inputDialog.setPositiveButton("??????",
                (dialog, which) -> {
                    // ?????????
                    String signCode = "";
                    if (TextUtils.isEmpty(editText.getText()))
                        signCode = "errorCode";
                    else
                        signCode = editText.getText().toString();


                Log.d("?????????????????????", "rightCode " + rightCode + " + getCode " + signCode);
                    if (signCode.equals(rightCode)) {
                        postSign(classKey, account, location);
                        Log.d("??????", account + classKey + location + "??????");
                        Toast.makeText(mContext, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("??????", null)
                .show();
    }

    // ???????????????????????????
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
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        // ???????????????????????????????????????????????????????????????
        // ?????????????????????
        String code = (int)((Math.random()*9+1)*1000)+"";
        Intent intent = new Intent(mContext, SignStateActivity.class);
        intent.putExtra("signCode", code);
        intent.putExtra("classKey", classKey);
        mContext.startActivity(intent);
    }

    // ???????????????
    private void getSignCode(String classKey) {
        new Thread(() -> {
            Message msg = Message.obtain();
            String code;
            OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getCode?classKey=" + classKey;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    code = response.body().string();
                    Log.d("????????????????????????", "?????????" + code);
                    msg.what = 1;
                    msg.obj = code;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                handler.sendMessage(msg);
            }
        }
        ).start();
    }

    private void setClassNum(String classKey) {
        new Thread(() -> {
            String msg;
            OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getStateByClassKey?classKey=" + classKey;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    msg = response.body().string();
                    Gson gson = new Gson();
                    List<Student> list = gson.fromJson(msg, new TypeToken<List<Student>>() {
                    }.getType());
                    int num = list.size();
                    Log.d("hashMap", "??????" + num + "???  ");
                    hashMap.put(classKey, num);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

}
