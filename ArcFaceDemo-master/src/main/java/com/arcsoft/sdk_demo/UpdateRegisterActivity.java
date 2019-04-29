package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.connect.busInfo;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtImageView;
import com.guo.android_extend.widget.HListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqj3375 on 2017/4/27.
 */

public class UpdateRegisterActivity extends Activity implements SurfaceHolder.Callback {
	private final String TAG = this.getClass().toString();
	private final static int MSG_CODE = 0x1000;
	private final static int MSG_EVENT_REG = 0x1001;
	private final static int MSG_EVENT_NO_FACE = 0x1002;
	private final static int MSG_EVENT_NO_FEATURE = 0x1003;
	private final static int MSG_EVENT_FD_ERROR = 0x1004;
	private final static int MSG_EVENT_FR_ERROR = 0x1005;
	private UIHandler mUIHandler;
	// Intent data.
	private String 		mFilePath;
	private int stuId;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBitmap;
	private Rect src = new Rect();
	private Rect dst = new Rect();
	private Thread view,view2;
	private EditText mEditText;
	private ExtImageView mExtImageView;
	private HListView mHListView;
	private AFR_FSDKFace mAFR_FSDKFace;
	private List<AFD_FSDKFace> resultpub = new ArrayList<AFD_FSDKFace>();
	float scalepub;
	int fi;

	busInfo result;
	StuInfo stu;
	String mFile,mPath;
	int stuPos;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);
		//initial data.
		if (!getIntentData(getIntent().getExtras())) {
			Log.e(TAG, "getIntentData fail!");
			this.finish() ;
		}

		stuId=Integer.parseInt(getIntent().getStringExtra("stuId"));
		result=((Application)this.getApplicationContext()).mBusInfo;
		mPath=((Application)this.getApplicationContext()).mPath;
		int iline;
		iline=result.getLinechecked();
		int k=0;
		for(int i=0; i<result.getLine().get(iline).getStationcount();i++) {
			for (int j = 0; j < result.getLine().get(iline).getStationlist().get(i).getStudentcount(); j++) {
				if(result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuid()==stuId)
				{
					mFile=String.valueOf(stuId)+"_"+result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuname();
					stuPos=k;
				}
				k++;
			}
		}





		mUIHandler = new UIHandler();
		mBitmap = Application.decodeImage(mFilePath);
		src.set(0,0,mBitmap.getWidth(),mBitmap.getHeight());
		mSurfaceView = (SurfaceView)this.findViewById(R.id.surfaceView);
		mSurfaceView.getHolder().addCallback(this);
		view = new Thread(new Runnable() {
			@Override
			public void run() {
				while (mSurfaceHolder == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
				try {
					ImageConverter convert = new ImageConverter();
					convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
					if (convert.convert(mBitmap, data)) {
						Log.d(TAG, "convert ok!");
					}
					convert.destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}

				AFD_FSDKEngine engine = new AFD_FSDKEngine();
				AFD_FSDKVersion version = new AFD_FSDKVersion();
				List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
				AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
				Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
				if (err.getCode() != AFD_FSDKError.MOK) {
					Message reg = Message.obtain();
					reg.what = MSG_CODE;
					reg.arg1 = MSG_EVENT_FD_ERROR;
					reg.arg2 = err.getCode();
					mUIHandler.sendMessage(reg);
				}
				err = engine.AFD_FSDK_GetVersion(version);
				Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
				err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
				Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
				while (mSurfaceHolder != null) {
					Canvas canvas = mSurfaceHolder.lockCanvas();
					if (canvas != null) {
						Paint mPaint = new Paint();
						boolean fit_horizontal = canvas.getWidth() / (float)src.width() < canvas.getHeight() / (float)src.height() ? true : false;
						float scale = 1.0f;
						if (fit_horizontal) {
							scale = canvas.getWidth() / (float)src.width();
							dst.left = 0;
							dst.top = (canvas.getHeight() - (int)(src.height() * scale)) / 2;
							dst.right = dst.left + canvas.getWidth();
							dst.bottom = dst.top + (int)(src.height() * scale);
						} else {
							scale = canvas.getHeight() / (float)src.height();
							dst.left = (canvas.getWidth() - (int)(src.width() * scale)) / 2;
							dst.top = 0;
							dst.right = dst.left + (int)(src.width() * scale);
							dst.bottom = dst.top + canvas.getHeight();
						}
						scalepub=scale;
						canvas.drawBitmap(mBitmap, src, dst, mPaint);
						canvas.save();
						canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
						canvas.translate(dst.left / scale, dst.top / scale);
						for (AFD_FSDKFace face : result) {
							mPaint.setColor(Color.RED);
							mPaint.setStrokeWidth(10.0f);
							mPaint.setStyle(Paint.Style.STROKE);
							canvas.drawRect(face.getRect(), mPaint);
						}
						resultpub=result;
						canvas.restore();
						mSurfaceHolder.unlockCanvasAndPost(canvas);
						break;
					}
				}

				if (!result.isEmpty()) {
					AFR_FSDKVersion version1 = new AFR_FSDKVersion();
					AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
					AFR_FSDKFace result1 = new AFR_FSDKFace();
					AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
					Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
					if (error1.getCode() != AFD_FSDKError.MOK) {
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_FR_ERROR;
						reg.arg2 = error1.getCode();
						mUIHandler.sendMessage(reg);
					}
					error1 = engine1.AFR_FSDK_GetVersion(version1);
					Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
					error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
					Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
					if(error1.getCode() == error1.MOK) {

						mAFR_FSDKFace = result1.clone();
						int width = result.get(0).getRect().width();
						int height = result.get(0).getRect().height();
						Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
						Canvas face_canvas = new Canvas(face_bitmap);								Rect recttemp=resultpub.get(fi).getRect();
						Rect rectlarge=new Rect((int)(recttemp.left-width*0.2),(int)(recttemp.top-height*0.3),(int)(recttemp.right+width*0.2),(int)(recttemp.bottom+height*0.1));
						face_canvas.drawBitmap(mBitmap, rectlarge, new Rect(0, 0, width, height), null);
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_REG;
						reg.obj = face_bitmap;
						mUIHandler.sendMessage(reg);
					} else {
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_NO_FEATURE;
						mUIHandler.sendMessage(reg);
					}
					error1 = engine1.AFR_FSDK_UninitialEngine();
					Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
				} else {
					Message reg = Message.obtain();
					reg.what = MSG_CODE;
					reg.arg1 = MSG_EVENT_NO_FACE;
					mUIHandler.sendMessage(reg);
				}
				err = engine.AFD_FSDK_UninitialFaceEngine();
				Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
			}
		});
		view.start();
		mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					float x = event.getX();
					float y = event.getY();
					Rect rect0,rect;
//创建和按钮一样位置的Rect
					x=x/scalepub;
					y=y/scalepub;

					if (!resultpub.isEmpty()) {


						for(int i=0;i<resultpub.size();i++){
							rect0=resultpub.get(i).getRect();
							rect = new Rect(rect0.left+(int)(dst.left/scalepub),rect0.top+(int)(dst.top/scalepub),rect0.right+(int)(dst.left/scalepub),rect0.bottom+(int)(dst.top/scalepub));
							if (rect.contains((int) x, (int) y)) {
								//Toast.makeText(RegisterActivity.this.getApplicationContext(), fi+"按钮范围之内", Toast.LENGTH_SHORT).show();
								fi=i;
								view2 = new Thread(new Runnable() {
									@Override
									public void run() {
										AFR_FSDKVersion version1 = new AFR_FSDKVersion();
										AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
										AFR_FSDKFace result1 = new AFR_FSDKFace();
										AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);

										byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
										try {
											ImageConverter convert = new ImageConverter();
											convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
											if (convert.convert(mBitmap, data)) {
												Log.d(TAG, "convert ok!");
											}
											convert.destroy();
										} catch (Exception e) {
											e.printStackTrace();
										}

										Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
										if (error1.getCode() != AFD_FSDKError.MOK) {
											Message reg = Message.obtain();
											reg.what = MSG_CODE;
											reg.arg1 = MSG_EVENT_FR_ERROR;
											reg.arg2 = error1.getCode();
											mUIHandler.sendMessage(reg);
										}
										error1 = engine1.AFR_FSDK_GetVersion(version1);
										Log.d("com.arcsoft", "FR=" + version1.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
										error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(resultpub.get(fi).getRect()), resultpub.get(fi).getDegree(), result1);
										Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
										if(error1.getCode() == error1.MOK) {

											mAFR_FSDKFace = result1.clone();
											int width = (int)(resultpub.get(fi).getRect().width()*1.4);
											int height = (int)(resultpub.get(fi).getRect().height()*1.4);
											Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
											Canvas face_canvas = new Canvas(face_bitmap);
											Rect recttemp=resultpub.get(fi).getRect();
											Rect rectlarge=new Rect((int)(recttemp.left-width*0.2),(int)(recttemp.top-height*0.3),(int)(recttemp.right+width*0.2),(int)(recttemp.bottom+height*0.1));

											face_canvas.drawBitmap(mBitmap, rectlarge, new Rect(0, 0, width, height), null);
											Message reg = Message.obtain();
											reg.what = MSG_CODE;
											reg.arg1 = MSG_EVENT_REG;
											reg.obj = face_bitmap;
											mUIHandler.sendMessage(reg);
										} else {
											Message reg = Message.obtain();
											reg.what = MSG_CODE;
											reg.arg1 = MSG_EVENT_NO_FEATURE;
											mUIHandler.sendMessage(reg);
										}
										error1 = engine1.AFR_FSDK_UninitialEngine();
										Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());

									}
								});
								view2.start();
							}
						}
					}
					else {
						Message reg = Message.obtain();
						reg.what = MSG_CODE;
						reg.arg1 = MSG_EVENT_NO_FACE;
						mUIHandler.sendMessage(reg);
					}

				}
				return false;
			}
		});
	}


	public int saveImage(Bitmap bmp,String name) {
        String appDir = ((Application) UpdateRegisterActivity.this.getApplicationContext()).mPath+"/faces";//new File(Environment.getExternalStorageDirectory(), "Boohee");
        //if (!appDir.exists()) {
        //   appDir.mkdir();
       // }
        String fileName = appDir + "/"+ name + ".jpg";
        String fileData= ((Application) UpdateRegisterActivity.this.getApplicationContext()).mPath+"/"+name+".data";
        File file = new File(fileName);
		File datafile = new File(fileData);

		if (file.exists()) {
			file.delete();
		}
		if (datafile.exists()) {
			datafile.delete();
		}

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
	 * @note bundle data :
	 * String imagePath
	 *
	 * @param bundle
	 */
	private boolean getIntentData(Bundle bundle) {
		try {
			mFilePath = bundle.getString("imagePath");
			if (mFilePath == null || mFilePath.isEmpty()) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		try {
			view.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class UIHandler extends android.os.Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_CODE) {
				if (msg.arg1 == MSG_EVENT_REG) {
					LayoutInflater inflater = LayoutInflater.from(UpdateRegisterActivity.this);
					View layout = inflater.inflate(R.layout.dialog_register, null);
					mEditText = (EditText) layout.findViewById(R.id.editview);
					mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
					mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
					mExtImageView.setImageBitmap((Bitmap) msg.obj);
					final Bitmap face = (Bitmap) msg.obj;

					saveImage(face, mFile);

						((Application) UpdateRegisterActivity.this.getApplicationContext()).mFaceDB.addFace(mFile, mAFR_FSDKFace);

						Intent intent = new Intent();
						intent.putExtra("stuPos",stuPos);
						//设置返回数据
						UpdateRegisterActivity.this.setResult(RESULT_OK, intent);
						//关闭Activity
						UpdateRegisterActivity.this.finish();

					}
				} else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
					Toast.makeText(UpdateRegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_NO_FACE ){
					Toast.makeText(UpdateRegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
					Toast.makeText(UpdateRegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FR_ERROR){
					Toast.makeText(UpdateRegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				}
			}
		}

}
