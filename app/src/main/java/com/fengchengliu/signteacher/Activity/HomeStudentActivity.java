package com.fengchengliu.signteacher.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.fengchengliu.signteacher.Adapter.ClassAdapter;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Object.Classes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_FALL;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_GET_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_REFRESH_SUCCESS;

// 学生端的活动
public class HomeStudentActivity extends AppCompatActivity {

    private ListView listView;
    private ClassAdapter classAdapter;
    private String contentBody = null;
    Handler handler= new Handler((msg)->{
        switch (msg.what){
            case MESSAGE_GET_SUCCESS:
                contentBody = (String) msg.obj;
                dealDate(contentBody);
                // Log.d("123", (String) msg.obj);
                break;
            case MESSAGE_CREATE_SUCCESS:
                Toast.makeText(HomeStudentActivity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_CREATE_FALL:
                if (msg.obj == "输入为空")
                    Toast.makeText(getApplicationContext(), "班级名称不能为空！" , Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(HomeStudentActivity.this, "创建失败, 请检查网络连接!", Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_REFRESH_SUCCESS:
                getClassData((String) msg.obj);
                if (contentBody != null)
                    dealDate(contentBody);
                Log.d("debug:: ", contentBody);
                classAdapter.notifyDataSetChanged();
                Toast.makeText(HomeStudentActivity.this, "刷新完成!", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);
        // toolBar的设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.allClass);
        // 获取学生账号
        String account = getIntent().getStringExtra("account");

        //悬浮的按钮
        FloatingActionButton fab = findViewById(R.id.fab_stu_class);
        fab.setOnClickListener(v -> {
            // 浮动按钮可以写加入班级

        });
        // 滑动刷新
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
        //获取班级数据
        getClassData(account);
    }
    // 处理返回的json数据并放入list作为数据源更新适配器
    private void dealDate(String message) {
        Gson gson = new Gson();
        List<Classes> classList = gson.fromJson(message, new TypeToken<List<Classes>>() {
        }.getType());
        classAdapter = new ClassAdapter(HomeStudentActivity.this, classList,0);
        listView.setAdapter(classAdapter);
    }
    // 工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_class_menu, menu);
        return true;
    }
    // 菜单事件
    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_joinCLa:
                Toast.makeText(this, "加入班级", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_person:
                Toast.makeText(this, "个人信息", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getClassData(String account) {
        new Thread(() -> {
            Message msg = Message.obtain();
            final OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getClassInfo?account=" + account;
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
}