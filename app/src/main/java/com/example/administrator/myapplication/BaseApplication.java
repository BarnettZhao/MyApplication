package com.example.administrator.myapplication;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.umeng.socialize.PlatformConfig;
import com.yolanda.nohttp.NoHttp;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class BaseApplication extends Application {
    private static BaseApplication mApplication;
    EMOptions options;
    private static final String TAG = "BaseApplication";
    /**
     * 获取Context
     * @return 返回Context的对象
     */
    public static Context getContext(){
        return mApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mApplication = this;
        NoHttp.init(this);
        SDKInitializer.initialize(getApplicationContext());
        Fresco.initialize(this);
        PlatformConfig.setWeixin("wxa59fc6dbc91f7521", "893e44ae26ffe4a92d811d498faac79f");
//        PlatformConfig.setWeixin("wx1e48313855ee1630", "4d395bee2cc7ce077773e0cc9d93da97");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        Context appContext = this;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果app启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(appContext.getPackageName())) {
//			Log.e(TAG, "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        //初始化
        EMClient.getInstance().init(getApplicationContext(), options);
        //注册一个监听连接状态的listener

    }
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
