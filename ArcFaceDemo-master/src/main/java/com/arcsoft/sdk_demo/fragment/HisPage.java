package com.arcsoft.sdk_demo.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.Main2Activity;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.pager.LocalLogFragment;
import com.arcsoft.sdk_demo.pager.PagerFragment;
import com.google.android.material.tabs.TabLayout;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class HisPage extends LazyLoadFragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener, CalendarView.OnViewChangeListener {
    @Nullable
   //public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
   //    View view = inflater.inflate(R.layout.fragment_his, container, false);

   //    return view;
   //}
    private Application myApp;
    private FloatingNavigationView mFloatingNavigationView;

    //calendar
    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    private String HisDate;
    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private TextView mTabRed;
    TabLayout tabLayout;

    public String getHisDate() {
        return HisDate;
    }

    public int setContentView() {
        return R.layout.fragment_his;
    }

    @SuppressLint("ResourceAsColor")
    public void initView(View view) {

        myApp=(Application) getActivity().getApplication();

       // setStatusBarDarkMode();
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mTextYear = (TextView) findViewById(R.id.tv_year);
        mTextLunar = (TextView) findViewById(R.id.tv_lunar);
        mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);

        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnViewChangeListener(this);

        ((Main2Activity)getActivity()).setHisDate(mCalendarView.getCurYear()+"-"+mCalendarView.getCurMonth()+"-"+mCalendarView.getCurDay());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        FragmentAdapter adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        adapter.reset(new String[]{"历史记录","未上传记录"});
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(PagerFragment.newInstance());
        fragments.add(LocalLogFragment.newInstance());
        adapter.reset(fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    protected void lazyLoad() {
       // String message = "Fragment3" + (isInit ? "已经初始并已经显示给用户可以加载数据" : "没有初始化不能加载数据");
        //showToast(message);
        //Log.d(TAG, message);

        ((PagerFragment)getActivity().getSupportFragmentManager().getFragments().get(3)).loadlast();


 /*
        tablayout上的标记
        tabLayout.getTabAt(1).setCustomView(R.layout.tab_with_red_point);

        final TextView mTabTitle= (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tv_tab_title);
        mTabRed= (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.iv_tab_red);
        mTabTitle.setText("未上传记录");
        final int[] count = {0};
        //添加tabLayout选中监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //设置选中时的文字颜色
                if (tab.getCustomView() != null) {
                    mTabTitle.setTextColor(0xff111111);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //设置未选中时的文字颜色
                if (tab.getCustomView() != null) {
                    mTabTitle.setTextColor(0xffaaaaaa);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //写出记录
                File f=new File(myApp.mPath+"/buslog");
                File[] file=f.listFiles();

                if(file!=null) {
                    for (int i = 0; i < file.length; i++) {
                        if (!file[i].getName().substring(file[i].getName().length() - 1, file[i].getName().length()).equals("u")) {
                            count[0]++;
                        }
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTabRed.setText(String.valueOf(count[0]));
                    }
                });
            }
        }).start();*/
    }

    @Override
    protected void stopLoad() {

        Log.d(TAG, "Fragment3" + "已经对用户不可见，可以停止加载数据");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());


        ((Main2Activity)getActivity()).setHisDate(calendar.getYear()+"-"+calendar.getMonth()+"-"+calendar.getDay());


    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    @Override
    public void onViewChange(boolean isMonthView) {


    }
}
