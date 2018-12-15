package com.shoppay.wynfc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.adapter.RechargeAdapter;
import com.shoppay.wynfc.bean.VipInfo;
import com.shoppay.wynfc.bean.VipRecharge;
import com.shoppay.wynfc.card.ReadCardOpt;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.modle.ImpWeixinPay;
import com.shoppay.wynfc.modle.InterfaceMVC;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.BluetoothUtil;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.ESCUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.MergeLinearArraysUtil;
import com.shoppay.wynfc.tools.PreferenceHelper;
import com.shoppay.wynfc.tools.StringUtil;
import com.shoppay.wynfc.tools.WeixinPayDialog;
import com.shoppay.wynfc.view.MyGridViews;
import com.shoppay.wynfc.wxpay.AlarmReceiver;
import com.shoppay.wynfc.wxpay.PayResultPollService;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipRechargeActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_rechage, rl_money, rl_line;
    private EditText et_vipcard;
    private TextView tv_title, tv_money, tv_line, tv_vipname, tv_vipyue;
    private MyGridViews myGridViews;
    private Context ac;
    private String state = "现金";
    private String editString;
    private Dialog dialog;
    private RechargeAdapter adapter;
    private VipRecharge recharge;
    private boolean isSuccess = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    if (info.MemState == 0) {
                        tv_vipname.setText(info.MemName);
                        tv_vipyue.setText(info.MemMoney);
                        PreferenceHelper.write(ac, "shoppay", "memid", info.MemID);
                        PreferenceHelper.write(ac, "shoppay", "percent", info.ClassRechargePointRate);
                        PreferenceHelper.write(ac, "shoppay", "vipcar", et_vipcard.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "jifen", info.MemPoint);
                        isSuccess = true;
                    } else if (info.MemState == 1) {
                        Toast.makeText(ac, "此卡已锁定", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac, "shoppay", "viptoast", "此卡已锁定");
                        tv_vipname.setText("");
                        tv_vipyue.setText("");
                        isSuccess = false;
                    } else {
                        Toast.makeText(ac, "此卡已挂失", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac, "shoppay", "viptoast", "此卡已挂失");
                        tv_vipname.setText("");
                        tv_vipyue.setText("");
                        isSuccess = false;
                    }
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipyue.setText("");
                    isSuccess = false;
                    break;
                case 5:
                    String card = msg.obj.toString();
                    Log.d("xxxx", card);
                    et_vipcard.setText(card);
                    break;
            }
        }
    };
    private MsgReceiver msgReceiver;
    private Intent intent;
    private Dialog weixinDialog;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viprecharge);
        ac = MyApplication.context;
        dialog = DialogUtil.loadingDialog(VipRechargeActivity.this, 1);
        PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "未查询到会员");
        ActivityStack.create().addActivity(VipRechargeActivity.this);
        initView();
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent =
                PendingIntent.getActivity(this, 0, nfcIntent, 0);
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        obtainVipRecharge();
        et_vipcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法

                handler.postDelayed(delayRun, 800);
            }
        });
        myGridViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                recharge = (VipRecharge) adapterView.getItemAtPosition(i);
                if (et_vipcard.getText().toString().equals("")
                        || et_vipcard.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (isSuccess) {
                        if (CommonUtils.checkNet(getApplicationContext())) {
                            if (!state.equals("现金")) {
                                PreferenceHelper.write(MyApplication.context, "shoppay", "WxOrder", System.currentTimeMillis() + PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                                ImpWeixinPay weixinPay = new ImpWeixinPay();
                                weixinPay.weixinPay(ac, recharge.getRechargeMoney(), "", "会员充值", new InterfaceMVC() {
                                    @Override
                                    public void onResponse(int code, Object response) {
                                        weixinDialog = WeixinPayDialog.weixinPayDialog(VipRechargeActivity.this, 1, (String) response, recharge.getRechargeMoney());
                                        intent = new Intent(getApplicationContext(),
                                                PayResultPollService.class);
                                        startService(intent);
                                    }

                                    @Override
                                    public void onErrorResponse(int code, Object msg) {

                                    }
                                });
                            } else {
                                if (PreferenceHelper.readBoolean(MyApplication.context, "shoppay", "IsChkPwd", false)) {
                                    DialogUtil.pwdDialog("recharge", VipRechargeActivity.this, 1, new InterfaceBack() {
                                        @Override
                                        public void onResponse(Object response) {
                                            vipRecharge(recharge);
                                        }

                                        @Override
                                        public void onErrorResponse(Object msg) {

                                        }
                                    });
                                } else {
                                    vipRecharge(recharge);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyApplication.context, PreferenceHelper.readString(MyApplication.context, "shoppay", "viptoast", "未查询到会员"), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        PreferenceHelper.write(getApplicationContext(), "PayOk", "time", "false");
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            ontainVipInfo();
        }
    };

    private void ontainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memCard", editString);
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetMem", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxVipinfoS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<VipInfo>>() {
                        }.getType();
                        List<VipInfo> list = gson.fromJson(jso.getString("data"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    private void obtainVipRecharge() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetRechargeGive", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxVipRechargeS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        JSONObject js = jso.getJSONObject("data");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<VipRecharge>>() {
                        }.getType();
                        List<VipRecharge> list = gson.fromJson(js.getString("list"), listType);
                        adapter = new RechargeAdapter(ac, list);
                        myGridViews.setAdapter(adapter);
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "服务器异常，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "服务器异常，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_rechage = (RelativeLayout) findViewById(R.id.viprecharge_rl_recharge);
        rl_line = (RelativeLayout) findViewById(R.id.rl_line);
        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        et_vipcard = (EditText) findViewById(R.id.viprecharge_et_cardnum);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_line = (TextView) findViewById(R.id.tv_line);
        tv_vipname = (TextView) findViewById(R.id.viprecharge_et_name);
        tv_vipyue = (TextView) findViewById(R.id.viprecharge_et_yue);
        myGridViews = (MyGridViews) findViewById(R.id.gridview);

        tv_title.setText("会员充值");

        rl_left.setOnClickListener(this);
        rl_rechage.setOnClickListener(this);
        rl_money.setOnClickListener(this);
        rl_line.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_money:
                rl_money.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_line.setBackgroundColor(getResources().getColor(R.color.white));
                tv_money.setTextColor(getResources().getColor(R.color.white));
                tv_line.setTextColor(getResources().getColor(R.color.text_30));
                state = "现金";
                break;
            case R.id.rl_line:
//                Toast.makeText(ac,"暂未开通该功能",Toast.LENGTH_SHORT).show();
                rl_money.setBackgroundColor(getResources().getColor(R.color.white));
                rl_line.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_money.setTextColor(getResources().getColor(R.color.text_30));
                tv_line.setTextColor(getResources().getColor(R.color.white));
                state = "在线";
                break;

        }
    }

    private void vipRecharge(final VipRecharge recharge) {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("memID", PreferenceHelper.readString(ac, "shoppay", "memid", ""));
        map.put("money", recharge.getRechargeMoney());
        map.put("giveMoney", recharge.getGiveMoney());
        map.put("remark", "app充值");
        map.put("point", (int) CommonUtils.div(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(PreferenceHelper.readString(ac, "shoppay", "percent", "1")), 2));
//        2: 现金充值
//        6: 微信充值
        if (state.equals("现金")) {
            map.put("RechargeType", 2);
        } else {
            map.put("RechargeType", 6);
        }
        Log.d("xx", map.toString());
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemRecharge", map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxviprechargeS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Toast.makeText(ac, "充值成功", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
                        if (PreferenceHelper.readBoolean(ac, "shoppay", "IsPrint", false)) {
                            BluetoothUtil.connectBlueTooth(ac);
                            BluetoothUtil.sendData(printReceipt_BlueTooth(), PreferenceHelper.readInt(ac, "shoppay", "RechargePrintNumber", 1));
                        }
                        finish();

                    } else {

                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "会员充值失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "会员充值失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_vipcard);
        if (mAdapter == null) {
            Toast.makeText(ac, "该设备不支持NFC功能", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(ac, "请在系统设置中先启用NFC功能", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAdapter != null) {
            //隐式启动
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            //隐式启动
            mAdapter.disableForegroundDispatch(this);
        }
    }


    //获取系统隐式启动的
    @Override
    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent != null) {
            String CardId = ByteArrayToHex(tagFromIntent.getId());
            if (null != CardId) {
                Log.d("xxnfccard", Long.parseLong(CardId, 16) + "");
                Message msg = handler.obtainMessage();
                msg.what = 5;
                msg.obj = CardId;
                handler.sendMessage(msg);
            }
        }
    }


    public static String ByteArrayToHex(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        long x = Long.parseLong(out, 16);
//        int x = Integer.parseInt(out,16);
        out = String.format("%010d", x);
        return out;
    }

    @Override
    protected void onStop() {
        //终止检卡
        try {
            new ReadCardOpt().overReadCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onStop();
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
    }


    public byte[] printReceipt_BlueTooth() {
        String danhao = "充值单号:" + PreferenceHelper.readString(ac, "shoppay", "OrderAccount", "");
        String huiyuankahao = "会员卡号:" + et_vipcard.getText().toString();
        String huiyuanming = "会员名称:" + tv_vipname.getText().toString();

        try {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(ac, "shoppay", "PrintTitle", "").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(ac, "shoppay", "PrintFootNote", "").getBytes("gb2312");
            byte[] tickname = "会员充值小票".getBytes("gb2312");
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
                    left, vipname, nextLine, xiahuaxian};

            byte[] headerBytes = ESCUtil.byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            byte[] rechargemoney = ("充值金额:" + StringUtil.twoNum(recharge.getRechargeMoney())).getBytes("gb2312");
            byte[] givemoney = ("赠送金额:" + StringUtil.twoNum(recharge.getGiveMoney())).getBytes("gb2312");
            byte[] obtainjifen = ("获得积分:" + (int) CommonUtils.div(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(PreferenceHelper.readString(ac, "shoppay", "percent", "1")), 2)).getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, rechargemoney, nextLine, left, givemoney, nextLine, left, obtainjifen};
            bytesList.add(ESCUtil.byteMerger(mticket1));


            byte[][] mtickets = {nextLine, xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));

            if (state.equals("现金")) {
                byte[] yfmoney = ("应付金额:" + StringUtil.twoNum(recharge.getRechargeMoney())).getBytes("gb2312");
                byte[][] mticketsn = {nextLine, left, yfmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
                byte[] moneys = ("现金付款:" + StringUtil.twoNum(recharge.getRechargeMoney())).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            } else {
                byte[] moneys = ("微信支付:" + StringUtil.twoNum(recharge.getRechargeMoney())).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            double sy = Double.parseDouble(recharge.getRechargeMoney()) + Double.parseDouble(recharge.getGiveMoney()) + Double.parseDouble(tv_vipyue.getText().toString());
            byte[] shengyu = ("卡内余额:" + StringUtil.twoNum(sy + "")).getBytes("gb2312");
            byte[][] mticketsy = {nextLine, left, shengyu};
            bytesList.add(ESCUtil.byteMerger(mticketsy));
            double syjf = Double.parseDouble(PreferenceHelper.readString(ac, "shoppay", "jifen", "0")) + CommonUtils.div(Double.parseDouble(recharge.getRechargeMoney()), Double.parseDouble(PreferenceHelper.readString(ac, "shoppay", "percent", "1")), 2);
            byte[] syjinfen = ("剩余积分:" + (int) syjf).getBytes("gb2312");
            byte[][] mticketsyjf = {nextLine, left, syjinfen};
            bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            byte[][] mticketsxx = {nextLine, xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mticketsxx));

            byte[] ha = ("操作人员：" + PreferenceHelper.readString(ac
                    , "shoppay", "UserName", "")).getBytes("gb2312");
            byte[] time = ("充值时间：" + getStringDate()).getBytes("gb2312");
            byte[] qianming = ("客户签名：").getBytes("gb2312");

            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("xx", "UnsupportedEncodingException");
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
            if (state == null || state.equals("")) {

            } else {
                if (state.equals("success")) {
                    weixinDialog.dismiss();
                    vipRecharge(recharge);
                } else {
                    String msg = intent.getStringExtra("msg");
                    Toast.makeText(ac, msg, Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
        if (intent != null) {

            stopService(intent);
        }

        //关闭闹钟机制启动service
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 1000; // 这是一小时的毫秒数 60 * 60 * 1000
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        //注销广播
        unregisterReceiver(msgReceiver);
    }
}
