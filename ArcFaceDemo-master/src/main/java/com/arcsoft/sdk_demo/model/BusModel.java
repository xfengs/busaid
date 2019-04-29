package com.arcsoft.sdk_demo.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.LoginActivity;
import com.arcsoft.sdk_demo.Main2Activity;
import com.arcsoft.sdk_demo.fragment.RecPage;
import com.arcsoft.sdk_demo.utils.Common;
import com.connect.StatusJson;
import com.connect.busInfo;
import com.connect.interfaceHttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.RequiresApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusModel {

    private Application myApp;
    private Context context;
    private List<StuInfo> stu;

    public BusModel(Context context) {
        this.context = context;
        this.myApp=(Application)context.getApplicationContext();
    }

    public BusModel(Context context, List<StuInfo> stu) {
        this.context = context;
        this.stu = stu;
        this.myApp=(Application)context.getApplicationContext();
    }

    public BusModel() {

    }

    public List<StuInfo> getStu() {
        return stu;
    }

    public void Login(final String mobile) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<busInfo> call = request.Login(mobile);
        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("登录中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<busInfo>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<busInfo> call, Response<busInfo> response) {

                if(response.isSuccessful()) {
                    busInfo result = response.body();

                    if(result.getLinecount()==0) {
                        Toast.makeText(myApp.getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        tipDialog.dismiss();

                    }

                    else {

                        tipDialog.dismiss();
                        //Toast.makeText(myApp.getApplicationContext(), "登录成功，加载中...", Toast.LENGTH_SHORT).show();

                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("登录成功，加载中...")
                                .create();
                        doneDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(context!=null && !((LoginActivity)context).isFinishing()){
                                    doneDialog.dismiss();
                                }

                            }
                        }, 1000);

                        //获取sharedPreferences对象
                        SharedPreferences phoneSP = myApp.getApplicationContext().getSharedPreferences("lisheng", Context.MODE_PRIVATE);
                        //获取editor对象
                        SharedPreferences.Editor editor = phoneSP.edit();//获取编辑器
                        //存储键值对
                        editor.putString("phone", mobile);
                        //提交
                        editor.commit();//提交修改

                        result.setLinechecked(-1);
                        //保存数据到json
                        myApp.mBusInfo = result;
                        saveBusInfoFile(result);


                        //ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation((LoginActivity)context);
                        Intent i2 = new Intent(context,Main2Activity.class);
                        context.startActivity(i2);
                        ((LoginActivity)context).finish();
                        ((LoginActivity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }

                }

                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("登录失败:"+response.errorBody().toString())
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
            }

            @Override
            public void onFailure(Call<busInfo> call, Throwable t) {
                tipDialog.dismiss();
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
    public void saveBusInfoFile(busInfo result){

        try {
            JSONObject inf = new JSONObject();
            inf.put("linecount", result.getLinecount());
            inf.put("linechecked", result.getLinechecked());
            JSONArray array = new JSONArray();
            //String filePath = getApplication().getExternalFilesDir("").getPath();
            for (int i = 0; i < result.getLinecount(); i++) {
                JSONObject arr_ = new JSONObject();
                arr_.put("lineid", result.getLine().get(i).getLineid());
                arr_.put("linename", result.getLine().get(i).getLinename());
                arr_.put("busnumber", result.getLine().get(i).getBusnumber());
                arr_.put("busid", result.getLine().get(i).getBusid());
                arr_.put("teachername", result.getLine().get(i).getTeachername());
                arr_.put("teachernumber", result.getLine().get(i).getTeachernumber());
                arr_.put("schoolname", result.getLine().get(i).getSchoolname());
                arr_.put("driver", result.getLine().get(i).getDriver());
                arr_.put("stationcount", result.getLine().get(i).getStationcount());

                JSONArray stationlist = new JSONArray();
                for (int j = 0; j < result.getLine().get(i).getStationcount(); j++) {
                    JSONObject arr_b = new JSONObject();
                    arr_b.put("stationname", result.getLine().get(i).getStationlist().get(j).getStationname());
                    arr_b.put("studentcount", result.getLine().get(i).getStationlist().get(j).getStudentcount());
                    JSONArray studentlist = new JSONArray();
                    for (int k = 0; k < result.getLine().get(i).getStationlist().get(j).getStudentcount(); k++) {
                        JSONObject arr_c = new JSONObject();
                        arr_c.put("stuid", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getStuid());
                        arr_c.put("stuname", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getStuname());
                        arr_c.put("baba", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getBaba());
                        arr_c.put("mama", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getMama());
                        arr_c.put("babanum", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getBabanum());
                        arr_c.put("mamanum", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).getMamanum());
                        arr_c.put("isleave", result.getLine().get(i).getStationlist().get(j).getStudentlist().get(k).isIsLeave());
                        studentlist.put(k, arr_c);

                    }
                    arr_b.put("studentlist", studentlist);
                    stationlist.put(j, arr_b);
                }
                arr_.put("stationlist",stationlist);
                array.put(i, arr_);
            }
            inf.put("line", array);
            //文件路径
            String path = myApp.mPath + "/businfo.json";

            File file = new File(path);
            if (file.exists()) {
                Log.i("myTag", "文件存在");
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("myTag", "文件创建成功");


            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(inf.toString().getBytes());
                // fileOutputStream.write(sbString.getBytes());
                fileOutputStream.close();
                Log.i("myTag", "json数据保存到成功！！！");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateData(final String mobile, final int flag) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        final int templinechecked=myApp.mBusInfo.getLinechecked();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<busInfo> call = request.Login(mobile);
        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("更新数据中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<busInfo>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<busInfo> call, Response<busInfo> response) {

                if(response.isSuccessful()) {
                    busInfo result = response.body();

                    if(result.getLinecount()==0) {
                        Toast.makeText(myApp.getApplicationContext(), "更新数据失败", Toast.LENGTH_SHORT).show();

                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("更新数据失败")
                                .create();
                        doneDialog.show();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                    }

                    else {

                        //获取sharedPreferences对象
                        SharedPreferences phoneSP = myApp.getApplicationContext().getSharedPreferences("lisheng", Context.MODE_PRIVATE);
                        //获取editor对象
                        SharedPreferences.Editor editor = phoneSP.edit();//获取编辑器
                        //存储键值对
                        editor.putString("phone", mobile);
                        //提交
                        editor.commit();//提交修改


                        //保存数据到json
                        result.setLinechecked(-1);
                        saveBusInfoFile(result);
                        //result.setLinechecked(templinechecked);
                        myApp.mBusInfo = result;



                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("更新数据成功")
                                .create();
                        doneDialog.show();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                        if(flag==1) {
                            myApp.isFirstLoad = true;
/*                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation((Main2Activity) context);
                            Intent i2 = new Intent(context, Main2Activity.class);
                            ((Main2Activity)context).finish();
                            context.startActivity(i2);
                            */
                            Intent it = new Intent(context, Main2Activity.class);
                            context.startActivity(it);
                            ((Main2Activity)context).finish();
                            ((Main2Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }


                    }

                }
                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("更新失败:"+response.errorBody().toString())
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
            }

            @Override
            public void onFailure(Call<busInfo> call, Throwable t) {

                tipDialog.dismiss();
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

    public void sendData(String data, final View headerView, final File logfile) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.uploadBusLog(data);

        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("上传数据中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {

                //Log.e("xfeng", "onResponse: "+response.body() );
                if (response.isSuccessful()) {
                    StatusJson StatusJson = response.body();
                    if (StatusJson.isStatus()) {
                        //Toast.makeText(myApp.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传成功")
                                .create();
                        doneDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                        myApp.mBusStatus.writeIsUploaded(true, myApp.order_flag);
                        String date = new Common().getDate();
                        myApp.mBusStatus.writeUploadDate(date);
                        myApp.mBusStatus.uploadDate = date;


                        if(logfile!=null) {
                            if (logfile.exists()) {
                                new Common().changeUploaded(logfile);
                            }
                        }

                        //上传完数据后弹出选择对话框
                        ((RecPage)(((Main2Activity)context).getSupportFragmentManager().getFragments().get(1))).reInitData();

                        /*final Main2Activity mainActivity = (Main2Activity) context;
                        mainActivity.setFragmentSkipInterface(new Main2Activity.FragmentSkipInterface() {
                            @Override
                            public void gotoFragment(ViewPager viewPager) {
                                *//** 跳转到第三个页面的逻辑 *//*
                                viewPager.setCurrentItem(2);
                            }
                        });
                        *//** 进行跳转 *//*
                        mainActivity.skipToFragment();*/

                    } else {
                        tipDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传失败")
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
                }
                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("上传失败:"+response.errorBody().toString())
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
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {
                tipDialog.dismiss();
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

    public void uploadAvatar(String data) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.uploadBusLogHis(data);

        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("上传数据中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {

                //Log.e("xfeng", "onResponse: "+response.body() );
                if (response.isSuccessful()) {
                    StatusJson StatusJson = response.body();
                    if (StatusJson.isStatus()) {
                        //Toast.makeText(myApp.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传成功")
                                .create();
                        doneDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                    } else {
                        tipDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传失败")
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
                }

                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("上传失败:"+response.errorBody().toString())
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
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {
                tipDialog.dismiss();
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

    public void uploadHisData(String data) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.uploadBusLogHis(data);

        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("上传数据中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {

                //Log.e("xfeng", "onResponse: "+response.body() );
                if (response.isSuccessful()) {
                    StatusJson StatusJson = response.body();
                    if (StatusJson.isStatus()) {
                        //Toast.makeText(myApp.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传成功")
                                .create();
                        doneDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                    } else {
                        tipDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传失败")
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
                }

                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("上传失败:"+response.errorBody().toString())
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
            }

            @Override
            public void onFailure(Call<StatusJson> call, Throwable t) {
                //Toast.makeText(myApp.getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                tipDialog.dismiss();
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


    public void getBusInfo() {
        String filesPath=myApp.mPath;
        String path = filesPath + "/businfo.json";
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
            java.lang.reflect.Type type = new TypeToken<busInfo>() {}.getType();
            busInfo info = gson.fromJson(jsonData,busInfo.class);

            myApp.mBusInfo=info;

        }
    }

}
