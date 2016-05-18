package com.example.administrator.myapplication.chat.audioUtil;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wangdanfeng on 2016/2/17.
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mAudioParentFileDir;
    private String mCurrentFilePath;
    private boolean mIsPrepared;
    private static AudioManager mAudioManager;

    private AudioManager(String mCurrentFilePath) {
        this.mAudioParentFileDir=mCurrentFilePath;
    }

    public static AudioManager newInstance(String mCurrentFilePath) {
        if (mAudioManager == null) {
            synchronized (AudioManager.class) {
                if (mAudioManager == null) {

                    mAudioManager = new AudioManager(mCurrentFilePath);
                }
            }
        }
        return mAudioManager;
    }

    public interface AudioStateListener {
        void preparewell();
    }

    private AudioStateListener mAudioStateListener;

    /**
     * 监听回调
     *
     * @param listener
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mAudioStateListener = listener;
    }


    public void prepareAudio() {
        mIsPrepared = false;
        File parentfiledir = new File(mAudioParentFileDir);
        if (!parentfiledir.exists()) {
            parentfiledir.mkdirs();
        }
        String filename = generateFileName();
        File currentfile = new File(parentfiledir, filename);
        mCurrentFilePath = currentfile.getAbsolutePath();
        try {
            //根据MediaRecorder的状态转换顺序进行编写
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(currentfile.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式（api小于10可以使用RAW_AMR,api大于10使用AMR_NB，可以加版本判断）
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsPrepared = true;
            if (mAudioStateListener != null) {
                mAudioStateListener.preparewell();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 产生音频文件的文件名
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID() + ".amr";
    }


    public int getAudioLevel(int level) {
        if (mIsPrepared) {
            try {
                //MediaRecorder.getMaxAmplitude()的值在1~32767之间
                return level * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }

        }

        return 1;
    }

    public void release() {

           // mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;


    }

    public void cancle() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getmCurrentFilePath() {
        return mCurrentFilePath;
    }
}
