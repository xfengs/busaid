package com.arcsoft.sdk_demo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.arcsoft.sdk_demo.Application;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 获取当前时间戳
     */
    public static String getNowStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        //Date date = new Date(System.nanoTime());
        String res;
        long ts = date.getTime()/1000;
        res = String.valueOf(ts);
        return res;
    }
    /*
     * 获取当前时间戳
     */
    public String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        //Date date = new Date(System.nanoTime());

        String dt=simpleDateFormat.format(date);
        return dt;
    }

    public String getDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    public int getDay()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return date.getDate();
    }

    public int getOrderFlag(){
        int order_flag=0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");// HH:mm:ss//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        int hour =Integer.parseInt(simpleDateFormat.format(date));

        if(hour>=0 && hour <11)
            order_flag=1;
        if(hour>=11 && hour <13)
            order_flag=2;
        if(hour>=13 && hour <15)
            order_flag=3;
        if(hour>=15 && hour <=23)
            order_flag=4;

        if(order_flag==0)
            order_flag=4;

        return order_flag;
    }


    public String getOrderFlagString(){
        int order_flag=getOrderFlag();
        String order_flagString="";

        switch (order_flag) {
            case 1:
                order_flagString = "上午上学";
                break;
            case 2:
                order_flagString = "中午放学";
                break;
            case 3:
                order_flagString = "中午上学";
                break;
            case 4:
                order_flagString = "下午放学";
                break;
        }

        return order_flagString;
    }

    /**
     * 对象转为json
     * @param obj
     * @param method
     * @return
     */
    public String toJson(Object obj,int method) {
        // TODO Auto-generated method stub
        if (method==1) {

            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        }else if(method==2){

            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔

            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2=gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    public String getLogFile(int line_no,int line_id, Application app){

        String logFileName = "buslog_" +line_no +"_" + line_id + "_" + getDate() + "_" + getOrderFlag();
        File buslogfile2 = new File(app.mPath+"/buslog/"+app.tmobile+"/" + logFileName+"_u");

        if(buslogfile2.exists())
            return app.mPath+"/buslog/" +app.tmobile+"/" + logFileName+"_u";
        else
            return app.mPath+"/buslog/" +app.tmobile+"/" + logFileName;
    }

    public boolean isUploaded(int line_no,int line_id, Application app){
        String logFileName = "buslog_" +line_no +"_" + line_id + "_" + getDate() + "_" + getOrderFlag()+"_u";
        File buslogfile = new File(app.mPath+"/buslog/"+app.tmobile+"/" + logFileName);

        if(buslogfile.exists()){
            return true;
        }
        else
            return false;
    }

    public boolean isBuslog(int line_no,int line_id, Application app){
        String logFileName = "buslog_" +line_no +"_" + line_id + "_" + getDate() + "_" + getOrderFlag();
        File buslogfile = new File(app.mPath+"/buslog/"+app.tmobile+"/" + logFileName);

        if(buslogfile.exists()){
            return true;
        }
        else
            return false;
    }

    public void changeUploaded(File file){
        String s=file.getName();
        if(!s.substring(s.length()-1,s.length()).equals("u")){
            file.renameTo(new File(file.getAbsolutePath()+"_u"));
        }

    }

    public void scaleImage(final Activity activity, final View view, int drawableResId) {
/*        new Thread(new Runnable() {
            @Override
            public void run() {


                splashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();*/
        // 获取屏幕的高宽
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);

        // 解析将要被处理的图片
        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);

        if (resourceBitmap == null) {
            return;
        }

        // 开始对图片进行拉伸或者缩放

        // 使用图片的缩放比例计算将要放大的图片的高度
        int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());

        // 以屏幕的宽度为基准，如果图片的宽度比屏幕宽，则等比缩小，如果窄，则放大
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, bitmapScaledHeight, false);

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这里防止图像的重复创建，避免申请不必要的内存空间
                if (scaledBitmap.isRecycled())
                    //必须返回true
                    return true;


                // 当UI绘制完毕，我们对图片进行处理
                int viewHeight = view.getMeasuredHeight();


                // 计算将要裁剪的图片的顶部以及底部的偏移量
                int offset = (scaledBitmap.getHeight() - viewHeight) / 2;
                if(offset<0) offset=0;


                // 对图片以中心进行裁剪，裁剪出的图片就是非常适合做引导页的图片了
                Bitmap finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(),
                        scaledBitmap.getHeight() - offset * 2);


                if (!finallyBitmap.equals(scaledBitmap)) {//如果返回的不是原图，则对原图进行回收
                    scaledBitmap.recycle();
                    System.gc();
                }


                // 设置图片显示
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), finallyBitmap));
                return true;
            }
        });
    }
}
