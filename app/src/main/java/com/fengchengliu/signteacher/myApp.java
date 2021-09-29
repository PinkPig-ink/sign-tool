package com.fengchengliu.signteacher;

import android.app.Application;

import com.xuexiang.xui.XUI;

public class myApp extends Application {

    public void onCreate() {
        super.onCreate();

        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
    }
}
