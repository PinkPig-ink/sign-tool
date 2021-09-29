package com.fengchengliu.signteacher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.fengchengliu.signteacher.Adapter.classAdapter;
import com.fengchengliu.signteacher.entity.classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    dealDate((String) msg.obj);

            }
        }
    };

    private void dealDate(String  message) {
        Gson gson = new Gson();
        List<classes> classList= gson.fromJson(message, new TypeToken<List<classes>>(){}.getType());
        classAdapter classAdapter = new classAdapter(this,classList);
        listView.setAdapter(classAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d("点击","点击了");
                Intent intent = new Intent(HomeActivity.this,studentActivity.class);
                TextView classID = (TextView) arg1.findViewById(R.id.classId);
                String classId = classID.getText().toString();
                intent.putExtra("classId",classId);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//悬浮的按钮
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        listView = findViewById(R.id.allClass);
        String account = getIntent().getStringExtra("account");
//        System.out.println(account);
//        Log.d("intent account ",account);
        getClassData(account);
//        classAdapter classAdapter = new classAdapter(this,listItem);
//        listView.setAdapter(classAdapter);


    }

    private void showInputDialog() {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(HomeActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(HomeActivity.this);
        inputDialog.setTitle("请输入班级名称！").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(HomeActivity.this,
                               "创建成功！",
                                Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private ArrayList<HashMap<String,Object>> getClassData(String account ){
        ArrayList<HashMap<String,Object>> listItem = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//        String account = sharedPreferences.getString("account","0");
//        Log.d("account",account);
        new Thread(){
            @Override
            public void run() {


            }
        }.start();

        return  listItem;
    }
}