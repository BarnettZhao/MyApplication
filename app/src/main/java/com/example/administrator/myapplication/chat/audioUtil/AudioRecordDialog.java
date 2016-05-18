package com.example.administrator.myapplication.chat.audioUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;


/**
 * Created by wangdanfeng on 2016/2/17.
 */
public class AudioRecordDialog{

	private Dialog mDialog;
	private ImageView mImageIcon;
	private ImageView mImageLevel;
	private TextView mTextView;
	private Context mContext;
    private View mAudioRecordDialogView;

	public AudioRecordDialog(Context mContext) {
		super();
		this.mContext = mContext;
		
	}
	
	public void showDialog(){
		mDialog=new Dialog(mContext, R.style.AudioRecordDialog);
		mAudioRecordDialogView=LayoutInflater.from(mContext).inflate(R.layout.audiorecord_dialog, null);
		mDialog.setContentView(mAudioRecordDialogView);
		
		mImageIcon=(ImageView) mAudioRecordDialogView.findViewById(R.id.audiorecord_imageicon);
		mImageLevel=(ImageView)mAudioRecordDialogView.findViewById(R.id.audiorecord_imagelevel);
		mTextView=(TextView) mAudioRecordDialogView.findViewById(R.id.audiorecord_textView);
		mDialog.show();
	}
	
	public void recordingDialog(){
		if(mDialog!=null&&mDialog.isShowing()){
			mImageIcon.setImageResource(R.mipmap.voice_rcd_hint);
			mImageLevel.setVisibility(View.VISIBLE);
			mTextView.setText("手指上滑，取消发送");
			
		}
	}
	
	public void tooShortDilaog(){
		if(mDialog!=null&&mDialog.isShowing()){
			mImageIcon.setImageResource(R.mipmap.voice_to_short);
			mImageLevel.setVisibility(View.GONE);
			mTextView.setText("录音时间太短");
			
		}
	}
	
	public void cancleRecordingDialog(){
		if(mDialog!=null&&mDialog.isShowing()){
			mImageIcon.setImageResource(R.mipmap.rcd_cancel_icon);
			mImageLevel.setVisibility(View.GONE);
			mTextView.setText("松开手指，取消发送");
			
		}
	}
	public void dismiss(){
	   
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
			mDialog=null;			
		}
   }
   
   
   /**
    * 更新音量
    * 
    * @param level
    */
   public void updateVolumeLevel(int level) {
       if (mDialog!=null&&mDialog.isShowing()) {


           int volumeResId = mContext.getResources().getIdentifier(
                   "amp" + level, "mipmap",
                   mContext.getPackageName());
          mImageLevel.setImageResource(volumeResId);
       }
   }
}
