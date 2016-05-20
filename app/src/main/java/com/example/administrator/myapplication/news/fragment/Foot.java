package com.example.administrator.myapplication.news.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.administrator.myapplication.base.BaseActivity;
import com.example.administrator.myapplication.base.BaseFragment;

import com.example.administrator.myapplication.dao.FootNews;
import com.example.administrator.myapplication.news.adapter.FootNewsAdapter;
import com.example.administrator.myapplication.news.adapter.NewsMenuAdapter;
import com.example.administrator.myapplication.R;

import com.example.administrator.myapplication.dao.FootThing;

import com.example.administrator.myapplication.news.ui.NewsDetail;
import com.example.administrator.myapplication.xListView.MyListView;
import com.google.gson.Gson;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Administrator on 2016/1/2.
 * 新闻
 */
public class Foot extends BaseFragment implements View.OnClickListener{
    private static final int NEW_MENU = 1;
    private static final int NEW_DETAIL = 2;
    private MyListView mListView;
    private List<FootThing> mDatas;
    private List<FootNews> mNews;
    private ImageView imageMenu;
    public PopupWindow mPopupWindow;
    private CircleImageView circleImageView;
    private Gson gson;
    private int page;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getNews(page+1);
                    mListView.onRefreshComplete();
                    break;
                case 2:
                    getNews(page+1);
                    mListView.onLoadComplete();
                    break;
                default:
                    break;
            }
        }
    };
     @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         Log.e("nohttp","oncreateview");
        View view=inflater.inflate(R.layout.foot,null);
         imageMenu = (ImageView) view.findViewById(R.id.image_menu);
         circleImageView = (CircleImageView) view.findViewById(R.id.foot_icon);
         imageMenu.setOnClickListener(this);
         gson = new Gson();
         mDatas = new ArrayList<>();
         mNews = new ArrayList<>();
         getNews(1);
         mListView = (MyListView) view.findViewById(R.id.xListView_news);
         onRefresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String pathMe =getActivity().getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/myicon.jpg";

        Bitmap bitmap = BaseActivity.getDiskBitmap(pathMe);
        Log.e("path" , pathMe);
        if (bitmap != null){
            circleImageView.setImageBitmap(bitmap);
        }else {
            circleImageView.setImageResource(R.mipmap.icon);
        }
    }

    private void onRefresh() {
        mListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mListView.isLoading()) {
                    mListView.onLoadComplete();
                }
               handler.sendEmptyMessageDelayed(1,1500);
            }
        });
        mListView.setonLoadListener(new MyListView.OnLoadListener() {
            @Override
            public void onLoad() {
                if (mListView.isRefreshing()) {
                    mListView.onRefreshComplete();
                }
                handler.sendEmptyMessageDelayed(2,1500);
            }
        });
    }

    private void getNewsMenu() {
        com.yolanda.nohttp.Request<JSONObject> request = NoHttp.createJsonObjectRequest
                ("http://way.jd.com/showapi/channel_news", RequestMethod.POST);
        request.add("appkey","3cc6b2761731b2884e394c7c50f2a07d");
        requestNet(request,NEW_MENU);
    }

    @Override
    public void onSuccess(int what, Response<JSONObject> response) {
        super.onSuccess(what, response);
        Log.e("nohttp","success");
        switch (what) {
            case NEW_MENU:
                setNewsMenu(response);
                break;
            case NEW_DETAIL:
                setNews(response);
                break;
            default:
                break;
        }
    }

    private void setNewsMenu(Response<JSONObject> response) {
        JSONObject responseJson = response.get();// 响应结果
        Log.e("nohttp",responseJson+"");
        JSONObject result = null;
        try {
            result = responseJson.getJSONObject("result");
            JSONObject showapi = result.getJSONObject("showapi_res_body");
            JSONArray channelists = showapi.getJSONArray("channelList");
            for (int i = 0; i <channelists.length(); i ++){
                JSONObject channel = (JSONObject) channelists.get(i);
                String name = channel.getString("name");
                String channelId = channel.getString("channelId");
                mDatas.add(new FootThing(name,channelId));
//                        Log.e("nohttp",name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showPopupWindow(mDatas);
    }

    private void setNews(Response<JSONObject> response) {
        JSONObject responseJson1 = response.get();// 响应结果
        Log.e("nohttp",responseJson1 + "");
        try {
            JSONObject result1 = responseJson1.getJSONObject("result");
            JSONObject showapi = result1.getJSONObject("showapi_res_body");
            JSONObject pagebean = showapi.getJSONObject("pagebean");
            JSONArray contentlist = pagebean.getJSONArray("contentlist");
            for (int i = 0; i <contentlist.length(); i ++){
                JSONObject channel = (JSONObject) contentlist.get(i);
                String title = channel.getString("title");
                String content = channel.getString("desc");
                String source = channel.getString("source");
                String time = channel.getString("pubDate");
                String url = channel.getString("link");
                JSONArray imageUrls = channel.getJSONArray("imageurls");
                Log.e("nohttp",imageUrls+"");
                String imageUrl = null;
                if (imageUrls.length() > 0) {
                    JSONObject jsonObject = (JSONObject) imageUrls.get(0);
                    Log.e("nohttp0",jsonObject+"");
                    imageUrl = jsonObject.getString("url");
                    Log.e("nohttp00",imageUrl+"");
                }
                mNews.add(0,new FootNews(title,source,content,time,url,imageUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FootNewsAdapter footNewsAdapter = new FootNewsAdapter(getActivity(),mNews);
        mListView.setAdapter(footNewsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsDetail.class);
                FootNews item = mNews.get(position-1);
                intent.putExtra(NewsDetail.NEWS_TITLE,item.getTitle());
                intent.putExtra(NewsDetail.NEWS_CONTENT,item.getContent());
                intent.putExtra(NewsDetail.NEWS_SOURCE,item.getSource());
                intent.putExtra(NewsDetail.NEWS_TIME,item.getTime());
                intent.putExtra(NewsDetail.NEWS_URL,item.getUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFail(Exception exception) {
        super.onFail(exception);
        Log.e("nohttp","onFailed"+exception);
    }

    private void showPopupWindow(final List<FootThing> mDatas){
        mPopupWindow=new PopupWindow(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popView=inflater.inflate(R.layout.activity_popupwindow, null);
        ListView listViewPop = (ListView) popView.findViewById(R.id.list_pop);
        NewsMenuAdapter aListdapter = new NewsMenuAdapter(getActivity(),mDatas);
        listViewPop.setAdapter(aListdapter);
        listViewPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.yolanda.nohttp.Request<JSONObject> request = NoHttp.createJsonObjectRequest
                        ("https://way.jd.com/showapi/search_news", RequestMethod.POST);
                request.add("channelId",mDatas.get(position).getChannelId());
                request.add("channelName",mDatas.get(position).getName());
                request.add("title","");
                request.add("page","3");
                request.add("appkey","3cc6b2761731b2884e394c7c50f2a07d");
                requestNet(request,NEW_DETAIL);
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT+1300);
        mPopupWindow.setContentView(popView);
        mPopupWindow.setFocusable(true);//点击空白处时，隐藏掉pop窗口
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(imageMenu);
    }
    private void getNews(int page) {
        com.yolanda.nohttp.Request<JSONObject> request = NoHttp.createJsonObjectRequest
                ("https://way.jd.com/showapi/search_news", RequestMethod.POST);
        request.add("channelId","5572a108b3cdc86cf39001cd");
        request.add("channelName","国内焦点");
        request.add("title","");
        request.add("page",page+"");
        request.add("appkey","3cc6b2761731b2884e394c7c50f2a07d");

        requestNet(request,NEW_DETAIL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_menu:
                getNewsMenu();
                break;
            default:
                break;
        }
    }
}
