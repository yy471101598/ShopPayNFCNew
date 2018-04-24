package com.shoppay.wynew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.wynew.R;
import com.shoppay.wynew.bean.VipRecharge;

import java.util.ArrayList;
import java.util.List;

public class RechargeAdapter extends BaseAdapter {
	private Context context;
	private List<VipRecharge> list;
	private LayoutInflater inflater;

	public RechargeAdapter(Context context, List<VipRecharge> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<VipRecharge>();
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
			convertView = inflater.inflate(R.layout.item_rechage, null);
			vh = new ViewHolder();
			vh.tv_give = (TextView) convertView
					.findViewById(R.id.tv_give);
			vh.tv_money = (TextView) convertView
					.findViewById(R.id.tv_money);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final VipRecharge VipRecharge = list.get(position);
		vh.tv_money.setText(VipRecharge.getRechargeMoney());
		vh.tv_give.setText(VipRecharge.getGiveMoney());
		return convertView;
	}

	class ViewHolder {
		TextView tv_money,tv_give;
	}
}
