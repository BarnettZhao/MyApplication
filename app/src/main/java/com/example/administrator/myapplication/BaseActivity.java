package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.myapplication.activity.Login;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.util.NetUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/1/5.
 */
public class BaseActivity extends Activity{
    private static final String TAG = "BaseActivity";
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(getApplicationContext(),"显示帐号在其他设备登陆",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BaseActivity.this, Login.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
            Log.e(TAG, "已连接到服务器");
        }
        @Override
        public void onDisconnected(final int error) {
//            runOnUiThread(new Runnable() {

//                @Override
//                public void run() {
            if(error == EMError.USER_REMOVED){
                // 显示帐号已经被移除
                Log.e(TAG, "显示帐号已经被移除");
            }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                // 显示帐号在其他设备登陆
                Log.e(TAG, "显示帐号在其他设备登陆");
                handler.sendEmptyMessage(1);

            } else {
                if (NetUtils.hasNetwork(getApplicationContext())){
                    //连接不到聊天服务器
                    Log.e(TAG, "连接不到聊天服务器");
                }else{
                    //当前网络不可用，请检查网络设置
                    Log.e(TAG, "当前网络不可用，请检查网络设置");
                }
            }
//                }
//            });
        }
    }
    /**
     * 从本地获取图片
     * @param pathString 文件路径
     * @return 图片
     */
    public static Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }
}
