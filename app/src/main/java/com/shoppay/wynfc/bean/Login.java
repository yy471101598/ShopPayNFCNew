package com.shoppay.wynfc.bean;

/**
 * Created by songxiaotao on 2017/7/3.
 */

public class Login {

    /**
     * success : true
     * msg : 帐户校验成功！
     * code : null
     * data : null
     */

    private boolean success;
    private String msg;
    private Object code;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
