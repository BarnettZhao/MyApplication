package com.example.administrator.myapplication.foot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.dao.FootNews;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by kkkkk on 2016/4/29.
 */
public class FootNewsAdapter extends BaseAdapterNew<FootNews> {
	public FootNewsAdapter(Context context, List<FootNews> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.foot_news_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		FootNews item = getItem(position);
		TextView textViewTitle = (TextView) convertView.findViewById(R.id.foot_item_title);
		TextView textViewContent = (TextView) convertView.findViewById(R.id.foot_item_content);
		TextView textViewSource = (TextView) convertView.findViewById(R.id.foot_item_source);
		TextView textViewTime = (TextView) convertView.findViewById(R.id.foot_item_time);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.foot_item_image);
		if (!TextUtils.isEmpty(item.getImageUrl())) {
			imageView.setVisibility(View.VISIBLE);
			Log.e("footadapter0",item.getImageUrl());
			String imageurl = item.getImageUrl();
			Uri uri = Uri.parse(imageurl);
			imageView.setImageURI(uri);
		} else {
			imageView.setVisibility(View.GONE);
		}
		textViewTitle.setText(item.getTitle());
		textViewContent.setText(item.getContent());
		textViewSource.setText(item.getSource());
		textViewTime.setText(item.getTime());
	}
}
