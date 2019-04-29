package com.arcsoft.sdk_demo.pager;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.Main2Activity;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.adapter.GridRecyclerAdapter;
import com.arcsoft.sdk_demo.fragment.LazyLoadFragment;
import com.arcsoft.sdk_demo.layout.DropDownMenu;
import com.arcsoft.sdk_demo.model.BusModel;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.utils.Common;
import com.connect.BusLogHisInfo;
import com.connect.interfaceHttp;
import com.jayfang.dropdownmenu.OnMenuSelectedListener;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tuacy.pinnedheader.PinnedHeaderItemDecoration;
import com.tuacy.pinnedheader.PinnedHeaderRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PagerFragment extends LazyLoadFragment {

    private RecyclerView mRecyclerView;
    private DropDownMenu mMenu;
    private List<String[]> menuList=new ArrayList<String[]>();
    private Application myApp;
    private PinnedHeaderRecyclerView mPinRecyclerView;
    private GridRecyclerAdapter mGridRecyclerAdapter;

    private Common commonUtil=new Common();

    private List<StuInfo> stu = new ArrayList<StuInfo>();
    private List<StuInfo> stu0 = new ArrayList<StuInfo>();
    private BusLogHisInfo  mBusLogHisInfo;
    private int line_no=-1;
    private int type=-1;
    private QMUITipDialog tipDialog;
    private QMUIPullRefreshLayout mPullRefreshLayout;
    private QMUIRoundButton mSubmitButton;



    public static PagerFragment newInstance() {
        return new PagerFragment();
    }


    protected int getLayoutId() {
        return R.layout.fragment_pager;
    }


    protected void initView(final View view) {

        myApp=(Application) getActivity().getApplication();


        mMenu=(DropDownMenu)view.findViewById(R.id.menu);

        mMenu.setmMenuCount(2);//Menu的个数
        mMenu.setmShowCount(4);//Menu展开list数量太多是只显示的个数


        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(15);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(Color.WHITE);//Menu的文字颜色
        mMenu.setmMenuTitleTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListBackColor(Color.WHITE);
        mMenu.setmMenuListSelectorRes(R.color.white);//展开list的listselector
        mMenu.setmMenuListTextColor(0xff212121);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(0xff00A8E1);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(0xff00BCD4);//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.ico_make);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.ico_make);//Menu展开list的勾选图片
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头

        String[] menuString1=new String [myApp.mBusInfo.getLinecount()];
        for(int i=0;i<myApp.mBusInfo.getLinecount();i++)
            menuString1[i]=String.valueOf(i+1)+"."+myApp.mBusInfo.getLine().get(i).getLinename();
        menuList.add(menuString1);
        String[] menuString2=new String [4];
        menuString2[0]="1.上午上学";
        menuString2[1]="2.中午放学";
        menuString2[2]="3.中午上学";
        menuString2[3]="4.下午放学";
        menuList.add(menuString2);
        mMenu.setmMenuItems(menuList);

        String[] menuTitle=new String[2];

            menuTitle[0]="选择线路";
            menuTitle[1]="选择车次";


        mMenu.setDefaultMenuTitle(menuTitle);//默认未选择任何过滤的Menu title

        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {

                if(ColumnIndex==0)
                    line_no=RowIndex;
                else if(ColumnIndex==1)
                    type=RowIndex+1;

                if(line_no!=-1&&type!=-1){
                    //String log_date=((Main2Activity)getActivity()).getHisDate();
                    //getBuslogHis(myApp.mBusInfo.getLine().get(line_no).getLineid(), log_date, type);
                }

            }
        });

        /////////////////////////

        mPinRecyclerView = view.findViewById(R.id.recycler_grid);
        final GridLayoutManager manager = new GridLayoutManager(view.getContext(), 5);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mGridRecyclerAdapter.isPinnedPosition(position)) {
                    return manager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
        mPinRecyclerView.setLayoutManager(manager);
        mGridRecyclerAdapter = new GridRecyclerAdapter(stu0,getActivity(),1);

        QMUIRoundButton mUploadBtn=view.findViewById(R.id.btn_updatebuslog);
        mSubmitButton = view.findViewById(R.id.btn_submit);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stu0.size() > 0) {
                    int count=0;
                    count=mBusLogHisInfo.getData().getCount();


                    mBusLogHisInfo.setTakenCount(mGridRecyclerAdapter.count1);
                    mBusLogHisInfo.getData().getStudentlist().clear();

                    List<BusLogHisInfo.DataBean.StudentlistBean> stu_his=new ArrayList<BusLogHisInfo.DataBean.StudentlistBean>();
                    for (int i = 0; i < stu0.size(); i++) {
                        if(stu0.get(i).status!=4) {
                            BusLogHisInfo.DataBean.StudentlistBean tmpstuhis = new BusLogHisInfo.DataBean.StudentlistBean();
                            tmpstuhis.setStu_id(stu0.get(i).stuId);
                            tmpstuhis.setStu_name(stu0.get(i).stuName);
                            tmpstuhis.setRecord_type(String.valueOf(stu0.get(i).status));
                            if (stu0.get(i).isAuto)
                                tmpstuhis.setIdentity_type("2");
                            else
                                tmpstuhis.setIdentity_type("1");
                            tmpstuhis.setStu_id(stu0.get(i).stuId);

                            stu_his.add(tmpstuhis);
                            mBusLogHisInfo.getData().setStudentlist(stu_his);
                        }
                        else{
                            //count--;
                        }
                    }
                    mBusLogHisInfo.getData().setCount(count);

                    BusModel busModel = new BusModel(getActivity(), stu);
                    busModel.uploadHisData(commonUtil.toJson(mBusLogHisInfo,1));

                } else {
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("无数据")
                            .create();
                    doneDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doneDialog.dismiss();
                        }
                    }, 1000);
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(line_no==-1){
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("请选择线路")
                            .create();
                    doneDialog.show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doneDialog.dismiss();
                        }
                    }, 1000);
                }
                else if(type==-1){

                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("请选择车次")
                            .create();
                    doneDialog.show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doneDialog.dismiss();
                        }
                    }, 1000);
                }
                else {
                    String log_date = ((Main2Activity) getActivity()).getHisDate();
                    getBuslogHis(myApp.mBusInfo.getLine().get(line_no).getLineid(), log_date, type);
                }

            }
        });


        //mPullRefreshLayout=(QMUIPullRefreshLayout)view.findViewById(R.id.pull_to_refresh);
        initData();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    protected void initData() {


    }

    public boolean isScrollTop() {
        return mPinRecyclerView != null && mPinRecyclerView.computeVerticalScrollOffset() == 0;
    }

    // @Override
    protected int setContentView() {
        return R.layout.fragment_pager;
    }

    //@Override
    protected void lazyLoad() {
//        String message = "Fragment3" + (isInit ? "已经初始并已经显示给用户可以加载数据" : "没有初始化不能加载数据");
//        showToast(message);
//        Log.d(TAG, message);

        loadlast();
    }

    public void loadlast(){
        //默认选中当前最近一次已上传数据
        if(myApp.mBusInfo.getLinechecked()!=-1 ){
//            if(commonUtil.isUploaded(myApp.mBusInfo.getLinechecked(),
//                    myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLineid(),myApp)) {
                String[] menuString2=new String [4];
                menuString2[0]="1.上午上学";
                menuString2[1]="2.中午放学";
                menuString2[2]="3.中午上学";
                menuString2[3]="4.下午放学";

                String[] menuTitle=new String[2];
                menuTitle[0] = myApp.mBusInfo.getLine().get(myApp.mBusInfo.getLinechecked()).getLinename();
                menuTitle[1] = menuString2[commonUtil.getOrderFlag()-1];
                line_no=myApp.mBusInfo.getLinechecked();
                type=commonUtil.getOrderFlag();
                int[] option= new int[]{line_no,type-1};
                mMenu.resetDefaultMenuTitle(option);
                //getBuslogHis(myApp.mBusInfo.getLine().get(line_no).getLineid(), commonUtil.getDate(), type);
            }
//        }
    }


    public void getBuslogHis(final int line_id, final String log_date, final int type) {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<BusLogHisInfo> call = request.getBuslogHis(line_id,log_date,type);

        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<BusLogHisInfo>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<BusLogHisInfo> call, Response<BusLogHisInfo> response) {

                if(response.isSuccessful()) {
                    BusLogHisInfo result = response.body();

                    if(!result.isStatus()) {
                        //Toast.makeText(myApp.getApplicationContext(), "更新数据失败", Toast.LENGTH_SHORT).show();

                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("加载失败")
                                .create();
                        doneDialog.show();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                    }

                    else {


                        final QMUITipDialog doneDialog;
                        Handler handler;
                        if(result.getData().getCount()==0)
                        {
                            doneDialog = new QMUITipDialog.Builder(getActivity())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                    .setTipWord("无数据")
                                    .create();
                            doneDialog.show();
                            handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doneDialog.dismiss();
                                }
                            }, 1000);

                            Message msg = new Message();
                            //对消息一个识别号，便于handler能够识别
                            msg.what = 2;
                            mFloatHandler.sendMessage(msg);
                        }
                        else{

                             doneDialog = new QMUITipDialog.Builder(getActivity())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                    .setTipWord("加载成功")
                                    .create();
                            doneDialog.show();
                            handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doneDialog.dismiss();
                                }
                            }, 1000);
                        }

                        //保存数据到json
                        mBusLogHisInfo=result;
                        mBusLogHisInfo.setLine_id(line_id);
                        mBusLogHisInfo.setLog_date(log_date);
                        mBusLogHisInfo.setType(type);
                        obtainData();


                    }

                }
                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("加载失败:"+response.errorBody().toString())
                            .create();
                    doneDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doneDialog.dismiss();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<BusLogHisInfo> call, Throwable t) {

                tipDialog.dismiss();
                final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                        .setTipWord("错误:"+t.getMessage())
                        .create();
                doneDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doneDialog.dismiss();
                    }
                }, 1000);
            }
        });
    }

    private void obtainData() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<BusLogHisInfo.DataBean.StudentlistBean> stu_his=mBusLogHisInfo.getData().getStudentlist();

                int flag = 0;
                int count1 = 0, count2 = 0, count3 = 0;
                stu.clear();

                for(int i=0;i<mBusLogHisInfo.getData().getCount();i++)
                {
                    StuInfo tmpstu=new StuInfo();
                    tmpstu.stuId=stu_his.get(i).getStu_id();
                    tmpstu.stuName=stu_his.get(i).getStu_name();
                    tmpstu.babanum=stu_his.get(i).getTel1();
                    tmpstu.mamanum=stu_his.get(i).getTel2();
                    tmpstu.status=Integer.parseInt(stu_his.get(i).getRecord_type());
                    if(stu_his.get(i).getRecord_type()=="2")
                        tmpstu.isAuto=true;
                    if(tmpstu.status==1) {
                        tmpstu.isTaken = true;
                        count1++;
                    }
                    if(tmpstu.status==2) {
                        tmpstu.isTaken = false;
                        tmpstu.isLeave=true;
                        count2++;
                    }
                    if(tmpstu.status==3) {
                        tmpstu.isTaken = false;
                        count3++;
                    }
                    tmpstu.station=stu_his.get(i).getStation_name();
                    stu.add(tmpstu);
                }

                stu0.clear();
                stu0.addAll(stu);


                Collections.sort(stu0);
                int flag1 = 1, flag2 = 1, flag3 = 1;
                for (int i = 0; i < stu0.size(); i++) {
                    if (stu0.get(i).status == 1 && flag1 == 1) {
                        stu0.add(i, new StuInfo("已乘车学生(" + count1 + "人)", 4));
                        flag1 = 0;
                    } else if (stu0.get(i).status == 2 && flag2 == 1) {
                        stu0.add(i, new StuInfo("已请假学生(" + count2 + "人)", 4));
                        flag2 = 0;
                    } else if (stu0.get(i).status == 3 && flag3 == 1) {
                        stu0.add(i, new StuInfo("未乘车未请假学生(" + count3 + "人)", 4));
                        flag3 = 0;
                    }
                }

                Message msg = new Message();
                //对消息一个识别号，便于handler能够识别
                msg.what = 1;
                mFloatHandler.sendMessage(msg);

            }

        }).start();
    }

    private Handler mFloatHandler =new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            int what=msg.what;
            Log.i("handler","已经收到消息，消息what："+what+",id:"+Thread.currentThread().getId());

            if(what==1)
            {
                Log.i("handler已接受到消息",""+what);

                mPinRecyclerView.setAdapter(mGridRecyclerAdapter);
                mPinRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration());
                tipDialog.dismiss();
            }
            if(what==2)
            {
                Log.i("handler已接受到消息",""+what);
                stu0.clear();
                mPinRecyclerView.setAdapter(mGridRecyclerAdapter);
                mPinRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration());
                tipDialog.dismiss();
               // mPullRefreshLayout.finishRefresh();
            }
        }
    };

}
