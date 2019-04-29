package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.arcsoft.sdk_demo.location.NotificationUtils;
import com.arcsoft.sdk_demo.location.service.LocationService;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.connect.Translation;
import com.connect.busInfo;
import com.connect.interfaceHttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guo.android_extend.widget.ExtImageView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusinfoActivity extends Activity implements OnClickListener {

	private Application myApp;
	private final String TAG = this.getClass().toString();
	private SharedPreferences sp;
	QMUITopBarLayout mTopBar;
	private int cols=6;

	com.arcsoft.sdk_demo.QMUIGroupListView mGroupListView;

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;
    private static final int REQUEST_CODE_OFFLINE = 4;



//xfeng gps
	private BaiduMap mBaiduMap;
	private NotificationUtils mNotificationUtils;
	private Notification notification;
	private boolean isFirstLoc = true;
	public String gpsinfo;
	private String postxtlog="";
	private busInfo mBusInfo;

	private String filesPath;
    //xfeng add
	// Layout Views
	private TextView mTitle;
	private TextView mTestView;
	private EditText mEditText;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;
	private ListView mLineListView;
    public List<Map<String, String>> lineList=new ArrayList<Map<String, String>>();



    /* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    JSONArray jArray;
    String result = null;
    InputStream is = null;
    StringBuilder sb = null;


	public String facesFolder="/faces";
	public int stuPos=0;

    //map

	private MapView mMapView = null;
	private LocationService locationService;
	private RecyclerView mRecyclerView;
	private List<String> mDatas;
	private ItemAdapter mAdapter;
	private FaceDB.FaceRegist face;
	private int stuSize;
	private List<String> stuList = new ArrayList<>();
	private List<Integer>  stuIdList = new ArrayList<Integer>();
	private List<StuInfo> stu = new ArrayList<StuInfo>();
	private boolean isLineChecked;
	private int linechecked;


	private ListAdapter mLineAdapter;
	private boolean sIsScrolling;

	private DownloadBuilder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_info);

		myApp=(Application)this.getApplication();

        filesPath=((Application)this.getApplicationContext()).mPath;
        //xfeng add
		mTopBar =  findViewById(R.id.topBar);
		mGroupListView=(com.arcsoft.sdk_demo.QMUIGroupListView)findViewById(R.id.groupListView);
		isLineChecked=false;


		mBusInfo=myApp.mBusInfo;

		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		//mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setLayoutManager(new GridLayoutManager(this,cols));
		mRecyclerView.setAdapter(mAdapter = new ItemAdapter(stu,this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    sIsScrolling = true;
                    Glide.with(BusinfoActivity.this).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling == true) {
                        Glide.with(BusinfoActivity.this).resumeRequests();

                    }
                    sIsScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


		mTestView = (TextView) findViewById(R.id.testView);
		mTitle = (TextView) findViewById(R.id.stuCount);

        // TODO Auto-generated method stub

		View v = this.findViewById(R.id.logoff);
        v.setOnClickListener(this);
        v = this.findViewById(R.id.btn_startrecg);
        v.setOnClickListener(this);

        //xfeng
		//首先需要获取到wifi管理者，初始化工具类
		//WifiAdmin wifiAdmin = new WifiAdmin(this);
//		wifiAdmin.createAp();

		initTopBar();
		getBusInfo();

		reInitLog();

		makeDirectory(filesPath+"/facedata");
		makeDirectory(filesPath+facesFolder);

		updateapp();


	}
	private void updateapp()
	{
		builder = AllenVersionChecker
				.getInstance()
				.requestVersion()
				.setRequestUrl("http://xfeng.gz01.bdysite.com/busaidapp/getversion.php")
				.request(new RequestVersionListener() {
					@Nullable
					@Override
					public UIData onRequestVersionSuccess(String result) {

						String downloadUrl="";
						String version="";
						//拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
						JSONObject json = null;
						try {
							json = new JSONObject(result);
							version=json.getString("version");
							if(myApp.version.equals(version))
								return null;
							downloadUrl =json.getString("url");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//如果是最新版本直接return null
						return UIData
								.create()
								.setDownloadUrl(downloadUrl)
								.setTitle("版本更新")
								.setContent("版本号："+version+"\nbug修复");

					}
					@Override
					public void onRequestVersionFailure(String message) {

					}
				});

				builder.executeMission(BusinfoActivity.this);
	}

	private void initTopBar() {

		mTopBar.setTitle("利笙校车");
		mTopBar.setBackgroundColor(0xff00A8E1);
	}
	private void reInitLog()
    {

    }

	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO 设置菜单

		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
        //TODO 菜单点击

		return false;
	}
/*
    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }*/
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "++ ON START ++");
//
		//// If BT is not on, request that it be enabled.
		//// setupChat() will then be called during onActivityResult
		//if (!mBluetoothAdapter.isEnabled()) {
		//	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		//	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		//	// Otherwise, setup the chat session
		//} else {
		//	if (mChatService == null) setupChat();
		//}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
			//Bundle bundle = data.getExtras();
			//String path = bundle.getString("imagePath");
			stuPos=getIntent().getIntExtra("stuPos",-1);

			//stuSize=((Application)BusinfoActivity.this.getApplicationContext()).mFaceDB.mRegister.size();
			//Log.i("xfeng", "size2"+stuSize);
			//face = myApp.mFaceDB.mRegister.get(stuSize-1);
			//stuList.add(stuSize-1, "" + face.mName);
			//mAdapter.notifyItemInserted(stuSize);
			//mAdapter.notifyDataSetChanged();
			//mTitle.setText("本线路共"+ (stuSize) +"人");


		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = myApp.getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = Application.decodeImage(file);
			startRegister(bmp, file);
		}

		mAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		switch (paramView.getId()) {

            case R.id.logoff:

				//获取sharedPreferences对象
				SharedPreferences sharedPreferences = getSharedPreferences("lisheng", Context.MODE_PRIVATE);
				//获取editor对象
				SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
				//存储键值对
				editor.putString("phone", "");
				//提交
				editor.commit();//提交修改
				myApp.mBusStatus.reInit();
				Intent it=new Intent(BusinfoActivity.this,splashActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);


                break;
			case R.id.btn_startrecg:
				if(isLineChecked==true)
				 	startDetector(1);
				else
					Toast.makeText(BusinfoActivity.this, "请先选择线路!", Toast.LENGTH_SHORT).show();
				break;
			default:;
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
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

					return getDataColumn(this, contentUri, null, null);
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

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
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
		Intent it = new Intent(BusinfoActivity.this, UpdateRegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		it.putExtra("stuId",String.valueOf(stu.get(stuPos).stuId));
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
        myApp.stuLine=stu;
		Intent it = new Intent(BusinfoActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

//xfeng add registerList

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
			String bmppath=((Application)BusinfoActivity.this.getApplicationContext()).mPath +facesFolder+"/"+faceFile+".jpg";
			File file = new File(bmppath);
			Log.i("xfeng", bmppath);
			RequestOptions options = new RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.NONE)
					.skipMemoryCache(true);
			if(file.exists()) {
				Glide.with(context).load(bmppath).apply(options).into(holder.siv);

			}
			else
				Glide.with(context).load(R.drawable.default_profile).apply(options).into(holder.siv);
			/*
			try {
				FileInputStream fis = new FileInputStream(bmppath+"/"+faceFile+".jpg");
				Bitmap facebmp=BitmapFactory.decodeStream(fis);
				DisplayMetrics dm = getResources().getDisplayMetrics();
				Bitmap nfb=scaleBitmap(facebmp,dm.widthPixels/cols,dm.widthPixels/cols);
				holder.siv.setImageBitmap(nfb);
			} catch (FileNotFoundException e) {
				//FileInputStream fis = new FileInputStream(R.drawable.default_profile);
				Bitmap facebmp=BitmapFactory.decodeResource(getResources(),R.drawable.default_profile);;
				DisplayMetrics dm = getResources().getDisplayMetrics();
				Bitmap nfb=scaleBitmap(facebmp,dm.widthPixels/cols,dm.widthPixels/cols);
				holder.siv.setImageBitmap(nfb);
			}
			*/
			//子项的点击事件监听
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					/*
					//Toast.makeText(context, "点击子项" + position, Toast.LENGTH_SHORT).show();
					final String name = ((Application)BusinfoActivity.this.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
					final int count = ((Application)BusinfoActivity.this.getApplicationContext()).mFaceDB.mRegister.get(position).mFaceList.size();
					new AlertDialog.Builder(BusinfoActivity.this)
							.setTitle("删除注册名:" + name)
							.setMessage("包含:" + count + "个注册人脸特征信息")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									((Application)BusinfoActivity.this.getApplicationContext()).mFaceDB.delete(name);
									mAdapter.removeItem(position);
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
							*/
					stuPos=position;
						final String[] items = new String[]{"姓名："+stu.get(position).stuName, "父亲："+stu.get(position).babanum, "母亲："
															 +stu.get(position).mamanum,"站点："+stu.get(position).station,"设置人像"};
					new AlertDialog.Builder(BusinfoActivity.this)
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
											/*
											busInfo result=((Application)BusinfoActivity.this.getApplicationContext()).mBusInfo;
											int iline;
											iline=result.getLinechecked();
											for(int i=0; i<result.getLine().get(iline).getStationcount();i++) {
												for (int j = 0; j < result.getLine().get(iline).getStationlist().get(i).getStudentcount(); j++) {
													if(result.getLine().get(iline).getStationlist().get(i).getStudentlist().get(j).getStuid()==stu.get(position).stuId)
													{
														//((Application)BusinfoActivity.this.getApplicationContext()).mBusInfo.getLine().get(iline)
																//.getStationlist().get(i).getStudentlist().get(j).setIsLeave(true);
														stu.get(position).jia=true;
														Toast.makeText(BusinfoActivity.this, stu.get(position).stuName+"请假成功", Toast.LENGTH_SHORT).show();
														holder.jia.setText("假");
													}
												}
											}
											*/
											break;
										case 4:
											new AlertDialog.Builder(BusinfoActivity.this)
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
																	Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
																	((Application)(BusinfoActivity.this.getApplicationContext())).setCaptureImage(uri);
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
			ExtImageView siv;
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





	private void getBusInfo() {

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
			mBusInfo=info;

			initGroupListView(1);
        }



	}

    private void initGroupListView(final int onoffflag) {
		busInfo result=mBusInfo;
		mTestView.setText(""+ result.getLine().get(0).getTeachername() + "  |  "+ result.getLine().get(0).getTeachernumber());
		final QMUIGroupListView.Section lineSection=QMUIGroupListView.newSection(BusinfoActivity.this);
		mGroupListView.removeAllViews();
		lineSection.setTitle("线路");
		mTitle.setText("");



		for (int i = 0; i < result.getLinecount(); i++) {

			final QMUICommonListItemView itemWithSwitch = mGroupListView.createItemView(result.getLine().get(i).getBusnumber()+":"+result.getLine().get(i).getLinename());
			itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

			final int finalI = i;
			itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					lineSection.singleCheck(buttonView, isChecked);
					if(isChecked==true) {
						isLineChecked=true;
						Toast.makeText(getApplicationContext(), "已上线，GPS开始记录", Toast.LENGTH_SHORT).show();
						myApp.mBusInfo.setLinechecked(finalI);
						myApp.mBusStatus.writeOnline(finalI);
						myApp.mBusStatus.writeBusid(mBusInfo.getLine().get(finalI).getBusid());
						myApp.mBusStatus.writeLinename(mBusInfo.getLine().get(finalI).getLinename());
						myApp.mBusStatus.online=finalI;
						//myApp.mBusStatus.isUploaded=false;

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
						Date date = new Date(System.currentTimeMillis());
						myApp.mBusStatus.writeLoginTime(simpleDateFormat.format(date));
						myApp.linechecked=finalI;
						showStu();
						if(onoffflag==1)
							myApp.initGPS();



					}
					else {
						isLineChecked=false;
						Toast.makeText(getApplicationContext(), "已下线", Toast.LENGTH_SHORT).show();
						myApp.mBusInfo.setLinechecked(-1);
						myApp.mClient.stop();
                        myApp.mBusStatus.writeOnline(-1);
                        myApp.mBusStatus.reInit();
						closeGPS();
						mAdapter.clearAll();
                        mTitle.setText("");
					}


				}
			});

			int size = QMUIDisplayHelper.dp2px(getApplicationContext(), 20);
			lineSection.addItemView(itemWithSwitch, null);
		}
		lineSection.addTo(mGroupListView);

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

    public void showStu(){
		busInfo result=((Application)BusinfoActivity.this.getApplicationContext()).mBusInfo;
		int iline;
		iline=result.getLinechecked();
        stuSize=0;
		stu.clear();

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
        mTitle.setText("本线路共"+ (stuSize) +"人");
		mRecyclerView.setAdapter(mAdapter);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)



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
		call = request.closeGPS(mBusInfo.getLine().get(linechecked).getBusid());

		myApp.mClient.stop();

		//步骤6:发送网络请求(异步)
		call.enqueue(new Callback<Translation>() {
			@Override
			public void onResponse(Call<Translation> call, Response<Translation> response) {
				Log.e("xfeng", "onResponse: "+response.body() );

				Translation translation = response.body();
				if(translation.getStatus()==1){
					Toast.makeText(getApplicationContext(), "下线成功", Toast.LENGTH_SHORT).show();
					myApp.mBusStatus.reInit();
                    myApp.buslog= new busRecordInfo();
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





