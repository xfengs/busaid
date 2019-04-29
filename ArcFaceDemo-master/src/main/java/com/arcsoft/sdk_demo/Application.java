package com.arcsoft.sdk_demo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.arcsoft.sdk_demo.location.ForegroundActivity;
import com.arcsoft.sdk_demo.location.NotificationUtils;
import com.arcsoft.sdk_demo.location.service.LocationService;
import com.arcsoft.sdk_demo.model.GpsModel;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.model.StuModel;
import com.arcsoft.sdk_demo.utils.Common;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.connect.busInfo;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class Application extends android.app.Application {
	private final String TAG = this.getClass().toString();
	public FaceDB mFaceDB;
	List<FaceDB> mFaceDBLine;
	public String mPath;
	public int mode;
	Uri mImage;
	public String version="0.1.4";
	public busInfo mBusInfo;
	public List<StuInfo> stuLine = new ArrayList<StuInfo>();
	public busRecordInfo buslog;
	public LocationClient mClient;
	public String GPSinfo;
	public int order_flag;
	public String tmobile;
	public StuModel stuModel;
	public int isOnline;


	public LocationService locationService;
	public Vibrator mVibrator;
	public BusStatus mBusStatus=new BusStatus();
	public int linechecked;
	public int lastLine=-1;
	public String AppURL="http://dev.busaid.cn/api/app/";



	private MyLocationListener myLocationListener = new MyLocationListener();
	private BaiduMap mBaiduMap;
	private NotificationUtils mNotificationUtils;
	private Notification notification;
	private boolean isFirstLoc = true;
	public String gpsinfo;
	public String postxtlog="";


	public boolean isFirstLoad=true;


	private GpsModel gpsModel=new GpsModel(this);


	@Override
	public void onCreate() {
		super.onCreate();
        linechecked=-1;
		mPath=this.getExternalFilesDir(null).getPath();
		mFaceDB = new FaceDB(this.getExternalFilesDir(null).getPath());
		isOnline=-1;


		mImage = null;

		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        //内存泄漏
		//initLeakCanary();

	}
	/**        Explain : 初始化内存泄漏检测
	 * @author LiXaing
	 * **/
	private void initLeakCanary() {
/*		if (LeakCanary.isInAnalyzerProcess(this)) {
			return;
		}
		LeakCanary.install(this);*/
	}

	public void setCaptureImage(Uri uri) {
		mImage = uri;
	}

	public Uri getCaptureImage() {
		return mImage;
	}

	/**
	 * @param path
	 * @return
	 */
	public static Bitmap decodeImage(String path) {
		Bitmap res;
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 1;
			op.inJustDecodeBounds = false;
			//op.inMutable = true;
			res = BitmapFactory.decodeFile(path, op);
			//rotate and scale.
			Matrix matrix = new Matrix();

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}

			Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
			Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

			if (!temp.equals(res)) {
				res.recycle();
			}
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	public void initGPS(){

		mClient = new LocationClient(this);
		LocationClientOption mOption = new LocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
		mOption.setScanSpan(5000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
		mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
		mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
		mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
		mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
		mClient.setLocOption(mOption);
		mClient.registerLocationListener(myLocationListener);
		mClient.start();
		//设置后台定位
		//android8.0及以上使用NotificationUtils

		if (Build.VERSION.SDK_INT >= 26) {
			mNotificationUtils = new NotificationUtils(this);
			Notification.Builder builder2 = mNotificationUtils.getAndroidChannelNotification
					("适配android 8限制后台定位功能", "正在后台定位");
			notification = builder2.build();
		} else {
			//获取一个Notification构造器
			Notification.Builder builder = new Notification.Builder(this);
			Intent nfIntent = new Intent(this, ForegroundActivity.class);

			builder.setContentIntent(PendingIntent.
					getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
					.setContentTitle("适配android 8限制后台定位功能") // 设置下拉列表里的标题
					.setSmallIcon(R.drawable.ic_launcher) // 设置状态栏内的小图标
					.setContentText("正在后台定位") // 设置上下文内容
					.setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

			notification = builder.build(); // 获取构建好的Notification
		}
		notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

	}


	public class BusStatus{
	    public int online;
	    public int busid;
	    public String linename;
	    public boolean[] isUploaded;
	    public String loginTime;
	    public String phonenumber;
	    public boolean reReadData;
	    public String uploadDate;


	    public BusStatus(){
	    	online=-1;
	    	busid=-1;
			isUploaded=new boolean[4];
	    	loginTime="";
            linename="";
			reReadData=false;
			uploadDate="";
		}

        public void writeBusid(int ol){
            SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = busSP.edit();
            editor.putInt("busid",ol);
            editor.commit();//提交修改
        }
        public void writeLinename(String ln){
            SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = busSP.edit();
            editor.putString("linename",ln);
            editor.commit();//提交修改
        }
		public void writeOnline(int ol){
			SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = busSP.edit();
			editor.putInt("online",ol);
			editor.commit();//提交修改
		}
		public void writeIsUploaded(boolean iu,int orderflag){
			SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = busSP.edit();
			if(orderflag!=0)
				isUploaded[orderflag-1]=iu;
			JSONArray jsonArray = new JSONArray();
			for (boolean b : isUploaded) {
				jsonArray.put(b);
			}
			editor.putString("isuploaded", jsonArray.toString());
			editor.commit();//提交修改
		}
		public void writeLoginTime(String lt){
			SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = busSP.edit();
			editor.putString("logintime", lt);
			editor.commit();//提交修改
		}
		public void writeUploadDate(String ud){
			SharedPreferences busSP = getSharedPreferences("bus", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = busSP.edit();
			editor.putString("uploaddate", ud);
			editor.commit();//提交修改
		}
		public void reInit(){
			online=-1;
			busid=-1;
			isUploaded[0]=false;
			isUploaded[1]=false;
			isUploaded[2]=false;
			isUploaded[3]=false;
			loginTime="";
			linename="";
			reReadData=false;

			writeOnline(-1);
			writeLinename("");
			writeBusid(-1);
			writeIsUploaded(false,1);
			writeIsUploaded(false,2);
			writeIsUploaded(false,3);
			writeIsUploaded(false,4);
			writeLoginTime("");

			String lastbuslogpath = mPath + "/lastbuslog.json";
			File lastbuslogfile = new File(lastbuslogpath);
			if (lastbuslogfile.exists()) {
				lastbuslogfile.delete();
			}
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
				GPSinfo=gpsinfo;

				lon=String.valueOf(bdLocation.getLongitude());
				lat=String.valueOf(bdLocation.getLatitude());
				postxt=bdLocation.getLocationDescribe();
				postxtlog=postxt;
				speed=String.valueOf(bdLocation.getSpeed());


				if(postxt!=null)
					gpsModel.updateGPS(lon,lat,postxt,speed,(new Common()).getNowStamp());

			}
			else{
				//Toast.makeText(getApplicationContext(),"error:"+BDLocation.TypeServerError,Toast.LENGTH_SHORT).show();
			}

		}


	}
}
