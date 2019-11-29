package com.qjj.cn.myredraindemo;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qjj.cn.myredraindemo.service.RedRainService;


/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/11/8
 * Describe:
 */
public class MyActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private int startCount;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.e("==============", "======>onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("==============", "======>onActivityStarted");
        startCount++;
        if (startCount == 1) {
            Log.e("==============", "应用在前台了！！！");
            App.getAppContext().setActivity(true);
            if (App.getAppContext().isRedRain()) {
                Intent intent = new Intent(App.getAppContext(), RedRainService.class);
                intent.putExtra(RedRainService.SHOWTYPE_KEY, RedRainService.TYPE_VISIBLE_FLOATING);
                App.getAppContext().startService(intent);
            }

        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("==============", "======>onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("==============", "======>onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("==============", "======>onActivityStopped");
        startCount--;
        if (startCount == 0) {
            Log.e("TAG", "应用在后台了！！！");
            App.getAppContext().setActivity(false);
            if (App.getAppContext().isRedRain()) {
                Intent intent = new Intent(App.getAppContext(), RedRainService.class);
                intent.putExtra(RedRainService.SHOWTYPE_KEY, RedRainService.TYPE_GONE_FLOATING);
                App.getAppContext().startService(intent);
            }
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("==============", "======>onActivityDestroyed");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.e("==============", "======>onActivitySaveInstanceState");
    }


}