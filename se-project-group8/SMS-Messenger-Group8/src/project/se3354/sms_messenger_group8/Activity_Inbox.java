package project.se3354.sms_messenger_group8;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.ActionBar.LayoutParams;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

public class Activity_Inbox extends Activity {
	
	public static final String DRAFT = "3";
	public static final String USERSENT = "2";
	public static final String RECIEVED = "1";
	
	Button btnReturn;
	ListView messagesList;
	
    /* Called when the activity is first created. */
	
	//////////////////////////
	// Create Contacts List //
	//////////////////////////
	
	//This is the Adapter being used to display the list's data
	ContactsAdapter mAdapter;
	
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
	    ContactsContract.Data.DISPLAY_NAME};
	
	// This is the select criteria
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		messagesList = (ListView) findViewById(R.id.MessagesList);
	    btnReturn = (Button) findViewById(R.id.btnReturn);
	    

		// Create a progress bar to display while the list loads
	    messagesList.setEmptyView(findViewById(R.id.loadingScreen));
	    
	    // Create a uri to get all sms messages
	    ArrayList<MyMessage> smsList = new ArrayList<MyMessage>();
	    Uri smsURI = Uri.parse("content://sms");
	    Cursor c= getContentResolver().query(smsURI, null, null ,null,null);
	    startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
        	String messageType;
            for(int i=0; i < c.getCount(); i++) {
                MyMessage sms = new MyMessage();
                messageType = c.getString(c.getColumnIndexOrThrow("type")).toString();
                if (DRAFT.equals(messageType)) {
                	// address is null for drafts, because of this we need to find the phone number
                	// by searching "content://mms-sms/canonical-addresses" with our thread_id
                	String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();
                	sms.setContactName(getAddressFromThreadID(thread_id));
                	
                	// date needs to be formatted from primitive long datatype
                	String messageDate = SimplifyDate(c.getLong(c.getColumnIndexOrThrow("date")));
	               	sms.setMessageDate(messageDate);
                	
	                sms.setMessageBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	               	sms.isDraft(true);
	               	smsList.add(sms);
                } 
                else {
	                sms.setContactName(c.getString(c.getColumnIndexOrThrow("address")).toString());
	                
	                // date needs to be formatted from primitive long datatype
                	String messageDate = SimplifyDate(c.getLong(c.getColumnIndexOrThrow("date")));
	               	sms.setMessageDate(messageDate);
	               	
                	sms.setMessageBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	               	smsList.add(sms);
                }

               	c.moveToNext();
           	}
       	}
       	c.close();
		
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new ContactsAdapter(Activity_Inbox.this, 
				R.layout.message_layout, smsList);
		messagesList.setAdapter(mAdapter);
		
		/* Action when click on Contact Item */
		messagesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//do something
			}
		}); 
		
	    /* Action when click "Return" button */
	    btnReturn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	        }
	    });
		
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
                	// address may be a string of multiple recipients seperated by spaces
                	String recipient_ids = cr.getString(cr.getColumnIndexOrThrow("address")).toString();
                	String[] recipients = recipient_ids.split(" ");
                	System.out.println("Test");
                	System.out.println(recipient_ids);
                	
                	// assume we want the first recipient
        			address = recipients[0];
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
		SimpleDateFormat time_xm = new SimpleDateFormat("h:m a");
		Date currentDate = new Date();
		Date messageDate = new Date(Date);
		
		// if the message was sent today, return the exact time it was sent
		if (month_day.format(messageDate).equals(month_day.format(currentDate))) {
			return (time_xm.format(messageDate));
		}
		
		return(month_day.format(messageDate));
    }
}
