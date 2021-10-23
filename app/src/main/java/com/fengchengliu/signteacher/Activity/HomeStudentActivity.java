package com.fengchengliu.signteacher.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fengchengliu.signteacher.Adapter.ClassAdapter;
import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Object.Classes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.INTERNET;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_FALL;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_GET_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_REFRESH_SUCCESS;

// 学生端的活动
public class HomeStudentActivity extends AppCompatActivity {

    private ListView listView;
    private ClassAdapter classAdapter;
    private String contentBody = null;
    public LocationClient mLocationClient  = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String account;
    private String moreAddress = "地址未初始化";
    Handler handler = new Handler((msg) -> {
        switch (msg.what) {
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
                    Toast.makeText(getApplicationContext(), "班级名称不能为空！", Toast.LENGTH_LONG).show();
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

        mLocationClient = new LocationClient(getApplicationContext());
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPRS
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(1500); // 设置发起定位请求的间隔时间为5000ms
        // 设置获取地址信息
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
       new Thread(()->{
           // 调用此方法开始定位
           mLocationClient.start();
       }).start();

        // intent
        this.account = getIntent().getStringExtra("account");
        String name = getIntent().getStringExtra(("name"));
        // toolBar的设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.allClass);

        //悬浮的按钮
        FloatingActionButton fab = findViewById(R.id.fab_stu_class);
        fab.setOnClickListener(v -> {
            // 浮动按钮可以写加入班级
            joinclass(this.account, name);
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

    /**
     * 定位成功之后的回调函数
     *
     * @author zhaokaiqiang
     *
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            StringBuffer sb = new StringBuffer(256);
            sb.append("\n县 ：");
            sb.append(location.getAddrStr());
            sb.append("\n描述 ：");
            sb.append(location.getLocationDescribe());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());
                sb.append("\n卫星数 : ");
                sb.append(location.getSatelliteNumber());
            }
            moreAddress = location.getLocationDescribe();
            Log.d("dizhi","duz"+moreAddress);
        }
    }



    private void joinclass(String account, String name) {
        final EditText editText = new EditText(HomeStudentActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(HomeStudentActivity.this);
        inputDialog.setTitle("请输入班级邀请码").setView(editText);
        inputDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    String classKey = editText.getText().toString();
                    new Thread(() -> {
                        Message msg = Message.obtain();
                        try {
                            // 判断输入框是否为空
                            if (TextUtils.isEmpty(editText.getText()))
                                throw new IOException();
                            final OkHttpClient client = new OkHttpClient();
                            String url = "http://116.63.131.15:9001/joinInClass";
                            RequestBody formBody = new FormBody.Builder()
                                    .add("classKey", classKey)
                                    .add("name", name)
                                    .add("account", account)
                                    .add("state", "0")
                                    .add("location", "花江校区") // 默认地址
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
                })
                .setNegativeButton("取消", null)
                .show();
    }


    // 处理返回的json数据并放入list作为数据源更新适配器
    private void dealDate(String message) {
        Gson gson = new Gson();
        List<Classes> classList = gson.fromJson(message, new TypeToken<List<Classes>>() {
        }.getType());
        if (moreAddress == null)
            moreAddress = "默认地址";
        classAdapter = new ClassAdapter(HomeStudentActivity.this, classList, 0, account, moreAddress);
        listView.setAdapter(classAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(position);
            }
        });
    }


    // 工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_tool_menu, menu);
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



    private void updateItem(int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = listView.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = listView.getChildAt(position - firstVisiblePosition);
            classAdapter.getView(position, view, listView);
        }
    }

}