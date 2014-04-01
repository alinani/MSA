package com.example.buzz;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Shop extends SherlockActivity implements OnClickListener {

	DatabaseConnector dbcon = new DatabaseConnector(this);
	Button btnScan, btnClearList;
	ImageButton Imgbtnsave;
	String sCode, s_SubTotal, s_lpProdId,s_lTableName,s_lListStore;
	ListView itemList; 
	TextView txtSubTotal, ItemsList, txtQuantity,txtUnitPrice;
	Float l_SubTotal, l_fPrice;
	Integer prodrowid;
	
	//Shared Preferences
	static final String PREFS="shopdata";  

	Product prod = new Product();
	ProductCustomAdapter productAdapter;  
	ArrayList<Product> ProductArray = new ArrayList<Product>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Shopping</font>"));

		txtSubTotal = (TextView) findViewById(R.id.textSubTotal);
		ItemsList = (TextView) findViewById(R.id.textItemslist);
		txtQuantity = (TextView) findViewById(R.id.txtQty);
		txtUnitPrice=(TextView) findViewById(R.id.txtUnitPrice);
		Imgbtnsave = (ImageButton) findViewById(R.id.btnSave);
		Imgbtnsave.setOnClickListener(this);
		itemList = (ListView) findViewById(R.id.listItems);
		registerForContextMenu(itemList);
		SetScreenItems();  
		
		//retrieve from SP data table name
		SharedPreferences dataStore=getSharedPreferences(PREFS, 0);
		s_lTableName=dataStore.getString("shop", "No Data Found");
		s_lListStore=s_lTableName;
		if(s_lTableName.equals("SHOPRITE"))
		{
			s_lTableName="ShopriteProducts";
		}
		else if(s_lTableName.equals("SPAR"))
		{
			s_lTableName="SparProducts";
		}
		else if(s_lTableName.equals("Pick n Pay"))
		{
			s_lTableName="PnPProducts";
		}
		else if(s_lTableName.equals("Melisa"))
		{
			s_lTableName="MelisaProducts";
		}
		else if(s_lTableName.equals("Café bonjour"))
		{
			s_lTableName="Bonjour";
		}  
		 

	}

	public void getItemNum(Integer c) {
		ItemsList.setText(c.toString());
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
				ScannrDownload();
				
			}

			break;

		case R.id.clearlist:

			dbcon.DoClearList();
			SetScreenItems();

			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	//download scanner if doesn't exist
	private void ScannrDownload()
	{
		Intent intent = new Intent();
		intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/search?q=Zxing%20bar%20code%20scanner&c=apps&hl=en"));
		startActivity(intent);
	}
	

	// We want to create a context Menu when the user long click on an item
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

		// We know that each row in the adapter is a Map
		Product c = productAdapter.getItem(aInfo.position);

		s_lpProdId = c.getId();
		// String s_pname = c.getString(i_pname);

		// menu.setHeaderTitle(s_pname);
		menu.add(1, 1, 1, "Delete Item");
		menu.add(1, 2, 2, "Quantity");

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		// On long press selection
		// Delete item
		if (item.getTitle() == "Delete Item") {

			dbcon.DoDeleteCartItem(s_lpProdId);

		} else if (item.getTitle() == "Quantity") {

			// Reduce items
			EditNumberofItems(s_lpProdId);

		}
		SetScreenItems();
		return super.onContextItemSelected(item);

	}

	public void SetScreenItems() {
		String cnt;
		dbcon.open();
		String s_productname;
		// Declare a cursor to get retrieved data
		Cursor resProds = dbcon.Products();

		ProductArray.clear();
		if (resProds.moveToFirst()) {
			do {
				int i_prodrowid = resProds.getColumnIndex("_id");
				int i_productname = resProds.getColumnIndex("Product");
				int i_productid = resProds.getColumnIndex("ProductId");
				int i_productprice = resProds.getColumnIndex("Price");
				int i_productQty = resProds.getColumnIndex("Qty");

				prodrowid = resProds.getInt(i_prodrowid);
				s_productname = resProds.getString(i_productname);
				String I_productid = resProds.getString(i_productid);
				Integer I_productQauntity = resProds.getInt(i_productQty);
				l_fPrice = resProds.getFloat(i_productprice);

				/**
				 * add item in arraylist
				 */
				ProductArray.add(new Product(prodrowid, s_productname,
						I_productid.toString(), l_fPrice, I_productQauntity));

			} while (resProds.moveToNext());

		}

		/**
		 * set item into adapter
		 */
		cnt = dbcon.CountItems();
		ItemsList.setText(cnt);
		productAdapter = new ProductCustomAdapter(Shop.this,
				R.layout.product_row, ProductArray);

		itemList.setItemsCanFocus(false);

		itemList.setAdapter(productAdapter);

		// calculate sub total from arraylist
		int j = 0;
		Float RunTotal = (float) 0;
		Float Total = (float) 0;

		// Declare a cursor to get retrieved data
		Cursor resCartProd = dbcon.GetCartItems();
		if (resCartProd.moveToFirst()) {
			do {

				int i_productid = resCartProd.getColumnIndex("ProductId");
				int in_productQty = resCartProd.getColumnIndex("Qty");
				int in_productPrice = resCartProd.getColumnIndex("Price");

				Integer i_productQty = resCartProd.getInt(in_productQty);
				String s_productId = resCartProd.getString(i_productid);
				Float f_productPrice = resCartProd.getFloat(in_productPrice);

				RunTotal = f_productPrice * i_productQty;
				Total = Total + RunTotal;

			} while (resCartProd.moveToNext());

		}

		Double subTotal = (double) Total;
		DecimalFormat df = new DecimalFormat("###.##");

		s_SubTotal = df.format(subTotal);
		txtSubTotal.setText("K: "+s_SubTotal);

		// item listener to remove item from list
		itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View childview,
					int position, long id) {

				String selectedprodid = ProductArray.get(position).getId();

				// Dialog to enter number of items needed
				GetNumberofItems(selectedprodid);

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == Imgbtnsave) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Enter list name");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String listname = input.getText().toString();

							if (listname.equals("")) {

							} else {
								SaveList(listname);

							}

						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// do nothing
						}
					});
			alert.show();

		}

	}

	// Adding items dialog
	public void GetNumberofItems(final String Producid) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Quantity");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String numitems = input.getText().toString();

				if (numitems.equals("")) {

				} else {
					int numitemtoadd = Integer.parseInt(numitems);
					AddManyItemstoCart(Producid, numitemtoadd);
				}

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing
					}
				});
		alert.show();
	}

	// Reducing items dialog
	public void EditNumberofItems(final String Producid) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Quantity");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String numitems = input.getText().toString();

				if (numitems.equals("")) {

				} else {
					Integer iQty = Integer.parseInt(numitems);
					dbcon.UpdateCartProductQty(Producid, iQty);
					SetScreenItems();
				}

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing
					}
				});
		alert.show();
	}

	// This function adds a scanned item to the arraylist which will then be
	// saved to the shopping cart table
	public void AddItemtoCart(String s_ProductId) {

		// check if the scanned item exists in the cart already
		Cursor cartres = dbcon.GetCartItem(s_ProductId);

		if (cartres.getCount() > 0) {
			cartres.moveToFirst();
			int i_productid = cartres.getColumnIndex("ProductId");
			int in_productQty = cartres.getColumnIndex("Qty");

			Integer i_productQty = cartres.getInt(in_productQty);
			String s_productId = cartres.getString(i_productid);

			// add one more to product quantity
			i_productQty = i_productQty + 1;

			dbcon.UpdateCartProductQty(s_productId, i_productQty);
		} else {
			// else if its a new item insert it into cart table
			Cursor res = dbcon.GetItem(s_ProductId,s_lTableName);

			int x = res.getCount();
			if (x > 0) {
				res.moveToFirst();
				int i_productid = res.getColumnIndex("ProductId");
				int i_productname = res.getColumnIndex("Name");
				int i_price = res.getColumnIndex("Price");

				String s_productname = res.getString(i_productname);
				String s_productId = res.getString(i_productid);
				Float l_Price = res.getFloat(i_price);

				dbcon.AddtoCart(s_productId, s_productname, l_Price);
				// ProductArray.add(new Product(s_productname,
				// s_productId,l_Price));
			} else {
				Toast.makeText(this, "Item not found", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	// This function adds an item multiple times to the list and cart table
	public void AddManyItemstoCart(String s_ProductId, Integer numitems) {

		Cursor cartres = dbcon.GetCartItem(s_ProductId);

		cartres.moveToFirst();
		int i_productid = cartres.getColumnIndex("ProductId");
		int in_productQty = cartres.getColumnIndex("Qty");

		Integer i_productQty = cartres.getInt(in_productQty);
		String s_productId = cartres.getString(i_productid);

		// add one more to product quantity
		i_productQty = numitems;
		dbcon.UpdateCartProductQty(s_productId, i_productQty);

		SetScreenItems();

	}


	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				sCode = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan

				AddItemtoCart(sCode);
				SetScreenItems();

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast toast = Toast.makeText(this, "Scan was Cancelled!",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();      
				

			}
		}
	}

	// Removes item from arraylist on button click, updates the sub total and
	// also number of items
	public void removeAtomPayOnClickHandler(View v) {
		Product itemToRemove = (Product) v.getTag();

		// remove item from array adapter
		productAdapter.remove(itemToRemove);

		// remove the item from cart table
		Integer productrowid = itemToRemove.getRowId();
		String ProdId = itemToRemove.getId();
		Cursor cartres = dbcon.GetCartItem(ProdId);
		cartres.moveToFirst();
		int i_productid = cartres.getColumnIndex("ProductId");
		int in_productQty = cartres.getColumnIndex("Qty");

		Integer i_productQty = cartres.getInt(in_productQty);
		String s_productId = cartres.getString(i_productid);

		// subtract one more to product quantity and if quantity become 0 the
		// delete product from cart
		i_productQty = i_productQty - 1;

		if (i_productQty == 0) {
			dbcon.DoDeleteCartItem(s_productId);
		} else {
			dbcon.UpdateCartProductQty(s_productId, i_productQty);
		}

		SetScreenItems();

	}

	// This function gets items from Cart into History table
	public void SaveList(String ListName) {
		
		
		
		//insert shopping list data
		dbcon.InsertShoppingList(ListName, s_SubTotal,s_lListStore);

		// Declare a cursor to get retrieved data
		Cursor resProds = dbcon.GetCartItems();

		ProductArray.clear();
		if (resProds.moveToFirst()) {
			do {

				int i_productname = resProds.getColumnIndex("Product");
				int i_productid = resProds.getColumnIndex("ProductId");
				int i_productprice = resProds.getColumnIndex("Price");
				int i_productQty = resProds.getColumnIndex("Qty");

				Integer i_Qty = resProds.getInt(i_productQty);
				String s_productname = resProds.getString(i_productname);
				String s_productid = resProds.getString(i_productid);
				l_fPrice = resProds.getFloat(i_productprice);

				// inserting items from cart into history table, these are the shopping list details connected via the ListName
				dbcon.InsertHistoryProducts(s_productid, s_productname,
						ListName, l_fPrice, i_Qty);

			} while (resProds.moveToNext());

		}

		SetScreenItems();

	}

}
