package com.shoppay.wynew.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.wynew.BalanceActivity;
import com.shoppay.wynew.R;
import com.shoppay.wynew.bean.ShopClass;

import java.util.ArrayList;
import java.util.List;

public class LeftAdapter extends BaseAdapter {
	private Context context;
	private List<ShopClass> list;
	private LayoutInflater inflater;

	public LeftAdapter(Context context, List<ShopClass> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<ShopClass>();
		} else {
			this.list = list;
		}
		inflater = LayoutInflater.from(context);
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
		ViewHolder vh = null;
//		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_balanceleft, null);
			vh = new ViewHolder();
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.tv);
			vh.tv_num = (TextView) convertView
					.findViewById(R.id.tv_num);
			vh.rl_item = (RelativeLayout) convertView
					.findViewById(R.id.rl_item);
			vh.rl_num = (RelativeLayout) convertView
					.findViewById(R.id.rl_num);
			convertView.setTag(vh);
//		} else {
//			vh = (ViewHolder) convertView.getTag();
//		}
		if (position == BalanceActivity.mPosition) {
			vh.rl_item.setBackgroundResource(R.drawable.tongcheng_all_bg01);
		} else {
			vh.rl_item.setBackgroundColor(Color.parseColor("#f4f4f4"));
		}
		final ShopClass home = list.get(position);
		if(home.shopnum==null||home.shopnum.equals("0")||home.shopnum.equals("")){
			vh.rl_num.setVisibility(View.GONE);
		}else{
			vh.rl_num.setVisibility(View.VISIBLE);
			vh.tv_num.setText(home.shopnum);
		}
		vh.tv_name.setText(home.ClassName);
		return convertView;
	}

	class ViewHolder {
		TextView tv_name,tv_num;
		RelativeLayout rl_item,rl_num;
	}
}
