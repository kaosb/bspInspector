package com.bspinspector;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class preExistence extends Activity {

	private String user;
	private String pass;
	private String cod_ubicacion;
	private String glosaMarca;
	private String glosaModelo;
	private ProgressDialog pd = null;
	private File dbfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");
		this.cod_ubicacion = bundle.getString("cod_ubicacion");
		this.glosaMarca = bundle.getString("glosaMarca");
		this.glosaModelo = bundle.getString("glosaModelo");
		
	    this.pd = ProgressDialog.show(this, "", "Cargando...", true, false);
        new DownloadTask().execute("");

	}


	/**
	 * Clase AsyncTask
	 * */
	private class DownloadTask extends AsyncTask<String, Void, Object> {
		protected Object doInBackground(String... args) {
			/*Instancia el DownloadHelper*/
			Downloader dw = new Downloader();
			/*Agregar codigo para controlar la version.*/
			//if(!dbfile.exists()){
				dbfile = dw.getDB();	
			//}
			return null;
		}

		protected void onPostExecute(Object result) {
			if(dbfile.exists()){

				//SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
				//String[] args = new String[] {"1"};
				//Cursor c = db.rawQuery("SELECT * FROM section WHERE status=? ORDER BY orden ASC",args);

				//ScrollView sv = new ScrollView(preExistence.this);
				final LinearLayout ll = new LinearLayout(preExistence.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				//sv.addView(ll);

				// Cabecera
				LinearLayout cabecera = new LinearLayout(preExistence.this);
				cabecera.setOrientation(LinearLayout.HORIZONTAL);
				cabecera.setBackgroundColor(Color.parseColor("#0A0C29"));
				cabecera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
				cabecera.setPadding(15, 5, 5, 5);
				TextView nombre = new TextView(preExistence.this);
				nombre.setText(preExistence.this.user);
				nombre.setTextSize(12);
				nombre.setTextColor(Color.parseColor("#FDFDFD"));
				nombre.setTypeface(null, Typeface.BOLD);
				cabecera.addView(nombre);
				ll.addView(cabecera);

				LinearLayout infoSpace = new LinearLayout(preExistence.this);
				infoSpace.setOrientation(LinearLayout.VERTICAL);
				infoSpace.setBackgroundColor(Color.parseColor("#CFDCE9"));
				infoSpace.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				TextView marca = new TextView(preExistence.this);
				marca.setPadding(15, 3, 3, 3);
				marca.setBackgroundColor(Color.parseColor("#FDFDFD"));
				marca.setTextColor(Color.parseColor("#838383"));
				marca.setTextSize(14);
				marca.setTypeface(null, Typeface.BOLD);
				marca.setText("MARCA: "+preExistence.this.glosaMarca);
				infoSpace.addView(marca);

				TextView modelo = new TextView(preExistence.this);
				modelo.setPadding(15, 3, 3, 3);
				modelo.setBackgroundColor(Color.parseColor("#FDFDFD"));
				modelo.setTextColor(Color.parseColor("#838383"));
				modelo.setTextSize(14);
				modelo.setTypeface(null, Typeface.BOLD);
				modelo.setText("MODELO: "+preExistence.this.glosaModelo);
				infoSpace.addView(modelo);

				ll.addView(infoSpace);

				View bordeinfo = new View(preExistence.this);
				bordeinfo.setBackgroundColor(Color.parseColor("#3F8A00"));
				bordeinfo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 4));
				ll.addView(bordeinfo);
				// FIN Cabecera

				// TODO aqui va toda la chachara de enmedio
           	 View borde = new View(preExistence.this);
           	 borde.setBackgroundColor(Color.parseColor("#616161"));
           	 borde.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 2));
           	 
           	 LinearLayout cont = new LinearLayout(preExistence.this);
           	 cont.setOrientation(LinearLayout.VERTICAL);
           	 //cont.setTag(c.getString(0));
           	 cont.setPadding(5, 0, 5, 0);
           	 cont.setBackgroundColor(Color.parseColor("#CFDCE9"));
           	 
           	 TextView titulo = new TextView(preExistence.this);
           	 titulo.setText("\n Selecciona el tipo de vehiculo.");
           	 titulo.setTextSize(18);
           	 titulo.setTextColor(Color.parseColor("#0A0C29"));
           	 titulo.setTypeface(null, Typeface.BOLD);
           	 cont.addView(titulo);
           	 
           	String[] items = new String[] {"1","2","3","4"};
         	Spinner select = new Spinner(preExistence.this);
         	select.setId(1);
         	select.setTag(2);
         	ArrayAdapter<String> adapter = new ArrayAdapter<String>(preExistence.this, android.R.layout.simple_spinner_item, items);
        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	select.setAdapter(adapter);
        	//Get saved value
        	/*value = getFieldValue(c.getInt(0));
        	if(value != null){
        		//int pos = Arrays.binarySearch(items, value);
        		int pos = this.indexOf(adapter, value);
        		Log.i("posicion seleccionado", String.valueOf(pos));
        		select.setSelection(pos);
        	}*/
        	// Lo agrego a la vista
        	cont.addView(select);
           	 
           	 // TODO Agregar el select
           	 
           	 ll.addView(cont);
           	 ll.addView(borde);
				
				// PIE
				LinearLayout pie = new LinearLayout(preExistence.this);
				pie.setOrientation(LinearLayout.HORIZONTAL);
				pie.setBackgroundColor(Color.parseColor("#0A0C29"));
				pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
				pie.setPadding(15, 5, 5, 5);

				TextView isonline = new TextView(preExistence.this);
				isonline.setPadding(0, 0, 0, 0);
				isonline.setTypeface(null, Typeface.BOLD);
				if(connectionOK()){
					isonline.setText("ONLINE");
					isonline.setTextColor(Color.parseColor("#00FF00"));
				}else{
					isonline.setText("OFFLINE");
					isonline.setTextColor(Color.parseColor("#FF0000"));
				}

				pie.addView(isonline);
				ll.addView(pie);
				// FIN PIE

				//c.close();
				//db.close();

				preExistence.this.setContentView(ll);

			}else{
				Log.i("Ups!","El archivo no existe");
			}
			preExistence.this.pd.dismiss();
		}
	} 
	/**
	 * fin clase AsyncTask
	 * */
	
	
	/**
	 * Verifica Conexion
	 * */
	public boolean connectionOK(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null && ni.isConnected()){
			Log.i("INTERNET", "conectado");
			return true;
		}else{
			Log.i("INTERNET", "no conectado");
			return false;
		}
	}
}
