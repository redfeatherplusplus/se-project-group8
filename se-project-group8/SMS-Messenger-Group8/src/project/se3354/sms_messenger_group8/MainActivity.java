package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView; 
import android.widget.ToggleButton;
import android.provider.Contacts.Intents;
import android.provider.Telephony.Sms.Inbox;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.lang.Object;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity 
{
	public static final int MAIN_ACTIVITY = 256;
	public static final int ACTIVITY_MULTISEND = 255;
	public static EditText txtAutoReply;
	public static EditText txtPhoneNo;
	public static EditText txtMessage;
	
	Button btnFindContactNo;
	Button btnAddContact;
	Button btnAddMoreNo;
	Button btnSendSMS;
	Button btnScheduleSend;
	Button btnSaveDraft;
	Button btnOpenDraft;
	Button btnInbox;
	Button btnForward;
	Button btnReply;
	Button btnEdit;
	ToggleButton toggleBtnAutoReply;
	
	static TextView txtReceive;
	
	public static int autoReplyOn=0;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {   
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnFindContactNo = (Button) findViewById(R.id.btnFindContactNo); 
        btnAddMoreNo = (Button) findViewById(R.id.btnAddMoreNo);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnScheduleSend = (Button) findViewById(R.id.btnScheduleSend);
        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnSaveDraft = (Button) findViewById(R.id.btnSaveDraft);
        btnOpenDraft = (Button) findViewById(R.id.btnOpenDraft);
        btnForward = (Button) findViewById(R.id.btnForward);
        btnReply = (Button) findViewById(R.id.btnReply);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        
        txtReceive=(TextView)findViewById(R.id.txtReceive); //TextView box for newest message
        
        txtAutoReply = (EditText) findViewById(R.id.txtAutoReply);
        toggleBtnAutoReply = (ToggleButton) findViewById(R.id.toggleBtnAutoReply); // Auto_Reply button
        
        //register context menu for btnAddContact
        registerForContextMenu(findViewById(R.id.btnAddContact));
        
        /* Action when click "From Contacts" button */             
        btnFindContactNo.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
	    		Activity_Contacts.caller = MAIN_ACTIVITY;
            	Intent myIntent = new Intent(v.getContext(), Activity_Contacts.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        /* Action when click "Add as Contact" button */             
        btnAddContact.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
            	// check to see if there is a phone number to add as a contact
        		String phoneNo = txtPhoneNo.getText().toString();
        		
        		// open context menu if there is a phonenumber, else display warning
            	if (phoneNo.length()>0) {
            		openContextMenu(v);
            	}
            	else {
                	Toast.makeText(getBaseContext(), 
                        "Please put in the contact's phone number first", 
                        Toast.LENGTH_SHORT).show();
            	}
            }
        });
        
        /* Action when click "Add More" button */ 
        btnAddMoreNo.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
            	Intent myIntent = new Intent(v.getContext(), Activity_MultiSend.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        /* Action when click "Send" button */             
        btnSendSMS.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {            	
            	String phoneNo = txtPhoneNo.getText().toString();
            	String message = txtMessage.getText().toString();
            	
            	/* Error reports, when phone number or/and message is empty  */
                if (phoneNo.length()>0 && message.length()>0) {               
                    sendSMS(phoneNo, message);
                   
                    //Clear phone number box and message box after Sending
                    txtPhoneNo.setText(null);
                    txtMessage.setText(null);

                }
                else if (phoneNo.length()==0 && message.length()>0) {
                	Toast.makeText(getBaseContext(), 
                        "Phone number is missing", 
                        Toast.LENGTH_SHORT).show();
                }
                else if (phoneNo.length()>0 && message.length()==0) {
                	Toast.makeText(getBaseContext(), 
                        "Message cannot be empty", 
                        Toast.LENGTH_SHORT).show();
                }
                else {
                	Toast.makeText(getBaseContext(), 
                        "Both phone number and message are missing", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        /* Action when click "Send later" button */
        btnScheduleSend.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	
                String phoneNo = txtPhoneNo.getText().toString();
            	String message = txtMessage.getText().toString();
                if (phoneNo.length()>0 && message.length()>0) { 
                	//Get to new page
                	Intent myIntent = new Intent(v.getContext(), Activity_ScheduleSend.class);
                    startActivityForResult(myIntent, 0);
                                  

                }
                else if (phoneNo.length()==0 && message.length()>0) {
                	Toast.makeText(getBaseContext(), 
                        "Phone number is missing", 
                        Toast.LENGTH_SHORT).show();
                }
                else if (phoneNo.length()>0 && message.length()==0) {
                	Toast.makeText(getBaseContext(), 
                        "Message cannot be empty", 
                        Toast.LENGTH_SHORT).show();
                }
                else {
                	Toast.makeText(getBaseContext(), 
                        "Both phone number and message are missing", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        /* Action when click "Save Draft" button */             
        btnSaveDraft.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) { 
        		//if this is the default sms app we can save drafts
        		if (Utils.isDefaultSmsApp(v.getContext())) {
            		String phoneNo = txtPhoneNo.getText().toString();
                	String message = txtMessage.getText().toString();
                	//phone number has to be there before saving draft
                	if (phoneNo.length()>0) {               
                		Date resultdate = new Date(System.currentTimeMillis());
                        /*Write newly sent message in the TextView box*/
                        txtReceive.setText("Saved draft for SMS sending to " + "<" + phoneNo + "> : "
                        		+"\n"+"["+message+"]\n"+resultdate);
                        
                        //Clear phone number box and message box after Sending
                        txtPhoneNo.setText(null);
                        txtMessage.setText(null);
                        
                        saveDraft(phoneNo, message);
    	        		
    	        		//state that message was saved as a draft
                    	Toast.makeText(getBaseContext(), "Message saved as draft.", 
                        Toast.LENGTH_LONG).show();
                	}
                	//phone number has to be there, if not, report error
                	else {
                    	Toast.makeText(getBaseContext(), "Phone number cannot be empty", 
                        Toast.LENGTH_LONG).show();
                    }
        		}
        		else {
        			Toast.makeText(getBaseContext(), "Must be default SMS app save drafts", 
                    Toast.LENGTH_LONG).show();
        		}
            }
        });
        
        /* Action when click "Get into Inbox" button.--- Main layout-->Inbox layout----*/             
        btnInbox.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	Intent myIntent = new Intent(v.getContext(), Activity_Inbox.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        /* Action when click "Open Draft" button.--- Main layout-->Draft layout----*/             
        btnOpenDraft.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
        		//if this is the default sms app we can save drafts
        		if (Utils.isDefaultSmsApp(v.getContext())) {
                	Intent myIntent = new Intent(v.getContext(), Activity_Drafts.class);
                    startActivityForResult(myIntent, 0);
        		}
        		else {
                	Toast.makeText(getBaseContext(), "Must be default SMS app edit drafts", 
                	Toast.LENGTH_LONG).show();
        		}
            }
        });
        
        // Action for Auto_Reply button. 
        toggleBtnAutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	Toast.makeText(getApplicationContext(), "Auto_Reply ON",
                    		Toast.LENGTH_SHORT).show();
                	//When it is on, switch is set to 1, and it will be used in "Activity.Receiver"
                	autoReplyOn=1; 
                } else {
                	Toast.makeText(getApplicationContext(), "Auto_Reply OFF",
                    		Toast.LENGTH_SHORT).show();
                	autoReplyOn=0;
                }
            }
        });
        
        // Action for Forward button. It will fill phone number field and leave message empty
        btnForward.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
	            
	            String message = txtReceive.getText().toString();
           	 	if (message.length()>0){
	                 message = message.substring(message.indexOf('[')+1, message.lastIndexOf(']'));
	                 txtPhoneNo.setText(null);
	                 txtMessage.setText(message);	
           	 }
            }
        });
        
        // Action for Reply button. It will fill message field and leave phone number empty
        btnReply.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	String phoneNo = txtReceive.getText().toString();
	            if (phoneNo.length()>0){
		            phoneNo = phoneNo.substring(phoneNo.indexOf('<')+1, phoneNo.indexOf('>'));
		            txtMessage.setText(null);
		            txtPhoneNo.setText(phoneNo);
	            }
            }
        });
        
        // Action for Edit button.It will fill both phone number and message fields
        btnEdit.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) {
            	 String message = txtReceive.getText().toString();
            	 String phoneNo = txtReceive.getText().toString();
            	 if (message.length()>0){
	 	             phoneNo = phoneNo.substring(phoneNo.indexOf('<')+1, phoneNo.indexOf('>'));
	                 message = message.substring(message.indexOf('[')+1, message.lastIndexOf(']'));
	                 txtPhoneNo.setText(phoneNo);
	                 txtMessage.setText(message);
            	 }
            	
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        //create a package manger to enable and disable broadcast receivers as needed
        PackageManager pm = getPackageManager();
        ComponentName nonDefaultReceiver = new ComponentName(getApplicationContext(), 
        		Activity_Receiver_NonDefault.class);
        
        /* Display a set as default sms button if this is not the default SMS app */
        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.

        	// Disable and enable receivers accordingly
        	pm.setComponentEnabledSetting(nonDefaultReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
        			PackageManager.DONT_KILL_APP);

            // Set up a button that allows the user to change the default SMS app
            Button button = (Button) findViewById(R.id.changeDefaultApp);
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, 
                            myPackageName);
                    startActivity(intent);
                }
            });
        } else {
            // App is the default.
        	// Disable and enable receivers accordingly
        	pm.setComponentEnabledSetting(nonDefaultReceiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
        			PackageManager.DONT_KILL_APP);

            // Hide the "not currently set as the default SMS app" interface
            Button button = (Button) findViewById(R.id.changeDefaultApp);
            button.setVisibility(View.GONE);
            button.setEnabled(false);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        //if the current app is the stock app, save messages as a draft.
        final String myPackageName = getPackageName();
        if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
        	String phoneNo = txtPhoneNo.getText().toString();
        	String message = txtMessage.getText().toString();
        	//phone number has to be there before saving draft
        	//message body must also be nonempty for auto-draft save
        	if (phoneNo.length()>0 && message.length()>0) {               
        		Date resultdate = new Date(System.currentTimeMillis());
                /*Write newly sent message in the TextView box*/
                txtReceive.setText("Saved draft for SMS sending to " + "<" + phoneNo + "> : "
                		+"\n"+"["+message+"]\n"+resultdate);
                
                //Clear phone number box and message box after saving
                txtPhoneNo.setText(null);
                txtMessage.setText(null);
                
                saveDraft(phoneNo, message);
        		
        		//state that message was saved as a draft
            	Toast.makeText(getBaseContext(), "Message saved as draft.", 
                        Toast.LENGTH_LONG).show();
        	}
        }
    }
    
    /* Context Menu that appears on btnAddContact press */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		//create the context menu
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle("This will open the Contact Manager.");
	    MenuInflater inflater = this.getMenuInflater();
	    inflater.inflate(R.menu.menu_add_contact, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.yes_addcontact:
	        	//get the text inside the phonenumber
            	String phoneNo = txtPhoneNo.getText().toString();
	        	
	        	// pass the acquired phone number to the contacts app
            	Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNo);
				startActivity(intent);
				
				//Clear phone number box after adding the contac
                txtPhoneNo.setText(null);
				
	            return true;
	        case R.id.no_addcontact:
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

    /* Method of sending a message to another device */
    public void sendSMS(String phoneNumber, String message)
    {  
    	if (Utils.isDefaultSmsApp(this)) {
    		// Send SMS, and write in bottom TextView box
            try {
                SmsManager sms = SmsManager.getDefault();           
                sms.sendTextMessage(phoneNumber, null, message, PendingIntent.getBroadcast(
                        this, 0, new Intent("SMS_SENT"), 0), null);
                Toast.makeText(getApplicationContext(), "SMS Sent",
                		Toast.LENGTH_SHORT).show();

                // manage "content://sms" since we are the default sms app
                saveUsersentSMS(phoneNumber, message);
                
                //Add time and date at the end
                Date resultdate = new Date(System.currentTimeMillis());
                txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"
                		+"\n"+"["+ message+"]\n"+ resultdate);
             } 
            // Not sure how to test exception
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                "SMS failed",
                	Toast.LENGTH_LONG).show();
                Date resultdate = new Date(System.currentTimeMillis());
                txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"+"\n"
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
                Toast.makeText(getApplicationContext(), "SMS Sent",
                		Toast.LENGTH_SHORT).show();
                
                //Add time and date at the end
                Date resultdate = new Date(System.currentTimeMillis());
                txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"
                		+"\n"+"["+ message+"]\n"+ resultdate);
             } 
            // Not sure how to test exception
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                "SMS failed",
                	Toast.LENGTH_LONG).show();
                Date resultdate = new Date(System.currentTimeMillis());
                txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"+"\n"
                		+"["+ message+"]\n" +" !!!SMS failed\n"+resultdate);
                e.printStackTrace();
             }
        }
    }    

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
        values.put("type", 1);
        values.put("thread_id", thread_id);
        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }
    
    public void saveDraft(String recipient, String body) {
        Uri threadIdUri = Uri.parse("content://mms-sms/threadID");
        Uri.Builder builder = threadIdUri.buildUpon();
        builder.appendQueryParameter("recipient", recipient);
        Uri uri = builder.build();
        Long thread_id = get_thread_id(uri, recipient);

        ContentValues values = new ContentValues();
        values.put("address", recipient);
        values.put("body", body);
        values.put("date", System.currentTimeMillis());
        values.put("type", 3);
        values.put("thread_id", thread_id);
        getContentResolver().insert(Uri.parse("content://sms/draft"), values);
    }
    
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

}
