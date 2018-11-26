//package com.shoppay.wy;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.PersistentCookieStore;
//import com.loopj.android.http.RequestParams;
//import com.shoppay.wy.http.InterfaceBack;
//import com.shoppay.wy.tools.ActivityStack;
//import com.shoppay.wy.tools.DialogUtil;
//import com.shoppay.wy.tools.PreferenceHelper;
//
//
//import org.json.JSONObject;
//
//import cz.msebera.android.httpclient.Header;
//
///**
// * Created by songxiaotao on 2017/6/30.
// */
//
//public class BossCenterActivity extends Activity implements View.OnClickListener{
//    private TextView tv_title,tv_time,tv_czmoney,tv_xfmoney,tv_ccmoney,tv_txmoney,tv_xjmoney,tv_yuemoney,tv_yhq;
//    private Activity ac;
//    private RelativeLayout rl_left,rl_right;
//    private Dialog dialog;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bosscenter);
//        ac=this;
//        ActivityStack.create().addActivity(ac);
//        initView();
//        dialog=DialogUtil.loadingDialog(ac,1);
//        obtainRptTotal(1);
//    }
//
//   private void  obtainRptTotal(int type){
//           dialog.show();
//           AsyncHttpClient client = new AsyncHttpClient();
//           final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
//           client.setCookieStore(myCookieStore);
//           RequestParams params = new RequestParams();
//        params.put("type",type);
////        params.put("userPassword",et_pwd.getText().toString());
//           client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetWxRptTotal", params, new AsyncHttpResponseHandler()
//           {
//               @Override
//               public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
//               {
//                   dialog.dismiss();
//                   try {
//                       Log.d("xxbossS",new String(responseBody,"UTF-8"));
//                       JSONObject jsonObject=new JSONObject(new String(responseBody,"UTF-8"));
//                       if(jsonObject.getBoolean("success")){
//                          String data= jsonObject.getString("data");
//                           JSONObject jso=new JSONObject(data);
//                           Log.d("xx",jso.getString("memSRechargeMoney"));
//                           //充值总金额
//                           double RecharegeMoneySum = Double.parseDouble(jso.getString("memSRechargeMoney")) + Double.parseDouble(jso.getString("memFRechargeMoney")) + Double.parseDouble(jso.getString("expenseBankSumMoneys")) + Double.parseDouble(jso.getString("FRechargeWebMoney")) + Double.parseDouble(jso.getString("WXCZ")) + Double.parseDouble(jso.getString("FRechargeGiveMoney"));
//
//
////消费总金额
//                           double ExpeseMoneySum = Double.parseDouble(jso.getString("payCard")) + Double.parseDouble(jso.getString("expenseSumMoneys")) + Double.parseDouble(jso.getString("payBink")) + Double.parseDouble(jso.getString("payCoupon")) + Double.parseDouble(jso.getString("payWeiXin")) + Double.parseDouble(jso.getString("payPoint"));
//
//
////充次总金额
//                           double CountMoneySum = Double.parseDouble(jso.getString("countPayCard")) + Double.parseDouble(jso.getString("countSumMoneys")) + Double.parseDouble(jso.getString("countPayBink")) + Double.parseDouble(jso.getString("countpayCoupon")) + Double.parseDouble(jso.getString("countPayWeiXin")) + Double.parseDouble(jso.getString("countPayPoint"));
//
//                          double tixian= Double.parseDouble(jso.getString("AllDrawMoney")) ;//提现总金额
//
//                          double xianjin= Double.parseDouble(jso.getString("allMoney")) ;//现金总收入
//
////余额总收入
//                           double CardMoneySum = Double.parseDouble(jso.getString("payCard")) + Double.parseDouble(jso.getString("countPayCard")) + Double.parseDouble(jso.getString("StorageTimingPayCard"));
//
////优惠券抵用
//                           double CouponSum = Double.parseDouble(jso.getString("payCoupon")) + Double.parseDouble(jso.getString("countpayCoupon")) + Double.parseDouble(jso.getString("StorageTimingPayCoupon"));
//
//                           tv_czmoney.setText(RecharegeMoneySum+"");
//                           tv_xfmoney.setText(ExpeseMoneySum+"");
//                           tv_ccmoney.setText(CountMoneySum+"");
//                           tv_txmoney.setText(tixian+"");
//                           tv_xjmoney.setText(xianjin+"");
//                           tv_yuemoney.setText(CardMoneySum+"");
//                           tv_yhq.setText(CouponSum+"");
//                           tv_time.setText(jso.getString("timeRadion"));
//                       }else{
//                           Toast.makeText(ac, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                       }
//                   }catch (Exception e){
//                       Toast.makeText(ac,"获取报表失败",Toast.LENGTH_SHORT).show();
//                   }
//               }
//
//               @Override
//               public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
//               {
//                   dialog.dismiss();
//                   Log.d("xxbossE",new String(responseBody));
//                   Toast.makeText(ac,"获取报表失败",Toast.LENGTH_SHORT).show();
//               }
//           });
//       }
//
//    private void initView() {
//        rl_left= (RelativeLayout) findViewById(R.id.rl_left);
//        rl_right= (RelativeLayout) findViewById(R.id.rl_right);
//        tv_title= (TextView) findViewById(R.id.tv_title);
//        tv_time= (TextView) findViewById(R.id.boss_tv_time);
//        tv_czmoney= (TextView) findViewById(R.id.boss_tv_czmoney);
//        tv_xfmoney= (TextView) findViewById(R.id.boss_tv_xfmoney);
//        tv_ccmoney= (TextView) findViewById(R.id.boss_tv_ccmoney);
//        tv_txmoney= (TextView) findViewById(R.id.boss_tv_txmoney);
//        tv_xjmoney= (TextView) findViewById(R.id.boss_tv_xjmoney);
//        tv_yuemoney= (TextView) findViewById(R.id.boss_tv_yemoney);
//        tv_yhq= (TextView) findViewById(R.id.boss_tv_yhdyq);
//
//        tv_title.setText("老板中心");
//
//        rl_left.setOnClickListener(this);
//        rl_right.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//      switch (view.getId()){
//          case R.id.rl_left:
//              finish();
//              break;
//          case R.id.rl_right:
//              DialogUtil.timeChoseDialog(ac, 3, new InterfaceBack() {
//                  @Override
//                  public void onResponse(Object response) {
//                      Log.d("xx",(String) response);
//                      String type=(String) response;
////                      1今日，2昨日，3 7天，4本月，5 30天内
//                    if(type.equals("昨天")){
//                        obtainRptTotal(2);
//                    }else if(type.equals("7天")){
//                        obtainRptTotal(3);
//                    }else if(type.equals("本月")){
//                        obtainRptTotal(4);
//                    }else if(type.equals("30天")){
//                        obtainRptTotal(5);
//                    }
//                  }
//
//                  @Override
//                  public void onErrorResponse(Object msg) {
//
//                  }
//              });
//              break;
//      }
//    }
//}
