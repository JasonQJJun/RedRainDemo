package com.qjj.cn.myredraindemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.qjj.cn.myredraindemo.R;
import com.qjj.cn.myredraindemo.model.RedPacketBean;
import com.qjj.cn.myredraindemo.model.RedPacketRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;

/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/10/25
 * Describe:红包雨自定义View
 */
public class RedPacketView extends View {

    private int[] mImgIds = new int[]{R.mipmap.red_1, R.mipmap.red_2};//红包图片
    private int count;//红包数量
    private int speed;//下落速度
    private float maxSize;//红包大小的范围
    private float minSize;//红包大小的范围
    private int mWidth;//view宽度
    private ValueAnimator animator;//属性动画，用该动画来不断改变红包下落的坐标值

    private static final int SLEEP_TIME = 20; //多少毫秒一帧（请根据设备性能权衡）
    private static final float BOOM_PER_TIME = 80 / SLEEP_TIME; //爆炸物多少帧刷新一次（UI给的动画是80ms一帧，所以需要拿 80/每帧时长）。
    private Paint paint;//画笔
    private long prevTime;
    private ArrayList<RedPacketBean> redpacketlist = new ArrayList<>();//红包数组
    private Map<Integer, Bitmap> mBitmapMap = new ConcurrentHashMap<>();

    /**
     * 红包雨 包含红包大类，1现金，2通证
     * 单位 秒
     */
    private String types = "1,2";
    private List<String> typelist;

    public RedPacketView(Context context) {
        super(context);
        init();
    }

    public RedPacketView(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RedPacketStyle);
        count = typedArray.getInt(R.styleable.RedPacketStyle_count, 20);
        speed = typedArray.getInt(R.styleable.RedPacketStyle_speed, 20);
        minSize = typedArray.getFloat(R.styleable.RedPacketStyle_min_size, 0.5f);
        maxSize = typedArray.getFloat(R.styleable.RedPacketStyle_max_size, 1.2f);
        typedArray.recycle();
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setAntiAlias(true);
        animator = ValueAnimator.ofFloat(0, 1);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        initAnimator();
    }


    private void initAnimator() {
        //每次动画更新的时候，更新红包下落的坐标值
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long nowTime = System.currentTimeMillis();
                float secs = (float) (nowTime - prevTime) / 1000f;
                prevTime = nowTime;
                for (int i = 0; i < redpacketlist.size(); ++i) {
                    RedPacketBean redPacket = redpacketlist.get(i);
                    //更新红包的下落的位置y
                    redPacket.y += (redPacket.speed * secs);
                    if (redPacket.getType() == RedPacketBean.TYPE_BOOM_BT || redPacket.getType() == RedPacketBean.TYPE_BOOM_GOLD) {
                        Log.i("RedPacketView", "onAnimationUpdate  redPacket.y: " + redPacket.y + "   (redPacket.speed * secs): " + (redPacket.speed * secs));
                        final int typeIndex = redPacket.addTypeIndex(1) - 1;
                        Log.i("RedPacketView", "onAnimationUpdate  typeIndex " + typeIndex);
                        final int boomIndex = (int) (typeIndex / BOOM_PER_TIME);
                        Log.i("RedPacketView", "onAnimationUpdate  boomIndex " + boomIndex);

                        Log.i("RedPacketView", "onAnimationUpdate  boomIndex " + boomIndex);

                        if (boomIndex < RedPacketRes.BOOM_LIST_BT.length) {
                            Log.i("RedPacketView", "onAnimationUpdate  " +
                                    "redPacket.getType() " + redPacket.getType());

                            if (redPacket.getType() == RedPacketBean.TYPE_BOOM_BT || redPacket.getType() == RedPacketBean.TYPE_PACKET_BT) {
                                redPacket.bitmap = getBitmapFromRes(RedPacketRes.BOOM_LIST_BT[boomIndex]);
                            } else {
                                Log.i("RedPacketView", "onAnimationUpdate  " +
                                        "redPacket.getType() " + redPacket.getType());
                                redPacket.bitmap = getBitmapFromRes(RedPacketRes.BOOM_LIST_GOLD[boomIndex]);
                            }
                        } else {
//                            如果点击在红包上，重新设置起始位置，以及中奖属性
                            redPacket.setTypeIndex(0);
                            redPacket.y = 0 - redPacket.height;

                            int num = 0;
                            //获取红包原始图片
                            if (typelist.size() == 2) {
                                num = (int) (Math.random() * 2);
                            } else if (typelist.size() == 1) {
                                num = Integer.parseInt(typelist.get(0)) - 1;
                            }
                            if (redPacket.getType() == RedPacketBean.TYPE_PACKET_GOLD || redPacket.getType() == RedPacketBean.TYPE_BOOM_GOLD) {
                                redPacket.setType(RedPacketBean.TYPE_PACKET_GOLD);
                            } else {
                                redPacket.setType(RedPacketBean.TYPE_PACKET_BT);
                            }
                            Bitmap originalBitmap = getBitmapFromRes(mImgIds[num]);
                            redPacket.bitmap = originalBitmap;
                        }
                    }
                    //如果y坐标大于view的高度 说明划出屏幕，y重新设置起始位置，以及中奖属性
                    if (redPacket.y > getHeight()) {
                        redPacket.y = 0 - redPacket.height;
                        int num = 0;
                        //获取红包原始图片
                        if (typelist.size() == 2) {
                            num = (int) (Math.random() * 2);
                        } else if (typelist.size() == 1) {
                            num = Integer.parseInt(typelist.get(0)) - 1;
                        }
                        Bitmap originalBitmap = getBitmapFromRes(mImgIds[num]);
                        if (num == 0) {
                            redPacket.setType(RedPacketBean.TYPE_PACKET_GOLD);
                        } else {
                            redPacket.setType(RedPacketBean.TYPE_PACKET_BT);
                        }
                        redPacket.bitmap = originalBitmap;
                    }
                    //更新红包的旋转的角度
                    redPacket.rotation = redPacket.rotation
                            + (redPacket.rotationSpeed * secs);
                }
                invalidate();
            }
        });
        //属性动画无限循环
        animator.setRepeatCount(ValueAnimator.INFINITE);
        //属性值线性变换
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取自定义view的宽度
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        //遍历红包数组，绘制红包
        for (int i = 0; i < redpacketlist.size(); i++) {
            RedPacketBean redPacket = redpacketlist.get(i);
            //将红包旋转redPacket.rotation角度后 移动到（redPacket.x，redPacket.y）进行绘制红包
            Matrix m = new Matrix();
            m.setTranslate(-redPacket.width / 2, -redPacket.height / 2);
            m.postRotate(redPacket.rotation);
            m.postTranslate(redPacket.width / 2 + redPacket.x, redPacket.height / 2 + redPacket.y);
            //绘制红包
            canvas.drawBitmap(redPacket.bitmap, m, paint);

        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //根据点击的坐标点，判断是否点击在红包的区域
                RedPacketBean redPacket = isRedPacketClick(motionEvent.getX(), motionEvent.getY());
                if (redPacket != null) {
                    //如果点击在红包上，重新设置起始位置，以及中奖属性
//                    redPacket.y = 0 - redPacket.height;
                    if (redPacket.getType() == RedPacketBean.TYPE_PACKET_BT || redPacket.getType() == RedPacketBean.TYPE_BOOM_BT) {
                        redPacket.setType(RedPacketBean.TYPE_BOOM_BT);
                    } else {
                        redPacket.setType(RedPacketBean.TYPE_BOOM_GOLD);
                    }
                    if (onRedPacketClickListener != null) {
                        onRedPacketClickListener.onRedPacketClickListener(redPacket);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_CANCEL:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 停止动画
     */
    public void stopRainNow() {
        //清空红包数据
        clear();
        //重绘
        invalidate();
        //动画取消
        animator.cancel();
    }


    /**
     * 开始动画
     */
    public void startRain() {
        //清空红包数据
        clear();
        //添加红包
        setRedpacketCount(count);
        prevTime = System.currentTimeMillis();
        //动画开始
        animator.start();
        invalidate();
    }

    public void setRedpacketCount(int count) {
        if (mImgIds == null || mImgIds.length == 0)
            return;
        for (int i = 0; i < count; ++i) {
            int num = 0;
            //获取红包原始图片
            if (typelist.size() == 2) {
                num = (int) (Math.random() * 2);
            } else if (typelist.size() == 1) {
                num = Integer.parseInt(typelist.get(0)) - 1;
            }
            Bitmap originalBitmap = getBitmapFromRes(mImgIds[num]);
            //生成红包实体类
            RedPacketBean redPacket = new RedPacketBean(getContext(), originalBitmap, speed, maxSize, minSize, mWidth);
            if (num == 0) {
                redPacket.setType(RedPacketBean.TYPE_PACKET_GOLD);
            } else {
                redPacket.setType(RedPacketBean.TYPE_PACKET_BT);
            }
            //添加进入红包数组
            redpacketlist.add(redPacket);
        }
    }

    /**
     * 暂停红包雨
     */
    public void pauseRain() {
        animator.cancel();
    }

    /**
     * 重新开始
     */
    public void restartRain() {
        animator.start();
    }

    /**
     * 清空红包数据，并回收红包中的bitmap
     */
    private void clear() {
        for (RedPacketBean redPacket : redpacketlist) {
            redPacket.recycle();
        }
        redpacketlist.clear();
        mBitmapMap.clear();
    }


    //根据点击坐标点，遍历红包数组，看是否点击在红包上
    private RedPacketBean isRedPacketClick(float x, float y) {
        for (int i = redpacketlist.size() - 1; i >= 0; i--) {
            if (redpacketlist.get(i).isContains(x, y)) {
                return redpacketlist.get(i);
            }
        }
        return null;
    }


    private Bitmap getBitmapFromRes(int imageRes) {
        Bitmap bitmap;
        //缓存策略
        if (mBitmapMap.containsKey(imageRes)) {
            bitmap = mBitmapMap.get(imageRes);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), imageRes);
            mBitmapMap.put(imageRes, bitmap);
        }
        return bitmap;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        if (!TextUtils.isEmpty(types)) {
            this.types = types;
            this.typelist = Arrays.asList(types.split(","));
        }
    }


    public interface OnRedPacketClickListener {
        void onRedPacketClickListener(RedPacketBean redPacket);
    }

    private OnRedPacketClickListener onRedPacketClickListener;

    public void setOnRedPacketClickListener(OnRedPacketClickListener onRedPacketClickListener) {
        this.onRedPacketClickListener = onRedPacketClickListener;
    }


    //dp转px
    public static float dpToPixel(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return dp * displayMetrics.density;
    }


}
