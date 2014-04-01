package com.example.buzz;

import static com.example.buzz.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.buzz.CommonUtilities.EXTRA_MESSAGE;
import static com.example.buzz.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

public class Supermarket extends SherlockActivity {

	// Shared Preferences id name
	static final String PREFS = "shopdata";
	ImageButton btnShoprite, btnSpar, btnMelisa, btnPnp;

	ListView StorelistView;
	ArrayList<Store> StoreArray = new ArrayList<Store>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_supermarket);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar()
				.setTitle(
						Html.fromHtml("<font color='#ffffff'>Mobile Shopping Assistant</font>"));
		StoreArray.add(new Store(R.drawable.ic_spar, "SPAR"));
		StoreArray.add(new Store(R.drawable.ic_shoprite, "SHOPRITE"));
		StoreArray.add(new Store(R.drawable.ic_pnp, "Pick n Pay"));
		StoreArray.add(new Store(R.drawable.ic_bonjour, "Café bonjour"));
		StoreArray.add(new Store(R.drawable.ic_melisa, "Melisa"));

		StoreCustomAdapter adapter = new StoreCustomAdapter(this,
				R.layout.store_row_item, StoreArray);

		StorelistView = (ListView) findViewById(R.id.storelistView);
		StorelistView.setAdapter(adapter);

		// item listenerget selected store
		StorelistView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							View childview, int position, long id) {

						String l_SelectedStore = StoreArray.get(position)
								.getStore();

						System.out.println(l_SelectedStore);

						// Dialog to enter number of items needed
						// Delare and set values in shared prefs.
						SharedPreferences dataStore = getSharedPreferences(
								PREFS, 0);
						Editor editor = dataStore.edit();

						editor.putString("shop", l_SelectedStore);
						editor.commit();

						GoHome();

					}

				});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.supermarket, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.info:
			Intent intent = new Intent(Supermarket.this, Information.class);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void GoHome() {
		Intent intent = new Intent(this, Home.class);
		startActivity(intent);

	}

}
