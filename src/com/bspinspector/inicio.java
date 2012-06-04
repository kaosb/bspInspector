package com.bspinspector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class inicio extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
    	startActivity(new Intent(this, dologin.class));
    	//startActivity(new Intent(this, formulario1.class));
        //startActivity(new Intent(this, sectionSelector.class));
    	finish();

    }
}