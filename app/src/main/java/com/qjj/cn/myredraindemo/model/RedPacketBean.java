package com.qjj.cn.myredraindemo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/10/25
 * Describe: 红包实体类
 */
public class RedPacketBean implements Parcelable {
    public static final int TYPE_PACKET_BT = 1; // 区块链币
    public static final int TYPE_PACKET_GOLD = 2; // 金币

    public static final int TYPE_BOOM_BT = 5; //爆炸
    public static final int TYPE_BOOM_GOLD = 6; //爆炸

    public float x, y;
    public float rotation;
    public float speed;
    public float rotationSpeed;
    public int width, height;
    public Bitmap bitmap;
//    public boolean isRealRed;

    private String contract;
    private String symbol;
    private String money;

    private int mType = TYPE_PACKET_BT; //飘落物体的类型。
    private int mTypeIndex; //飘落物当前类型的索引（每帧自增）。
    //累加数
    private int num;

    public RedPacketBean(Context context, Bitmap originalBitmap, int speed, float maxSize, float minSize, int viewWidth) {
        //获取一个显示红包大小的倍数
        double widthRandom = Math.random();
        if (widthRandom < minSize || widthRandom > maxSize) {
            widthRandom = maxSize;
        }
        //红包的宽度
        width = (int) (originalBitmap.getWidth() * widthRandom);
        //红包的高度
        height = width * originalBitmap.getHeight() / originalBitmap.getWidth();
        int mWidth = (viewWidth == 0) ? context.getResources().getDisplayMetrics().widthPixels : viewWidth;
        //生成红包bitmap
//        bitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
//        originalBitmap.recycle();
        this.bitmap = originalBitmap;

        Random random = new Random();
        //红包起始位置x:[0,mWidth-width]
        int rx = random.nextInt(mWidth) - width;
        x = rx <= 0 ? 0 : rx;
        //红包起始位置y
        y = -height;
        //初始化该红包的下落速度
        this.speed = speed + (float) Math.random() * 1000;
        //初始化该红包的初始旋转角度
        rotation = (float) Math.random() * 180 - 90;
        //初始化该红包的旋转速度
        rotationSpeed = (float) Math.random() * 90 - 45;
    }

    public RedPacketBean() {

    }


    /**
     * 判断当前点是否包含在区域内
     */
    public boolean isContains(float x, float y) {
        //稍微扩大下点击的区域
        return this.x - 50 < x && this.x + 50 + width > x
                && this.y - 50 < y && this.y + 50 + height > y;
    }

    /**
     * 随机 是否为中奖红包
     */
    public boolean isRealRedPacket(int probability) {
        int x = (int) (Math.random() * 100);
        //如果[1,100]随机出的数字 为中奖红包
        if (x <= probability) {
            return true;
        }
        return false;
    }

    /**
     * 回收图片
     */
    public void recycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public boolean isClickable() {
        return mType == TYPE_PACKET_BT || mType == TYPE_PACKET_GOLD;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        if (mType != type) {
            mTypeIndex = 0;
        }
        mType = type;
    }

    public void setTypeIndex(int typeIndex) {
        mTypeIndex = typeIndex;
    }

    public int addTypeIndex(int addIndex) {
        mTypeIndex += addIndex;
        return mTypeIndex;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "RedPacketBean{" +
                "x=" + x +
                ", y=" + y +
                ", rotation=" + rotation +
                ", speed=" + speed +
                ", rotationSpeed=" + rotationSpeed +
                ", width=" + width +
                ", height=" + height +
                ", bitmap=" + bitmap +
                ", contract='" + contract + '\'' +
                ", symbol='" + symbol + '\'' +
                ", money='" + money + '\'' +
                ", mType=" + mType +
                ", mTypeIndex=" + mTypeIndex +
                ", num=" + num +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeFloat(this.x);
//        dest.writeFloat(this.y);
        dest.writeFloat(this.rotation);
        dest.writeFloat(this.speed);
        dest.writeFloat(this.rotationSpeed);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
//        dest.writeParcelable(this.bitmap, flags);
        dest.writeString(this.contract);
        dest.writeString(this.symbol);
        dest.writeString(this.money);
        dest.writeInt(this.mType);
        dest.writeInt(this.mTypeIndex);
        dest.writeInt(this.num);
    }

    protected RedPacketBean(Parcel in) {
//        this.x = in.readFloat();
//        this.y = in.readFloat();
        this.rotation = in.readFloat();
        this.speed = in.readFloat();
        this.rotationSpeed = in.readFloat();
        this.width = in.readInt();
        this.height = in.readInt();
//        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.contract = in.readString();
        this.symbol = in.readString();
        this.money = in.readString();
        this.mType = in.readInt();
        this.mTypeIndex = in.readInt();
        this.num = in.readInt();
    }

    public static final Parcelable.Creator<RedPacketBean> CREATOR = new Parcelable.Creator<RedPacketBean>() {
        @Override
        public RedPacketBean createFromParcel(Parcel source) {
            return new RedPacketBean(source);
        }

        @Override
        public RedPacketBean[] newArray(int size) {
            return new RedPacketBean[size];
        }
    };
}
