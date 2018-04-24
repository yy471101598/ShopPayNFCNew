package com.shoppay.wynew;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.shoppay.wynew.adapter.LeftAdapter;
import com.shoppay.wynew.bean.ShopCar;
import com.shoppay.wynew.bean.ShopClass;
import com.shoppay.wynew.bean.VipInfo;
import com.shoppay.wynew.bean.VipPayMsg;
import com.shoppay.wynew.card.ReadCardOpt;
import com.shoppay.wynew.db.DBAdapter;
import com.shoppay.wynew.http.InterfaceBack;
import com.shoppay.wynew.modle.ImpWeixinPay;
import com.shoppay.wynew.modle.InterfaceMVC;
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
public class BalanceActivity extends FragmentActivity implements
        OnItemClickListener ,View.OnClickListener{

    private ListView listView;
    private List<ShopClass> list;
    private LeftAdapter adapter;
    private BalanceFragment myFragment;
    public static int mPosition;
    private RelativeLayout rl_yes,rl_no,rl_card,rl_jiesuan,rl_left,rl_vipname,rl_vipjifen,rl_vipyue;
    private TextView  tv_yes,tv_no,tv_num,tv_money,tv_jifen,tv_title,tv_vipname,tv_vipjifen,tv_vipyue;
    private LinearLayout li_jifen;
    private EditText et_card;
    private String type="是";
    private Dialog dialog;
    private Context ac;
    private ShopChangeReceiver shopchangeReceiver;
    private DBAdapter dbAdapter;
    private String editString;
    private double num=0,money=0,jifen=0,xfmoney=0;
    private Dialog jiesuanDialog;
    private VipPayMsg vipPayMsg;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    if(info.MemState==0) {
                        tv_vipname.setText(info.MemName);
                        tv_vipjifen.setText(info.MemPoint);
                        tv_vipyue.setText(info.MemMoney);
                        PreferenceHelper.write(ac, "shoppay", "vipcar", et_card.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "vipname", tv_vipname.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "memid", info.MemID+"");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", info.MemLevelID + "");
                        PreferenceHelper.write(ac, "shoppay", "MemMoney", info.MemMoney + "");
                        PreferenceHelper.write(ac, "shoppay", "jifenall",  info.MemPoint);
                    }else  if(info.MemState==1) {
                        Toast.makeText(ac,"此卡已锁定",Toast.LENGTH_LONG).show();
                        tv_vipname.setText("");
                        tv_vipjifen.setText("");
                        tv_vipyue.setText("");
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已锁定");
                    }else{
                        Toast.makeText(ac,"此卡已挂失",Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(ac,"shoppay","viptoast","此卡已挂失");
                        tv_vipname.setText("");
                        tv_vipjifen.setText("");
                        tv_vipyue.setText("");
                    }
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipjifen.setText("");
                    tv_vipyue.setText("");
                    break;
            }
        }
    };
    private Intent intent;
    private MsgReceiver msgReceiver;
    private Dialog weixinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ac=MyApplication.context;
       dialog= DialogUtil.loadingDialog(BalanceActivity.this,1);
        dbAdapter=DBAdapter.getInstance(ac);
        PreferenceHelper.write(ac, "shoppay", "memid", "");
        PreferenceHelper.write(ac,"shoppay","isSan",true);
        PreferenceHelper.write(ac, "shoppay", "vipcar","无");
        PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
        PreferenceHelper.write(MyApplication.context,"shoppay","viptoast","未查询到会员");
        dbAdapter.deleteShopCar();
        initView();
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
        obtainShopClass();

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

    @Override
    protected void onResume() {
        super.onResume();
      new ReadCardOpt(et_card);

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
                        PreferenceHelper.write(ac, "shoppay", "memid", list.get(0).MemID);
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
        rl_yes= (RelativeLayout) findViewById(R.id.rl_yes);
        rl_no= (RelativeLayout) findViewById(R.id.rl_no);
        rl_left= (RelativeLayout) findViewById(R.id.rl_left);
        rl_card= (RelativeLayout) findViewById(R.id.balance_rl_card);
        rl_vipjifen= (RelativeLayout) findViewById(R.id.balance_rl_vipjifen);
        rl_vipname= (RelativeLayout) findViewById(R.id.balance_rl_vipname);
        rl_jiesuan= (RelativeLayout) findViewById(R.id.balance_rl_jiesan);
        rl_vipyue= (RelativeLayout) findViewById(R.id.balance_rl_vipyue);

        tv_jifen= (TextView) findViewById(R.id.balance_tv_jifen);
        tv_vipjifen= (TextView) findViewById(R.id.balance_tv_vipjifen);
        tv_vipyue= (TextView) findViewById(R.id.balance_tv_vipyue);
        tv_vipname= (TextView) findViewById(R.id.balance_tv_vipname);
        tv_num= (TextView) findViewById(R.id.balance_tv_num);
        tv_money= (TextView) findViewById(R.id.balance_tv_money);
        tv_yes= (TextView) findViewById(R.id.tv_yes);
        tv_no= (TextView) findViewById(R.id.tv_no);
        tv_title= (TextView) findViewById(R.id.tv_title);
         tv_title.setText("商品消费");
        li_jifen= (LinearLayout) findViewById(R.id.balance_li_jifen);
        et_card= (EditText) findViewById(R.id.balance_et_card);
        listView = (ListView) findViewById(R.id.listview);

        rl_left.setOnClickListener(this);
        rl_yes.setOnClickListener(this);
        rl_no.setOnClickListener(this);
        rl_jiesuan.setOnClickListener(this);



        listView.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        //拿到当前位置
        mPosition = position;
        //即使刷新adapter
        adapter.notifyDataSetChanged();
            myFragment = new BalanceFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, myFragment);
            Bundle bundle = new Bundle();
            bundle.putString(BalanceFragment.TAG, list.get(mPosition).ClassID);
            bundle.putString("isSan",type);
            myFragment.setArguments(bundle);
            fragmentTransaction.commit();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_yes:
                rl_yes.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_no.setBackgroundColor(getResources().getColor(R.color.white));
                tv_yes.setTextColor(getResources().getColor(R.color.white));
                tv_no.setTextColor(getResources().getColor(R.color.text_30));
                type = "是";
                li_jifen.setVisibility(View.GONE);
                rl_card.setVisibility(View.GONE);
                rl_vipname.setVisibility(View.GONE);
                rl_vipjifen.setVisibility(View.GONE);
                rl_vipyue.setVisibility(View.GONE);
                dbAdapter.deleteShopCar();
                tv_money.setText("0");
                tv_jifen.setText("0");
                tv_num.setText("0");
                PreferenceHelper.write(ac,"shoppay","isSan",true);
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                PreferenceHelper.write(ac, "shoppay", "vipcar","无");
                PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
                for(ShopClass c:list){
                    c.shopnum="";
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.rl_no:
                rl_yes.setBackgroundColor(getResources().getColor(R.color.white));
                rl_no.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_yes.setTextColor(getResources().getColor(R.color.text_30));
                tv_no.setTextColor(getResources().getColor(R.color.white));
                type = "否";
                PreferenceHelper.write(ac,"shoppay","isSan",false);
                li_jifen.setVisibility(View.VISIBLE);
                rl_card.setVisibility(View.VISIBLE);
                rl_vipname.setVisibility(View.VISIBLE);
                rl_vipjifen.setVisibility(View.VISIBLE);
                rl_vipyue.setVisibility(View.VISIBLE);
                dbAdapter.deleteShopCar();
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                et_card.setText("");
                tv_vipjifen.setText("");
                tv_vipname.setText("");
                tv_vipyue.setText("");
                tv_money.setText("0");
                tv_jifen.setText("0");
                tv_num.setText("0");
                for(ShopClass c:list){
                    c.shopnum="";
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.balance_rl_jiesan:
                if(tv_num.getText().toString().equals("0")){
                    Toast.makeText(getApplicationContext(), "请选择商品",
                            Toast.LENGTH_SHORT).show();
                }else{
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        if(type.equals("否")){
                          if(tv_vipjifen.getText().toString().equals("")||tv_vipjifen.getText().toString().equals("获取中")){
                              Toast.makeText(ac,"您选择的是会员结算，请确认会员信息是否正确",Toast.LENGTH_SHORT).show();
                          }else{//会员结算
//
                         jiesuanDialog=  DialogUtil.jiesuanDialog("balance",BalanceActivity.this, 1,true,money,xfmoney,jifen, new InterfaceBack() {
                               @Override
                               public void onResponse(Object response) {
                                   finish();
                               }

                               @Override
                               public void onErrorResponse(Object msg) {
          vipPayMsg=(VipPayMsg) msg;
                                   PreferenceHelper.write(ac, "shoppay", "WxOrder", System.currentTimeMillis()+  PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                                       ImpWeixinPay weixinPay =new ImpWeixinPay();
                                       weixinPay.weixinPay(ac,tv_money.getText().toString(),PreferenceHelper.readString(getApplicationContext(), "shoppay", "OrderAccount", ""),"商品消费", new InterfaceMVC() {
                                           @Override
                                           public void onResponse(int code, Object response) {
                                             weixinDialog=  WeixinPayDialog.weixinPayDialog(BalanceActivity.this,1,(String)response,tv_money.getText().toString());
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
                        }else{//散客结算
                        jiesuanDialog=    DialogUtil.jiesuanDialog("balance",BalanceActivity.this, 1,false,money,xfmoney,jifen, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    Log.d("xxx","sankejiesuan");
                                    finish();
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                        vipPayMsg=(VipPayMsg) msg;
                                    PreferenceHelper.write(ac, "shoppay", "WxOrder", System.currentTimeMillis()+  PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                                        ImpWeixinPay weixinPay =new ImpWeixinPay();
                                        weixinPay.weixinPay(ac,tv_money.getText().toString(),PreferenceHelper.readString(getApplicationContext(), "shoppay", "OrderAccount", ""),"商品消费", new InterfaceMVC() {
                                            @Override
                                            public void onResponse(int code, Object response) {
                                              weixinDialog=  WeixinPayDialog.weixinPayDialog(BalanceActivity.this,1,(String)response,tv_money.getText().toString());
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
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void jiesuan(final  Context context,final VipPayMsg msg) {
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
        params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        params.put("DiscountMoney", msg.zhMoney);
        params.put("totalMoney", msg.xfMoney);
        params.put("Point",msg.obtainJifen);
        params.put("Remark", "");
        params.put("Count", datalength + "");
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
            params.put("data[" + i + "][price]", shoplist.get(i).price);
            params.put("data[" + i + "][pointPercent]", shoplist.get(i).pointPercent);
            params.put("data[" + i + "][point]", shoplist.get(i).point);
            params.put("data[" + i + "][discount]", shoplist.get(i).discount);
            params.put("data[" + i + "][discountmoney]", shoplist.get(i).discountmoney);
            params.put("data[" + i + "][goodsid]", shoplist.get(i).goodsid);
            params.put("data[" + i + "][goodspoint]", shoplist.get(i).goodspoint);
            params.put("data[" + i + "][goodsType]", shoplist.get(i).goodsType);
            params.put("data[" + i + "][count]", shoplist.get(i).count);
        }
        Log.d("xx", params.toString());

        client.post(PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGoodsExpense", params, new AsyncHttpResponseHandler() {
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
                                BluetoothUtil.sendData(printReceipt_BlueTooth(type, context, msg), PreferenceHelper.readInt(context, "shoppay", "GoodsExpenesPrintNumber", 1));
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
                dialog.dismiss();
                Toast.makeText(context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void obtainShopClass() {
            dialog.show();
        if(list!=null) {
            list.clear();
        }
            AsyncHttpClient client = new AsyncHttpClient();
            final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
            client.setCookieStore(myCookieStore);
            RequestParams params = new RequestParams();
            client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetGoodsClass", params, new AsyncHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                {
                    dialog.dismiss();
                    try {
                        LogUtils.d("xxshopclassS",new String(responseBody,"UTF-8"));
                        JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                        if(jso.getBoolean("success")){
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<ShopClass>>(){}.getType();
                            list = gson.fromJson(jso.getString("data"), listType);
                            //创建MyFragment对象
                            myFragment = new BalanceFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                    .beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, myFragment);
                            //通过bundle传值给MyFragment
                            Bundle bundle = new Bundle();
                            bundle.putString(BalanceFragment.TAG, list.get(mPosition).ClassID);
                            bundle.putString("isSan",type);
                            myFragment.setArguments(bundle);
                            fragmentTransaction.commit();
                            adapter = new LeftAdapter(ac, list);
                            Gson g=new Gson();
                           Log.d("xx",   g.toJson(list));
                            listView.setAdapter(adapter);
                        }else{
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(ac,"获取商品分类失败",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                {
                    dialog.dismiss();
                }
            });
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
//          if(shopClass.ClassID.equals(shopCar.goodsclassid)){
//              classnum=classnum+shopCar.count;
//          }
          for(ShopClass c:list){
              int classnum=0;
              for(ShopCar shopCar:listss){
                  if(shopCar.goodsclassid.equals(c.ClassID)){
                       classnum=classnum+shopCar.count;
                  }
              }
              c.shopnum=classnum+"";
          }
          adapter.notifyDataSetChanged();
       tv_jifen.setText((int) jifen+"");
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

                    weixinDialog.dismiss();
                    jiesuanDialog.dismiss();
                jiesuan(MyApplication.context,vipPayMsg);
                } else {
                    String msg = intent.getStringExtra("msg");
                    Toast.makeText(ac,msg,Toast.LENGTH_SHORT).show();

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
        String xfmoney = "消费金额:" +StringUtil.twoNum(msg.xfMoney);
        String obtainjifen = "获得积分:" +(int)Double.parseDouble(msg.obtainJifen);
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
            String shopdetai="商品名称    "+"单价    "+"数量    "+"合计";
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
                    byte[] a=(sb.toString()+"" +CommonUtils.lasttwo(Double.parseDouble(numShop.price))+"    "+numShop.count+"    "+numShop.discountmoney ).getBytes("gb2312");
                    byte[][] mticket = {nextLine, left, a};
                    bytesList.add(ESCUtil.byteMerger(mticket));
                }
            }
            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));
            if(msg.isMoney==1){

                byte[] yfmoney =( "应付金额:" +StringUtil.twoNum(msg.zhMoney)).getBytes("gb2312");
                byte[] jinshengmoney =( "节省金额:" +StringUtil.twoNum(msg.jieshengMoney)).getBytes("gb2312");

                byte[][] mticketsn = {nextLine,left,yfmoney,nextLine,left,jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
                byte[] moneys=( "现金支付:" +StringUtil.twoNum(msg.xjMoney)).getBytes("gb2312");
                byte[][] mticketsm= {nextLine,left,moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            if(msg.isWx==1){
                byte[] weixin=( "微信支付:" +StringUtil.twoNum(msg.wxMoney)).getBytes("gb2312");
                byte[][] weixins= {nextLine,left,weixin};
                bytesList.add(ESCUtil.byteMerger(weixins));
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
            if(!msg.vipName.equals("散客")) {
                byte[] syjinfen = ("剩余积分:" + (int) Double.parseDouble(msg.vipSyJifen)).getBytes("gb2312");
                byte[][] mticketsyjf = {nextLine, left, syjinfen};
                bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            }
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
