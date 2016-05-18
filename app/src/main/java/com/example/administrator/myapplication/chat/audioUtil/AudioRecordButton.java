package com.example.administrator.myapplication.chat.audioUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplication.R;

import java.io.File;


/**
 * Created by wangdanfeng on 2016/2/18.
 */
public class AudioRecordButton extends Button implements AudioManager.AudioStateListener {
	private static final int STATE_NORMAL = 0x223;//正常状态
	private static final int STATE_RECORDING = 0x0224;//录音状态
	private static final int STATE_WANT_TO_CANCLE = 0x225;//取消录音状态
	private int mCurrentState = STATE_NORMAL;
	private static final int DISTANCE_CANCEL_Y = 50;
	private boolean mIsRecording = false;
	// 是否触发longclick
	private boolean mIsReady;
	private AudioRecordDialog mDialogManager;
	private AudioManager mAudioManager;
	private float mTime;
	private String mCurrentFileDir;

	public AudioRecordButton(Context context) {
		this(context, null);
	}

	public AudioRecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDialogManager = new AudioRecordDialog(context);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mCurrentFileDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ getPackagename(context) + File.separator + "audio";
		}
		mAudioManager = AudioManager.newInstance(mCurrentFileDir);
		mAudioManager.setOnAudioStateListener(this);
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mIsReady = true;
				mAudioManager.prepareAudio();
				// TODO真正显示应该在audio end prepare以后
				return false;
			}
		});
	}

	private static final int MSG_AUDIO_PREPARED = 0x226;
	private static final int MSG_AUDIOVOICE_UPDATE = 0x227;
	private static final int MSG_DIALOG_DISMISS = 0x228;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_AUDIO_PREPARED:
					mDialogManager.showDialog();
					mIsRecording = true;

					new Thread(new Runnable() {

						@Override
						public void run() {
							// 如果在录音就循环检测
							while (mIsRecording) {
								try {
									Thread.sleep(100);
									mTime += 0.1f;
									mHandler.sendEmptyMessage(MSG_AUDIOVOICE_UPDATE);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
					}).start();
					break;
				case MSG_AUDIOVOICE_UPDATE:
					mDialogManager
							.updateVolumeLevel(mAudioManager.getAudioLevel(7));
					break;
				case MSG_DIALOG_DISMISS:
					mDialogManager.dismiss();

					break;
				default:
					break;
			}

		};
	};

	@Override
	public void preparewell() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);

	}

	private String getPackagename(Context context) {
		// TODO Auto-generated method stub
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return packageinfo.packageName;
	}

	public interface OnAudioFinishListener {
		void audioFinish(float time, String dir);
	}

	private OnAudioFinishListener mOnAudioFinishListener;

	public void setOnAudioFinishListener(OnAudioFinishListener listener) {
		mOnAudioFinishListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				changeState(STATE_RECORDING);
				break;
			case MotionEvent.ACTION_MOVE:
				if (mIsRecording) {
					if (wantToCancle(x, y)) {
						changeState(STATE_WANT_TO_CANCLE);
						mDialogManager.cancleRecordingDialog();

					} else {
						changeState(STATE_RECORDING);
						// TODO Dialog不改变
						mDialogManager.recordingDialog();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				// 1、点击了按钮但是没触发longclick
				// 2、点击按钮触发longclick但是AudioManager还没有准备好
				// 3、录音时间很短
				if (!mIsReady) {
					reset();
					return super.onTouchEvent(event);
				}
				if (!mIsRecording || mTime < 0.6f) {
					mDialogManager.tooShortDilaog();
					// 为了消除生成的文件夹
					mAudioManager.cancle();

					// 延迟一段时间使Dialog消失
					mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 700);


				} else if (mCurrentState == STATE_RECORDING) {
					// 正常录制结束
					mDialogManager.dismiss();
					mAudioManager.release();
					if (mOnAudioFinishListener != null) {
						mOnAudioFinishListener.audioFinish(mTime,
								mAudioManager.getmCurrentFilePath());
					}
				}else if (mCurrentState == STATE_WANT_TO_CANCLE) {
						mDialogManager.dismiss();
						mAudioManager.cancle();
//						changeState(STATE_NORMAL);

					}



				reset();

				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	private boolean wantToCancle(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_CANCEL_Y || y > getHeight() + DISTANCE_CANCEL_Y) {
			return true;
		}
		return false;
	}

	private void changeState(int state) {
		// TODO Auto-generated method stub
		if (mCurrentState != state) {
			mCurrentState = state;
			switch (state) {
				case STATE_NORMAL:
					setBackgroundResource(R.drawable.audiorecoder_button_bg_normal);
					setText("按住说话");

					break;
				case STATE_RECORDING:
					setBackgroundResource(R.drawable.audiorecoder_button_bg_recoding);
					setText("松开完成");

					break;
				case STATE_WANT_TO_CANCLE:
					setBackgroundResource(R.drawable.audiorecoder_button_bg_recoding);
					setText("上滑取消");

					break;
				default:
					break;
			}
		}

	}

	// 恢复初始状态
	private void reset() {
		mIsRecording = false;
		mTime = 0;
		mIsReady = false;
		changeState(STATE_NORMAL);
	}

}
