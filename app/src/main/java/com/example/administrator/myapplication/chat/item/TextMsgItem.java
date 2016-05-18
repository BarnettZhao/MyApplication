package com.example.administrator.myapplication.chat.item;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Administrator on 2016/4/4.
 */
public class TextMsgItem extends BaseMsgItem {
    private TextView mContentView;
    public TextMsgItem(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
       mInflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.msglist_item_text_receive : R.layout.msglist_item_text_send, this);
    }

    @Override
    protected void onFindViewById() {

        mContentView= (TextView) findViewById(R.id.textview_msglistitem_chatcontent);

    }
    protected void handleTextMessage() {
        if (mMessage.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (mMessage.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    mImageViewstatus.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS: // 发送成功
                    if (progressBar!=null&&mImageViewstatus!=null){
                        progressBar.setVisibility(View.GONE);
                        mImageViewstatus.setVisibility(View.GONE);
                    }
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    mImageViewstatus.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    mImageViewstatus.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            if(!mMessage.isAcked() && mMessage.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) mMessage.getBody();
        String text=txtBody.toString();
        int length=text.length();
        mContentView.setText(text.substring(5,length-1));
        handleTextMessage();
    }

    @Override
    protected void onItemClick() {

    }
}
