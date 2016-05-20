package com.example.administrator.myapplication.news.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.myapplication.base.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.dao.FootThing;


import java.util.List;

/**
 * Created by Administrator on 2016/1/2.
 */
public class NewsMenuAdapter extends BaseAdapterNew<FootThing> {

    public NewsMenuAdapter(Context context, List<FootThing> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getResourceId(int Position) {
        return R.layout.popup_item;
    }

    @Override
    protected void setViewData(View convertView, int position) {
        FootThing item = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.pop_item_name);
        textView.setText(item.getName());
    }
}
