package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
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

public class RegisterActivity extends Activity implements SurfaceHolder.Callback {
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

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBitmap;
	private Rect src = new Rect();
	private Rect dst = new Rect();
	private Thread view,view2;
	private EditText mEditText;
	private ExtImageView mExtImageView;
	private HListView mHListView;
	private RegisterViewAdapter mRegisterViewAdapter;
	private AFR_FSDKFace mAFR_FSDKFace;
	private List<AFD_FSDKFace> resultpub = new ArrayList<AFD_FSDKFace>();
	float scalepub;
	int fi;

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

		mRegisterViewAdapter = new RegisterViewAdapter(this);
		mHListView = (HListView)findViewById(R.id.hlistView);
		mHListView.setAdapter(mRegisterViewAdapter);
		mHListView.setOnItemClickListener(mRegisterViewAdapter);

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



	private void createbtn()
	{
		//xfeng add button
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int swidth = dm.widthPixels;
		int sheight = dm.heightPixels;

		LinearLayout mLinearLayout;
		mLinearLayout = (LinearLayout)findViewById(R.id.linearLayout);

		Button Btn[] = new Button[resultpub.size()+1];
		int j = -1;
		for (int i=0; i<resultpub.size(); i++) {
			Btn[i] = new Button(RegisterActivity.this);
			Btn[i].setId(2000 + i);
			Btn[i].setText("头像" + i);
			RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams((swidth - 50) / 4, 40);
			if (i%4 == 0) {
				j++;
			}
			btParams.leftMargin = 10+ ((swidth-50)/4+10)*(i%4); //横坐标定位
			btParams.topMargin = 20 + 55*j; //纵坐标定位
			mLinearLayout.addView(Btn[i],btParams); //将按钮放入layout组件
		}
		for (int k = 0; k <resultpub.size(); k++) {
			//这里不需要findId，因为创建的时候已经确定哪个按钮对应哪个Id
			Btn[k].setTag(k);    //为按钮设置一个标记，来确认是按下了哪一个按钮

			Btn[k].setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i = (Integer) v.getTag();

					Toast.makeText(RegisterActivity.this.getApplicationContext(), i, Toast.LENGTH_SHORT).show();
				}
			});
		}

	}
	public int saveImage(Bitmap bmp,String name) {
        String appDir = ((Application) RegisterActivity.this.getApplicationContext()).mPath;//new File(Environment.getExternalStorageDirectory(), "Boohee");
        //if (!appDir.exists()) {
        //   appDir.mkdir();
       // }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
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
			Log.i(TAG, "getIntentData:" + mFilePath);
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
					LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
					View layout = inflater.inflate(R.layout.dialog_register, null);
					mEditText = (EditText) layout.findViewById(R.id.editview);
					mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
					mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
					mExtImageView.setImageBitmap((Bitmap) msg.obj);
					final Bitmap face = (Bitmap) msg.obj;

					new AlertDialog.Builder(RegisterActivity.this)
							.setTitle("请输入注册名字")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(layout)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									if(((Application)RegisterActivity.this.getApplicationContext()).mFaceDB.check(mEditText.getText().toString())==1){
										Toast.makeText(RegisterActivity.this.getApplicationContext(), "存在重名，如果是同一人请在名字后加编号，如果不是同一人请加括号备注", Toast.LENGTH_SHORT).show();
									}
									else {
										((Application) RegisterActivity.this.getApplicationContext()).mFaceDB.addFace(mEditText.getText().toString(), mAFR_FSDKFace);
										mRegisterViewAdapter.notifyDataSetChanged();

										saveImage(face, mEditText.getText().toString());
										Intent intent = new Intent();
										//把返回数据存入Intent
										intent.putExtra("result", "My name is linjiqin");
										//设置返回数据
										RegisterActivity.this.setResult(RESULT_OK, intent);
										//关闭Activity
										RegisterActivity.this.finish();
										if(view2!=null)
											view2.interrupt();
										dialog.dismiss();
									}
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									if(view2!=null)
										view2.interrupt();
								}
							})
							.show();
				} else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
					Toast.makeText(RegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_NO_FACE ){
					Toast.makeText(RegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
					Toast.makeText(RegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				} else if(msg.arg1 == MSG_EVENT_FR_ERROR){
					Toast.makeText(RegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	class Holder {
		ExtImageView siv;
		TextView tv;
	}

	class RegisterViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
		Context mContext;
		LayoutInflater mLInflater;

		public RegisterViewAdapter(Context c) {
			// TODO Auto-generated constructor stub
			mContext = c;
			mLInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (convertView != null) {
				holder = (Holder) convertView.getTag();
			} else {
				convertView = mLInflater.inflate(R.layout.item_sample, null);
				holder = new Holder();
				holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
				holder.tv = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}

			if (!((Application)mContext.getApplicationContext()).mFaceDB.mRegister.isEmpty()) {
				FaceDB.FaceRegist face = ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.get(position);
				holder.tv.setText(face.mName);
				//holder.siv.setImageResource(R.mipmap.ic_launcher);
				convertView.setWillNotDraw(false);
			}

			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("onItemClick", "onItemClick = " + position + "pos=" + mHListView.getScroll());
			final String name = ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
			final int count = ((Application)mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mFaceList.size();
			new AlertDialog.Builder(RegisterActivity.this)
					.setTitle("删除注册名:" + name)
					.setMessage("包含:" + count + "个注册人脸特征信息")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((Application)mContext.getApplicationContext()).mFaceDB.delete(name);
							mRegisterViewAdapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
		}
	}
}
