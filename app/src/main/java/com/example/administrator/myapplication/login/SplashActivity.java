package com.example.administrator.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.example.administrator.myapplication.base.BaseActivity;
import com.example.administrator.myapplication.base.MainActivity;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.example.administrator.myapplication.R;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;

	
	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.em_activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);

	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				Boolean isLogin = PreferencesUtils.getInstance().readSharedBoolean(getApplicationContext(),"isLogin",false);
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}
				if (isLogin) {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
				} else {
					startActivity(new Intent(SplashActivity.this, Login.class));
				}
				finish();
			}
		}).start();

	}

}
