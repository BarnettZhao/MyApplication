package com.example.administrator.myapplication.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.ActivityController;
import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.MainActivity;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.example.administrator.myapplication.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/1/10.
 * login
 */
public class Login extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "Login";
    private CircleImageView mCircleLoginIcon;
    private EditText mEditName,mEditPass;
    private CheckBox mCheckRemember,mCheckLogin;
    private Button mButtonLogin;
    private TextView mTextRegister,mTextForget;
    private String loginName, loginPwd;
    private boolean progressShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initWidgets();
       /* String pathMe =getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/me.jpg";
        Bitmap bitmapLogin = getDiskBitmap(pathMe);
        if (bitmapLogin != null){
            mCircleLoginIcon.setImageBitmap(bitmapLogin);
        }else {
            mCircleLoginIcon.setImageResource(R.mipmap.icon);
        }*/
        if (PreferencesUtils.getInstance().readSharedBoolean(this,"isremember",false)) {
            mEditName.setText(PreferencesUtils.getInstance().readSharedString(this,"loginName",""));
            mEditPass.setText(PreferencesUtils.getInstance().readSharedString(this,"pwd",""));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String pathMe = "";
        if (getApplicationContext().getExternalFilesDir("icon").exists()) {
            pathMe = getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/myicon.jpg";
        }
//		String pathIt =getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/it.jpg";
        Log.e("path" , pathMe);
        Bitmap bitmap = BaseActivity.getDiskBitmap(pathMe);
        Log.e("path" , pathMe);
        if (bitmap != null){
            mCircleLoginIcon.setImageBitmap(bitmap);
        }else {
            mCircleLoginIcon.setImageResource(R.mipmap.icon);
        }
    }

    private void initWidgets() {
        mCircleLoginIcon= (CircleImageView) findViewById(R.id.login_icon);
        mEditName= (EditText) findViewById(R.id.edit_name);
        mEditPass= (EditText) findViewById(R.id.edit_pass);
        mCheckRemember= (CheckBox) findViewById(R.id.check_remember);
        mCheckRemember.setOnClickListener(this);
        mCheckLogin= (CheckBox) findViewById(R.id.check_login);
        mCheckLogin.setOnClickListener(this);
        mButtonLogin= (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        mTextRegister= (TextView) findViewById(R.id.login_register);
        mTextRegister.setOnClickListener(this);
        mTextForget= (TextView) findViewById(R.id.login_forget);
        mTextForget.setOnClickListener(this);
        PreferencesUtils.getInstance().writeSharedBoolean(this, "isLogin", false);
    }

    @Override
    public void onClick(View v) {
        loginName = mEditName.getText().toString().trim();
        loginPwd = mEditPass.getText().toString().trim();
        switch (v.getId()){
            case R.id.check_remember:
                if (mCheckRemember.isChecked()) {
                    PreferencesUtils.getInstance().writeSharedBoolean(this, "isremember", true);
//                    PreferencesUtils.getInstance().writeSharedString(this,"loginPwd",loginPwd);
                } else {
                    PreferencesUtils.getInstance().writeSharedBoolean(this, "isremember", false);
                }
                break;
            case R.id.check_login:
                if (mCheckLogin.isChecked()) {
                    if (!mCheckRemember.isChecked()) {
                        mCheckRemember.setChecked(true);
                    }
                    PreferencesUtils.getInstance().writeSharedBoolean(this,"isLogin",true);
                } else {
                    PreferencesUtils.getInstance().writeSharedBoolean(this,"isLogin",false);
                }
                Log.e(TAG,PreferencesUtils.getInstance().readSharedBoolean(this,"isLogin",true)+"");
                break;
            case R.id.button_login:
                if (mCheckRemember.isChecked()) {
                    PreferencesUtils.getInstance().writeSharedBoolean(this, "isremember", true);
//                    PreferencesUtils.getInstance().writeSharedString(this,"loginPwd",loginPwd);
                } else {
                    PreferencesUtils.getInstance().writeSharedBoolean(this, "isremember", false);
                }
                login();
                break;
            case R.id.login_forget:
                break;
            case R.id.login_register:
                startActivity(new Intent(Login.this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
    /**
     * 登录
     */
    public void login() {

        if (TextUtils.isEmpty(loginName)) {
            Toast.makeText(this, "User_name_cannot_be_empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(this, "Password_cannot_be_empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;
        final ProgressDialog pd = new ProgressDialog(Login.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e(TAG, "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });
        pd.setMessage("Is_landing");
        pd.show();

        // 调用sdk登陆方法登陆聊天服务器
        Log.e(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.e(TAG, "login: onSuccess");

                if (!Login.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick("");
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                //异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
//                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                Log.e(TAG,"name: " + loginName + "pwd: " + loginPwd);
                PreferencesUtils.getInstance().writeSharedString(Login.this, "loginName", mEditName.getText().toString().trim());
                PreferencesUtils.getInstance().writeSharedString(Login.this, "pwd",mEditPass.getText().toString().trim());
                // 进入主页面
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.e(TAG, "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Login_failed" + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ActivityController.finishAll();
        }
        return super.onKeyDown(keyCode, event);
    }
}
