package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.sdk_demo.location.ForegroundActivity;
import com.arcsoft.sdk_demo.location.service.LocationService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.connect.WifiAdmin;
import com.guo.android_extend.widget.HListView;

import org.json.JSONArray;

import java.io.InputStream;

public class MainActivity extends Activity implements OnClickListener {

	private Application myApp;
	private final String TAG = this.getClass().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;

	/*//xfeng add bluetooth
    private static final int REQUEST_CONNECT_DEVICE = 4;
    private static final int REQUEST_ENABLE_BT = 5;
    private static final boolean D = true;

	//xfeng Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	//xfeng Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";*/


    //xfeng add
    private HListView mHFaceView;
	// Layout Views
	private TextView mTitle;
	private TextView mTestView;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    JSONArray jArray;
    String result = null;
    InputStream is = null;
    StringBuilder sb = null;

    //map

	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private LocationService locationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);

        //xfeng add
        mTestView = (TextView) findViewById(R.id.testView);

        // TODO Auto-generated method stub
		View v = this.findViewById(R.id.scan);
        v.setOnClickListener(this);
		v = this.findViewById(R.id.businfo);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.map);
		v.setOnClickListener(this);

        //xfeng


		//首先需要获取到wifi管理者，初始化工具类
		WifiAdmin wifiAdmin = new WifiAdmin(this);
//		wifiAdmin.createAp();


	}
	public boolean onCreateOptionsMenu(Menu menu) {
		//TODO 设置菜单

		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
        //TODO 菜单点击

		return false;
	}

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

		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);

		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {

		}

	}

	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		switch (paramView.getId()) {


			case R.id.scan:
				if( ((Application)getApplicationContext()).mFaceDB.mRegister.isEmpty() ) {
					Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
				} else {
                     startDetector(1);
			 		//new AlertDialog.Builder(this)
					//		.setTitle("请选择相机")
					//		.setIcon(android.R.drawable.ic_dialog_info)
					//		.setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
					//					@Override
					//					public void onClick(DialogInterface dialog, int which) {
					//						startDetector(which);
					//					}
					//				})
					//		.show();
				}
			break;


			case R.id.map:
				Intent it2 = new Intent(MainActivity.this, ForegroundActivity.class);
				it2.putExtra("from",0);
				startActivity(it2);


				break;
			case R.id.businfo:
				Intent it3 = new Intent(MainActivity.this, BusinfoActivity.class);
				it3.putExtra("from",0);
				startActivity(it3);

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


	private void startDetector(int camera) {
		Intent it = new Intent(MainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}



}





