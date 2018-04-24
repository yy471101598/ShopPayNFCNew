package com.shoppay.wynew.modle;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynew.tools.PreferenceHelper;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ImpWeixinPay implements InterfaceWeixinPay{

    @Override
    public void weixinPay(final Context context,String moeny,String orderNo,String pName,
                          final InterfaceMVC interf) {
        // TODO 自动生成的方法存根
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("money",moeny);
        params.put("orderNo",  PreferenceHelper.readString(context, "shoppay", "WxOrder","123"));
        params.put("pName",pName);
        Log.d("xxwinxin",params.toString());
        client.post( PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=ScanCodePay", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxweixinPayS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
//                        "data":{"imgcode":"weixin://wxpay/bizpayurl?pr=vQeTl2L"}}
                        interf.onResponse(0, jso.getJSONObject("data").getString("imgcode"));
                    }else{
                        interf.onErrorResponse(1, jso.getString("msg"));
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                try {
                    String result = new String(responseBody);
                    Log.d("xxCarPayE", result);
                    interf.onErrorResponse(2, result);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }
}
