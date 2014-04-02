package com.example.buzz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ChooseProducts extends SherlockActivity {

	// Database connection
	DatabaseConnector dbcon;

	// Shared Preferences
	static final String PREFS = "shopdata";

	// List view
	private ListView lv;

	// Listview Adapter
	ArrayAdapter<String> adapter;

	// Search EditText
	EditText inputSearch;

	// Variable Declaration
	String l_sProductName, l_sProductID, s_lTableName, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_products);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Make Shopping List</font>"));
		

		dbcon = new DatabaseConnector(this);
		// retrieve from SP
		SharedPreferences dataStore = this.getSharedPreferences(PREFS, 0);
		s_lTableName = dataStore.getString("shop", "No Data Found");

		if (s_lTableName.equals("SHOPRITE")) {
			s_lTableName = "ShopriteProducts";
		} else if (s_lTableName.equals("SPAR")) {
			s_lTableName = "SparProducts";
		} else if (s_lTableName.equals("Pick n Pay")) {
			s_lTableName = "PnPProducts";
		} else if (s_lTableName.equals("Melisa")) {
			s_lTableName = "MelisaProducts";
		}
		else if(s_lTableName.equals("Café bonjour"))
		{
			s_lTableName="Bonjour";
		}
		dbcon.open();
		// Get Active PD Name
		Cursor pdc = dbcon.GetAllProducts(s_lTableName);

		final ArrayList<String> mArrayList = new ArrayList<String>();

		int x = pdc.getCount();

		if (pdc.moveToFirst()) {
			do {
				l_sProductID = pdc.getString(pdc.getColumnIndex("ProductId"));
				l_sProductName = pdc.getString(pdc.getColumnIndex("Name"));
				mArrayList.add(l_sProductName);

			} while (pdc.moveToNext());
		}

		lv = (ListView) findViewById(R.id.search_products_listview);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		// Adding items to listview
		adapter = new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.product_name, mArrayList);
		lv.setAdapter(adapter);

		// item listener to remove item from list
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				String ProductName = mArrayList.get(position);

				// ListView Clicked item value
				name = (String) lv.getItemAtPosition(position);
				ProductName = name;

				Cursor Res = dbcon.GetProdItembyName(ProductName, s_lTableName);
				Res.moveToFirst();
				int x = Res.getCount();

				FnDoDisplay();
				
				// Get indexes

				// Get String Values
				String l_sProdId = Res.getString(Res
						.getColumnIndex("ProductId"));
				String l_sProdName = Res.getString(Res.getColumnIndex("Name"));
				String l_sPrice = Res.getString(Res.getColumnIndex("Price"));

				// check if product exists in table already

				Cursor tmp = dbcon.GetTempProdItem(l_sProdName);
				tmp.moveToFirst();
				int cnt = tmp.getCount();

				if (cnt > 0) {
					// product already exists just update the quantity
					// Integer l_iQty=Integer.parseInt(l_sQty);

					int indexQty = tmp.getColumnIndex("Quantity");
					Integer l_iQty = tmp.getInt(indexQty);
					l_iQty = l_iQty + 1;

					dbcon.UpdateTempProductQtyByName(l_sProdName, l_iQty);
				} else {
					// insert new product to list
					dbcon.InsertTempProdList(l_sProdId, l_sProdName, l_sPrice);
				}

			}

			
		});
		
		/*  *//**
		 * Enabling Search Filter
		 * */
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				ChooseProducts.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.choose_products, menu);

		return true;
	}
	private void FnDoDisplay() {
		// TODO Auto-generated method stub
		Toast.makeText(this, name+" added to shopping list", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, ShoppingList.class));
			break;

	
		case R.id.itemnext:

			Intent intent=new Intent(this,CreatedListView.class);
			startActivity(intent);

			break;

		}
		return super.onOptionsItemSelected(item);
	}

}
