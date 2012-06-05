package com.bspinspector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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
			public void onClick(View arg0) { //--clickOnListItem
				/*Intent myIntent = new Intent(listaCasos.this, formulario1.class);
				myIntent.putExtra("cod_ubicacion", listItem.getCod_ubicacion());
				myIntent.putExtra("glosa_marca", listItem.getGlosa_marca());
				myIntent.putExtra("glosa_modelo", listItem.getGlosa_modelo());
				myIntent.putExtra("direccion", listItem.getDireccion());
				startActivity(myIntent);*/
			}
		});
		
		ImageView btnOcuenta = (ImageView) findViewById(R.id.imageView4);
		btnOcuenta.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
	}
}
