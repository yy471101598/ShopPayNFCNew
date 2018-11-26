package com.shoppay.wynfc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.wynfc.R;

import java.util.ArrayList;
import java.util.List;

public class BossAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private LayoutInflater inflater;

	public BossAdapter(Context context, List<String> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<String>();
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
			convertView = inflater.inflate(R.layout.item_boss, null);
			vh = new ViewHolder();
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.item_tv_boss);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final String home = list.get(position);
		vh.tv_name.setText(home);
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
	}
}
