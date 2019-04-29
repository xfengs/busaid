package com.arcsoft.sdk_demo.model;

import com.arcsoft.sdk_demo.Application;

import androidx.annotation.NonNull;

public class StuInfo implements Comparable<StuInfo> {

    private Application myApp;

    public String stuName;
    public int stuId;
    public String baba,mama,babanum,mamanum;
    public String station;
    public boolean jia;
    //是否请假
    public boolean isLeave;
    //是否乘车，
    public boolean isTaken;
    /**
     * -- 1 乘车
     * -- 2 未乘车已请假
     * -- 3 未乘车未请假
     * -  4 标记为标题
     */
    public int status;
    public boolean isAuto;


    public StuInfo() {
        // TODO Auto-generated constructor stub
        stuName="";
        stuId=0;
        baba="";
        mama="";
        babanum="";
        mamanum="";
        station="";
        isLeave = false;
        isTaken=false;
        status=3;
        isAuto=false;
    }

    public StuInfo(String stuName, int status) {
        this.stuName = stuName;
        this.status = status;
    }

    public StuInfo(Application app) {
        this.myApp = app;
    }

    @Override
    public int compareTo(@NonNull StuInfo o) {
        int i = o.status - this.status ;//先按照年龄排序
        return i;
    }



}
