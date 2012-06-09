package com.bspinspector;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");
        
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
	            TextView nombre = new TextView(this);
	            nombre.setText(this.user);
	            nombre.setTextSize(18);
	            nombre.setTextColor(Color.parseColor("#FDFDFD"));
	            nombre.setTypeface(null, Typeface.BOLD);
	            cabecera.addView(nombre);
	            ll.addView(cabecera);
	            
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
	                     		startActivity(new Intent(sectionSelector.this, formMaker.class).putExtra("sectionId", temp));
	                     	}
	                	 });
	                	 
	                	 ll.addView(borde);
	                	 ll.addView(cont);
	                	 
	                 } while(c.moveToNext());
	                 
	            }
	            
	            View pie = new View(this);
	            pie.setBackgroundColor(Color.parseColor("#0A0C29"));
	            pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
	            ll.addView(pie);
	            
		        c.close();
	            db.close();
	            
	            this.setContentView(sv);
	             
	        }else{
	        	Log.i("Ups!","El archivo no existe");
	        }
        
	}
}
