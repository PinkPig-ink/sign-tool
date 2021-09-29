package com.fengchengliu.signteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.Toast;


import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    static int MSG_SUCCESS=1;
    static int MSG_FALL=0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
         @Override
          public void handleMessage(Message msg) {
                         if (msg.what == MSG_SUCCESS) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                 //保存登录的状态和用户名
                             final EditText Account =  findViewById(R.id.userAccount);
                             Log.d("得到 account ",Account.getText().toString());
                             saveLoginStatus(Account.getText().toString());
                                 //登录成功的状态保存到MainActivity
                             Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                             intent.putExtra("account",Account.getText().toString() );
                             startActivity(intent);
//                                 Intent intent = new Intent();

//                             setResult(RESULT_OK, intent);
                             LoginActivity.this.finish();
                         }else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                         }
                     }
    };

    private void saveLoginStatus(String account) {
        SharedPreferences preference = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("account",account);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.btn_login);
        final EditText Account =  findViewById(R.id.userAccount);
        final EditText Password = findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = Account.getText().toString();
                String password = Password.getText().toString();
                if(TextUtils.isEmpty(account)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(true){
                    new Thread(){
                        @Override
                        public void run() {
                            String message= null;
                            try{
                                final OkHttpClient client = new OkHttpClient();
                                // String url = "http://10.0.2.2:8081/user/getUser";
                                // RequestBody formBody = new FormBody.Builder().add("account",account).add("password",password).build();
                                // Request request = new Request.Builder().post(formBody).url(url).build();
                                // Response response = client.newCall((request)).execute();
                                // if(response.isSuccessful()) {
                                //     message = response.body().string();
                                //     Log.d("123",message);
                                // }else
                                //     Log.d("123","fall");
                                message = "true";
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if(message.equals("true"))
                                handler.sendEmptyMessage(MSG_SUCCESS);
                            else
                                handler.sendEmptyMessage(MSG_FALL);
                        }
                    }.start();
                }
            }
        });


    }

    boolean isRight(String account ,String password){
        // String url = "http://localhost:8081/user/getUse/"+account+"/"+password;
//        RequestSign requestSign = new RequestSign();
//         String message=HttpUtil.getData(url);
//        String message = requestSign.connect(url);
//         if(message.equals("true"))
//             return true;
//         else return false;
        return true;
    }

}