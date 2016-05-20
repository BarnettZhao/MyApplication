package com.example.administrator.myapplication.netConnection;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.myapplication.base.BaseApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/5.
 */
public class NetConnect {
    //将网络连接单例
    private static NetConnect mConnection;
    private NetConnect() {
    }
    public static synchronized NetConnect getInstance() {
        if (mConnection == null) {
            mConnection = new NetConnect();
        }
        return mConnection;
    }

    /**
     * 监听网络连接上的接口
     */
    public interface OnConnectionListerner {
        /**
         * 网络连接的状态的。
         * @param connection 判断网络是否连接，连接返回true,未连接返回false
         * @param type       如果连接网络，返回网络的连接类型。
         */
        void isConnection(boolean connection, String type);

        /**
         * 连接网络成功回调的方法。
         * @param response 返回网络的返回值
         */
        void onSuccessConnection(String response);

        /**
         * 网络连接错误
         * @param response   返回“网络连接错误”字符串
         * @param statusCode 返回连接的错误码
         */
        void onFailConnection(String response, int statusCode);
    }

    /**
     * 提交HashMap数据的网络连接
     * @param url                 网络连接的URL网址
     * @param map                 提交的数据
     * @param connectionListerner 网络连接的监听对象， 获得网络的连接状态
     */
    public void addRequest(String url, final HashMap<String, String> map, final OnConnectionListerner connectionListerner) {
        //获取网络的状态
        if (!NetWorkUtils.isConnection()) {
            connectionListerner.isConnection(false, null);
        } else {
            connectionListerner.isConnection(true, NetWorkUtils.getConnectionType());
        }

        //创建请求
        StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        connectionListerner.onSuccessConnection(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse==null){
                            connectionListerner.onFailConnection("网络连接失败", 404);
                        }else {
                            connectionListerner.onFailConnection("网络连接失败", error.networkResponse.statusCode);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        //创建请求队列并将请求添加到请求队列中
        VolleySingleton.getInstance(BaseApplication.getContext()).addToRequestQueue(request);
    }
}
