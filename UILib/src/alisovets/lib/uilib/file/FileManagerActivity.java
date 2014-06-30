package alisovets.lib.uilib.file;

import alisovets.lib.uilib.R;
import alisovets.lib.uilib.dialog.Confirmable;
import alisovets.lib.uilib.dialog.TextReceivable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * 
 * An Activity to manage of the filesystem. 
 * @author Alexander Lisovets
 *
 */
public class FileManagerActivity extends ActionBarActivity implements TextReceivable, Confirmable{
	
	public static final String ACTIVITY_TITLE_KEY = "activity_title";
	public static final String FRAGMENT_TAG = "fragment_view";
	
	public FileManagerFragment mFragment; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("log", "activity onCreate");
		
		Intent intent = getIntent();
		String title = intent.getStringExtra(ACTIVITY_TITLE_KEY);
		if (title != null) {
			setTitle(title);
			
		}
		
		getSupportActionBar().setBackgroundDrawable(null);
		setContentView(R.layout.uilib_file_activity);
	
		if (savedInstanceState == null) {
			mFragment =  new FileManagerFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment, FRAGMENT_TAG).commit();
		}
		else{
			FragmentManager fragmentManager = getSupportFragmentManager();
			mFragment = (FileManagerFragment)fragmentManager.findFragmentByTag(FRAGMENT_TAG);		
		}
	}

	@Override
	public void receiveName(String name) {
		mFragment.receiveFileName(name);		
	}
	
	
	@Override
	public void confirm() {
		mFragment.confirm();
		
	}

	
	@Override
	public void onBackPressed() {
		if(mFragment.backPressed()){
			super.onBackPressed();
		}
		return;	
	}

}
