package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView; 
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.lang.Object;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity 
{

	public static EditText txtAutoReply;
	public static EditText txtPhoneNo;
	
	Button btnFindContactNo;
	Button btnSendSMS;
	Button btnScheduleSend;
	Button btnSaveDraft;
	Button btnOpenDraft;
	Button btnInbox;
	Button btnForward;
	Button btnReply;
	Button btnEdit;
	EditText txtMessage;
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
        
    
        /* Action when click "From Contacts" button */             
        btnFindContactNo.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
            	Intent myIntent = new Intent(v.getContext(), Activity_Contacts.class);
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
        
                
        /* Action when click "Save Draft" button */             
        btnSaveDraft.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) { 
        		ContentValues values = new ContentValues();
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
                    
	            	values.put("address", phoneNo);
	        		values.put("body", message);
	        		values.put("date", String.valueOf(System.currentTimeMillis())); 
	        		values.put("type", "3");
	        		values.put("thread_id", "0"); 
	        		getContentResolver().insert(Uri.parse("content://sms/draft"), values);
            	}
            	//phone number has to be there, if not, report error
            	else {
                	Toast.makeText(getBaseContext(), "Phone number cannot be empty", 
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
    
    
    /* Method of sending a message to another device */
    public void sendSMS(String phoneNumber, String message)
    {      	
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