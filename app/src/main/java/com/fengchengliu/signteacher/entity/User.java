package com.fengchengliu.signteacher.entity;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    private String userName;
    @SerializedName("type")
    private int userType; //用户标识
    @SerializedName("account")
    private int userAccount;
    @SerializedName("password")
    private String userPassword;

    public int getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(int userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userType=" + userType +
                ", userAccount=" + userAccount +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
