package project.se3354.sms_messenger_group8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/* device receive the message and give a popup with the number and message body*/
public class SmsReceiver extends BroadcastReceiver
{
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
                smsString += "SMS from " + msgs[i].getOriginatingAddress();                     
                smsString += " :";
                smsString += msgs[i].getMessageBody().toString();
                smsString += "\n";        
            }
            /* display the new message */
            Toast.makeText(context, smsString, Toast.LENGTH_LONG).show();
            



        } 
        
    }
    
}