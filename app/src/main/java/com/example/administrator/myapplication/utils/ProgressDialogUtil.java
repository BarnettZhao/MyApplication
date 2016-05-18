package com.example.administrator.myapplication.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by kkkkk on 2016/5/18.
 */
public class ProgressDialogUtil {
	private ProgressDialog dialog;
	private static ProgressDialogUtil progressDialog;
	private Context context;

	public ProgressDialogUtil(Context context) {
		this.context = context;
	}
	public static  ProgressDialogUtil getInstance (Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialogUtil(context);
		}
		return progressDialog;
	}
	public void showProgressDialog(){
		//使用自定义的加载Dialog
		dialog = new ProgressDialog(context);
//        dialog.setTitle("友情提示");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("正在加载...");
		dialog.show();
	}
	public void finishProgeressDialog(){
		dialog.dismiss();
	}
}
