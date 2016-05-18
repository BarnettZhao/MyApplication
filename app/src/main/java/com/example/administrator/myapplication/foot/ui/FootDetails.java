package com.example.administrator.myapplication.foot.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.R;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kkkkk on 2016/3/24.
 */
public class FootDetails extends BaseActivity {
	private TextView textViewShare, textViewName, textViewTime, textViewContent;
	private UMShareAPI mShareAPI;
	private CircleImageView circleImageView;
	private String name,time,content,image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foot_details);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		time = intent.getStringExtra("time");
		content = intent.getStringExtra("content");
		image = intent.getStringExtra("icon");
		initWidgets();
		mShareAPI = UMShareAPI.get(this);
		final UMImage image = new UMImage(FootDetails.this, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
		textViewShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//默认shareboard
				final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
						{
								SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
								SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
						};
				new ShareAction(FootDetails.this)
						.setDisplayList(displaylist)
						.withText(content)
						.withTitle("title")
						.withTargetUrl("http://www.baidu.com")
						.withMedia( image )
						.setListenerList(umShareListener, umShareListener)
//						.setShareboardclickCallback(shareBoardlistener)
						.open();
			}
		});
		//使用自定义的加载Dialog
		ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setTitle("友情提示");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("正在加载...");
		Config.dialog = dialog;
	}

	private void initWidgets() {
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		String time = intent.getStringExtra("time");
		String content = intent.getStringExtra("content");
		String image = intent.getStringExtra("icon");
		circleImageView = (CircleImageView) findViewById(R.id.circleImageView1);
		Bitmap bitmap = getDiskBitmap(image);
		if (bitmap == null) {
			circleImageView.setImageResource(R.mipmap.icon);
		} else {
			circleImageView.setImageBitmap(getDiskBitmap(image));
		}
		textViewShare = (TextView) findViewById(R.id.title_right);
		textViewShare.setBackgroundResource(R.mipmap.share);
		textViewName = (TextView) findViewById(R.id.tv_name);
		textViewName.setText(name);
		textViewTime = (TextView) findViewById(R.id.tv_time);
		textViewTime.setText(time);
		textViewContent = (TextView) findViewById(R.id.tv_message);
		textViewContent.setText(content);
	}

	/**
	 * 分享的回调接口
	 */
	final UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Toast.makeText(FootDetails.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(FootDetails.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(FootDetails.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mShareAPI.onActivityResult(requestCode, resultCode, data);
	}
}
