package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class Activity_MultiSend extends Activity {
	private Button btnAdd;
	private EditText phoneNoEdit;
	private ListView lv;
	private Button btnSendMulti;
	private Button btnBack;
	private EditText txtMessage;
	ArrayList<String> list = new ArrayList<String>();
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multisend);
		
		phoneNoEdit = (EditText)findViewById(R.id.editText);
		phoneNoEdit.setText(MainActivity.txtPhoneNo.getText().toString());
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);
	    // set the lv variable to your list in the xml
	    lv=(ListView)findViewById(R.id.list);  
	    lv.setAdapter(adapter);
	    btnSendMulti = (Button) findViewById(R.id.btnSendMulti);
	    btnBack = (Button) findViewById(R.id.btnBack);
		btnAdd = (Button)findViewById(R.id.addTaskBtn);
		txtMessage = (EditText)findViewById(R.id.txtMessage);
		txtMessage.setText(MainActivity.txtMessage.getText().toString());
		
	    btnAdd.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v)
	    	{
	    	    String input = phoneNoEdit.getText().toString();
	    	    if(input.length() > 0)
	    	    {
	    	        // add string to the adapter, not the listview
	    	        list.add(input);
	    	        adapter.notifyDataSetChanged();
	    	        phoneNoEdit.setText("");
	    	    }
	    	}
	    });
	    
	    btnBack.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v)
	    	{Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
	    	}
	    });
	    
	    btnSendMulti.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v)
	    	{
	    		String message = txtMessage.getText().toString();
	    		String phoneNumber = null;
	    	    if(list.size() > 0)//No less than one phone number is in list
	    	    {
	    	        for(int i=0; i<list.size(); i++){
	    	        	sendMultiSMS(list.get(i), message);
	    	        	phoneNumber=phoneNumber + "<"+list.get(i)+"> ";
	    	        }
	    	        Toast.makeText(getApplicationContext(), "Multiple SMS Sent",
	                		Toast.LENGTH_SHORT).show();
	    	      
	                
	            }
	    	    //After sending, go back to main layout
	    	    Intent myIntent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
              //Add time and date at the end
                Date resultdate = new Date(System.currentTimeMillis());
                MainActivity.txtReceive.setText("SMS sent to "+phoneNumber + ": "
                		+"\n"+"["+ message+"]\n"+ resultdate);
	    	}
	    });
	}
	
	public void sendMultiSMS(String phoneNumber, String message)
    {      	
        // Send SMS, and write in bottom TextView box
        try {
            SmsManager sms = SmsManager.getDefault();           
            sms.sendTextMessage(phoneNumber, null, message, null, null);
     
         } 
        // Not sure how to test exception
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),
            "SMS failed",
            	Toast.LENGTH_LONG).show();
            Date resultdate = new Date(System.currentTimeMillis());
            MainActivity.txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"+"\n"
            		+"["+ message+"]\n" +" !!!SMS failed\n"+resultdate);
            e.printStackTrace();
         }
    }

/*
	  
*/
    
    /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity__multi_send, menu);
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
	*/
}
