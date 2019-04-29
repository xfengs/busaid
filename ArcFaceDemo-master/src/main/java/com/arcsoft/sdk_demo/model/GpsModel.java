package com.arcsoft.sdk_demo.model;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.busRecordInfo;
import com.connect.StatusJson;
import com.connect.interfaceHttp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GpsModel {

    private Application myApp;
    private Context context;

    public GpsModel(Application app) {
        this.myApp = app;
    }

    public void updateGPS(String lon, String lat, String postxt, String speed, String time) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.updategps(myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLineid(), myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getBusid(),
                lon,lat,speed,time,postxt);


        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {
                Log.e("xfeng", "onResponse: "+response.body() );

                StatusJson StatusJson = response.body();
                if(StatusJson.isStatus()){
                    //Toast.makeText(myApp.getApplicationContext(), "gps保存成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(myApp.getApplicationContext(), "GPS提交失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {
                //Toast.makeText(myApp.getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void endGPS() {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.endgps(myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLineid());

        myApp.mClient.stop();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {
                Log.e("xfeng", "onResponse: "+response.body() );

                StatusJson statusjson = response.body();
                if(statusjson.isStatus()){
                    Toast tt=Toast.makeText(myApp.getApplicationContext(), "下线成功", Toast.LENGTH_SHORT);
                    tt.setGravity(Gravity.BOTTOM,0,200);
                    //tt.show();
                    myApp.mBusStatus.reInit();
                    myApp.buslog= new busRecordInfo();
                }
                else{
                    Toast tt=Toast.makeText(myApp.getApplicationContext(), "下线失败", Toast.LENGTH_SHORT);
                    tt.setGravity(Gravity.BOTTOM,0,200);
                    //tt.show();
                }
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {
                Toast.makeText(myApp.getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("onFailure=" + t.getMessage());
            }
        });

    }
}
