package com.example.buzz;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;


public class CreatedListView extends SherlockActivity implements
		OnItemClickListener,OnClickListener {

	// Database connection
	DatabaseConnector dbcon;
	SimpleCursorAdapter data;
	
	//cursor holding shoppinglist items
	Cursor resProds;
	//Interface components
	ListView shoppinglist;
	TextView txtTotal;
	ImageButton btnDiscard,btnSavelist;
	private ActionMode mMode;
	
	//Shared Preferences
	static final String PREFS="shopdata";  
		
	long rowid;
	Float fRunTotal=(float)0,fListTotal=(float)0;
	String ShareShoppingList,ShoppingListName,AccEmail,s_lListStore,s_SubTotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_created_list_view);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Shopping List</font>"));
		
		shoppinglist = (ListView) findViewById(R.id.tempListview);
		txtTotal=(TextView) findViewById(R.id.txtListTotal);
		btnDiscard=(ImageButton)findViewById(R.id.btnDiscard);
		btnSavelist=(ImageButton)findViewById(R.id.btnSavelist);
		btnSavelist.setOnClickListener(this);
		btnDiscard.setOnClickListener(this);
		setListViewItems();
		MakeShareShoppingList();
		
		
		//get store name to use when saving shoppinglist
		//retrieve from SP data table name
		SharedPreferences dataStore=getSharedPreferences(PREFS, 0);
		s_lListStore=dataStore.getString("shop", "No Data Found");
				
	}
	
	public void RefreshActivity()
	{
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	
	public void setListViewItems()
	{
		
		dbcon = new DatabaseConnector(this);
		dbcon.open();
		Cursor resProds = dbcon.GetAllTempProducts();
		// NumItems

		Integer NumItemlist = resProds.getCount();

		String[] from = new String[] { dbcon.KEY_TPRODQTY, dbcon.KEY_TPRODNAME,
				dbcon.KEY_TPRODPRICE, dbcon.KEY_TID };
		int[] to = new int[] { R.id.txttempQty, R.id.txttempProduct,
				R.id.txttempPrice };

		// Now create an array adapter and set it to display using our row
		data = new SimpleCursorAdapter(this,
				R.layout.tempprod_list_item, resProds, from, to);
		shoppinglist.setAdapter(data);
		shoppinglist.setOnItemClickListener(this);
		setListTotal();  
		
	}

	//on initial load of list
	public void setListTotal()
	{
		fListTotal=(float)0;
		fRunTotal=(float)0;
		 Cursor c = dbcon.GetAllTempProdShare();
	        startManagingCursor(c);
	        
	        //Fetch product details
	        if (c.moveToFirst())
	        {
	         do{
	         
	       
	          Integer iQty = c.getInt(c.getColumnIndex("Quantity"));
	          Float fPrice = c.getFloat(c.getColumnIndex("Price"));
	          
	      	         
	          fRunTotal=iQty*fPrice;
	          fListTotal = fListTotal + fRunTotal;
	          
	         }  while (c.moveToNext());  
	         
	     	Double subTotal = (double) fListTotal;
			DecimalFormat df = new DecimalFormat("###.##");

			s_SubTotal = df.format(subTotal);
		    txtTotal.setText(fListTotal.toString());
	        
	        }
		
	}
	public void MakeShareShoppingList()
	{
		ShareShoppingList="MSA Shopping List\n\n";
		 Cursor c = dbcon.GetAllTempProdShare();
	        startManagingCursor(c);
	        
	        //Fetch Candidate Details
	        if (c.moveToFirst())
	        {
	        
	         do{
	         
	          String sProdName = c.getString(c.getColumnIndex("ProdName"));
	          Integer iQty = c.getInt(c.getColumnIndex("Quantity"));
	          Float fPrice = c.getFloat(c.getColumnIndex("Price"));
	          
	          //shopping list store at point of sharing with another app.
	          ShareShoppingList=ShareShoppingList+iQty+":  "+sProdName+"\n";
	       	          
	         }  while (c.moveToNext());  
	         ShareShoppingList=ShareShoppingList+"\n Sub Total : K"+fListTotal;
	        
	        }
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Notice how the ListView api is lame
		// You can use mListView.getCheckedItemIds() if the adapter
		// has stable ids, e.g you're using a CursorAdaptor
		rowid=id;
		if (mMode == null) {
			mMode = startActionMode(new ModeCallback());
		} else {
			if (mMode != null) {
			 mMode.finish();
			}
		}
	};

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private final class ModeCallback implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Create the menu from the xml file
			MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.contextual_actions, menu);
			return true;

		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Here, you can checked selected items to adapt available actions
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Destroying action mode, let's unselect all items

			   if (mode == mMode) {
	                mMode = null;
	            }
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			switch (item.getItemId()) {
            case R.id.cab_action_delete:
            	dbcon.DoDeleteItemFromTempProdList(rowid);
            	setListViewItems();
            	MakeShareShoppingList();
          
 
                mode.finish(); // Action picked, so close the CAB
                return true;
                
            case R.id.cab_action_editQty:
            	EditNumberofItems(rowid);
            	MakeShareShoppingList();
 
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
			

		}

	};
	
	//ABS Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.created_list_view, menu);
		
		MenuItem shareItem=(MenuItem) menu.findItem(R.id.action_share);
		
		ShareActionProvider mShare=(ShareActionProvider) shareItem.getActionProvider();
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, ShareShoppingList);
		
		mShare.setShareIntent(shareIntent);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, ChooseProducts.class));
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	// Edit item number dialog
		public void EditNumberofItems(long Id) {
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
						Integer Qty = Integer.parseInt(numitems);
						dbcon.UpdateTempProductQtyById(rowid, Qty);
						setListViewItems();
					
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

		//bottom menu actions
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v==btnDiscard)
			{
				dbcon.DoClearTempList();
				setListViewItems();
				
			}
			else if(v==btnSavelist)
			{
			  //Prompt to enter list name
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
		
		// This function gets items from TempProdList table into History table
		public void SaveList(String ListName) {
			
			ShoppingListName=ListName;
						
			dbcon.InsertShoppingList(ListName, fListTotal.toString(),s_lListStore);

			// Declare a cursor to get  data
			Cursor resProds = dbcon.GetAllTempProdShare();

			
			if (resProds.moveToFirst()) {
				do {


					String sProdId = resProds.getString(resProds.getColumnIndex("ProdId"));
					String sProdName = resProds.getString(resProds.getColumnIndex("ProdName"));
					Float sPrice = resProds.getFloat(resProds.getColumnIndex("Price"));
					Integer iQuantity = resProds.getInt(resProds.getColumnIndex("Quantity"));

					// inserting items from cart into history table
					dbcon.InsertHistoryProducts(sProdId, sProdName,
							ListName, sPrice, iQuantity);

				} while (resProds.moveToNext());
				resProds.close();
			}

			UploadList lp=new UploadList();
			lp.execute();
			Toast.makeText(this, "List "+ListName +" saved", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(this,ShoppingList.class);
			startActivity(intent);

		}
		
		private class UploadList extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				UploadData();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				dbcon.DoClearTempList();
			
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			
			}

		}
		public String UploadData() {
			String response = null;
			
			//get account name
			 Cursor rc=dbcon.GetRegStatus();
		     
			
		        if(rc.moveToFirst())
		        	do{
		        		AccEmail=rc.getString(rc.getColumnIndex("Email"));
		        		
		        	}while(rc.moveToNext());
		        

			// TODO Auto-generated method stub
			ArrayList<NameValuePair> ShoppingListpostParameters = new ArrayList<NameValuePair>();

			ShoppingListpostParameters.add(new BasicNameValuePair("CustomerId", AccEmail));
			ShoppingListpostParameters.add(new BasicNameValuePair("ListName", ShoppingListName));
			ShoppingListpostParameters.add(new BasicNameValuePair("SubTotal", fListTotal.toString()));
			
			//Load items in that shoppinglist
			Cursor res = dbcon.GetAllTempProdShare();
			if (res.moveToFirst()) {
				do {


					String sProdId = res.getString(res.getColumnIndex("ProdId"));   
					String sProdName = res.getString(res.getColumnIndex("ProdName"));
					Float sPrice = res.getFloat(res.getColumnIndex("Price"));
					Integer iQuantity = res.getInt(res.getColumnIndex("Quantity"));

					ShoppingListpostParameters.add(new BasicNameValuePair("ProdId[]", sProdId));
					ShoppingListpostParameters.add(new BasicNameValuePair("ProdName[]", sProdName));
					ShoppingListpostParameters.add(new BasicNameValuePair("Quantity[]", iQuantity.toString()));
					ShoppingListpostParameters.add(new BasicNameValuePair("Price[]", sPrice.toString()));

				} while (res.moveToNext());  
				res.close();
			}
			
			try {
				//"http://digitalpluszm.com/Buzz/insertShoppingList.php"
				response = CustomHttpClient.executeHttpPost(
						"http://41.215.180.98/insertShoppingList.php",
						ShoppingListpostParameters);
  
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection!!" + e.toString());

			}
				

			return response;  
		}

}
