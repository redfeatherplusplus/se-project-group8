package project.se3354.sms_messenger_group8;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Conversation extends Activity {

	public static final String DRAFT = "3";
	public static final String USERSENT = "2";
	public static final String RECIEVED = "1";
	
	// Search EditText
    EditText inputSearch;
	Button btnReturn;
	ListView messagesList;
	
    /* Called when the activity is first created. */
	
	//////////////////////////
	// Create Contacts List //
	//////////////////////////
	
	//This is the Adapter being used to display the list's data
	private ContactsAdapter mAdapter;
	private ArrayList<MyMessage> smsList;
	
	//Create String Value of the Phone Number of other Person in Conversation
	private String convAddress;
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//get data from inbox
		convAddress = getIntent().getStringExtra("convAddress");
		
		// match views with their xml ids
		setContentView(R.layout.activity_conversation);
		messagesList = (ListView) findViewById(R.id.MessagesList);
	    btnReturn = (Button) findViewById(R.id.btnReturn);
	    inputSearch = (EditText) findViewById(R.id.inputSearch);
	    
		// Create a progress bar to display while the list loads
	    messagesList.setEmptyView(findViewById(R.id.loadingScreen));
	    
	    // fill the list with sms messages
	    smsList = new ArrayList<MyMessage>();
	    smsListGenerate();
	    
	    // Create a receiver to update the conversation
	    IntentFilter filterState = new IntentFilter("Conversation.updateActivity");
	    registerReceiver(new BroadcastReceiver(){
	        public void onReceive(Context context, Intent intent) {
	        	// update the inbox, save searchbox during update
		        String SearchBox = inputSearch.getText().toString();
	            mAdapter.clear();
	            smsListGenerate();
	            
	            // create a new message filter since the database changed
	            mAdapter = new ContactsAdapter(Activity_Conversation.this, 
	    				R.layout.message_layout, smsList);
	            mAdapter.notifyDataSetChanged();
	    		messagesList.setAdapter(mAdapter);
		        inputSearch.setText(SearchBox);
	        }
	    }, filterState);
		
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new ContactsAdapter(Activity_Conversation.this, 
				R.layout.message_layout, smsList);
		messagesList.setAdapter(mAdapter);
		
		/* Action when click on Contact Item */
		messagesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//do something, maybe implement deleting messages?
			}
		}); 
		
		//Search function removed from conversation view
		
	    /* Action when click "Return" button */
	    btnReturn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	        }
	    });
		
	}
	
	public void smsListGenerate() {
		// Create a uri to get all sms messages
	    Uri smsURI = Uri.parse("content://sms");
	    Cursor c= getContentResolver().query(smsURI, null, null ,null,null);
	    startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
        	String messageType;
            for(int i=0; i < c.getCount(); i++) {
                MyMessage sms = new MyMessage();	//Create new message to be populated
                //Create variable to store address of current message being queried
                //String address = (c.getString(c.getColumnIndexOrThrow("address")).toString());		
                messageType = c.getString(c.getColumnIndexOrThrow("type")).toString();
                
                //create an sms with properly filled datafields
                if (DRAFT.equals(messageType)) {
                	// address is null for drafts, because of this we need to find the phone number
                	// by searching "content://mms-sms/canonical-addresses" with our thread_id
                	String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();
                	sms.setContactName(getAddressFromThreadID(thread_id));
                	
                	// date needs to be formatted from primitive long datatype
                	String messageDate = SimplifyDate(c.getLong(c.getColumnIndexOrThrow("date")));
	               	sms.setMessageDate(messageDate);
                	
	                sms.setMessageBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	                sms.setMessageType(messageType);
	               	sms.isDraft(true);
                } 
                else {
                	sms.setContactName(c.getString(c.getColumnIndexOrThrow("address")).toString());
	                
	                // date needs to be formatted from primitive long datatype
                	String messageDate = SimplifyDate(c.getLong(c.getColumnIndexOrThrow("date")));
	               	sms.setMessageDate(messageDate);
	               	
                	sms.setMessageBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	                sms.setMessageType(messageType);
                }
                //only add message if address is convAddress.
                if (sms.getContactName().equals(convAddress))
                {
                	smsList.add(sms);
                }
                
               	c.moveToNext();
           	}
       	}
       	c.close();
	}
	
	public String getAddressFromThreadID(String thread_id)
    {
		String address = "No Phone Number";
		
		// Create a URI to look for the matching _id
	    Uri conversationURI = Uri.parse("content://mms-sms/canonical-addresses");
	    Cursor cr = getContentResolver().query(conversationURI, null, null ,null,null);
	    startManagingCursor(cr);

        // Read the conversation data until a matching _id is found
        if(cr.moveToFirst()) {
            for(int i=0; i < cr.getCount(); i++) {
                MyMessage sms = new MyMessage();
                String conversation_id = cr.getString(cr.getColumnIndexOrThrow("_id")).toString();
                
                if (conversation_id.equals(thread_id)) {
                	// warning: address may be a string of multiple recipients seperated by spaces
                	String recipient_ids = cr.getString(cr.getColumnIndexOrThrow("address")).toString();
        			address = recipient_ids;
	                break;
                } 
                
               	cr.moveToNext();
           	}
       	}
       	cr.close();
       	
       	return (address);
    }
	
	public String SimplifyDate(Long Date)
    {
		String SimpleDate = "Feb 30";
		SimpleDateFormat month_day = new SimpleDateFormat("LLL W");
		SimpleDateFormat time_xm = new SimpleDateFormat("h:mm a");
		Date currentDate = new Date();
		Date messageDate = new Date(Date);
		
		// if the message was sent today, return the exact time it was sent
		if (month_day.format(messageDate).equals(month_day.format(currentDate))) {
			return (time_xm.format(messageDate));
		}
		
		return(month_day.format(messageDate));
    }

}
