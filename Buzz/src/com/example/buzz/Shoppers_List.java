package com.example.buzz;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Shoppers_List extends SherlockActivity {

	// objects declaration
	DatabaseConnector dbcon = new DatabaseConnector(this);
	SimpleCursorAdapter data;
	ListView shoppinglist;
	TextView ShoppinglistName,sQty;

	String ShoppersList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppers__list);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Shopping List</font>"));
		dbcon.open();
		shoppinglist = (ListView) findViewById(R.id.lsthistorylist);
		ShoppinglistName = (TextView) findViewById(R.id.shoppinglistname);
		
		ShoppersList = getIntent().getStringExtra("ShoppingListName");

		SetScreenItems(ShoppersList);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.shoppers__list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, ShoppingList.class));
			break;

		case R.id.uselist:

			UseShoppingList(ShoppersList);
			Intent intent=new Intent(Shoppers_List.this,Shop.class);
			startActivity(intent);

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void SetScreenItems(String listname) {

		// set shopping listname
		ShoppinglistName.setText(listname);

		// Declare a cursor to get retrieved data
		Cursor resProds = dbcon.GetHistoryItems(listname);
		
		int x=resProds.getCount();
		
		// NumItems
		resProds.moveToFirst();

		String[] from = new String[] {dbcon.KEY_HLQTY,dbcon.KEY_HLPRODUCTNAME,
				dbcon.KEY_HLPRICE, dbcon.KEY_HLID };
		int[] to = new int[] {R.id.txtHistQuantity, R.id.txtproductrow, R.id.textAmount };

		// Now create an array adapter and set it to display using our row
		data = new SimpleCursorAdapter(this, R.layout.shoppinglistproductrow,
				resProds, from, to);

		shoppinglist.setAdapter(data);

		// item listener to remove item from list
		shoppinglist
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							View childview, int position, long id) {

						TextView textView = (TextView) childview
								.findViewById(R.id.txtproductrow);

						textView.setTextColor(Color.parseColor("#FF0000"));

					}
				});

	}

	// This function gets list items in History folder and transforms a list
	// into a current list by adding items to Cart table
	public void UseShoppingList(String listname) {
		// Clear all items in Cart table if any exist.
		dbcon.DoClearList();

		// get items from history table
		Cursor resProds = dbcon.GetHistoryItems(listname);

		if (resProds.moveToFirst()) {
			do {

				int i_productname = resProds.getColumnIndex("Name");
				int i_productid = resProds.getColumnIndex("ProductId");
				int i_productprice = resProds.getColumnIndex("Price");
				int i_productQty = resProds.getColumnIndex("Qty");

				Integer I_productQty = resProds.getInt(i_productQty);
				String s_productname = resProds.getString(i_productname);
				String s_productid = resProds.getString(i_productid);
				Float l_fPrice = resProds.getFloat(i_productprice);

				/**
				 * add item in arraylist
				 */
				dbcon.AddtoCart(s_productid, s_productname, l_fPrice,I_productQty);

			} while (resProds.moveToNext());

		}

	}

}
