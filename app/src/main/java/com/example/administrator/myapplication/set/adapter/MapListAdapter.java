package com.example.administrator.myapplication.set.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.administrator.myapplication.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.ViewHolder;
import com.example.administrator.myapplication.dao.AddressInfo;

import java.util.List;

/**
 * Created by Liu_Zhichao on 2016/2/20 14:35.
 * 地图
 */
public class MapListAdapter extends BaseAdapterNew<AddressInfo> {

	public MapListAdapter(Context context, List<AddressInfo> mDatas) {
		super(context, mDatas);
	}

	@Override
	protected int getResourceId(int Position) {
		return R.layout.map_list_item;
	}

	@Override
	protected void setViewData(View convertView, int position) {
		AddressInfo addressInfo = getItem(position);

		TextView locationItemName = ViewHolder.get(convertView, R.id.location_item_name);
		TextView locationItemAddress = ViewHolder.get(convertView, R.id.location_item_address);
		ImageView locationItemMark = ViewHolder.get(convertView, R.id.location_item_mark);

		locationItemName.setText(addressInfo.getName());
		locationItemAddress.setText(addressInfo.getAddress());
		if (addressInfo.isSelected()) {
			locationItemMark.setVisibility(View.VISIBLE);
		} else {
			locationItemMark.setVisibility(View.INVISIBLE);
		}
	}
}
