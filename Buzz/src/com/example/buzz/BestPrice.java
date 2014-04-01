package com.example.buzz;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class BestPrice extends SherlockActivity implements OnClickListener {

	// objects declaration
	DatabaseConnector dbcon = new DatabaseConnector(this);
	String sCode, s_lTableName, sProductName, sProductId;
	Float PnPPrice, SparPrice, MelisaPrice, ShopritePrice, TempPrice;
	TextView txtBestPrice, txtLowestPriceStore, txtBestProd;
	AutoCompleteTextView txtCompare;
	ListView OtherPrices;
	Button btnCompare;
	SimpleCursorAdapter data;

	ArrayList<String> l_aAutoCompleteProducts = new ArrayList<String>();
	// Shared Preferences
	static final String PREFS = "shopdata";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_best_price);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#ffffff'>Best Price</font>"));
		txtBestPrice = (TextView) findViewById(R.id.txtBestPrice);
		txtLowestPriceStore = (TextView) findViewById(R.id.txtBestShop);
		txtCompare = (AutoCompleteTextView) findViewById(R.id.ProductautoComplete);
		txtBestProd = (TextView) findViewById(R.id.txtBestProduct);
		OtherPrices = (ListView) findViewById(R.id.listotherprices);

		btnCompare = (Button) findViewById(R.id.btnCompare);
		btnCompare.setOnClickListener(this);

		SharedPreferences dataStore = getSharedPreferences(PREFS, 0);
		s_lTableName = dataStore.getString("shop", "No Data Found");

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
		dbcon.open();
		DoPopulateProductArray(s_lTableName);

		// auto complete feature
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				l_aAutoCompleteProducts);
		txtCompare.setAdapter(adapter);
		// txtCompare.setTokenizer(new AutoCompleteTextView.CommaTokenizer());

	}

	private void SetScreenItems() {
		// TODO Auto-generated method stub
		Float fMinPrice=(float)0;
		String StoreName="",sid="";
		Cursor minprice=dbcon.GetMinimumPrice();
	
		//find minimum price
		if (minprice.moveToFirst()) {
			do {				
				fMinPrice = minprice.getFloat(minprice.getColumnIndex("Price"));			
			} while (minprice.moveToNext());

		}
		
		//get minimum price store details
		Cursor cBestPrice=dbcon.GetMinimumPriceStore(fMinPrice);
		
		if (cBestPrice.moveToFirst()) {
			do {
				StoreName = cBestPrice.getString(cBestPrice.getColumnIndex("StoreName"));	
				fMinPrice = cBestPrice.getFloat(cBestPrice.getColumnIndex("Price"));			
			} while (cBestPrice.moveToNext());

		}
		
		txtLowestPriceStore.setText(StoreName);
		txtBestPrice.setText("K "+fMinPrice.toString());
		txtBestProd.setText(sProductName);
		
		
		//set listview items to show performance or prices of other shops
		Cursor cprices=dbcon.GetOtherPrices(fMinPrice);
		
		String[] from = new String[] { dbcon.KEY_BSTORE, dbcon.KEY_BPRICE, dbcon.KEY_TID };
		int[] to = new int[] { R.id.txtBestShopOther, R.id.txtBestPriceOther};

		// Now create an array adapter and set it to display using our row
		data = new SimpleCursorAdapter(this,
				R.layout.otherprices_row, cprices, from, to);
		OtherPrices.setAdapter(data);
	
	}

	private void DoPopulateProductArray(String p_sTablname) {
		Cursor autores = dbcon.GetAllProducts(p_sTablname);

		if (autores.moveToFirst()) {
			do {
				String ProductName = autores.getString(autores
						.getColumnIndex("Name"));
				l_aAutoCompleteProducts.add(ProductName);
			} while (autores.moveToNext());

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.shop, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, Home.class));
			break;

		case R.id.add_item:

			try {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);

			} catch (ActivityNotFoundException anfe) {
				Log.e("onCreate", "Scanner Not Found", anfe);

			}

			break;

		case R.id.clearlist:

			dbcon.DoClearList();
			SetScreenItems();

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				sCode = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan

				ComparePrice(sCode);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast toast = Toast.makeText(this, "Scan was Cancelled!",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();

			}
		}
	}

	private void ComparePrice(String ProductCode) {
		String tblName = "", sStoreName = "";
		Float fPrice = (float) 0;

		//erase previous comparisons from table
		dbcon.DoDeleteCaparisonItems();
		
		for (int i = 0; i < 5; i++) {
			// Get Prices from different shops
			if (i == 0) {
				tblName = "SparProducts";
				sStoreName = "SPAR";
			} else if (i == 1) {
				tblName = "ShopriteProducts";
				sStoreName = "SHOPRITE";
			} else if (i == 2) {
				tblName = "PnPProducts";
				sStoreName = "Pick n Pay";
			} else if (i == 3) {
				tblName = "MelisaProducts";
				sStoreName = "Melisa";
			} else if (i == 4) {
				tblName = "Bonjour";
				sStoreName = "Café bonjour";
			}

			//get product
			Cursor cprice = dbcon.GetItem(ProductCode, tblName);

			//if product exists in that store then add to temp table
			if(cprice.getCount()>0)
			{
			if (cprice.moveToFirst()) {
				do {
					fPrice = cprice.getFloat(cprice.getColumnIndex("Price"));

				} while (cprice.moveToNext());

				dbcon.AddtoTempBestProd(ProductCode, sStoreName, fPrice);
			}
			}
			
		}

		SetScreenItems();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == btnCompare) {
			DoGetProductId();
			
			//hide soft keyboard
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	public void DoGetProductId() {
		sProductName = txtCompare.getText().toString();
		String ProdId;
		Cursor cr = dbcon.GetProdItembyName(sProductName, s_lTableName);
		int u = cr.getCount();

		if (cr.moveToFirst()) {

			do {
				ProdId = cr.getString(cr.getColumnIndex("ProductId"));

			} while (cr.moveToNext());

			//send to compare price function
			ComparePrice(ProdId);
		}

	}
}
