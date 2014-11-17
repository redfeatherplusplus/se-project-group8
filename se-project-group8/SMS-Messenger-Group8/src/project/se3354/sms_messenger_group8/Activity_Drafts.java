package project.se3354.sms_messenger_group8;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.ActionBar.LayoutParams;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

public class Activity_Drafts extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<MyMessage>>{
	
	public static final String DRAFT = "3";
	public static final String USERSENT = "2";
	public static final String RECEIVED = "1";
	
	// Search EditText
    EditText inputSearch;
	Button btnReturn;
	ListView messagesList;
	
    /* Called when the activity is first created. */
	
	//////////////////////////
	// Create Contacts List //
	//////////////////////////
	
	//This is the Adapter being used to display the list's data
	private ContactsAdapter mAdapter = null;
	private ArrayList<MyMessage> smsList = null;
	
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
	    inputSearch = (EditText) findViewById(R.id.inputSearch);
	    
	    // Create a progress bar to display while the list loads
	    messagesList.setEmptyView(findViewById(R.id.loadingScreen));
	    
	    // fill the list with sms messages
	    smsList = new ArrayList<MyMessage>();
	    smsListGenerate();
		
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new ContactsAdapter(Activity_Drafts.this, 
				R.layout.message_layout, smsList);
		messagesList.setAdapter(mAdapter);
		
		/* Action when click on Draft Item */
		messagesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//set message body and phone number to the select drafts
				String address = smsList.get(position).getPhoneNumber();
				String body = smsList.get(position).getMessageBody();
				MainActivity.txtPhoneNo.setText(address);
				MainActivity.txtMessage.setText(body);
				
				//delete that draft and return to the main layout
				//main_activity will save the draft again if it is not sent or cleared
				//so we want to delete it to avoid creating duplicates
				String _id = smsList.get(position).getMessageId();
				Context context = view.getContext();
				deleteMessage(context, _id);
				finish();
			}
		}); 

	    /* Adding search functionality*/
		inputSearch.addTextChangedListener(new TextWatcher() {
	        
	        @Override
	        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
	            // When user changed the Text
	            mAdapter.getFilter().filter(cs.toString()); 
	        }
	         
	        @Override
	        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	                int arg3) {
	            // TODO Auto-generated method stub
	             
	        }
	         
	        @Override
	        public void afterTextChanged(Editable arg0) {
	            // TODO Auto-generated method stub                         
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
	    
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}
	
	public synchronized void smsListGenerate() {
		// Create a uri to get all sms messages
	    Uri smsURI = Uri.parse("content://sms");
	    Cursor c= getContentResolver().query(smsURI, null, null ,null,null);
	    startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
        	String messageType;
            for(int i=0; i < c.getCount(); i++) {
                MyMessage sms = new MyMessage();
                messageType = c.getString(c.getColumnIndexOrThrow("type")).toString();
                
                // get the thread id of this message in case conversation checking is needed
            	String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();
                
                if(DRAFT.equals(messageType)) {
                	// address is null for drafts, because of this we need to find the phone number
                	// by searching "content://mms-sms/canonical-addresses" with our thread_id
                	sms.setPhoneNumber(getAddressFromThreadID(thread_id));
                	
                	// date needs to be formatted from primitive long datatype
                	String messageDate = SimplifyDate(c.getLong(c.getColumnIndexOrThrow("date")));
	               	sms.setMessageDate(messageDate);
                	
	                sms.setMessageBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	                sms.setMessageId(c.getString(c.getColumnIndexOrThrow("_id")).toString());
	                sms.setMessageThreadId(thread_id);
	                sms.setMessageType(messageType);
	               	sms.isDraft(true);
	               	smsList.add(sms);
                } 
                
                //display phonenumber while contactName loads
                sms.setContactName(sms.getPhoneNumber());
               	c.moveToNext();
           	}
       	}
	}
	
	public void deleteMessage(Context context, String _id){
		 try {
		        Uri deleterUri = Uri.parse("content://sms");
		        Cursor c = context.getContentResolver().query(deleterUri,
		            new String[] { "_id", "thread_id", "address",
		                "person", "date", "body" }, null, null, null);

		        if (c != null && c.moveToFirst()) {
		            do {
		                String id = String.valueOf(c.getLong(0));

		                if (_id.equals(id)) {
		                    context.getContentResolver().delete(
		                        Uri.parse("content://sms/" + id), null, null);
		                }
		            } while (c.moveToNext());
		        }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
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
       	
       	return (address);
    }
	
	public String getContactName(String address) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));  
        Cursor cs = getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME},PhoneLookup.NUMBER+"='"+address+"'",null,null);

        if(cs.getCount()>0) {
        	cs.moveToFirst();
        	String ContactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        	return (ContactName);
        } 

        cs.close();
        return(address);
	}
	
	public String SimplifyDate(Long Date)
    {
		SimpleDateFormat month_day_year = new SimpleDateFormat("MMMMM d, yyyy");
		SimpleDateFormat month_day = new SimpleDateFormat("MMM d");
		SimpleDateFormat time_xm = new SimpleDateFormat("h:mm a");
		Date currentDate = new Date();
		Date messageDate = new Date(Date);
		
		// if the message was sent today, return the exact time it was sent
		if (month_day_year.format(messageDate).equals(month_day_year.format(currentDate))) {
			return (time_xm.format(messageDate));
		}
		return(month_day.format(messageDate));
    }
	
    public void stopLoading() {
    	getLoaderManager().getLoader(0).stopLoading();
    }
    
    public void restartLoading() {
    	getLoaderManager().restartLoader(0, null, this).forceLoad();
    }
	
	@Override
	public Loader<ArrayList<MyMessage>> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new ContactNameLoader(this, smsList);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<MyMessage>> loader, ArrayList<MyMessage> data) {
		// TODO Auto-generated method stub
        mAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<MyMessage>> loader) {
		// TODO Auto-generated method stub
        mAdapter.notifyDataSetChanged();
	}
}
