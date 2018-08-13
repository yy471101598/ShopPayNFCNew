package com.shoppay.wynew;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import com.shoppay.wynew.bean.Dengji;
import com.shoppay.wynew.bean.VipInfo;
import com.shoppay.wynew.card.ReadCardOpt;
import com.shoppay.wynew.http.InterfaceBack;
import com.shoppay.wynew.tools.ActivityStack;
import com.shoppay.wynew.tools.CommonUtils;
import com.shoppay.wynew.tools.DateUtils;
import com.shoppay.wynew.tools.DialogUtil;
import com.shoppay.wynew.tools.LogUtils;
import com.shoppay.wynew.tools.PreferenceHelper;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipCardActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_save, rl_boy, rl_girl, rl_vipdj;
    private EditText et_vipcard, et_bmcard, et_vipname, et_phone, et_tjcard;
    private TextView tv_title, tv_boy, tv_girl, tv_vipsr, tv_vipdj, tv_tjname, tv_endtime;
    private Context ac;
    private String state = "男";
    private String editString;
    private Dialog dialog;
    private List<Dengji> list;
    private Dengji dengji;
    private static final DateFormat TIME_FORMAT = SimpleDateFormat
            .getDateTimeInstance();
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    tv_tjname.setText(info.MemName);
                    break;
                case 2:
                    tv_tjname.setText("获取中");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipcard);
        ac = MyApplication.context;
        dialog = DialogUtil.loadingDialog(VipCardActivity.this, 1);
        ActivityStack.create().addActivity(VipCardActivity.this);
        initView();
        resolveIntent(getIntent());

        // 获取默认的NFC控制器
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        //拦截系统级的NFC扫描，例如扫描蓝牙
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord("",
                Locale.ENGLISH, true) });

        vipDengjiList("no");

        et_tjcard.addTextChangedListener(new TextWatcher() {
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


    private NdefRecord newTextRecord(String text, Locale locale,
                                     boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(
                Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
                .forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
                textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);
    }


    //初步判断是什么类型NFC卡
    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent
                        .getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
                        empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
            // Setup the views
            Log.d("xxNFC",new Gson().toJson(msgs));
            NdefRecord record = msgs[0].getRecords()[0];
            String textRecord = parseTextRecord(record);
            Log.d("xxNFCMSG",textRecord);
        }
    }
    //一般公家卡，扫描的信息
    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    //显示NFC扫描的数据
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        // Parse the first message in the list
        // Build views for all of the sub records
        Date now = new Date();
//        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
//        final int size = records.size();
//        for (int i = 0; i < size; i++) {
//            TextView timeView = new TextView(this);
//            timeView.setText(TIME_FORMAT.format(now));
//            ParsedNdefRecord record = records.get(i);
//            promt.append(record.getViewText());
//        }
    }

    //获取系统隐式启动的
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
//        readNfcTag(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_vipcard);
        if (mAdapter == null) {
            if (!mAdapter.isEnabled()) {
               Toast.makeText(ac,"该设备不支持NFC功能",Toast.LENGTH_SHORT).show();
            }

            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(ac,"请在系统设置中先启用NFC功能",Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAdapter != null) {
            //隐式启动
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            //隐式启动
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
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
                        PreferenceHelper.write(ac, "shoppay", "memid", list.get(0).MemID + "");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", list.get(0).MemLevelID + "");
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                        PreferenceHelper.write(ac, "shoppay", "memid", list.get(0).MemID);
                    } else {
                        PreferenceHelper.write(ac, "shoppay", "memid", "");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(ac, "shoppay", "memid", "");
                    PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                PreferenceHelper.write(ac, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    private void initView() {


        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_save = (RelativeLayout) findViewById(R.id.vipcard_rl_save);
        rl_girl = (RelativeLayout) findViewById(R.id.rl_girl);
        rl_boy = (RelativeLayout) findViewById(R.id.rl_boy);
        rl_vipdj = (RelativeLayout) findViewById(R.id.vipcard_rl_chose);
        et_vipcard = (EditText) findViewById(R.id.vipcard_et_cardnum);
        et_bmcard = (EditText) findViewById(R.id.vipcard_et_kmnum);
        et_tjcard = (EditText) findViewById(R.id.vipcard_et_tjcard);
        et_vipname = (EditText) findViewById(R.id.vipcard_et_vipname);
        et_phone = (EditText) findViewById(R.id.vipcard_et_phone);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_boy = (TextView) findViewById(R.id.tv_boy);
        tv_girl = (TextView) findViewById(R.id.tv_girl);
        tv_vipsr = (TextView) findViewById(R.id.vipcard_tv_vipsr);
        tv_vipdj = (TextView) findViewById(R.id.vipcard_tv_vipdj);
        tv_tjname = (TextView) findViewById(R.id.vipcard_tv_tjname);
        tv_endtime = (TextView) findViewById(R.id.vipcard_tv_endtime);
        tv_title.setText("会员办卡");

        rl_left.setOnClickListener(this);
        rl_save.setOnClickListener(this);
        rl_boy.setOnClickListener(this);
        rl_girl.setOnClickListener(this);
        rl_vipdj.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
        tv_vipsr.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.vipcard_rl_save:
                if (et_vipcard.getText().toString().equals("")
                        || et_vipcard.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                }
//                else if (et_vipname.getText().toString().equals("")
//                        || et_vipname.getText().toString() == null) {
//                    Toast.makeText(getApplicationContext(), "请输入会员姓名",
//                            Toast.LENGTH_SHORT).show();
//                }
                else if (et_phone.getText().toString().equals("")
                        || et_phone.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入手机号码",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_vipdj.getText().toString().equals("请选择")) {
                    Toast.makeText(getApplicationContext(), "请选择会员等级",
                            Toast.LENGTH_SHORT).show();
                } else if (CommonUtils.isMobileNO(et_phone.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入正确的手机号码",
                            Toast.LENGTH_SHORT).show();
                } else {
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
            case R.id.vipcard_rl_chose:
                if (null == list || list.size() == 0) {
                    vipDengjiList("yes");
                } else {
                    DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            dengji = (Dengji) response;
                            tv_vipdj.setText(dengji.LevelName);
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                }
                break;
            case R.id.rl_boy:
                rl_boy.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_girl.setBackgroundColor(getResources().getColor(R.color.white));
                tv_boy.setTextColor(getResources().getColor(R.color.white));
                tv_girl.setTextColor(getResources().getColor(R.color.text_30));
                state = "男";
                break;
            case R.id.rl_girl:
                rl_boy.setBackgroundColor(getResources().getColor(R.color.white));
                rl_girl.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_boy.setTextColor(getResources().getColor(R.color.text_30));
                tv_girl.setTextColor(getResources().getColor(R.color.white));
                state = "女";
                break;
            case R.id.vipcard_tv_vipsr:
                DialogUtil.dateChoseDialog(VipCardActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        tv_vipsr.setText((String) response);
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_vipsr.setText((String) msg);
                    }
                });
                break;
            case R.id.vipcard_tv_endtime:
                DialogUtil.dateChoseDialog(VipCardActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        String data = DateUtils.timeTodata((String) response);
                        String cru = DateUtils.timeTodata(DateUtils.getCurrentTime_Today());
                        Log.d("xxTime", data + ";" + cru + ";" + DateUtils.getCurrentTime_Today() + ";" + (String) response);
                        if (Double.parseDouble(data) > Double.parseDouble(cru)) {
                            tv_endtime.setText((String) response);
                        } else {
                            Toast.makeText(ac, "过期时间要大于当前时间", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_endtime.setText((String) msg);
                    }
                });
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
//        map.put("memName", et_vipname.getText().toString());//会员姓名
        if (state.equals("男")) {
            map.put("memSex", 0);
        } else {
            map.put("memSex", 1);
        }
        map.put("memPhone", et_phone.getText().toString());
        map.put("memLeve", Integer.parseInt(dengji.LevelID));
        if (et_vipname.getText().toString().equals("")
                || et_vipname.getText().toString() == null) {
            map.put("memName", "");
        } else {
            map.put("memName", et_vipname.getText().toString());
        }
//        if (et_phone.getText().toString().equals("")
//                || et_phone.getText().toString() == null) {
//            map.put("memPhone", "");
//        } else {
//            map.put("memPhone", et_phone.getText().toString());
//        }
        if (et_bmcard.getText().toString().equals("")
                || et_bmcard.getText().toString() == null) {
            map.put("cardNumber", "");//卡面号码
        } else {
            map.put("cardNumber", et_bmcard.getText().toString());//卡面号码
        }
        if (tv_vipsr.getText().toString().equals("年-月-日")) {
            map.put("memBirthday", "");
        } else {
            map.put("memBirthday", tv_vipsr.getText().toString());
        }
        if (tv_tjname.getText().toString().equals("")
                || tv_tjname.getText().toString() == null) {
            map.put("memRecommendId", "");//推介人id
        } else {
            map.put("memRecommendId", Integer.parseInt(PreferenceHelper.readString(ac, "shoppay", "memid", "")));//推介人id
        }
        if (tv_endtime.getText().toString().equals("年-月-日")) {
            map.put("memPastTime", "");//过期时间
        } else {
            map.put("memPastTime", tv_endtime.getText().toString());//过期时间
        }
        LogUtils.d("xxparams", map.toString());
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppMemAdd", map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxsaveVipCardS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Toast.makeText(ac, "办卡成功", Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });


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

    //把字符串转为日期
    public static Date stringToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    private void vipDengjiList(final String type) {

        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        params.put("UserAcount", susername);
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetMemLevelList", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("xxLoginS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        String data = jso.getString("data");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Dengji>>() {
                        }.getType();
                        list = gson.fromJson(data, listType);
                        if (type.equals("no")) {

                        } else {
                            DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dengji = (Dengji) response;
                                    tv_vipdj.setText(dengji.LevelName);
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    } else {
                        if (type.equals("no")) {

                        } else {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (type.equals("no")) {

                    } else {
                        Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (type.equals("no")) {

                } else {
                    Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//
//    String mTagText;
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        //1.获取Tag对象
//        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        //2.获取Ndef的实例
//        Log.d("xxnfcm","nfc");
//        Ndef ndef = Ndef.get(detectedTag);
////        mTagText = ndef.getType() + "\nmaxsize:" + ndef.getMaxSize() + "bytes\n\n";
//        Log.d("xxnfc",new Gson().toJson(ndef));
//        readNfcTag(intent);
//    }
//
    /**
     * 读取NFC标签文本数据
     */
    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    Log.d("xxNFCMSG",textRecord);
//                    mTagText += textRecord + "\n\ntext\n" + contentSize + " bytes";
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     *
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
