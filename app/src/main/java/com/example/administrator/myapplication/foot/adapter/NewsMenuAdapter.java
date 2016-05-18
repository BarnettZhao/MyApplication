package com.example.administrator.myapplication.foot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.BaseAdapterNew;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.activity.ImageShower;
import com.example.administrator.myapplication.dao.FootThing;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
