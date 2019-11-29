package com.qjj.cn.myredraindemo.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qjj.cn.myredraindemo.R;
import com.qjj.cn.myredraindemo.model.GetRedPacketRainOpenResponse;
import com.qjj.cn.myredraindemo.model.RedPacketBean;
import com.qjj.cn.myredraindemo.util.SoundPoolUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/10/30
 * Describe:红包雨弹窗View
 */
public class RedRainPopupView extends RelativeLayout implements View.OnClickListener {

    private View rl_clock, btn_close, rl_content, line;
    private View rl_totalmoney1, rl_totalmoney2, rl_totalmoney3, rl_totalmoney4;
    private ImageView iv_totalmoney1, iv_totalmoney2, iv_totalmoney3, iv_totalmoney4;
    private TextView tv_totalmoney1, tv_totalmoney2, tv_totalmoney3, tv_totalmoney4;
    private TextView tv_countdown;
    private FrameLayout ll_root;
    private RelativeLayout red_rain_view;
    private ImageView iv_clock;
    private RedPacketView redPacketsView;
    private Timer timer = new Timer();
    private Context mContext;
    private OnProgressListener mOnProgressListener;

    /**
     * 红包雨 包含红包大类，1现金，2通证
     * 单位 秒
     */
    private String types = "1,2";
    /**
     * 红包雨 倒计时
     * 单位 秒
     */
    private int countdown = 10;
    /**
     * 倒计时计数
     * 单位 秒
     */
    private int number = 0;
    private long numberTime = 60 * 1000L;
    /**
     * 红包雨持续的时间
     * 60 秒
     */
    private int duration = 60;
    /**
     * 场次  第几场红包雨
     * 5
     */
    private int session = 1;

    private int totalmoney = 0;

    /**
     * 中奖率
     */
    private int probability = 50;
    private String redpacketrainid; //红包雨ID

    private SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS");
    /**
     * 开红包累加金额集合
     */
    Map<String, Double> totalmoneysList = new HashMap<>();
    private int index = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    numberTime = numberTime - 125;
                    if (numberTime > 0) {
                        tv_countdown.setText(formatter.format(new Date(numberTime)));
                    } else {
                        numberTime = 0;
                        stopTask.cancel();
                    }

                    break;

                case 200:// 开始下红包雨
                    Log.i("RedRain", "RedRainPopupView   mHandler  300 倒计时中  countdown:" + countdown);
                    if (countdown == 10) {
                        iv_clock.setImageResource(R.mipmap.red_clock_10);
                    } else if (countdown == 9) {
                        iv_clock.setImageResource(R.mipmap.red_clock_9);
                    } else if (countdown == 8) {
                        iv_clock.setImageResource(R.mipmap.red_clock_8);
                    } else if (countdown == 7) {
                        iv_clock.setImageResource(R.mipmap.red_clock_7);
                    } else if (countdown == 6) {
                        iv_clock.setImageResource(R.mipmap.red_clock_6);
                    } else if (countdown == 5) {
                        iv_clock.setImageResource(R.mipmap.red_clock_5);
                    } else if (countdown == 4) {
                        iv_clock.setImageResource(R.mipmap.red_clock_4);
                    } else if (countdown == 3) {
                        iv_clock.setImageResource(R.mipmap.red_clock_3);
                    } else if (countdown == 2) {
                        iv_clock.setImageResource(R.mipmap.red_clock_2);
                    } else if (countdown == 1) {
                        iv_clock.setImageResource(R.mipmap.red_clock_1);
                    } else if (countdown == 0) {
                        rl_clock.setVisibility(View.GONE);
                        red_rain_view.setVisibility(View.VISIBLE);
                        countDownTask.cancel();
                        startRedRain();
                        timer.schedule(startRedRainTask, 0, 1000);
                        timer.schedule(stopTask, 0, 125);
                        return;
                    }
                    countdown--;
                    break;

                case 300://  红包雨倒计时
                    Log.i("RedRain", "RedRainPopupView   mHandler  300 红包雨进行中  duration:" + duration);
                    if (0 == duration) {
                        stopRedRain();
                    }
                    duration--;
                    break;


                case 400:
                    TextView mTextView = (TextView) msg.obj;
                    if (mTextView != null) {
                        mTextView.setVisibility(View.GONE);
                        if (red_rain_view != null) {
                            red_rain_view.removeView(mTextView);
                        }
                    }
                    Bundle data = msg.getData();
                    String moneyType = data.getString("moneyType");
                    String contract = data.getString("contract");
                    int redPackeType = data.getInt("redPackeType", 5);
                    String money = data.getString("money");
                    Log.i("RedRainMoney", " 添加金币  " + contract + " , " + moneyType + " , " + money);
                    Log.i("TotalMoneys", " 获得金币  " + contract + " , " + moneyType + " , " + money);
                    double parseDouble = Double.parseDouble(money);
                    if (!totalmoneysList.containsKey(contract)) {
                        setTotalMoneys(contract, moneyType, parseDouble, redPackeType);
                    } else {
                        Double aDouble = totalmoneysList.get(contract) + parseDouble;
                        Iterator<Map.Entry<String, Double>> it = totalmoneysList.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Double> tab = it.next();
                            String key = tab.getKey();
                            if (key.equals(contract)) {
                                it.remove();
                                break;
                            }
                        }
                        setTotalMoneys(contract, moneyType, aDouble, redPackeType);
                    }
                    break;
            }
        }
    };

    /**
     * 倒计时
     */
    TimerTask countDownTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(200);
        }
    };

    /**
     * 开始红包
     */
    TimerTask startRedRainTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(300);
        }
    };

    /**
     * 开始红包
     */
    TimerTask stopTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(100);
        }
    };
    private SoundPoolUtil mSoundPoolUtil;


    public RedRainPopupView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public RedRainPopupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        mContext = context;

        View mainView = LayoutInflater.from(context).inflate(R.layout.popupwindow_red_rain_layout, this, true);

        ll_root = mainView.findViewById(R.id.ll_root);
        line = mainView.findViewById(R.id.line);
        rl_content = mainView.findViewById(R.id.rl_content);
        rl_clock = mainView.findViewById(R.id.rl_clock);
        btn_close = mainView.findViewById(R.id.btn_close);
        red_rain_view = mainView.findViewById(R.id.red_rain_view);
        iv_clock = mainView.findViewById(R.id.iv_clock);
        redPacketsView = mainView.findViewById(R.id.red_packets_view);
        redPacketsView.setCount(50);
        rl_totalmoney1 = mainView.findViewById(R.id.rl_totalmoney1);
        rl_totalmoney2 = mainView.findViewById(R.id.rl_totalmoney2);
        rl_totalmoney3 = mainView.findViewById(R.id.rl_totalmoney3);
        rl_totalmoney4 = mainView.findViewById(R.id.rl_totalmoney4);

        iv_totalmoney1 = mainView.findViewById(R.id.iv_totalmoney1);
        iv_totalmoney2 = mainView.findViewById(R.id.iv_totalmoney2);
        iv_totalmoney3 = mainView.findViewById(R.id.iv_totalmoney3);
        iv_totalmoney4 = mainView.findViewById(R.id.iv_totalmoney4);

        tv_totalmoney1 = mainView.findViewById(R.id.tv_totalmoney1);
        tv_totalmoney2 = mainView.findViewById(R.id.tv_totalmoney2);
        tv_totalmoney3 = mainView.findViewById(R.id.tv_totalmoney3);
        tv_totalmoney4 = mainView.findViewById(R.id.tv_totalmoney4);

        tv_countdown = mainView.findViewById(R.id.tv_countdown);
        btn_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            if (mOnProgressListener != null) {
                mOnProgressListener.onStopRedRain(this.duration);
            }
        }
    }

    /**
     * 设置当前倒计时  单位秒
     *
     * @param time 如果  time=0  则已经开始
     */
    public void setCountDown(int time) {
        this.countdown = time;
    }

    /**
     * 设置下雨持续多久时长 单位秒
     *
     * @param time 如果  time=0  则已经结束
     */
    public void setDuration(int time) {
        this.duration = time;
        this.numberTime = time * 1000L;
    }

    public String getRedpacketrainid() {
        return redpacketrainid;
    }

    public void setRedpacketrainid(String redpacketrainid) {
        this.redpacketrainid = redpacketrainid;
    }

    /**
     * 设置下雨已进行多少秒 单位秒
     *
     * @param number
     */
    public void setNumberTime(int number) {
        this.number = number;
    }

    public int getCountdown() {
        return this.countdown;
    }


    public int getDuration() {
        return this.duration;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public String getTypes() {
        if (redPacketsView != null) {
            return redPacketsView.getTypes();
        }
        return this.types;
    }

    public void setTypes(String types) {
        this.types = types;
        if (redPacketsView != null) {
            redPacketsView.setTypes(this.types);
        }
    }

    /**
     * 开始下红包雨
     */
    public void run() {
        if (countdown > 0) {
            timer.schedule(countDownTask, 0, 1000);
            rl_clock.setVisibility(View.VISIBLE);
            red_rain_view.setVisibility(View.GONE);
        } else {
            rl_clock.setVisibility(View.GONE);
            red_rain_view.setVisibility(View.VISIBLE);
            if (duration > 0) {
                timer.schedule(startRedRainTask, 0, 1000);
                timer.schedule(stopTask, 0, 125);
                startRedRain();
            }
        }
    }

    /**
     * 开始下红包雨
     */
    public void startRedRain() {
        redPacketsView.startRain();
        redPacketsView.setOnRedPacketClickListener(new RedPacketView.OnRedPacketClickListener() {
            @Override
            public void onRedPacketClickListener(RedPacketBean redPacket) {
                if (mSoundPoolUtil == null) {
                    mSoundPoolUtil = SoundPoolUtil.getInstance(mContext);
                }
                mSoundPoolUtil.play(2);
                //请求服务器 调用开红包接口
                boolean isWinning = redPacket.isRealRedPacket(probability);
                if (isWinning) {
                    redPacketRainOpen(redPacket);
                } else {
                    setWinning(redPacket, false);
                }
            }
        });
    }

    /**
     * 停止下红包雨
     */
    public void stopRedRain() {
        totalmoney = 0;//金额清零
        startRedRainTask.cancel();
        stopTask.cancel();
        timer.cancel();
        duration = 0;
        if (mOnProgressListener != null) {
            if (totalmoneysList.size() > 0) {
                mOnProgressListener.onEnd(true);
            } else {
                mOnProgressListener.onEnd(false);
            }

        }
        redPacketsView.stopRainNow();
    }

    /**
     * 窗口监听
     */
    public interface OnProgressListener {
        void onStopRedRain(int time);

        void onEnd(boolean isAdd);

    }

    public void setOnProgressListener(OnProgressListener listener) {
        this.mOnProgressListener = listener;
    }


    private void setTotalMoneys(String contract, String moneyType, double money, int redPackeType) {
        synchronized (this) {
            Log.i("TotalMoneys", " 累加金币  " + contract + " , " + moneyType + " , " + money);
            totalmoneysList.put(contract, money);
            getIndexToUI(contract, moneyType, money, redPackeType);
        }

    }

    private int setIndex(String type) {
        String tag1 = (String) rl_totalmoney1.getTag();
        String tag2 = (String) rl_totalmoney2.getTag();
        String tag3 = (String) rl_totalmoney3.getTag();
        String tag4 = (String) rl_totalmoney4.getTag();
//        Log.i("RedRain", "RedRainPopupView   getIndexToUI    type:" + type + "  tag1: " + tag1 + "  tag2: " + tag2 + "  tag3: " + tag3 + "  tag4: " + tag4);
        if (type.equals(tag1)) {
            index = 0;
        } else if (type.equals(tag2)) {
            index = 1;
        } else if (type.equals(tag3)) {
            index = 2;
        } else if (type.equals(tag4)) {
            index = 3;
        } else {
            if (TextUtils.isEmpty(tag1)) {
                index = 0;
            } else if (TextUtils.isEmpty(tag2)) {
                index = 1;
            } else if (TextUtils.isEmpty(tag3)) {
                index = 2;
            } else if (TextUtils.isEmpty(tag4)) {
                index = 3;
            }
        }
        return index;
    }

    private void getIndexToUI(String contract, String type, double money, int redPackeType) {
        int caseIndex = setIndex(contract);
        switch (caseIndex) {
            case 0:
                rl_totalmoney1.setVisibility(View.VISIBLE);
                rl_totalmoney1.setTag(contract);
                tv_totalmoney1.setTag(contract);
                getTypeToUI(contract, type, money, iv_totalmoney1, tv_totalmoney1, redPackeType);
                break;

            case 1:
                rl_totalmoney2.setVisibility(View.VISIBLE);
                rl_totalmoney2.setTag(contract);
                tv_totalmoney2.setTag(contract);
                getTypeToUI(contract, type, money, iv_totalmoney2, tv_totalmoney2, redPackeType);
                break;

            case 2:
                rl_totalmoney3.setVisibility(View.VISIBLE);
                rl_totalmoney3.setTag(contract);
                tv_totalmoney3.setTag(contract);
                getTypeToUI(contract, type, money, iv_totalmoney3, tv_totalmoney3, redPackeType);
                break;

            case 3:
                rl_totalmoney4.setVisibility(View.VISIBLE);
                rl_totalmoney4.setTag(contract);
                tv_totalmoney4.setTag(contract);
                getTypeToUI(contract, type, money, iv_totalmoney4, tv_totalmoney4, redPackeType);
                break;
        }
        index++;
    }

    private void getTypeToUI(String contract, String type, double money, ImageView iv_totalmoney, TextView tv_totalmoney, int redPackeType) {
        if (mSoundPoolUtil == null) {
            mSoundPoolUtil = SoundPoolUtil.getInstance(mContext);
        }
        int url = getContract_icon(contract);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.red_rain_placeholder)
                .placeholder(R.mipmap.red_rain_placeholder);
        Glide.with(mContext).load(url).apply(options).into(iv_totalmoney);

        if (redPackeType == RedPacketBean.TYPE_BOOM_GOLD || redPackeType == RedPacketBean.TYPE_PACKET_GOLD) {
            tv_totalmoney.setText("+" + String.format("%.2f", money));
        } else {
            BigDecimal bd1 = new BigDecimal(Double.toString(money));
            tv_totalmoney.setText("+" + String.format("%.6f", Double.parseDouble(bd1.toPlainString())));
        }
        mSoundPoolUtil.play(1);
    }

    private void getDataToUI(String contract, String text, View rl_totalmoney, ImageView iv_totalmoney, TextView tv_totalmoney) {
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.red_rain_placeholder)
                .placeholder(R.mipmap.red_rain_placeholder);
        Glide.with(mContext).load(getContract_icon(contract)).apply(options).into(iv_totalmoney);
        tv_totalmoney.setText("+" + text);
        rl_totalmoney.setVisibility(View.VISIBLE);
        if (rl_totalmoney != null && tv_totalmoney != null) {
            rl_totalmoney.setTag(contract);
            tv_totalmoney.setTag(contract);
        }

    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    /**
     * dip----to---px
     *
     * @return
     */
    public static int dip2px(Context context, int dip) {
        // 缩放比例(密度)
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }


    /**
     * 调用中奖打开红包接口
     *
     * @param redPacket
     */
    private void redPacketRainOpen(final RedPacketBean redPacket) {
        Map<String, String> map = new HashMap<>();
        if (redPacket.getType() == RedPacketBean.TYPE_BOOM_GOLD) {
            map.put("type", "1"); //1现金，2通证
        } else {
            map.put("type", "2");
        }
//        map.put("redPacketRainId", "5dbbef8cdabe713e880014ad");//红包雨ID
//        map.put("times", String.valueOf(2));//	第几场雨

        map.put("redPacketRainId", redpacketrainid);//红包雨ID
        map.put("times", String.valueOf(session));//	第几场雨

        /**
         * 此处为接口返回数据
         */
        GetRedPacketRainOpenResponse rainOpenResponse = new GetRedPacketRainOpenResponse();
        rainOpenResponse.setCode(1);
        rainOpenResponse.setStatus(1);
        rainOpenResponse.setMsg("获得红包");
        rainOpenResponse.setData(new GetRedPacketRainOpenResponse.ResultEntity());
        int num = (int) (Math.random() * 5 + 1);
        switch (num) {
            case 1:
                rainOpenResponse.getData().setSymbol("AAA");
                rainOpenResponse.getData().setContract("AAA");
                rainOpenResponse.getData().setMoney("0.1235");
                break;
            case 2:
                rainOpenResponse.getData().setSymbol("BBB");
                rainOpenResponse.getData().setContract("BBB");
                rainOpenResponse.getData().setMoney("0.2012");
                break;
            case 3:
                rainOpenResponse.getData().setSymbol("CCC");
                rainOpenResponse.getData().setContract("CCC");
                rainOpenResponse.getData().setMoney("0.1013");
                break;
            case 4:
                rainOpenResponse.getData().setSymbol("DDD");
                rainOpenResponse.getData().setContract("DDD");
                rainOpenResponse.getData().setMoney("0.1132");
                break;
            case 5:
                rainOpenResponse.getData().setSymbol("EEE");
                rainOpenResponse.getData().setContract("EEE");
                rainOpenResponse.getData().setMoney("0.1354");
                break;
        }

        if (rainOpenResponse.getStatus() == 1) {
            GetRedPacketRainOpenResponse.ResultEntity data = rainOpenResponse.getData();
            Log.i("RedRainMoney", " Post  " + data.getContract() + " , " + data.getSymbol() + " , " + data.getMoney());
            RedPacketBean redPacketBean = new RedPacketBean();
            redPacketBean.setContract(data.getContract());
            redPacketBean.setMoney(data.getMoney());
            redPacketBean.setSymbol(data.getSymbol());
            redPacketBean.setType(redPacket.getType());
            redPacketBean.x = redPacket.x;
            redPacketBean.y = redPacket.y;
            setWinning(redPacketBean, true);
        } else {
            RedPacketBean redPacketBean = new RedPacketBean();
            redPacketBean.setType(redPacket.getType());
            redPacketBean.x = redPacket.x;
            redPacketBean.y = redPacket.y;
            setWinning(redPacketBean, false);
        }
    }

    /**
     * 设置中奖
     */
    private synchronized void setWinning(final RedPacketBean redPacket, final boolean isWinning) {
        String text = "";
        String money = "0";
        String types = "";
        final TextView tv = new TextView(redPacketsView.getContext());

        if (isWinning) {
            money = redPacket.getMoney();
            types = redPacket.getSymbol();
            if (redPacket.getType() == RedPacketBean.TYPE_BOOM_BT) {
                text = String.format("+%1$s%2$s", money, types);
            } else {
                text = "+" + money;
            }
            if (text.contains(types)) {
                // 字体颜色
                int index = text.indexOf(types);
                ForegroundColorSpan buleSpan = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(buleSpan, index, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                tv.setText(style);
            } else {
                tv.setText(text);
            }
        } else {
            tv.setText(mContext.getString(R.string.happy_and_prosperous));
        }

        tv.setTextColor(Color.parseColor("#FFBD00"));
        tv.setTextSize(20);
        tv.getPaint().setFakeBoldText(true);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) redPacket.x, (int) redPacket.y, 0, 0);

        tv.setLayoutParams(lp);
        red_rain_view.addView(tv);
        final float y = line.getY();
        PropertyValuesHolder scaleYProper = PropertyValuesHolder.ofFloat("translationY", 0, -1300);
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1, 1.1f);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1, 1.1f);
        final ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(tv, scaleYProper, scaleXHolder, scaleYHolder);
        animator.setDuration(1400);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (tv.getY() > 0 && tv.getY() <= y) {
                    tv.setVisibility(View.GONE);
                    animator.cancel();
                }

            }
        });

        Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHandler == null) {
                    return;
                }
                if (isWinning) {
                    /*Message message = mHandler.obtainMessage(400);
                    message.obj = tv;
                    Bundle mBundle = new Bundle();
                    mBundle.putString("moneyType", redPacket.getSymbol());
                    mBundle.putString("money", redPacket.getMoney());
                    mBundle.putString("contract", redPacket.getContract());
                    mBundle.putInt("redPackeType", redPacket.getType());
                    message.setData(mBundle);
                    mHandler.sendMessage(message);*/

                    setMapData(tv, redPacket.getSymbol(), redPacket.getContract(), redPacket.getType(), redPacket.getMoney());
                }
                if (tv != null) {
                    tv.setVisibility(View.GONE);
                    if (red_rain_view != null) {
                        red_rain_view.removeView(tv);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        animator.addListener(mAnimatorListener);
    }

    public int getContract_icon(String contract) {
        if (contract.equals("AAA")){
            return R.mipmap.red_rain_gold;
        } else if (contract.equals("BBB")){
            return R.mipmap.share_qq;
        } else if (contract.equals("CCC")){
            return R.mipmap.share_qq_zone;
        } else if (contract.equals("DDD")){
            return R.mipmap.share_wechat;
        } else if (contract.equals("EEE")){
            return R.mipmap.share_wechat_pyq;
        } else {
            return R.mipmap.share_weibo;
        }
    }


    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (countDownTask != null) {
            countDownTask.cancel();
        }

        if (startRedRainTask != null) {
            startRedRainTask.cancel();
        }
        if (stopTask != null) {
            stopTask.cancel();
        }
        timer = null;
        countDownTask = null;
        startRedRainTask = null;

        mContext = null;

        mHandler.removeMessages(100);
        mHandler.removeMessages(200);
        mHandler.removeMessages(300);
        mHandler.removeMessages(400);
        totalmoneysList.clear();
        totalmoneysList = null;
        mHandler = null;
    }

    /**
     * 设置上次操作的数据 并显示UI
     *
     * @param data
     */
/*    public void setMoneyTotalsData(List<RedPacketRainMoneyTotalResponse.DataBean> data) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                RedPacketRainMoneyTotalResponse.DataBean dataBean = data.get(i);
                totalmoneysList.put(dataBean.getContract(), Double.parseDouble(dataBean.getMoney()));
                if (i == 0) {
                    getDataToUI(dataBean.getContract(), dataBean.getMoney(), rl_totalmoney1, iv_totalmoney1, tv_totalmoney1);

                } else if (i == 1) {
                    getDataToUI(dataBean.getContract(), dataBean.getMoney(), rl_totalmoney2, iv_totalmoney2, tv_totalmoney2);

                } else if (i == 2) {
                    getDataToUI(dataBean.getContract(), dataBean.getMoney(), rl_totalmoney3, iv_totalmoney3, tv_totalmoney3);

                } else if (i == 3) {
                    getDataToUI(dataBean.getContract(), dataBean.getMoney(), rl_totalmoney4, iv_totalmoney4, tv_totalmoney4);
                }
            }
        }
    }*/

    /**
     * 获取在红包雨活动中获得的金额总数
     *
     * @return
     */
    public Map<String, Double> getMoneyTotalsData() {
        return this.totalmoneysList;
    }

    private void setMapData(TextView mTextView, String moneyType, String contract, int redPackeType, String money) {
        Log.i("RedRainMoney", " 添加金币  " + contract + " , " + moneyType + " , " + money);
        Log.i("TotalMoneys", " 获得金币  " + contract + " , " + moneyType + " , " + money);
        double parseDouble = Double.parseDouble(money);
        if (!totalmoneysList.containsKey(contract)) {
            setTotalMoneys(contract, moneyType, parseDouble, redPackeType);
        } else {
            Double aDouble = totalmoneysList.get(contract) + parseDouble;
            Iterator<Map.Entry<String, Double>> it = totalmoneysList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Double> tab = it.next();
                String key = tab.getKey();
                if (key.equals(contract)) {
                    it.remove();
                    break;
                }
            }
            setTotalMoneys(contract, moneyType, aDouble, redPackeType);
        }
    }

}
