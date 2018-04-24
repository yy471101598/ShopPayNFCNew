package com.shoppay.wynew;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynew.bean.Dayin;
import com.shoppay.wynew.bean.Yuming;
import com.shoppay.wynew.http.RetrofitAPI;
import com.shoppay.wynew.tools.ActivityStack;
import com.shoppay.wynew.tools.CommonUtils;
import com.shoppay.wynew.tools.DialogUtil;
import com.shoppay.wynew.tools.ESCUtil;
import com.shoppay.wynew.tools.LogUtils;
import com.shoppay.wynew.tools.PreferenceHelper;
import com.shoppay.wynew.tools.SysUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private RelativeLayout rl_bang,rl_login;
    private EditText et_account,et_pwd,et_yuming;
    private TextView tv_yuming;
    private CheckBox cb;
    private Activity ac;
    private Dialog dialog;
    private ImageView img;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ac=this;
        ActivityStack.create().addActivity(ac);
        initView();
        if(PreferenceHelper.readBoolean(ac,"shoppay","remember",false)){
            cb.setChecked(true);
            et_account.setText(PreferenceHelper.readString(ac,"shoppay","account","123"));
            et_pwd.setText(PreferenceHelper.readString(ac,"shoppay","pwd","123"));
            et_yuming.setText(PreferenceHelper.readString(ac,"shoppay","bianhao",""));
        }
        dialog= DialogUtil.loadingDialog(ac,1);
        setimg();
        file = new File(Environment.getExternalStorageDirectory(),
                "error.log");
//        adapter=DBAdapter.getInstance(ac);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//
//                    insertShopCar();
//            }
//        }.start();
    }

    private void loadError(String s) {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("error",s);

        Log.d("xx",s);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=LogError", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                Log.d("xxLogS","sd");
                try {
                     file.delete();
                    Log.d("xxLogS",new String(responseBody,"UTF-8"));
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Log.d("xxLogE",error.getMessage());
            }
        });
    }

    private void setimg(){
        DisplayMetrics disMetrics = new DisplayMetrics();
           this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
       int width = disMetrics.widthPixels;
          int height = disMetrics.heightPixels;
           Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
          SysUtil.setImageBackground(bitmap,img,width,dip2px(ac,170));
    }


//    private void insertShopCar(){
//        //加入购物车
//        List<ShopCar> li = new ArrayList<ShopCar>();
//        for(int i=0;i<500;i++) {
//            ShopCar shopCar = new ShopCar();
//            shopCar.account = PreferenceHelper.readString(ac, "shoppay", "account", "123");
//            shopCar.count = 1;
//            shopCar.discount = "1";
//            shopCar.discountmoney = "1";
//            shopCar.point = 0;
//            shopCar.pointPercent = "0";
//            shopCar.goodsid = i+"";
//            shopCar.goodsclassid = "11111";
//            shopCar.goodspoint = 0;
//            shopCar.goodsType = "11111";
//            shopCar.price = "11111";
//            shopCar.shopname = "11111";
//            li.add(shopCar);
//        }
//        long time1=System.currentTimeMillis();
//        Log.d("xx1",time1+"");
//
//        adapter.updateListShopCar(li,"1");
//        long time2=System.currentTimeMillis();
//        Log.d("xx2",time2+"");
//        Log.d("xxend",time2-time1+"");
////		intent.putExtra("shopclass",shop.GoodsClassID);
////		intent.putExtra("num",num+"");
//    }
    public  int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    private void initView() {
        img= (ImageView) findViewById(R.id.imgview);
        rl_bang= (RelativeLayout) findViewById(R.id.login_rl_bang);
        rl_login= (RelativeLayout) findViewById(R.id.rl_login);
        et_account= (EditText) findViewById(R.id.et_login_phone);
        et_pwd= (EditText) findViewById(R.id.et_login_pwd);
        et_yuming= (EditText) findViewById(R.id.login_et_yuming);
        tv_yuming= (TextView) findViewById(R.id.login_tv_yuming);
        cb= (CheckBox) findViewById(R.id.login_cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d("xx","checked");
                    PreferenceHelper.write(ac,"shoppay","remember",b);
                }
            }
        });

        rl_login.setOnClickListener(this);
        rl_bang.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.login_rl_bang:
              if (et_yuming.getText().toString().equals("")
                      || et_yuming.getText().toString() == null) {
                  Toast.makeText(getApplicationContext(), "请输入域名编号",
                          Toast.LENGTH_SHORT).show();
              }
              else{
                  if (CommonUtils.checkNet(getApplicationContext())) {
                      obtainYuming();
                  } else {
                      Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                              Toast.LENGTH_SHORT).show();
                  }
              }
              break;
          case R.id.rl_login:
              if (et_account.getText().toString().equals("")
                      || et_account.getText().toString() == null) {
                  Toast.makeText(getApplicationContext(), "请输入账号",
                          Toast.LENGTH_SHORT).show();
              }else  if (et_pwd.getText().toString().equals("")
                      || et_pwd.getText().toString() == null) {
                  Toast.makeText(getApplicationContext(), "请输入密码",
                          Toast.LENGTH_SHORT).show();
              }else  if (tv_yuming.getText().toString().equals("")
                      || tv_yuming.getText().toString() == null) {
                  Toast.makeText(getApplicationContext(), "请先绑定域名",
                          Toast.LENGTH_SHORT).show();
              }
              else{
                  if (CommonUtils.checkNet(getApplicationContext())) {
                      login();
                      if(file.exists()){
                          if (Environment.getExternalStorageState().equals(
                                  Environment.MEDIA_MOUNTED)) {
                              try {
                                  FileInputStream inputStream = new FileInputStream(file);
                                  byte[] b = new byte[inputStream.available()];
                                  inputStream.read(b);
                                  loadError(new String(b));
                              } catch (Exception e) {
                              }
                          } else {
                              // 此时SDcard不存在或者不能进行读写操作的
                          }

                      }


//                      BlueUtil blueUtil=new BlueUtil();
//
//                      blueUtil.send(print_BlueTooth(),);
//                      BluetoothUtil.connectBlueTooth(ac);
//                      BluetoothUtil.sendData(print_BlueTooth());
                  } else {
                      Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                              Toast.LENGTH_SHORT).show();
                  }
              }
              break;

      }
    }

    public byte[] print_BlueTooth()
    {
//        String xiaofei = et_account.getText().toString();
//        String shifujine = et_pwd.getText().toString();
//        double a = Double.parseDouble(xiaofei);
//        double b = Double.parseDouble(shifujine);
        //优惠金额
//        double c = a - b;
        DecimalFormat df = new DecimalFormat("0.00");
        String price = "0.00";
        String danhao = "消费单号:" + "code";
        String huiyuankahao = "会员卡号:" + "card";
//        if (vipname == null | vipname.isEmpty()) vipname = "散客";
        String huiyuanming = "会员名称:" + "vip";
        String xiaofeijine = "消费金额:" + "xfmoney";
        String youhuijine = "优惠金额:" + "youhui";
        String huodejifen = "获得积分:" + "jifen";
        String yinfujine = "应付金额:" + "yfmoney";
        String sfje = "实付金额:" + "sfmoney";
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String date = "消费时间:" + sDateFormat.format(new java.util.Date());
        String creator = "操作人员:" + "handler";

        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            byte[] title = "标题".getBytes("gb2312");
            byte[] bottom = "谢谢惠顾！".getBytes("gb2312");
            byte[] tickname = "快速消费小票".getBytes("gb2312");
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");
            byte[] xiaofeinum = xiaofeijine.getBytes("gb2312");
            byte[] youhuinum = youhuijine.getBytes("gb2312");
            byte[] getjifen = huodejifen.getBytes("gb2312");
            byte[] shoulepay = yinfujine.getBytes("gb2312");
            byte[] factpay = sfje.getBytes("gb2312");
            byte[] user = creator.getBytes("gb2312");
            byte[] ordertime = date.getBytes("gb2312");
            byte[] customs = "客户签名:".getBytes("gb2312");
            byte[] boldOn = ESCUtil.boldOn();
            byte[] center = ESCUtil.alignCenter();
            byte[] boldOff = ESCUtil.boldOff();
            byte[] left = ESCUtil.alignLeft();
            boldOn = ESCUtil.boldOn();
            boldOff = ESCUtil.boldOff();
            next2Line = ESCUtil.nextLine(2);
            byte[] nextLine = ESCUtil.nextLine(1);
            nextLine = ESCUtil.nextLine(1);
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();
            byte[][] mticket = {nextLine, center, boldOn, title, boldOff, nextLine, left, tickname, nextLine, left, ordernum, nextLine, left,
                    vipcardnum, nextLine,
                    left, vipname, nextLine, xiahuaxian, nextLine, left, xiaofeinum, nextLine, left, youhuinum, nextLine, left, getjifen,
                    nextLine, xiahuaxian, nextLine, left, shoulepay, nextLine, left, factpay, nextLine,
                    left, user, nextLine, left, ordertime, nextLine, left, customs, next2Line, center, bottom, next4Line, breakPartial};

            return ESCUtil.byteMerger(mticket);

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void obtaindayin() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetSysParameter", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxdayinS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        Gson gson=new Gson();
                       Dayin dayin= gson.fromJson(jso.getString("data"), Dayin.class);
                        PreferenceHelper.write(ac,"shoppay","IsPrint",dayin.IsPrint);
                        PreferenceHelper.write(ac,"shoppay","PrintTitle",dayin.PrintTitle);
                        PreferenceHelper.write(ac,"shoppay","PrintFootNote",dayin.PrintFootNote);
                        PreferenceHelper.write(ac,"shoppay","GoodsExpenesPrintNumber",Integer.parseInt(dayin.GoodsExpenesPrintNumber));
                        PreferenceHelper.write(ac,"shoppay","CountExpenesPrintNumber",Integer.parseInt(dayin.CountExpenesPrintNumber));
                        PreferenceHelper.write(ac,"shoppay","FastExpenesPrintNumber",Integer.parseInt(dayin.FastExpenesPrintNumber));
                        PreferenceHelper.write(ac,"shoppay","RechargePrintNumber",Integer.parseInt(dayin.RechargePrintNumber));
                        PreferenceHelper.write(ac,"shoppay","RechargeCountPrintNumber",Integer.parseInt(dayin.RechargeCountPrintNumber));
                        PreferenceHelper.write(ac,"shoppay","IsChkPwd",dayin.IsChkPwd);
                    }else{
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
            }
        });
    }


    private void obtainYuming() {
      dialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://imp.zhiluo.cc/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI api=retrofit.create(RetrofitAPI.class);
        Map<String,Object> map=new HashMap<>();
        map.put("domainCode",et_yuming.getText().toString());
        Call<Yuming> call=api.obtainYuming(map);
        call.enqueue(new Callback<Yuming>() {
            @Override
            public void onResponse(Call<Yuming> call, Response<Yuming> response) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxYumingS",response.body().toString());
                    if(response.body().isSuccess()){
                        PreferenceHelper.write(ac,"shoppay","yuming",response.body().getData().get(0).getDomain());
                        tv_yuming.setVisibility(View.VISIBLE);
                        tv_yuming.setText(response.body().getData().get(0).getDomain());
                    }else{
                        tv_yuming.setVisibility(View.GONE);
                        Toast.makeText(ac,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ac,"绑定域名失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Yuming> call, Throwable t) {
                dialog.dismiss();
                LogUtils.d("xxYumingE",t.getMessage());
                Toast.makeText(ac,"绑定域名失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void login() {
        dialog.show();
   AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("userAccount",et_account.getText().toString());
        params.put("userPassword",et_pwd.getText().toString());
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppShopLogin", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                dialog.dismiss();
                try {
                    Log.d("xxLoginS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){

                        PreferenceHelper.write(ac,"shoppay","account",et_account.getText().toString());
                        PreferenceHelper.write(ac,"shoppay","pwd",et_pwd.getText().toString());
                        PreferenceHelper.write(ac,"shoppay","bianhao",et_yuming.getText().toString());
                        PreferenceHelper.write(ac,"shoppay","UserName",jso.getJSONObject("data").getString("UserName"));
                        PreferenceHelper.write(ac,"shoppay","UserShopID",jso.getJSONObject("data").getString("UserShopID"));
                        obtaindayin();
                    Intent intent=new Intent(ac,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    }else{
                        Toast.makeText(ac,jso.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(ac,"登录失败，请重新登录",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                dialog.dismiss();
                Log.d("xxLoginE",new String(responseBody));
                Toast.makeText(ac,"登录失败，请重新登录",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
