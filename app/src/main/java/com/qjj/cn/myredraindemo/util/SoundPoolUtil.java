package com.qjj.cn.myredraindemo.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.qjj.cn.myredraindemo.R;


public class SoundPoolUtil {
    private static SoundPoolUtil soundPoolUtil;
    private SoundPool soundPool;
    private int play;

    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        //加载音频文件
        soundPool.load(context, R.raw.red_rain_add, 1);
        soundPool.load(context, R.raw.open_red_rain, 1);
    }

    public void play(int number) {
//        LogUtils.logd( "number " + number);
        //播放音频
        play = soundPool.play(number, 1, 1, 0, 0, 1);
    }

    public void play(int number,int loop) {
//        LogUtils.logd( "number " + number);
        //播放音频
        play = soundPool.play(number, 1, 1, 0, loop, 1);
    }

    public void stop(int number){
//        LogUtils.logd( "play " + play);
        soundPool.stop(play);
//        soundPool.pause(number);
    }


}
