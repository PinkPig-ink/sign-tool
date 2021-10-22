package com.fengchengliu.signteacher.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fengchengliu.signteacher.Adapter.ClassAdapter;
import com.fengchengliu.signteacher.Listener.MyLocationListener;
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

import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_FALL;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_CREATE_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_GET_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_REFRESH_SUCCESS;

// 学生端的活动
public class HomeStudentActivity extends AppCompatActivity {

    private ListView listView;
    private ClassAdapter classAdapter;
    private String contentBody = null;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
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
    private String account;
    private String moreAddress = "地址未初始化";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);
        // 声明定位客户端， 注册监听函数
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        // 客户端的定位参数配置
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

        // 权限配置
        getPermissionMethod();
        mLocationClient.start();
        new Thread(()->{
            while (true) {
                moreAddress = myListener.moreAddress;
                if (moreAddress!=null)
                    break;
            }
            Log.d("位置信息", "位置信息: "+moreAddress);
        }).start();

        // intent
        this.account = getIntent().getStringExtra("account");
        String name=getIntent().getStringExtra(("name"));
        // toolBar的设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.allClass);

        //悬浮的按钮
        FloatingActionButton fab = findViewById(R.id.fab_stu_class);
        fab.setOnClickListener(v -> {
            // 浮动按钮可以写加入班级
            joinclass(this.account,name);
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
                                    .add("state","0")
                                    .add("location","花江校区") // 默认地址
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
        classAdapter = new ClassAdapter(HomeStudentActivity.this, classList,0,account,moreAddress);
        listView.setAdapter(classAdapter);
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

    private void getPermissionMethod() {
        //授权列表
        List<String> permissionList = new ArrayList<>();

        //检查是否获取该权限 ACCESS_FINE_LOCATION
        if(ContextCompat.checkSelfPermission(HomeStudentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty()){ //权限列表不是空
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            //动态申请权限的请求
            ActivityCompat.requestPermissions(HomeStudentActivity.this,permissions,1);
        }
    }

    /**
     * 监听用户是否授权
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            //拒绝获取权限
//                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
//                            finish();
//                            return;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}