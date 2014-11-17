package project.se3354.sms_messenger_group8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;
import android.app.Activity;

public class Activity_AlarmReciever extends BroadcastReceiver
{
         @Override
            public void onReceive(Context context, Intent intent)
            {
                    
                
                    
                      // here you can start an activity or service depending on your need
                     // for ex you can start an activity to vibrate phone or to ring the phone   
        	 		//Activity_ScheduleSend sendLater = new Activity_ScheduleSend();                 
                    String phoneNumberReciver=Activity_ScheduleSend.txtNumber.getText().toString();
                    String message=Activity_ScheduleSend.txtText.getText().toString();
                    SmsManager sms = SmsManager.getDefault(); 
                    sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
                    
                    /*---Doesn't work
                    MainActivity laterSend = new MainActivity();
                    laterSend.sendSMS("5556", "message");
                    */
                    
                    Toast.makeText(context, "Scheduled SMS has been Sent", Toast.LENGTH_SHORT).show();
                    //Clear SendLater information
                    Activity_ScheduleSend.txtNumber.setText("");
                    Activity_ScheduleSend.txtText.setText("");
                    Activity_ScheduleSend.txtDay.setText("");
                    Activity_ScheduleSend.txtHr.setText("");
                    Activity_ScheduleSend.txtMin.setText("");
                    Activity_ScheduleSend.txtSec.setText("");
                    
                    MainActivity.btnScheduleSend.setText("Send Later");
                	MainActivity.btnScheduleSend.setTextColor(-16777216);
                    
                	Activity_ScheduleSend.txtTime.setText("Scheduled SMS has been sent!!!");
                	
                    
                   
             }
         
      
}