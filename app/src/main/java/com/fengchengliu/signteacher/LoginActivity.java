package com.fengchengliu.signteacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.fengchengliu.signteacher.entity.User;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    static int MSG_SUCCESS = 1;
    static int MSG_FALL = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SUCCESS) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                //保存登录的状态和用户名
                final EditText Account = findViewById(R.id.userAccount);
                Log.d("得到 account ", Account.getText().toString());
                //登录成功的状态保存到MainActivity
                saveLoginStatus(Account.getText().toString());
                // 根据账号类型判断启动不同页面
                String message = msg.obj.toString();
                User user = new Gson().fromJson(message, User.class);

                if (user.getUserType() == 0) {
                    // 学生端 , 进入签到页面

                }else if (user.getUserType() == 1) {
                    // 老师端 , 进入班级列表
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("account", Account.getText().toString());
                    startActivity(intent);
                }
                Log.d("321", user.toString());

                // startActivity(intent);
            } else if (msg.what == MSG_FALL){
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

    });

    private void saveLoginStatus(String account) {
        SharedPreferences preference = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("account", account);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.btn_login);
        final EditText Account = findViewById(R.id.userAccount);
        final EditText Password = findViewById(R.id.password);
        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        String saveAccount = user.getString("account", "");
        Account.setText(saveAccount);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = Account.getText().toString();
                String password = Password.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (true) {
                    new Thread(() -> {
                        Message msg = Message.obtain();
                        String message = null;
                        try {
                            final OkHttpClient client = new OkHttpClient();
                            Log.d("123", account + "/n" + password);
                            String url = "http://116.63.131.15:9001/getUser?account=" + account + "&password=" + password;
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            Response response = client.newCall((request)).execute();
                            Log.d("123",url);
                            // 根据查询数据库来判断登录成功
                            if (response.isSuccessful() && response.body().contentLength()!= 0) {
                                message = response.body().string();
                                // 传入msg消息
                                msg.obj = message;
                                Log.d("123", message);
                                message = "true";
                            } else {
                                Log.d("123", "fall");
                                message = "false";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (message != null) {
                            if (message.equals("true")) {
                                // handler.sendEmptyMessage(MSG_SUCCESS);
                                // 发送消息
                                msg.what = MSG_SUCCESS;
                                handler.sendMessage(msg);
                            }
                            if (message.equals("false"))
                                handler.sendEmptyMessage(MSG_FALL);
                        }else {
                            handler.sendEmptyMessage(MSG_FALL);
                        }
                    }).start();
                }
            }
        });


    }


}