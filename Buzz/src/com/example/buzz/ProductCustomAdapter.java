package com.example.buzz;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProductCustomAdapter extends ArrayAdapter<Product> {

	protected static final String LOG_TAG = ProductCustomAdapter.class.getSimpleName();
	
	private List<Product> items;
	private int layoutResourceId;
	private Context context;

	public ProductCustomAdapter(Context context, int layoutResourceId, List<Product> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ProductHolder holder = null;

	
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

	
		holder = new ProductHolder();
		holder.productItem = items.get(position);
		holder.productqty = (TextView) row.findViewById(R.id.txtQty);
		holder.productname = (TextView) row.findViewById(R.id.txtproductrow);
		holder.price=(TextView) row.findViewById(R.id.txtUnitPrice);
		holder.removeProducttButton = (ImageButton)row.findViewById(R.id.btndelete);
		holder.removeProducttButton.setFocusable(false);
		
	   holder.removeProducttButton.setTag(holder.productItem);


		row.setTag(holder);
		
		
		Product itemrow=items.get(position);
		holder.productqty.setText(itemrow.getQty().toString());
		holder.productname.setText(itemrow.getProductName());
		holder.price.setText(itemrow.getPrice().toString());
		
		
		return row;
	}


	public static class ProductHolder {
		Product productItem;
		TextView productname;
		TextView productqty;
		TextView price;
		ImageButton removeProducttButton;
	}
	
	

}