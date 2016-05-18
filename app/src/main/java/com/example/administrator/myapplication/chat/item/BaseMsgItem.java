package com.example.administrator.myapplication.chat.item;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.ui.ChatActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.Date;

/**
 * Created by wangdanfeng on 2016/4/1.
 */
public abstract class BaseMsgItem extends LinearLayout {
    protected TextView mTextViewTime;
    protected ImageView mImageViewUserHead;
    protected View mClickLayout;
    protected TextView mTextViewUsername;

    protected TextView percentageView;
    protected ProgressBar progressBar;

    protected ImageView mImageViewstatus;
    protected Context mContext;
    protected EMMessage mMessage;
    protected int mPosotion;
    protected BaseAdapter mAdapter;
    protected Activity mActivity;
    protected LayoutInflater mInflater;
    protected ChatActivity.MessageListItemClickListener itemClickListener;
    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    public BaseMsgItem(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mMessage = message;
        this.mPosotion = position;
        this.mAdapter = adapter;
        mInflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {

        onInflatView();
        mTextViewTime = (TextView) findViewById(R.id.textview_msglistitem_time);
        mImageViewUserHead = (ImageView) findViewById(R.id.imageview_msglistitem_userimg);
        mClickLayout = findViewById(R.id.clickLayout);
        mTextViewUsername = (TextView) findViewById(R.id.textview_msglistitem_username);
        percentageView = (TextView) findViewById(R.id.percentage);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mImageViewstatus = (ImageView) findViewById(R.id.imageview_msglistitem_msgstate);
//        ackedView = (TextView) findViewById(R.id.tv_ack);
//        deliveredView = (TextView) findViewById(R.id.tv_delivered);
//
        onFindViewById();

    }

    /**
     * 根据当前message和position设置控件属性等
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position, ChatActivity.MessageListItemClickListener listener ) {
        this.mMessage = message;
        this.mPosotion = position;
        this.itemClickListener = listener;
        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.textview_msglistitem_time);
        if (timestamp != null) {
            if (mPosotion == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(mMessage.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                EMMessage prevMessage = (EMMessage) mAdapter.getItem(mPosotion - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(mMessage.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(mMessage.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            mTextViewUsername.setText(mMessage.getFrom());            //发送方不显示nick
        }
    }

    private void setClickListener() {
        if (mClickLayout != null) {
            mClickLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (!itemClickListener.onBubbleClick(mMessage)) {
                            //如果listener返回false不处理这个事件，执行lib默认的处理
                            onItemClick();
                        }
                    }
                }
            });
        }

        if (mImageViewstatus != null) {
            mImageViewstatus.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(mMessage);
                    }
                }
            });
        }
    }

    protected void updateView() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mMessage.status() == EMMessage.Status.FAIL) {

                    if (mMessage.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
//                        ToastUtils.showText("发送失败");
                    } else if (mMessage.getError() == EMError.GROUP_NOT_JOINED) {
//                        ToastUtils.showText("发送失败");
                    } else {
//                        ToastUtils.showText("发送失败");
                    }
                }

                onUpdateView();

            }
        });

    }

    /**
     * 设置消息发送callback
     */
    protected void setMessageSendCallback() {
        if (messageSendCallback == null) {
            messageSendCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (percentageView != null)
                                percentageView.setText(progress + "%");

                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        mMessage.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * 设置消息接收callback
     */
    protected void setMessageReceiveCallback() {
        if (messageReceiveCallback == null) {
            messageReceiveCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (percentageView != null) {
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        mMessage.setMessageStatusCallback(messageReceiveCallback);
    }

    /**
     * 填充layout
     */
    protected abstract void onInflatView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 消息状态改变，刷新listview
     */
    protected abstract void onUpdateView();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();

    /**
     * 聊天气泡被点击事件
     */
    protected abstract void onItemClick();
}
