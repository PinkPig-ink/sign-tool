package com.fengchengliu.signteacher.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Utils.RandomNum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.fengchengliu.signteacher.Adapter.ClassAdapter;
import com.fengchengliu.signteacher.Object.Classes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private String account = null;

    private ListView listView;
    private ClassAdapter classAdapter;
    private String contentBody;
    public final static int MESSAGE_CREATE_FALL = 0;
    public final static int MESSAGE_CREATE_SUCCESS = 1;
    public final static int MESSAGE_GET_SUCCESS = 2;
    public final static int MESSAGE_REFRESH_SUCCESS = 3;
    public static final int MESSAGE_SET_ZERO_SUCCESS = 4;
    public static final int MESSAGE_SET_ZERO_FALL = 5;
    public static final int MESSAGE_SET_CODE_SUCCESS = 6;
    public static final int MESSAGE_SET_CODE_FALL = 7;
    public static final int MESSAGE_GET_NUM_SUCCESS = 8;

    Handler handler = new Handler((msg) -> {
        switch (msg.what) {
            case MESSAGE_GET_SUCCESS:
                contentBody = (String) msg.obj;
                dealDate(contentBody);
                // Log.d("123", (String) msg.obj);
                break;
            case MESSAGE_CREATE_SUCCESS:
                Toast.makeText(HomeActivity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_CREATE_FALL:
                if (msg.obj == "输入为空")
                    Toast.makeText(getApplicationContext(), "班级名称不能为空！", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(HomeActivity.this, "创建失败, 请检查网络连接!", Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_REFRESH_SUCCESS:
                getClassData((String) msg.obj);
                if (contentBody != null) {
                    dealDate(contentBody);
                    Log.d("debug:: ", contentBody);
                    classAdapter.notifyDataSetChanged();
                    Toast.makeText(HomeActivity.this, "刷新完成!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return false;
    });

    private void dealDate(String message) {
        Gson gson = new Gson();
        List<Classes> classList = gson.fromJson(message, new TypeToken<List<Classes>>() {
        }.getType());
        classAdapter = new ClassAdapter(HomeActivity.this, classList, 1,account);
        listView.setAdapter(classAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_tool_menu, menu);
        return true;
    }

    // 右上角菜单
    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Toast.makeText(this, "创建班级", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_joinClass:
                Toast.makeText(this, "加入班级", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_MyInfo:
                Toast.makeText(this, "个人信息", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // toolBar的设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.allClass);
        account = getIntent().getStringExtra("account");
        //悬浮的按钮
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            showInputDialog(account);
        });
        SwipeRefreshLayout sp = findViewById(R.id.swipeFlash);
        sp.setOnRefreshListener(() -> {
            new Thread(() -> {
                Message msg = Message.obtain();
                msg.what = MESSAGE_REFRESH_SUCCESS;
                msg.obj = account;
                handler.sendMessage(msg);
                sp.setRefreshing(false);
            }).start();

        });
        getClassData(account);
    }

    private void showInputDialog(String account) {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(HomeActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(HomeActivity.this);
        inputDialog.setTitle("请输入班级名称").setView(editText);
        inputDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    String className = editText.getText().toString();
                    String classKey = RandomNum.createRandomString(6).toUpperCase();
                    // Log.d("show param", classKey + " " + className + " " + account);
                    // 新建一个线程来创建班级
                    new Thread(() -> {
                        Message msg = Message.obtain();
                        try {
                            // 判断输入框是否为空
                            if (TextUtils.isEmpty(editText.getText()))
                                throw new IOException();
                            final OkHttpClient client = new OkHttpClient();
                            String url = "http://116.63.131.15:9001/createClass";
                            RequestBody formBody = new FormBody.Builder()
                                    .add("className", className)
                                    .add("classKey", classKey)
                                    .add("owner", account)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(formBody)
                                    .build();
                            Call call = client.newCall(request);
                            call.execute();
                            msg.what = MESSAGE_CREATE_SUCCESS;

                        } catch (IOException e) {
                            msg.obj = "输入为空";
                            msg.what = MESSAGE_CREATE_FALL;
                            e.printStackTrace();
                        } finally {
                            handler.sendMessage(msg);
                        }
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void getClassData(String account) {
//        String account = sharedPreferences.getString("account","0");
//        Log.d("account",account);
        new Thread(() -> {
            Message msg = Message.obtain();
            final OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getClasses?account=" + account;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().contentLength() != 0) {
                        msg.what = MESSAGE_GET_SUCCESS;
                        msg.obj = response.body().string();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void setClassNum(String classKey) {
        new Thread(() -> {
            Message msg = Message.obtain();
            final OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getStateByClassKey?classKey=" + classKey;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().contentLength() != 0) {
                        String message = response.body().string();
                        Gson gson = new Gson();
                        List<Classes> classList = gson.fromJson(message, new TypeToken<List<Classes>>() {
                        }.getType());
                        msg.arg1 = classList.size();
                        Log.d("size::" ,"个数："+msg.arg1);
                        msg.what = MESSAGE_GET_NUM_SUCCESS;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}