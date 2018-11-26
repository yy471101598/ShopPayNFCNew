package com.shoppay.wynfc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wynfc.R;
import com.shoppay.wynfc.bean.Shop;
import com.shoppay.wynfc.bean.ShopCar;
import com.shoppay.wynfc.bean.Zhekou;
import com.shoppay.wynfc.db.DBAdapter;
import com.shoppay.wynfc.tools.CommonUtils;
import com.shoppay.wynfc.tools.DialogUtil;
import com.shoppay.wynfc.tools.LogUtils;
import com.shoppay.wynfc.tools.PreferenceHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class NumRechargeAdapter extends BaseAdapter {
	private Context context;
	private List<Shop> list;
	private LayoutInflater inflater;
	private Intent intent;
	private Dialog dialog;
	private DBAdapter dbAdapter;
	public NumRechargeAdapter(Context context, List<Shop> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<Shop>();
		} else {
			this.list = list;
		}
		inflater = LayoutInflater.from(context);
		intent=new Intent("com.shoppay.wy.numberchange");
		dialog= DialogUtil.loadingDialog(context,1);
		dbAdapter=DBAdapter.getInstance(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder	vh;
			convertView = inflater.inflate(R.layout.item_balanceright, null);
	        vh = new ViewHolder();
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.item_tv_shopname);
			vh.tv_num = (TextView) convertView
					.findViewById(R.id.item_tv_num);
			vh.tv_money = (TextView) convertView
					.findViewById(R.id.item_tv_money);
			vh.img_add= (ImageView) convertView.findViewById(R.id.item_iv_add);
			vh.img_del= (ImageView) convertView.findViewById(R.id.item_iv_del);
			convertView.setTag(vh);
		final Shop home = list.get(position);
		vh.tv_name.setText(home.Name);
		vh.tv_money.setText(home.Price);
		ShopCar dbshop=dbAdapter.getShopCar(home.GoodsID);
		if(dbshop==null){

		}else{
			if(dbshop.count!=0) {
				vh.tv_num.setVisibility(View.VISIBLE);
				vh.img_del.setVisibility(View.VISIBLE);
				vh.tv_num.setText(dbshop.count+"");
			}
		}

		vh.img_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
						if(  !PreferenceHelper.readBoolean(context, "shoppay", "isInput", false)){
							Toast.makeText(context,"请输入会员卡号",Toast.LENGTH_SHORT).show();

						}else {
							if (PreferenceHelper.readBoolean(context, "shoppay", "isSuccess", false)) {
								int num = Integer.parseInt(vh.tv_num.getText().toString());
								if (num == 0) {
									vh.tv_num.setVisibility(View.VISIBLE);
									vh.img_del.setVisibility(View.VISIBLE);
								}
								ShopCar shopCar = dbAdapter.getShopCar(home.GoodsID);
								if (shopCar == null) {
									obtainShopZhekou(home);
									num = 1;
									vh.tv_num.setText(num + "");
								} else {
									num = num + 1;
//									if (Integer.parseInt(home.GoodsNumber) < num) {
//										num = num - 1;
//										Toast.makeText(context, "该商品的最大库存量为" + home.GoodsNumber, Toast.LENGTH_SHORT).show();
//									}
									vh.tv_num.setText(num + "");
									Zhekou zk = new Zhekou();
									zk.discount = shopCar.discount;
									zk.pointPercent = shopCar.pointPercent;
									insertShopCar(zk, home, num);
								}
							} else {
								Toast.makeText(context,PreferenceHelper.readString(context,"shoppay","viptoast","未查询到会员"),Toast.LENGTH_SHORT).show();
							}
						}
					}
		});
		vh.img_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int num=Integer.parseInt(vh.tv_num.getText().toString());
				num=num-1;
				if(num==0){
					vh.img_del.setVisibility(View.GONE);
					vh.tv_num.setVisibility(View.GONE);
				}
				vh.tv_num.setText(num+"");

					ShopCar shopCar=dbAdapter.getShopCar(home.GoodsID);
						Zhekou zk=new Zhekou();
						zk.discount=shopCar.discount;
						zk.pointPercent=shopCar.pointPercent;
						insertShopCar(zk,home,num);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv_name,tv_money,tv_num;
		ImageView img_add,img_del;
	}

	private void obtainShopZhekou(final Shop shop) {
		dialog.show();
		AsyncHttpClient client = new AsyncHttpClient();
		final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		RequestParams params = new RequestParams();
		params.put("memid",PreferenceHelper.readString(context, "shoppay", "memid", "123"));
		params.put("goodsid",shop.GoodsID);
		Log.d("xxx",params.toString());
		client.post( PreferenceHelper.readString(context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=APPGetGoodsPoints", params, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				try {
					dialog.dismiss();
					LogUtils.d("xxshopzkS",new String(responseBody,"UTF-8"));
					JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
					if(jso.getBoolean("success")){
						Gson gson = new Gson();
						Zhekou zk=gson.fromJson(jso.getString("data"), Zhekou.class);
						//加入购物车
				     insertShopCar(zk,shop,1);

					}else{
						Toast.makeText(context,"获取商品折扣失败",Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e){
					Toast.makeText(context,"获取商品折扣失败",Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				dialog.dismiss();
				LogUtils.d("xxshopzkE",new String(responseBody));
				Toast.makeText(context,"获取商品折扣失败",Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void insertShopCar(Zhekou zk,Shop shop,int num){
		//加入购物车
		List<ShopCar> li=new ArrayList<ShopCar>();
		ShopCar shopCar=new ShopCar();
		shopCar.account=PreferenceHelper.readString(context,"shoppay","account","123");
		shopCar.count=num;
			shopCar.discount = zk.discount;
			shopCar.discountmoney = Double.parseDouble(shop.Price) * num * Double.parseDouble(zk.discount) + "";
		  if(zk.pointPercent.equals("0")){
			  shopCar.point = 0;
			  shopCar.pointPercent = zk.pointPercent;
		  }else {
			  shopCar.point = CommonUtils.div(Double.parseDouble(shop.Price) * Double.parseDouble(zk.discount) * num, Double.parseDouble(zk.pointPercent), 2);
			  shopCar.pointPercent = zk.pointPercent;
		  }
		shopCar.goodsid=shop.GoodsID;
		shopCar.goodsclassid=shop.GoodsClassID;
		shopCar.goodspoint=0;
		shopCar.goodsType=shop.GoodsType;
		shopCar.price=shop.Price;
		shopCar.shopname=shop.Name;
		li.add(shopCar);
		dbAdapter.insertShopCar(li);
//		intent.putExtra("shopclass",shop.GoodsClassID);
//		intent.putExtra("num",num+"");
		context.sendBroadcast(intent);
	}
}
