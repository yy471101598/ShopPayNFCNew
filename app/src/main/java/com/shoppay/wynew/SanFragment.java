package com.shoppay.wynew;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynew.modle.ImpWeixinPay;
import com.shoppay.wynew.modle.InterfaceMVC;
import com.shoppay.wynew.tools.ActivityStack;
import com.shoppay.wynew.tools.BluetoothUtil;
import com.shoppay.wynew.tools.CommonUtils;
import com.shoppay.wynew.tools.DialogUtil;
import com.shoppay.wynew.tools.ESCUtil;
import com.shoppay.wynew.tools.LogUtils;
import com.shoppay.wynew.tools.MergeLinearArraysUtil;
import com.shoppay.wynew.tools.PreferenceHelper;
import com.shoppay.wynew.tools.StringUtil;
import com.shoppay.wynew.tools.WeixinPayDialog;
import com.shoppay.wynew.wxpay.AlarmReceiver;
import com.shoppay.wynew.wxpay.PayResultPollService;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/7/1.
 */

public class SanFragment extends Fragment implements View.OnClickListener {
    private EditText et_money;
    private RelativeLayout rl_jiesuan;
    private Dialog dialog;
    private RelativeLayout rl_money,rl_wx;
    private TextView tv_money,tv_wx,tv_jiesuan;
    private boolean isMoney=true,isWx=false;
    private MsgReceiver msgReceiver;
    private Intent intent;
    private Dialog weixinDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sanconsumption, null);
        initView(view);
        dialog= DialogUtil.loadingDialog(getActivity(),1);
        PreferenceHelper.write(getActivity(), "PayOk", "time", "false");
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        getActivity().registerReceiver(msgReceiver, intentFilter);
        return view;
    }

    private void initView(View view) {
        et_money = (EditText) view.findViewById(R.id.san_et_money);
        rl_jiesuan= (RelativeLayout) view.findViewById(R.id.san_rl_jiesuan);
        rl_money= (RelativeLayout) view.findViewById(R.id.rl_money);
        rl_wx= (RelativeLayout) view.findViewById(R.id.rl_wx);

        tv_money= (TextView) view.findViewById(R.id.tv_money);
        tv_wx= (TextView) view.findViewById(R.id.tv_wx);
        tv_jiesuan= (TextView) view.findViewById(R.id.tv_jiesuan);
        rl_jiesuan.setOnClickListener(this);
        rl_money.setOnClickListener(this);
        rl_wx.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.san_rl_jiesuan:
               if (et_money.getText().toString().equals("")
                       || et_money.getText().toString() == null) {
                   Toast.makeText(getActivity(), "请输入支付金额",
                           Toast.LENGTH_SHORT).show();
               }
               else{
                   if (CommonUtils.checkNet(getActivity())) {
                            if(isWx){
                                PreferenceHelper.write(getActivity(), "shoppay", "fasttype", "san");
                                PreferenceHelper.write(getActivity(), "shoppay", "WxOrder", System.currentTimeMillis()+  PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                                ImpWeixinPay weixinPay =new ImpWeixinPay();
                                weixinPay.weixinPay(getActivity(), et_money.getText().toString(),"","快速消费", new InterfaceMVC() {
                                    @Override
                                    public void onResponse(int code, Object response) {
                                        weixinDialog= WeixinPayDialog.weixinPayDialog(getActivity(),1,(String)response, et_money.getText().toString());
                                        intent = new Intent(getActivity(),
                                                PayResultPollService.class);
                                        getActivity().startService(intent);
                                    }

                                    @Override
                                    public void onErrorResponse(int code, Object msg) {

                                    }
                                });
                            }else {
                                jiesuan();
                            }
                   } else {
                       Toast.makeText(getActivity(), "请检查网络是否可用",
                               Toast.LENGTH_SHORT).show();
                   }
               }
               break;
           case R.id.rl_wx:
               if (isWx) {
                   Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
               } else {
                   rl_wx.setBackgroundColor(getResources().getColor(R.color.theme_red));
                   tv_wx.setTextColor(getResources().getColor(R.color.white));
                   tv_jiesuan.setText("扫码支付");
                   isWx = true;
                   resetPayType();
               }
               break;
           case R.id.rl_money:
               if (!isWx) {
                   Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
               } else {
                   if (isMoney) {
                       rl_money.setBackgroundColor(getResources().getColor(R.color.white));
                       tv_money.setTextColor(getResources().getColor(R.color.text_30));
                       isMoney = false;

                   } else {
                       rl_money.setBackgroundColor(getResources().getColor(R.color.theme_red));
                       tv_money.setTextColor(getResources().getColor(R.color.white));
                       isMoney = true;
                       rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
                       tv_wx.setTextColor(getResources().getColor(R.color.text_30));
                       isWx = false;
                       tv_jiesuan.setText("结算");
                   }
               }
               break;
       }
    }
    private void resetPayType() {
        isMoney=false;
        rl_money.setBackgroundColor(getResources().getColor(R.color.white));
        tv_money.setTextColor(getResources().getColor(R.color.text_30));
    }
    private void jiesuan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Ismember",0);//1为用户消费，0为散客消费
        params.put("memID",0);
        params.put("Point",0);
        params.put("Money",et_money.getText().toString());
        params.put("discountmoney",et_money.getText().toString());
            params.put("bolIsPoint",0);//1：真 0：假
            params.put("PointPayMoney",0);
            params.put("UsePoint",0);
            params.put("bolIsCard",0);//1：真 0：假
            params.put("CardPayMoney",0);
           if(isWx){
               params.put("bolIsWeiXin",1);//1：真 0：假
               params.put("WeiXinPayMoney",et_money.getText().toString());
               params.put("bolIsCash",0);//1：真 0：假
               params.put("CashPayMoney",0);
           }else{
               params.put("bolIsWeiXin",0);//1：真 0：假
               params.put("WeiXinPayMoney",0);
               params.put("bolIsCash",1);//1：真 0：假
               params.put("CashPayMoney",et_money.getText().toString());
           }
        Log.d("xxx",params.toString());
        client.post( PreferenceHelper.readString(getActivity(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppShopFastExpense", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                            Toast.makeText(getActivity(), "结算成功",
                                    Toast.LENGTH_SHORT).show();
//                        et_money.setText("");
                            PreferenceHelper.write(getActivity(), "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
                            if (PreferenceHelper.readBoolean(getActivity(), "shoppay", "IsPrint", false)) {
                                BluetoothUtil.connectBlueTooth(getActivity());
                                BluetoothUtil.sendData(printReceipt_BlueTooth(), PreferenceHelper.readInt(getActivity(), "shoppay", "FastExpenesPrintNumber", 1));
                            }
                            ActivityStack.create().finishActivity(FastConsumptionActivity.class);
                    }else{
                        Toast.makeText(getActivity(), jso.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public byte[] printReceipt_BlueTooth()
    {
        String danhao = "消费单号:" + PreferenceHelper.readString(getActivity(), "shoppay","OrderAccount","");
        String huiyuankahao = "会员卡号: 无" ;
        String huiyuanming = "会员名称:" +"散客";

        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(getActivity(),"shoppay","PrintTitle","").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(getActivity(),"shoppay","PrintFootNote","").getBytes("gb2312");
            byte[] tickname = "快速消费小票".getBytes("gb2312");
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus = "网 507".getBytes("gb2312");
            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
            byte[] left = ESCUtil.alignLeft();
            boldOn = ESCUtil.boldOn();
            byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
            boldOff = ESCUtil.boldOff();
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
            next2Line = ESCUtil.nextLine(2);
            byte[] nextLine = ESCUtil.nextLine(1);
            nextLine = ESCUtil.nextLine(1);
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();
            byte[][] mytitle = {nextLine, center, boldOn, title, boldOff, next2Line, left, tickname, nextLine, left, ordernum, nextLine, left,
                    vipcardnum, nextLine,
                    left, vipname,nextLine,xiahuaxian};

            byte[] headerBytes =ESCUtil. byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            byte[] xfmoney =( "消费金额:" +StringUtil.twoNum(et_money.getText().toString())).getBytes("gb2312");
            byte[] hasjifen =( "获得积分:" +"0").getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, xfmoney,nextLine,left,hasjifen};
            bytesList.add(ESCUtil.byteMerger(mticket1));


            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));
            if(isWx){
                byte[] moneys = ("微信支付:" + StringUtil.twoNum(et_money.getText().toString())).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }else {

                byte[] yfmoney =( "应付金额:" + StringUtil.twoNum(et_money.getText().toString())).getBytes("gb2312");
                byte[] jinshengmoney =( "节省金额:" +"0.00").getBytes("gb2312");

                byte[][] mticketsn = {nextLine,left,yfmoney,nextLine,left,jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
                byte[] moneys = ("现金支付:" + StringUtil.twoNum(et_money.getText().toString())).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            byte[] ha=("操作人员："+ PreferenceHelper.readString(getActivity()
                    ,"shoppay","UserName","")).getBytes("gb2312");
            byte[] time=("消费时间："+ getStringDate()).getBytes("gb2312");
            byte[] qianming=("客户签名：").getBytes("gb2312");

            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }




    /**
     * 广播接收器
     *
     * @author len
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            String state = intent.getStringExtra("success");
            Log.d("MsgReceiver", "MsgReceiver" + state);
            String type = PreferenceHelper.readString(getActivity(), "shoppay", "fasttype", "vip");
            Log.d("xxxx",type);
            if (type.equals("san")) {
                if (state == null || state.equals("")) {

                } else {
                    if (state.equals("success")) {
                        //支付成功，跳转
                        weixinDialog.dismiss();
                        jiesuan();
                    } else {
                        String msg = intent.getStringExtra("msg");
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
//
                    }
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
        if (intent != null) {

            getActivity().stopService(intent);
        }

        //关闭闹钟机制启动service
        AlarmManager manager = (AlarmManager)getActivity(). getSystemService(Context.ALARM_SERVICE);
        int anHour =2 * 1000; // 这是一小时的毫秒数 60 * 60 * 1000
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
        manager.cancel(pi);
        //注销广播
        getActivity().unregisterReceiver(msgReceiver);
    }
}
