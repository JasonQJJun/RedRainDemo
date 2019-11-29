package com.qjj.cn.myredraindemo.model;

/**
 * created by QinJiaJun
 * Email qinjiajun_1230@163.com
 * on 2019/10/25
 * Describe: 红包雨开红包
 */
public class GetRedPacketRainOpenResponse  {

    private int status;
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Status返回码
     *
     * 5001	本次下雨活动已结束
     * 5002	本次下雨活动还没有开始
     * 5003	你已超出最大可获得红包数
     * 5004	本次红包雨已被抢完
     */

    private ResultEntity data;

    public ResultEntity getData() {
        return data;
    }

    public void setData(ResultEntity data) {
        this.data = data;
    }

    public static class ResultEntity {
        /**
         * contract : 0x000000000
         * symbol : abc
         * money : 0.002646
         */

        private String contract;
        private String symbol;
        private String money;

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
    }
}
