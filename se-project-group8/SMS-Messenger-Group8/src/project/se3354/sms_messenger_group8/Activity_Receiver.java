package project.se3354.sms_messenger_group8;

import java.util.Date;

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
    private static String oldSMS = ""; 
    Activity_Inbox resetLoadingSignal = null;  
	 
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

            
            for (int i=0; i<msgs.length; i++){
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
            if (activity1st.autoReplyOn==1 & !PhoneNoRec.equals(oldSMS)){
            	// if two devices have "Auto_Reply" on, they will reply each other only once
            	//oldSMS is a blocker, when new coming is same as previous number
            	            			     		   		
	            	SmsManager sms = SmsManager.getDefault();
	            	String ar = MainActivity.txtAutoReply.getText().toString();
	            	oldSMS=PhoneNoRec;
	            	sms.sendTextMessage(PhoneNoRec, null, ar, null, null);          	
	            	
            //If Auto_Reply switch off, oldSMS is reset to be null. So, if no other number call in, 
	        //the same number will not be blocked when next time Auto_reply is on.
            }else if (activity1st.autoReplyOn!=1){
            	oldSMS="";
            
            }
          
        } 
        
        /* tell the inbox it needs to update */
        Intent update = new Intent("Inbox.updateActivity");
        context.sendBroadcast(update);
        
        /* tell the conversation it needs to update */
        Intent updateConv = new Intent("Conversation.updateActivity");
        context.sendBroadcast(updateConv);

    }
    
    public void setMainActivityHandler(Activity_Inbox inbox){
    	resetLoadingSignal = inbox;
	}  

    
}