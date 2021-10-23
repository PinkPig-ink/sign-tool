package com.fengchengliu.signteacher.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fengchengliu.signteacher.R;

public class SpalshActivity extends AppCompatActivity {
    Handler handler = new Handler((msg )->{
        return false;
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        Intent intent = new Intent(this, LoginActivity.class);
        handler.postDelayed(new Thread(()->{
            startActivity(intent);
            finish();
        }),500);

    }
}