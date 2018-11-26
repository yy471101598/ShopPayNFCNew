package com.shoppay.wynfc.modle;

public interface InterfaceMVC {
    /**
     * 访问网络接口的成功失败
     *
     * @author Administrator
     */

    public void onResponse(int code, Object response);

    public void onErrorResponse(int code, Object msg);
}
