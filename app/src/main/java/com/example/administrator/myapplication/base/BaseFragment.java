package com.example.administrator.myapplication.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umeng.socialize.Config;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import org.json.JSONObject;

/**
 * Created by kkkkk on 2016/4/29.
 */
public class BaseFragment extends Fragment {
	private ProgressDialog dialog;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	public void requestNet (com.yolanda.nohttp.Request<JSONObject> request,int responseCode ){
		RequestQueue requestQueue = NoHttp.newRequestQueue();
		requestQueue.add(responseCode,request,onResponseListener);
	}
	/**
	 * 回调对象，接受请求结果
	 */
	private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {
		@Override
		public void onStart(int what) {
			Log.e("nohttp","onStart");
			showProgressDialog();
		}

		@Override
		public void onSucceed(int what, Response<JSONObject> response) {
			finishProgeressDialog();
			onSuccess(what,response);
		}

		@Override
		public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
			finishProgeressDialog();
			onFail(exception);
		}

		@Override
		public void onFinish(int what) {
			finishProgeressDialog();
			Log.e("nohttp","onFinish");
		}
	};
	public void onSuccess(int what,Response<JSONObject> response) {

	}
	public void onFail (Exception exception){
		Toast.makeText(getActivity().getApplicationContext(),"加载失败"+exception,Toast.LENGTH_SHORT).show();
	}
	private void showProgressDialog(){
		//使用自定义的加载Dialog
		dialog = new ProgressDialog(getActivity());
//        dialog.setTitle("友情提示");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("正在加载...");
		dialog.show();
	}
	private void finishProgeressDialog(){
		dialog.dismiss();
	}
}
