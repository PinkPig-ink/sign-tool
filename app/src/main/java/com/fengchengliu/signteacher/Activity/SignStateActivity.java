package com.fengchengliu.signteacher.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.fengchengliu.signteacher.Adapter.StudentAdapter;
import com.fengchengliu.signteacher.Object.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fengchengliu.signteacher.R;
import com.fengchengliu.signteacher.Utils.RandomNum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.reflect.KVariance;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_GET_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_REFRESH_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_SET_CODE_FALL;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_SET_CODE_SUCCESS;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_SET_ZERO_FALL;
import static com.fengchengliu.signteacher.Activity.HomeActivity.MESSAGE_SET_ZERO_SUCCESS;

public class SignStateActivity extends AppCompatActivity {
    private ListView listView;
    private StudentAdapter studentAdapter;
    private String contentBody = null;
    private String classKey = null;
    private ProgressBar progressBar = null;
    private final Thread th = new Thread() {
        @Override
        public void run() {
            handler.postDelayed(this, 10);
            handler.sendEmptyMessage(10);
        }
    };
    Handler handler = new Handler((msg) -> {
        switch (msg.what) {
            case MESSAGE_GET_SUCCESS:
                contentBody = (String) msg.obj;
                dealDate(contentBody);
                break;
            case MESSAGE_REFRESH_SUCCESS:
                getStudentDataByClassKey((String) msg.obj);
                if (contentBody != null)
                    dealDate(contentBody);
                studentAdapter.notifyDataSetChanged();
                break;
            case MESSAGE_SET_ZERO_SUCCESS:
                getStudentDataByClassKey(classKey);
                if (contentBody != null)
                    dealDate(contentBody);
                studentAdapter.notifyDataSetChanged();
                break;
            case MESSAGE_SET_ZERO_FALL:
                // Toast.makeText(this,"网络错误",Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_SET_CODE_FALL:
                // Toast.makeText(this,"网络错误",Toast.LENGTH_SHORT).show();
                break;
            case 10:
                int progress = progressBar.getProgress();
                if (progress == 100) {
                    progress = 0;
                } else {
                    progress += 3;
                }
                progressBar.setProgress(progress);
                break;
            case 11:

                progressBar.setProgress(100);
                break;
            default:
                Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_state);
        Intent intent = getIntent();
        String signCode = intent.getStringExtra("signCode");
        classKey = intent.getStringExtra("classKey");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.pg_sign_bar);

        // ActionBar actionBar = getSupportActionBar();
        // if (actionBar != null)
        //     actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.allStates);
        getStudentDataByClassKey(classKey);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sign_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // toolbar 右上角的菜单：签到开始，停止，刷新
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_start_sign:
                progressBar.setProgress(20);
                handler.postDelayed(th, 0);
                postSign(classKey);
                break;
            case R.id.action_done_sign:
                handler.removeCallbacks(th);
                handler.sendEmptyMessage(11);
                stopSign();
                break;
            case R.id.action_update_data:
                new Thread(() -> {
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_REFRESH_SUCCESS;
                    msg.obj = classKey;
                    handler.sendMessage(msg);
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void stopSign() {
        String newCode = RandomNum.createRandomString(6).toUpperCase().toUpperCase();
        TextView tvSignCode = findViewById(R.id.tv_sign_code);
        tvSignCode.setText("签到未开始");
        tvSignCode.setTextColor(Color.BLACK);
        sendCodeToClass(newCode);
    }

    private void dealDate(String message) {
        // 解析message并获取学生数据
        Gson gson = new Gson();
        List<Student> studentList = gson.fromJson(message, new TypeToken<List<Student>>() {
        }.getType());
        studentAdapter = new StudentAdapter(this, studentList, classKey);
        listView.setAdapter(studentAdapter);
    }

    private void getStudentDataByClassKey(String classKey) {
//        String account = sharedPreferences.getString("account","0");
//        Log.d("account",account);
        new Thread(() -> {
            Message msg = Message.obtain();
            final OkHttpClient client = new OkHttpClient();
            String url = "http://116.63.131.15:9001/getStateByClassKey?classKey=" + classKey;
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

    private void postSign(String classKey) {
        setStateZero(classKey);
        TextView tvSignCode = findViewById(R.id.tv_sign_code);
        String signCode = RandomNum.createRandomString(6).toUpperCase();
        tvSignCode.setText("签到码：" + signCode);
        tvSignCode.setTextColor(Color.GREEN);
        sendCodeToClass(signCode);
    }

    private void sendCodeToClass(String signCode) {
        new Thread(() -> {
            Message msg = Message.obtain();
            try {
                // 修改签到码
                final OkHttpClient client = new OkHttpClient();
                String url = "http://116.63.131.15:9001/postCode";
                RequestBody formBody = new FormBody.Builder()
                        .add("classKey", classKey)
                        .add("code", signCode)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.execute();
                msg.what = MESSAGE_SET_CODE_SUCCESS; // 修改成功
            } catch (IOException e) {
                msg.what = MESSAGE_SET_CODE_FALL; // 修改失败
                e.printStackTrace();
            } finally {
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void setStateZero(String classKey) {
        new Thread(() -> {
            Message msg = Message.obtain();
            try {
                // 重置本班签到状态
                final OkHttpClient client = new OkHttpClient();
                String url = "http://116.63.131.15:9001/startSign";
                RequestBody formBody = new FormBody.Builder()
                        .add("classKey", classKey)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.execute();
                msg.what = MESSAGE_SET_ZERO_SUCCESS; // 重置成功
            } catch (IOException e) {
                msg.what = MESSAGE_SET_ZERO_FALL; // 重置失败
                e.printStackTrace();
            } finally {
                handler.sendMessage(msg);
            }
        }).start();
    }

}