package com.example.administrator.myapplication.set.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.ActivityController;
import com.example.administrator.myapplication.base.BaseActivity;
import com.example.administrator.myapplication.base.MainActivity;
import com.example.administrator.myapplication.utils.DataCleanManager;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.login.Login;
import com.example.administrator.myapplication.set.activity.WriteFoot;
import com.example.administrator.myapplication.set.activity.LocationMapActivity;
import com.example.administrator.myapplication.set.activity.SetIcon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/1/2.
 */
public class Set extends Fragment implements View.OnClickListener{
	public static final int REQUEST_NAME = 0;
	private static final int REQUEST_ACT_LOCATION = 100;
	private CircleImageView mCircleViewIcon,mCircleIcon;
	private TextView mTextViewLogin, mTextSex, mTextData,mTextName,mTextLocation;
	private EditText mEditMotto;
	private View mLayoutLove;
	private LinearLayout mLayoutLocation, mLayoutBirthday, mLayoutSex, mLayoutName, mLayoutIcon, mLayoutCache;
	private Button mButtonQuit, mButtonSure;
	private String []mSexs={"男","女"};
	private String sex ,data;
	private Calendar mCalendar;
	public static String NAME = "loginName", SEX = "sex", DATA = "data", ADDRESS = "address";
	private AlertDialog alertDialog;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.set,null);
		initWidgets(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String pathMe =getActivity().getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/myicon.jpg";
		Bitmap bitmap = BaseActivity.getDiskBitmap(pathMe);
		Log.e("path" , pathMe);
		if (bitmap != null){
			mCircleIcon.setImageBitmap(bitmap);
			mCircleViewIcon.setImageBitmap(bitmap);
		}else {
			mCircleIcon.setImageResource(R.mipmap.icon);
		}
		mTextData.setText(PreferencesUtils.getInstance().readSharedString(getActivity(),DATA, "1月1日"));

		mTextSex.setText(PreferencesUtils.getInstance().readSharedString(getActivity(),SEX, "男"));

		mTextName.setText(PreferencesUtils.getInstance().readSharedString(getActivity(), NAME, "二锅头"));

		mTextLocation.setText(PreferencesUtils.getInstance().readSharedString(getActivity(),ADDRESS,""));

	}

	private void initWidgets(View view) {
		mCircleViewIcon= (CircleImageView) view.findViewById(R.id.circleImageView_icon);
		mCircleViewIcon.setOnClickListener(this);
		mTextViewLogin= (TextView) view.findViewById(R.id.textview_login);
		mTextViewLogin.setOnClickListener(this);
		mLayoutLove=view.findViewById(R.id.layout_love);
		mLayoutLove.setOnClickListener(this);
		mButtonQuit= (Button) view.findViewById(R.id.person_info_login_out);
		mButtonQuit.setOnClickListener(this);
		mButtonSure = (Button) view.findViewById(R.id.button_sure);
		mButtonSure.setOnClickListener(this);
		mLayoutLocation = (LinearLayout) view.findViewById(R.id.person_info_location_ll);
		mLayoutLocation.setOnClickListener(this);
		mLayoutBirthday = (LinearLayout) view.findViewById(R.id.layout_mybirthday);
		mLayoutBirthday.setOnClickListener(this);
		mLayoutSex = (LinearLayout) view.findViewById(R.id.layout_mysex);
		mLayoutSex.setOnClickListener(this);
		mLayoutName = (LinearLayout) view.findViewById(R.id.layout_myname);
		mLayoutName.setOnClickListener(this);
		mLayoutIcon = (LinearLayout) view.findViewById(R.id.layout_myicon);
		mLayoutIcon.setOnClickListener(this);
		mTextSex = (TextView) view.findViewById(R.id.person_info_gender);
		mTextData = (TextView) view.findViewById(R.id.person_info_birthday);
		mTextName = (TextView) view.findViewById(R.id.person_info_nickname);
		mCircleIcon = (CircleImageView) view.findViewById(R.id.circleImageView_mine);
		mTextLocation = (TextView) view.findViewById(R.id.person_info_location);
		mEditMotto = (EditText) view.findViewById(R.id.edit_motto);
		mLayoutCache = (LinearLayout) view.findViewById(R.id.layout_cache);
		mLayoutCache.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.button_sure:
				if (mEditMotto.getText() != null) {
					mTextViewLogin.setText(mEditMotto.getText().toString().trim());
					mEditMotto.setText("");
				}
				break;
			case R.id.person_info_location_ll:
				startActivityForResult(new Intent(getActivity(), LocationMapActivity.class), REQUEST_ACT_LOCATION);
				break;
			case R.id.layout_mybirthday:
				showCalendarDialog("noYear",mTextData,DATA);
				break;
			case R.id.layout_mysex:
				showSexDialog(mTextSex,SEX);
				break;
			case R.id.layout_myname:
//				startActivityForResult(new Intent(getActivity(),WriteFoot.class),REQUEST_NAME);
				break;
			case R.id.layout_myicon:
				startActivity(new Intent(getActivity(),SetIcon.class));
				break;
			case R.id.person_info_login_out:
				new MainActivity().quitHuanxin(getActivity().getApplicationContext());
				ActivityController.finishAll();
				startActivity(new Intent(getActivity(), Login.class));
				Toast.makeText(getActivity().getApplicationContext(),"已退出应用",Toast.LENGTH_SHORT).show();
				break;
			case R.id.layout_cache:
				View dialogView = View.inflate(getActivity(), R.layout.custom_dialog_view, null);
				TextView cancel= (TextView) dialogView.findViewById(R.id.dialog_cancle);
				TextView ensure= (TextView) dialogView.findViewById(R.id.dialog_confirm);
				TextView title = (TextView) dialogView.findViewById(R.id.dialog_title);
				cancel.setText("取消");
				ensure.setText("确定");
				try {
					title.setText("清除缓存"+ DataCleanManager.getTotalCacheSize(getActivity()) + "?");
				} catch (Exception e) {
					e.printStackTrace();
				}
				alertDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
				dialogView.findViewById(R.id.dialog_cancle).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				});
				dialogView.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DataCleanManager.clearAllCache(getActivity());
						alertDialog.dismiss();
					}
				});
				alertDialog.show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getActivity().RESULT_OK == resultCode) {
			switch (requestCode) {
				case REQUEST_ACT_LOCATION:

					String address = data.getStringExtra("address");
					mTextLocation.setText(address);
					PreferencesUtils.getInstance().writeSharedString(getActivity(), ADDRESS, address);
					Toast.makeText(getActivity().getApplicationContext(),address,Toast.LENGTH_SHORT).show();
					break;
				case REQUEST_NAME:
					String name = data.getStringExtra(WriteFoot.SET_USERNAME);
					mTextName.setText(name);
					Toast.makeText(getActivity().getApplicationContext(),name,Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	}
	private void showSexDialog(final TextView textView, final String sharSex){
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setSingleChoiceItems(mSexs, 2, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sex=mSexs[which];
				Toast.makeText(getActivity().getApplicationContext(),"你的选择是"+ sex,Toast.LENGTH_LONG).show();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				textView.setText(sex);
				PreferencesUtils.getInstance().writeSharedString(getActivity(),sharSex,sex);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Dialog dialog=builder.show();
		dialog.show();
	}

	private void showCalendarDialog(final String typeYear, final TextView textView,final String sharData) {
		mCalendar= Calendar.getInstance();
		DatePickerDialog dialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(year,monthOfYear,dayOfMonth);
				SimpleDateFormat format=null;
				if ("Year".equals(typeYear)){
					format=new SimpleDateFormat("yyyy年MM月dd日");
				}else if ("noYear".equals(typeYear)){
					format=new SimpleDateFormat("MM月dd日");
				}
				data=format.format(mCalendar.getTime());
				textView.setText(data);
				PreferencesUtils.getInstance().writeSharedString(getActivity(), sharData, data);
			}
		},mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

}
