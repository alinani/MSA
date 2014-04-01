package com.example.buzz;

import java.util.List;

import com.example.buzz.StoreCustomAdapter.WeatherHolder;

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

public class MSAActionsAdapter extends ArrayAdapter<MSAActions> {

	Context context;
	int layoutResourceId;
	private List<MSAActions> items;

	public MSAActionsAdapter(Context context, int layoutResourceId,  List<MSAActions> items) {
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
			holder.HmeimgIcon = (ImageView) row.findViewById(R.id.actimgIcon);
			holder.HmetxtTitle = (TextView) row.findViewById(R.id.txtAction);
			
			 // Font path
	        String fontPath = "fonts/Face Your Fears.ttf";
	 
	      		

			row.setTag(holder);
		} else {
			holder = (WeatherHolder) row.getTag();
		}

		MSAActions act = items.get(position);
		holder.HmetxtTitle.setText(act.title);
		holder.HmeimgIcon.setImageResource(act.icon);

		return row;
	}

	static class WeatherHolder {
		ImageView HmeimgIcon;
		TextView HmetxtTitle;
	}

}
