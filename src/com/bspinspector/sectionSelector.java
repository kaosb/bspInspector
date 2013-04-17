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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class sectionSelector extends Activity {

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
		this.glosaMarca = bundle.getString("glosa_marca");
		this.glosaModelo = bundle.getString("glosa_modelo");
		
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
            dbfile = dw.getDB();
			return null;
        }

        protected void onPostExecute(Object result) {
            if(dbfile.exists()){
            	
            	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
                /*Trabajar con la BD*/
                	String[] args = new String[] {"1"};
                	Cursor c = db.rawQuery("SELECT * FROM section WHERE status=? ORDER BY orden ASC",args);
                	
    	            ScrollView sv = new ScrollView(sectionSelector.this);
    	            final LinearLayout ll = new LinearLayout(sectionSelector.this);
    	            ll.setOrientation(LinearLayout.VERTICAL);
    	            sv.addView(ll);
    	            
    	            LinearLayout cabecera = new LinearLayout(sectionSelector.this);
    	            cabecera.setOrientation(LinearLayout.HORIZONTAL);
    	            cabecera.setBackgroundColor(Color.parseColor("#0A0C29"));
    	            cabecera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
    	            cabecera.setPadding(15, 5, 5, 5);
    	            TextView nombre = new TextView(sectionSelector.this);
    	            nombre.setText(sectionSelector.this.user);
    	            nombre.setTextSize(12);
    	            nombre.setTextColor(Color.parseColor("#FDFDFD"));
    	            nombre.setTypeface(null, Typeface.BOLD);
    	            cabecera.addView(nombre);
    	            ll.addView(cabecera);
    	            
    	            LinearLayout infoSpace = new LinearLayout(sectionSelector.this);
    	            infoSpace.setOrientation(LinearLayout.VERTICAL);
    	            infoSpace.setBackgroundColor(Color.parseColor("#CFDCE9"));
    	            infoSpace.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	            
    	            TextView marca = new TextView(sectionSelector.this);
    	            marca.setPadding(15, 3, 3, 3);
    	            marca.setBackgroundColor(Color.parseColor("#FDFDFD"));
    	            marca.setTextColor(Color.parseColor("#838383"));
    	            marca.setTextSize(14);
    	            marca.setTypeface(null, Typeface.BOLD);
    	            marca.setText("MARCA: "+sectionSelector.this.glosaMarca);
    	            infoSpace.addView(marca);
    	            
    	            TextView modelo = new TextView(sectionSelector.this);
    	            modelo.setPadding(15, 3, 3, 3);
    	            modelo.setBackgroundColor(Color.parseColor("#FDFDFD"));
    	            modelo.setTextColor(Color.parseColor("#838383"));
    	            modelo.setTextSize(14);
    	            modelo.setTypeface(null, Typeface.BOLD);
    	            modelo.setText("MODELO: "+sectionSelector.this.glosaModelo);
    	            infoSpace.addView(modelo);
    	            
    	            ll.addView(infoSpace);
    	            
    	            View bordeinfo = new View(sectionSelector.this);
    	            bordeinfo.setBackgroundColor(Color.parseColor("#3F8A00"));
    	            bordeinfo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 4));
    	            ll.addView(bordeinfo);
    	            
    	            if (c.moveToFirst()) {
    	                 //Recorremos el cursor hasta que no haya m√°s registros
    	                 do {
    	                	 Log.i("Pregunta", c.getString(1));
    	                	 
    	                	 View borde = new View(sectionSelector.this);
    	                	 borde.setBackgroundColor(Color.parseColor("#616161"));
    	                	 borde.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 2));
    	                	 
    	                	 LinearLayout cont = new LinearLayout(sectionSelector.this);
    	                	 cont.setOrientation(LinearLayout.VERTICAL);
    	                	 cont.setTag(c.getString(0));
    	                	 cont.setPadding(5, 0, 5, 0);
    	                	 cont.setBackgroundColor(Color.parseColor("#CFDCE9"));
    	                	 
    	                	 TextView titulo = new TextView(sectionSelector.this);
    	                	 titulo.setText("\n"+c.getString(1));
    	                	 titulo.setTextSize(18);
    	                	 titulo.setTextColor(Color.parseColor("#0A0C29"));
    	                	 titulo.setTypeface(null, Typeface.BOLD);
    	                	 cont.addView(titulo);
    	                	 
    	                	 TextView subtitulo = new TextView(sectionSelector.this);
    	                	 subtitulo.setText(c.getString(2)+"\n");
    	                	 subtitulo.setTextSize(12);
    	                	 subtitulo.setTextColor(Color.parseColor("#0A0C29"));
    	                	 cont.addView(subtitulo);
    	                	 
    	                	 final String temp = c.getString(0);
    	                	 
    	                	 cont.setOnClickListener(new View.OnClickListener() {
    	                     	public void onClick(View v) {
    	                     		Toast.makeText(sectionSelector.this, "Sección "+temp, Toast.LENGTH_SHORT).show();
    	                     		Intent myIntent = null;
    	                     		// Detectar si son las pre-existencias
    	                     		if(Integer.parseInt(temp.toString()) == 5){
    	                     			// Si es ua pre existencia.
    	                     			myIntent = new Intent(sectionSelector.this, preExistence.class);
        								myIntent.putExtra("glosaMarca", sectionSelector.this.glosaMarca);
        								myIntent.putExtra("glosaModelo", sectionSelector.this.glosaModelo);
    	                     		}else{
    	                     			myIntent = new Intent(sectionSelector.this, formMaker.class);
    	                     		}
    								myIntent.putExtra("user", user);
    								myIntent.putExtra("pass", pass);
    								myIntent.putExtra("sectionId", temp);
    								myIntent.putExtra("cod_ubicacion", cod_ubicacion);
    								startActivity(myIntent);
    	                     	}
    	                	 });
    	                	 
    	                	 ll.addView(borde);
    	                	 ll.addView(cont);
    	                	 
    	                 } while(c.moveToNext());
    	                 
    	            }
    	            
    	            LinearLayout pie = new LinearLayout(sectionSelector.this);
    	            pie.setOrientation(LinearLayout.HORIZONTAL);
    	            pie.setBackgroundColor(Color.parseColor("#0A0C29"));
    	            pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
    	            pie.setPadding(15, 5, 5, 5);
    	            
    	            TextView isonline = new TextView(sectionSelector.this);
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
    	            
    		        c.close();
    	            db.close();
    	            
    	            sectionSelector.this.setContentView(sv);
    	             
    	        }else{
    	        	Log.i("Ups!","El archivo no existe");
    	        }
        	sectionSelector.this.pd.dismiss();
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
