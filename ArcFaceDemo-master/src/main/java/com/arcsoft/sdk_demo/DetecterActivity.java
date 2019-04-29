package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.arcsoft.sdk_demo.CameraSurfaceView.OnCameraListener;
import com.arcsoft.sdk_demo.location.NotificationUtils;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.connect.GetIpAddress;
import com.connect.Translation;
import com.connect.busInfo;
import com.connect.interfaceHttp;
import com.face.adapter.FaceAdapter;
import com.face.entity.Face;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.tools.CameraHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class DetecterActivity extends Activity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback, View.OnClickListener {
    private Application myApp;
	QMUITopBarLayout mQMTopBar;

    private int goorback;
    private int onoroff;
    private int stuCount;
    private int titleCount;
    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private busRecordInfo blog;

    private final String TAG = this.getClass().getSimpleName();

	private int mWidth, mHeight, mFormat;
	private CameraSurfaceView mSurfaceView;
	private CameraGLSurfaceView mGLSurfaceView;
	private Camera mCamera;
//xfeng gps
	private LocationClient mClient;
	private MyLocationListener myLocationListener = new MyLocationListener();
	private BaiduMap mBaiduMap;
	private NotificationUtils mNotificationUtils;
	private Notification notification;
	private boolean isFirstLoc = true;


	private busInfo mBusInfo;
	private int linechecked;
	TextView mTopBar;

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

    //xfengadd  local connection
    private SharedPreferences mSharedPreferences;
    private final int DEFAULT_PORT = 8086;
    private int mServerPort; //服务端端口

    private ServerSocket mServerSocket;
    public Socket mSocket;

    private static final String SERVER_PORT = "server_port";
    private static final String SERVER_MESSAGETXT = "server_msgtxt";
    private OutputStream mOutStream;
    private InputStream mInStream;
    private SocketAcceptThread mAcceptThread;
    private SocketReceiveThread mReceiveThread;
    private SocketSendThread mSendThread;

    private final int STATE_CLOSED = 1;
    private final int STATE_ACCEPTING= 2;
    private final int STATE_CONNECTED = 3;
    private final int STATE_DISCONNECTED = 4;

    private int mSocketConnectState = STATE_CLOSED;

    private String mRecycleMsg;
    private static final int MSG_TIME_SEND = 1;
    private static final int MSG_SOCKET_CONNECT = 2;
    private static final int MSG_SOCKET_DISCONNECT = 3;
    private static final int MSG_SOCKET_ACCEPTFAIL = 4;
    private static final int MSG_RECEIVE_DATA = 5;
    private static final int MSG_SEND_DATA = 6;

    private TextView mServerState, mTvReceive, mIp;
    private TextView mNumberCount;

    private String gettime;



    int mCameraID;
	int mCameraRotate;
	boolean mCameraMirror;
	byte[] mImageNV21 = null;
	FRAbsLoop mFRAbsLoop = null;
	AFT_FSDKFace mAFT_FSDKFace = null;
	Handler mHandler;
	boolean isPostted = false;
	String faceInfo,faceName;

	Runnable hide = new Runnable() {
		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void run() {
			mTextView.setAlpha(0.5f);
			mImageView.setImageAlpha(128);
			isPostted = false;
		}
	};
    private Handler mSocketHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_TIME_SEND:
                    writeMsg(mRecycleMsg);
                    break;
                case MSG_SOCKET_CONNECT:
                    mSocketConnectState = STATE_CONNECTED;
                    mServerState.setText(R.string.state_connected);
                    mReceiveThread = new SocketReceiveThread();
                    mReceiveThread.start();
                    break;
                case MSG_SOCKET_DISCONNECT:
                    mSocketConnectState = STATE_DISCONNECTED;
                    mServerState.setText(R.string.state_disconect_accept);
                    startAccept();
                    break;
                case MSG_SOCKET_ACCEPTFAIL:
                    startAccept();
                    break;
                case MSG_RECEIVE_DATA:
                    /*if((String)msg.obj=="cameraon") {
                        onoroff_camera = 0;
                        switchCamera(onoroff_camera);
                    }
                    if((String)msg.obj=="cameraoff") {
                        onoroff_camera = 1;
                        switchCamera(onoroff_camera);
                    }*/
                    String text = mTvReceive.getText().toString() +"\r\n" + (String)msg.obj;
                    mTvReceive.setText(text);
                    break;
                case MSG_SEND_DATA:
                   // mSendThread.threadExit();
                    break;
                default:
                    break;
            }
        };
    };

	class FRAbsLoop extends AbsLoop {

		AFR_FSDKVersion version = new AFR_FSDKVersion();
		AFR_FSDKEngine engine = new AFR_FSDKEngine();
		AFR_FSDKFace result = new AFR_FSDKFace();

		List<FaceDB.FaceRegist> mResgist = ((Application)DetecterActivity.this.getApplicationContext()).mFaceDB.mRegister;
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

		//xfeng add Facelist
		//private String[] xfdata = { "Apple" };
		//ArrayAdapter<String> adapter;
		//ListView nameList = (ListView) findViewById(R.id.list_view);


		///xfeng add
		@Override
		public void loop() {


			if (mImageNV21 != null) {
			    //xfeng add for loop
			    faceNum=FTresult.size();

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

				ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
				ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
				Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
				Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
				final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
				final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");


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

                    //fr success.
                    final float max_score = max;
					Log.d(TAG, "fit Score:" + max + ", NAME:" + name);
                    final String mNameShow = name;
					mHandler.removeCallbacks(hide);
					mHandler.post(new Runnable() {
						@Override			public void run() {
							mTextView.setAlpha(1.0f);
							mTextView.setText(mNameShow);
							mTextView.setTextColor(Color.RED);
							mTextView1.setVisibility(View.VISIBLE);
							mTextView1.setText("置信度：" + (float)((int)(max_score * 1000)) / 1000.0);
                            faceInfo+="\n置信度：" + (float)((int)(max_score * 1000)) / 1000.0;
							mTextView1.setTextColor(Color.RED);
							mImageView.setRotation(rotate);
							if (mCameraMirror) {
								mImageView.setScaleY(-1);
							}
							//mImageView.setImageAlpha(255);
							mImageView.setImageBitmap(bmp);



                            //xfdata[0]=mName;
							//adapter = new ArrayAdapter<String>(DetecterActivity.this, android.R.layout.simple_list_item_1, xfdata);
							//nameList.setAdapter(adapter);
							getFace(mNameShow);
                            //flushFace();
						}
					});
				} else {
					final String mNameShow = "未识别";
					DetecterActivity.this.runOnUiThread(new Runnable() {
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
    public List<Face> faceList = new ArrayList<Face>();  //创建集合保存水果信息
    FaceAdapter faceAdapter;          //关联数据和子布局
	List <busRecordInfo.StudentlistBean> stuList=new ArrayList<busRecordInfo.StudentlistBean>();
	private String postxtlog="";

    public void getFace( String name) {
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
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
			rt.play();

        	if(mNumberCount.getVisibility()==View.INVISIBLE)
				mNumberCount.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
            Date date = new Date(System.currentTimeMillis());
            gettime=simpleDateFormat.format(date);
            //Toast.makeText(this, gettime, Toast.LENGTH_SHORT).show();
			//Toast.makeText(this, gpsinfo, Toast.LENGTH_SHORT).show();

			faceList.add(new Face(name,gettime,mode,gpsinfo));  //将图片id和对应的name存储到一起
			lvFaces.setAdapter(faceAdapter);


			busRecordInfo.StudentlistBean stuLog=new busRecordInfo.StudentlistBean();
			stuLog.setDate(date.getDate());
			stuLog.setDatetime(gettime);
			stuLog.setPostxt(postxtlog);
			stuLog.setStuid(Integer.parseInt(name.split("_")[0]));
			stuLog.setStuname(name.split("_")[1]);
			stuLog.setStatus(1);

			stuList.add(stuLog);

			blog.setStudentlist(stuList);


            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            stuCount++;
            mNumberCount.setText(String.valueOf(stuCount));


			String lastbuslogpath = myApp.mPath + "/lastbuslog.json";
			File lastbuslogfile = new File(lastbuslogpath);
			if (lastbuslogfile.exists()) {
				Log.i("myTag", "文件存在");
				lastbuslogfile.delete();
			}
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(lastbuslogfile);
				fileOutputStream.write(toJson(blog,1).getBytes());
				// fileOutputStream.write(sbString.getBytes());
				fileOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}


        }


    }
    public void setFace() {
        lvFaces = (ListView) findViewById(R.id.lvFaces);   //获得子布局
        faceAdapter= new FaceAdapter(DetecterActivity.this,R.layout.listview_item, faceList,mCameraRotate,lvFaces);
        lvFaces.setAdapter(faceAdapter);                   //绑定数据和适配器

        lvFaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //点击每一行的点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) {
                Face fruit=faceList.get(position);         //获取点击的那一行
               // Toast.makeText(DetecterActivity.this,fruit.getGpsinfo(),Toast.LENGTH_LONG).show();//使用吐司输出点击那行水果的名字
				AlertDialog.Builder builder = new AlertDialog.Builder(DetecterActivity.this);
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
    public void flushFace() {

    	faceAdapter.notifyDataSetChanged();

    }

	private void sendGetOnOrOFF(String onoroff, String name,int getoo) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

		//步骤4:创建Retrofit对象
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://xfeng.gz01.bdysite.com/bus/") //http://fy.iciba.com/
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		// 步骤5:创建 网络请求接口 的实例
		interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<Translation> call;
        if(onoroff=="on")
		    call = request.getOn("on",name,getoo,gettime);
        else
            call = request.getOff("off",name,getoo,gettime);


		//步骤6:发送网络请求(异步)
		call.enqueue(new Callback<Translation>() {
			@Override
			public void onResponse(Call<Translation> call, Response<Translation> response) {
				Log.e("xfeng", "onResponse: "+response.body() );

				Translation translation = response.body();
				if(translation.getStatus()==1){
					//Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
				}
				else{
					//Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Call<Translation> call, Throwable t) {
				Toast.makeText(getApplicationContext(), "错误:" + t.getMessage(), Toast.LENGTH_SHORT).show();
				//System.out.println("onFailure=" + t.getMessage());
			}
		});

	}
	//xfeng
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    //xfeng test

		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
		myApp=(Application)this.getApplication();

		mServerState = (TextView) findViewById(R.id.serverState);
		mNumberCount = (TextView) findViewById(R.id.numberCount);
        mIp=(TextView) findViewById(R.id.ip);
        mTvReceive = (TextView) findViewById(R.id.server_receive);
		mSwitchCamera = (ImageButton) findViewById(R.id.switch_camera);
		mSwitchonoff = (Button) findViewById(R.id.switch_onoff);
        mSwitchCamera.setOnClickListener(this);
		mSwitchonoff.setOnClickListener(this);
		mode=0;
		myApp.mode=mode;
		mBusInfo=myApp.mBusInfo;
		blog=myApp.buslog;
		if(blog==null)
			blog=new busRecordInfo();
		stu=myApp.stuLine;
        //startServer();
		mTopBar = (TextView) findViewById(R.id.topBar2);
		mQMTopBar=findViewById(R.id.DetecterTopBar);
		mEnd=(Button)findViewById(R.id.btn_end);

		mEnd.setOnClickListener(this);

		mNumberCount.setVisibility(View.INVISIBLE);


		setFace();
		if(myApp.mBusStatus.online!=-1 && myApp.mBusStatus.reReadData==true) {
			mNumberCount.setVisibility(View.VISIBLE);
			reInitData();
		}
		else
		{
			linechecked=mBusInfo.getLinechecked();
		}


		String lineName=mBusInfo.getLine().get(linechecked).getLinename();
		int jiaCount=0;
		for(int i =0;i<stu.size();i++)
		{
			if(stu.get(i).jia=true)
				jiaCount++;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");// HH:mm:ss//获取当前时间
		Date date = new Date(System.currentTimeMillis());
		int hour =Integer.parseInt(simpleDateFormat.format(date));

		if(hour>5 && hour <11)
			goorback=1;
		if(hour>=11 && hour <13)
			goorback=2;
		if(hour>=13 && hour <16)
			goorback=3;
		if(hour>=16 && hour <=20)
			goorback=4;

		//mTopBar.setText(lineName+"(共"+String.valueOf(stu.size())+"人）");

		blog.setBusnumber(mBusInfo.getLine().get(linechecked).getBusnumber());
		blog.setBusId(mBusInfo.getLine().get(linechecked).getBusid());
		blog.setLineId(mBusInfo.getLine().get(linechecked).getLineid());
		blog.setOrder(goorback);


		myApp.buslog=blog;

		stuCount=0;



		// TODO Auto-generated method stub

		mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
		mCameraRotate = getIntent().getIntExtra("Camera", 0) == 0 ? 90 : 270;
		mCameraMirror = getIntent().getIntExtra("Camera", 0) == 0 ? false : true;


		mWidth = 1280;
		mHeight = 720;
		mFormat = ImageFormat.NV21;
		mHandler = new Handler();

		mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
		mGLSurfaceView.setOnTouchListener(this);
		mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setOnCameraListener(this);
		mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
		mSurfaceView.debug_print_fps(true, false);

		//snap
		mTextView = (TextView) findViewById(R.id.textView);
		mTextView.setText("");
        mTextView1 = (TextView) findViewById(R.id.textView1);
        mTextView1.setText("");

		mImageView = (ImageView) findViewById(R.id.imageView);
		mImageButton = (ImageButton) findViewById(R.id.imageButton);
		mImageButton.setOnClickListener(this);


		mGLSurfaceView.setAspectRatio(0);


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

		mFRAbsLoop = new FRAbsLoop();
		mFRAbsLoop.start();

		titleCount=stu.size();
		initTopBar(lineName+"(共"+String.valueOf(titleCount)+"人）");
       // initGPS();



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


    public void onStart() {
        super.onStart();



        // Log.e(TAG, "++ ON START ++");

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mSocketConnectState == STATE_CLOSED)
            mServerState.setText(R.string.state_closed);
        else if(mSocketConnectState == STATE_CONNECTED)
            mServerState.setText(R.string.state_connected);
        else if(mSocketConnectState == STATE_DISCONNECTED || mSocketConnectState == STATE_ACCEPTING)
            mServerState.setText(R.string.state_disconect_accept);

        blog = myApp.buslog;

		faceList.clear();
		if(blog.getStudentlist()!=null)
			for (int i = 0; i < blog.getStudentlist().size(); i++) {

				faceList.add(new Face(blog.getStudentlist().get(i).getStuid() + "_" + blog.getStudentlist().get(i).getStuname(), blog.getStudentlist().get(i).getDatetime(), mode, blog.getStudentlist().get(i).getPostxt()));  //将图片id和对应的name存储到一起
				lvFaces.setAdapter(faceAdapter);
				mNumberCount.setText(String.valueOf(blog.getStudentlist().size()));
			}
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mFRAbsLoop.shutdown();
		AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
		Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

		ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
		Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());

		ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
		Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());


		closeConnect();
		if(mServerSocket != null){
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Camera setupCamera() {
		// TODO Auto-generated method stub
		mCamera = Camera.open(mCameraID);
		try {

			WindowManager windowManager = getWindowManager();
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

	@Override
	public void setupChanged(int format, int width, int height) {

	}

	@Override
	public boolean startPreviewImmediately() {
		return true;
	}

	@Override
	public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
		AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
		Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
		Log.d(TAG, "Face=" + result.size());
		for (AFT_FSDKFace face : result) {
			Log.d(TAG, "Face:" + face.toString());
		}
		if (mImageNV21 == null) {
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
		Rect[] rects = new Rect[result.size()];
		for (int i = 0; i < result.size(); i++) {
			rects[i] = new Rect(result.get(i).getRect());
		}
		//clear result.
		result.clear();
		//return the rects for render.
		return rects;
	}

	@Override
	public void onBeforeRender(CameraFrameData data) {

	}

	@Override
	public void onAfterRender(CameraFrameData data) {
		mGLSurfaceView.getGLES2Render().draw_rect((Rect[])data.getParams(), Color.GREEN, 2);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CameraHelper.touchFocus(mCamera, event, v, this);
		return false;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			Log.d(TAG, "Camera Focus SUCCESS!");
			camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imageButton) {
			if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
				mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;

				mCameraRotate  = 270;
				mCameraMirror = true;
			} else {
				mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
				mCameraRotate = 90;
				mCameraMirror = false;
			}
			mSurfaceView.resetCamera();
			mGLSurfaceView.setRenderConfig(mCameraRotate, mCameraMirror);
			mGLSurfaceView.getGLES2Render().setViewAngle(mCameraMirror, mCameraRotate);
		}
        if (view.getId() == R.id.switch_camera) {

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

        }
		if (view.getId() == R.id.switch_onoff) {

			if(mode==0) {
				mSwitchonoff.setText("下车识别");
				mSwitchonoff.setBackgroundColor(Color.parseColor("#9AFF9A"));
				mode=1;
				((Application)this.getApplicationContext()).mode=1;

			}
			else
			{
				mSwitchonoff.setText("上车识别");
				mSwitchonoff.setBackgroundColor(Color.parseColor("#EEC900"));
				mode=0;
				((Application)this.getApplicationContext()).mode=0;
			}
		}
		if (view.getId() == R.id.btn_end) {


			myApp.buslog=blog;

			String lastbuslogpath = myApp.mPath + "/lastbuslog.json";
			File lastbuslogfile = new File(lastbuslogpath);
			if (lastbuslogfile.exists()) {
				Log.i("myTag", "文件存在");
				lastbuslogfile.delete();
			}
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(lastbuslogfile);
				fileOutputStream.write(toJson(blog,1).getBytes());
				// fileOutputStream.write(sbString.getBytes());
				fileOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}


			Intent it = new Intent(DetecterActivity.this, BuslogActivity.class);
			startActivityForResult(it, 1);
		}

	}
	/**
	 * 将实体类转换成json字符串对象            注意此方法需要第三方gson  jar包
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

	private busRecordInfo getBuslog() {

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
			busRecordInfo info=gson.fromJson(jsonData,busRecordInfo.class);
			if(info.getStudentlist()==null)
			    info.setStudentlist(new ArrayList<busRecordInfo.StudentlistBean>());
			return info;

		}
		else return null;
	}
    private void getBusInfo() {

        String path = myApp.mPath + "/businfo.json";
        String jsonData = "";

        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                byte[] b = new byte[fileInputStream.available()];

                //将字节流中的数据传递给字节数组
                fileInputStream.read(b);

                //将字节数组转为字符串
                jsonData = new String(b);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<busInfo>() {
            }.getType();
            busInfo info = gson.fromJson(jsonData, busInfo.class);

            myApp.mBusInfo = info;
            mBusInfo = info;

        }
    }


        private void reInitData(){
		myApp.buslog=getBuslog();
		blog = myApp.buslog;
		if(blog==null)
			blog=new busRecordInfo();

		if(blog.getStudentlist()!=null)
			for (int i = 0; i < blog.getStudentlist().size(); i++) {

				faceList.add(new Face(blog.getStudentlist().get(i).getStuid() + "_" + blog.getStudentlist().get(i).getStuname(), blog.getStudentlist().get(i).getDatetime(), mode, blog.getStudentlist().get(i).getPostxt()));  //将图片id和对应的name存储到一起
				lvFaces.setAdapter(faceAdapter);
				mNumberCount.setText(String.valueOf(blog.getStudentlist().size()));
			}

		getBusInfo();

		linechecked=myApp.mBusStatus.online;
		int iline=linechecked;
		int stuSize=0;
		stu.clear();
		for(int i=0; i<mBusInfo.getLine().get(iline).getStationcount();i++)
		{
			stuSize+=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentcount();
			for(int j=0;j<mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentcount();j++) {
				StuInfo tmpStu=new StuInfo();
				tmpStu.stuId=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuid();
				tmpStu.stuName=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuname();
				tmpStu.baba=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getBaba();
				tmpStu.mama=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getMama();
				tmpStu.babanum=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getBabanum();
				tmpStu.mamanum=mBusInfo.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getMamanum();
				tmpStu.station=mBusInfo.getLine().get(iline).getStationlist().get(i).getStationname();
				//tmpStu.jia=result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getIsLeave();
				stu.add(tmpStu);
			}
		}
		myApp.stuLine=stu;

	}

	public void switchCamera(int cid)
    {
            if(Camera.getNumberOfCameras()<=1) return;

            if (mCameraID == 0) {
                mCameraID = 1;
            } else {
                mCameraID = 1;
                mCameraRotate = 0;
            }
            mSurfaceView.resetCamera();
            mGLSurfaceView.setRenderConfig(mCameraRotate, mCameraMirror);
            mGLSurfaceView.getGLES2Render().setViewAngle(mCameraMirror, mCameraRotate);


    }


	//tcp connect
    public void startServer() {
        if(mSocketConnectState != STATE_CLOSED) return;
        try {
            //开启服务、指定端口号
            mServerPort=DEFAULT_PORT;
            mServerSocket = new ServerSocket(mServerPort);
            GetIpAddress.getLocalIpAddress();
            mIp.setText(GetIpAddress.getIP()+":"+mServerPort );

        } catch (IOException e) {
            e.printStackTrace();
            mSocketConnectState = STATE_DISCONNECTED;
            Toast.makeText(this, "服务开启失败", Toast.LENGTH_SHORT).show();
            return;
        }
        startAccept();
        mServerState.setText(getString(R.string.state_opened));
        Toast.makeText(this, "服务开启", Toast.LENGTH_SHORT).show();
    }

    private void startAccept(){
        mSocketConnectState = STATE_ACCEPTING;
        mAcceptThread = new SocketAcceptThread();
        mAcceptThread.start();
    }

    private void sendTxt(String msg){
        if(mRecycleMsg != null){
            //每次点击发送按钮发送数据，将之前的定时发送移除。
            mSocketHandler.removeMessages(MSG_TIME_SEND);
            mRecycleMsg = null;
        }
        if(mSocket == null){
            Toast.makeText(this, "没有客户端连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //String msg = mEditMsg.getText().toString();
        if(msg.length() == 0)
            return;
        writeMsg(msg+"已上车，上车时间："+gettime);
    }

    private void writeMsg(String msg){
        if(msg.length() == 0 || mOutStream == null)
            return;
        try {
            mOutStream.write(msg.getBytes());//发送
            mOutStream.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnect(){
        try {
            if (mOutStream != null) {
                mOutStream.close();
            }
            if (mInStream != null) {
                mInStream.close();
            }
            if(mSocket != null){
                mSocket.close();  //关闭socket
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mReceiveThread != null){
            mReceiveThread.threadExit();
            mReceiveThread = null;
        }
        if(mSendThread != null){
            mSendThread.threadExit();
            mSendThread = null;
        }
    }

    class SocketAcceptThread extends Thread{
        @Override
        public void run() {
            try {
                //等待客户端的连接，Accept会阻塞，直到建立连接，
                //所以需要放在子线程中运行。
                if(mSocket==null) return;
                mSocket = mServerSocket.accept();
                //获取输入流
                mInStream = mSocket.getInputStream();
                //获取输出流
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                mSocketHandler.sendEmptyMessage(MSG_SOCKET_ACCEPTFAIL);
                return;
            }
            Log.i(TAG, "accept success");
            mSocketHandler.sendEmptyMessage(MSG_SOCKET_CONNECT);
        }
    }
    class SocketReceiveThread extends Thread{
        private boolean threadExit = false;
        public void run(){
            byte[] buffer = new byte[1024];
            while(threadExit == false){
                try { //读取数据，返回值表示读到的数据长度。-1表示结束
                    int count = mInStream.read(buffer);
                    if(count == -1){
                        Log.i(TAG, "read read -1");
                        mSocketHandler.sendEmptyMessage(MSG_SOCKET_DISCONNECT);
                        break;
                    }else{
                        String receiveData;
                        receiveData = new String(buffer, 0, count);
                        Log.i(TAG, "read buffer:"+receiveData+",count="+count);
                        Message msg = new Message();
                        msg.what = MSG_RECEIVE_DATA;
                        msg.obj = receiveData;
                        mSocketHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void threadExit(){
            threadExit = true;
        }
    }

    class SocketSendThread extends Thread{
        private boolean threadExit = false;
        public void run(){
            sendTxt(faceName);
            Message msg = new Message();
            msg.what = MSG_SEND_DATA;
            msg.obj = "1";
            mHandler.sendMessage(msg);
        }

        void threadExit(){
            threadExit = true;
        }
    }

	class  MyLocationListener extends BDAbstractLocationListener {
		private String id,lon,lat,postxt,speed,time,busnumber;
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {

			//MyLocationData locData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
			//		.direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
			//		.longitude(bdLocation.getLongitude()).build();
			// 设置定位数据
			//mBaiduMap.setMyLocationData(locData);
			//地图SDK处理
			if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
				StringBuffer sb = new StringBuffer(256);
				sb.append("时间 : ");
				sb.append(bdLocation.getTime());
				sb.append("\n纬度 : ");// 纬度
				sb.append(bdLocation.getLatitude());
				sb.append("\n经度 : ");// 经度
				sb.append(bdLocation.getLongitude());
				sb.append("\n位置描述 : ");
				sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
				gpsinfo=sb.toString();

				lon=String.valueOf(bdLocation.getLongitude());
				lat=String.valueOf(bdLocation.getLatitude());
				postxt=bdLocation.getLocationDescribe();
				postxtlog=postxt;
				speed=String.valueOf(bdLocation.getSpeed());

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
				// 获取当前时间
				Date date = new Date(System.currentTimeMillis());
				time=simpleDateFormat.format(date);

				setGPS(lon,lat,postxt,speed,time);

			}

		}

		public void setGPS(String lon,String lat, String postxt, String speed, String time) {
			//Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

			//步骤4:创建Retrofit对象
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("http://xfeng.gz01.bdysite.com/busnew/src/views/bus/") //http://fy.iciba.com/
					.addConverterFactory(GsonConverterFactory.create())
					.build();

			// 步骤5:创建 网络请求接口 的实例
			interfaceHttp request = retrofit.create(interfaceHttp.class);
			Call<Translation> call;
			call = request.setGPS(mBusInfo.getLine().get(linechecked).getBusid(), mBusInfo.getLine().get(linechecked).getBusnumber(),lon,lat,speed,time,postxt,1);


			//步骤6:发送网络请求(异步)
			call.enqueue(new Callback<Translation>() {
				@Override
				public void onResponse(Call<Translation> call, Response<Translation> response) {
					Log.e("xfeng", "onResponse: "+response.body() );

					Translation translation = response.body();
					if(translation.getStatus()==1){
						//Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
					}
					else{
						//Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
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
}
