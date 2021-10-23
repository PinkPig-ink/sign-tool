package com.fengchengliu.signteacher.Activity.Register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fengchengliu.signteacher.Activity.LoginActivity;
import com.fengchengliu.signteacher.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Registerpassword extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private EditText password1;
    private TextView tips;
    private Button button;
    private ProgressBar progressbar;
    private ProgressBar progressbar1;
    private String userAccount;
    private String userPassword;
    private String userPassword1;
    private String name;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpassword);
       account=findViewById(R.id.registeraccount);
       password=findViewById(R.id.registerpassword);
       password1=findViewById(R.id.registerpassword1);
       tips=findViewById(R.id.tips);
        progressbar1=findViewById(R.id.progress_bar_register);
        progressbar1.setVisibility(View.INVISIBLE);
       button=findViewById(R.id.end1);
       name=getIntent().getStringExtra("name0");
       type=getIntent().getStringExtra("type");

       password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                   userPassword=password.getText().toString();
                   userPassword1=password1.getText().toString();
                    if(userPassword.equals(userPassword1))
                        tips.setVisibility(View.INVISIBLE);
               }
                   else{
                   userPassword=password.getText().toString();
                   userPassword1=password1.getText().toString();
                       if(! userPassword.equals(userPassword1)||userPassword.length()==0) {
                           tips.setText("两次密码不一致请重新输入");
                           tips.setTextColor(Color.parseColor("#FF0000"));
                           tips.setVisibility(View.VISIBLE);
                       }else
                       {
                           tips.setVisibility(View.INVISIBLE);
                       }
               }
           }
       });
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              createUser();
          }
      });

    }

    private void createUser() {
        progressbar=findViewById(R.id.progress_bar_register);
        progressbar.setVisibility(View.VISIBLE);
        userAccount=account.getText().toString();
        userPassword=password.getText().toString();
        userPassword1=password1.getText().toString();
        Log.i("aaaa","sssssssss"+userPassword1);
        if(userPassword1.length()!=0&&userPassword.length()!=0&&userAccount.length()!=0&&userPassword.equals(userPassword1)){
            Log.i("aaa","saasdfasfasfa");
        new Thread(() -> {
            try {
                final OkHttpClient client = new OkHttpClient();
                String url = "http://116.63.131.15:9001/addUser";
                RequestBody formBody = new FormBody.Builder()
                        .add("name", name)
                        .add("type", type)
                        .add("account",userAccount)
                        .add("pwd",userPassword)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.execute();
                progressbar.setVisibility(View.INVISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
            Toast.makeText(Registerpassword.this, "注册成功", Toast.LENGTH_SHORT).show();
            sign();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Registerpassword.this);
                   builder.setTitle("提示");
                    builder.setMessage("两次输入密码不一致请重新输入");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
        }
    }

    private void sign() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registerpassword.this);
        builder.setTitle("提示");
        builder.setMessage("注册成功！");
        builder.setPositiveButton("去登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Registerpassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}