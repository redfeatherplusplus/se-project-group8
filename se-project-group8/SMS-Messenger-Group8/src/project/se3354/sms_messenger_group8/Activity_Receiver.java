package project.se3354.sms_messenger_group8;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    //private constant 
	 
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        /*get the message passed in */
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String smsString = "";  
        String oldSMS = "";
        int j=0;
        
        
        if (bundle != null)
        {
            /*retrieve the message received*/
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length]; 

            
            for (int i=0; i<msgs.length; i++){
            	msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            	PhoneNoRec=msgs[i].getOriginatingAddress();
            	MessageRec=msgs[i].getMessageBody().toString();
                smsString += "SMS from " + PhoneNoRec+" : "+"\n"+MessageRec+"\n";                   
            }
            
            /* display the new message */
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            /*Write newly received message in the TextView box*/
            MainActivity activity1st=new MainActivity();
            activity1st.txtReceive.setText(smsString);
            
            //Autoreply: if Auto_Reply button is on, reply "I am not available"
            if (activity1st.autoReplyOn==1){
            	// if two devices have "Auto_Reply" on, they will reply each other 2 times
            	if (j<2){
            		if (PhoneNoRec.equals(oldSMS)){
            			j+=1;
            		}else{
            			j=0;
            		}
	            	SmsManager sms = SmsManager.getDefault();
	            	//String ar = activity1st.txtAutoReply.getText().toString();
	            	sms.sendTextMessage(PhoneNoRec, null, "I am not available", null, null);
	            	oldSMS=PhoneNoRec;
	            	
            	}
            }
        } 
        
        /* tell the inbox it needs to update */
        Intent update = new Intent("Inbox.updateActivity");
        context.sendBroadcast(update);

    }

    
}