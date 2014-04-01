package com.example.buzz;




import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreCustomAdapter extends ArrayAdapter<Store> {

	Context context;
	int layoutResourceId;
	private List<Store> items;

	public StoreCustomAdapter(Context context, int layoutResourceId,  List<Store> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		WeatherHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new WeatherHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			
			 // Font path
	        String fontPath = "fonts/Face Your Fears.ttf";
	 
	      		

			row.setTag(holder);
		} else {
			holder = (WeatherHolder) row.getTag();
		}

		Store store = items.get(position);
		holder.txtTitle.setText(store.title);
		holder.imgIcon.setImageResource(store.icon);

		return row;
	}

	static class WeatherHolder {
		ImageView imgIcon;
		TextView txtTitle;
	}
}
