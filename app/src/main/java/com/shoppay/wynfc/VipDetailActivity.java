package com.shoppay.wynfc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.shoppay.wynfc.bean.VipList;
import com.shoppay.wynfc.http.InterfaceBack;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DateUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.PreferenceHelper;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipDetailActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_change;
    private EditText et_vipname,et_phone;
    private TextView tv_title, tv_birthday, tv_leve, tv_endtime, tv_tjcard,tv_tjname;
    private TextView tv_vipname,tv_vipcard,tv_vipphone,tv_vipdjstate,tv_vipyuejf;
    private RadioGroup rg;
    private RadioButton rb_boy,rb_girl;
    private Context ac;
    private String type="男";
    private String editString;
    private VipList vipList;
    private Dialog dialog;
    private List<Dengji> list;
    private Dengji dengji;
    private String dengjiId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipdetail);
        ac = MyApplication.context;
        dialog = DialogUtil.loadingDialog(VipDetailActivity.this, 1);
        ActivityStack.create().addActivity(VipDetailActivity.this);
        vipList=(VipList) getIntent().getSerializableExtra("viplist");
        initView();
        vipDengjiList("no");
        showMsg();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
              RadioButton rb= (RadioButton) findViewById(  radioGroup.getCheckedRadioButtonId());
                type=rb.getText().toString();
            }
        });

    }

    private void showMsg() {
        et_vipname.setText(vipList.MemName);
        et_phone.setText(vipList.MemMobile);
        tv_birthday.setText(vipList.MemBirthday);
        tv_leve.setText(vipList.LevelName);
        tv_birthday.setText(vipList.MemBirthday);
        if(vipList.MemRecommendCard.equals("")){
            tv_tjcard.setText("无");
        }else {
            tv_tjcard.setText(vipList.MemRecommendCard);
        }
        if(vipList.MemRecommendName.equals("")){
            tv_tjname.setText("无");
        }else {
            tv_tjname.setText(vipList.MemRecommendName);
        }
        if(vipList.MemPastTime.equals("")){
            tv_endtime.setText("永久有效");
        }else {
            tv_endtime.setText(vipList.MemPastTime);
        }
      dengjiId=vipList.MemLevelID+"";
        if(vipList.MemSex==0){
            type="男";
            rb_girl.setChecked(true);
        }else{
            type="女";
            rb_boy.setChecked(true);
        }
        String vipstate="正常";
        switch (vipList.MemState){
            case 0:
                vipstate="正常";
                break;
            case 1:
                vipstate="锁定";
                break;
            case 2:
                vipstate="挂失";
                break;
        }
        tv_vipname.setText("会员姓名："+vipList.MemName);
        tv_vipcard.setText("会员卡号："+vipList.MemCard);
        tv_vipphone.setText("手机号码："+vipList.MemMobile);
        tv_vipdjstate.setText("会员等级："+vipList.LevelName+"   状态："+vipstate);
        tv_vipyuejf.setText("余额：¥"+vipList.MemMoney+"   积分："+vipList.MemPoint);
    }


    private void initView() {
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_change = (RelativeLayout) findViewById(R.id.detail_rl_change);
        et_vipname = (EditText) findViewById(R.id.detail_et_name);
        et_phone = (EditText) findViewById(R.id.detail_et_phone);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_birthday = (TextView) findViewById(R.id.detail_tv_birthday);
        tv_leve = (TextView) findViewById(R.id.detail_tv_leve);
        tv_endtime = (TextView) findViewById(R.id.detail_tv_endtime);
        tv_tjcard = (TextView) findViewById(R.id.detail_tv_tjcard);
        tv_tjname = (TextView) findViewById(R.id.detail_tv_tjname);

        tv_vipname = (TextView) findViewById(R.id.detail_tv_vipname);
        tv_vipcard = (TextView) findViewById(R.id.detail_tv_vipcard);
        tv_vipphone = (TextView) findViewById(R.id.detail_tv_vipphone);
        tv_vipdjstate= (TextView) findViewById(R.id.detail_tv_vipdjstate);
        tv_vipyuejf = (TextView) findViewById(R.id.detail_tv_vipyuejf);
        rg= (RadioGroup) findViewById(R.id.detail_rg);
        rb_boy= (RadioButton) findViewById(R.id.detail_rb_boy);
        rb_girl= (RadioButton) findViewById(R.id.detail_rb_girl);
        tv_title.setText("会员详细");


        rl_left.setOnClickListener(this);
        rl_change.setOnClickListener(this);
        tv_birthday.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
        tv_leve.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.detail_tv_birthday:
                DialogUtil.dateChoseDialog(VipDetailActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        tv_birthday.setText((String) response);
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_birthday.setText((String) msg);
                    }
                });
                break;
            case R.id.detail_tv_endtime:
                DialogUtil.dateChoseDialog(VipDetailActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        String data= DateUtils.timeTodata((String) response);
                        String cru=DateUtils.timeTodata(DateUtils.getCurrentTime_Today());
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
            case R.id.detail_tv_leve:
                if(list==null||list.size()==0){
                    vipDengjiList("yes");
                }else {
                    DialogUtil.dengjiChoseDialog(VipDetailActivity.this, list, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            dengji=(Dengji) response;
                            tv_leve.setText(dengji.LevelName);
                            dengjiId=dengji.LevelID;
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                }
                break;
            case R.id.detail_rl_change:
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
                break;

        }
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
                            DialogUtil.dengjiChoseDialog(ac, list, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dengji=(Dengji) response;
                                    tv_leve.setText(dengji.LevelName);
                                    dengjiId=dengji.LevelID;
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
    private void saveVipCard() throws Exception {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("MemID", vipList.MemID);
        map.put("MemName", et_vipname.getText().toString());
        if(type.equals("男")){
            map.put("MemSex", 0);
        }else {
            map.put("MemSex",1);
        }
        map.put("MemMobile", et_phone.getText().toString());
        map.put("MemLevelID",dengjiId);
        if(tv_endtime.getText().toString().equals("永久有效")) {
            map.put("MemPastTime", "");
        }else{
            map.put("MemPastTime", tv_endtime.getText().toString());
        }
        map.put("MemBirthday", tv_birthday.getText().toString());
        Log.d("xx",map.toString());
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppEditMem", map, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxsaveVipCardS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        Toast.makeText(ac, "修改成功", Toast.LENGTH_LONG).show();
                        finish();
                    }else{

                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                        Toast.makeText(ac, "修改失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
                    Toast.makeText(ac, "修改失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
