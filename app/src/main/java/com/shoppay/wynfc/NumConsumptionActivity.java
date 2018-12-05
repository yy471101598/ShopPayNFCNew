package com.shoppay.wynfc;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.shoppay.wynfc.adapter.NumAdapter;
import com.shoppay.wynfc.bean.NumShop;
import com.shoppay.wynfc.bean.VipInfo;
import com.shoppay.wynfc.bean.VipServece;
import com.shoppay.wynfc.card.ReadCardOpt;
import com.shoppay.wynfc.db.DBAdapter;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.BluetoothUtil;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.ESCUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.MergeLinearArraysUtil;
import com.shoppay.wynfc.tools.PreferenceHelper;

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

public class NumConsumptionActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_jiesuan, rl_vipname;
    private EditText et_vipcard;
    private TextView tv_title, tv_num, tv_vipname;
    private Context ac;
    private ListView listView;
    private String editString;
    private NumAdapter adapter;
    private List<VipServece> list;
    private NumchangeReceiver numchangeReceiver;
    private Dialog dialog;
    private DBAdapter dbAdapter;
    private int datalength = 0;
    private int shopnum = 0;
    private List<NumShop> numShopList = new ArrayList<>();
    private Gson gson = new Gson();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    VipInfo info = (VipInfo) msg.obj;
                    if (info.MemState == 0) {
                        tv_vipname.setText(info.MemName);
                        PreferenceHelper.write(ac, "shoppay", "vipcar", et_vipcard.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "memid", info.MemID + "");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", info.MemLevelID + "");
                        obtainVipServece();
                    } else if (info.MemState == 1) {
                        Toast.makeText(ac, "此卡已锁定", Toast.LENGTH_LONG).show();
                        tv_vipname.setText("");
                        listView.setVisibility(View.GONE);
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已锁定");
                    } else {
                        Toast.makeText(ac, "此卡已挂失", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已挂失");
                        tv_vipname.setText("");
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    tv_vipname.setText("");
                    listView.setVisibility(View.GONE);
                    break;
                case 5:
                    String card= msg.obj.toString();
                    Log.d("xxxx",card);
                    et_vipcard.setText(card);
                    break;
            }
        }
    };
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numconsumption);
        ac = MyApplication.context;
        ActivityStack.create().addActivity(NumConsumptionActivity.this);
        initView();
        dialog = DialogUtil.loadingDialog(NumConsumptionActivity.this, 1);
        dbAdapter = DBAdapter.getInstance(ac);
        dbAdapter.deleteNumShopCar();
        PreferenceHelper.write(ac, "shoppay", "memid", "123");
        PreferenceHelper.write(MyApplication.context,"shoppay","viptoast","未查询到会员");
        // 注册广播
        numchangeReceiver = new NumchangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.servecenumberchange");
        registerReceiver(numchangeReceiver, iiiff);

        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent =
                PendingIntent.getActivity(this, 0, nfcIntent, 0);
        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        et_vipcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rl_vipname.setVisibility(View.VISIBLE);
                tv_vipname.setText("");
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 1000);

            }
        });
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainVipInfo();
        }
    };

    private void obtainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memCard", editString);
        Log.d("xxvipinfo", editString);
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
                        LogUtils.d("xxVipinfoS", "22222");
                        PreferenceHelper.write(ac, "shoppay", "memid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
//                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    LogUtils.d("xxVipinfoS", "2222");
                    PreferenceHelper.write(ac, "shoppay", "memid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(ac, "shoppay", "memid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    private void obtainVipServece() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memID", PreferenceHelper.readString(ac, "shoppay", "memid", "123"));
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPMemCountProductList", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxVipServeceS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<VipServece>>() {
                        }.getType();
                        list = gson.fromJson(jso.getString("data"), listType);
                        listView.setVisibility(View.VISIBLE);
                        adapter = new NumAdapter(ac, list);
                        listView.setAdapter(adapter);


                    } else {
                        listView.setVisibility(View.GONE);
                        Toast.makeText(ac, "未查询到项目", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(ac, "shoppay", "memid", "123");
                listView.setVisibility(View.GONE);
            }
        });

    }

    private void initView() {
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_jiesuan = (RelativeLayout) findViewById(R.id.num_rl_jiesan);
        rl_vipname = (RelativeLayout) findViewById(R.id.num_rl_vipname);
        et_vipcard = (EditText) findViewById(R.id.num_et_card);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_num = (TextView) findViewById(R.id.num_tv_num);
        tv_vipname = (TextView) findViewById(R.id.num_tv_vipname);
        listView = (ListView) findViewById(R.id.num_listview);
        tv_title.setText("计次消费");

        rl_jiesuan.setOnClickListener(this);
        rl_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.num_rl_jiesan:
                if (tv_num.getText().toString().equals("0")) {
                    Toast.makeText(ac, "请选择消费项目",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(ac)) {
                        if (PreferenceHelper.readBoolean(ac, "shoppay", "IsChkPwd", false)) {
                            DialogUtil.pwdDialog("num", NumConsumptionActivity.this, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    jiesuan();
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        } else {
                            jiesuan();
                        }
                    } else {
                        Toast.makeText(ac, "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    private void jiesuan() {
        dialog.show();


        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        List<NumShop> listn = dbAdapter.getListNumShopCar(PreferenceHelper.readString(ac, "shoppay", "account", "123"));
        for (int i = 0; i < listn.size(); i++) {
            if (listn.get(i).count == 0) {
            } else {
                numShopList.add(listn.get(i));
            }
        }
        params.put("MemID", PreferenceHelper.readString(ac, "shoppay", "memid", "123"));
        params.put("number", shopnum);//消费总次
        params.put("count", datalength);
        for (int i = 0; i < numShopList.size(); i++) {
            params.put("data[" + i + "][CountDetailGoodsID]", numShopList.get(i).CountDetailGoodsID);
            params.put("data[" + i + "][count]", numShopList.get(i).count);
        }
        Log.d("xxx", params.toString());
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPCountExpense", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    Log.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Toast.makeText(ac, "结算成功", Toast.LENGTH_SHORT).show();
                        PreferenceHelper.write(ac, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
                        if (PreferenceHelper.readBoolean(ac, "shoppay", "IsPrint", false)) {
                            BluetoothUtil.connectBlueTooth(ac);
                            BluetoothUtil.sendData(printReceipt_BlueTooth(), PreferenceHelper.readInt(ac, "shoppay", "CountExpenesPrintNumber", 1));
                        }
                        finish();
//                        list.clear();
//                        dbAdapter.deleteNumShopCar();
//
//                        adapter.notifyDataSetChanged();
//                        et_vipcard.setText("");
//                        rl_vipname.setVisibility(View.GONE);
//                        tv_num.setText("0");
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Log.d("xxjiesuanE", new String(responseBody));
                Toast.makeText(ac, "结算失败，请重新结算", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public byte[] printReceipt_BlueTooth()
    {
        String danhao = "消费单号:" + PreferenceHelper.readString(ac, "shoppay","OrderAccount","");
        String huiyuankahao = "会员卡号:" + et_vipcard.getText().toString();
        String huiyuanming = "会员名称:" +tv_vipname.getText().toString();
        String xfnum = "消费次数:" +tv_num.getText().toString();

        String shopdetai="服务名称    "+"次数    "+"剩余次数";
        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(ac,"shoppay","PrintTitle","").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(ac,"shoppay","PrintFootNote","").getBytes("gb2312");
            byte[] tickname = "计次消费小票".getBytes("gb2312");
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xiaofeinum=xfnum.getBytes("gb2312");
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

            byte [] shoptitle=shopdetai.getBytes("gb2312");
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
                    left, vipname, nextLine, left,xiaofeinum,nextLine,xiahuaxian};

            byte[] headerBytes =ESCUtil. byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            byte[] sh=shopdetai.getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, sh};
            bytesList.add(ESCUtil.byteMerger(mticket1));
            //商品明细
            List<NumShop> list= dbAdapter.getListNumShopCar(PreferenceHelper.readString(ac,"shoppay","account","123"));
            for(NumShop numShop:list){
                if(numShop.count==0){
                }else{
                    byte[] a=(numShop.shopname+"             " +numShop.count+"      "+(Integer.parseInt(numShop.allnum)-numShop.count)+"").getBytes("gb2312");
                    byte[][] mticket = {nextLine, left, a};
                    bytesList.add(ESCUtil.byteMerger(mticket));
                }
            }
            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));
            byte[] ha=("操作人员:"+PreferenceHelper.readString(ac,"shoppay","UserName","")).trim().getBytes("gb2312");
            byte[] time=("消费时间:"+getStringDate()).trim().getBytes("gb2312");
            byte[] qianming=("客户签名:").getBytes("gb2312");
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


    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_vipcard);
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
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(numchangeReceiver);
        dbAdapter.deleteNumShopCar();
    }

    private class NumchangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("xx", "ShopChangeReceiver");
            List<NumShop> list = dbAdapter.getListNumShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
            shopnum = 0;
            datalength = 0;
            Gson gson = new Gson();
            for (NumShop numShop : list) {
                Log.d("xxx", gson.toJson(numShop));
                if (numShop.count == 0) {

                } else {
                    shopnum = shopnum + numShop.count;
                    datalength = datalength + 1;
                }

            }

            tv_num.setText(shopnum + "");

        }
    }


}
