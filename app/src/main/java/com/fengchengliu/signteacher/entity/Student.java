package com.fengchengliu.signteacher.entity;

public class Student {

        private String studentName; // 姓名
        private int userAccount; // 学生账号
        private String userPassword; // 学生密码


    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(int userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "student{" +
                "studentName='" + studentName + '\'' +
                ", userAccount=" + userAccount +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
