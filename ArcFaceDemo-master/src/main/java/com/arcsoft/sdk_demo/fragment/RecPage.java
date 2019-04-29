package com.arcsoft.sdk_demo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.ageestimation.ASAE_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.genderestimation.ASGE_FSDKVersion;
import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.CameraFrameData;
import com.arcsoft.sdk_demo.CameraGLSurfaceView;
import com.arcsoft.sdk_demo.CameraSurfaceView;
import com.arcsoft.sdk_demo.FaceDB;
import com.arcsoft.sdk_demo.Main2Activity;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.adapter.GridRecyclerAdapter;
import com.arcsoft.sdk_demo.busRecordInfo;
import com.arcsoft.sdk_demo.model.BusModel;
import com.arcsoft.sdk_demo.model.DrawInfo;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.model.StuModel;
import com.arcsoft.sdk_demo.utils.Common;
import com.arcsoft.sdk_demo.utils.DrawHelper;
import com.arcsoft.sdk_demo.widget.FaceRectView;
import com.connect.busInfo;
import com.face.adapter.FaceAdapter;
import com.face.entity.Face;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tuacy.pinnedheader.PinnedHeaderItemDecoration;
import com.tuacy.pinnedheader.PinnedHeaderRecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

//import com.guo.android_extend.widget.CameraSurfaceView;



public class RecPage extends LazyLoadFragment  implements CameraSurfaceView.OnCameraListener  {
    @Nullable
    Runnable hide = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            mTextView.setAlpha(0.5f);
            mImageView.setImageAlpha(128);
            isPostted = false;
        }
    };



    public int line_id;

    private Application myApp;
    QMUITopBarLayout mQMTopBar;
    private int goorback;
    private int stuCount;
    private int titleCount;
    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private busRecordInfo reclog;
    private final String TAG = this.getClass().getSimpleName();
    private DrawHelper drawHelper;
    private FaceRectView faceRectView;
    List<DrawInfo> drawInfoList = new ArrayList<>();

    private FloatingNavigationView mFloatingNavigationView;
    private FloatingActionButton mFloatingActionButton;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


    private int mWidth, mHeight, mFormat;
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;
    QMUIEmptyView mEmptyView;


    private busInfo mBusInfo;
    private int linechecked;
    private TextView mTopBar;
    private TextView mGrooveView;
    private PinnedHeaderRecyclerView mPinRecyclerView;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();
    List<ASAE_FSDKAge> ages = new ArrayList<>();
    List<ASGE_FSDKGender> genders = new ArrayList<>();

    //xfeng add
    List<AFT_FSDKFace> FTresult = new ArrayList<>();
    int faceNum,iFace;
    public int mode;
    public String gpsinfo;

    private TextView mServerState, mTvReceive, mIp;
    private TextView mNumberCount;

    private String gettime;
    private String getdate;

    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;
    Handler mHandler;
    boolean isPostted = false;
    String faceInfo,faceName;
    private Common commonUtil=new Common();



    ///////////////////////////////
    ///////////////////////////////
    private TextView mTextView;
    private TextView mTextView1;
    private ImageView mImageView;
    private ImageButton mImageButton;
    private ImageButton mSwitchCamera;
    private Button mSwitchonoff;
    private Button mEnd;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    //xfeng
    public ListView lvFaces;
    public List<Face> faceList = new ArrayList<Face>();  //创建集合保存学生信息
    FaceAdapter faceAdapter;          //关联数据和子布局
    List <busRecordInfo.StudentlistBean> stuList=new ArrayList<busRecordInfo.StudentlistBean>();
    private String postxtlog="";
    private File logfile;
    private File logFileName;

    private int stuSize;

    TextToSpeech mSpeech;

    private View mView;

    public List<Face> getFaceList() {
        return faceList;
    }

    public class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();

        List<FaceDB.FaceRegist> mResgist = myApp.mFaceDB.mRegister;
        List<ASAE_FSDKFace> face1 = new ArrayList<>();
        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        //xfeng add
        AFT_FSDKFace tempFace = new AFT_FSDKFace();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        public void loop() {


            if (mImageNV21 != null) {
                //xfeng add for loop
                faceNum=FTresult.size();
                drawInfoList.clear();

                for (iFace=0;iFace<faceNum;iFace++) {

                     final int rotate = mCameraRotate;
                    long time = System.currentTimeMillis();

                    faceInfo="";

                    tempFace = FTresult.get(iFace).clone();
                    AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21,tempFace.getRect(), tempFace.getDegree(), result);

                    //AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                    Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                    Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                    AFR_FSDKMatching score = new AFR_FSDKMatching();
                    float max = 0.0f;
                    String name = null;
                    for (FaceDB.FaceRegist fr : mResgist) {


                        for (AFR_FSDKFace face : fr.mFaceList) {
                            error = engine.AFR_FSDK_FacePairMatching(result, face, score);

                            Log.d(TAG,  "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                            if (max < score.getScore()) {
                                max = score.getScore();
                                name = fr.mName;
                            }

                        }
                    }

                    //age & gender
                    face1.clear();
                    face2.clear();
                    //face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                    //face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                    face1.add(new ASAE_FSDKFace(tempFace.getRect(), tempFace.getDegree()));
                    face2.add(new ASGE_FSDKFace(tempFace.getRect(), tempFace.getDegree()));

                    ages.clear();
                    genders.clear();
                    ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                    ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                    //Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
                    //Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                    String age0="";
                    String gender0="";
                    if(ages.size()>0) {
                        age0 = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                    }
                    if(genders.size()>0) {
                        gender0 = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");
                    }

                    final String age=age0;
                    final String gender=gender0;



                    if(mImageNV21==null)
                    {
                        continue;
                    }

                    byte[] data = mImageNV21;

                    YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                    ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                    yuv.compressToJpeg(tempFace.getRect(), 80, ops);
                    final Bitmap bmp= BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                    try {
                        ops.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (max > 0.65f) {
                        //xfeng add
                        faceInfo += name;
                        //faceName=name;
                        if(iFace==faceNum)
                            iFace-=1;
                        //闪退
                        if(iFace<FTresult.size())
                            drawInfoList.add(new DrawInfo(FTresult.get(iFace).getRect(),gender,age,1,name));

                        //fr success.
                        final float max_score = max;
                        Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                        final String mNameShow = name;
                        mHandler.removeCallbacks(hide);
                        mHandler.post(new Runnable() {
                            @Override			public void run() {

                                mTextView.setAlpha(1.0f);
                                mTextView.setText(mNameShow.split("_")[1]);
                                mTextView.setTextColor(Color.RED);
                                mTextView1.setVisibility(View.VISIBLE);
                                mTextView1.setText("");
                                //mTextView1.setText("置信度：" + (float)((int)(max_score * 1000)) / 1000.0);
                                //faceInfo+="\n置信度：" + (float)((int)(max_score * 1000)) / 1000.0;
                                //mTextView1.setTextColor(Color.RED);
                                mImageView.setRotation(rotate);
                                if (mCameraMirror) {
                                    mImageView.setScaleY(-1);
                                }
                                mImageView.setImageBitmap(bmp);


                                getFace(mNameShow);
                            }
                        });
                    } else {
                        final String mNameShow = "未识别";
                        if(iFace==faceNum)
                            iFace-=1;
                        //闪退
                        if(iFace<FTresult.size())
                            drawInfoList.add(new DrawInfo(FTresult.get(iFace).getRect(),gender,age,1,null));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mTextView.setAlpha(1.0f);
                                mTextView1.setVisibility(View.VISIBLE);
                                mTextView1.setText( gender + "," + age);
                                mTextView1.setTextColor(Color.RED);
                                mTextView.setText(mNameShow);
                                mTextView.setTextColor(Color.RED);
                                //mImageView.setImageAlpha(255);
                                mImageView.setRotation(rotate);
                                if (mCameraMirror) {
                                    mImageView.setScaleY(-1);
                                }
                                mImageView.setImageBitmap(bmp);

                            }
                        });
                    }

                } // xfeng add for

                //setFace();


                mImageNV21 = null;

            }

        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private class TTSListener implements TextToSpeech.OnInitListener {
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
//                int supported = mSpeech.setLanguage(Locale.US);
//                if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
//                    Toast.makeText(MainActivity.this, "不支持当前语言！", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onInit: 支持当前选择语言");
//                }else{
//
//                }
                Log.i(TAG, "onInit: TTS引擎初始化成功");
            }
            else{
                Log.i(TAG, "onInit: TTS引擎初始化失败");
            }
        }
    }

    private DrawerLayout drawer;
    NavigationView navigationView;
    GridLayoutManager manager;
    QMUITipDialog tipDialog;
    @Override
    public void initView(View view){
        mView=view;
/*        drawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withRootView((ViewGroup) view.findViewById(R.id.rootView))
                .withDisplayBelowStatusBar(false)
                .withDrawerLayout(R.layout.material_drawer)
                .withDrawerGravity(Gravity.RIGHT)
                .buildForFragment();
        drawer.getDrawerLayout().setFitsSystemWindows(false);
        drawer.getSlider().setFitsSystemWindows(false);*/

        myApp=(Application) getActivity().getApplication();


        mSwitchCamera = (ImageButton) view.findViewById(R.id.switch_camera);
        mode=0;

        mGrooveView = view.findViewById(R.id.groove);
        mQMTopBar=view.findViewById(R.id.DetecterTopBar);
        mEnd=(Button)view.findViewById(R.id.btn_end);
        //mEnd.setOnClickListener(this);
        mNumberCount = (TextView) view.findViewById(R.id.numberCount);
        mNumberCount.setVisibility(View.INVISIBLE);

        lvFaces = (ListView)view.findViewById(R.id.lvFaces);   //获得子布局

        myApp.order_flag=commonUtil.getOrderFlag();


        stuCount=0;
        mGLSurfaceView = (CameraGLSurfaceView) view.findViewById(R.id.glsurfaceView);

        mSurfaceView = (CameraSurfaceView) view.findViewById(R.id.surfaceView);
        //snap
        mTextView = (TextView)view.findViewById(R.id.textView);
        mTextView.setText("");
        mTextView1 = (TextView) view.findViewById(R.id.textView1);
        mTextView1.setText("");

        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mImageButton = (ImageButton) view.findViewById(R.id.imageButton);

        /////////////////////////
        /**
         * initial FSDK
         */

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
        Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());

        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
        Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());


/*        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Snackbar.make((View) navigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                mFloatingNavigationView.close();
                return true;
            }
        });*/

        ///////////////////////
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick (View v){
                switch (v.getId()) {
                    case R.id.switch_camera:
                        if(Camera.getNumberOfCameras()<=1) return;
                        if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                            mCameraRotate = 270;
                            mCameraMirror = true;
                        } else {
                            mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                            mCameraRotate = 90;
                            mCameraMirror = false;
                        }
                        mSurfaceView.resetCamera();
                        mGLSurfaceView.setRenderConfig(mCameraRotate, mCameraMirror);
                        mGLSurfaceView.getGLES2Render().setViewAngle(mCameraMirror, mCameraRotate);
                        break;
                }
            }
        };

        mSwitchCamera.setOnClickListener(listener);

        View.OnTouchListener touchlistener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.surfaceView:

                        break;
                }
                return false;
            }

        };
        mCameraID = 0;
        mCameraRotate = 90;
        mCameraMirror = false;
        mFormat = ImageFormat.NV21;
        mHandler = new Handler();

        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        mSurfaceView.debug_print_fps(true, true);
        mGLSurfaceView.setOnTouchListener(touchlistener);
        mSurfaceView.setOnCameraListener(this);

    }
    protected void lazyLoad() {
        String message = "Fragment2" + (isInit ? "已经初始并已经显示给用户可以加载数据" : "没有初始化不能加载数据");
        //showToast(message);
        Log.d(TAG, message);
        ////
        mBusInfo=myApp.mBusInfo;
        reclog=myApp.buslog;
        if(reclog==null)
            reclog=new busRecordInfo();
        stu.clear();
        stu.addAll(myApp.stuLine);
        /////////



        setFace();
        initTopBar(myApp.mBusInfo.getLine().get(linechecked).getLinename() + "（" + stu.size() + "人）"+ commonUtil.getOrderFlagString());

        ///////////////////////////


        /////////////////////////
        drawer=(DrawerLayout)mView.findViewById(R.id.material_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mEmptyView=(QMUIEmptyView)navigationView.findViewById(R.id.emptyView);
        mFloatingActionButton=mView.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mEmptyView.show(true);
                if(mPinRecyclerView!=null)
                    mPinRecyclerView.setVisibility(View.INVISIBLE);
                if (!drawer.isDrawerOpen(GravityCompat.END))
                    drawer.openDrawer(GravityCompat.END);
                else
                    drawer.closeDrawer(GravityCompat.END);
                /////////////////////
            }
        });


        manager = new GridLayoutManager(navigationView.getContext(), 4);
        mGridRecyclerAdapter = new GridRecyclerAdapter(stu, getActivity(), 0);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                /**
                 * 抽屉滑动时，调用此方法
                 * */
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                /**
                 * 抽屉被完全展开时，调用此方法
                 * */

                mSurfaceView.stopPreview();
                //initEvent();
                obtainData();

                Button mUploadBtn = navigationView.findViewById(R.id.upload);


                mUploadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new QMUIDialog.MessageDialogBuilder(getActivity())
                                .setTitle("上传记录")
                                .setMessage("确定上传吗？")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                        Common common = new Common();
                                        String dt = common.getDateTime();

                                        myApp.stuModel=new StuModel(myApp,stu);
                                        myApp.stuModel.calculateCount();

                                        busRecordInfo uploadInfo = new busRecordInfo();
                                        uploadInfo.setBusId(reclog.getBusId());
                                        uploadInfo.setOrder(reclog.getOrder());
                                        uploadInfo.setLineId(reclog.getLineId());
                                        uploadInfo.setBusnumber(reclog.getBusnumber());
                                        uploadInfo.setChengcheCount(myApp.stuModel.getShowup());
                                        uploadInfo.setWeichengCount(myApp.stuModel.getAbsent());
                                        uploadInfo.setQingjiaCount(myApp.stuModel.getLeave());
                                        uploadInfo.setTotalCount(myApp.stuModel.getTotal());
                                        uploadInfo.setDatetime(dt);
                                        uploadInfo.setDate(common.getDay());

                                        List<busRecordInfo.StudentlistBean> bltmp1 = new ArrayList<busRecordInfo.StudentlistBean>();
                                        List<busRecordInfo.StudentlistBean> bltmp2 = new ArrayList<busRecordInfo.StudentlistBean>();
                                        int flag = 0;


                                        for (StuInfo s : stu) {

                                            if (s.status != 4) {
                                                flag = 0;
                                                //添加自动识别学生
                                                if (reclog.getStudentlist() != null) {
                                                    for (busRecordInfo.StudentlistBean b : reclog.getStudentlist()) {
                                                        if (s.stuId == b.getStuid()) {
                                                            if(s.status==1) {
                                                                bltmp1.add(b);
                                                                bltmp2.add(b);
                                                                flag = 1;
                                                            }
                                                        }
                                                    }
                                                }
                                                //添加手动设置的学生
                                                if (flag == 0) {
                                                    busRecordInfo.StudentlistBean btmp = new busRecordInfo.StudentlistBean();
                                                    btmp.setStuid(s.stuId);
                                                    btmp.setStuname(s.stuName);
                                                    btmp.setDatetime(dt);
                                                    btmp.setDate(new Date().getDate());
                                                    btmp.setStatus(s.status);
                                                    if (s.status == 1) {
                                                        btmp.setReason("手动");
                                                        bltmp1.add(btmp);
                                                    }
                                                    bltmp2.add(btmp);
                                                }
                                            }
                                        }
                                        uploadInfo.setStudentlist(bltmp1);
                                        if (uploadInfo.getStudentlist().size() == 0) {
                                            //Toast.makeText(getActivity(), "无学生乘车！", Toast.LENGTH_SHORT).show();
                                            final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                                    .setTipWord("无学生乘车，请勿提交")
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
                                            //保存文件
                                            if (logfile.exists()) {
                                                Log.i("myTag", "文件存在");
                                                logfile.delete();
                                            }
                                            try {
                                                FileOutputStream fileOutputStream = new FileOutputStream(logfile);
                                                fileOutputStream.write(commonUtil.toJson(uploadInfo, 1).getBytes());
                                                // fileOutputStream.write(sbString.getBytes());
                                                fileOutputStream.close();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            uploadInfo.setStudentlist(bltmp2);
//                                            mFloatingNavigationView.close();
                                            BusModel busModel = new BusModel(getActivity(), stu);
                                            busModel.sendData(commonUtil.toJson(uploadInfo, 1), navigationView, logfile);
                                        }
                                    }
                                })
                                .create(mCurrentDialogStyle).show();

                    }
                });

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                /**
                 * 抽屉被完全关闭时，调用此方法
                 * */
                mSurfaceView.startPreview();
                stuToLog();

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                /**
                 * 抽屉状态改变时，调用此方法
                 * */
            }
        });

        if(mSpeech==null) {
            mSpeech = new TextToSpeech(getActivity(), new TTSListener());
            mSpeech.setSpeechRate(1.5f);//最快语速1.5
            mSpeech.setPitch(1.2f);//最高音调2.0
            mSpeech.setLanguage(Locale.CHINA);
        }
        //mSpeech.speak("hello",TextToSpeech.QUEUE_ADD,null,null);

        ////////////////////////////////////////////



        //更换线路后，更新界面
        if(myApp.mBusInfo.getLinechecked()!=-1 && myApp.mBusInfo.getLinechecked()!=myApp.lastLine){
            faceList.clear();
            lvFaces.setAdapter(faceAdapter);
            myApp.buslog=new busRecordInfo();
            reclog=new busRecordInfo();
            mNumberCount.setText("0");

            myApp.lastLine=myApp.mBusInfo.getLinechecked();
        }

        line_id=myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLineid();
        logfile=new File(commonUtil.getLogFile(myApp.mBusInfo.getLinechecked(),line_id,myApp));

        /*if(myApp.mBusInfo.getLinechecked()==-1){
            chooseLine();
        }

        else {*/

            if (commonUtil.isBuslog(myApp.mBusInfo.getLinechecked(),line_id,myApp)
                    || commonUtil.isUploaded(myApp.mBusInfo.getLinechecked(),line_id,myApp)){
            //if (myApp.mBusStatus.online != -1 && myApp.mBusStatus.reReadData == true) {
                mNumberCount.setVisibility(View.VISIBLE);
                initTopBar(myApp.mBusInfo.getLine().get(linechecked).getLinename() + "（" + stu.size() + "人）"+ commonUtil.getOrderFlagString());
                reInitData();
            } else {

                    linechecked = myApp.mBusInfo.getLinechecked();
                    reclog.setBusnumber(mBusInfo.getLine().get(linechecked).getBusnumber());
                    reclog.setBusId(mBusInfo.getLine().get(linechecked).getBusid());
                    reclog.setLineId(mBusInfo.getLine().get(linechecked).getLineid());
                    reclog.setOrder(myApp.order_flag);

                    myApp.buslog = reclog;
                    initTopBar(myApp.mBusInfo.getLine().get(linechecked).getLinename() + "（" + stu.size() + "人）"+ commonUtil.getOrderFlagString());

                    if(mCamera==null)
                        mSurfaceView.resetCamera();
                    mSurfaceView.startPreview();
                    if(mFRAbsLoop==null)
                        mFRAbsLoop = new FRAbsLoop();
                    if(mFRAbsLoop.getState().toString()!="RUNNABLE")
                        mFRAbsLoop.start();
                }

        //}

    }


    @Override
    protected void stopLoad() {
        mSurfaceView.stopPreview();
        if(drawer!=null)
            if (drawer.isDrawerOpen(GravityCompat.END))
                drawer.closeDrawer(Gravity.END);

        mSpeech.stop();
        mSpeech.shutdown();
        mSpeech = null;
    }
    protected void onFragmentFirstUnVisible() {

//        mSurfaceView.stopPreview();
    }

    public void onStart() {
        super.onStart();
        if(mSurfaceView!=null && mCamera!=null) {
            mSurfaceView.resetCamera();
            mSurfaceView.startPreview();
        }
    }

    public void onResume() {
        super.onResume();
        if(mFRAbsLoop!=null)
        Log.d(TAG,"mFRAbsLoop state:"+mFRAbsLoop.getState().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mFloatHandler.removeCallbacks(mObtainDataRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void getFace(final String name) {
        int isNewFace=0;


        for(Face fl: faceList){                  //将数据添加到集合中
            if(name.equals(fl.getImageName())){// && mode==fl.getMode()){
                isNewFace=1;
            }
            //if(name==fl.getImageName()){// && mode!=fl.getMode()){
            //  isNewFace=0;
            // }
        }
        if(isNewFace==0  )
        {
            if(mNumberCount.getVisibility()==View.INVISIBLE)
                mNumberCount.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss//获取当前时间
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss//获取当前时间
            Date date = new Date(System.currentTimeMillis());
            gettime=simpleDateFormat.format(date);
            getdate=simpleDateFormat2.format(date);
            //Toast.makeText(this, gettime, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, gpsinfo, Toast.LENGTH_SHORT).show();

            faceList.add(new Face(name,gettime,mode,gpsinfo));  //将图片id和对应的name存储到一起
            lvFaces.setAdapter(faceAdapter);


            busRecordInfo.StudentlistBean stuLog=new busRecordInfo.StudentlistBean();
            stuLog.setDate(date.getDate());
            stuLog.setDatetime(gettime);
            stuLog.setPostxt(myApp.postxtlog);
            stuLog.setStuid(Integer.parseInt(name.split("_")[0]));
            stuLog.setStuname(name.split("_")[1]);
            stuLog.setStatus(1);
            //stuLog.setReason("自动");

            stuList.add(stuLog);

            reclog.setStudentlist(stuList);
            reclog.setDatetime(getdate);


            /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(myApp, notification);
            r.play();*/
            //语音播报

            stuCount++;
            //myApp.stuModel.addShowup(1);
            mNumberCount.setText(String.valueOf(stuList.size()));

            if(stuCount>0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //写出记录


                        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {

                            mSpeech.speak(name.split("_")[1], TextToSpeech.QUEUE_FLUSH, null, null);

                        }else{

                            mSpeech.speak(name.split("_")[1], TextToSpeech.QUEUE_FLUSH, null);

                        }

                        if (logfile.exists()) {
                            Log.i("myTag", "文件存在");
                            logfile.delete();
                        }
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(logfile);
                            fileOutputStream.write(commonUtil.toJson(reclog, 1).getBytes());
                            // fileOutputStream.write(sbString.getBytes());
                            fileOutputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }).start();
            }

        }


    }
    public void setFace() {

        faceAdapter= new FaceAdapter(getActivity(),R.layout.listview_item, faceList,mCameraRotate,lvFaces);
        lvFaces.setAdapter(faceAdapter);                   //绑定数据和适配器

        lvFaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //点击每一行的点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) {
                Face face=faceList.get(position);         //获取点击的那一行
                // Toast.makeText(DetecterActivity.this,fruit.getGpsinfo(),Toast.LENGTH_LONG).show();//使用吐司输出点击那行学生的名字
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //    设置Title的图标
                //builder.setIcon(R.drawable.ic_launcher);
                //    设置Title的内容
                builder.setTitle("详细信息");
                //    设置Content来显示一个信息
                builder.setMessage(myApp.GPSinfo);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });
    }
    private void initTopBar(String title) {

        mQMTopBar.setTitle(title);
        mQMTopBar.setBackgroundColor(0xff00A8E1);
    }
    public static  Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for(Camera.Size size : preSizeList){
            if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }


    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera = Camera.open(mCameraID);
        try {

            WindowManager windowManager = getActivity().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            //获取屏幕的宽和高
            display.getMetrics(metrics);
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;

            Log.d(TAG, "xfengwidth:" + screenWidth + "x" + screenHeight);

            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size preSize = getCloselyPreSize(true, screenWidth, screenHeight, parameters.getSupportedPreviewSizes());

            mWidth=preSize.width;
            mHeight=preSize.height;
            mWidth=screenWidth;
            mHeight=screenHeight;


            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewFormat(mFormat);


            for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for( Integer format : parameters.getSupportedPreviewFormats()) {
                Log.d(TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for(int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
            parameters.setPreviewFpsRange(15000, 30000);
            parameters.setExposureCompensation(parameters.getExposureCompensation());
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);

            drawHelper = new DrawHelper(mHeight,mWidth,  mGLSurfaceView.getWidth(), mGLSurfaceView.getHeight()
                    , mCameraRotate , mCameraID, mCameraMirror);


            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
        }
        return mCamera;
    }

    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {


       /* if(myApp.isFirstLoad){
            mSurfaceView.stopPreview();
            myApp.isFirstLoad=false;
            return new Rect[0];
        }*/
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        if (mImageNV21 == null && data!=null) {
            if (!result.isEmpty()) {
                //xfeng add
                FTresult.clear();
                for (AFT_FSDKFace object : result) {
                    FTresult.add(object);
                }
                //xfeng

                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
                if (!isPostted) {
                    mHandler.removeCallbacks(hide);
                    mHandler.postDelayed(hide, 2000);
                    isPostted = true;
                }
            }
        }
        //copy rects
        List<DrawInfo> drawInfoList2 = new ArrayList<>();
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
            //处理前置摄像头识别 方框垂直方向镜像 xfeng
            if(mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                rects[i].left=width-rects[i].left;
                rects[i].right=width-rects[i].right;

            }
            //drawInfoList2.add(new DrawInfo(rects[i],"","",1,"测试"));
        }

        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    public void setupChanged(int format, int width, int height) {

    }
    public boolean startPreviewImmediately() {
        return true;
    }
    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[])data.getParams(), Color.GREEN, 5);
        //if (drawInfoList != null && faceRectView != null && drawHelper != null) {
          //  drawHelper.draw(faceRectView, drawInfoList);
        //}
    }

    public boolean onTouch(View v, MotionEvent event) {
        //CameraHelper.touchFocus(mCamera, event, v, this);
        return false;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!");
            camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦
        }
    }


    private busRecordInfo getBuslog() {

        String jsonData="";

        try {
            FileInputStream fileInputStream = new FileInputStream(logfile);
            byte[] b=new byte[fileInputStream.available()];

            //将字节流中的数据传递给字节数组
            fileInputStream.read(b);

            //将字节数组转为字符串
            jsonData=new String(b); } catch (Exception e) {
            e.printStackTrace(); }
            Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<busRecordInfo>() {}.getType();
        busRecordInfo info=gson.fromJson(jsonData,busRecordInfo.class);
        if(info.getStudentlist()==null)
            info.setStudentlist(new ArrayList<busRecordInfo.StudentlistBean>());

        stuList.clear();
        stuList=info.getStudentlist();
        return info;

    }


    public void reInitData() {

        String goorbackstring = commonUtil.getOrderFlagString();
        logfile=new File(commonUtil.getLogFile(myApp.mBusInfo.getLinechecked(),line_id,myApp));

        myApp.buslog = getBuslog();
        reclog = myApp.buslog;
        if (reclog == null)
            reclog = new busRecordInfo();

        String lineName = "";
        lineName = myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLinename();
        final String[] items;
        if(commonUtil.isUploaded(myApp.mBusInfo.getLinechecked(),line_id,myApp)){
            items = new String[3];
            items[0]="首页";
            items[1]="历史记录";
            items[2]="继续识别";
            final Main2Activity mainActivity = (Main2Activity) getActivity();
            new AlertDialog.Builder(getActivity())
                   // .setTitle(lineName + ":" + goorbackstring)
                    .setTitle("已乘车" + reclog.getStudentlist().size() + "人（已上传）")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;
                            Uri data;
                            switch (which) {
                                case 0:
                                    mainActivity.setFragmentSkipInterface(new Main2Activity.FragmentSkipInterface() {
                                        @Override
                                        public void gotoFragment(ViewPager viewPager) {
                                            /** 跳转到第三个页面的逻辑 */
                                            viewPager.setCurrentItem(0);
                                        }
                                    });
                                    /** 进行跳转 */
                                    mainActivity.skipToFragment();
                                    break;
                                case 1:
                                    mainActivity.setFragmentSkipInterface(new Main2Activity.FragmentSkipInterface() {
                                        @Override
                                        public void gotoFragment(ViewPager viewPager) {
                                            /** 跳转到第三个页面的逻辑 */
                                            viewPager.setCurrentItem(2);
                                        }
                                    });
                                    /** 进行跳转 */
                                    mainActivity.skipToFragment();
                                    break;
                                case 2:
                                    mSurfaceView.startPreview();
                                    resetData();
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                    .show();
        }
        else {
            items = new String[3];
            items[0]="首页";
            items[1]="历史记录";
            items[2]="继续识别";
            final Main2Activity mainActivity = (Main2Activity) getActivity();
            new AlertDialog.Builder(getActivity())
                    //.setTitle(lineName + ":" + goorbackstring)
                    .setTitle("已乘车" + String.valueOf(reclog.getStudentlist().size()) + "人（未上传）")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;
                            Uri data;
                            switch (which) {
                                case 0:
                                    mainActivity.setFragmentSkipInterface(new Main2Activity.FragmentSkipInterface() {
                                        @Override
                                        public void gotoFragment(ViewPager viewPager) {
                                            /** 跳转到第三个页面的逻辑 */
                                            viewPager.setCurrentItem(0);
                                        }
                                    });
                                    /** 进行跳转 */
                                    mainActivity.skipToFragment();
                                    break;
                                case 1:
                                    mainActivity.setFragmentSkipInterface(new Main2Activity.FragmentSkipInterface() {
                                        @Override
                                        public void gotoFragment(ViewPager viewPager) {
                                            /** 跳转到第三个页面的逻辑 */
                                            viewPager.setCurrentItem(2);
                                        }
                                    });
                                    /** 进行跳转 */
                                    mainActivity.skipToFragment();
                                    break;
                                case 2:
                                    resetData();
                                    mSurfaceView.startPreview();
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                    .show();
        }





    }


    private void resetData() {
        mHandler = new Handler();
        if(reclog.getStudentlist()!=null) {
            faceList.clear();
            for (int i = 0; i < reclog.getStudentlist().size(); i++) {
                faceList.add(new Face(reclog.getStudentlist().get(i).getStuid() + "_"
                        + reclog.getStudentlist().get(i).getStuname(), reclog.getStudentlist().get(i).getDatetime()
                        , mode, reclog.getStudentlist().get(i).getPostxt()));  //将图片id和对应的name存储到一起
                mNumberCount.setText(String.valueOf(stuList.size()));
            }
            lvFaces.setAdapter(faceAdapter);
        }

        LogToStu();

        if(mFRAbsLoop==null)
            mFRAbsLoop = new FRAbsLoop();
        if(mFRAbsLoop.getState().toString()!="RUNNABLE")
            mFRAbsLoop.start();

    }



    public int setContentView() {
        return R.layout.material_drawer;
       // return R.layout.fragment_rec;
    }


    /**
     * pin recycleview
     */
    private List<StuInfo> stu0 = new ArrayList<StuInfo>();
    private GridRecyclerAdapter mGridRecyclerAdapter;


    private void initEvent() {
        mPinRecyclerView.setOnPinnedHeaderClickListener(new PinnedHeaderRecyclerView.OnPinnedHeaderClickListener() {
            @Override
            public void onPinnedHeaderClick(int adapterPosition) {
                //Toast.makeText(getActivity(), "点击了悬浮标题 position = " + adapterPosition, LENGTH_SHORT).show();
            }
        });
    }
    private Runnable mObtainDataRunnable;
    private Runnable mStuToReclogRunnable;
    private Handler mFloatHandler =new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            int what=msg.what;
            Log.i("handler","已经收到消息，消息what："+what+",id:"+Thread.currentThread().getId());

            if(what==1) //logtostu，识别学生转为统计学生
            {
                Log.i("handler已接受到消息",""+what);
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (mGridRecyclerAdapter.isPinnedPosition(position)) {
                            return manager.getSpanCount();
                        } else {
                            return 1;
                        }
                    }
                });
                mPinRecyclerView = navigationView.findViewById(R.id.recycler_grid);
                mPinRecyclerView.setVisibility(View.VISIBLE);
                mPinRecyclerView.setLayoutManager(manager);
                mPinRecyclerView.setAdapter(mGridRecyclerAdapter);
                mPinRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration());
                mEmptyView.hide();
            }
            if(what==2){  //stutolog，统计学生的手动设置转为识别的已乘学生
                mNumberCount.setVisibility(View.VISIBLE);
                mNumberCount.setText(String.valueOf(stuList.size()));
                lvFaces.setAdapter(faceAdapter);
                mFloatHandler.removeCallbacks(mStuToReclogRunnable);
            }
        }
    };


    //从log读入数据，然后把log赋给stu，并且分组并加入统计信息
    private void LogToStu() {

        int flag = 0;
        int count1 = 0, count2 = 0, count3 = 0;
        stu.clear();
        stu.addAll(myApp.stuLine);


        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i).isLeave) {//请假学生
                stu.get(i).isTaken = false;
                stu.get(i).status = 2;
            }

            //乘车学生
            flag = 0;
            if (reclog.getStudentlist() != null) {
                for (busRecordInfo.StudentlistBean b : reclog.getStudentlist()) {
                    if (b.getStuid() == stu.get(i).stuId) {//乘车学生
                        stu.get(i).isTaken = true;
                        stu.get(i).status = 1;
                        stu.get(i).isAuto=true;
                        //stu1.add(stu0.get(i));
                        flag = 1;
                        count1++;
                        break;
                    }
                }
            }

            if (flag == 0 && !stu.get(i).isLeave) {//未乘车学生
                stu.get(i).isTaken = false;
                stu.get(i).status = 3;
                count3++;
                //stu3.add(stu0.get(i));
            }

        }
        myApp.stuLine.clear();
        myApp.stuLine.addAll(stu);

        count2 = stu.size() - count1 - count3;
               /* stu0.clear();
                stu0.addAll(stu);*/


        Collections.sort(stu);
        int flag1 = 1, flag2 = 1, flag3 = 1;
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i).status == 1 && flag1 == 1) {
                stu.add(i, new StuInfo("已乘车学生(" + count1 + "人)", 4));
                flag1 = 0;
            } else if (stu.get(i).status == 2 && flag2 == 1) {
                stu.add(i, new StuInfo("已请假学生(" + count2 + "人)", 4));
                flag2 = 0;
            } else if (stu.get(i).status == 3 && flag3 == 1) {
                stu.add(i, new StuInfo("未乘车未请假学生(" + count3 + "人)", 4));
                flag3 = 0;
            }
        }

    }

    private void obtainData() {

        mObtainDataRunnable=new Runnable() {
            @Override
            public void run() {

                LogToStu();

                Message msg = new Message();
                //对消息一个识别号，便于handler能够识别
                msg.what = 1;
                mFloatHandler.sendMessage(msg);

            }

        };

        mFloatHandler.post(mObtainDataRunnable);

    }

    //drawer手动设置后，返回recpage，然后将值写入log
    private void stuToLog() {
        if(mGridRecyclerAdapter.isManuel==0)
            return;
        if(mGridRecyclerAdapter.count1==0 && stuList.size()==0)
            return;

        mStuToReclogRunnable=new Runnable() {
            @Override
            public void run() {

                int flag = 0;
                List <busRecordInfo.StudentlistBean> tmpstulist=new ArrayList<busRecordInfo.StudentlistBean>();
                Common common = new Common();
                String dt = common.getDateTime();

                tmpstulist.addAll(stuList);
                faceList.clear();
                if(stuList!=null)
                    stuList.clear();
                if(reclog.getStudentlist()!=null)
                    reclog.getStudentlist().clear();
                for (StuInfo s : stu) {
                    if (s.status ==1) {
                        flag = 0;
                        //status==1而且在识别列表里，直接加入stulist

                        for (int i = 0; i < tmpstulist.size(); i++) {
                            if (s.stuId == tmpstulist.get(i).getStuid()) {
                                stuList.add(tmpstulist.get(i));
                                faceList.add(new Face(tmpstulist.get(i).getStuid() + "_"
                                        + tmpstulist.get(i).getStuname(), tmpstulist.get(i).getDatetime()
                                        , mode, tmpstulist.get(i).getPostxt()));  //乘车列表
                                flag = 1;
                                break;
                            }
                        }
                        //status==1但是不在识别列表里，设置为手动，并新建对象，加入stulist
                        if (flag == 0) {
                            busRecordInfo.StudentlistBean btmp = new busRecordInfo.StudentlistBean();
                            btmp.setStuid(s.stuId);
                            btmp.setStuname(s.stuName);
                            btmp.setDatetime(dt);
                            btmp.setDate(new Date().getDate());
                            btmp.setStatus(s.status);
                            btmp.setReason("手动");
                            stuList.add(btmp);
                            faceList.add(new Face(btmp.getStuid() + "_"
                                    + btmp.getStuname(), btmp.getDatetime()
                                    , mode, btmp.getPostxt()));  //乘车列表
                        }
                    }
                }

                reclog.setStudentlist(stuList);

                if (logfile.exists()) {
                    logfile.delete();
                }
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(logfile);
                    fileOutputStream.write(commonUtil.toJson(reclog, 1).getBytes());
                    // fileOutputStream.write(sbString.getBytes());
                    fileOutputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                //对消息一个识别号，便于handler能够识别
                msg.what = 2;
                mFloatHandler.sendMessage(msg);

            }
        };
        mFloatHandler.post(mStuToReclogRunnable);

    }

}
