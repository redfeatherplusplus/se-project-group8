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
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView; 

public class MainActivity extends Activity 
{
	Button btnFindContactNo;
	Button btnSendSMS;
	Button btnScheduleSend;
	EditText txtPhoneNo;
	EditText txtMessage;
	
	static TextView txtReceive;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {   
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);       
        btnFindContactNo = (Button) findViewById(R.id.btnFindContactNo); 
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnScheduleSend = (Button) findViewById(R.id.btnScheduleSend);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        
        txtReceive=(TextView)findViewById(R.id.txtReceive); //TextView box for newest message
        
        /*------built-in intent method is hidden here----------
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "Put your message here"); 
        sendIntent.setType("vnd.android-dir/mms-sms");
        try {
        startActivity(sendIntent);
        finish();
        } 
        catch (android.content.ActivityNotFoundException ex) {
        Toast.makeText(MainActivity.this, 
        "Sending message faild", Toast.LENGTH_SHORT).show();
        }      
        You can try it to see how easy it will be-------------------------*/
        
        /* Action when click "From Contacts" button */             
        btnFindContactNo.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
            	Intent myIntent = new Intent(v.getContext(), Activity_Inbox.class);
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
                    txtReceive.setText("SMS sent to "+phoneNo+" : "+message);
                    //Activity_TextView.addNewSMS("SMS sent to "+phoneNo+" : "+message);//ongoing by binz

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
        
        /* Action when click "Send Later" button */             
        btnScheduleSend.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) { 
            	//do stuff
            }
        });

    }
    
    /* Method of sending a message to another device */
    private void sendSMS(String phoneNumber, String message)
    {      
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
    	
        /* Results report after sending */
        /* Making reports, don't need to read between lines. 
         * And I may re-write it later, because I found better examples from other sources.
         ---------------------------------------------------------------------------------------*/
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS sent", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					    Toast.makeText(getBaseContext(), "Generic failure", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NO_SERVICE:
					    Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NULL_PDU:
					    Toast.makeText(getBaseContext(), "Null PDU", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_RADIO_OFF:
					    Toast.makeText(getBaseContext(), "Radio off", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				}
			}
        }, new IntentFilter(SENT));
        
        /*Results report after delivery*/
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case Activity.RESULT_CANCELED:
					    Toast.makeText(getBaseContext(), "SMS not delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;					    
				}
			}
        }, new IntentFilter(DELIVERED));        
    	/*------------------------------------------------------------------------------*/
        
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);               
    }    
}
