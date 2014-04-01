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

public class ShoppingList extends SherlockActivity {

	DatabaseConnector dbcon = new DatabaseConnector(this);
	SimpleCursorAdapter data;
	ListView shoppinglist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Saved Shopping Lists</font>"));

		dbcon.open();
		shoppinglist = (ListView) findViewById(R.id.lstShoppingLists);
		

		//set listview item or load it
		setListViewItems();
		// item listener to remove item from list
		shoppinglist
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							View childview, int position, long id) {

						// When the gropu clicked group details fetched then
						// passed to next page where they are used to fetch a
						// list of users
						Cursor res = dbcon.GetSingleListDetails(id);
						res.moveToFirst();

						int i_listname = res.getColumnIndex("Name");

						String s_Listname = res.getString(i_listname);
						res.close();
						Intent intent = new Intent(ShoppingList.this,
								Shoppers_List.class);
						intent.putExtra("ShoppingListName", s_Listname);
						startActivityForResult(intent, 0);

					}
				});

	}

	public void setListViewItems() {
		Cursor resProds = dbcon.GetShoppingLists();
		// NumItems

		Integer NumItemlist = resProds.getCount();

		String[] from = new String[] { dbcon.KEY_SLSHOP,dbcon.KEY_SLNAME, dbcon.KEY_SLDATE,
				dbcon.KEY_SLSUBTOTAL, dbcon.KEY_SLID,dbcon.KEY_SLID };
		int[] to = new int[] { R.id.txtstoreName,R.id.textShoppingList, R.id.textDate,
				R.id.textTotal };

		// Now create an array adapter and set it to display using our row
		data = new SimpleCursorAdapter(this, R.layout.shoppinlist_row,
				resProds, from, to);

		shoppinglist.setAdapter(data);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.shopping_list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, Home.class));
			break;

		case R.id.newlist:

			Intent intent = new Intent(this, ChooseProducts.class);
			startActivity(intent);

			break;

		case R.id.clearshoppinglists:

			dbcon.DoClearShoppingLists();
			setListViewItems();

			break;

		}
		return super.onOptionsItemSelected(item);
	}

}
