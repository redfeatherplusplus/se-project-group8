package project.se3354.sms_messenger_group8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;

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
        	//if the contact name is its phone number, if we haven't gotten the name yet
        	if(smsMessages.get(i).getPhoneNumber().equals(smsMessages.get(i).getContactName())) {
        		//replace the phone number with the contact name
        		smsMessages.get(i).setContactName(getContactName(smsMessages.get(i).getPhoneNumber()));
            	
            	//replace the contact name for each message that has the same number
            	//we do this because the getContactName call is expensive
            	String thisContactName = smsMessages.get(i).getContactName();
            	String thisContactNumber = smsMessages.get(i).getPhoneNumber();
            	for(int j=0; j<smsMessages.size(); j++) {
            		if (smsMessages.get(i).getPhoneNumber().equals(thisContactNumber)) {
            			smsMessages.get(i).setContactName(thisContactName);
            		}
            	}
        	}
        }        
            
        return smsMessages;
    }
    
	public synchronized String getContactName(String address) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));  
        Cursor cs = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME},PhoneLookup.NUMBER+"='"+address+"'",null,null);

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
}