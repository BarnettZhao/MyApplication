package com.example.administrator.myapplication.netConnection;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;//消息队列
    private ImageLoader mImageLoader;//ImageLoader对象
    private static Context mCtx;

    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = getImageLoader();
    }
    public static synchronized VolleySingleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());


        }
        return mRequestQueue;
    }
    public ImageLoader getImageLoader(){
        if(mImageLoader==null){
            mImageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache(){
                private final LruCache<String,Bitmap> cache = new LruCache<String,Bitmap>(20);//设置图片缓存
                @Override
                public Bitmap getBitmap(String url) {
                    return null;
                }
                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                }
            });
        }
        return mImageLoader;
    }
    //将请求添加到队列中
    public void addToRequestQueue(Request req){
        getRequestQueue().add(req);
        req.setRetryPolicy(new DefaultRetryPolicy(8000,3,1.0f));
    }
}
