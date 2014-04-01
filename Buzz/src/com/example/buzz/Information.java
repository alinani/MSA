package com.example.buzz;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;




public class Information extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		//set action bar props
				getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
				getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Information</font>"));
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
