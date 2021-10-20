package com.fengchengliu.signteacher.Object;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("name")
    private String name;
    @SerializedName("account")
    private int account;
    @SerializedName("state")
    private int state;
    @SerializedName("location")
    private String location;

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", account=" + account +
                ", state=" + state +
                ", location='" + location + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
