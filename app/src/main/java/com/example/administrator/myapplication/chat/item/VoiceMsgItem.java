package com.example.administrator.myapplication.chat.item;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.ui.VoicePlayClickListener;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;

public class VoiceMsgItem extends FileMsgItem{

    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStutausView;

    public VoiceMsgItem(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        mInflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.msglist_item_voice_receive : R.layout.msglist_item_voice_send, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.imageview_msglistitem_voice));
        voiceLengthView = (TextView) findViewById(R.id.textview_msglistitem_voicelength);
        readStutausView = (ImageView) findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) mMessage.getBody();
        int len = voiceBody.getLength();
        if(len>0){
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        }else{
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (VoicePlayClickListener.playMsgId != null && VoicePlayClickListener.playMsgId.equals(mMessage.getMsgId()) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.drawable.voice_from_icon);
            } else {
                voiceImageView.setImageResource(R.drawable.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
            voiceAnimation.start();
        } else {
            if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
                voiceImageView.setImageResource(R.mipmap.chatfrom_voice_playing);
            } else {
                voiceImageView.setImageResource(R.mipmap.chatto_voice_playing);
            }
        }

        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (mMessage.isListened()) {
                // 隐藏语音未听标志
                readStutausView.setVisibility(View.INVISIBLE);
            } else {
                readStutausView.setVisibility(View.VISIBLE);
            }

            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                progressBar.setVisibility(View.VISIBLE);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.INVISIBLE);

            }
            return;
        }

        // until here, deal with send voice msg
        handleSendMessage();
    }

    @Override
    protected void onItemClick() {
        new VoicePlayClickListener(mMessage, voiceImageView, readStutausView, mAdapter, mActivity).onClick(mClickLayout);

    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }


    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (VoicePlayClickListener.currentPlayListener != null && VoicePlayClickListener.isPlaying) {
            // 停止语音播放
           VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }
    }
    
}
