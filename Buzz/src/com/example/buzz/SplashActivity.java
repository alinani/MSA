package com.example.buzz;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;

public class SplashActivity extends Activity {
	private long splashDelay = 3000; //3 seconds
	String Status;
	  DatabaseOpenHelper dbhelper = new DatabaseOpenHelper(this);
      
	  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
      
       // load database
     		try {
     			dbhelper.createDataBase();
     		} catch (IOException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}

        
        //Connect to db
        DatabaseConnector dbcon=new DatabaseConnector(this);
        
       //Open Database
        dbcon.open();
        
        //Get status
        Cursor sc=dbcon.GetRegStatus();
        
        if(sc.moveToFirst())
        	do{
        		Status=sc.getString(sc.getColumnIndex("Status"));
        		
        	}while(sc.moveToNext());
        
      		
        TimerTask task = new TimerTask()
        {

			@Override
			public void run() {
				finish();
				
				//if the status is 1 then it means user has registered before hence go straight to application else go to register page
				if(Status.equals("1"))
				{
					Intent mainIntent = new Intent().setClass(SplashActivity.this, Supermarket.class);
					startActivity(mainIntent);
				}
				else
				{
					
					Intent mainIntent = new Intent().setClass(SplashActivity.this, Registration.class);
					startActivity(mainIntent);
				}
				
			}
        	
        };
        
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);
    }
}