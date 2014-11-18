package project.se3354.sms_messenger_group8;

import java.util.Date;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;

import java.lang.Object;


public class Activity_AlarmReciever extends BroadcastReceiver
{
	Activity activity = new Activity();
         @Override
            public void onReceive(Context context, Intent intent)
            {
                    String phoneNumberReciver=Activity_ScheduleSend.txtNumber.getText().toString();
                    String message=Activity_ScheduleSend.txtText.getText().toString();
                    
                    
                    sendSchedule (phoneNumberReciver, message);
                    
                                                           
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
                	Activity_ScheduleSend2.textView2.setText("Scheduled SMS has been sent!!! \n Please go back.");
                	
                	
 
             }
   
         Context context;
         public void sendSchedule(String phoneNumber, String message)
         {      	
        	 
                 try {
                     SmsManager sms = SmsManager.getDefault();           
                     sms.sendTextMessage(phoneNumber, null, message, null, null);

                     // manage "content://sms" since we are the default sms app
                     //saveUsersentSMS(phoneNumber, message);
                     
                   //Add time and date at the end
                     Date resultdate = new Date(System.currentTimeMillis());
                     MainActivity.txtReceive.setText("SMS sent to "+phoneNumber + ": "+"\n"+"["+ message+"]\n"+ resultdate);
                 } 
                 // Not sure how to test exception
                 catch (Exception e) {
                	 
                     Toast.makeText(context,"SMS failed", Toast.LENGTH_LONG).show();
                     Date resultdate = new Date(System.currentTimeMillis());
                     MainActivity.txtReceive.setText("SMS sent to "+"<"+phoneNumber+"> :"+"\n"
                     		+"["+ message+"]\n" +" !!!SMS failed\n"+resultdate);
                     e.printStackTrace();
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
             context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
         }
     	
     	private Long get_thread_id(Uri uri, String recipient) {
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