package com.example.buzz;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Supermarket extends SherlockActivity {

	// Shared Preferences id name
	static final String PREFS = "shopdata";
	ImageButton btnShoprite, btnSpar, btnMelisa, btnPnp;

	ListView StorelistView;
	ArrayList<Store> StoreArray = new ArrayList<Store>();
	
	// Alert dialog manager
		AlertDialogManager alert = new AlertDialogManager();

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
		
		Intent intent = new Intent(
				"com.google.zxing.client.android.SCAN");
		
		// check if scanner installed
		boolean scanstatus = isCallable(intent);

		// if no scanner found download scanner
		if (scanstatus == false) {
			
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setMessage("Barcode Scanner not detected, Click okay to download one");
			ad.setButton("okay", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();  
			        
			        //download scanner on ok press
			        Intent intent = new Intent();
					intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/search?q=Zxing%20bar%20code%20scanner&c=apps&hl=en"));
					startActivity(intent);
					
					
			    }
			});
			ad.show();
			
		}
		
	}

	
	private boolean isCallable(Intent intent) {  
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,   
        PackageManager.MATCH_DEFAULT_ONLY);  
        return list.size() > 0;  
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
