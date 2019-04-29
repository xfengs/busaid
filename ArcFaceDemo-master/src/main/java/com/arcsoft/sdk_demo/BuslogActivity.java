package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.sdk_demo.model.StuInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.connect.Translation;
import com.connect.interfaceHttp;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guo.android_extend.widget.ExtImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuslogActivity extends Activity implements View.OnClickListener {
    private Application myApp;
    private String facesFolder="/faces";
    private int cols=6;
    private SumAdapter mSumAdapter1;
    private SumAdapter mSumAdapter2;
    private RecyclerView mRecyclerView1,mRecyclerView2;
    private TextView ycText,wcText;
    private boolean sIsScrolling;
    private busRecordInfo blog;
    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private List<StuInfo> stu1 = new ArrayList<StuInfo>();
    private List<StuInfo> stu2 = new ArrayList<StuInfo>();
    private Button mUpload,mOffline;
    private static final int REQUEST_CODE_OFFLINE = 4;
    private int linechecked;
    QMUITopBarLayout mTopBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buslog);
        myApp=(Application)this.getApplication();

        TextView mTestView;
        mTestView=(TextView)findViewById(R.id.testView);
        ycText=(TextView)findViewById(R.id.yclabel);
        wcText=(TextView)findViewById(R.id.wclabel);
        mTopBar =  findViewById(R.id.buslogTopBar);


        mUpload = (Button) findViewById(R.id.btn_upload);
        mUpload.setOnClickListener(this);
        mOffline = (Button) findViewById(R.id.btn_offline);
        mOffline.setOnClickListener(this);

        blog=myApp.buslog;
        if(blog==null)
            blog=new busRecordInfo();
        stu=myApp.stuLine;

        int flag;
        if(blog.getStudentlist()!=null) {
            for (StuInfo s : stu) {
                flag = 0;
                for (busRecordInfo.StudentlistBean b : blog.getStudentlist()) {
                    if (b.getStuid() == s.stuId) {
                        s.isTaken = true;
                        stu1.add(s);
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    stu2.add(s);
                }

            }
        }
        else
            stu2.addAll(stu);

        ycText.setText("已乘车学生("+stu1.size()+")");
        wcText.setText("未乘车学生("+stu2.size()+")");

        mRecyclerView1 = (RecyclerView) findViewById(R.id.sumView1);
        mRecyclerView1.setLayoutManager(new GridLayoutManager(this,cols));
        mRecyclerView1.setAdapter(mSumAdapter1 = new SumAdapter(stu1,this));

        mRecyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(BuslogActivity.this).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling == true) {
                        Glide.with(BuslogActivity.this).resumeRequests();

                    }
                    sIsScrolling = false;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRecyclerView2 = (RecyclerView) findViewById(R.id.sumView2);
        mRecyclerView2.setLayoutManager(new GridLayoutManager(this,cols));
        mRecyclerView2.setAdapter(mSumAdapter2 = new SumAdapter(stu2,this));
        mRecyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(BuslogActivity.this).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling == true) {
                        Glide.with(BuslogActivity.this).resumeRequests();

                    }
                    sIsScrolling = false;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        initTopBar();

    }


    private void initTopBar() {

        mTopBar.setTitle("数据统计");
        mTopBar.setBackgroundColor(0xff00A8E1);
    }
    /* 将实体类转换成json字符串对象            注意此方法需要第三方gson  jar包
    * @param obj  对象
    * @return  map
    */
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
    public void onClick(View view) {
        if (view.getId() == R.id.btn_upload) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String dt=simpleDateFormat.format(date);

            busRecordInfo uploadInfo=new busRecordInfo();
            uploadInfo.setBusId(blog.getBusId());
            uploadInfo.setOrder(blog.getOrder());
            uploadInfo.setLineId(blog.getLineId());
            uploadInfo.setBusnumber(blog.getBusnumber());
            uploadInfo.setChengcheCount(stu1.size());
            uploadInfo.setWeichengCount(stu2.size());
            uploadInfo.setQingjiaCount(0);
            uploadInfo.setTotalCount(stu.size());
            uploadInfo.setDatetime(dt);
            uploadInfo.setDate(date.getDate());

            List<busRecordInfo.StudentlistBean> bltmp=new ArrayList<busRecordInfo.StudentlistBean>();
            int flag=0;


            for(StuInfo s:stu1){
                flag=0;
                if(blog.getStudentlist()!=null) {
                    for (busRecordInfo.StudentlistBean b : blog.getStudentlist()) {
                        if (s.stuId == b.getStuid()) {
                            bltmp.add(b);
                            flag = 1;
                        }
                    }
                }
                if(flag==0){
                    busRecordInfo.StudentlistBean btmp= new  busRecordInfo.StudentlistBean();
                    btmp.setStuid(s.stuId);
                    btmp.setStuname(s.stuName);
                    btmp.setDatetime(dt);
                    btmp.setDate(date.getDate());
                    btmp.setStatus(1);
                    btmp.setReason("手动");
                    bltmp.add(btmp);
                }
            }
            uploadInfo.setStudentlist(bltmp);
            if(uploadInfo.getStudentlist().size()==0)
                Toast.makeText(this, "无学生乘车！", Toast.LENGTH_SHORT).show();
            else
                sendData(toJson(uploadInfo,1));
        }
        if (view.getId() == R.id.btn_offline) {



            if (myApp.mBusStatus.isUploaded[myApp.order_flag-1]== true)
            {
                closeGPS();
            }
            else {
                Toast.makeText(this, "请先上传数据", Toast.LENGTH_SHORT).show();
            }
        }

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
                    myApp.mBusStatus.writeIsUploaded(true,myApp.order_flag);
                    myApp.mBusStatus.isUploaded[myApp.order_flag-1]=true;
                    mUpload.setClickable(false);
                    mUpload.setText("已上传");
                    mUpload.setTextColor(Color.GRAY);

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


    public class SumAdapter extends RecyclerView.Adapter<SumAdapter.SumViewHolder> {

        List<StuInfo> stuItem;
        Context context;

        public SumAdapter(List<StuInfo> stu, Context context) {
            this.stuItem = stu;
            this.context = context;
        }

        @Override
        public SumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SumViewHolder holder = new SumViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sample, parent, false));


            return holder;
        }

        //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
// 在这里对获取对象进行操作
        //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
        //position是点击位置
        @Override
        public void onBindViewHolder(SumViewHolder holder, final int position) {
            //设置textView显示内容为list里的对应项
            holder.tv.setText(stuItem.get(position).stuName);
            final String faceFile = String.valueOf(stuItem.get(position).stuId)+"_"+stuItem.get(position).stuName;
            String bmppath=((Application)context.getApplicationContext()).mPath +facesFolder+"/"+faceFile+".jpg";
            File file = new File(bmppath);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            if(file.exists()) {
                Glide.with(context).load(bmppath).apply(options).into(holder.siv);

            }
            else
                Glide.with(context).load(R.drawable.default_profile).apply(options).into(holder.siv);

            //子项的点击事件监听

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //if(stuItem.size()<position) {
                        Log.i("xfeng debug", "stuItem.size:" + stuItem.size() + " position:" + position);
                        Log.i("xfeng debug", "holder:" + view.getId());
                   // }


                    final String[] items;
                    if( myApp.mBusStatus.isUploaded[myApp.order_flag-1]==true) {
                        if (stuItem.get(position).isTaken == true)
                            items = new String[]{stuItem.get(position).stuName, "父亲电话：" + stuItem.get(position).babanum, "母亲电话："
                                    + stuItem.get(position).mamanum, "站点：" + stu.get(position).station};
                        else
                            items = new String[]{stuItem.get(position).stuName, "父亲电话：" + stuItem.get(position).babanum, "母亲电话："
                                    + stuItem.get(position).mamanum, "站点：" + stu.get(position).station};
                    }
                    else
                    {
                        if (stuItem.get(position).isTaken == true)
                            items = new String[]{stuItem.get(position).stuName, "父亲电话：" + stuItem.get(position).babanum, "母亲电话："
                                    + stuItem.get(position).mamanum, "站点：" + stu.get(position).station, "设为未乘车"};
                        else
                            items = new String[]{stuItem.get(position).stuName, "父亲电话：" + stuItem.get(position).babanum, "母亲电话："
                                    + stuItem.get(position).mamanum, "站点：" + stu.get(position).station, "设为已乘车"};
                    }



                    new AlertDialog.Builder(context)
                            .setTitle("学生信息")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    Uri data;
                                    switch (which) {
                                        case 1:
                                            if(stuItem.get(position).babanum!="") {
                                                intent = new Intent(Intent.ACTION_DIAL);
                                                data = Uri.parse("tel:" + stuItem.get(position).babanum);
                                                intent.setData(data);
                                                startActivity(intent);
                                            }
                                            break;
                                        case 2:
                                            if(stuItem.get(position).mamanum!="") {
                                                intent = new Intent(Intent.ACTION_DIAL);
                                                data = Uri.parse("tel:" + stuItem.get(position).mamanum);
                                                intent.setData(data);
                                                startActivity(intent);
                                            }
                                            break;
                                        case 4:
                                            if(stuItem.get(position).isTaken==true) {
                                                stu2.add(stuItem.get(position));
                                                stu1.remove(stuItem.get(position));
                                                stu2.get(stu2.size()-1).isTaken=false;
                                                mSumAdapter1.stuItem=stu1;
                                                mSumAdapter2.stuItem=stu2;

                                                ycText.setText("已乘车学生(" + stu1.size() + ")");
                                                wcText.setText("未乘车学生(" + stu2.size() + ")");
                                                mSumAdapter1.notifyDataSetChanged();
                                                mSumAdapter2.notifyDataSetChanged();
                                                //Toast.makeText(getApplicationContext(), stuItem.get(position).stuName + "设为未乘车", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                stu1.add(stuItem.get(position));
                                                stu2.remove(stuItem.get(position));
                                                stu1.get(stu1.size()-1).isTaken=true;
                                                mSumAdapter1.stuItem=stu1;
                                                mSumAdapter2.stuItem=stu2;
                                                ycText.setText("已乘车学生(" + stu1.size() + ")");
                                                wcText.setText("未乘车学生(" + stu2.size() + ")");
                                                mSumAdapter1.notifyDataSetChanged();
                                                mSumAdapter2.notifyDataSetChanged();
                                                //Toast.makeText(getApplicationContext(), stuItem.get(position).stuName + "设为已乘车", Toast.LENGTH_SHORT).show();

                                            }

                                            break;

                                    }
                                }
                            })
                            .show();
                }
            });
        }

        //要显示的子项数量
        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            return stuItem.size();
        }

        public void addData(StuInfo s) {
            stuItem.add(1,s);
            notifyItemInserted(1);  //删除
        }

        public void removeData(int position) {
            stuItem.remove(position);
            notifyItemRemoved(position);  //插入
        }

        //这里定义的是子项的类，不要在这里直接对获取对象进行操作
        public class SumViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            ExtImageView siv;
            TextView tv,jia;


            public SumViewHolder(View itemView) {
                super(itemView);
                siv = itemView.findViewById(R.id.imageView1);
                tv = itemView.findViewById(R.id.textView1);


            }
        }

    }


    public void closeGPS() {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://xfeng.gz01.bdysite.com/busnew/src/views/bus/") //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<Translation> call;
        call = request.closeGPS(blog.getBusId());

        if(myApp.mClient!=null)
            if(myApp.mClient.isStarted())
                myApp.mClient.stop();

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



                    Intent it = new Intent(BuslogActivity.this, BusinfoActivity.class);
                    //it.putExtra("stop","1");
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
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

}
