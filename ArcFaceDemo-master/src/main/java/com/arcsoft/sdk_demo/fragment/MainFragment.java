package com.arcsoft.sdk_demo.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.Main2Activity;
import com.arcsoft.sdk_demo.QMUICommonListItemView;
import com.arcsoft.sdk_demo.QMUIGroupListView;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.UpdateRegisterActivity;
import com.arcsoft.sdk_demo.model.BusModel;
import com.arcsoft.sdk_demo.model.GpsModel;
import com.arcsoft.sdk_demo.model.NoticeModel;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.splashActivity;
import com.arcsoft.sdk_demo.utils.Common;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.connect.StuHolsJson;
import com.connect.busInfo;
import com.connect.interfaceHttp;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class MainFragment extends BaseFragment {
    @Nullable
    private Application myApp;
    private final String TAG = this.getClass().toString();
    private SharedPreferences sp;
    QMUITopBarLayout mTopBar;
    private int cols=6;

    QMUIGroupListView mGroupListView;

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;

    private static final int STATE_REFRESHING=0;
    private static final int STATE_FINISH=1;


    private busInfo mBusInfo;
    private String filesPath;
    private TextView mTitle;
    private TextView mTestView;
    private TextView mGrooveView;

    private QMUIGroupListView.Section lineSection;

    public List<Map<String, String>> lineList=new ArrayList<Map<String, String>>();

    InputStream is = null;

    public String facesFolder="/faces";
    public int stuPos=0;
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private int stuSize;

    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private boolean isLineChecked;

    private boolean sIsScrolling;
    private DownloadBuilder builder;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private CardView mCardView;
    private GpsModel gpsModel;

    private int mRefreshState;
    QMUIEmptyView mEmptyView;


    public List<StuInfo> getStu() {
        return stu;
    }

    public void setStu(List<StuInfo> stu) {
        this.stu = stu;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            //更新界面数据，如果数据还在下载中，就显示加载框
            if (mRefreshState == STATE_REFRESHING) {
                //mRefreshListener.onRefreshing();
                //mEmptyView.show(true);
            }
            //数据加载完毕
            else{
                mEmptyView.hide();
                lazyLoad();
            }
        } else {
            //关闭加载框
            //mRefreshListener.onRefreshFinish();
            mEmptyView.hide();
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        makeDirectory(filesPath+"/facedata");
        makeDirectory(filesPath+"/buslog");
        makeDirectory(filesPath+"/buslog/"+myApp.tmobile);
        makeDirectory(filesPath+facesFolder);

        //去服务器下载数据
        mRefreshState = STATE_REFRESHING;
       // mEmptyView.show(true);
        BusModel busModel=new BusModel(getActivity());
        busModel.getBusInfo();
        mBusInfo=myApp.mBusInfo;
        mRefreshState = STATE_FINISH;
        //mCategoryController.loadBaseData();
    }


    public void initView(View view){

        myApp=(Application) getActivity().getApplication();

        filesPath=myApp.mPath;
        //xfeng add
        mGroupListView=(QMUIGroupListView)view.findViewById(R.id.groupListView);
        isLineChecked=false;

        mEmptyView= view.findViewById(R.id.emptyView);


        mTopBar =  view.findViewById(R.id.topBar);
        mGrooveView = view.findViewById(R.id.groove);
        if(((Main2Activity)getActivity()).getIsHasGroove()){
            mGrooveView.setLineSpacing(80,1);
        }

        mCardView=view.findViewById(R.id.namecard);
        mCardView.getBackground().mutate().setAlpha(200);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyGridLayoutManager gridLayoutManager=new MyGridLayoutManager(getActivity(),cols);
        gridLayoutManager.setScrollEnabled(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter = new ItemAdapter(stu,getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(getActivity()).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling == true) {
                        Glide.with(getActivity()).resumeRequests();

                    }
                    sIsScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        mTestView = (TextView) view.findViewById(R.id.testView);
        mTitle = (TextView) view.findViewById(R.id.stuCount);

        // TODO Auto-generated method stub


         View.OnClickListener listener = new  View.OnClickListener() {
            public void onClick (View v){
                switch (v.getId()) {
                    case R.id.logoff:
                        //获取sharedPreferences对象
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("lisheng", Context.MODE_PRIVATE);
                        //获取editor对象
                        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                        //存储键值对
                        editor.putString("phone", "");
                        //提交
                        editor.commit();//提交修改
                        myApp.mBusStatus.reInit();
                        Intent it=new Intent(getActivity(),splashActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(it);
                        break;
                    case R.id.send:
                        final String[] items=new String[myApp.mBusInfo.getLine().size()];
                        for(int i=0;i<myApp.mBusInfo.getLine().size();i++) {
                            items[i] = myApp.mBusInfo.getLine().get(i).getLinename() ;
                        }
                        new AlertDialog.Builder(getActivity())
                                .setTitle("请选择线路")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent;
                                        Uri data;
                                        showEditTextDialog(myApp.mBusInfo.getLine().get(which).getLineid() );
                                    }
                                })
                                .show();

                        break;

                    case R.id.updatedata:
                        if(myApp.mBusInfo.getLinechecked()!=-1 && stu!=null) {
                            BusModel busModel = new BusModel(getActivity(), stu);
                            busModel.updateData(myApp.mBusInfo.getLine().get(0).getTeachernumber(),0);
                            stu = busModel.getStu();
                            getLeaveInfo(myApp.order_flag);
                        }
                        else {
                            final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                    .setTipWord("选择线路后可更新\n该线路请假信息")
                                    .create();
                            doneDialog.show();
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    BusModel busModel = new BusModel(getActivity(), stu);
                                    busModel.updateData(myApp.mBusInfo.getLine().get(0).getTeachernumber(),1);
                                    doneDialog.dismiss();
                                }
                            }, 1500);

                        }
                        break;
                }
            }
        };

        View v = view.findViewById(R.id.logoff);
        v.setOnClickListener(listener);
        v.setBackgroundColor(Color.parseColor("#00000000"));
        v = view.findViewById(R.id.send);
        v.setOnClickListener(listener);
        v.setBackgroundColor(Color.parseColor("#00000000"));
        v = view.findViewById(R.id.updatedata);
        v.setOnClickListener(listener);
        v.setBackgroundColor(Color.parseColor("#00000000"));

        //xfeng
        //首先需要获取到wifi管理者，初始化工具类
        //WifiAdmin wifiAdmin = new WifiAdmin(this);
//		wifiAdmin.createAp();

        initTopBar();
        updateapp();


    }


    private void showEditTextDialog(final int lineid) {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
//        builder.getEditText().setMaxHeight(5);
        builder.setTitle("发送消息")
                .setPlaceholder("在此输入消息")
                .setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("发送", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        /*
                        QMUITipDialog tipDialog = new QMUITipDialog.Builder(getActivity())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord("正在发送")
                                .create();;
                        tipDialog.show();
                        */

                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            //Toast.makeText(getActivity(), "公告: " + text, Toast.LENGTH_SHORT).show();
                            NoticeModel noticeModel=new NoticeModel(getActivity());
                            noticeModel.SendNotice(lineid,myApp.mBusInfo.getLine().get(0).getTeachernumber(),text.toString());

                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请填入内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void initTopBar() {
        //后退按钮
        /*mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // popBackStack();
            }
        });*/

        mTopBar.setTitle("利笙校车");
        mTopBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void initGroupListView() {
        busInfo result=mBusInfo;
        mTestView.setText(""+ result.getLine().get(0).getTeachername() + "     "+ result.getLine().get(0).getTeachernumber());
        lineSection=QMUIGroupListView.newSection(getActivity());
        mGroupListView.removeAllViews();
        lineSection.setTitle("负责线路");
        mTitle.setText("");

        for (int i = 0; i < result.getLinecount(); i++) {

            final QMUICommonListItemView itemWithSwitch = mGroupListView.createItemView(result.getLine().get(i).getBusnumber()+":"+result.getLine().get(i).getLinename());
            itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

            final int finalI = i;
            itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @SuppressLint("ResourceAsColor")
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    lineSection.singleCheck(buttonView, isChecked);
                    if(isChecked==true) {
                        isLineChecked=true;
                        if(myApp.lastLine==-1)
                            myApp.lastLine=finalI;

                        makeDirectory(filesPath+"/facedata/"+myApp.mBusInfo.getLine().get(finalI).getLineid());

                        //加载人脸
                        showStu(finalI);

                    }
                    else {
                        isLineChecked=false;
                        Toast.makeText(myApp.getApplicationContext(), "已下线", Toast.LENGTH_SHORT).show();
                        gpsModel=new GpsModel(myApp);

                        if(myApp.mBusInfo.getLinechecked()!=-1)
                            gpsModel.endGPS();

                        myApp.mBusInfo.setLinechecked(-1);
                        mBusInfo.setLinechecked(-1);
                        myApp.mClient.stop();
                        myApp.mBusStatus.writeOnline(-1);
                        myApp.mBusStatus.reInit();

                        mAdapter.clearAll();
                        mRecyclerView.setBackgroundColor(Color.alpha(0));
                        mTitle.setText("");
                    }


                }
            });

            int size = QMUIDisplayHelper.dp2px(myApp.getApplicationContext(), 20);
            lineSection.addItemView(itemWithSwitch, null);
        }
        lineSection.addTo(mGroupListView);

        if(myApp.mBusInfo.getLinechecked()!=-1){
            lineSection.checkOne(myApp.mBusInfo.getLinechecked());
            showStu(myApp.mBusInfo.getLinechecked());
        }
    }

    public void showStu(int finalI){

        Toast.makeText(myApp.getApplicationContext(), "已上线，GPS开始记录", Toast.LENGTH_SHORT).show();
        myApp.order_flag=new Common().getOrderFlag();

        myApp.mBusInfo.setLinechecked(finalI);
        mBusInfo.setLinechecked(finalI);
        myApp.mBusStatus.writeOnline(finalI);
        myApp.mBusStatus.writeBusid(mBusInfo.getLine().get(finalI).getBusid());
        myApp.mBusStatus.writeLinename(mBusInfo.getLine().get(finalI).getLinename());
        myApp.mBusStatus.online=finalI;
        //myApp.mBusStatus.isUploaded=false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        myApp.mBusStatus.writeLoginTime(simpleDateFormat.format(date));

        myApp.mFaceDB.setmDBPath(mBusInfo.getLine().get(finalI).getLineid());
        myApp.mFaceDB.setDataPath(mBusInfo.getLine().get(finalI).getLineid());

       // myApp.initGPS();

    //////////
        final busInfo result=myApp.mBusInfo;
        final int iline;
        iline=result.getLinechecked();
        stuSize=0;
        stu.clear();

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("加载数据...");
        mProgressDialog.setCancelable(false);
        //mProgressDialog.show();

        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        tipDialog.show();

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                //Toast.makeText(myApp, "加载完毕", Toast.LENGTH_SHORT).show();
                if(mProgressDialog.isShowing()) {
                    //mProgressDialog.cancel();
                    //tipDialog.dismiss();
                }
                if(tipDialog.isShowing()) {
                    //mProgressDialog.cancel();
                    tipDialog.dismiss();
                }

            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                myApp.mFaceDB.loadFaces();
                for(int i=0; i<result.getLine().get(iline).getStationcount();i++)
                {
                    stuSize+=result.getLine().get(iline).getStationlist().get(i).getStudentcount();
                    for(int j=0;j<result.getLine().get(iline).getStationlist().get(i).getStudentcount();j++) {
                        StuInfo tmpStu=new StuInfo();
                        tmpStu.stuId=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuid();
                        tmpStu.stuName=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuname();
                        tmpStu.baba=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getBaba();
                        tmpStu.mama=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getMama();
                        tmpStu.babanum=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getBabanum();
                        tmpStu.mamanum=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getMamanum();
                        tmpStu.station=result.getLine().get(iline).getStationlist().get(i).getStationname();
                        //tmpStu.jia=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getIsLeave();
                        stu.add(tmpStu);
                    }

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTitle.setText("本线路共"+ (stuSize) +"人");
                        mRecyclerView.setBackgroundColor(Color.WHITE);
                        mRecyclerView.setAdapter(mAdapter);

                        //下载请假信息
                        getLeaveInfo(myApp.order_flag);

                    }
                });
            }
        }).start();


    }
    public boolean getLeaveInfo(int hols_type){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StuHolsJson> call;
        call = request.getLeaveInfo(myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLineid(), myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getTeachernumber(),
                hols_type);


        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StuHolsJson>() {
            @Override
            public void onResponse(Call<StuHolsJson> call, Response<StuHolsJson> response) {

                if (response.isSuccessful()) {
                    Log.e("xfeng", "onResponse: "+response.body() );

                    StuHolsJson stuHolsJson = response.body();
                    if(stuHolsJson.isStatus()){
                        //Toast.makeText(myApp.getApplicationContext(), "gps保存成功", Toast.LENGTH_SHORT).show();
                        stu=stuHolsJson.setStuLeaveInfo(stu);
                        myApp.stuLine.clear();
                        myApp.stuLine.addAll(stu);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    else{
                        Toast.makeText(myApp.getApplicationContext(), "获取请假信息失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("获取请假信息失败，请检查网络:"+response.errorBody().toString())
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
            public void onFailure(Call<StuHolsJson> call, Throwable t) {
                Toast.makeText(myApp.getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                //System.out.println("onFailure=" + t.getMessage());
            }
        });
        return true;
    }

    private void updateapp()
    {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(myApp.AppURL+"getversion")
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {

                        String downloadUrl="";
                        String version="";
                        String msg="";
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            version=json.getString("version");
                            if(myApp.version.equals(version))
                                return null;
                            downloadUrl =json.getString("url");
                            msg =json.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //如果是最新版本直接return null
                        return UIData
                                .create()
                                .setDownloadUrl(downloadUrl)
                                .setTitle("版本更新")
                                .setContent("版本号："+version+"\n"+msg);

                    }
                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                });

        builder.executeMission(getActivity());
    }

    public static void makeDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            Uri mPath = data.getData();
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
                Log.e(TAG, "error");
            } else {
                Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
            }
            startRegister(bmp, file);
        } else if (requestCode == REQUEST_CODE_OP) {
            Log.i(TAG, "RESULT =" + resultCode);
            if (data == null) {
                return;
            }
            stuPos=getActivity().getIntent().getIntExtra("stuPos",-1);

        } else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            Uri mPath = myApp.getCaptureImage();
            String file = getPath(mPath);
            Bitmap bmp = Application.decodeImage(file);
            startRegister(bmp, file);
        }

        mAdapter.notifyDataSetChanged();

    }
    /**
     * @param uri
     * @return
     */
    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(getActivity(), contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(getActivity(), contentUri, selection, selectionArgs);
                }
            }
        }
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param mBitmap
     */
    private void startRegister(Bitmap mBitmap, String file) {
        Intent it = new Intent(getActivity(), UpdateRegisterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", file);
        it.putExtras(bundle);
        it.putExtra("stuId",String.valueOf(stu.get(stuPos).stuId));
        startActivityForResult(it, REQUEST_CODE_OP);
    }

    protected void lazyLoad() {

        initGroupListView();
        myApp.tmobile=myApp.mBusInfo.getLine().get(0).getTeachernumber();
    }


    //网格布局
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

        List<StuInfo> stu;//存放数据
        Context context;

        public ItemAdapter(List<StuInfo> stu, Context context) {
            this.stu = stu;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sample, parent, false));

			/*
			for(int i=0;i<stu.size(); i++)
			{
				if (stu.get(i).jia == true)
					holder.jia.setText("假");
			}
			*/

            return holder;
        }

        //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
// 在这里对获取对象进行操作
        //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
        //position是点击位置
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            //设置textView显示内容为list里的对应项
            holder.tv.setText(stu.get(position).stuName);
            final String faceFile = String.valueOf(stu.get(position).stuId)+"_"+stu.get(position).stuName;
            String bmppath=myApp.mPath +facesFolder+"/"+faceFile+".jpg";
            File file = new File(bmppath);
            Log.i("xfeng", bmppath);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            if(file.exists()) {
                Glide.with(context).load(bmppath).apply(options).into(holder.siv);

            }
            else
                Glide.with(context).load(R.drawable.default_avatar).apply(options).into(holder.siv);

            if(stu.get(position).isLeave)
                holder.siv.setBorderColor(Color.RED);
            else
                holder.siv.setBorderColor(Color.parseColor("#06020b"));



            //子项的点击事件监听
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    stuPos=position;
                    final String[] items;
                    if(stu.get(position).isLeave)
                        items= new String[]{"姓名："+stu.get(position).stuName+"（本班次请假）", "家长1："+stu.get(position).babanum, "家长2："
                                +stu.get(position).mamanum,"站点："+stu.get(position).station,"设置人像","取消请假"};
                    else
                        items = new String[]{"姓名："+stu.get(position).stuName, "家长1："+stu.get(position).babanum, "家长2："
                            +stu.get(position).mamanum,"站点："+stu.get(position).station,"设置人像","设为请假"};

                    new AlertDialog.Builder(getActivity())
                            .setTitle("学生信息")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;
                                    Uri data;
                                    switch (which) {
                                        case 1:
                                            if(stu.get(position).babanum!="") {
                                                intent = new Intent(Intent.ACTION_DIAL);
                                                data = Uri.parse("tel:" + stu.get(position).babanum);
                                                intent.setData(data);
                                                startActivity(intent);
                                            }
                                            break;
                                        case 2:
                                            if(stu.get(position).mamanum!="") {
                                                intent = new Intent(Intent.ACTION_DIAL);
                                                data = Uri.parse("tel:" + stu.get(position).mamanum);
                                                intent.setData(data);
                                                startActivity(intent);
                                            }
                                            break;
                                        case 3:

                                            break;
                                        case 4:
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("请选择注册方式")
                                                    .setIcon(android.R.drawable.ic_dialog_info)
                                                    .setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which){
                                                                case 1:
                                                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                                                    ContentValues values = new ContentValues(1);
                                                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                                                    Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                                                    myApp.setCaptureImage(uri);
                                                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                                                    startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
                                                                    break;
                                                                case 0:
                                                                    Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
                                                                    getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
                                                                    getImageByalbum.setType("image/jpeg");
                                                                    startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
                                                                    break;
                                                                default:
                                                            }
                                                        }
                                                    })
                                                    .show();

                                            dialog.dismiss();
                                            break;
                                        case 5:
                                            if(stu.get(position).isLeave){
                                                stu.get(position).isLeave=false;
                                                Toast.makeText(getActivity(), "已取消请假", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                stu.get(position).isLeave=true;
                                                Toast.makeText(getActivity(), "已请假", Toast.LENGTH_SHORT).show();
                                            }
                                            myApp.stuLine.clear();
                                            myApp.stuLine.addAll(stu);
                                            mAdapter.notifyDataSetChanged();
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            });
        }
        private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
            if (origin == null) {
                return null;
            }
            int height = origin.getHeight();
            int width = origin.getWidth();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (!origin.isRecycled()) {
                origin.recycle();
            }
            return newBM;
        }

        //要显示的子项数量
        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            return stu.size();
        }


        //这里定义的是子项的类，不要在这里直接对获取对象进行操作
        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            QMUIRadiusImageView siv;
            TextView tv,jia;


            public MyViewHolder(View itemView) {
                super(itemView);
                siv = itemView.findViewById(R.id.imageView1);
                tv = itemView.findViewById(R.id.textView1);


            }
        }

        /*之下的方法都是为了方便操作，并不是必须的*/

        //在指定位置插入，原位置的向后移动一格
        public boolean addItem(int position, StuInfo msg) {
            if (position < stu.size() && position >= 0) {
                stu.add(position, msg);
                notifyItemInserted(position);
                return true;
            }
            return false;
        }

        //去除指定位置的子项
        public boolean removeItem(int position) {
            if (position < stu.size() && position >= 0) {
                stu.remove(position);
                notifyItemRemoved(position);
                return true;
            }
            return false;
        }

        //清空显示数据
        public void clearAll() {
            stu.clear();
            notifyDataSetChanged();
        }
    }

    public class MyGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public MyGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }





}
