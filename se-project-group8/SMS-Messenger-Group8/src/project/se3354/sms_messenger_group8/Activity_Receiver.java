package project.se3354.sms_messenger_group8;

import java.util.Date;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony.Sms.Intents;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/* device receive the message and give a popup with the number and message body*/
public class Activity_Receiver extends BroadcastReceiver
{        
	private static String PhoneNoRec = "";
    private static String MessageRec = "";
    private static String oldSMS = ""; 
	 
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        /*get the message passed in */
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String smsString = "";  
                
        if (bundle != null)
        {
            /*retrieve the message received*/
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length]; 

            if (Utils.isDefaultSmsApp(context)) {
            	// get message and save it since we are the default sms app
            	for (int i=0; i<msgs.length; i++) {
                	msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                	PhoneNoRec=msgs[i].getOriginatingAddress();
                	MessageRec=msgs[i].getMessageBody().toString();
                    smsString += "SMS from " +"<"+ PhoneNoRec+"> : "+"\n"+"["+ MessageRec+"]\n";                   
                }
                
                /* display the new message */
                Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
                /*Write newly received message in the TextView box*/
                Date resultdate = new Date(System.currentTimeMillis());
                MainActivity activity1st=new MainActivity();
                activity1st.txtReceive.setText(smsString+"\n"+resultdate);
                
                //Autoreply: if Auto_Reply button is on, reply "I am not available"
                if (activity1st.autoReplyOn==1 & !PhoneNoRec.equals(oldSMS)) {
                	// if two devices have "Auto_Reply" on, they will reply each other only once
                	//oldSMS is a blocker, when new coming is same as previous number
                	            			     		   		
                	SmsManager sms = SmsManager.getDefault();
                	String ar = MainActivity.txtAutoReply.getText().toString();
                	oldSMS=PhoneNoRec;
                	sms.sendTextMessage(PhoneNoRec, null, ar, null, null);          	
                    	
                //If Auto_Reply switch off, oldSMS is reset to be null. So, if no other number call in, 
                //the same number will not be blocked when next time Auto_reply is on.
                }
                else if (activity1st.autoReplyOn!=1) {
                	oldSMS="";
                }
                
                //save the received message since we are the default sms app
            	saveReceivedSMS(PhoneNoRec, MessageRec, context);
            } 
            else {
            	for (int i=0; i<msgs.length; i++) {
                	msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                	PhoneNoRec=msgs[i].getOriginatingAddress();
                	MessageRec=msgs[i].getMessageBody().toString();
                    smsString += "SMS from " +"<"+ PhoneNoRec+"> : "+"\n"+"["+ MessageRec+"]\n";                   
                }
                
                /* display the new message */
                Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
                /*Write newly received message in the TextView box*/
                Date resultdate = new Date(System.currentTimeMillis());
                MainActivity activity1st=new MainActivity();
                activity1st.txtReceive.setText(smsString+"\n"+resultdate);
                
                //Autoreply: if Auto_Reply button is on, reply "I am not available"
                if (activity1st.autoReplyOn==1 & !PhoneNoRec.equals(oldSMS)) {
                	// if two devices have "Auto_Reply" on, they will reply each other only once
                	//oldSMS is a blocker, when new coming is same as previous number
                	            			     		   		
                	SmsManager sms = SmsManager.getDefault();
                	String ar = MainActivity.txtAutoReply.getText().toString();
                	oldSMS=PhoneNoRec;
                	sms.sendTextMessage(PhoneNoRec, null, ar, null, null);          	
                    	
                //If Auto_Reply switch off, oldSMS is reset to be null. So, if no other number call in, 
                //the same number will not be blocked when next time Auto_reply is on.
                }
                else if (activity1st.autoReplyOn!=1) {
                	oldSMS="";
                }
            }     
        } 
        
        /* tell the inbox it needs to update */
        Intent update = new Intent("Inbox.updateActivity");
        context.sendBroadcast(update);
        
        /* tell the conversation it needs to update */
        Intent updateConv = new Intent("Conversation.updateActivity");
        context.sendBroadcast(updateConv);

    }
    
    public void getSmsMessage(SmsMessage[] msgs, Object[] pdus, Context context, 
    		String smsString) {
    	
    }
    
    protected void saveReceivedSMS(String recipient, String body, Context context) {
        Uri threadIdUri = Uri.parse("content://mms-sms/threadID");
        Uri.Builder builder = threadIdUri.buildUpon();
        builder.appendQueryParameter("recipient", recipient);
        Uri uri = builder.build();
        Long thread_id = get_thread_id(uri, recipient, context);

        ContentValues values = new ContentValues();
        values.put("address", recipient);
        values.put("body", body);
        values.put("date", System.currentTimeMillis());
        values.put("type", 1);
        values.put("thread_id", thread_id);
        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
    }
    
    private Long get_thread_id(Uri uri, String recipient, Context context) {
        long threadId = 0;
        Cursor cursor = context.getContentResolver().query(uri, new String[] { "_id" },
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