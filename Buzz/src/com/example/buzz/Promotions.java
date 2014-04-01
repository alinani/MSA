package com.example.buzz;



import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Promotions extends SherlockActivity {

	/*Declarations*/
	DatabaseConnector dbcon=new DatabaseConnector(this);
	
	SimpleCursorAdapter data;
	
	ListView promoList;
	TextView NumMsg;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promotions);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Promotions</font>"));
		
		dbcon.open();
		promoList=(ListView) findViewById(R.id.PromoListview);
		NumMsg=(TextView) findViewById(R.id.txtNumMsg);
		setListViewItems();
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.promotions, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpTo(this, new Intent(this, Home.class));
			break;

		case R.id.clearshoppinglists:
		
           dbcon.DoDeletePromotions();
           setListViewItems();
         

			break;

		}
		return super.onOptionsItemSelected(item);
	}
	public void setListViewItems() {
		
	
		Cursor resMsg = dbcon.GetPromotionalmsg();
		
	
		// NumItems
		Integer NumMsges = resMsg.getCount();

		String[] from = new String[] { dbcon.KEY_PMSG, dbcon.KEY_PrID};
		int[] to = new int[] { R.id.txtpromomsg};

		// Now create an array adapter and set it to display using our row
		data = new SimpleCursorAdapter(this, R.layout.promotions_row,
				resMsg, from, to);

		promoList.setAdapter(data);
		
		//Also Set Count number of messages
		NumMsg.setText(NumMsges.toString());

	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbcon.close();
	}

}
