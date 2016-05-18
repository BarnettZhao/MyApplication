package com.example.administrator.myapplication.dao;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/2.
 */
public class FootThing {
    private String name;
    private String channelId;

    public FootThing(String name, String channelId) {
        this.name = name;
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public FootThing() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
