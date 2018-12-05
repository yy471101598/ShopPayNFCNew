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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.adapter.NumRechargeAdapter;
import com.shoppay.wynfc.bean.NumServece;
import com.shoppay.wynfc.bean.Shop;
import com.shoppay.wynfc.bean.ShopCar;
import com.shoppay.wynfc.bean.VipInfo;
import com.shoppay.wynfc.bean.VipPayMsg;
import com.shoppay.wynfc.card.ReadCardOpt;
import com.shoppay.wynfc.db.DBAdapter;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.modle.ImpWeixinPay;
import com.shoppay.wynfc.modle.InterfaceMVC;
import com.shoppay.wynfc.tools.BluetoothUtil;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.ESCUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.MergeLinearArraysUtil;
import com.shoppay.wynfc.tools.PreferenceHelper;
import com.shoppay.wynfc.tools.StringUtil;
import com.shoppay.wynfc.tools.WeixinPayDialog;
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
 *
 * @author qdwang
 *
 */
public class NumRechargeActivity extends Activity implements
        OnItemClickListener ,View.OnClickListener{

    private ListView listView;
    private RelativeLayout rl_jiesuan,rl_left;
    private TextView tv_num,tv_money,tv_jifen,tv_title,tv_vipname,tv_vipjifen,tv_vipyue;
    private EditText et_card;
    private Dialog dialog;
    private Context ac;
    private DBAdapter dbAdapter;
    private String editString;
    private NumRechargeAdapter adapter;
    private List<NumServece> list;
    private double num=0,money=0,jifen=0,xfmoney=0;
    private ShopChangeReceiver shopchangeReceiver;
    private boolean isSuccess=false;
    private Dialog jiesuanDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    if(info.MemState==0){
                    tv_vipname.setText(info.MemName);
                    tv_vipjifen.setText(info.MemPoint);
                    tv_vipyue.setText(info.MemMoney);
                        PreferenceHelper.write(ac, "shoppay", "memid", info.MemID+"");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", info.MemLevelID + "");
                        PreferenceHelper.write(ac, "shoppay", "MemMoney", info.MemMoney + "");
                        PreferenceHelper.write(ac, "shoppay", "jifenall",  info.MemPoint);
                    PreferenceHelper.write(ac, "shoppay", "vipcar", et_card.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "vipname", tv_vipname.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "isSuccess", true);
                    PreferenceHelper.write(ac, "shoppay", "isInput", true);
                    isSuccess=true;
            }else  if(info.MemState==1) {
                Toast.makeText(ac,"此卡已锁定",Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已锁定");
                        tv_vipname.setText("");
                        tv_vipjifen.setText("");
                        tv_vipyue.setText("");
                        PreferenceHelper.write(ac, "shoppay", "isSuccess", false);
                        if(et_card.getText().toString().equals("")||et_card.getText().toString()==null){
                            PreferenceHelper.write(ac, "shoppay", "isInput", false);
                        }else{
                            PreferenceHelper.write(ac, "shoppay", "isInput", true);
                        }
                        isSuccess=false;
            }else{
                Toast.makeText(ac,"此卡已挂失",Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已挂失");
                        tv_vipname.setText("");
                        tv_vipjifen.setText("");
                        tv_vipyue.setText("");
                        PreferenceHelper.write(ac, "shoppay", "isSuccess", false);
                        if(et_card.getText().toString().equals("")||et_card.getText().toString()==null){
                            PreferenceHelper.write(ac, "shoppay", "isInput", false);
                        }else{
                            PreferenceHelper.write(ac, "shoppay", "isInput", true);
                        }
                        isSuccess=false;
            }
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipjifen.setText("");
                    tv_vipyue.setText("");
                    PreferenceHelper.write(ac, "shoppay", "isSuccess", false);
                    if(et_card.getText().toString().equals("")||et_card.getText().toString()==null){
                        PreferenceHelper.write(ac, "shoppay", "isInput", false);
                    }else{
                        PreferenceHelper.write(ac, "shoppay", "isInput", true);
                    }
                    isSuccess=false;
                    break;
                case 5:
                    String card= msg.obj.toString();
                    Log.d("xxxx",card);
                    et_card.setText(card);
                    break;
            }
        }
    };
    private Intent intent;
    private MsgReceiver msgReceiver;
    private Dialog weixinDialog;
    private VipPayMsg vipPayMsg;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numrecharge);
        ac=MyApplication.context;
       dialog= DialogUtil.loadingDialog(NumRechargeActivity.this,1);
        dbAdapter=DBAdapter.getInstance(ac);
        PreferenceHelper.write(ac, "shoppay", "memid", "");
        PreferenceHelper.write(ac, "shoppay", "vipcar","无");
        PreferenceHelper.write(ac, "shoppay", "isInput", false);
        PreferenceHelper.write(MyApplication.context,"shoppay","viptoast","未查询到会员");
        dbAdapter.deleteShopCar();
        initView();
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent =
                PendingIntent.getActivity(this, 0, nfcIntent, 0);
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        obtainServeceShop();

        // 注册广播
        shopchangeReceiver = new ShopChangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.numberchange");
        registerReceiver(shopchangeReceiver, iiiff);


        PreferenceHelper.write(getApplicationContext(), "PayOk", "time", "false");
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        et_card.addTextChangedListener(new TextWatcher() {
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
    }

    private void obtainServeceShop() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Key","");
        params.put("Size",50);
        params.put("index",1);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetServiceGoodsList", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxNumshopS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        JSONObject js=jso.getJSONObject("data");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Shop>>(){}.getType();
                        List<Shop> list = gson.fromJson(js.getString("list"), listType);
                        adapter=new NumRechargeAdapter(NumRechargeActivity.this,list);
                        listView.setAdapter(adapter);
                    }else{
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      new ReadCardOpt(et_card);
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
        if (mAdapter == null) {
            if (!mAdapter.isEnabled()) {
                Toast.makeText(ac, "该设备不支持NFC功能", Toast.LENGTH_SHORT).show();
            }

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
        if (tagFromIntent!=null) {
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


    public static  String ByteArrayToHex(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9","A","B","C","D","E","F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        long x = Long.parseLong(out,  16);
//        int x = Integer.parseInt(out,16);
        out = String.format("%010d",x);
        return out;
    }

    @Override
    protected void onStop() {
        //终止检卡
        try
        {
            new ReadCardOpt().overReadCard();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        super.onStop();
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
        params.put("memCard",editString);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetMem", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    LogUtils.d("xxVipinfoS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<VipInfo>>(){}.getType();
                        List<VipInfo> list = gson.fromJson(jso.getString("data"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    }else{
                        PreferenceHelper.write(ac, "shoppay", "memid", "");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
//                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    PreferenceHelper.write(ac, "shoppay", "memid", "");
                    PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        // TODO Auto-generated method stub
        rl_left= (RelativeLayout) findViewById(R.id.rl_left);
        rl_jiesuan= (RelativeLayout) findViewById(R.id.numrecharge_rl_jiesan);

        tv_jifen= (TextView) findViewById(R.id.numrecharge_tv_jifen);
        tv_vipjifen= (TextView) findViewById(R.id.numrecharge_tv_vipjifen);
        tv_vipyue= (TextView) findViewById(R.id.numrecharge_tv_vipyue);
        tv_vipname= (TextView) findViewById(R.id.numrecharge_tv_vipname);
        tv_num= (TextView) findViewById(R.id.numrecharge_tv_num);
        tv_money= (TextView) findViewById(R.id.numrecharge_tv_money);
        tv_title= (TextView) findViewById(R.id.tv_title);
         tv_title.setText("会员充次");
        et_card= (EditText) findViewById(R.id.numrecharge_et_card);
        listView = (ListView) findViewById(R.id.listview);

        rl_left.setOnClickListener(this);
        rl_jiesuan.setOnClickListener(this);

        listView.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_left:
                finish();
                break;
            case R.id.numrecharge_rl_jiesan:
                if(tv_num.getText().toString().equals("0")){
                    Toast.makeText(getApplicationContext(), "请选择商品",
                            Toast.LENGTH_SHORT).show();
                }else{
                    if (CommonUtils.checkNet(getApplicationContext())) {
                          if(et_card.getText().toString().equals("")||et_card.getText().toString()==null){
                              Toast.makeText(ac,"请输入会员卡号",Toast.LENGTH_SHORT).show();
                          }else{

//
                       jiesuanDialog=    DialogUtil.jiesuanDialog("num",NumRechargeActivity.this, 1,true,money,xfmoney,jifen, new InterfaceBack() {
                               @Override
                               public void onResponse(Object response) {

                                   finish();
                               }

                               @Override
                               public void onErrorResponse(Object msg) {

                                   vipPayMsg=(VipPayMsg) msg;
                                   PreferenceHelper.write(ac, "shoppay", "WxOrder", System.currentTimeMillis()+  PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                                       ImpWeixinPay weixinPay =new ImpWeixinPay();
                                       weixinPay.weixinPay(ac,tv_money.getText().toString(),PreferenceHelper.readString(getApplicationContext(), "shoppay", "OrderAccount", ""),"会员充次", new InterfaceMVC() {
                                           @Override
                                           public void onResponse(int code, Object response) {
                                            weixinDialog=   WeixinPayDialog.weixinPayDialog(NumRechargeActivity.this,1,(String)response,tv_money.getText().toString());
                                               intent = new Intent(getApplicationContext(),
                                                       PayResultPollService.class);
                                               startService(intent);
                                           }

                                           @Override
                                           public void onErrorResponse(int code, Object msg) {

                                           }
                                       });


                               }
                           });
                          }
                    }
                        else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    private class  ShopChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("xx","ShopChangeReceiver");
            List<ShopCar> listss= dbAdapter.getListShopCar(PreferenceHelper.readString(context,"shoppay","account","123"));
            num=0;
            money=0;
            jifen=0;
            xfmoney=0;
            for(ShopCar shopCar:listss){
                if(shopCar.count==0){

                }else{
                    num=num+shopCar.count;
                    money=money+Double.parseDouble(shopCar.discountmoney);
                    jifen=jifen+shopCar.point;
                    xfmoney=xfmoney+shopCar.count*Double.parseDouble(shopCar.price);
                }
            }
            tv_jifen.setText((int)jifen+"");
            tv_num.setText((int)num+"");
            tv_money.setText(StringUtil.twoNum(money+""));

        }
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
                    jiesuanDialog.dismiss();
                    weixinDialog.dismiss();
                    jiesuan(ac,vipPayMsg);
                } else {
                    String msg = intent.getStringExtra("msg");
                    Toast.makeText(ac,msg,Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    private void jiesuan(final Context context, final VipPayMsg msg) {
dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        final DBAdapter dbAdapter = DBAdapter.getInstance(context);
        List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
        List<ShopCar> shoplist = new ArrayList<>();
        int datalength = 0;
        for (ShopCar numShop : list) {
            if (numShop.count == 0) {
            } else {
                datalength = datalength + 1;
                shoplist.add(numShop);
            }
        }
        RequestParams params = new RequestParams();
        params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        params.put("DiscountMoney", msg.zhMoney);
        params.put("Money", msg.xfMoney);
        params.put("Point",msg.obtainJifen);
        params.put("DataCount", datalength + "");
        params.put("bolIsPoint", msg.isJifen);
        params.put("PointPayMoney", msg.jifenDkmoney);
        params.put("UsePoint", msg.useJifen);
        params.put("bolIsCard", msg.isYue);//1：真 0：假
        params.put("CardPayMoney", msg.yueMoney);
        params.put("bolIsCash", msg.isMoney);//1：真 0：假
        params.put("CashPayMoney", msg.xjMoney);
        params.put("bolIsWeiXin",msg.isWx);//1：真 0：假
        params.put("WeiXinPayMoney",msg.wxMoney);

        for (int i = 0; i < shoplist.size(); i++) {
            params.put("Data[" + i + "][ExpPoint]", shoplist.get(i).point);
            params.put("Data[" + i + "][ExpMoney]", shoplist.get(i).discountmoney);
            params.put("Data[" + i + "][GoodsID]", shoplist.get(i).goodsid);
            params.put("Data[" + i + "][ExpNum]", shoplist.get(i).count);
        }
        Log.d("xx", params.toString());

        client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemRechargeCount", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Toast.makeText(context, "结算成功",
                                Toast.LENGTH_SHORT).show();
                        PreferenceHelper.write(context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
//						printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
                        if (PreferenceHelper.readBoolean(context, "shoppay", "IsPrint", false)) {
                            BluetoothUtil.connectBlueTooth(context);
                            BluetoothUtil.sendData(printReceipt_BlueTooth("num", context, msg), PreferenceHelper.readInt(context, "shoppay", "RechargeCountPrintNumber", 1));
                        }
                        dbAdapter.deleteShopCar();
                        finish();
                    } else {
                        Toast.makeText(context, jso.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.show();
                Toast.makeText(context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
        int anHour =2 * 1000; // 这是一小时的毫秒数 60 * 60 * 1000
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        //注销广播
        unregisterReceiver(msgReceiver);
        unregisterReceiver(shopchangeReceiver);
    }


    public static byte[] printReceipt_BlueTooth(final String type,Context context,VipPayMsg msg)
    {
        String danhao = "消费单号:" +PreferenceHelper.readString(context, "shoppay","OrderAccount","");
        String huiyuankahao = "会员卡号:" +PreferenceHelper.readString(context, "shoppay", "vipcar","无");
        String huiyuanming = "会员名称:" +PreferenceHelper.readString(context, "shoppay", "vipname", "散客");
        String xfmoney = "消费金额:" +msg.xfMoney;
        String obtainjifen = "获得积分:" +msg.obtainJifen;
        Log.d("xx",PreferenceHelper.readString(context, "shoppay", "vipname", "散客"));
        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(context,"shoppay","PrintTitle","").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(context,"shoppay","PrintFootNote","").getBytes("gb2312");
            byte[] tickname;
            if(type.equals("num")){
                tickname = "服务充次小票".getBytes("gb2312");
            }else {
                tickname = "商品消费小票".getBytes("gb2312");
            }
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xfmmm = xfmoney.getBytes("gb2312");
            byte[] objfff = (obtainjifen+"").getBytes("gb2312");
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
                    left, vipname,nextLine,left,xfmmm,nextLine,left,objfff,nextLine,xiahuaxian};

            byte[] headerBytes =ESCUtil. byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            String shopdetai="服务名称    "+"单价    "+"次数    "+"合计";
            //商品头
            byte[] sh=shopdetai.getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, sh};
            bytesList.add(ESCUtil.byteMerger(mticket1));
            //商品明细
            DBAdapter dbAdapter=DBAdapter.getInstance(context);
            List<ShopCar> list= dbAdapter.getListShopCar(PreferenceHelper.readString(context,"shoppay","account","123"));
            for(ShopCar numShop:list){
                if(numShop.count==0){
                }else{
                    StringBuffer sb=new StringBuffer();

                    String sn=numShop.shopname;
                    Log.d("xxleng",sb.length()+"");
                    int sbl=sn.length();
                    if(sbl<6){
                        sb.append(sn);
                        for(int i=0;i<7-sbl;i++) {
                            sb.insert(sb.length(), " ");
                        }
                    }else{
                        sn=sn.substring(0,6);
                        sb.append(sn);
                        sb.append(" ");
                    }
                    Log.d("xxleng",sb.length()+"");
                    byte[] a=(sb.toString()+"" +CommonUtils.lasttwo(Double.parseDouble(numShop.price))+"      "+numShop.count+"      "+StringUtil.twoNum(numShop.discountmoney) ).getBytes("gb2312");
                    byte[][] mticket = {nextLine, left, a};
                    bytesList.add(ESCUtil.byteMerger(mticket));
                }
            }
            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));
            if(msg.isWx==1){
                byte[] weixin=( "微信支付:" +StringUtil.twoNum(msg.wxMoney)).getBytes("gb2312");
                byte[][] weixins= {nextLine,left,weixin};
                bytesList.add(ESCUtil.byteMerger(weixins));
            }else {
                byte[] yfmoney = ("应付金额:" + StringUtil.twoNum(msg.zhMoney)).getBytes("gb2312");
                byte[] jinshengmoney = ("节省金额:" + StringUtil.twoNum(msg.jieshengMoney)).getBytes("gb2312");

                byte[][] mticketsn = {nextLine, left, yfmoney, nextLine, left, jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
            }
            if(msg.isMoney==1){
                byte[] moneys=( "现金支付:" +StringUtil.twoNum(msg.xjMoney)).getBytes("gb2312");
                byte[][] mticketsm= {nextLine,left,moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            if(msg.isYue==1){
                byte[] yue=( "余额支付:" +StringUtil.twoNum(msg.yueMoney)).getBytes("gb2312");
                byte[][] mticketyue= {nextLine,left,yue};
                bytesList.add(ESCUtil.byteMerger(mticketyue));
            }
            if(msg.isJifen==1){
                byte[] jifen=( "积分抵扣:" +msg.jifenDkmoney).getBytes("gb2312");
                byte[][] mticketjin= {nextLine,left,jifen};
                bytesList.add(ESCUtil.byteMerger(mticketjin));
            }
            byte[] syjinfen=( "剩余积分:" +(int)Double.parseDouble(msg.vipSyJifen)).getBytes("gb2312");
            byte[][] mticketsyjf= {nextLine,left,syjinfen};
            bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            if(msg.isYue==1){
//				double sy=CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney","")),Double.parseDouble(et_yuemoney.getText().toString()));
                byte[] shengyu=( "卡内余额:"+StringUtil.twoNum(msg.vipYue)).getBytes("gb2312");
                byte[][] mticketsy= {nextLine,left,shengyu};
                bytesList.add(ESCUtil.byteMerger(mticketsy));
            }

            byte[] ha=( "操作人员:"+PreferenceHelper.readString(context
                    ,"shoppay","UserName","")).trim().getBytes("gb2312");
            byte[] time=( "消费时间:"+getStringDate()).trim().getBytes("gb2312");
            byte[] qianming=( "客户签名:").getBytes("gb2312");
            Log.d("xx",PreferenceHelper.readString(context
                    ,"shoppay","UserName",""));
            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            Log.d("xxprint",new String(MergeLinearArraysUtil.mergeLinearArrays(bytesList)));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);


            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
//			Log.d("xx","异常");
        }
        return null;
    }
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
