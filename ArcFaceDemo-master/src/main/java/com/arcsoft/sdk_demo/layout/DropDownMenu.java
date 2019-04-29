package com.arcsoft.sdk_demo.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jayfang.dropdownmenu.MenuListAdapter;
import com.jayfang.dropdownmenu.OnMenuSelectedListener;
import com.jayfang.dropdownmenu.R.color;
import com.jayfang.dropdownmenu.R.drawable;
import com.jayfang.dropdownmenu.R.id;
import com.jayfang.dropdownmenu.R.layout;

import java.util.ArrayList;
import java.util.List;

public class DropDownMenu extends LinearLayout {
    private List<MenuListAdapter> mMenuAdapters = new ArrayList();
    private List<String[]> mMenuItems = new ArrayList();
    private List<TextView> mTvMenuTitles = new ArrayList();
    private List<RelativeLayout> mRlMenuBacks = new ArrayList();
    private List<ImageView> mIvMenuArrow = new ArrayList();
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ListView mMenuList;
    private RelativeLayout mRlShadow;
    private OnMenuSelectedListener mMenuSelectedListener;
    private int mMenuCount;
    private int mShowCount;
    private int mRowSelected = 0;
    private int mColumnSelected = 0;
    private int mMenuTitleTextColor;
    private int mMenuTitleTextSize;
    private int mMenuPressedTitleTextColor;
    private int mMenuPressedBackColor;
    private int mMenuBackColor;
    private int mMenuListTextSize;
    private int mMenuListTextColor;
    private boolean mShowCheck;
    private boolean mShowDivider;
    private int mMenuListBackColor;
    private int mMenuListSelectorRes;
    private int mArrowMarginTitle;
    private int mCheckIcon;
    private int mUpArrow;
    private int mDownArrow;
    private boolean mDrawable = false;
    private String[] mDefaultMenuTitle;
    private boolean isDebug = true;

    public DropDownMenu(Context mContext) {
        super(mContext);
        this.init(mContext);
    }

    public DropDownMenu(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.init(mContext);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        View popWindows = LayoutInflater.from(mContext).inflate(layout.popupwindow_menu, (ViewGroup)null);
        this.mPopupWindow = new PopupWindow(popWindows, -1, -2, true);
        this.mMenuList = (ListView)popWindows.findViewById(id.lv_menu);
        this.mRlShadow = (RelativeLayout)popWindows.findViewById(id.rl_menu_shadow);
        this.mMenuCount = 2;
        this.mShowCount = 5;
        this.mMenuTitleTextColor = this.getResources().getColor(color.default_menu_text);
        this.mMenuPressedBackColor = this.getResources().getColor(color.default_menu_press_back);
        this.mMenuPressedTitleTextColor = this.getResources().getColor(color.default_menu_press_text);
        this.mMenuBackColor = this.getResources().getColor(color.default_menu_back);
        this.mMenuListBackColor = this.getResources().getColor(color.white);
        this.mMenuListSelectorRes = color.white;
        this.mMenuTitleTextSize = 18;
        this.mArrowMarginTitle = 10;
        this.mShowCheck = true;
        this.mShowDivider = true;
        this.mCheckIcon = drawable.ico_make;
        this.mUpArrow = drawable.arrow_up;
        this.mDownArrow = drawable.arrow_down;
    }

    public void setmMenuItems(List<String[]> menuItems) {
        this.mMenuItems = menuItems;
        this.mDrawable = true;
        this.invalidate();
    }

    public void setmMenuCount(int menuCount) {
        this.mMenuCount = menuCount;
    }

    public void setShowDivider(boolean mShowDivider) {
        this.mShowDivider = mShowDivider;
    }

    public void setmMenuListBackColor(int menuListBackColor) {
        this.mMenuListBackColor = menuListBackColor;
    }

    public void setmMenuListSelectorRes(int menuListSelectorRes) {
        this.mMenuListSelectorRes = menuListSelectorRes;
    }

    public void setmArrowMarginTitle(int arrowMarginTitle) {
        this.mArrowMarginTitle = arrowMarginTitle;
    }

    public void setmMenuPressedTitleTextColor(int menuPressedTitleTextColor) {
        this.mMenuPressedTitleTextColor = menuPressedTitleTextColor;
    }

    public void setDefaultMenuTitle(String[] mDefaultMenuTitle) {
        this.mDefaultMenuTitle = mDefaultMenuTitle;
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public void setmShowCount(int showCount) {
        this.mShowCount = showCount;
    }

    public void setmMenuTitleTextColor(int menuTitleTextColor) {
        this.mMenuTitleTextColor = menuTitleTextColor;
    }

    public void setmMenuTitleTextSize(int menuTitleTextSize) {
        this.mMenuTitleTextSize = menuTitleTextSize;
    }

    public void setmMenuBackColor(int menuBackColor) {
        this.mMenuBackColor = menuBackColor;
    }

    public void setmMenuPressedBackColor(int menuPressedBackColor) {
        this.mMenuPressedBackColor = menuPressedBackColor;
    }

    public void setmMenuListTextColor(int menuListTextColor) {
        this.mMenuListTextColor = menuListTextColor;

        for(int i = 0; i < this.mMenuAdapters.size(); ++i) {
            ((MenuListAdapter)this.mMenuAdapters.get(i)).setTextColor(this.mMenuListTextColor);
        }

    }

    public void setmMenuListTextSize(int menuListTextSize) {
        this.mMenuListTextSize = menuListTextSize;

        for(int i = 0; i < this.mMenuAdapters.size(); ++i) {
            ((MenuListAdapter)this.mMenuAdapters.get(i)).setTextSize(menuListTextSize);
        }

    }

    public void setShowCheck(boolean mShowCheck) {
        this.mShowCheck = mShowCheck;
    }

    public void setmCheckIcon(int checkIcon) {
        this.mCheckIcon = checkIcon;
    }

    public void setmUpArrow(int upArrow) {
        this.mUpArrow = upArrow;
    }

    public void setmDownArrow(int downArrow) {
        this.mDownArrow = downArrow;
    }

    public void setMenuSelectedListener(OnMenuSelectedListener menuSelectedListener) {
        this.mMenuSelectedListener = menuSelectedListener;
    }
    /**
     * xfeng
     * @param option
     */
    public void resetDefaultMenuTitle(int [] option) {
        this.mDefaultMenuTitle = mDefaultMenuTitle;
        for(int i = 0; i < this.mMenuCount; ++i) {
            mColumnSelected=i;
            mRowSelected=option[i];
            if (this.mDefaultMenuTitle != null && this.mDefaultMenuTitle.length != 0) {
                ((TextView) mTvMenuTitles.get(mColumnSelected)).setText(((String[]) mMenuItems.get(mColumnSelected))[mRowSelected]);
            } else {
                ((TextView) mTvMenuTitles.get(mColumnSelected)).setText(((String[])this.mMenuItems.get(i))[0]);
            }
            ((MenuListAdapter) mMenuAdapters.get(mColumnSelected)).setSelectIndex(mRowSelected);
        }
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawable) {
            this.mPopupWindow.setTouchable(true);
            this.mPopupWindow.setOutsideTouchable(true);
            this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            this.mRlShadow.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            this.mMenuList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPopupWindow.dismiss();
                    mRowSelected = position;
                    ((TextView) mTvMenuTitles.get(mColumnSelected)).setText(((String[]) mMenuItems.get(mColumnSelected))[mRowSelected]);
                    ((ImageView) mIvMenuArrow.get(mColumnSelected)).setImageResource(mDownArrow);
                    ((MenuListAdapter) mMenuAdapters.get(mColumnSelected)).setSelectIndex(mRowSelected);
                    if (mMenuSelectedListener == null && isDebug) {
                        Toast.makeText(mContext, "MenuSelectedListener is  null", Toast.LENGTH_LONG).show();
                    } else {
                        mMenuSelectedListener.onSelected(view, mRowSelected, mColumnSelected);
                    }

                }
            });
            this.mPopupWindow.setOnDismissListener(new OnDismissListener() {
                public void onDismiss() {
                    for(int i = 0; i < mMenuCount; ++i) {
                        ((ImageView) mIvMenuArrow.get(i)).setImageResource(mDownArrow);
                        ((RelativeLayout) mRlMenuBacks.get(i)).setBackgroundColor(mMenuBackColor);
                        ((TextView) mTvMenuTitles.get(i)).setTextColor(mMenuTitleTextColor);
                    }

                }
            });
            if (this.mMenuItems.size() != this.mMenuCount) {
                if (this.isDebug) {
                    Toast.makeText(this.mContext, "Menu item is not setted or incorrect", Toast.LENGTH_LONG).show();
                }

                return;
            }

            int width;
            if (this.mMenuAdapters.size() == 0) {
                for(width = 0; width < this.mMenuCount; ++width) {
                    MenuListAdapter adapter = new MenuListAdapter(this.mContext, (String[])this.mMenuItems.get(width));
                    adapter.setShowCheck(this.mShowCheck);
                    adapter.setCheckIcon(this.mCheckIcon);
                    this.mMenuAdapters.add(adapter);
                }
            } else if (this.mMenuAdapters.size() != this.mMenuCount) {
                if (this.isDebug) {
                    Toast.makeText(this.mContext, "If you want set Adapter by yourself,please ensure the number of adpaters equal mMenuCount", Toast.LENGTH_LONG).show();
                }

                return;
            }

            width = this.getWidth();

            for(int i = 0; i < this.mMenuCount; ++i) {
                final int j=i;
                final RelativeLayout v = (RelativeLayout)LayoutInflater.from(this.mContext).inflate(layout.menu_item, (ViewGroup)null, false);
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width / this.mMenuCount, -2);
                v.setLayoutParams(parms);
                TextView tv = (TextView)v.findViewById(id.tv_menu_title);
                tv.setTextColor(this.mMenuTitleTextColor);
                tv.setTextSize((float)this.mMenuTitleTextSize);
                if (this.mDefaultMenuTitle != null && this.mDefaultMenuTitle.length != 0) {
                    tv.setText(this.mDefaultMenuTitle[i]);
                } else {
                    tv.setText(((String[])this.mMenuItems.get(i))[0]);
                }

                this.addView(v, i);
                this.mTvMenuTitles.add(tv);
                RelativeLayout rl = (RelativeLayout)v.findViewById(id.rl_menu_head);
                rl.setBackgroundColor(this.mMenuBackColor);
                this.mRlMenuBacks.add(rl);
                ImageView iv = (ImageView)v.findViewById(id.iv_menu_arrow);
                this.mIvMenuArrow.add(iv);
                ((ImageView)this.mIvMenuArrow.get(i)).setImageResource(this.mDownArrow);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)iv.getLayoutParams();
                params.leftMargin = this.mArrowMarginTitle;
                iv.setLayoutParams(params);
                v.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        mMenuList.setAdapter((ListAdapter) mMenuAdapters.get(j));
                        View childView;
                        RelativeLayout.LayoutParams parms;
                        if (((MenuListAdapter) mMenuAdapters.get(j)).getCount() > mShowCount) {
                            childView = ((MenuListAdapter) mMenuAdapters.get(j)).getView(0, (View)null, mMenuList);
                            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                            parms = new RelativeLayout.LayoutParams(-1, childView.getMeasuredHeight() * mShowCount);
                            mMenuList.setLayoutParams(parms);
                        } else {
                            childView = ((MenuListAdapter) mMenuAdapters.get(j)).getView(0, (View)null, mMenuList);
                            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                            parms = new RelativeLayout.LayoutParams(-1, -2);
                            mMenuList.setLayoutParams(parms);
                        }

                        if (!mShowDivider) {
                            mMenuList.setDivider((Drawable)null);
                        }

                        mMenuList.setBackgroundColor(mMenuListBackColor);
                        mMenuList.setSelector(mMenuListSelectorRes);
                        mColumnSelected = j;
                        ((TextView) mTvMenuTitles.get(j)).setTextColor(mMenuPressedTitleTextColor);
                        ((RelativeLayout) mRlMenuBacks.get(j)).setBackgroundColor(mMenuPressedBackColor);
                        ((ImageView) mIvMenuArrow.get(j)).setImageResource(mUpArrow);
                        mPopupWindow.showAsDropDown(v);
                    }
                });
            }

            this.mDrawable = false;
        }

    }
}
