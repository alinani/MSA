package com.example.buzz;

import static com.example.buzz.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.buzz.CommonUtilities.EXTRA_MESSAGE;
import static com.example.buzz.CommonUtilities.SENDER_ID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gcm.GCMRegistrar;


public class Registration extends SherlockActivity implements OnClickListener {

	/** Declaration **/
	 //DB Object
    DatabaseConnector dbcon=new DatabaseConnector(this);
  
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector object
	ConnectionDetector cd;

	public static String UserEmail;

	// Interface Components
	Button btnRegister;
	EditText txtEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		//set action bar props
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1B75BB")));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Register Account</font>"));

		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);

		
		//open db connect
		dbcon.open();
		//connect detection
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(Registration.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			btnRegister.setEnabled(false);
			// stop executing code by return
			return;
		}

		// display msg
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

	
	}

	@Override
	public void onClick(View v) {
		
		
		UserEmail = txtEmail.getText().toString().trim();
		
		if(DoValidateEmail(UserEmail))
		{
		dbcon.UpdateRegStatus(UserEmail);
		//update database time
		dbcon.UpdateDbUpdate();
		// register with GCM MSA Server
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			
			
			// Registration is not present, register now with GCM
			RegisterAccount reg=new RegisterAccount();
			reg.execute();
			
			
			Intent intent=new Intent(Registration.this,Supermarket.class);
			startActivity(intent);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) { 

			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						// get sim serial number

						ServerUtilities.register(context, UserEmail,"MSA Shopper", regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
						
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

		}
		else
		{
			Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
			
		}
	}

	/**
	 * Registering with MSA GCM
	 * */
	
	class RegisterAccount extends AsyncTask<Void,Void,Void>
	{

		ProgressDialog PD ;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		
			PD = ProgressDialog.show(Registration.this, "Registering Account",
					"Please Wait.....");
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			GCMRegistrar.register(Registration.this, SENDER_ID);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			PD.dismiss();
		}


		/**
		 * Receiving push messages
		 * */
		
	}
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			//Toast.makeText(getApplicationContext(),
					//"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	public boolean DoValidateEmail(String pEmail)
	
	{
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(pEmail);
	    return matcher.matches();
	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//GCMRegistrar.onDestroy(this);
	}
}
