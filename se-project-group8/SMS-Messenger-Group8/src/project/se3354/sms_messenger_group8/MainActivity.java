package project.se3354.sms_messenger_group8;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class MainActivity extends Activity 
{

	public static EditText txtAutoReply;
	public static EditText txtPhoneNo;
	
	Button btnFindContactNo;
	Button btnSendSMS;
	Button btnScheduleSend;
	Button btnInbox;
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
                    /*Write newly sent message in the TextView box*/
                    //txtReceive.setText("SMS sent to "+phoneNo+" : "+message);
                    
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
        
                
        /* Action when click "Go to Conversation" button */             
        btnScheduleSend.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) { 
            	Intent myIntent = new Intent(v.getContext(), Activity_Conversation.class);
                startActivityForResult(myIntent, 0);
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
        
        toggleBtnAutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	Toast.makeText(getApplicationContext(), "Auto_Reply ON",
                    		Toast.LENGTH_SHORT).show();
                	autoReplyOn=1; 
                } else {
                	Toast.makeText(getApplicationContext(), "Auto_Reply OFF",
                    		Toast.LENGTH_SHORT).show();
                	autoReplyOn=0;
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
            txtReceive.setText("SMS sent to "+phoneNumber+" :"+"\n"+message+"\n");
         } 
        // Not sure how to test exception
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),
            "SMS failed",
            	Toast.LENGTH_LONG).show();
            txtReceive.setText("SMS sent to "+phoneNumber+" :"+"\n"+message+"\n"+" !!!SMS failed");
            e.printStackTrace();
         }
    }
   


}
