package com.shoppay.wynew;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
                        PreferenceHelper.write(ac, "shoppay", "memid", list.get(0).MemID+"");
                        PreferenceHelper.write(ac, "shoppay", "vipdengjiid", list.get(0).MemLevelID + "");
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
                }
                else if (tv_vipdj.getText().toString().equals("请选择")) {
                    Toast.makeText(getApplicationContext(), "请选择会员等级",
                            Toast.LENGTH_SHORT).show();
                }
                else if(CommonUtils.isMobileNO(et_phone.getText().toString())){
                    Toast.makeText(getApplicationContext(), "请输入正确的手机号码",
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
            case R.id.vipcard_rl_chose:
                if(list==null||list.size()==0){
                    vipDengjiList("yes");
                }else {
                    DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            dengji=(Dengji) response;
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
                        String data=DateUtils.timeTodata((String) response);
                        String cru=DateUtils.timeTodata(DateUtils.getCurrentTime_Today());
                        Log.d("xxTime",data+";"+cru+";"+DateUtils.getCurrentTime_Today()+";"+(String) response);
                        if(Double.parseDouble(data)>Double.parseDouble(cru)){
                            tv_endtime.setText((String) response);
                        }else{
                            Toast.makeText(ac,"过期时间要大于当前时间",Toast.LENGTH_SHORT).show();
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
            map.put("memBirthday","");
        } else {
            map.put("memBirthday", tv_vipsr.getText().toString());
        }
        if (tv_tjname.getText().toString().equals("")
                || tv_tjname.getText().toString() == null) {
            map.put("memRecommendId", "");//推介人id
        } else {
            map.put("memRecommendId", Integer.parseInt( PreferenceHelper.readString(ac, "shoppay", "memid", "")));//推介人id
        }
        if (tv_endtime.getText().toString().equals("年-月-日")) {
            map.put("memPastTime", "");//过期时间
        } else {
            map.put("memPastTime", tv_endtime.getText().toString());//过期时间
        }
   LogUtils.d("xxparams",map.toString());
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
                        Toast.makeText(ac, "办卡成功", Toast.LENGTH_LONG).show();
                        finish();
                    }else{

                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                        Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
                    Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_vipcard);
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
    //把字符串转为日期
    public static Date stringToDate(String strDate) throws Exception
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    private void vipDengjiList(final String type) {

        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        params.put("UserAcount", susername);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetMemLevelList", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxLoginS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        String data=jso.getString("data");
                        Gson gson=new Gson();
                        Type listType = new TypeToken<List<Dengji>>(){}.getType();
                        list = gson.fromJson(data, listType);
                        if(type.equals("no")){

                        }else{
                            DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dengji=(Dengji) response;
                                    tv_vipdj.setText(dengji.LevelName);
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    }else{
                        if(type.equals("no")){

                        }else {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    if(type.equals("no")){

                    }else {
                        Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if(type.equals("no")){

                }else {
                    Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
