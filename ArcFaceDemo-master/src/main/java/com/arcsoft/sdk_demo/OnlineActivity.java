package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.connect.Translation;
import com.connect.interfaceHttp;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.io.FileInputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlineActivity extends Activity implements View.OnClickListener {
    private Application myApp;
    private TextView mOnlineInfo;
    private Button mQMButton;
    private Button mContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        myApp=(Application) this.getApplication();
        mOnlineInfo=findViewById(R.id.onlineInfo);
        mQMButton=(Button)findViewById(R.id.btn_uploadend);
        mContinue=(Button)findViewById(R.id.btn_continue);
        mQMButton.setOnClickListener(this);
        mContinue.setOnClickListener(this);




        String lineName=myApp.mBusStatus.linename;
        mOnlineInfo.setText(lineName+"在"+myApp.mBusStatus.loginTime+"上线，请选择以下操作：");
        if(myApp.mBusStatus.isUploaded[myApp.order_flag-1])
        {
            mQMButton.setText("下线");
        }
        else{
            mQMButton.setText("上传数据并下线");
        }



    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_continue)
        {
            myApp.initGPS();
            Intent it=new Intent(OnlineActivity.this,DetecterActivity.class);
            it.putExtra("Camera", 1);
            startActivity(it);
            OnlineActivity.this.finish();
        }
        if(v.getId()==R.id.btn_uploadend)
        {
            if(myApp.mBusStatus.isUploaded[myApp.order_flag-1])
            {
                closeGPS(myApp.mBusStatus.busid);
                QMUITipDialog tipDialog = new QMUITipDialog.Builder(this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("正在连接")
                        .create();;
                tipDialog.show();
            }
            else{
                mQMButton.setText("上传数据并下线");
                sendData(toJson(getBusInfo(),1));
                if(myApp.mBusStatus.isUploaded[myApp.order_flag-1]==true)
                    closeGPS(myApp.mBusStatus.busid);
            }

            myApp.mBusStatus.reInit();
            Intent it=new Intent(OnlineActivity.this,BusinfoActivity.class);
            startActivity(it);
            OnlineActivity.this.finish();

        }
    }
    public void closeGPS(int busid) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://xfeng.gz01.bdysite.com/busnew/src/views/bus/") //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<Translation> call;
        call = request.closeGPS(busid);


        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                Log.e("xfeng", "onResponse: "+response.body() );

                Translation translation = response.body();
                if(translation.getStatus()==1){
                    Toast.makeText(getApplicationContext(), "下线成功", Toast.LENGTH_SHORT).show();

                    if(myApp.mClient!=null)
                        if(myApp.mClient.isStarted())
                            myApp.mClient.stop();

                    myApp.mBusStatus.reInit();
                    myApp.buslog= new busRecordInfo();
                    myApp.mBusStatus.reReadData=false;

                    Intent it = new Intent(OnlineActivity.this, BusinfoActivity.class);
                    //it.putExtra("stop","1");
                    //it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    OnlineActivity.this.finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "下线失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("onFailure=" + t.getMessage());
            }
        });

    }

    private void sendData(String data) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://xfeng.gz01.bdysite.com/busnew/src/views/busdata/") //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<Translation> call;
        call = request.uploadStuData(data);


        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                Log.e("xfeng", "onResponse: "+response.body() );

                Translation translation = response.body();
                if(translation.getStatus()==1){
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    myApp.mBusStatus.isUploaded[myApp.order_flag-1]=true;

                }
                else{
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("onFailure=" + t.getMessage());
            }
        });

    }

    private String toJson(Object obj,int method) {
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

    private busRecordInfo getBusInfo() {
        String path = myApp.mPath + "/lastbuslog.json";
        String jsonData="";

        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                byte[] b=new byte[fileInputStream.available()];

                //将字节流中的数据传递给字节数组
                fileInputStream.read(b);

                //将字节数组转为字符串
                jsonData=new String(b);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<busRecordInfo>() {}.getType();
            return gson.fromJson(jsonData,busRecordInfo.class);

        }
        else return null;
    }
}
