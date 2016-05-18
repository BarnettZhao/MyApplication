package com.example.administrator.myapplication.chat.ui;

import java.io.File;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.adapter.MessageListAdapter;
import com.example.administrator.myapplication.chat.audioUtil.AudioRecordButton;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by zhaoww on 2016/4/10.
 * chat jiemian
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    public static final String FRIEND_NAME = "friendName";

    private RelativeLayout mButtonBiaoQing, mRelativeEdit;
    private ImageView mbutton_back;
    private Button mButtonSend, mButtonVoice, mButtonKeyBoard;
    private static String TAG = "ChatActivity";
    private EditText mEditText;
    private TextView mTextFriendName;
    private AudioRecordButton mAudioButton;
    private String toChatUsername;


    private ListView mChatListView;
    private MessageListAdapter mAdapter;
    private String[] mImageName = {"icon_002_cover", "icon_007_cover", "icon_010_cover", "icon_012_cover", "icon_013_cover", "icon_018_cover",
            "icon_019_cover", "icon_020_cover", "icon_021_cover", "icon_022_cover", "icon_024_cover", "icon_027_cover",
            "icon_029_cover", "icon_030_cover", "icon_035_cover", "icon_040_cover"};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mChatListView.setAdapter(mAdapter);
		        	mAdapter.notifyDataSetChanged();
                    mAdapter.refreshSelectLast();
                    Log.e("chatactivity", "messagereceive");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_act);
        toChatUsername = getIntent().getStringExtra(FRIEND_NAME);
        initViews();
        mChatListView = (ListView) findViewById(R.id.chat_listview);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new MessageListAdapter(ChatActivity.this, toChatUsername, EMConversation.EMConversationType.Chat, mChatListView);
        mChatListView.setSelection(mChatListView.getBottom());
        Log.e(TAG, toChatUsername);
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername);
        if (conversation != null) {
            List<EMMessage> messages = conversation.getAllMessages();
            for (EMMessage messages1 : messages) {
                Log.e(TAG, "" + messages1);
                handler.sendEmptyMessage(1);
            }
        }
        setListItemClickListener();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            Log.e(TAG, "收到消息");
            for (int i = 0; i < messages.size(); i++) {
                String message = messages.get(i).toString();
                Log.e(TAG, message);
                String[] s = message.split(":");
                Log.e("分隔", s[4]);

                handler.sendEmptyMessage(1);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            Log.e(TAG, "收到透传消息");
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
            Log.e(TAG, "收到已读回执");
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
            Log.e(TAG, "收到已送达回执");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            Log.e(TAG, "消息状态变动");
        }
    };

    private void initViews() {
        mAudioButton= (AudioRecordButton) findViewById(R.id.audiobutton);
        mAudioButton.setOnAudioFinishListener(new AudioRecordButton.OnAudioFinishListener() {
            @Override
            public void audioFinish(float time, String dir) {
                sendVoiceMessage(dir,(int) time);
            }
        });
        mButtonBiaoQing = (RelativeLayout) findViewById(R.id.biaoqingsend);
        mButtonBiaoQing.setOnClickListener(this);
        mbutton_back = (ImageView) findViewById(R.id.left_button);
        mbutton_back.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.edittext_words);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mButtonSend.setOnClickListener(this);
        mTextFriendName = (TextView) findViewById(R.id.toolbar_title);
        mTextFriendName.setText(toChatUsername);
        mTextFriendName.setVisibility(View.VISIBLE);
        mButtonVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        mButtonVoice.setOnClickListener(this);
        mButtonKeyBoard = (Button) findViewById(R.id.btn_set_mode_keyboard);
        mButtonKeyBoard.setOnClickListener(this);
        mRelativeEdit = (RelativeLayout) findViewById(R.id.edittext_layout);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.biaoqingsend:
                selectPicFromLocal();
                break;
            case R.id.btn_set_mode_voice:
                showSpeckState();
                break;
            case R.id.btn_set_mode_keyboard:
                showEditState();
                break;
            case R.id.btn_send:
                sentTextMessage();
                break;
            case R.id.left_button:
                finish();
                break;

            default:
                break;
        }
    }

    private void sentTextMessage() {
        String text = mEditText.getText().toString().trim();
        Log.e(TAG,text);
        if ("".equals(text)) {
            Toast.makeText(getApplicationContext(),"内容不能为空", Toast.LENGTH_SHORT).show();
        }else {
            EMMessage message = EMMessage.createTxtSendMessage(text, toChatUsername);
            sendMessage(message);
        }
    }


    private void showSpeckState() {
        mButtonVoice.setVisibility(View.GONE);
        mAudioButton.setVisibility(View.VISIBLE);
        mButtonKeyBoard.setVisibility(View.VISIBLE);
        mButtonBiaoQing.setVisibility(View.GONE);
        mRelativeEdit.setVisibility(View.GONE);
    }

    private void showEditState() {
        mButtonVoice.setVisibility(View.VISIBLE);
        mAudioButton.setVisibility(View.GONE);
        mButtonKeyBoard.setVisibility(View.GONE);
        mButtonBiaoQing.setVisibility(View.VISIBLE);
        mRelativeEdit.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    public interface MessageListItemClickListener{
        void onResendClick(EMMessage message);
        boolean onBubbleClick(EMMessage message);
    }

    /**
     * 设置list item里控件的点击事件
     * @param listener
     */
    public void setItemClickListener(MessageListItemClickListener listener){
        if ( mAdapter!= null) {
            mAdapter.setItemClickListener(listener);
        }
    }

    private void setListItemClickListener() {
        setItemClickListener(new MessageListItemClickListener() {
            @Override
            public void onResendClick(final EMMessage message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("提示").setMessage("确定要重新发送吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        resendMessage(message);
                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {

                return false;
            }
        });
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, com.example.administrator.myapplication.Config.SELECTIMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){

            switch (requestCode){
                case com.example.administrator.myapplication.Config.SELECTIMG:

                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }

                    break;
            }
        }
    }
    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
//                ToastUtils.showText("找不到图片");
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
//                ToastUtils.showText("找不到图片");
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    //发送图片消息
    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        Log.d("发送图片", "sendImageMessage() called with: " + "imagePath = [" + imagePath + "]");
        sendMessage(message);
    }

    //发送音频消息
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    private void sendMessage(EMMessage message) {
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        mAdapter.refreshSelectLast();
        mAdapter.notifyDataSetChanged();
        mEditText.setText("");
    }
}
