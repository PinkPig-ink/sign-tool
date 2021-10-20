package com.fengchengliu.signteacher.Activity;

import android.app.Application;

import com.xuexiang.xui.XUI;

public class myApp extends Application {

    public void onCreate() {
        super.onCreate();

        XUI.init(this); //初始化UI框架
        XUI.debug(false);  //开启UI框架调试日志
    }
}
