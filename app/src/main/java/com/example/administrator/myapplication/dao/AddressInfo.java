package com.example.administrator.myapplication.dao;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Liu_Zhichao on 2016/2/20 14:36.
 * 地图位置
 */
public class AddressInfo {

	private String name;
	private String address;
	private LatLng latLng;
	private boolean isSelected;

	public AddressInfo(String name, String address, LatLng latLng, boolean isSelected) {
		this.name = name;
		this.address = address;
		this.latLng = latLng;
		this.isSelected = isSelected;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
}
