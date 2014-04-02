package com.example.buzz;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class Home extends SherlockActivity implements OnClickListener {

	// Shared Preferences
	static final String PREFS = "shopdata";

	TextView shopname;
	UpdatePrices update = new UpdatePrices();
	private String SPData, dbdts, s_lTableName;
	ListView listviewmsa;
	TextView ShopHeader;

	

	ArrayList<MSAActions> MSAACtionArray = new ArrayList<MSAActions>();

	// private String PUSHBOTS_APPLICATION_ID="5218a8d14deeaecc06003302";

	//
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// Update prices
		setSupportProgressBarIndeterminateVisibility(true);
		new UpdatePrices().execute();

		MSAACtionArray.add(new MSAActions(R.drawable.ic_goshop, "Go Shopping"));
		MSAACtionArray.add(new MSAActions(R.drawable.ic_shoppinglist,
				"Shopping Lists"));
		MSAACtionArray
				.add(new MSAActions(R.drawable.ic_bestprice, "Best Price"));
		MSAACtionArray
				.add(new MSAActions(R.drawable.ic_promotion, "Promotions"));

		MSAActionsAdapter msadapter = new MSAActionsAdapter(this,
				R.layout.msaactions_row_item, MSAACtionArray);

		setContentView(R.layout.activity_home);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#ffffff'>Home</font>"));

		// retrieve from SP
		SharedPreferences dataStore = getSharedPreferences(PREFS, 0);
		s_lTableName = dataStore.getString("shop", "No Data Found");

		listviewmsa = (ListView) findViewById(R.id.actionslistView);
		ShopHeader = (TextView) findViewById(R.id.txtShopHeader);

		// Listview header
		View header = (View) getLayoutInflater().inflate(
				R.layout.store_header_row, null);
		ShopHeader = (TextView) findViewById(R.id.txtShopHeader);
		ShopHeader.setText(s_lTableName);

		listviewmsa.setAdapter(msadapter);

		// item listenerget selected store
		listviewmsa
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							View childview, int position, long id) {

						String l_SelectedAction = MSAACtionArray.get(position)
								.getAction();
						SelectedAction(l_SelectedAction);

					}

				});

		// Determine table to use
		if (s_lTableName.equals("SHOPRITE")) {
			s_lTableName = "ShopriteProducts";
		} else if (s_lTableName.equals("SPAR")) {
			s_lTableName = "SparProducts";
		} else if (s_lTableName.equals("Pick n Pay")) {
			s_lTableName = "PnPProducts";
		} else if (s_lTableName.equals("Melisa")) {
			s_lTableName = "MelisaProducts";
		} else if (s_lTableName.equals("Café bonjour")) {
			s_lTableName = "Bonjour";
		}
		
		
		

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, Supermarket.class));
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void SelectedAction(String Action) {

		if (Action.equals("Go Shopping")) {
			Intent intent = new Intent(this, Shop.class);
			startActivity(intent);
		} else if (Action.equals("Shopping Lists")) {
			Intent intent = new Intent(this, ShoppingList.class);
			startActivity(intent);
		} else if (Action.equals("Best Price")) {
			Intent intent = new Intent(this, BestPrice.class);
			startActivity(intent);
		} else if (Action.equals("Promotions")) {
			Intent intent = new Intent(this, Promotions.class);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*
		 * if (v == btnshoper) { Intent intent = new Intent(this, Shop.class);
		 * startActivity(intent); } else if (v == btnbudget) { Intent intent =
		 * new Intent(this, ShoppingList.class); startActivity(intent); } else
		 * if (v == btnpricecompare) { Intent intent = new Intent(this,
		 * BestPrice.class); startActivity(intent); }
		 */
	}

	private class UpdatePrices extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			SyncData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setSupportProgressBarIndeterminateVisibility(false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

	}

	public String SyncData() {

		DatabaseConnector dbcon = new DatabaseConnector(this);
		dbcon.open();

		// get date database last updated
		Cursor dts = dbcon.GetRegStatus();

		if (dts.moveToFirst())
			do {
				dbdts = dts.getString(dts.getColumnIndex("DbUpdateTime"));

			} while (dts.moveToNext());

		int x = 0;
		// TODO Auto-generated method stub
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("Dts", dbdts));

		String response = null;
		try {

			response = CustomHttpClient.executeHttpPost(
					"http://41.215.180.98/SyncData.php", postParameters);

			String res = response.toString();
			res = res.trim();
			res = res.replaceAll("\\s+", "");

			// check if anything returned by getting length of string
			x = res.length();

			if (x > 0) {

				// parse json data

				try {

					String returnString = "";
					JSONArray jArray = new JSONArray(res);

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json_data = jArray.getJSONObject(i);

						// Get an output to the screen

						String l_sProuctId = json_data.getString("ProductId");
						String l_sPrice = json_data.getString("Price");

						// insert values into table
						dbcon.UpdateProductPrices(l_sProuctId, l_sPrice,
								s_lTableName);

					}

				}

				catch (JSONException e) {

					Log.e("log_tag", "Error parsing data " + e.toString());

				}
			}

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection!!" + e.toString());

		}

		// Update the date that db has been updated
		dbcon.UpdateDbUpdate();
		dbcon.close();
		return response;
	}

}
