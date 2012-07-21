package com.bspinspector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class inicio extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	startActivity(new Intent(this, dologin.class));
    	finish();
    }
}