package com.example.administrator.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by kkkkk on 2016/4/7.
 */
public abstract class BaseAdapterNew<T> extends ArrayAdapter<T> {

	private int page = 1;
	private int totalPage = 10000;

	public BaseAdapterNew(Context context,List<T> mDatas) {
		super(context, 0, mDatas) ;
		page = 1;
	}

	public void addDatas(List<T> datas) {
		super.addAll(datas);
		page++;
	}

	public void addData(T data) {
		super.add(data);
	}

	public void addData(int index,T data) {
		super.insert(data,index);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return this.getView(position, convertView);
	}

	protected View getView(final int position, View convertView) {
		if (convertView == null) {
			convertView = View.inflate(getContext(), getResourceId(position), null);
		}
		try {
			setViewData(convertView, position);
		} catch (Exception e) {
			Log.e("adapter setViewData error " + this.getClass().getSimpleName(), e.toString());
		}
		return convertView;
	}

	protected abstract int getResourceId(int Position);

	protected abstract void setViewData(View convertView, int position);

	public void cleanViewMap() {
		page = 1;
		super.clear();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}


}
