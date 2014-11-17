package project.se3354.sms_messenger_group8;

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ScheduleSend extends Activity {
	
	static EditText txtDay;
	static EditText txtHr;
	static EditText txtMin;
	static EditText txtSec;
	static TextView txtNumber;
	static TextView txtText;
	static TextView txtTime;
	Button btnBack;
	Button btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_send);
		
		txtDay = (EditText) findViewById(R.id.txtDay);
		txtHr = (EditText) findViewById(R.id.txtHr);
		txtMin = (EditText) findViewById(R.id.txtMin);
		txtSec = (EditText) findViewById(R.id.txtSec);
		txtNumber = (TextView) findViewById(R.id.txtNumber);
		txtText = (TextView) findViewById(R.id.txtText);
		txtTime = (TextView) findViewById(R.id.txtTime);
		
		btnBack = (Button) findViewById(R.id.button2);
		btnCancel = (Button) findViewById(R.id.button3);
		
		//Read number and text to Scheduled Send page
		txtNumber.setText(MainActivity.txtPhoneNo.getText().toString());
		txtText.setText(MainActivity.txtMessage.getText().toString());
		
		//Clean information in main page
		MainActivity.txtPhoneNo.setText(null);
        MainActivity.txtMessage.setText(null);
        
        btnBack.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		//Go back to previous page, keep old information
	    		finish();
	    	}
	    });
        
    }
	
	public void cancelAlarm(View v) {
		// create the AlarmManager object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// create an Intent and set the class which will execute when Alarm triggers
	    Intent intentAlarm = new Intent(this, Activity_AlarmReciever.class);
		alarmManager.cancel(PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		//Go back to clean main page
		Intent myIntent = new Intent(v.getContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
	}
	
	
	
    
	//android:onClick="scheduleAlarm"
	public void scheduleAlarm(View V)
    {	
		String days = txtDay.getText().toString();
		String hrs = txtHr.getText().toString();
		String mins = txtMin.getText().toString();
		String secs = txtSec.getText().toString();
		int day;
		int hr;
		int min;
		int sec;
		if (days.equals("")){
			day = 0;
		}else{
			day = Integer.parseInt(days);
		}
		if (hrs.equals("")){
			hr = 0;
		}else {
			hr = Integer.parseInt(hrs);
		}
		if (mins.equals("")){
			min = 0;
		}else {
			min = Integer.parseInt(mins);
		}
		if (secs.equals("")){
			sec = 0;
		}else {
			sec = Integer.parseInt(secs);
		}
		
            
        // The current time is in milliseconds 
        // i.e. 24*60*60*1000= 86,400,000 :  milliseconds in a day  
		Long timeDelay = (long) (day*24*60*60*1000 + hr*60*60*1000 + min*60*1000 + sec*1000);
        Long time = new GregorianCalendar().getTimeInMillis()+timeDelay;
        
        if (timeDelay<1000){
        	Toast.makeText(this, "At least 1 second, pleaase enter integer", Toast.LENGTH_LONG).show();
        }else{

	        // create an Intent and set the class which will execute when Alarm triggers
	        Intent intentAlarm = new Intent(this, Activity_AlarmReciever.class);
	        
	        // create the AlarmManager object
	        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	
	        //set the alarm for particular time
	        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
	        
	        // real sending time
	        Date sendTime = new Date (time);
	        //Toast.makeText(this, "Scheduled SMS after: "+days+" days "+hrs+" hours "+mins+" minutes "+secs+" seconds ", Toast.LENGTH_LONG).show();
	        Toast.makeText(this, "Scheduled SMS: \n" + sendTime, Toast.LENGTH_LONG).show();
	        
	        txtTime.setText("Scheduled SMS: \n"+sendTime);
	        
	      //Go back to clean main page
			Intent myIntent = new Intent(V.getContext(), Activity_ScheduleSend2.class);
	        startActivityForResult(myIntent, 0);
	        
	        finish();
        }
         
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity__schedule_send, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}


