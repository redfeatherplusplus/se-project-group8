package project.se3354.sms_messenger_group8;

import android.app.ActionBar.LayoutParams;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

public class Activity_Inbox extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	Button btnReturn;
	ListView ContactsList;
	
    /* Called when the activity is first created. */
	
	//////////////////////////
	// Create Contacts List //
	//////////////////////////
	
	// Create Inbox box URI
	Uri inboxURI = Uri.parse("content://sms/inbox");
	 
	// List required columns
	String[] reqCols = new String[] { "_id", "address", "body" };
	 
	// Get Content Resolver object, which will deal with Content Provider
	ContentResolver cr = getContentResolver();
	 
	// Fetch Inbox SMS Message from Built-in Content Provider
	Cursor c = cr.query(inboxURI, reqCols, null, null, null);
	
	//This is the Adapter being used to display the list's data
	SimpleCursorAdapter mAdapter;
	
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
	    ContactsContract.Data.DISPLAY_NAME};
	
	// This is the select criteria
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
	    ContactsList = (ListView) findViewById(R.id.ContactsList);
	    btnReturn = (Button) findViewById(R.id.btnReturn);
	
		// Create a progress bar to display while the list loads
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		        LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progressBar.setIndeterminate(true);
		ContactsList.setEmptyView(progressBar);
		
		// Must add the progress bar to the root of the layout
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		root.addView(progressBar);
		
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
		
		int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
		
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this, 
		        android.R.layout.simple_list_item_1, null,
		        fromColumns, toViews, 0);
		ContactsList.setAdapter(mAdapter);
		
		/* Action when click on Contact Item */
		ContactsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor Contact = (Cursor) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), 
                		Contact.getString(1) + " doesn't like you.", 
                		Toast.LENGTH_LONG).show();
			}
		}); 
		
	    /* Action when click "Return" button */
	    btnReturn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	        }
	    });
	    
	    /* Action when click "Return" button */
	    btnReturn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	        }
	    });
		
		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}
	
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	// Now create and return a CursorLoader that will take care of
	// creating a Cursor for the data being displayed.
	return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
	        PROJECTION, SELECTION, null, null);
	}
	
	// Called when a previously created loader has finished loading
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	// Swap the new cursor in.  (The framework will take care of closing the
	// old cursor once we return.)
	mAdapter.swapCursor(data);
	}
	
	// Called when a previously created loader is reset, making the data unavailable
	public void onLoaderReset(Loader<Cursor> loader) {
	// This is called when the last Cursor provided to onLoadFinished()
	// above is about to be closed.  We need to make sure we are no
	// longer using it.
	mAdapter.swapCursor(null);
	}
	
}
