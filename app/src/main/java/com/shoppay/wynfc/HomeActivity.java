package com.shoppay.wynfc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.shoppay.wynfc.adapter.HomeAdapter;
import com.shoppay.wynfc.bean.Home;
import com.shoppay.wynfc.bean.Login;
import com.shoppay.wynfc.http.RetrofitAPI;
import com.shoppay.wynfc.tools.ActivityStack;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.PreferenceHelper;
import com.shoppay.wynfc.tools.SysUtil;
import com.shoppay.wynfc.view.MyGridViews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class HomeActivity extends Activity {
    private MyGridViews gridViews;
    private List<Home> list;
    private HomeAdapter adapter;
    private Activity ac;
    private Dialog dialog;
    private long firstTime = 0;
    private ImageView img;
    private String AppAuthority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ac = this;
        dialog = DialogUtil.loadingDialog(ac, 1);
        gridViews = (MyGridViews) findViewById(R.id.gridview);
        img = (ImageView) findViewById(R.id.imgview);
        AppAuthority = getIntent().getStringExtra("AppAuthority");
        setimg();
        obtainHome();
        adapter = new HomeAdapter(ac, list);
        gridViews.setAdapter(adapter);
        gridViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                （会员办卡：1,；快速消费：2；商品消费：3；计次消费：4；会员充值：5；会员充次：6；会员列表：7；老板中心：8；退出：9；）
                switch (i) {
                    case 0:
                        if (AppAuthority.contains("1")) {
                            Intent intent = new Intent(ac, VipCardActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ac, "没有该权限", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        if (AppAuthority.contains("2")) {
                            Intent intent1 = new Intent(ac, FastConsumptionActivity.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(ac, "没有该权限", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if(AppAuthority.contains("3")){
                        Intent intent2 = new Intent(ac, BalanceActivity.class);
                        startActivity(intent2);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        if(AppAuthority.contains("4")){
                        Intent intent3 = new Intent(ac, NumConsumptionActivity.class);
                        startActivity(intent3);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        if(AppAuthority.contains("5")){
                        Intent intentre = new Intent(ac, VipRechargeActivity.class);
                        startActivity(intentre);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 5:
                        if(AppAuthority.contains("6")){
                        Intent intentrn = new Intent(ac, NumRechargeActivity.class);
                        startActivity(intentrn);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 6:
                        if(AppAuthority.contains("7")) {
                            Intent intentlist = new Intent(ac, VipListActivity.class);
                            startActivity(intentlist);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 7:
                        if(AppAuthority.contains("8")){
                        Intent intent4 = new Intent(ac, NewBossCenterActivity.class);
                        startActivity(intent4);
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 8:
                        if(AppAuthority.contains("9")){
                        outLogin();
                        }else{
                            Toast.makeText(ac,"没有该权限",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

    }

    private void setimg() {
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
        SysUtil.setImageBackground(bitmap, img, width, dip2px(ac, 230));
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void obtainHome() {
        list = new ArrayList<Home>();
        Home h1 = new Home();
        h1.name = "会员办卡";
        h1.iconId = R.drawable.vipcard;
        list.add(h1);
        Home h2 = new Home();
        h2.name = "快速消费";
        h2.iconId = R.drawable.fastpay;
        list.add(h2);
        Home h3 = new Home();
        h3.name = "商品消费";
        h3.iconId = R.drawable.shoppay;
        list.add(h3);
        Home h4 = new Home();
        h4.name = "计次消费";
        h4.iconId = R.drawable.numpay;
        list.add(h4);
        Home h7 = new Home();
        h7.name = "会员充值";
        h7.iconId = R.drawable.recharge;
        list.add(h7);
        Home h8 = new Home();
        h8.name = "会员充次";
        h8.iconId = R.drawable.numrecharge;
        list.add(h8);
        Home h9 = new Home();
        h9.name = "会员列表";
        h9.iconId = R.drawable.viplist;
        list.add(h9);
        Home h5 = new Home();
        h5.name = "老板中心";
        h5.iconId = R.drawable.boss;
        list.add(h5);
        Home h6 = new Home();
        h6.name = "退出";
        h6.iconId = R.drawable.out;
        list.add(h6);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long secndTime = System.currentTimeMillis();
            if (secndTime - firstTime > 3000) {
                firstTime = secndTime;
                Toast.makeText(ac, "再按一次退出", Toast.LENGTH_LONG)
                        .show();
            } else {
                ActivityStack.create().AppExit(ac);
            }
            return true;
        }
        return false;
    }

    private void outLogin() {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(PreferenceHelper.readString(ac, "shoppay", "yuming", "123")).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI api = retrofit.create(RetrofitAPI.class);
        Map<String, Object> map = new HashMap<>();
        Call<Login> call = api.outLogin(map);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxoutLoginS", response.body().toString());
                    if (response.body().isSuccess()) {
                        Intent intent = new Intent(ac, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ac, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("xxE", "exception");
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                dialog.dismiss();
                LogUtils.d("xxoutLoginE", t.getMessage());
            }
        });
    }

}
