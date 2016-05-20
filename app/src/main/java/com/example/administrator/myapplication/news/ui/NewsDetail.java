package com.example.administrator.myapplication.news.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.base.BaseActivity;
import com.example.administrator.myapplication.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by kkkkk on 2016/4/29.
 */
public class NewsDetail extends BaseActivity implements View.OnClickListener{
	public static final String NEWS_URL = "newsurl";
	public static final String NEWS_TITLE = "newstitle";
	public static final String NEWS_CONTENT = "newscontent";
	public static final String NEWS_SOURCE = "newssource";
	public static final String NEWS_TIME = "newstime";
	private String url,title,content,source,time;
	private WebView webView;
	private ImageView back;
	private TextView textViewShare;
	private UMShareAPI mShareAPI;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_webview);
		initWidgets();
		getIntentData();
	}

	private void initWidgets() {
		webView = (WebView) findViewById(R.id.webView);
		back = (ImageView) findViewById(R.id.left_button);
		back.setOnClickListener(this);
		textViewShare = (TextView) findViewById(R.id.right_text);
		textViewShare.setBackgroundResource(R.mipmap.share);
		textViewShare.setVisibility(View.VISIBLE);
		textViewShare.setOnClickListener(this);
	}

	private void getIntentData () {
		Intent intent = getIntent();
		url = intent.getStringExtra(NEWS_URL);
		title = intent.getStringExtra(NEWS_TITLE);
		content = intent.getStringExtra(NEWS_CONTENT);
		source = intent.getStringExtra(NEWS_SOURCE);
		time = intent.getStringExtra(NEWS_TIME);
		webView.loadUrl(url);
		showProgressDialog();
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				finishProgeressDialog();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_button:
				finish();
				break;
			case R.id.right_text:
				final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
						{
								SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
								SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
						};
				final UMImage image = new UMImage(NewsDetail.this, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
				new ShareAction(NewsDetail.this)
						.setDisplayList(displaylist)
						.withText(content)
						.withTitle(title)
						.withTargetUrl(url)
						.withMedia( image )
						.setListenerList(umShareListener, umShareListener)
//						.setShareboardclickCallback(shareBoardlistener)
						.open();
				break;
			default:
				break;
		}
	}
	/**
	 * 分享的回调接口
	 */
	final UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Toast.makeText(NewsDetail.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(NewsDetail.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(NewsDetail.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mShareAPI.onActivityResult(requestCode, resultCode, data);
	}
	private void showProgressDialog(){
		//使用自定义的加载Dialog
		dialog = new ProgressDialog(this);
//        dialog.setTitle("友情提示");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("正在加载...");
		dialog.show();
	}
	private void finishProgeressDialog(){
		dialog.dismiss();
	}
}
