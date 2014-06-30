package alisovets.example.demofilemanager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * 
 * It is a Demo activity to demonstrate few examples of usage FileManagerActivity from UILib library    
 * 
 * @author Alexander Lisovets, 2014
 *
 */
public class MainActivity extends ActionBarActivity {

	MainFragment mMainFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			mMainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, mMainFragment).commit();
		}
	}

}
