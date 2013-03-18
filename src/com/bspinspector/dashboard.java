package com.bspinspector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class dashboard extends Activity {
	
	private String user;
	private String pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");

		/*Saludo Bienvenida*/
		TextView mensajeBienvenida = (TextView) findViewById(R.id.textViewSaludo);
		mensajeBienvenida.setText("Bienvenido "+this.user);

		/*Botones*/
		ImageView btnDescargar = (ImageView) findViewById(R.id.imageView1);
		btnDescargar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent myIntent = new Intent(dashboard.this, getNuevosCasos.class);
				myIntent.putExtra("user", user);
				myIntent.putExtra("pass", pass);
				startActivity(myIntent);
			}
		});
		
		ImageView btnCasos = (ImageView) findViewById(R.id.imageView2);
		btnCasos.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent myIntent = new Intent(dashboard.this, listaCasos.class);
				myIntent.putExtra("user", user);
				myIntent.putExtra("pass", pass);
				startActivity(myIntent);
			}
		});
		
		ImageView btnOpciones = (ImageView) findViewById(R.id.imageView3);
		btnOpciones.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent myIntent = new Intent(dashboard.this, settings.class);
				myIntent.putExtra("user", user);
				myIntent.putExtra("pass", pass);
				startActivity(myIntent);
			}
		});
		
		ImageView btnOcuenta = (ImageView) findViewById(R.id.imageView4);
		btnOcuenta.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
		ImageView btnCasosUpload = (ImageView) findViewById(R.id.imageView5);
		btnCasosUpload.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(dashboard.this, "Caracteristica en desarrollo.", Toast.LENGTH_LONG).show();
			}
		});
		
	}
}
