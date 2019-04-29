package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.arcsoft.sdk_demo.model.BusModel;
import com.arcsoft.sdk_demo.utils.Common;


public class splashActivity extends Activity implements View.OnClickListener{

    private Application myApp;
    private String lastPhone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Common commonUtil=new Common();
        //commonUtil.scaleImage(splashActivity.this, findViewById(R.id.splash), R.drawable.splash);




        SharedPreferences phoneSP = getSharedPreferences("lisheng", Context.MODE_PRIVATE);
        SharedPreferences busSP=getSharedPreferences("bus", Context.MODE_PRIVATE);
        //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
        lastPhone = phoneSP.getString("phone", "");


        if(lastPhone!="")
        {
            Thread myThread=new Thread(){
                public void run(){
                    try{
                        //写出记录
                        BusModel busModel= new BusModel(splashActivity.this);
                        busModel.getBusInfo();

                        sleep(500);
                        splashActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent it = new Intent(getApplicationContext(), Main2Activity.class);
                                startActivity(it);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();

        }
        else{
            Intent it=new Intent(splashActivity.this,LoginActivity.class);
            startActivity(it);
            finish();
        }




    }

    @Override
    public void onClick(View v) {
    }


}
