package com.shoppay.wynfc;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.bean.Dengji;
import com.shoppay.wynfc.bean.VipInfo;
import com.shoppay.wynfc.card.ReadCardOpt;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.PreferenceHelper;
import com.shoppay.wynfc.view.MyGridViews;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipRechargeNumActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_rechage, rl_money, rl_line;
    private EditText et_vipcard;
    private TextView tv_title, tv_money, tv_line, tv_vipname, tv_vipyue;
    private MyGridViews myGridViews;
    private Activity ac;
    private String state = "现金";
    private String editString;
    private Dialog dialog;
    private List<Dengji> list;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    tv_vipname.setText(info.MemName);
                    tv_vipyue.setText(info.MemMoney);
                    break;
                case 2:
                    tv_vipname.setText("获取中");
                    tv_vipname.setText("获取中");
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
        setContentView(R.layout.activity_viprecharge);
        ac = this;
        dialog = DialogUtil.loadingDialog(ac, 1);
        ActivityStack.create().addActivity(ac);
        initView();
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
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
                LogUtils.d("xxVipInfoE",new String(responseBody));
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
        myGridViews= (MyGridViews) findViewById(R.id.gridview);

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
            case R.id.viprecharge_rl_recharge:
                if (et_vipcard.getText().toString().equals("")
                        || et_vipcard.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        try {
                            saveVipCard();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.rl_money:
                rl_money.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_line.setBackgroundColor(getResources().getColor(R.color.white));
                tv_money.setTextColor(getResources().getColor(R.color.white));
                tv_line.setTextColor(getResources().getColor(R.color.text_30));
                state = "现金";
                break;
            case R.id.rl_line:
                rl_money.setBackgroundColor(getResources().getColor(R.color.white));
                rl_line.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_money.setTextColor(getResources().getColor(R.color.text_30));
                tv_line.setTextColor(getResources().getColor(R.color.white));
                state = "在线";
                break;

        }
    }

    private void saveVipCard() throws Exception {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("memCard", et_vipcard.getText().toString());//会员卡号
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemAdd", map, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxsaveVipCardS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        Toast.makeText(ac, "充值成功", Toast.LENGTH_LONG).show();
                        finish();
                    }else{

                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                        Toast.makeText(ac, "会员充值失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
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
        out = String.valueOf(x);
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
}
