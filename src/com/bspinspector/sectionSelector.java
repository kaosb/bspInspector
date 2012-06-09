package com.bspinspector;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");
		this.cod_ubicacion = bundle.getString("cod_ubicacion");
        
        /*Instancia el DownloadHelper*/
        Downloader dw = new Downloader();
        
        /*Agregar codigo para controlar la version.*/
        File dbfile = dw.getDB();
        if(dbfile.exists()){
        	
        	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        	
            String result = null;
            
            if(db.isOpen()){
            	result = "BD Open OK";
            }else{
            	result = "BD NO Open";
            }

            Log.i("DB open? ",result);
            
            /*Trabajar con la BD*/
            	String[] args = new String[] {"0"};
            	Cursor c = db.rawQuery("SELECT * FROM section WHERE status=?",args);
            	
	            ScrollView sv = new ScrollView(this);
	            final LinearLayout ll = new LinearLayout(this);
	            ll.setOrientation(LinearLayout.VERTICAL);
	            sv.addView(ll);
	            
	            LinearLayout cabecera = new LinearLayout(this);
	            cabecera.setOrientation(LinearLayout.HORIZONTAL);
	            cabecera.setBackgroundColor(Color.parseColor("#0A0C29"));
	            cabecera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
	            cabecera.setPadding(15, 5, 5, 5);
	            TextView nombre = new TextView(this);
	            nombre.setText(this.user);
	            nombre.setTextSize(18);
	            nombre.setTextColor(Color.parseColor("#FDFDFD"));
	            nombre.setTypeface(null, Typeface.BOLD);
	            cabecera.addView(nombre);
	            ll.addView(cabecera);
	            
	            LinearLayout infoSpace = new LinearLayout(this);
	            infoSpace.setOrientation(LinearLayout.VERTICAL);
	            infoSpace.setBackgroundColor(Color.parseColor("#CFDCE9"));
	            infoSpace.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	            
	            TextView marca = new TextView(this);
	            marca.setPadding(15, 3, 3, 3);
	            marca.setBackgroundColor(Color.parseColor("#FDFDFD"));
	            marca.setTextColor(Color.parseColor("#838383"));
	            marca.setTextSize(14);
	            marca.setTypeface(null, Typeface.BOLD);
	            marca.setText("MARCA: "+bundle.getString("glosa_marca"));
	            infoSpace.addView(marca);
	            
	            TextView modelo = new TextView(this);
	            modelo.setPadding(15, 3, 3, 3);
	            modelo.setBackgroundColor(Color.parseColor("#FDFDFD"));
	            modelo.setTextColor(Color.parseColor("#838383"));
	            modelo.setTextSize(14);
	            modelo.setTypeface(null, Typeface.BOLD);
	            modelo.setText("MODELO: "+bundle.getString("glosa_modelo"));
	            infoSpace.addView(modelo);
	            
	            ll.addView(infoSpace);
	            
	            View bordeinfo = new View(this);
	            bordeinfo.setBackgroundColor(Color.parseColor("#3F8A00"));
	            bordeinfo.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 4));
	            ll.addView(bordeinfo);
	            
	            if (c.moveToFirst()) {
	                 //Recorremos el cursor hasta que no haya más registros
	                 do {
	                	 Log.i("Pregunta", c.getString(1));
	                	 
	                	 View borde = new View(this);
	                	 borde.setBackgroundColor(Color.parseColor("#616161"));
	                	 borde.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 2));
	                	 
	                	 LinearLayout cont = new LinearLayout(this);
	                	 cont.setOrientation(LinearLayout.VERTICAL);
	                	 cont.setTag(c.getString(0));
	                	 cont.setPadding(5, 0, 5, 0);
	                	 cont.setBackgroundColor(Color.parseColor("#CFDCE9"));
	                	 
	                	 TextView titulo = new TextView(this);
	                	 titulo.setText("\n"+c.getString(1));
	                	 titulo.setTextSize(18);
	                	 titulo.setTextColor(Color.parseColor("#0A0C29"));
	                	 titulo.setTypeface(null, Typeface.BOLD);
	                	 cont.addView(titulo);
	                	 
	                	 TextView subtitulo = new TextView(this);
	                	 subtitulo.setText(c.getString(2)+"\n");
	                	 subtitulo.setTextSize(12);
	                	 subtitulo.setTextColor(Color.parseColor("#0A0C29"));
	                	 cont.addView(subtitulo);
	                	 
	                	 final String temp = c.getString(0);
	                	 
	                	 cont.setOnClickListener(new View.OnClickListener() {
	                     	public void onClick(View v) {
	                     		Toast.makeText(sectionSelector.this, temp, Toast.LENGTH_SHORT).show();
								Intent myIntent = new Intent(sectionSelector.this, formMaker.class);
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
	            
	            LinearLayout pie = new LinearLayout(this);
	            pie.setOrientation(LinearLayout.HORIZONTAL);
	            pie.setBackgroundColor(Color.parseColor("#0A0C29"));
	            pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
	            pie.setPadding(15, 5, 5, 5);
	            
	            TextView isonline = new TextView(this);
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
	            
	            this.setContentView(sv);
	             
	        }else{
	        	Log.i("Ups!","El archivo no existe");
	        }
        
	}
    
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
