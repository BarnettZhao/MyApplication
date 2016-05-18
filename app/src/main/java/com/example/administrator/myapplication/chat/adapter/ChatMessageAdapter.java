package com.example.administrator.myapplication.chat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.dao.ChatMessage;

import java.util.List;

/**
 * Created by kkkkk on 2016/4/11.
 */
public class ChatMessageAdapter extends BaseAdapterNew<ChatMessage> {
	public static final String TYPE_YOU="1";
	public static final String TYPE_MY="0";
	public ChatMessageAdapter(Context context, List<ChatMessage> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.message_layout_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		ChatMessage item = getItem(position);
		TextView textview_left = (TextView) convertView.findViewById(R.id.textview_left);
		TextView textview_right = (TextView) convertView.findViewById(R.id.textview_right);
		LinearLayout left_linear = (LinearLayout) convertView.findViewById(R.id.left_linear);
		LinearLayout right_linear = (LinearLayout) convertView.findViewById(R.id.right_linear);
		if(item.getType().equals(TYPE_YOU) ){
			left_linear.setVisibility(View.VISIBLE);
			right_linear.setVisibility(View.GONE);
			textview_left.setText(item.getWords());
		}
		if(item.getType().equals(TYPE_MY)){
			left_linear.setVisibility(View.GONE);
			right_linear.setVisibility(View.VISIBLE);
			textview_right.setText(item.getWords());
		}
	}
}
