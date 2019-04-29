package com.arcsoft.sdk_demo.adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.R;
import com.arcsoft.sdk_demo.model.StuInfo;
import com.squareup.picasso.Picasso;
import com.tuacy.pinnedheader.PinnedHeaderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GridRecyclerAdapter extends PinnedHeaderAdapter<RecyclerView.ViewHolder> {

	public static final int VIEW_TYPE_ITEM_TIME    = 0;
	private static final int VIEW_TYPE_ITEM_CONTENT = 1;
	private List<StuInfo> stu;//存放数据
	private List<StuInfo> stu0 = new ArrayList<StuInfo>();

	private List<String> mDataList;
	private Context context;
	private Application myApp;
	private GridRecyclerAdapter adapter=this;

	Handler mFloatHandler;
	private boolean animationsLocked = false;
	private boolean delayEnterAnimation = true;
	private int lastAnimatedPosition=-1;

	public int count1,count2,count3,total,isManuel=0;
    /**
     * frag=0 RecPage
     * frag=1 pagerfragment
     * frag=2 localLogfragment
     */
	private int frag;

	public GridRecyclerAdapter(List<StuInfo> stu, Context context,int frag) {
		this.stu = stu;
		this.context=context;
		this.myApp=(Application)context.getApplicationContext();
		this.frag=frag;
		flushAdapter(0);

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM_TIME) {
			return new TitleHolder(LayoutInflater.from(context).inflate(R.layout.item_grid_title, parent, false));
		} else {
			return new ContentHolder(LayoutInflater.from(context).inflate(R.layout.item_grid_content, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

		//runEnterAnimation(holder.itemView,position);

		final String faceFile = String.valueOf(stu.get(position).stuId)+"_"+stu.get(position).stuName;
		String bmppath=myApp.mPath +"/faces/"+faceFile+".jpg";
		File file = new File(bmppath);

		if (getItemViewType(position) == VIEW_TYPE_ITEM_TIME) {
			TitleHolder titleHolder = (TitleHolder) holder;
			titleHolder.mTextTitle.setText(stu.get(position).stuName);
			//titleHolder.mTextTitle.setText(mDataList.get(position));
		} else {
			ContentHolder contentHolder = (ContentHolder) holder;
			//Picasso.with(contentHolder.mImage.getContext()).load(mDataList.get(position)).into(contentHolder.mImage);
			contentHolder.mName.setText(stu.get(position).stuName);
			if(file.exists())
				Picasso.with(contentHolder.mImage.getContext()).load(file).into(contentHolder.mImage);
			else
				Picasso.with(contentHolder.mImage.getContext()).load(R.drawable.default_avatar).into(contentHolder.mImage);
		}

		mFloatHandler=new Handler()
		{
			public void handleMessage(android.os.Message msg)
			{
				int what=msg.what;
				Log.i("handler","已经收到消息，消息what："+what+",id:"+Thread.currentThread().getId());

				if(what==2)
				{
					Log.i("handler已接受到消息",""+what);
					notifyDataSetChanged();
				}
			}
		};

		//点击事件
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final String[] items;
				/*
				if( myApp.mBusStatus.isUploaded[myApp.order_flag-1]==true) {
					if (stu.get(position).isTaken == true)
						items = new String[]{stu.get(position).stuName, "家长1：" + stu.get(position).babanum, "家长2："
								+ stu.get(position).mamanum, "站点：" + stu.get(position).station};
					else
						items = new String[]{stu.get(position).stuName, "家长1：" + stu.get(position).babanum, "家长2："
								+ stu.get(position).mamanum, "站点：" + stu.get(position).station};
				}
				else
				{
				*/
					if (stu.get(position).isTaken == true)
						items = new String[]{stu.get(position).stuName, "家长1：" + stu.get(position).babanum, "家长2："
								+ stu.get(position).mamanum, "站点：" + stu.get(position).station, "设为未乘车"};
					else
						items = new String[]{stu.get(position).stuName, "家长1：" + stu.get(position).babanum, "家长2："
								+ stu.get(position).mamanum, "站点：" + stu.get(position).station, "设为已乘车"};
				//}
				
				new AlertDialog.Builder(context)
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
											context.startActivity(intent);
										}
										break;
									case 2:
										if(stu.get(position).mamanum!="") {
											intent = new Intent(Intent.ACTION_DIAL);
											data = Uri.parse("tel:" + stu.get(position).mamanum);
											intent.setData(data);
											context.startActivity(intent);
										}
										break;
									case 4:
										//已乘改未乘
										if(stu.get(position).isTaken==true) {
											stu.get(position).isTaken=false;
											if(stu.get(position).isLeave) {
												stu.get(position).status = 2;
												//myApp.stuModel.addLeave(1,"s");
											}
											else {
												stu.get(position).status = 3;
												//myApp.stuModel.minusShowup(1);

											}
											isManuel=1;
											flushAdapter(1);

											/*Log.i("stucount","total:"+myApp.stuModel.getTotal()
													+",showup:"+myApp.stuModel.getShowup()+",absent:"
													+myApp.stuModel.getAbsent()+",leave:"+myApp.stuModel.getLeave());*/
										}
										//未乘改已乘，包括请假
										else{
											stu.get(position).isTaken=true;
											stu.get(position).status=1;
											stu.get(position).isAuto=false;
											/*if(stu.get(position	).isLeave)
												myApp.stuModel.minusLeave(1,"s");
											else
												myApp.stuModel.minusAbsent(1);*/

											isManuel=1;
											flushAdapter(1);
											/*Log.i("stucount","total:"+myApp.stuModel.getTotal()
													+",showup:"+myApp.stuModel.getShowup()+",absent:"
													+myApp.stuModel.getAbsent()+",leave:"+myApp.stuModel.getLeave());*/
										}
										break;
								}
							}
						})
						.show();
			}
		});
	}

	public void flushAdapter(final int f){
		new Thread(new Runnable() {
			@Override
			public void run() {

				count1=0;
				count2=0;
				count3=0;
				total=0;

				for (int i = 0; i < stu.size(); i++) {
					if (stu.get(i).status==4) {//标题
						stu.remove(i);
					}
				}
				for (int i = 0; i < stu.size(); i++) {
					if (stu.get(i).status==1) {//乘车
						count1++;
					}
					if (stu.get(i).status==2) {//请假
						count2++;
					}
					if (stu.get(i).status==3) {//未乘车
						count3++;
					}
					if (stu.get(i).status!=4) {//总数
						total++;
					}
				}
				if(f==1) {
					Collections.sort(stu);
					int flag1 = 1, flag2 = 1, flag3 = 1;
					for (int i = 0; i < stu.size(); i++) {
						if (stu.get(i).status == 1 && flag1 == 1) {
							stu.add(i, new StuInfo("已乘车学生("+count1+"人)", 4));
							flag1 = 0;
						}
						if (stu.get(i).status == 2 && flag2 == 1) {
							stu.add(i, new StuInfo("已请假学生("+count2+"人)", 4));
							flag2 = 0;
						}  if (stu.get(i).status == 3 && flag3 == 1) {
							stu.add(i, new StuInfo("未乘车未请假学生("+count3+"人)", 4));
							flag3 = 0;
						}
					}

					if(frag==0){

                    }

					Message msg = new Message();
					//对消息一个识别号，便于handler能够识别
					msg.what = 2;
					mFloatHandler.sendMessage(msg);
				}

			}

		}).start();

	}
	private void runEnterAnimation(View view, int position) {

		if (animationsLocked) return;              //animationsLocked是布尔类型变量，一开始为false
		//确保仅屏幕一开始能够容纳显示的item项才开启动画

		if (position > lastAnimatedPosition) {//lastAnimatedPosition是int类型变量，默认-1，
			//这两行代码确保了recyclerview滚动式回收利用视图时不会出现不连续效果
			lastAnimatedPosition = position;
			//view.setTranslationY(300);     //Item项一开始相对于原始位置下方500距离
			view.setAlpha(0.f);           //item项一开始完全透明
			//每个item项两个动画，从透明到不透明，从下方移动到原始位置


			view.animate()
					.translationY(0).alpha(1.f)                                //设置最终效果为完全不透明
					//并且在原来的位置
					.setStartDelay(delayEnterAnimation ? 20 * (position) : 0)//根据item的位置设置延迟时间
					//达到依次动画一个接一个进行的效果
					.setInterpolator(new DecelerateInterpolator(0.5f))     //设置动画位移先快后慢的效果
					.setDuration(400)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							animationsLocked = true;
							//确保仅屏幕一开始能够显示的item项才开启动画
							//也就是说屏幕下方还没有显示的item项滑动时是没有动画效果
						}
					})
					.start();
		}
	}


	@Override
	public int getItemCount() {

        //return mDataList == null ? 0 : mDataList.size();
        return stu == null ? 0 : stu.size();
	}

	@Override
	public int getItemViewType(int position) {
		//if (position % 5 == 0) {
		if(stu.get(position).status==4){
			return VIEW_TYPE_ITEM_TIME;
		} else {
			return VIEW_TYPE_ITEM_CONTENT;
		}
	}

	@Override
	public boolean isPinnedPosition(int position) {
		return getItemViewType(position) == VIEW_TYPE_ITEM_TIME;
	}

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


	static class ContentHolder extends RecyclerView.ViewHolder {

		CircleImageView mImage;
		TextView mName;

		ContentHolder(View itemView) {
			super(itemView);
			mImage = itemView.findViewById(R.id.image_icon);
			mName = itemView.findViewById(R.id.name);
		}
	}

	static class TitleHolder extends RecyclerView.ViewHolder {

		TextView mTextTitle;

		TitleHolder(View itemView) {
			super(itemView);
			mTextTitle = itemView.findViewById(R.id.text_adapter_title_name);
		}
	}

}
