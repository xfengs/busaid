package com.arcsoft.sdk_demo.model;

import com.arcsoft.sdk_demo.Application;

import java.util.ArrayList;
import java.util.List;

public class StuModel   {

    private Application myApp;
    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private int total,showup,leave,absent;

    public List<StuInfo> getStu() {
        return stu;
    }

    public StuModel() {
        showup=0;
        leave=0;
        absent=0;
        total=0;
    }

    public StuModel(Application app, List<StuInfo> s) {
        myApp=app;
        stu = s;
        showup=0;
        leave=0;
        absent=0;
        total=stu.size();
    }
    public void reinitCount(){
        showup=0;
        leave=0;
        absent=0;
        total=0;
    }

    public void calculateCount(){

        showup=0;
        leave=0;
        absent=0;
        total=0;

        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i).status==1) {//乘车
                showup++;
            }
            if (stu.get(i).status==2) {//请假
                leave++;
            }
            if (stu.get(i).status==3) {//未乘车
                absent++;
            }
            if (stu.get(i).status!=4) {//未乘车
                total++;
            }
        }
    }
    public int getTotal() {
        return total;
    }

    public int getShowup() {
        return showup;
    }

    public int getLeave() {
        return leave;
    }

    public int getAbsent() {
        return absent;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setShowup(int showup) {
        this.showup = showup;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int addShowup(int a){
        showup+=a;
        absent-=a;
        return showup;
    }
    public int minusShowup(int m){
        showup-=m;
        absent+=m;
        return showup;
    }
    public int addAbsent(int a){
        absent+=a;
        showup-=a;
        return absent;
    }
    public int minusAbsent(int m){
        absent-=m;
        showup+=m;
        return absent;
    }

    //无法判断是由已乘车转为请假还是未乘车转为请假
    //所以仅处理请假，另外两个值需要手动计算
    public int addLeave(int a,String f){
        leave+=a;
        if(f=="s")//来源于已乘学生
            showup-=a;
        if(f=="a")//来源于未乘学生
            absent-=a;
        return leave;
    }
    //取消请假分2种情况
    public int minusLeave(int m,String f){
        leave-=m;
        if(f=="s")//改为已乘学生
            showup+=m;
        if(f=="a")//改为未乘学生
            absent+=m;

        return leave;
    }



}
