package com.shoppay.wynfc.http;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by songxiaotao on 2017/7/6.
 */

public  class MyRequestParams extends RequestParams {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ArrayList<BasicNameValuePair> list =null;
    public MyRequestParams() {
        list =new ArrayList<BasicNameValuePair>();
    }

    @Override
    public void put(String key, String value) {
        list.add(new BasicNameValuePair(key, value));
    }
    @Override
    public void put(String key, int value) {
        list.add(new BasicNameValuePair(key, String.valueOf(value)));
    }
    @Override
    public void put(String key, long value) {
        list.add(new BasicNameValuePair(key, String.valueOf(value)));
    }
    @Override
    protected ArrayList<BasicNameValuePair> getParamsList() {
        return list ;
    }
}