package com.qjj.cn.myredraindemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.List;




public class App extends Application {

    private static App applinctionContext;
    //是否有红包雨活动
    private boolean isRedRain = false;
    //是否活动在前台
    private boolean isActivity = true;
    //是否开启悬浮窗
    public static boolean isFloating = false;
    private MyActivityLifecycle callback;

    public static App getAppContext() {
        return applinctionContext;
    }

    public static Resources resources() {
        return applinctionContext.getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applinctionContext = this;
        callback = new MyActivityLifecycle();
        registerActivityLifecycleCallbacks(callback);

    }



    public String getLoginToken() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String loginToken = sp.getString("loginToken", "");
        return loginToken;
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }



    private int activityStartCount = 0;

    private Boolean whetherToEnterTheBackground = false;


    public boolean isRedRain() {
        return isRedRain;
    }

    public void setRedRain(boolean redRain) {
        isRedRain = redRain;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        isRedRain = false;
    }
}
