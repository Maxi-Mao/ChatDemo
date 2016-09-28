package com.maxi.chatdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maxi.chatdemo.R;

public class DataAdapter extends BaseAdapter {

	private String[] title;
	private Context context;
	public DataAdapter(Context context,String[] title) {
		super();
		this.context = context;
		this.title = title;
	}

	@Override
	public int getCount() {
		return title.length;
	}

	@Override
	public Object getItem(int position) {
		return title[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.layout_mess_iv_listitem, null);
			holder.money_Tv = (TextView) convertView
					.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.money_Tv.setText(title[position]);
		return convertView;
	}

	class ViewHolder {

		TextView money_Tv;
	}
}
