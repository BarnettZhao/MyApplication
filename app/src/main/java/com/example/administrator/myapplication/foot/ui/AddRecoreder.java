package com.example.administrator.myapplication.foot.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.database.MyDbManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kkkkk on 2016/3/23.
 */
public class AddRecoreder extends BaseActivity implements View.OnClickListener{

	private TextView textViewSure;
	private MyDbManager myDbManager;
	private EditText mEditTextContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_recorder);
		textViewSure = (TextView) findViewById(R.id.title_right);
		textViewSure.setText("确定");
		textViewSure.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.edit_add_recorder);
		myDbManager = MyDbManager.newInstances(this);
	}

	@Override
	public void onClick(View v) {
		String  iconPath =getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/myicon.jpg";
		String content = mEditTextContent.getText().toString().trim();
		String name = PreferencesUtils.getInstance().readSharedString(this,"loginName" ,"");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String time = formatter.format(curDate);
		switch (v.getId()) {
			case R.id.title_right:
//				myDbManager.insert("foot",new FootThing(iconPath,name ,time,content));
				finish();
				break;
			default:
				break;
		}
	}
}
