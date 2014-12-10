package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Scheduled sending 
 * @author Group 8
 *
 */
public class Activity_ScheduleSend2 extends Activity {
	
	static TextView textView2;
	Button btnBack;
	Button btnCancel;
	static TextView txtNumber;
	static TextView txtText;
	static TextView txtTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedulesend2);
				
		textView2 = (TextView) findViewById(R.id.textView2);
		btnBack = (Button) findViewById(R.id.button2);
		btnCancel = (Button) findViewById(R.id.button3);
		txtNumber = (TextView) findViewById(R.id.txtNumber);
		txtText = (TextView) findViewById(R.id.txtText);
		txtTime = (TextView) findViewById(R.id.txtTime);
		
		//Read number and text to Scheduled2 page
		txtNumber.setText(Activity_ScheduleSend.txtNumber.getText().toString());
		txtText.setText(Activity_ScheduleSend.txtText.getText().toString());
		//txtTime.setText (Activity_ScheduleSend.txtTime.getText().toString());
		textView2.setText (Activity_ScheduleSend.txtTime.getText().toString());
		

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




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity__schedule_send2, menu);
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
