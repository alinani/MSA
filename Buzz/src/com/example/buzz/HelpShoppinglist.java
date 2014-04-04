package com.example.buzz;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

public class HelpShoppinglist extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_shoppinglist);
		//set action bar props
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Make shopping list</font>"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.help_shoppinglist, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home: 
			Intent intent = new Intent(this, Information.class);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	
	

}
