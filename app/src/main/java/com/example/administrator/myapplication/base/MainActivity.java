package com.example.administrator.myapplication.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.myapplication.ActivityController;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.chat.fragment.Chat;
import com.example.administrator.myapplication.dao.FriendList;
import com.example.administrator.myapplication.news.fragment.Foot;
import com.example.administrator.myapplication.set.fragment.Set;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private FragmentManager mManager;
    private FragmentTransaction mTransaction;

    private Chat mChat;
    private Foot mFoot;
    private Set mSet;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonFoot,mRadioButtonChat,mRadioButtonSetting;

    private Boolean isExit=false;
    public static List<FriendList> mDatas = null;
    public static List<String> mMessages = null;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        initFragments();
        getFriends();
    }
    private void getFriends() {
        mDatas = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    for (int i = 0; i < usernames.size(); i++) {
                        mDatas.add(new FriendList(usernames.get(i)));
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(),"获取好友列表出错 " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initFragments() {
        mFoot=new Foot();
        mChat=new Chat();
        mSet=new Set();
        mManager=getFragmentManager();
        mTransaction=mManager.beginTransaction();
        mTransaction.replace(R.id.framelayout_main,mFoot);
        mTransaction.commit();
        mRadioGroup.check(R.id.radiobutton_foot);
    }

    private void initWidgets() {
        mRadioGroup= (RadioGroup) findViewById(R.id.radiogroup_main);
        mRadioButtonFoot= (RadioButton) findViewById(R.id.radiobutton_foot);
        mRadioButtonChat= (RadioButton) findViewById(R.id.radiobutton_chat);
        mRadioButtonSetting= (RadioButton) findViewById(R.id.radiobutton_setting);
        mRadioButtonFoot.setOnClickListener(this);
        mRadioButtonChat.setOnClickListener(this);
        mRadioButtonSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mTransaction=mManager.beginTransaction();
        switch (v.getId()){
            case R.id.radiobutton_foot:
                mTransaction.replace(R.id.framelayout_main,mFoot);
                break;
            case R.id.radiobutton_chat:
                mTransaction.replace(R.id.framelayout_main,mChat);
                break;
            case R.id.radiobutton_setting:
                mTransaction.replace(R.id.framelayout_main,mSet);
                break;
            default:
                break;
        }
        mTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if (mFoot.mPopupWindow != null && mFoot.mPopupWindow.isShowing()) {
                mFoot.mPopupWindow.dismiss();
            } else {
            exit();
            }
        }
        return false;
    }
    private void exit() {
        if(!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            quitHuanxin(MainActivity.this);
            ActivityController.finishAll();
        }
    }

    public void quitHuanxin(Context context) {
        Boolean isLogin = PreferencesUtils.getInstance().readSharedBoolean(context,"isLogin",true);
        Log.e("isLogin", isLogin + "");
        if (!isLogin) {
            //此方法为异步方法
            EMClient.getInstance().logout(true, new EMCallBack() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
//						Toast.makeText(getApplicationContext(),"成功退出环信！！！",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "成功退出环信");
                    finish();
                }

                @Override
                public void onProgress(int progress, String status) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(int code, String message) {
                    // TODO Auto-generated method stub
//						Toast.makeText(getApplicationContext(),"退出环信失败！！！",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "退出环信失败");
                }
            });
        }
    }
}
