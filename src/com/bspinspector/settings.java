package com.bspinspector;

import android.app.Activity;
import android.os.Bundle;

public class settings extends Activity {

	private String user;
	private String pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");
		
	}
}
