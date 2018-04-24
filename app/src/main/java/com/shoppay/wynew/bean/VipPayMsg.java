package com.shoppay.wynew.bean;

/**
 * Created by songxiaotao on 2017/7/13.
 */

public class VipPayMsg {
    /**
     * 会员卡号
     * */
    public String vipCard;
    /**
     * 会员卡id
     * */
    public String vipId;
    /**
     * 会员姓名
     * */
    public String vipName;
    /**
     * 消费获取积分
     * */
    public String obtainJifen;
    /**
     * 消费金额
     * */
    public String xfMoney;
    /**
     * 折后金额
     * */
    public String zhMoney;
    /**
     * 使用积分支付
     * */
    public int isJifen;
    /**
     * 使用余额支付
     * */
    public int isYue;
    /**
     * 使用现金支付
     * */
    public int isMoney;
    /**
     * 积分抵扣金额
     * */
    public String jifenDkmoney;
    /**
     * 消费使用积分
     * */
    public String useJifen;
    /**
     * 消费余额
     * */
    public String   yueMoney;
    /**
     * 消费现金
     * */
    public String  xjMoney;
    /**
     * 卡内余额
     * */
    public String vipYue;
    /**
     * 卡内剩余积分
     * */
    public String vipSyJifen;
    /**
     * 节省金额
     * */
    public String jieshengMoney;
    /**
     * 商品种类个数
     * */
    public String dataLength;

    /**
     * 微信支付
     * */
    public int isWx;
    /**
     * 微信支付金额
     * */
    public String wxMoney;


}
