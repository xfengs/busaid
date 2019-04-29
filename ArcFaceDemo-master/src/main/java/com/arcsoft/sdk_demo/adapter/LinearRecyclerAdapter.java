package com.arcsoft.sdk_demo.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcsoft.sdk_demo.R;
import com.tuacy.pinnedheader.PinnedHeaderAdapter;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LinearRecyclerAdapter extends PinnedHeaderAdapter<RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_ITEM_TIME    = 0;
	private static final int VIEW_TYPE_ITEM_CONTENT = 1;

	private List<String> mDataList;
 	private Context context;

	public LinearRecyclerAdapter(List<String> dataList, Context context) {
		mDataList = dataList;
		this.context=context;
	}

	public void setData(List<String> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_ITEM_TIME) {
			return new TitleHolder(LayoutInflater.from(context).inflate(R.layout.item_linear_title, parent, false));
		} else {
			return new ContentHolder(LayoutInflater.from(context).inflate(R.layout.item_linear_content, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (getItemViewType(position) == VIEW_TYPE_ITEM_TIME) {
			TitleHolder titleHolder = (TitleHolder) holder;
			titleHolder.mTextTitle.setText(mDataList.get(position));
		} else {
			ContentHolder contentHolder = (ContentHolder) holder;
			contentHolder.mTextTitle.setText(mDataList.get(position));
		}
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (position % 5 == 0) {
			return VIEW_TYPE_ITEM_TIME;
		} else {
			return VIEW_TYPE_ITEM_CONTENT;
		}
	}

	@Override
	public boolean isPinnedPosition(int position) {
		return getItemViewType(position) == VIEW_TYPE_ITEM_TIME;
	}

	static class ContentHolder extends RecyclerView.ViewHolder {

		TextView mTextTitle;

		ContentHolder(View itemView) {
			super(itemView);
			mTextTitle = itemView.findViewById(R.id.text_adapter_content_name);
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
