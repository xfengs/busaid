package com.arcsoft.sdk_demo.model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.arcsoft.sdk_demo.Application;
import com.connect.StatusJson;
import com.connect.interfaceHttp;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeModel {
    private String teacher_tel;
    private int line_id;
    private String content;
    private Context context;
    private Application myApp;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public NoticeModel(Context context){
        this.context=context;
        this.myApp=(Application)context.getApplicationContext();
    }

    public String getTeacher_tel() {
        return teacher_tel;
    }

    public int getLine_id() {
        return line_id;
    }

    public String getContent() {
        return content;
    }

    public void setTeacher_tel(String teacher_tel) {
        this.teacher_tel = teacher_tel;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void SendNotice(int line_id,String teacher_tel,String content) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

//构建可以监听进度的client

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.saveStuNotice(line_id,teacher_tel,content);
 
        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {
                Log.e("xfeng", "onResponse: "+response.body() );

                StatusJson StatusJson = response.body();
                if(StatusJson.isStatus()){
                    Toast.makeText(myApp.getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(myApp.getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {

                final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                        .setTipWord("错误:"+t.getMessage())
                        .create();
                doneDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doneDialog.dismiss();
                    }
                }, 1000);
            }
        });

    }
}
