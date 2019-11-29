package com.qjj.cn.myredraindemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.qjj.cn.myredraindemo.model.RedRainActivityResponse;
import com.qjj.cn.myredraindemo.red_permission.FloatWinPermissionCompat;
import com.qjj.cn.myredraindemo.service.RedRainService;
import com.qjj.cn.myredraindemo.util.StatusBarUtil;
import com.qjj.cn.myredraindemo.widget.RedRainPopupView;

import java.util.HashMap;
import java.util.Map;


/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/10/25
 * Describe: 红包雨活动界面
 */
public class RedRainActivity extends Activity {
    private RedRainPopupView redRainPopupView;
    private int type = 0;
    /**
     * 红包雨ID
     */
    private String redpacketrainid;
    /**
     * 场次  第几场红包雨
     * 5
     */
    private int session = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this, true, true, R.color.transparent);
        setContentView(R.layout.activity_red_rain);
        initView();
        initData();
    }


    private void initView() {
        redRainPopupView = findViewById(R.id.redRainPopupView);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundleExtra = intent.getBundleExtra("data");
            if (bundleExtra != null) {
                RedRainActivityResponse.ResultEntity data = bundleExtra.getParcelable(RedRainService.REDRAINACTIVITYRESPONSE_KEY);
                this.session = bundleExtra.getInt(RedRainService.SESSION_KEY, 1);
                redRainPopupView.setSession(session);
                this.type = bundleExtra.getInt("type", 4);
                Log.i("RedRain", "RedRainActivity initView   intent  type: " + type + "    session:" + session + "  data:" + data.toString());
                getDataToUI(data);
            } else {
                this.type = intent.getIntExtra("type", 0);
                this.session = intent.getIntExtra(RedRainService.SESSION_KEY, 0);
                Log.i("RedRain", "RedRainActivity initView   intent  type: " + type + "    session:" + session);
                redRainPopupView.setSession(session);
                if (type == 2) {
                    int duration = intent.getIntExtra(RedRainService.DURATION_KEY, 0);
                    int countdown = intent.getIntExtra(RedRainService.COUNTDOWN_KEY, 0);
                    int percent = intent.getIntExtra(RedRainService.PERCENT_KEY, 50);
                    this.redpacketrainid = intent.getStringExtra(RedRainService.REDPACKETRAINID_KEY);
                    String types = intent.getStringExtra(RedRainService.TYPES_KEY);
                    redRainPopupView.setCountDown(countdown);
                    redRainPopupView.setDuration(duration);
                    redRainPopupView.setRedpacketrainid(redpacketrainid);
                    redRainPopupView.setTypes(types);
                    redRainPopupView.setProbability(percent);
                } else {
                    RedRainActivityResponse.ResultEntity data = intent.getParcelableExtra(RedRainService.REDRAINACTIVITYRESPONSE_KEY);
                    Log.i("RedRain", "RedRainActivity    intent  data: " + data.toString());
                    getDataToUI(data);
                }
            }
        }

        redRainPopupView.setOnProgressListener(new RedRainPopupView.OnProgressListener() {
            @Override
            public void onStopRedRain(int number) {
                checkPermissionAndShow(number);
            }

            @Override
            public void onEnd(boolean isAdd) {
                Log.i("RedRain", "StartRedRain   RedRainActivity    onEnd  getSession(): " + redRainPopupView.getSession() + "  getRedpacketrainid(): " + redRainPopupView.getRedpacketrainid() + "   redpacketrainid: " + redpacketrainid + "  session: " + session);
                //红包雨结算界面  由于一些原因 暂时不公开
               /* Intent intent1 = new Intent(RedRainActivity.this, RedRainAwardPopActivity.class);
                intent1.putExtra(RedRainService.SESSION_KEY, redRainPopupView.getSession());
                intent1.putExtra(RedRainService.REDPACKETRAINID_KEY, redRainPopupView.getRedpacketrainid());
                startActivity(intent1);*/

                Intent intent = new Intent(RedRainActivity.this, RedRainService.class);
                intent.putExtra(RedRainService.SHOWTYPE_KEY, RedRainService.TYPE_START_REDRAIN);
                startService(intent);
                dismiss();
            }
        });

        redRainPopupView.run();
        Log.i("RedRain", "StartRedRain  RedRainActivity    intent   redpacketrainid: " + redpacketrainid + "  session: " + session);
    }

    private void initData() {
        getRedPacketData();
    }

    private void getDataToUI(RedRainActivityResponse.ResultEntity data) {
        if (data != null) {
            this.redpacketrainid = data.getRedPacketRainId();
            redRainPopupView.setTypes(data.getTypes());
            redRainPopupView.setRedpacketrainid(data.getRedPacketRainId());
            redRainPopupView.setProbability(data.getPercent());
            if (type == 0 || type == 4) {
                redRainPopupView.setCountDown(Integer.parseInt(data.getCountdown()));
                redRainPopupView.setDuration(Integer.parseInt(data.getDuration()));
            } else if (type == 1) {
                Long serverTime = Long.parseLong(data.getServerTime()); //服务器当前时间，时间戳
                Long beginTime = Long.parseLong(data.getBeginTime());//开始下雨时间 ，时间戳
                if (serverTime < beginTime) {
                    long time = beginTime - serverTime;
                }
            }
        }
    }

    /**
     * 获取当前场次红包雨 获得的具体金额数
     * 如果在红包雨下落期间（活动期间） 用户手动关闭当前界面
     * 再次打开后 应当再次显示关闭前同场红包雨获得金额数 以及展示列表
     */
    private void getRedPacketData() {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("redPacketRainId", redpacketrainid);
        stringMap.put("times", String.valueOf(session));
        //请求服务器
        /*OkGo.<String>post(Api.formatUrl(Api.WALLET_GETREDPACKETRAINMONEYTOTAL))
                .tag(RedRainActivity.this)
                .params(stringMap)
                .execute(new StringDialogCallback(RedRainActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        RedPacketRainMoneyTotalResponse rainMoneyTotalResponse = com.xiaowu.common.utils.Convert.fromJson(response.body(), RedPacketRainMoneyTotalResponse.class);
                        if (rainMoneyTotalResponse.getStatus() == 1) {
                            List<RedPacketRainMoneyTotalResponse.DataBean> data = rainMoneyTotalResponse.getData();
                            Log.i(TAG, "RedRainActivity      getRedPacketData  rainMoneyTotalResponse.getData(): " + data.toString());
                            if (redRainPopupView != null) {
                                redRainPopupView.setMoneyTotalsData(data);
                                Log.i(TAG, "StartRedRain   RedRainActivity      getRedPacketData  redRainPopupView.setMoneyTotalsData ");

                            }
                        }
                    }
                });*/
    }

    /**
     * 检查是否有悬浮窗权限
     *
     * @param number
     */
    protected void checkPermissionAndShow(int number) {
        // 检查是否已经授权
        if (FloatWinPermissionCompat.getInstance().check(RedRainActivity.this)) {
            // 已经授权
            showFloatWindowDelay(number);
        } else {
            // 授权提示 red_rain_permission_content
            new AlertDialog.Builder(RedRainActivity.this).setTitle(getString(R.string.red_rain_permission_title))
                    .setMessage(getString(R.string.red_rain_permission_content))
                    .setPositiveButton(getString(R.string.red_rain_permission_open), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 显示授权界面
                            try {
                                FloatWinPermissionCompat.getInstance().apply(RedRainActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.red_rain_permission_cancel), null).show();
        }
    }

    private void showFloatWindowDelay(int duration) {
        Intent intent = new Intent(RedRainActivity.this, RedRainService.class);
        intent.putExtra(RedRainService.SHOWTYPE_KEY, RedRainService.TYPE_START_FLOATING);
        if (redRainPopupView != null) {
            int durations = redRainPopupView.getDuration();
            int countdown = redRainPopupView.getCountdown();
            if (redRainPopupView.getDuration() < 60) {
                durations++;
            } else {
                countdown++;
            }
            intent.putExtra(RedRainService.DURATION_KEY, durations);
            intent.putExtra(RedRainService.COUNTDOWN_KEY, countdown);
            intent.putExtra(RedRainService.TYPES_KEY, redRainPopupView.getTypes());
            intent.putExtra(RedRainService.REDPACKETRAINID_KEY, redRainPopupView.getRedpacketrainid());
            intent.putExtra(RedRainService.SESSION_KEY, redRainPopupView.getSession());
            intent.putExtra(RedRainService.PERCENT_KEY, redRainPopupView.getProbability());
        }
        startService(intent);
        dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkPermissionAndShow(redRainPopupView.getDuration());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void dismiss() {
        overridePendingTransition(R.anim.anim_rain_in, R.anim.anim_rain_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (redRainPopupView != null) {
            redRainPopupView.onDestroy();
        }
    }
}
