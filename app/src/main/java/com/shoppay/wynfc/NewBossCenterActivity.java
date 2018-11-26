package com.shoppay.wynfc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.bean.NewBoss;
import com.shoppay.wynfc.bean.Shoper;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.DateUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.PreferenceHelper;
import com.shoppay.wynfc.tools.ShoperChoseDialog;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class NewBossCenterActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left;
    private TextView tv_title, tv_starttime,tv_endtime,tv_newvip,tv_give,tv_xjrecharge,tv_linerecharge, tv_czall,tv_yuexf,tv_linexf,tv_xjxf,tv_jifendk, tv_xfall,tv_allvip,tv_allxf,tv_allrecharge;
    private Context ac;
    private Dialog dialog;
    private TextView tv_shop;
    private RelativeLayout rl_shop;
    private List<Shoper> list;
    private Shoper shoper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bosscenter);
        ac = MyApplication.context;
        dialog = DialogUtil.loadingDialog(NewBossCenterActivity.this, 1);
        ActivityStack.create().addActivity(NewBossCenterActivity.this);
        initView();
        vipDengjiList("no");
        obtainBoss();

    }

    private void vipDengjiList(final String type) {

        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        params.put("UserAcount", susername);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetShopAuthority", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxLoginS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        String data=jso.getString("data");
                        Gson gson=new Gson();
                        Type listType = new TypeToken<List<Shoper>>(){}.getType();
                        list = gson.fromJson(data, listType);
                        if(type.equals("no")){

                        }else{
                            ShoperChoseDialog.shoperChoseDialog(NewBossCenterActivity.this, list, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    shoper=(Shoper) response;
                                    tv_shop.setText(shoper.ShopName);
                                    obtainBoss();
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    }else{
                        if(type.equals("no")){

                        }else {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    if(type.equals("no")){

                    }else {
                        Toast.makeText(ac, "获取商铺列表失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if(type.equals("no")){

                }else {
                    Toast.makeText(ac, "获取商铺列表失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initView() {
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_shop = (RelativeLayout) findViewById(R.id.rl_shop);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_shop = (TextView) findViewById(R.id.tv_shop);
        tv_starttime = (TextView) findViewById(R.id.boss_tv_starttime);
        tv_endtime= (TextView) findViewById(R.id.boss_tv_endtime);
        tv_czall = (TextView) findViewById(R.id.boss_tv_allrecharge);
        tv_xfall = (TextView) findViewById(R.id.boss_tv_allxf);
        tv_newvip = (TextView) findViewById(R.id.boss_tv_newvipnum);
        tv_give = (TextView) findViewById(R.id.boss_tv_givemoney);
        tv_xjrecharge = (TextView) findViewById(R.id.boss_tv_xjrecharge);
        tv_linerecharge = (TextView) findViewById(R.id.boss_tv_linerecharge);
        tv_yuexf = (TextView) findViewById(R.id.boss_tv_yuexf);
        tv_linexf = (TextView) findViewById(R.id.boss_tv_linexf);
        tv_xjxf = (TextView) findViewById(R.id.boss_tv_xjxf);
        tv_jifendk = (TextView) findViewById(R.id.boss_tv_jfdk);
        tv_allvip = (TextView) findViewById(R.id.boss_tv_ljvip);
        tv_allrecharge = (TextView) findViewById(R.id.boss_tv_ljrecharge);
        tv_allxf = (TextView) findViewById(R.id.boss_tv_ljxf);
        tv_title.setText("老板中心");
        tv_starttime.setText(getStringDate());
        tv_endtime.setText(getStringDate());
        rl_left.setOnClickListener(this);
        tv_starttime.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
        rl_shop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_shop:
                if(null==list||list.size()==0){
                    vipDengjiList("yes");
                }else {
                    ShoperChoseDialog.shoperChoseDialog(NewBossCenterActivity.this, list, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            shoper=(Shoper) response;
                            tv_shop.setText(shoper.ShopName);
                            obtainBoss();
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                }
                break;
            case R.id.rl_left:
                finish();
                break;
            case R.id.boss_tv_starttime:
                DialogUtil.dateChoseDialog(NewBossCenterActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        String data= DateUtils.timeTodata((String) response);
                        String cru=DateUtils.timeTodata(DateUtils.getCurrentTime_Today());
                        Log.d("xxTime",data+";"+cru+";"+DateUtils.getCurrentTime_Today()+";"+(String) response);
                        if(Double.parseDouble(data)<=Double.parseDouble(cru)){
                            tv_starttime.setText((String) response);
                            obtainBoss();
                        }else{
                            Toast.makeText(ac,"开始时间应小于当前时间",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_starttime.setText((String) msg);
                        obtainBoss();
                    }
                });
                break;
            case R.id.boss_tv_endtime:
                DialogUtil.dateChoseDialog(NewBossCenterActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {

                        String data=DateUtils.timeTodata((String) response);
                        String cru=DateUtils.timeTodata(tv_starttime.getText().toString());
                        Log.d("xxTime",data+";"+cru+";"+DateUtils.getCurrentTime_Today()+";"+(String) response);
                        if(Double.parseDouble(data)>Double.parseDouble(cru)){
                            tv_endtime.setText((String) response);
                            obtainBoss();
                        }else{
                            Toast.makeText(ac,"结束时间要大于起始时间",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_endtime.setText((String) msg);
                        obtainBoss();
                    }
                });
                break;
        }
    }

    private void obtainBoss() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        if(tv_shop.getText().toString().equals("选择店铺")) {
            map.put("shopid", "0");
        }else{
            map.put("shopid", shoper.ShopID);
        }
        map.put("startDate",tv_starttime.getText().toString());
        map.put("endDate",tv_endtime.getText().toString());
        Log.d("xx",map.toString());
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetBoosRpt", map, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxbossS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                      Gson gson=new Gson();
                      NewBoss newBoss=  gson.fromJson(jso.getString("data"), NewBoss.class);
                      tv_newvip.setText(newBoss.MemNumber);
                        tv_give.setText(newBoss.giveMoney);
                        tv_xjrecharge.setText(newBoss.cashRechargeMoney);
//                        tv_linerecharge.setText("在线充值"+Double.parseDouble(newBoss.bankRechargeMoney)+Double.parseDouble(newBoss.wxRechargeMoney)+Double.parseDouble(newBoss.zfbRechargeMoney)+"");
                        tv_linerecharge.setText(newBoss.wxRechargeMoney);
                       tv_czall.setText(Double.parseDouble(newBoss.cashRechargeMoney)+"");

                        tv_yuexf.setText(newBoss.orderCardMoney);
                        tv_linexf.setText(newBoss.orderWxMoney);
                        tv_xjxf.setText(newBoss.orderCashMoney);
                        tv_jifendk.setText(newBoss.orderPointMoney);
                        tv_xfall.setText(newBoss.orderMoney);


                        tv_allvip.setText(newBoss.AllMemNumber);
                        tv_allxf.setText(newBoss.AllorderMoney);
                        tv_allrecharge.setText(newBoss.AllRechargeMoney);
                    }else{

                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                        Toast.makeText(ac, "系统超时，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
                    Toast.makeText(ac, "系统超时，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
