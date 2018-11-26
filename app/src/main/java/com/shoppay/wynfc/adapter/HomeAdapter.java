package com.shoppay.wynfc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoppay.wynfc.R;
import com.shoppay.wynfc.bean.Home;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends BaseAdapter {
	private Context context;
	private List<Home> list;
	private LayoutInflater inflater;

	public HomeAdapter(Context context, List<Home> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<Home>();
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
			convertView = inflater.inflate(R.layout.item_home, null);
			vh = new ViewHolder();
			vh.iv_img = (ImageView) convertView
					.findViewById(R.id.iv_item);
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.tv_item);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final Home home = list.get(position);
		vh.tv_name.setText(home.name);
		vh.iv_img.setBackgroundResource(home.iconId);
		return convertView;
	}

	class ViewHolder {
		ImageView iv_img;
		TextView tv_name;
	}
}
