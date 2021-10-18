package com.fengchengliu.signteacher.ViewHolder;

import android.widget.ImageView;
import android.widget.TextView;

public class ClassItemVH {
    public TextView className;
    public TextView classNumber;
    public TextView classCode;
    public ImageView classDelete;

    @Override
    public String toString() {
        return "ClassItemVH{" +
                "className=" + className +
                ", classNumber=" + classNumber +
                ", classCode=" + classCode +
                ", classDelete=" + classDelete +
                '}';
    }
}
