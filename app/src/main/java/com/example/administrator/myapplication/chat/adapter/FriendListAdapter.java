package com.example.administrator.myapplication.chat.adapter;

import android.content.Context;
import android.view.View;

import android.widget.TextView;

import com.example.administrator.myapplication.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.ViewHolder;
import com.example.administrator.myapplication.dao.FriendList;

import java.util.List;

/**
 * Created by kkkkk on 2016/4/7.
 */
public class FriendListAdapter extends BaseAdapterNew<FriendList> {


	public FriendListAdapter(Context context, List <FriendList> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.friend_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		FriendList item = getItem(position);
		TextView textViewName = ViewHolder.get(convertView,R.id.friend_name);
		textViewName.setText(item.getName());
	}
}
