package com.arcsoft.sdk_demo.pager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.adapter.GridRecyclerAdapter;
import com.arcsoft.sdk_demo.adapter.LinearRecyclerAdapter;
import com.arcsoft.sdk_demo.busRecordInfo;
import com.arcsoft.sdk_demo.fragment.LazyLoadFragment;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.arcsoft.sdk_demo.utils.Common;
import com.connect.BusLogHisInfo;
import com.connect.StatusJson;
import com.connect.busInfo;
import com.connect.interfaceHttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayfang.dropdownmenu.DropDownMenu;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tuacy.pinnedheader.PinnedHeaderRecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocalLogFragment extends LazyLoadFragment {

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

    private TextView mText;

    private LinearRecyclerAdapter mLinearRecyclerAdapter;
    QMUIGroupListView mGroupListView;
    private ScrollView mScroll;
    List<QMUICommonListItemView> itemWithDetailBelow = new ArrayList<QMUICommonListItemView>();


    List<String > s=new ArrayList<>();
    private File[] file;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    public static LocalLogFragment newInstance() {
        return new LocalLogFragment();
    }


    protected int getLayoutId() {
        return R.layout.fragment_locallog;
    }


    protected void initView(final View view) {

        myApp=(Application) getActivity().getApplication();
        mGroupListView =(QMUIGroupListView) view.findViewById(R.id.groupLogListView);
        mScroll=(ScrollView)view.findViewById(R.id.local_his_scroll);

    }


    protected void initData() {

    }

    public boolean isScrollTop() {
        return mScroll != null && mScroll.getScrollY() == 0;
    }

    // @Override
    protected int setContentView() {
        return R.layout.fragment_locallog;
    }

    //@Override
    protected void lazyLoad() {

        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        tipDialog.show();
        initData();
        obtainData();
    }


    private busRecordInfo getBuslog(File logfile) {

        String jsonData="";

        try {
            FileInputStream fileInputStream = new FileInputStream(logfile);
            byte[] b=new byte[fileInputStream.available()];

            //将字节流中的数据传递给字节数组
            fileInputStream.read(b);

            //将字节数组转为字符串
            jsonData=new String(b); } catch (Exception e) {
            e.printStackTrace(); }
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<busRecordInfo>() {}.getType();
        busRecordInfo info=gson.fromJson(jsonData,busRecordInfo.class);
        if(info.getStudentlist()==null)
            info.setStudentlist(new ArrayList<busRecordInfo.StudentlistBean>());
        return info;

    }

    private void obtainData() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                File f=new File(myApp.mPath+"/buslog/"+myApp.tmobile);
                file=f.listFiles();
                String fname="";
                String goorbackstring = "";

                if(file!=null) {
                    itemWithDetailBelow.clear();
                    for (int i=0; i < file.length;i++){
                        if(!file[i].getName().substring(file[i].getName().length()-1,file[i].getName().length()).equals("u")) {
                            s.add(file[i].getName());
                            fname = fname + file[i].getName() + "\n";

                            busRecordInfo oldbuslog = getBuslog(file[i]);
                            String date = file[i].getName().split("_")[3];
                            int no = Integer.parseInt(file[i].getName().split("_")[1]);

                            switch (Integer.parseInt(file[i].getName().split("_")[4])) {
                                case 1:
                                    goorbackstring = "上午上学";
                                    break;
                                case 2:
                                    goorbackstring = "中午放学";
                                    break;
                                case 3:
                                    goorbackstring = "中午上学";
                                    break;
                                case 4:
                                    goorbackstring = "下午放学";
                                    break;
                            }

                            String title;
                            title = date+":"+oldbuslog.getBusnumber() + ":" + myApp.mBusInfo.getLine().get(no).getLinename() + ":"+goorbackstring;
                            QMUICommonListItemView lv = mGroupListView.createItemView(title);
                            lv.setOrientation(QMUICommonListItemView.VERTICAL);
                            lv.setDetailText("已乘车：" + oldbuslog.getStudentlist().size() + "人");
                            lv.setOrientation(QMUICommonListItemView.VERTICAL);
                            lv.setTag(i);
                            itemWithDetailBelow.add(lv);
                        }
                        else{
                            String date=commonUtil.getDate();
                            if(!date.equals(file[i].getName().split("_")[3])){
                                file[i].delete();
                            }
                        }
                    }
                }

                Message msg = new Message();
                //对消息一个识别号，便于handler能够识别
                msg.what = 1;
                mFloatHandler.sendMessage(msg);

            }

        }).start();
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof QMUICommonListItemView) {
                final int i=(int)v.getTag();
                CharSequence text = ((QMUICommonListItemView) v).getText();

                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setTitle(text.toString().split(":")[1]+":"+text.toString().split(":")[2])
                        .setMessage(text.toString().split(":")[3])
                        .addAction(0, "上传", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {

                               if(!upload(file[i],Integer.parseInt(file[i].getName().split("_")[1])))
                                   Toast.makeText(getActivity(), "记录编码出错！", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                if(file[i].delete()) {
                                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                    lazyLoad();
                                }

                                dialog.dismiss();
                            }
                        })
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .create(mCurrentDialogStyle).show();


            }
        }
    };

    private Handler mFloatHandler =new Handler()
    {
        public void handleMessage(Message msg)
        {
            int what=msg.what;
            Log.i("handler","已经收到消息，消息what："+what+",id:"+Thread.currentThread().getId());

            if(what==1)
            {
                Log.i("handler已接受到消息",""+what);
                QMUIGroupListView.Section lineSection = QMUIGroupListView.newSection(getActivity());

                lineSection.setTitle("点击以下历史记录可进行操作");

                mGroupListView.removeAllViews();
                for(int i=0;i<itemWithDetailBelow.size();i++) {
                    lineSection.addItemView(itemWithDetailBelow.get(i), onClickListener);
                }
                lineSection.addTo(mGroupListView);

                tipDialog.dismiss();
            }
        }
    };


    private boolean upload(File f,int line_no) {

        if (myApp.mBusInfo.getLine().get(line_no).getLineid() != Integer.parseInt(f.getName().split("_")[2]))
            return false;

        String dt = commonUtil.getDateTime();

        busRecordInfo blog = getBuslog(f);

        int total = 0;
        final busInfo result = myApp.mBusInfo;
        stu.clear();
        for (int i = 0; i < result.getLine().get(line_no).getStationcount(); i++) {
            total += result.getLine().get(line_no).getStationlist().get(i).getStudentcount();
            for (int j = 0; j < result.getLine().get(line_no).getStationlist().get(i).getStudentcount(); j++) {
                StuInfo tmpStu = new StuInfo();
                tmpStu.stuId = result.getLine().get(line_no).getStationlist().get(i).getStudentlist().get(j).getStuid();
                tmpStu.stuName = result.getLine().get(line_no).getStationlist().get(i).getStudentlist().get(j).getStuname();
                stu.add(tmpStu);
            }
        }


        busRecordInfo uploadInfo = new busRecordInfo();
        uploadInfo.setBusId(blog.getBusId());
        uploadInfo.setOrder(blog.getOrder());
        uploadInfo.setLineId(blog.getLineId());
        uploadInfo.setBusnumber(blog.getBusnumber());
        uploadInfo.setChengcheCount(blog.getStudentlist().size());
        uploadInfo.setWeichengCount(total - blog.getStudentlist().size());
        uploadInfo.setQingjiaCount(0);
        uploadInfo.setTotalCount(total);
        uploadInfo.setDatetime(f.getName().split("_")[3]+" 00:00:00");
        uploadInfo.setDate(Integer.parseInt(f.getName().split("-")[1]));

        List<busRecordInfo.StudentlistBean> bltmp1 = new ArrayList<busRecordInfo.StudentlistBean>();
        int flag = 0;


        for (StuInfo s : stu) {
            flag=0;
            if (blog.getStudentlist() != null) {
                for (busRecordInfo.StudentlistBean b : blog.getStudentlist()) {
                    if (s.stuId == b.getStuid()) {
                        bltmp1.add(b);
                        flag = 1;
                    }
                }
            }
            //添加手动设置的学生
            if (flag == 0) {
                busRecordInfo.StudentlistBean btmp = new busRecordInfo.StudentlistBean();
                btmp.setStuid(s.stuId);
                btmp.setStuname(s.stuName);
                btmp.setDatetime(f.getName().split("_")[3]+" 00:00:00");
                btmp.setDate(Integer.parseInt(f.getName().split("-")[1]));
                btmp.setStatus(s.status);
                bltmp1.add(btmp);
            }
        }
        uploadInfo.setStudentlist(bltmp1);

        sendData(commonUtil.toJson(uploadInfo, 1),f);

        return true;
    }

    public void sendData(String data, final File f) {
        //Toast.makeText(this, "xfeng", Toast.LENGTH_SHORT).show();

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myApp.AppURL) //http://fy.iciba.com/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        interfaceHttp request = retrofit.create(interfaceHttp.class);
        Call<StatusJson> call;
        call = request.uploadBusLog(data);

        final QMUITipDialog tipDialog;
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("上传数据中")
                .create();
        tipDialog.show();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<StatusJson>() {
            @Override
            public void onResponse(Call<StatusJson> call, Response<StatusJson> response) {

                //Log.e("xfeng", "onResponse: "+response.body() );
                if (response.isSuccessful()) {
                    StatusJson StatusJson = response.body();
                        //Toast.makeText(myApp.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        tipDialog.dismiss();
                        final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("上传成功")
                                .create();
                        doneDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doneDialog.dismiss();
                            }
                        }, 1000);

                        lazyLoad();
                        commonUtil.changeUploaded(f);


                }
                else{
                    tipDialog.dismiss();
                    final QMUITipDialog doneDialog = new QMUITipDialog.Builder(getActivity())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("上传失败:"+response.errorBody().toString())
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
            public void onFailure(Call<StatusJson> call, Throwable t) {

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

}
