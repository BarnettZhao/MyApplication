package com.example.administrator.myapplication.chat.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.administrator.myapplication.chat.item.BaseMsgItem;
import com.example.administrator.myapplication.chat.item.FileMsgItem;
import com.example.administrator.myapplication.chat.item.PicMsgItem;
import com.example.administrator.myapplication.chat.item.TextMsgItem;
import com.example.administrator.myapplication.chat.item.VoiceMsgItem;
import com.example.administrator.myapplication.chat.ui.ChatActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by wangdanfeng on 2016/3/31.
 */
public class MessageListAdapter extends BaseAdapter {
    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;

    private Context mContext;
    private String mToChatuserName;
    private EMConversation.EMConversationType mChattype;
    private ListView mListview;
    private EMConversation mConversation;
    EMMessage[] messages = null;
    private ChatActivity.MessageListItemClickListener itemClickListener;

    public MessageListAdapter(Context mContext, String mToChatuserName, EMConversation.EMConversationType mChattype, ListView mListview) {
        this.mContext = mContext;
        this.mToChatuserName = mToChatuserName;
        this.mChattype = mChattype;
        this.mListview = mListview;
        this.mConversation = EMClient.getInstance().chatManager().getConversation(mToChatuserName, mChattype, true);
        Log.e("messageadapter","constructor");
//        mConversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
//        final List<EMMessage> msgs = mConversation.getAllMessages();
//        int msgCount = msgs != null ? msgs.size() : 0;
    }

    Handler handler = new Handler() {
        private void refreshList() {
            // UI线程不能直接使用conversation.getAllMessages()
            // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
            Log.e("messageadapter","refreshlist");
            messages = (EMMessage[]) mConversation.getAllMessages().toArray(new EMMessage[0]);
            mConversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    Log.e("messageadapter", "handlemessage");
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages.length > 0) {
                        mListview.setSelection(messages.length - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    mListview.setSelection(position);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.length;

    }

    @Override
    public EMMessage getItem(int position) {
        if (messages != null && position <= messages.length) {
            return messages[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = null;
        if (messages != null && position <= messages.length) {
            message = messages[position];
        }
        Log.e("messageadapter",message+"");
        convertView = getMessageListItem(mContext, message, position);
//缓存的view的message很可能不是当前item的，传入当前message和position更新ui
        ((BaseMsgItem) convertView).setUpView(message, position, itemClickListener);
        return convertView;

    }

    private View getMessageListItem(Context mContext, EMMessage message, int position) {
        View itemview = null;
        switch (message.getType()) {
            case TXT:
                itemview = new TextMsgItem(mContext, message, position, this);
                break;

            case FILE:
                itemview = new FileMsgItem(mContext, message, position, this);

                break;
            case IMAGE:
                itemview = new PicMsgItem(mContext, message, position, this);
                break;
            case VOICE:
                itemview = new VoiceMsgItem(mContext, message, position, this);
                break;
            case VIDEO:
//                itemview = new Video(context, message, position, this);
                break;
            default:
                break;
        }
        return itemview;
    }



    public void refreshList() {
      String username=  mConversation.getUserName();
        messages = (EMMessage[]) mConversation.getAllMessages().toArray(new EMMessage[0]);
        mConversation.markAllMessagesAsRead();
        notifyDataSetChanged();
        selectLastItem();
    }

    public void selectLastItem() {
        if (messages.length > 0) {
            mListview.setSelection(messages.length - 1);
        }
    }

    public void setItemClickListener(ChatActivity.MessageListItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }


    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        // avoid refresh too frequently when receiving large amount offline messages
        Log.e("messageadapter","refreshlast");
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

}
