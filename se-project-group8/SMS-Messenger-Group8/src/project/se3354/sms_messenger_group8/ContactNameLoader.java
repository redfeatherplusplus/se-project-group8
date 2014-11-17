package project.se3354.sms_messenger_group8;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.ImageView;

public final class ContactNameLoader extends AsyncTaskLoader<ArrayList<MyMessage>> {

	private ArrayList<MyMessage> smsMessages;
	Context context;
	
    public ContactNameLoader(Context context, ArrayList<MyMessage> smsMessages) {
		super(context);
		this.smsMessages = smsMessages;
		this.context = context;
		// TODO Auto-generated constructor stub
	}
    @Override
    protected synchronized void onStartLoading() {
            // just make sure if we already have content to deliver
            if (smsMessages != null)
                    deliverResult(smsMessages);

            // otherwise if something has been changed or first try
            if (takeContentChanged() || smsMessages == null)
                    forceLoad();
    }

    @Override
    protected synchronized void onStopLoading() {
            cancelLoad();
    }

    @Override
    protected synchronized void onReset() {
            super.onReset();

            onStopLoading();

            // clear reference to object
            // it's necessary to allow GC to collect the object
            // to avoid memory leaking
            smsMessages = null;
    }
    
    @Override
    public synchronized ArrayList<MyMessage> loadInBackground() {
    	for(int i=0; i<smsMessages.size(); i++) {
    		//get the phone number of this smsMessage to reference with on the contact list
    		String address = smsMessages.get(i).getPhoneNumber();
    		
    		//set replace the displayed phone number with the contact name if it exists
    		String contactName = getContactName(address);
    		smsMessages.get(i).setContactName(contactName);
    		
    		//load the photo if it exists
    		Bitmap contactPhoto = getContactPhoto(address);
    		if (contactPhoto != null) {
	    		smsMessages.get(i).setIcon(contactPhoto); 
    		}
        }        
            
        return smsMessages;
    }
    
	public synchronized String getContactName(String address) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));  
        Cursor cs = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, 
        		PhoneLookup.NUMBER+"='"+address+"'",null,null);

        if(cs.getCount()>0) {
        	cs.moveToFirst();
        	String ContactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        	
            cs.close();
        	return (ContactName);
        } 
        else {
        	cs.close();
        	return(address);
        }
	}
	
	public synchronized Bitmap getContactPhoto(String address) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address)); 
        ContentResolver cr = getContext().getContentResolver();
        Cursor cs = context.getContentResolver().query(uri, new String[]{PhoneLookup._ID}, 
        		PhoneLookup.NUMBER+"='"+address+"'",null,null);
        
        if(cs.getCount()>0) {
        	cs.moveToFirst();
        	long _id = cs.getLong(cs.getColumnIndex(PhoneLookup._ID));
            Uri photoUri = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI, _id);
            
            InputStream input = ContactsContract.Contacts
                    .openContactPhotoInputStream(cr, photoUri);
            if (input == null) {
                cs.close();
                return null;
            }
            cs.close();
            return BitmapFactory.decodeStream(input);
        } 
        else {
        	cs.close();
        	return(null);
        }
	}
}