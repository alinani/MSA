package com.example.buzz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

public class Information extends SherlockActivity {

	// Declarations
	ListView lvHelpitems;

	// Listview Adapter
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		// set action bar props
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1B75BB"))); 
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#ffffff'>Information</font>"));

		lvHelpitems = (ListView) findViewById(R.id.helpitemlistView);

		// initialise string array
		String[] aHelpItems = { "1. Select Supermarket", "2. How to Shop",
				"3. How to make a shopping list", "4. Search for Best Price"};
		// Adding items to listview
		adapter = new ArrayAdapter<String>(this, R.layout.help_item_row,
				R.id.txtHelpitem, aHelpItems);
		lvHelpitems.setAdapter(adapter);

		// item listener to remove item from list
		lvHelpitems
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						
						HelpSorter(position);

					}

				});

	}

	public void HelpSorter(int position) {
		switch (position) {
		case 0:
			Intent intent = new Intent(this, HelpStore.class);
			startActivity(intent);
			break;

		case 1:
			Intent intent1 = new Intent(this, HelpShopping.class);
			startActivity(intent1);
			break;

		case 2:
			Intent intent2 = new Intent(this, HelpShoppinglist.class);
			startActivity(intent2);
			break;

		case 3:
			Intent intent3 = new Intent(this, HelpBestprice.class);
			startActivity(intent3);
			break;

		default:

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.information, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, Supermarket.class));
			break;

		case R.id.unregister:

			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);
			ServerUtilities.unregister(Information.this, regId);

			break;

		}
		return super.onOptionsItemSelected(item);
	}

}
