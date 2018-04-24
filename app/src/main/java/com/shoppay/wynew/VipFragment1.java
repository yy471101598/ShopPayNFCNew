package com.shoppay.wynew;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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
import com.shoppay.wynew.bean.FastShopZhehMoney;
import com.shoppay.wynew.bean.JifenDk;
import com.shoppay.wynew.bean.VipInfo;
import com.shoppay.wynew.bean.VipPayMsg;
import com.shoppay.wynew.card.ReadCardOpt;
import com.shoppay.wynew.tools.ActivityStack;
import com.shoppay.wynew.tools.BluetoothUtil;
import com.shoppay.wynew.tools.CommonUtils;
import com.shoppay.wynew.tools.DialogUtil;
import com.shoppay.wynew.tools.ESCUtil;
import com.shoppay.wynew.tools.LogUtils;
import com.shoppay.wynew.tools.MergeLinearArraysUtil;
import com.shoppay.wynew.tools.PreferenceHelper;

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

public class VipFragment1 extends Fragment implements View.OnClickListener {
    private EditText et_card, et_xfmoney, et_zfmoney, et_yuemoney, et_jfmoney;
    private TextView tv_vipname, tv_vipjf, tv_zhmoney, tv_maxdk, tv_dkmoney, tv_obtainjf, tv_money, tv_yue, tv_jf,tv_vipyue;
    private RelativeLayout rl_money, rl_yue, rl_jf, rl_jiesuan, rl_jifen;
    private boolean isMoney = true, isYue=false, isJifen=false;
    private RelativeLayout rl_pay_money, rl_pay_yue, rl_pay_jifen, rl_pay_jifenmaxdk, rl_pay_jifendkm;
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
                    tv_vipname.setText(info.MemName);
                    tv_vipjf.setText(info.MemPoint);
                    tv_vipyue.setText(info.MemMoney);
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipjf.setText("");
                    tv_vipyue.setText("");
                    break;


                case 3:
                    FastShopZhehMoney zh = (FastShopZhehMoney) msg.obj;
                    tv_zhmoney.setText(zh.Money);
                    tv_obtainjf.setText(zh.Point);
                    break;
                case 4:
                    tv_zhmoney.setText("0");
                    tv_obtainjf.setText("0.0");
                    break;

                case 5:
                    JifenDk jf = (JifenDk) msg.obj;
                    tv_maxdk.setText(jf.MaxMoney);
                    break;
                case 6:
                    tv_maxdk.setText("");
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vipconsumption, null);
        initView(view);
        dialog = DialogUtil.loadingDialog(getActivity(), 1);
        PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
        PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
        PreferenceHelper.write(getActivity(), "shoppay", "jifenpercent", "123");

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
                    if (tv_zhmoney.getText().toString().equals("0.0") || tv_zhmoney.getText().toString().equals("获取中") ||
                            tv_maxdk.getText().toString().equals("0.0") || tv_maxdk.getText().toString().equals("获取中")) {
                        Toast.makeText(getActivity(), "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_zfmoney.setText("");
                    } else {
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
                            money = 0;
                        } else {
                            money = Double.parseDouble(editable.toString());
                        }
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {
                            jifen = Double.parseDouble(et_jfmoney.getText().toString());
                            dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(getActivity(), "shoppay", "jifenpercent", "1"));
                            tv_dkmoney.setText(dkmoney + "");
                        }
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
                            yue = 0;
                        } else {
                            yue = Double.parseDouble(et_yuemoney.getText().toString());
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            et_zfmoney.setText("");
                            Toast.makeText(getActivity(), "超出折后金额，请减少支付金额", Toast.LENGTH_SHORT).show();
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
                    if (tv_zhmoney.getText().toString().equals("0.0") || tv_zhmoney.getText().toString().equals("获取中") ||
                            tv_maxdk.getText().toString().equals("0.0") || tv_maxdk.getText().toString().equals("获取中")) {
                        Toast.makeText(getActivity(), "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_yuemoney.setText("");
                    } else {
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {
                            yue = 0;
                        } else {
                            yue = Double.parseDouble(et_yuemoney.getText().toString());
                        }
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {
                            jifen = Double.parseDouble(et_jfmoney.getText().toString());
                            dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(getActivity(), "shoppay", "jifenpercent", "1"));
                            tv_dkmoney.setText(dkmoney + "");
                        }
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {
                            money = 0;
                        } else {
                            money = Double.parseDouble(et_zfmoney.getText().toString());
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            Toast.makeText(getActivity(), "超出折后金额，请减少余额", Toast.LENGTH_SHORT).show();
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
                    if (tv_zhmoney.getText().toString().equals("0.0") || tv_zhmoney.getText().toString().equals("获取中") ||
                            tv_maxdk.getText().toString().equals("0.0") || tv_maxdk.getText().toString().equals("获取中")) {
                        Toast.makeText(getActivity(), "请先输入消费金额，获取折后金额", Toast.LENGTH_SHORT).show();
                        et_jfmoney.setText("");
                    } else {
                        if (et_jfmoney.getText().toString() == null || et_jfmoney.getText().toString().equals("")) {
                            jifen = 0;
                            dkmoney = 0;
                            tv_dkmoney.setText("0");
                        } else {
                            jifen = Double.parseDouble(editable.toString());
                            dkmoney = jifen * Double.parseDouble(PreferenceHelper.readString(getActivity(), "shoppay", "jifenpercent", "1"));
                            tv_dkmoney.setText(dkmoney + "");
                        }
                        if (et_zfmoney.getText().toString() == null || et_zfmoney.getText().toString().equals("")) {

                        } else {
                            money = Double.parseDouble(et_zfmoney.getText().toString());
                        }
                        if (et_yuemoney.getText().toString() == null || et_yuemoney.getText().toString().equals("")) {

                        } else {
                            yue = Double.parseDouble(et_yuemoney.getText().toString());
                        }
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - yue - dkmoney - money < 0) {
                            et_jfmoney.setText("");
                            Toast.makeText(getActivity(), "超出折后金额，请减少输入积分", Toast.LENGTH_SHORT).show();
                        } else if (Double.parseDouble(tv_vipjf.getText().toString()) - jifen < 0) {
                            et_jfmoney.setText("");
                            Toast.makeText(getActivity(), "输入积分超过会员积分", Toast.LENGTH_SHORT).show();
                        } else if (dkmoney > Double.parseDouble(tv_maxdk.getText().toString())) {
                            et_jfmoney.setText("");
                            Toast.makeText(getActivity(), "输入积分超过最大抵扣", Toast.LENGTH_SHORT).show();
                        } else if (dkmoney > Double.parseDouble(tv_zhmoney.getText().toString())) {
                            et_jfmoney.setText("");
                            Toast.makeText(getActivity(), "输入积分超过折后金额", Toast.LENGTH_SHORT).show();
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
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
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
                    if (PreferenceHelper.readString(getActivity(), "shoppay", "vipdengjiid", "123").equals("123")) {
                        Toast.makeText(getActivity(), "请先输入会员卡", Toast.LENGTH_SHORT).show();
                        et_xfmoney.setText("");
                    } else {
                        if (moneyrun != null) {
                            //每次editText有变化的时候，则移除上次发出的延迟线程
                            handler.removeCallbacks(moneyrun);
                        }
                        xfmoney = editable.toString();

                        //延迟800ms，如果不再输入字符，则执行该线程的run方法
                        handler.postDelayed(moneyrun, 800);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new ReadCardOpt(et_card);
    }

    @Override
    public void onStop() {
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
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("money", xfmoney);
        params.put("levelid", PreferenceHelper.readString(getActivity(), "shoppay", "vipdengjiid", "123"));
        client.post(PreferenceHelper.readString(getActivity(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetDiscountMoney", params, new AsyncHttpResponseHandler() {
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
                        PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                        PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 4;
                        handler.sendMessage(msg);
                        Toast.makeText(getActivity(), jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                    PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
                LogUtils.d("xxVipInfoE", new String(responseBody));
            }
        });
    }

    private void obtainJifenDkMoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Money", xfmoney);
        client.post(PreferenceHelper.readString(getActivity(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetPointOffset", params, new AsyncHttpResponseHandler() {
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
                        PreferenceHelper.write(getActivity(), "shoppay", "jifenpercent", jf.PontToMoneyRatio);
                        Message msg = handler.obtainMessage();
                        msg.what = 5;
                        msg.obj = jf;
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(getActivity(), "shoppay", "jifenpercent", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 6;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(getActivity(), "shoppay", "jifenpercent", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 6;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(getActivity(), "shoppay", "jifenpercent", "123");
                Message msg = handler.obtainMessage();
                msg.what = 6;
                handler.sendMessage(msg);
                LogUtils.d("xxVipInfoE", new String(responseBody));
            }
        });
    }

    private void obtainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memCard", editString);
        client.post(PreferenceHelper.readString(getActivity(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetMem", params, new AsyncHttpResponseHandler() {
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
                        PreferenceHelper.write(getActivity(), "shoppay", "memid", list.get(0).MemID+"");
                        PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", list.get(0).MemLevelID + "");
                        PreferenceHelper.write(getActivity(), "shoppay", "MemMoney", list.get(0).MemMoney);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                        PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
//                        Toast.makeText(getActivity(), jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                    PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(getActivity(), "shoppay", "memid", "123");
                PreferenceHelper.write(getActivity(), "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
                LogUtils.d("xxVipInfoE", new String(responseBody));
            }
        });
    }

    private void initView(View view) {
        et_card = (EditText) view.findViewById(R.id.vip_et_card);
        et_xfmoney = (EditText) view.findViewById(R.id.vip_et_xfmoney);
        et_zfmoney = (EditText) view.findViewById(R.id.vip_et_money);
        et_yuemoney = (EditText) view.findViewById(R.id.vip_et_yue);
        et_jfmoney = (EditText) view.findViewById(R.id.vip_et_jifen);

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

        rl_jf = (RelativeLayout) view.findViewById(R.id.rl_jifen);
        rl_yue = (RelativeLayout) view.findViewById(R.id.rl_yue);
        rl_money = (RelativeLayout) view.findViewById(R.id.rl_money);
        rl_jifen = (RelativeLayout) view.findViewById(R.id.rl_jifen);
        rl_jiesuan = (RelativeLayout) view.findViewById(R.id.vip_rl_jiesuan);
        rl_pay_money = (RelativeLayout) view.findViewById(R.id.consumption_rl_money);
        rl_pay_jifen = (RelativeLayout) view.findViewById(R.id.consumption_rl_jifen);
        rl_pay_jifendkm = (RelativeLayout) view.findViewById(R.id.consumption_rl_jfdk);
        rl_pay_jifenmaxdk = (RelativeLayout) view.findViewById(R.id.consumption_rl_maxdk);
        rl_pay_yue = (RelativeLayout) view.findViewById(R.id.consumption_rl_yue);

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
                if (!isMoney && !isYue) {
                    Toast.makeText(getActivity(), "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
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
                        jifen=0;
                        dkmoney=0;
                    } else {
                        rl_jifen.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_jf.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_jifen.setVisibility(View.VISIBLE);
                        rl_pay_jifendkm.setVisibility(View.VISIBLE);
                        rl_pay_jifenmaxdk.setVisibility(View.VISIBLE);
                        isJifen = true;
                    }
                }
                break;
            case R.id.rl_yue:
                if (!isJifen && !isMoney) {
                    Toast.makeText(getActivity(), "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (isYue) {
                        rl_yue.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_yue.setTextColor(getResources().getColor(R.color.text_30));
                        isYue = false;
                        rl_pay_yue.setVisibility(View.GONE);
                        et_yuemoney.setText("");
                        yue=0;
                    } else {
                        rl_yue.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_yue.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_yue.setVisibility(View.VISIBLE);
                        isYue = true;
                    }
                }
                break;
            case R.id.rl_money:
                if (!isJifen && !isYue) {
                    Toast.makeText(getActivity(), "至少选择一种支付方式", Toast.LENGTH_SHORT).show();
                } else {
                    if (isMoney) {
                        rl_money.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_money.setTextColor(getResources().getColor(R.color.text_30));
                        isMoney = false;
                        et_zfmoney.setText("");
                        money=0;
                        rl_pay_money.setVisibility(View.GONE);
                    } else {
                        rl_money.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tv_money.setTextColor(getResources().getColor(R.color.white));
                        rl_pay_money.setVisibility(View.VISIBLE);
                        isMoney = true;
                    }
                }
                break;
            case R.id.vip_rl_jiesuan:
                if (et_card.getText().toString().equals("")
                        || et_card.getText().toString() == null) {
                    Toast.makeText(getActivity(), "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                } else if (et_xfmoney.getText().toString().equals("")
                        || et_xfmoney.getText().toString() == null) {
                    Toast.makeText(getActivity(), "请输入消费金额",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_vipjf.getText().toString().equals("获取中")) {
                    Toast.makeText(getActivity(), "请重新输入会员卡号，获取会员信息",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_zhmoney.getText().toString().equals("获取中")) {
                    Toast.makeText(getActivity(), "请重新输入消费金额，获取折后金额",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getActivity())) {
                        Log.d("xxx",Double.parseDouble(tv_zhmoney.getText().toString())+";"+money+";"+yue+";"+dkmoney);
                        if (Double.parseDouble(tv_zhmoney.getText().toString()) - money - yue - dkmoney < 0) {
                            Toast.makeText(getActivity(), "超过折后金额，请检查输入信息",
                                    Toast.LENGTH_SHORT).show();
                        } else if (Double.parseDouble(tv_zhmoney.getText().toString()) - money - yue - dkmoney > 0) {
                            Toast.makeText(getActivity(), "少于折后金额，请检查输入信息",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            savaPayMsg();
                            jiesuan();
                        }
                    } else {
                        Toast.makeText(getActivity(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private VipPayMsg savaPayMsg() {
        VipPayMsg vpm=new VipPayMsg();
        if (et_jfmoney.getText().toString() == null||et_jfmoney.getText().toString().equals("")) {
            vpm.isJifen=0;
            vpm.jifenDkmoney="0";
            vpm.useJifen="0";
        }else{
            vpm.isJifen=1;
            vpm.jifenDkmoney=tv_dkmoney.getText().toString();
            vpm.useJifen= et_jfmoney.getText().toString();
        }
        if (et_yuemoney.getText().toString() == null||et_yuemoney.getText().toString().equals("")) {
            vpm.isYue=0;
            vpm.yueMoney="0";
        }else{
            vpm.isYue=1;
            vpm.yueMoney=et_yuemoney.getText().toString();
        }
        if (et_zfmoney.getText().toString() == null||et_zfmoney.getText().toString().equals("")) {
            vpm.isMoney=0;
            vpm.xjMoney="0";
        }else{
            vpm.isMoney=0;
            vpm.xjMoney= et_zfmoney.getText().toString();
        }
        vpm.zhMoney=tv_zhmoney.getText().toString();
        vpm.dataLength="0";
        double xx=Double.parseDouble(et_xfmoney.getText().toString());
        double zh=Double.parseDouble(tv_zhmoney.getText().toString());
        vpm.jieshengMoney=String.valueOf(CommonUtils.del(xx,zh));
        vpm.obtainJifen=tv_obtainjf.getText().toString();
        double yuemoney=0;
        if(et_yuemoney.getText().toString()==null||et_yuemoney.getText().toString().equals("")){
        }else{
            yuemoney=Double.parseDouble(et_yuemoney.getText().toString());
        }

        double sy= Double.parseDouble(PreferenceHelper.readString(getActivity(), "shoppay", "MemMoney","0"))-yuemoney;
        vpm.vipYue=String.valueOf(sy);
        vpm.vipSyJifen=String.valueOf(Double.parseDouble(tv_vipjf.getText().toString()) - jifen + Double.parseDouble(tv_obtainjf.getText().toString()));
       vpm.vipCard=et_card.getText().toString();
        vpm.vipName=tv_vipname.getText().toString();
        vpm.vipId=PreferenceHelper.readString(getActivity(), "shoppay", "memid", "123");
        vpm.xfMoney=et_xfmoney.getText().toString();
    return vpm;
    }

    private void jiesuan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Ismember", 1);//1为用户消费，0为散客消费
        params.put("memID", PreferenceHelper.readString(getActivity(), "shoppay", "memid", "123"));
        params.put("Point", tv_obtainjf.getText().toString());
        params.put("Money", et_xfmoney.getText().toString());
        params.put("discountmoney", tv_zhmoney.getText().toString());
        if (et_jfmoney.getText().toString() == null||et_jfmoney.getText().toString().equals("")) {
            params.put("bolIsPoint", 0);//1：真 0：假
            params.put("PointPayMoney", 0);
            params.put("UsePoint", 0);
        } else {
            params.put("bolIsPoint", 1);
            params.put("PointPayMoney", tv_dkmoney.getText().toString());
            params.put("UsePoint", et_jfmoney.getText().toString());
        }
        if (et_yuemoney.getText().toString() == null||et_yuemoney.getText().toString().equals("")) {
            params.put("bolIsCard", 0);//1：真 0：假
            params.put("CardPayMoney", 0);
        } else {
            params.put("bolIsCard", 1);
            params.put("CardPayMoney", et_yuemoney.getText().toString());
        }
        if (et_zfmoney.getText().toString() == null||et_zfmoney.getText().toString().equals("")) {
            params.put("bolIsCash", 0);//1：真 0：假
            params.put("CashPayMoney", 0);
        } else {
            params.put("bolIsCash", 1);
            params.put("CashPayMoney", et_zfmoney.getText().toString());
        }
        Gson gson=new Gson();
        Log.d("xx",params.toString());
        client.post(PreferenceHelper.readString(getActivity(), "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppShopFastExpense", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        PreferenceHelper.write(getActivity(), "shoppay","OrderAccount",jso.getJSONObject("data").getString("OrderAccount"));
                        Toast.makeText(getActivity(), "结算成功",
                                Toast.LENGTH_LONG).show();
                        if( PreferenceHelper.readBoolean(getActivity(),"shoppay","IsPrint",false)){
                            BluetoothUtil.connectBlueTooth(getActivity());
                            BluetoothUtil.sendData(printReceipt_BlueTooth(),  PreferenceHelper.readInt(getActivity(),"shoppay","FastExpenesPrintNumber",1));
                        }
                        ActivityStack.create().finishActivity(FastConsumptionActivity.class);

                    } else {
                        Toast.makeText(getActivity(), jso.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public byte[] printReceipt_BlueTooth()
    {
        String danhao = "消费单号:" + PreferenceHelper.readString(getActivity(), "shoppay","OrderAccount","");
        String huiyuankahao = "会员卡号:" + et_card.getText().toString();
        String huiyuanming = "会员名称:" +tv_vipname.getText().toString();

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
            byte[] xfmoney =( "消费金额:" +et_xfmoney.getText().toString()).getBytes("gb2312");
            byte[] hasjifen =( "获得积分:" +tv_obtainjf.getText().toString()).getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, xfmoney,nextLine,left,hasjifen};
            bytesList.add(ESCUtil.byteMerger(mticket1));


            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));

            byte[] yfmoney =( "应付金额:" +tv_zhmoney.getText().toString()).getBytes("gb2312");
            double xx=Double.parseDouble(et_xfmoney.getText().toString());
            double zh=Double.parseDouble(tv_zhmoney.getText().toString());
            Log.d("xxx",xx+";"+zh);

            byte[] jinshengmoney =("节省金额:" +Double.toString(CommonUtils.del(xx,zh))).getBytes("gb2312");

            byte[][] mticketsn = {nextLine,left,yfmoney,nextLine,left,jinshengmoney};
            bytesList.add(ESCUtil.byteMerger(mticketsn));
             if(isMoney){
                 byte[] moneys=( "现金支付:" +et_zfmoney.getText().toString()).getBytes("gb2312");
                 byte[][] mticketsm= {nextLine,left,moneys};
                 bytesList.add(ESCUtil.byteMerger(mticketsm));
             }
            if(isYue){
                byte[] yue=( "余额支付:" +et_yuemoney.getText().toString()).getBytes("gb2312");
                byte[][] mticketyue= {nextLine,left,yue};
                bytesList.add(ESCUtil.byteMerger(mticketyue));
            }
            if(isJifen){
                byte[] jifen=( "积分抵扣:" +tv_dkmoney.getText().toString()).getBytes("gb2312");
                byte[][] mticketjin= {nextLine,left,jifen};
                bytesList.add(ESCUtil.byteMerger(mticketjin));
            }
               double syjf = Double.parseDouble(tv_vipjf.getText().toString()) - jifen + Double.parseDouble(tv_obtainjf.getText().toString());
            Log.d("xxx",tv_vipjf.getText().toString()+";"+jifen+";"+et_jfmoney.getText().toString());
            byte[] syjinfen=( "剩余积分:" +syjf).getBytes("gb2312");
            byte[][] mticketsyjf= {nextLine,left,syjinfen};
            bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            double yuemoney=0;
            if(et_yuemoney.getText().toString()==null||et_yuemoney.getText().toString().equals("")){
            }else{
                yuemoney=Double.parseDouble(et_yuemoney.getText().toString());
            }
               if(isYue){
            double sy= Double.parseDouble(PreferenceHelper.readString(getActivity(), "shoppay", "MemMoney","0"))-yuemoney;
            byte[] shengyu=( "卡内余额:" +sy).getBytes("gb2312");
            byte[][] mticketsy= {nextLine,left,shengyu};
            bytesList.add(ESCUtil.byteMerger(mticketsy));
               }
            byte[] ha=( "操作人员："+ PreferenceHelper.readString(getActivity()
                    ,"shoppay","UserName","")).getBytes("gb2312");
            byte[] time=( "消费时间："+ getStringDate()).getBytes("gb2312");
            byte[] qianming=( "客户签名：").getBytes("gb2312");

            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            Log.d("xx","UnsupportedEncodingException");
        }
        return null;
    }
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        new CardOperationUtils(et_card);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        new CardOperationUtils().close();
//    }
}
