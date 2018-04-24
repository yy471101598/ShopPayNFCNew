package com.shoppay.wynew.bean;

import java.util.List;

/**
 * Created by songxiaotao on 2017/7/3.
 */

public class Yuming {

    /**
     * success : true
     * msg : 绑定成功
     * code :
     * data : [{"domain":"http://ls.zhiluovip.com"}]
     */

    private boolean success;
    private String msg;
    private String code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
         * domain : http://ls.zhiluovip.com
         */

        private String domain;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }
}
