package com.shoppay.wynfc.bean;

/**
 * Created by songxiaotao on 2017/7/4.
 */

public class ShopCar {
    public String goodsid;//商品id
    public int count;//商品数量
    public String price;  //商品单价
    public String pointPercent;//获得积分比例
    public String discount;//折扣比例
    public String discountmoney;//消费折后金额  数量*单价的折后
    public int goodspoint;
    public double point;//消费获得的积分  根据折后金额
    public String goodsType;//商品类型
    public String account;//个人账号
    public String goodsclassid;//商品分类id
    public String shopname;
}
