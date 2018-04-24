package com.shoppay.wynew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.wynew.R;
import com.shoppay.wynew.bean.VipList;

import java.util.ArrayList;
import java.util.List;

public class ViplistAdapter extends BaseAdapter {
	private Context context;
	private List<VipList> list;
	private LayoutInflater inflater;

	public ViplistAdapter(Context context, List<VipList> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<VipList>();
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
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_viplist, null);
			vh = new ViewHolder();
			vh.tv_phone = (TextView) convertView
					.findViewById(R.id.item_viplist_phone);
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.item_viplist_name);
			vh.tv_jifen = (TextView) convertView
					.findViewById(R.id.item_viplist_jifen);
			vh.tv_yue = (TextView) convertView
					.findViewById(R.id.item_viplist_yue);
			vh.tv_card = (TextView) convertView
					.findViewById(R.id.item_viplist_card);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final VipList vip = list.get(position);
		vh.tv_card.setText("会员卡号："+vip.MemCard);
		vh.tv_name.setText("会员姓名："+vip.MemName);
		vh.tv_phone.setText("手机号码："+vip.MemMobile);
		vh.tv_yue.setText("帐号余额："+vip.MemMoney);
		vh.tv_jifen.setText("帐号积分："+vip.MemPoint);
		return convertView;
	}

	class ViewHolder {
		TextView tv_name,tv_phone,tv_yue,tv_jifen,tv_card;
	}
}
