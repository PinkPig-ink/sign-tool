package com.fengchengliu.signteacher.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Object.User;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    static int MSG_SUCCESS = 1;
    static int MSG_FALL = 0;
    private ProgressBar progress;
    private EditText account;
    private EditText password;
    private CheckBox checkBox;
    private SharedPreferences sp;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (progress != null)
                progress.setVisibility(View.INVISIBLE);
            if (msg.what == MSG_SUCCESS) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                //保存登录的状态和用户名
                Log.d("得到 account ", account.getText().toString());


                // 根据账号类型判断启动不同页面
                String message = msg.obj.toString();
                User user = new Gson().fromJson(message, User.class);
                String name = user.getUserName();
                Intent intent = null;
                if (user.getUserType() == 0) {
                    // 学生端 , 进入签到页面
                    intent = new Intent(LoginActivity.this, HomeStudentActivity.class);
                    intent.putExtra("account", account.getText().toString());
                    intent.putExtra("name", name);
                } else if (user.getUserType() == 1) {
                    // 老师端 , 进入班级列表
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("account", account.getText().toString());

                }
                if (intent != null)
                    startActivity(intent);
                Log.d("user_login", user.toString());

                // startActivity(intent);
            } else if (msg.what == MSG_FALL) {
                Toast.makeText(LoginActivity.this, "登陆失败，密码或网络错误", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account = findViewById(R.id.userAccount);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox_save_password);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        Button login = findViewById(R.id.btn_login);
        progress = findViewById(R.id.progress_bar_login);
        progress.setVisibility(View.INVISIBLE);
        TextView tv_help = findViewById(R.id.tv_login_tips);
        tv_help.setOnClickListener(v -> {
            Toast.makeText(this, "请联系您的学校老师获取您的账号和密码", Toast.LENGTH_SHORT)
                    .show();
        });
        account.setText(sp.getString("account",""));


        checkBox.setChecked(sp.getBoolean("check",false));

        login.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            String account = this.account.getText().toString();
            String password = this.password.getText().toString();
            SharedPreferences.Editor editor = sp.edit();
            if (TextUtils.isEmpty(account)) {
                Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            } else {
                editor.putString("account",account);
                editor.putString("password",password);
                editor.putBoolean("check",checkBox.isChecked());
                editor.apply();
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
                        Log.d("123", url);
                        // 根据查询数据库来判断登录成功
                        if (response.isSuccessful() && response.body().contentLength() != 0) {
                            message = response.body().string();
                            // 传入msg消息
                            msg.obj = message;
                            Log.d("msg_login", message);
                            message = "true";
                        } else {
                            Log.d("msg_login", "fall");
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
                    } else {
                        handler.sendEmptyMessage(MSG_FALL);
                    }
                }).start();
            }
        });
        if (sp.getBoolean("check",false))
            password.setText(sp.getString("password",""));

    }


}