package com.example.administrator.myapplication.photo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.example.administrator.myapplication.R;

public class PhotoActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new Template1Fragment()).commit();
		}
	}
}
