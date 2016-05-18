package com.example.administrator.myapplication.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.MainActivity;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.adapter.FriendListAdapter;
import com.example.administrator.myapplication.chat.ui.ChatActivity;
import com.example.administrator.myapplication.dao.FriendList;
import com.example.administrator.myapplication.xListView.MyListView;

import java.util.List;

/**
 * Created by Administrator on 2016/1/2.
 */
public class Chat extends Fragment{
    private MyListView friendListView;
    private FriendListAdapter mAdapter;
    private List<FriendList> mDatas;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    friendListView.onRefreshComplete();
                    break;
                case 2:
                    friendListView.onLoadComplete();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat,null);
        friendListView = (MyListView) view.findViewById(R.id.friend_listView);
        ImageView imageView = (ImageView) view.findViewById(R.id.left_button);
        imageView.setVisibility(View.GONE);
        TextView textView = (TextView) view.findViewById(R.id.toolbar_title);
        textView.setText("好友列表");
        mDatas = MainActivity.mDatas;
        if (mDatas != null) {
            mAdapter = new FriendListAdapter(getActivity(),mDatas);
            friendListView.setAdapter(mAdapter);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),"获取好友列表出错 ",Toast.LENGTH_SHORT).show();
        }

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity().getApplicationContext(),ChatActivity.class);
                chatIntent.putExtra(ChatActivity.FRIEND_NAME, mDatas.get(position - 1).getName().toString());
                startActivity(chatIntent);
            }
        });
        refreshList();
        return view;
    }

    private void refreshList() {
        friendListView.setonRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (friendListView.isLoading()) {
                    friendListView.onLoadComplete();
                }
                handler.sendEmptyMessageDelayed(1, 1500);
            }
        });
        friendListView.setonLoadListener(new MyListView.OnLoadListener() {
            @Override
            public void onLoad() {
                if (friendListView.isRefreshing()) {
                    friendListView.onRefreshComplete();
                }
                handler.sendEmptyMessageDelayed(2, 1500);
            }
        });
    }
}
