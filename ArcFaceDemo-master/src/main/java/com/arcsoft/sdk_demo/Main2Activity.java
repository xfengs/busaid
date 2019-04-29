package com.arcsoft.sdk_demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.sdk_demo.adapter.MyFragAdapter;
import com.arcsoft.sdk_demo.fragment.HisPage;
import com.arcsoft.sdk_demo.fragment.MainPage;
import com.arcsoft.sdk_demo.fragment.RecPage;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.utils.Common;
import com.arcsoft.sdk_demo.utils.CrashHandler;
import com.arcsoft.sdk_demo.widget.MyViewPager;
import com.connect.busInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class Main2Activity extends AppCompatActivity {

    private Application myApp;
    public FragmentManager fragmentManager;

    private final String TAG = this.getClass().toString();
    private busInfo mBusInfo;

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE_OP = 2;
    private static final int REQUEST_CODE_OP = 3;

    //ViewPager viewPager;
    MyViewPager viewPager;
    public BottomNavigationView navigation;//底部导航栏对象
    List<Fragment> listFragment = new ArrayList<Fragment>();//存储页面对象

    private List<StuInfo> stu = new ArrayList<StuInfo>();

    QMUIPullRefreshLayout mPullRefreshLayout;
    QMUITipDialog tipDialog;
    FrameLayout mEmptyView;
    private ImageView mSplash;
    private TextView mLoading;

    public boolean isHasGroove = true;
    public String hisDate = "";

    ///权限部分
    public static int PERMISSION_REQ = 0x123456;
    private String[] mPermission = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private List<String> mRequestPermission = new ArrayList<String>();


    ////////////函数///////////////////
    public String getHisDate() {
        return hisDate;
    }

    public void setHisDate(String hisDate) {
        this.hisDate = hisDate;
    }

    public boolean getIsHasGroove() {
        return isHasGroove;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        myApp = (Application) this.getApplication();

        //闪退检测
        makeDirectory(myApp.mPath + "/crash");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        CrashHandler.getInstance().init(this, myApp);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        isHasGroove = isOppoScreenHasGroove(this);

        initView();
    }


    public static void makeDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


    private void initView() {
        //mSplash=findViewById(R.id.splash);
        mLoading = findViewById(R.id.loading);

        Common commonUtil = new Common();
        //commonUtil.scaleImage(Main2Activity.this, findViewById(R.id.splash), R.drawable.splash);
       // hideBottomUIMenu();
        initData();
    }


    private void initData() {

        viewPager = (MyViewPager) findViewById(R.id.view_pager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        viewPager.setOffscreenPageLimit(3);

        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        listFragment.add(new MainPage());
        listFragment.add(new RecPage());
        listFragment.add(new HisPage());
        final MyFragAdapter myAdapter = new MyFragAdapter(getSupportFragmentManager(), Main2Activity.this, listFragment);


        //导航栏点击事件和ViewPager滑动事件,让两个控件相互关联
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //这里设置为：当点击到某子项，ViewPager就滑动到对应位置

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_dashboard:
                        if (myApp.mBusInfo.getLinechecked() == -1) {
                            tipDialog = new QMUITipDialog.Builder(Main2Activity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                    .setTipWord("请先选择线路")
                                    .create();
                            tipDialog.show();
                            viewPager.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tipDialog.dismiss();
                                }
                            }, 1500);
                            return false;
                        } else
                            viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //注意这个方法滑动时会调用多次，下面是参数解释：
                //position当前所处页面索引,滑动调用的最后一次绝对是滑动停止所在页面
                //positionOffset:表示从位置的页面偏移的[0,1]的值。
                //positionOffsetPixels:以像素为单位的值，表示与位置的偏移
            }

            @Override
            public void onPageSelected(int position) {
                //该方法只在滑动停止时调用，position滑动停止所在页面位置
                //当滑动到某一位置，导航栏对应位置被按下
                if (myApp.mBusInfo.getLinechecked() != -1)
                    navigation.getMenu().getItem(position).setChecked(true);
                //这里使用navigation.setSelectedItemId(position);无效，
                //setSelectedItemId(position)的官网原句：Set the selected
                // menu item ID. This behaves the same as tapping on an item
                //未找到原因
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //这个方法在滑动是调用三次，分别对应下面三种状态
                // 这个方法对于发现用户何时开始拖动，
                // 何时寻呼机自动调整到当前页面，或何时完全停止/空闲非常有用。
                //                state表示新的滑动状态，有三个值：
                //                SCROLL_STATE_IDLE：开始滑动（空闲状态->滑动），实际值为0
                //                SCROLL_STATE_DRAGGING：正在被拖动，实际值为1
                //                SCROLL_STATE_SETTLING：拖动结束,实际值为2
            }
        });

        viewPager.setAdapter(myAdapter);
        //mSplash.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);


        Thread Permission = new Thread() {
            @Override
            public void run() {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    for (String one : mPermission) {
                        if (PackageManager.PERMISSION_GRANTED != Main2Activity.this.checkPermission(one, Process.myPid(), Process.myUid())) {
                            mRequestPermission.add(one);
                        }
                    }
                    if (!mRequestPermission.isEmpty()) {
                        Main2Activity.this.requestPermissions(mRequestPermission.toArray(new String[mRequestPermission.size()]), PERMISSION_REQ);
                        return;
                    }
                }

                Main2Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        Permission.start();

    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //判断手机是否有刘海
    public static boolean isOppoScreenHasGroove(Context context) {
        boolean isHasGroove = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        return isHasGroove;
    }

    private FragmentSkipInterface mFragmentSkipInterface;

    public void setFragmentSkipInterface(FragmentSkipInterface fragmentSkipInterface) {
        mFragmentSkipInterface = fragmentSkipInterface;
    }

    /**
     * Fragment跳转
     */
    public void skipToFragment() {
        if (mFragmentSkipInterface != null) {
            mFragmentSkipInterface.gotoFragment(viewPager);
        }
    }

    public interface FragmentSkipInterface {
        /**
         * ViewPager中子Fragment之间跳转的实现方法
         */
        void gotoFragment(ViewPager viewPager);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        // 版本兼容
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        if (requestCode == PERMISSION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                for (String one : mPermission) {
                    if (permissions[i].equals(one) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        mRequestPermission.remove(one);
                    }
                }
            }
        }

        if (!mRequestPermission.isEmpty()) {
            Toast.makeText(this, "未授权!", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Main2Activity.this.finish();
                }
            }, 3000);
        }


    }

}
