package com.face.entity;

import android.graphics.Bitmap;

public class Face {
    private int imageId;          //使用id锁定水果图片
    private String imageName;     //对应的水果名字
    private int  mode;     //对应的水果名字
    private String gpsinfo;
    private String time;

    public Face( String imageName, String gettime, int mode, String gpsinfo) {
        super();

        this.imageName = imageName;
        this.mode = mode;
        this.gpsinfo=gpsinfo;
        this.time=gettime;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getImageName() {
        return imageName;
    }
    public int getMode() {
        return mode;
    }
    public String getGpsinfo(){ return gpsinfo; }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public String getTime(){return time;}

}
