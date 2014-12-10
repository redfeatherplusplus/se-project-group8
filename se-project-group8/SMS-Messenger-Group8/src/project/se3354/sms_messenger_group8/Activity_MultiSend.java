package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
/**
 * 
 * @author Group8
 * 
 * Activity for sending a SMS to multiple phone numbers
 *
 */
public class Activity_MultiSend extends Activity {
	
	public static final int MAIN_ACTIVITY = 256;
	public static final int ACTIVITY_MULTISEND = 255;
	public static EditText phoneNoEdit;
	
	private Button btnAdd;
	private ListView lv;
	private Button btnSendMulti;
	private Button addFromContacts;
	private Button btnBack;
	private EditText txtMessage;
	ArrayList<String> list = new ArrayList<String>();
	ArrayAdapter<String> adapter;

	@Override
	/**
	 * Creates the view for the multisending function
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multisend);
		
		phoneNoEdit = (EditText)findViewById(R.id.editText);
		phoneNoEdit.setText(MainActivity.txtPhoneNo.getText().toString());
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);
	    // set the lv variable to your list in the xml
	    lv=(ListView)findViewById(R.id.list_multisend);  
	    lv.setAdapter(adapter);
	    btnSendMulti = (Button) findViewById(R.id.btnSendMulti);
	    addFromContacts = (Button) findViewById(R.id.addFromContacts);
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
	    /**
	     * Allows for the add from contacts feature
	     */
	    addFromContacts.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v)
	    	{
	    		Activity_Contacts.caller = ACTIVITY_MULTISEND;
            	Intent myIntent = new Intent(v.getContext(), Activity_Contacts.class);
            	startActivity(myIntent);
	    	}
	    });
	    
	    btnBack.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		finish();
	    	}
	    });
	    
	    btnSendMulti.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v)
	    	{
	    		String message = txtMessage.getText().toString();
	    		String phoneNumber ="";
	    	    if(list.size() > 0)//No less than one phone number is in list
	    	    {
	    	        for(int i=0; i<list.size(); i++){
	    	        	sendMultiSMS(list.get(i), message);
	    	        	phoneNumber=phoneNumber + "<"+list.get(i)+"> ";
	    	        }
	    	        Toast.makeText(getApplicationContext(), "Multiple SMS Sent",
	                		Toast.LENGTH_SHORT).show();
	    	      
	                
	            }
	    	    
                //Add time and date at the end
                Date resultdate = new Date(System.currentTimeMillis());
                MainActivity.txtReceive.setText("SMS sent to "+phoneNumber + ": "
                		+"\n"+"["+ message+"]\n"+ resultdate);
                MainActivity.txtPhoneNo.setText("");
                MainActivity.txtMessage.setText("");
                //After sending, go back to main layout---replaced by finish()
	    	    //Intent myIntent = new Intent(v.getContext(), MainActivity.class);
                //startActivityForResult(myIntent, 0);
                finish();
	    	}
	    });
	}
	/**
	 * Function for sending the SMS
	 * @param phoneNumber Of recipient
	 * @param message Message body
	 */
	public void sendMultiSMS(String phoneNumber, String message)
    {      	
		if (Utils.isDefaultSmsApp(this)) {
    		// Send SMS, and write in bottom TextView box
            try {
                SmsManager sms = SmsManager.getDefault();           
                sms.sendTextMessage(phoneNumber, null, message, PendingIntent.getBroadcast(
                        this, 0, new Intent("SMS_SENT"), 0), null);

                // manage "content://sms" since we are the default sms app
                saveUsersentSMS(phoneNumber, message);
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
        // if sms app is not the default app, send normally
        } else {
	    	// Send SMS, and write in bottom TextView box
	        try {
	            SmsManager sms = SmsManager.getDefault();           
	            sms.sendTextMessage(phoneNumber, null, message, PendingIntent.getBroadcast(
	                    this, 0, new Intent("SMS_SENT"), 0), null);
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
    }
	
	/**
	 * Saves  user sent messages to URI
	 * @param recipient
	 * @param body
	 */
	public void saveUsersentSMS(String recipient, String body) {
        Uri threadIdUri = Uri.parse("content://mms-sms/threadID");
        Uri.Builder builder = threadIdUri.buildUpon();
        builder.appendQueryParameter("recipient", recipient);
        Uri uri = builder.build();
        Long thread_id = get_thread_id(uri, recipient);

        ContentValues values = new ContentValues();
        values.put("address", recipient);
        values.put("body", body);
        values.put("date", System.currentTimeMillis());
        values.put("type", 2);
        values.put("thread_id", thread_id);
        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }
	
	/**
	 * Returns the thread ID from URI database
	 * @param uri
	 * @param recipient
	 * @return Thread ID value
	 */
	private Long get_thread_id(Uri uri, String recipient) {
        long threadId = 0;
        Cursor cursor = getContentResolver().query(uri, new String[] { "_id" },
                null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                	threadId = cursor.getLong(0);
                }
            } finally {
                cursor.close();
            }
        }
        return threadId;
    }
    
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
