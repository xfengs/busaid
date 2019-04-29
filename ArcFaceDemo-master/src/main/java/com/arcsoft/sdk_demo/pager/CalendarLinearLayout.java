package com.arcsoft.sdk_demo.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.arcsoft.sdk_demo.fragment.FragmentAdapter;
import com.haibin.calendarview.CalendarLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 如果嵌套各种View出现事件冲突，可以实现这个方法即可
 */
public class CalendarLinearLayout extends LinearLayout implements CalendarLayout.CalendarScrollView {


    private FragmentAdapter mAdapter;

    public CalendarLinearLayout(Context context) {
        super(context);
    }

    public CalendarLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 如果你想让下拉无效，return false
     *
     * @return isScrollToTop
     */
    @Override
    public boolean isScrollToTop() {
       // return false;
        if(mAdapter == null) {
            if (getChildCount() > 1 && getChildAt(1) instanceof ViewPager) {
                ViewPager viewPager = (ViewPager) getChildAt(1);
                mAdapter = (FragmentAdapter) viewPager.getAdapter();
            }
        }
        if(mAdapter.getCurFragment() instanceof PagerFragment)
            return mAdapter != null && ((PagerFragment) mAdapter.getCurFragment()).isScrollTop();
        else if(mAdapter.getCurFragment() instanceof LocalLogFragment)
            return mAdapter != null && ((LocalLogFragment) mAdapter.getCurFragment()).isScrollTop();
        else
            return false;
    }

}
