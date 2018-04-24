package com.shoppay.wynew.bean;

import java.util.List;

/**
 * Created by songxiaotao on 2017/7/5.
 */

public class VipInfoMsg {


    /**
     * success : true
     * msg : 查询数据成功！
     * code : null
     * data : [{"MemID":"1012","MemCard":"1001","MemName":"李强","MemLevelID":"4","MemShopID":"1","MemPoint":"2942"}]
     */

    private boolean success;
    private String msg;
    private Object code;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * MemID : 1012
         * MemCard : 1001
         * MemName : 李强
         * MemLevelID : 4
         * MemShopID : 1
         * MemPoint : 2942
         */

        private String MemID;
        private String MemCard;
        private String MemName;
        private String MemLevelID;
        private String MemShopID;
        private String MemPoint;

        public String getMemID() {
            return MemID;
        }

        public void setMemID(String MemID) {
            this.MemID = MemID;
        }

        public String getMemCard() {
            return MemCard;
        }

        public void setMemCard(String MemCard) {
            this.MemCard = MemCard;
        }

        public String getMemName() {
            return MemName;
        }

        public void setMemName(String MemName) {
            this.MemName = MemName;
        }

        public String getMemLevelID() {
            return MemLevelID;
        }

        public void setMemLevelID(String MemLevelID) {
            this.MemLevelID = MemLevelID;
        }

        public String getMemShopID() {
            return MemShopID;
        }

        public void setMemShopID(String MemShopID) {
            this.MemShopID = MemShopID;
        }

        public String getMemPoint() {
            return MemPoint;
        }

        public void setMemPoint(String MemPoint) {
            this.MemPoint = MemPoint;
        }
    }
}
