package com.example.administrator.myapplication.chat.item;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.ui.ImageCache;
import com.example.administrator.myapplication.chat.ui.ShowBigImage;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.PathUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/4/4.
 */
public class PicMsgItem extends FileMsgItem {
    protected ImageView imageView;
    private EMImageMessageBody imgBody;

    public PicMsgItem(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        mInflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.msglist_item_pic_receive : R.layout.msglist_item_pic_send, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.imageview_msglistitem_image);
    }


    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) mMessage.getBody();
        // 接收方向的消息
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    // 兼容旧版SDK收到的thumbnail
                    String thumbImageName= imgBody.getLocalUrl().substring(imgBody.getLocalUrl().lastIndexOf("/") + 1, imgBody.getLocalUrl().length());
                     thumbPath = PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;

                }
                showImageView(thumbPath, imageView, imgBody.getLocalUrl(), mMessage);
            }
            return;
        }

        String filePath = imgBody.getLocalUrl();
        if (filePath != null) {
            String imageName= filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
            String path = PathUtil.getInstance().getImagePath()+"/"+ imageName;

            showImageView(path, imageView, filePath, mMessage);
        }
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onItemClick() {
        Intent intent = new Intent(mContext, ShowBigImage.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("secret", imgBody.getSecret());
            intent.putExtra("remotepath", imgBody.getRemoteUrl());
            intent.putExtra("localUrl", imgBody.getLocalUrl());
        }
        if (mMessage != null && mMessage.direct() == EMMessage.Direct.RECEIVE && !mMessage.isAcked()
                && mMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mContext.startActivity(intent);
    }

    /**
     * 展示图片
     * @param thumbernailPath
     * @param iv
     * @param localFullSizePath
     * @param message
     * @return
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return com.hyphenate.util.ImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return com.hyphenate.util.ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        iv.setImageBitmap(image);
                        ImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
//                            if (NetWorkUtils.checkNetWorkState(mActivity)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
//                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }
}
