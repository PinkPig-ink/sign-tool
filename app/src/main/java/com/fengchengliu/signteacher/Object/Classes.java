package com.fengchengliu.signteacher.Object;

import com.google.gson.annotations.SerializedName;

public class Classes {
    @SerializedName("className")
    private String className; // 班级名
    @SerializedName("0")
    private int classNumber; //班级人数, 通过State表的记录来获取,每个班的人数, 默认是 0 个人
    @SerializedName("classKey")
    private String classKey;  // 加入密码

    @Override
    public String toString() {
        return "classes{" +
                "className='" + className + '\'' +
                ", classNumber=" + classNumber +
                ", classKey='" + classKey + '\'' +
                '}';
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public String getClassKey() {
        return classKey;
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }
}