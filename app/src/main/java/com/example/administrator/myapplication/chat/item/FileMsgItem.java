package com.example.administrator.myapplication.chat.item;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.FileUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

/**
 * Created by Administrator on 2016/4/4.
 */
public class FileMsgItem extends BaseMsgItem {
    protected TextView mTextViewFileName;
    protected TextView mTextViewFileSize;
    protected TextView mTextviewFileState;

    protected EMCallBack sendfileCallBack;

    protected boolean isNotifyProcessed;
    private EMNormalFileMessageBody fileMessageBody;

    public FileMsgItem(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        mInflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.msglist_item_file_receive : R.layout.msglist_item_file_send, this);
    }

    @Override
    protected void onFindViewById() {
        mTextViewFileName = (TextView) findViewById(R.id.textview_msglistitem_file_name);
        mTextViewFileSize = (TextView) findViewById(R.id.textview_msglistitem_file_size);
        mTextviewFileState = (TextView) findViewById(R.id.textview_msglistitem_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
    }


    @Override
    protected void onSetUpView() {
        fileMessageBody = (EMNormalFileMessageBody) mMessage.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        mTextViewFileName.setText(fileMessageBody.getFileName());
        mTextViewFileSize.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) { // 接收的消息
            File file = new File(filePath);
            if (file != null && file.exists()) {
                mTextviewFileState.setText("下载完成");
            } else {
                mTextviewFileState.setText("未下载");
            }
            return;
        }

        // until here, deal with send voice msg
        handleSendMessage();
    }



    /**
     * 处理发送消息
     */
    protected void handleSendMessage() {
        setMessageSendCallback();
        switch (mMessage.status()) {
            case SUCCESS:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                mImageViewstatus.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                mImageViewstatus.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                progressBar.setVisibility(View.VISIBLE);
                if(percentageView != null){
                    percentageView.setVisibility(View.VISIBLE);
                    percentageView.setText(mMessage.progress() + "%");
                }
                mImageViewstatus.setVisibility(View.INVISIBLE);
                break;
            default:
                progressBar.setVisibility(View.INVISIBLE);
                if(percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);
                mImageViewstatus.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onUpdateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onItemClick() {
        String filePath = fileMessageBody.getLocalUrl();
        File file = new File(filePath);
        if (file != null && file.exists()) {
            // 文件存在，直接打开
            FileUtils.openFile(file, (Activity) mContext);
        } else {
            // 下载
//            mContext.startActivity(new Intent(mContext, ShowDownloadFile.class).putExtra("msgbody", mMessage.getBody()));
        }
        if (mMessage.direct() == EMMessage.Direct.RECEIVE && !mMessage.isAcked() && mMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
            } catch (HyphenateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
