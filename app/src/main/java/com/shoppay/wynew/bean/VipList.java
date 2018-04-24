package com.shoppay.wynew.bean;

import java.io.Serializable;

/**
 * Created by songxiaotao on 2017/7/20.
 */

public class VipList implements Serializable{
 public String   MemPoint;//0, //账户积分
    public String  MemMoney;//": 0, //账户余额

    public String  MemID;//1012, //会员ID
    public String    MemCard;//": "1001", //会员卡号
    public String    MemName;//": "李强", //会员姓名
    public String     MemMobile;//": "15208304101", //会员电话
    public int       MemLevelID;//": 0, //会员等级ID
    public String        LevelName;//": "VIP会员", //会员等级名称
    public String        MemPhoto;//": "", //会员头像图片地址  为空没有头像
    public int        MemSex;//": 0, // 0女，1男
    public String        MemBirthday;//": "", //生日
    public int       MemRecommendID;//": 0, //推荐人ID  为0无推荐人
    public String       MemRecommendCard;//": "", //推荐人卡号  无推荐人为空
    public String        MemRecommendName;//": "", //推荐人姓名 无推荐人为空
    public String        MemPastTime;//": "2017-07-31", //会员过期时间 为空永久有效
    public int         MemState;//": 0 //会员状态  0正常，1锁定，2挂失

}
