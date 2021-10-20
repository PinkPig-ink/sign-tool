package com.fengchengliu.signteacher.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fengchengliu.signteacher.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.fengchengliu.signteacher.Adapter.StudentAdapter;
import com.fengchengliu.signteacher.Object.User;


import java.util.List;

public class studentActivity extends AppCompatActivity {
    private ListView listView;
    private String contentBody;


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
        // 解析message并获取学生数据
        Gson gson = new Gson();
        List<User> studentList= gson.fromJson(message, new TypeToken<List<User>>(){}.getType());
        StudentAdapter studentAdapter = new StudentAdapter(this,studentList);
        listView.setAdapter(studentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d("点击","点击了");
            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar_student);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.allStudent);

    }


}