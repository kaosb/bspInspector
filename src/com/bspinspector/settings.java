package com.bspinspector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class settings extends Activity {

	private String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		
		TextView username = (TextView) findViewById(R.id.textViewSaludo);
		username.setText(user);
		
		final EditText cantidad = (EditText) findViewById(R.id.editText1);
		
    	SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String cant = sp.getString("cantCampos", null);
        
        if(cant != null){
        	cantidad.setText(cant);
        }
		
		ImageView btnGuardar = (ImageView) findViewById(R.id.imageView1);
		btnGuardar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				rememberSettings(cantidad.getText().toString());
				finish();
			}
		});

	}
	
	/**
	 * Funciones para guardar y chequear si guardo las preferencias.
	 * */
    public void rememberSettings(String cantidad){
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cantCampos", cantidad);
        editor.commit();
    }
	
}
