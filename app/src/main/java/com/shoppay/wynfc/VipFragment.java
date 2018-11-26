package com.shoppay.wynfc;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.shoppay.wynfc.bean.FastShopZhehMoney;
import com.shoppay.wynfc.bean.JifenDk;
import com.shoppay.wynfc.bean.VipInfo;
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
 * Created by songxiaotao on 2017/7/1.
 */

public class VipFragment extends Fragment implements View.OnClickListener {
    private EditText et_card, et_xfmoney, et_zfmoney, et_yuemoney, et_jfmoney;
    private TextView tv_vipname, tv_vipjf, tv_zhmoney, tv_maxdk, tv_dkmoney, tv_obtainjf, tv_money, tv_yue, tv_jf, tv_vipyue, tv_wx, tv_jiesuan;
    private RelativeLayout rl_money, rl_yue, rl_jf, rl_jiesuan, rl_jifen;
    private boolean isMoney = true, isYue = false, isJifen = false, isWx = false;
    private RelativeLayout rl_pay_money, rl_pay_yue, rl_pay_jifen, rl_pay_jifenmaxdk, rl_pay_jifendkm, rl_wx;
    private String editString;
    private Dialog dialog;
    private String xfmoney;
    double money = 0;
    double yue = 0;
    double jifen = 0;
    double dkmoney = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    if (info.MemState == 0) {
                        tv_vipname.setText(info.MemName);
                        tv_vipjf.setText(info.MemPoint);
                        tv_vipyue.setText(info.MemMoney);
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipcar", et_card.getText().toString());
                        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", info.MemID + "");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", info.MemLevelID + "");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "MemMoney", info.MemMoney);
                    } else if (info.MemState == 1) {
                        Toast.makeText(MyApplication.context, "此卡已锁定", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已锁定");
                        tv_vipname.setText("");
                        tv_vipjf.setText("");
                        tv_vipyue.setText("");
                    } else {
                        Toast.makeText(MyApplication.context, "此卡已挂失", Toast.LENGTH_LONG).show();
                        PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已挂失");
                        tv_vipname.setText("");
                        tv_vipjf.setText("");
                        tv_vipyue.setText("");
                    }
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipjf.setText("");
                    tv_vipyue.setText("");
                    break;


                case 3:
                    FastShopZhehMoney zh = (FastShopZhehMoney) msg.obj;
                    tv_zhmoney.setText(StringUtil.twoNum(zh.Money));
                    et_zfmoney.setText(StringUtil.twoNum(zh.Money));
                    tv_obtainjf.setText(Integer.parseInt(zh.Point) + "");
                    break;
                case 4:
                    tv_zhmoney.setText("0.00");
                    tv_obtainjf.setText("0");
                    break;

                case 5:
                    JifenDk jf = (JifenDk) msg.obj;
                    tv_maxdk.setText(StringUtil.twoNum(jf.MaxMoney));
                    break;
                case 6:
                    tv_maxdk.setText("");
                    break;
                case 8:
                    String card = msg.obj.toString();
                    Log.d("xxxx", card);
                    et_card.setText(card);
                    break;
            }
        }
    };
    private MsgReceiver msgReceiver;
    private Intent intent;
    private Dialog weixinDialog;
    private ShopChangeReceiver shopchangeReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vipconsumption, null);
        initView(view);
        dialog = DialogUtil.loadingDialog(getActivity(), 1);
        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
        PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", "123");
        PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "未查询到会员");

        // 注册广播
        shopchangeReceiver = new ShopChangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.fastvipcard");
        getActivity().registerReceiver(shopchangeReceiver, iiiff);
        et_zfmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {

                } else {
                    if (tv_zhmoney.getText().toString().equals("0.00") || tv_zhmoney.getText().toString().equals("获取中")) {
                        Toast.makeText(MyApplication.context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_zfmoney.setText("");
                    } else {
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
                            money = 0;
                        } else {
                            try {
                                money = Double.parseDouble(editable.toString());
                            } catch (Exception e) {
                                money = 0;
                            }
                        }
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {
                            try {
                                jifen = Double.parseDouble(et_jfmoney.getText().toString());
                                dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(MyApplication.context, "shoppay", "jifenpercent", "1"));
                                tv_dkmoney.setText(dkmoney + "");
                            } catch (Exception e) {
                                jifen = 0;
                                dkmoney = 0;
                                tv_dkmoney.setText(dkmoney + "");
                            }
                        }
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
                            yue = 0;
                        } else {
                            try {
                                yue = Double.parseDouble(et_yuemoney.getText().toString());
                            } catch (Exception e) {
                                yue = 0;
                            }
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            et_zfmoney.setText("");
                            Toast.makeText(MyApplication.context, "超出折后金额，请减少支付金额", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        et_yuemoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {

                } else {
                    if (tv_zhmoney.getText().toString().equals("0.00") || tv_zhmoney.getText().toString().equals("获取中")) {
                        Toast.makeText(MyApplication.context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_yuemoney.setText("");
                    } else {
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
                            yue = 0;
                        } else {
                            try {
                                yue = Double.parseDouble(et_yuemoney.getText().toString());
                            } catch (Exception e) {
                                yue = 0;
                            }
                        }
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {
                            try {
                                jifen = Double.parseDouble(et_jfmoney.getText().toString());
                                dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(MyApplication.context, "shoppay", "jifenpercent", "1"));
                                tv_dkmoney.setText(dkmoney + "");
                            } catch (Exception e) {
                                jifen = 0;
                                dkmoney = 0;
                                tv_dkmoney.setText(dkmoney + "");
                            }
                        }
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
                            money = 0;
                        } else {
                            try {
                                money = Double.parseDouble(et_zfmoney.getText().toString());
                            } catch (Exception e) {
                                money = 0;
                            }
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            Toast.makeText(MyApplication.context, "超出折后金额，请减少余额", Toast.LENGTH_SHORT).show();
                            et_yuemoney.setText("");
                        }
                    }
                }
            }
        });

        et_jfmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {

                } else {
                    if (tv_zhmoney.getText().toString().equals("0.00") || tv_zhmoney.getText().toString().equals("") ||
                            tv_maxdk.getText().toString().equals("")) {
                        Toast.makeText(MyApplication.context, "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_jfmoney.setText("");
                    } else {
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {

                            try {
                                jifen = Double.parseDouble(editable.toString());
                                dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(MyApplication.context, "shoppay", "jifenpercent", "1"));
                                tv_dkmoney.setText(dkmoney + "");
                            } catch (Exception e) {
                                jifen = 0;
                                dkmoney = 0;
                                tv_dkmoney.setText(dkmoney + "");
                            }
                        }
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {

                        } else {
                            try {
                                money = Double.parseDouble(et_zfmoney.getText().toString());
                            } catch (Exception e) {
                                money = 0;
                            }
                        }
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {

                        } else {
                            try {
                                yue = Double.parseDouble(et_yuemoney.getText().toString());
                            } catch (Exception e) {
                                yue = 0;
                            }
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            tv_dkmoney.setText("0");
                            et_jfmoney.setText("");
                            Toast.makeText(MyApplication.context, "超出折后金额，请减少输入积分", Toast.LENGTH_SHORT).show();
                        } else if (Double.parseDouble(tv_vipjf.getText().toString()) - jifen < 0) {
                            tv_dkmoney.setText("0");
                            et_jfmoney.setText("");
                            Toast.makeText(MyApplication.context, "输入积分超过会员积分", Toast.LENGTH_SHORT).show();
                        } else if (dkmoney > Double.parseDouble(tv_maxdk.getText().toString())) {
                            tv_dkmoney.setText("0");
                            et_jfmoney.setText("");
                            Toast.makeText(MyApplication.context, "输入积分超过最大抵扣", Toast.LENGTH_SHORT).show();
                        } else if (dkmoney > Double.parseDouble(tv_zhmoney.getText().toString())) {
                            tv_dkmoney.setText("0");
                            et_jfmoney.setText("");
                            Toast.makeText(MyApplication.context, "输入积分超过折后金额", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
        et_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 800);
            }
        });
        et_xfmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {

                } else {
                    if (PreferenceHelper.readString(MyApplication.context, "shoppay", "vipdengjiid", "123").equals("123")) {
                        Toast.makeText(MyApplication.context, PreferenceHelper.readString(MyApplication.context, "shoppay", "viptoast", "未查询到会员"), Toast.LENGTH_SHORT).show();
                        et_xfmoney.setText("");
                    } else {
                        if (moneyrun != null) {
                            //每次editText有变化的时候，则移除上次发出的延迟线程
                            handler.removeCallbacks(moneyrun);
                        }
                        xfmoney = editable.toString();
                        try {
                            Double.parseDouble(xfmoney);
                            handler.postDelayed(moneyrun, 800);
                        } catch (Exception e) {
                        }
                        //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    }
                }
            }
        });


        PreferenceHelper.write(getActivity(), "PayOk", "time", "false");
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        getActivity().registerReceiver(msgReceiver, intentFilter);
        return view;
    }

    private class ShopChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String card = intent.getStringExtra("card");
            Message msg = handler.obtainMessage();
            msg.what = 8;
            msg.obj = card;
            handler.sendMessage(msg);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        new ReadCardOpt(et_card);
    }

    @Override
    public void onStop() {
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

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable moneyrun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainZhehMoney();
            obtainJifenDkMoney();
        }
    };
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

    private void obtainZhehMoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("money", xfmoney);
        params.put("levelid", PreferenceHelper.readString(MyApplication.context, "shoppay", "vipdengjiid", "123"));
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetDiscountMoney", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxZhehMoneyS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<FastShopZhehMoney>>() {
                        }.getType();
                        List<FastShopZhehMoney> list = gson.fromJson(jso.getString("data"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 4;
                        handler.sendMessage(msg);
                        Toast.makeText(MyApplication.context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                    PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
            }
        });
    }

    private void obtainJifenDkMoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Money", xfmoney);
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetPointOffset", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxJifendkMoneyS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        JifenDk jf = gson.fromJson(jso.getString("data"), JifenDk.class);
//                        Type listType = new TypeToken<List<FastShopZhehMoney>>(){}.getType();
//                        List<FastShopZhehMoney> list = gson.fromJson(jso.getString("data"), listType);
                        PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", jf.PontToMoneyRatio);
                        Message msg = handler.obtainMessage();
                        msg.what = 5;
                        msg.obj = jf;
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 6;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 6;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", "123");
                Message msg = handler.obtainMessage();
                msg.what = 6;
                handler.sendMessage(msg);
            }
        });
    }

    private void obtainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memCard", editString);
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetMem", params, new AsyncHttpResponseHandler() {
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
                        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
//                        Toast.makeText(MyApplication.context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                    PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    private void initView(View view) {
        et_card = (EditText) view.findViewById(R.id.vip_et_card);
        et_xfmoney = (EditText) view.findViewById(R.id.vip_et_xfmoney);
        et_zfmoney = (EditText) view.findViewById(R.id.vip_et_money);
        et_yuemoney = (EditText) view.findViewById(R.id.vip_et_yue);
        et_jfmoney = (EditText) view.findViewById(R.id.vip_et_jifen);
        tv_jiesuan = (TextView) view.findViewById(R.id.tv_jiesuan);
        tv_vipname = (TextView) view.findViewById(R.id.vip_tv_name);
        tv_vipjf = (TextView) view.findViewById(R.id.vip_tv_jifen);
        tv_vipyue = (TextView) view.findViewById(R.id.vip_tv_vipyue);
        tv_zhmoney = (TextView) view.findViewById(R.id.vip_tv_zhmoney);
        tv_maxdk = (TextView) view.findViewById(R.id.vip_tv_maxdk);
        tv_dkmoney = (TextView) view.findViewById(R.id.vip_tv_dkmoney);
        tv_obtainjf = (TextView) view.findViewById(R.id.vip_tv_hasjf);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_yue = (TextView) view.findViewById(R.id.tv_yue);
        tv_jf = (TextView) view.findViewById(R.id.tv_jifen);
        tv_wx = (TextView) view.findViewById(R.id.tv_wx);
        rl_jf = (RelativeLayout) view.findViewById(R.id.rl_jifen);
        rl_wx = (RelativeLayout) view.findViewById(R.id.rl_wx);
        rl_yue = (RelativeLayout) view.findViewById(R.id.rl_yue);
        rl_money = (RelativeLayout) view.findViewById(R.id.rl_money);
        rl_jifen = (RelativeLayout) view.findViewById(R.id.rl_jifen);
        rl_jiesuan = (RelativeLayout) view.findViewById(R.id.vip_rl_jiesuan);
        rl_pay_money = (RelativeLayout) view.findViewById(R.id.consumption_rl_money);
        rl_pay_jifen = (RelativeLayout) view.findViewById(R.id.consumption_rl_jifen);
        rl_pay_jifendkm = (RelativeLayout) view.findViewById(R.id.consumption_rl_jfdk);
        rl_pay_jifenmaxdk = (RelativeLayout) view.findViewById(R.id.consumption_rl_maxdk);
        rl_pay_yue = (RelativeLayout) view.findViewById(R.id.consumption_rl_yue);
        rl_wx.setOnClickListener(this);
        rl_jf.setOnClickListener(this);
        rl_yue.setOnClickListener(this);
        rl_money.setOnClickListener(this);
        rl_jifen.setOnClickListener(this);
        rl_jiesuan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_jifen:
                if (!isMoney && !isYue && !isWx) {
                    Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (isJifen) {
                        rl_jifen.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_jf.setTextColor(getResources().getColor(R.color.text_30));
                        isJifen = false;
                        rl_pay_jifenmaxdk.setVisibility(View.GONE);
                        rl_pay_jifen.setVisibility(View.GONE);
                        rl_pay_jifendkm.setVisibility(View.GONE);
                        et_jfmoney.setText("");
                        tv_dkmoney.setText("");
                        jifen = 0;
                        dkmoney = 0;
                    } else {
                        rl_jifen.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_jf.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_jifen.setVisibility(View.VISIBLE);
                        rl_pay_jifendkm.setVisibility(View.VISIBLE);
                        rl_pay_jifenmaxdk.setVisibility(View.VISIBLE);
                        isJifen = true;
                        rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_wx.setTextColor(getResources().getColor(R.color.text_30));
                        isWx = false;
                        tv_jiesuan.setText("结算");
                    }
                }
                break;
            case R.id.rl_yue:
                if (!isJifen && !isMoney && !isWx) {
                    Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (isYue) {
                        rl_yue.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_yue.setTextColor(getResources().getColor(R.color.text_30));
                        isYue = false;
                        rl_pay_yue.setVisibility(View.GONE);
                        et_yuemoney.setText("");
                        yue = 0;
                    } else {
                        rl_yue.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_yue.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_yue.setVisibility(View.VISIBLE);
                        isYue = true;
                        rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_wx.setTextColor(getResources().getColor(R.color.text_30));
                        isWx = false;
                        tv_jiesuan.setText("结算");
                    }
                }
                break;
            case R.id.rl_wx:
                if (isWx) {
                    Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
//                        rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
//                        tv_wx.setTextColor(getResources().getColor(R.color.text_30));
//                        isWx = false;
//                        tv_jiesuan.setText("结算");
                } else {
                    rl_wx.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    tv_wx.setTextColor(getResources().getColor(R.color.white));
                    tv_jiesuan.setText("扫码支付");
                    isWx = true;
                    resetPayType();
                }
                break;
            case R.id.rl_money:
                if (!isJifen && !isYue && !isWx) {
                    Toast.makeText(MyApplication.context, "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (isMoney) {
                        rl_money.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_money.setTextColor(getResources().getColor(R.color.text_30));
                        isMoney = false;
                        et_zfmoney.setText("");
                        money = 0;
                        rl_pay_money.setVisibility(View.GONE);
                    } else {
                        rl_money.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_money.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_money.setVisibility(View.VISIBLE);
                        isMoney = true;
                        rl_wx.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_wx.setTextColor(getResources().getColor(R.color.text_30));
                        isWx = false;
                        tv_jiesuan.setText("结算");
                    }
                }
                break;
            case R.id.vip_rl_jiesuan:
                if (et_card.getText().toString().equals("")
                        || et_card.getText().toString() == null) {
                    Toast.makeText(MyApplication.context, "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                } else if (et_xfmoney.getText().toString().equals("")
                        || et_xfmoney.getText().toString() == null) {
                    Toast.makeText(MyApplication.context, "请输入消费金额",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_vipjf.getText().toString().equals("获取中")) {
                    Toast.makeText(MyApplication.context, "请重新输入会员卡号，获取会员信息",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_zhmoney.getText().toString().equals("获取中")) {
                    Toast.makeText(MyApplication.context, "请重新输入消费金额，获取折后金额",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(MyApplication.context)) {
                        Log.d("xxx", Double.parseDouble(tv_zhmoney.getText().toString()) + ";" + money + ";" + yue + ";" + dkmoney);
                        if (isWx) {
                            PreferenceHelper.write(getActivity(), "shoppay", "fasttype", "vip");
                            PreferenceHelper.write(getActivity(), "shoppay", "WxOrder", System.currentTimeMillis() + PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
                            ImpWeixinPay weixinPay = new ImpWeixinPay();
                            weixinPay.weixinPay(getActivity(), tv_zhmoney.getText().toString(), "", "快速消费", new InterfaceMVC() {
                                @Override
                                public void onResponse(int code, Object response) {
                                    weixinDialog = WeixinPayDialog.weixinPayDialog(getActivity(), 1, (String) response, tv_zhmoney.getText().toString());
                                    intent = new Intent(getActivity(),
                                            PayResultPollService.class);
                                    getActivity().startService(intent);
                                }

                                @Override
                                public void onErrorResponse(int code, Object msg) {

                                }
                            });
                        } else {
                            if (Double.parseDouble(tv_zhmoney.getText().toString()) - money - yue - dkmoney < 0) {
                                Toast.makeText(MyApplication.context, "超过折后金额，请检查输入信息",
                                        Toast.LENGTH_SHORT).show();
                            } else if (Double.parseDouble(tv_zhmoney.getText().toString()) - money - yue - dkmoney > 0) {
                                Toast.makeText(MyApplication.context, "少于折后金额，请检查输入信息",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (isMoney && !isJifen && !isYue) {
                                    jiesuan();
                                } else {
                                    if (PreferenceHelper.readBoolean(MyApplication.context, "shoppay", "IsChkPwd", false)) {
                                        DialogUtil.pwdDialog("vip", getActivity(), 1, new InterfaceBack() {
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
                                }
                            }
                        }
                    } else {
                        Toast.makeText(MyApplication.context, "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void resetPayType() {
        isYue = false;
        isJifen = false;
        isMoney = false;
        rl_jifen.setBackgroundColor(getResources().getColor(R.color.white));
        tv_jf.setTextColor(getResources().getColor(R.color.text_30));
        rl_pay_jifenmaxdk.setVisibility(View.GONE);
        rl_pay_jifen.setVisibility(View.GONE);
        rl_pay_jifendkm.setVisibility(View.GONE);
        et_jfmoney.setText("");
        tv_dkmoney.setText("");
        jifen = 0;
        dkmoney = 0;
        rl_yue.setBackgroundColor(getResources().getColor(R.color.white));
        tv_yue.setTextColor(getResources().getColor(R.color.text_30));
        rl_pay_yue.setVisibility(View.GONE);
        et_yuemoney.setText("");
        yue = 0;
        rl_money.setBackgroundColor(getResources().getColor(R.color.white));
        tv_money.setTextColor(getResources().getColor(R.color.text_30));
        et_zfmoney.setText("");
        money = 0;
        rl_pay_money.setVisibility(View.GONE);
    }

    private void jiesuan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Ismember", 1);//1为用户消费，0为散客消费
        params.put("memID", PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
        params.put("Point", tv_obtainjf.getText().toString());
        params.put("Money", et_xfmoney.getText().toString());
        params.put("discountmoney", tv_zhmoney.getText().toString());
        if (!isJifen) {
            params.put("bolIsPoint", 0);//1：真 0：假
            params.put("PointPayMoney", 0);
            params.put("UsePoint", 0);
        } else {
            params.put("bolIsPoint", 1);
            params.put("PointPayMoney", tv_dkmoney.getText().toString());
            params.put("UsePoint", et_jfmoney.getText().toString());
        }
        if (!isYue) {
            params.put("bolIsCard", 0);//1：真 0：假
            params.put("CardPayMoney", 0);
        } else {
            params.put("bolIsCard", 1);
            params.put("CardPayMoney", et_yuemoney.getText().toString());
        }
        if (!isMoney) {
            params.put("bolIsCash", 0);//1：真 0：假
            params.put("CashPayMoney", 0);
        } else {
            params.put("bolIsCash", 1);
            params.put("CashPayMoney", et_zfmoney.getText().toString());
        }
        if (isWx) {
            params.put("bolIsWeiXin", 1);//1：真 0：假
            params.put("WeiXinPayMoney", tv_zhmoney.getText().toString());
        } else {
            params.put("bolIsWeiXin", 0);//1：真 0：假
            params.put("WeiXinPayMoney", 0);
        }
        Gson gson = new Gson();
        Log.d("xx", params.toString());
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppShopFastExpense", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
                        Toast.makeText(MyApplication.context, "结算成功",
                                Toast.LENGTH_LONG).show();
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter.isEnabled()) {
                            if (PreferenceHelper.readBoolean(MyApplication.context, "shoppay", "IsPrint", false)) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(printReceipt_BlueTooth(), PreferenceHelper.readInt(MyApplication.context, "shoppay", "FastExpenesPrintNumber", 1));
                            }
                            ActivityStack.create().finishActivity(FastConsumptionActivity.class);
                        } else {
                            ActivityStack.create().finishActivity(FastConsumptionActivity.class);
                        }

                    } else {
                        Toast.makeText(MyApplication.context, jso.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                LogUtils.d("xxjiesuanE", error.getMessage());
                Toast.makeText(MyApplication.context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public byte[] printReceipt_BlueTooth() {
        String danhao = "消费单号:" + PreferenceHelper.readString(MyApplication.context, "shoppay", "OrderAccount", "");
        String huiyuankahao = "会员卡号:" + et_card.getText().toString();
        String huiyuanming = "会员名称:" + tv_vipname.getText().toString();

        try {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(MyApplication.context, "shoppay", "PrintTitle", "").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(MyApplication.context, "shoppay", "PrintFootNote", "").getBytes("gb2312");
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
                    left, vipname, nextLine, xiahuaxian};

            byte[] headerBytes = ESCUtil.byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            byte[] xfmoney = ("消费金额:" + StringUtil.twoNum(et_xfmoney.getText().toString())).getBytes("gb2312");
            byte[] hasjifen = ("获得积分:" + tv_obtainjf.getText().toString()).getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, xfmoney, nextLine, left, hasjifen};
            bytesList.add(ESCUtil.byteMerger(mticket1));


            byte[][] mtickets = {nextLine, xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));

            byte[] yfmoney = ("应付金额:" + StringUtil.twoNum(tv_zhmoney.getText().toString())).getBytes("gb2312");
            double xx = Double.parseDouble(et_xfmoney.getText().toString());
            double zh = Double.parseDouble(tv_zhmoney.getText().toString());
            Log.d("xxx", xx + ";" + zh);
            if (!isWx) {
                byte[] jinshengmoney = ("节省金额:" + StringUtil.twoNum(Double.toString(CommonUtils.del(xx, zh)))).getBytes("gb2312");

                byte[][] mticketsn = {nextLine, left, yfmoney, nextLine, left, jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
            }
            if (isMoney) {
                byte[] moneys = ("现金支付:" + StringUtil.twoNum(et_zfmoney.getText().toString())).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            if (isWx) {
                byte[] weixin = ("微信支付:" + StringUtil.twoNum(tv_zhmoney.getText().toString())).getBytes("gb2312");
                byte[][] weixins = {nextLine, left, weixin};
                bytesList.add(ESCUtil.byteMerger(weixins));
            }
            if (isYue) {
                byte[] yue = ("余额支付:" + StringUtil.twoNum(et_yuemoney.getText().toString())).getBytes("gb2312");
                byte[][] mticketyue = {nextLine, left, yue};
                bytesList.add(ESCUtil.byteMerger(mticketyue));
            }
            if (isJifen) {
                byte[] jifen = ("积分抵扣:" + tv_dkmoney.getText().toString()).getBytes("gb2312");
                byte[][] mticketjin = {nextLine, left, jifen};
                bytesList.add(ESCUtil.byteMerger(mticketjin));
            }
            double syjf = Double.parseDouble(tv_vipjf.getText().toString()) - jifen + Double.parseDouble(tv_obtainjf.getText().toString());

            Log.d("xxx", tv_vipjf.getText().toString() + ";" + jifen + ";" + et_jfmoney.getText().toString());
            byte[] syjinfen = ("剩余积分:" + (int) syjf).getBytes("gb2312");
            byte[][] mticketsyjf = {nextLine, left, syjinfen};
            bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            double yuemoney = 0;
            if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
            } else {
                yuemoney = Double.parseDouble(et_yuemoney.getText().toString());
            }
            if (isYue) {
                double sy = CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(MyApplication.context, "shoppay", "MemMoney", "0")), yuemoney);
                byte[] shengyu = ("卡内余额:" + StringUtil.twoNum(sy + "")).getBytes("gb2312");
                byte[][] mticketsy = {nextLine, left, shengyu};
                bytesList.add(ESCUtil.byteMerger(mticketsy));
            }
            byte[] ha = ("操作人员:" + PreferenceHelper.readString(MyApplication.context
                    , "shoppay", "UserName", "")).trim().getBytes("gb2312");
            byte[] time = ("消费时间:" + getStringDate()).trim().getBytes("gb2312");
            byte[] qianming = ("客户签名:").getBytes("gb2312");

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
            String type = PreferenceHelper.readString(getActivity(), "shoppay", "fasttype", "vip");
            Log.d("xxxx", type);
            if (type.equals("vip")) {
                if (state == null || state.equals("")) {

                } else {
                    if (state.equals("success")) {
                        //支付成功，跳转
                        weixinDialog.dismiss();
                        jiesuan();
                    } else {
                        String msg = intent.getStringExtra("msg");
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

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
        getActivity().unregisterReceiver(shopchangeReceiver);

        //关闭闹钟机制启动service
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        int anHour = 2 * 1000; // 这是一小时的毫秒数 60 * 60 * 1000
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
        manager.cancel(pi);
        //注销广播
        getActivity().unregisterReceiver(msgReceiver);
    }
}
