package com.shoppay.wynfc.bean;

/**
 * Created by songxiaotao on 2017/7/4.
 */

public class Shop {
 public String   GoodsID	;//	商品ID
    public String   GoodsClassID	;//	商品分类ID
    public String   GoodsCode;//		商品编码
    public String    Name	;//	商品名称
    public String   NameCode	;//	商品简码
    public String  Unit	;//	计量单位
    public String  GoodsNumber;//		库存
    public String  SalePercet		;//特价折扣（1不打折、0按会员折扣、其他数值时和会员折扣对比，按最低的折扣）
    public String  GoodsSaleNumber;//
    public String   Price	;//	零售单价
    public String CommissionType	;//	商品提成类型
    public String  CommissionNumber	;//	提成比例（实际计算员工提成时，还会根据系统的设置的提成类型来判断根据什么方式提成）
    public String  Point		;//积分数量
    public String MinPercent	;//	最低折扣
    public String GoodsType	;//	商品类型
    public String GoodsBidPrice	;//	参考进价
    public String  GoodsRemark	;//	备注
    public String  GoodsPicture	;//	商品图片路径
    public String  GoodsCreateTime	;//	创建时间
    public String  CreateShopID		;//创建店铺

}
