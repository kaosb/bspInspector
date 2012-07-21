package com.bspinspector;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class listaCasos extends Activity {

	private String user;
	private String pass;
	private TextView isonline;
	private ListView listview;
	private ArrayList<caso> mListItem;
	private ProgressDialog pd = null;
	private SQLiteDatabase db;
	private File dbfile;
	private ArrayList<caso> list;
	private caso caso;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_casos);
		
		listview = (ListView) findViewById(R.id.list_view);
        list = new ArrayList<caso>();

		/*Get DATA*/
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.pass = bundle.getString("pass");
		
		/*Saludo Bienvenida*/
		TextView mensajeBienvenida = (TextView) findViewById(R.id.textViewSaludo);
		mensajeBienvenida.setText("Bienvenido "+this.user);
		
		isonline = (TextView) findViewById(R.id.textViewOnline);
		alertOnline();

			
		/*Verificar Archivo*/
		dbfile = getdDBFile();
		
		if(dbfile.exists()){
		    this.pd = ProgressDialog.show(this, "", "Cargando...", true, false);
	        new DownloadTask().execute("");
		}else{
			mensajeBD();
		}
		
	}
	
	
	/**
	 * Clase AsyncTask
	 * */
	private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
        	
			db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			
	    	Cursor c = db.query("tbl_casos",
					new String [] {"cod_ubicacion","cod_ramo","glosa_ramo","cod_cia","glosa_cia","nom_asegurado","nom_contacto","telefono","direccion","cod_comuna","glosa_comuna","glosa_marca","glosa_modelo","comentario","patente","corredor","observaciones","lleva_decla_datos_adic","lleva_propuesta","lleva_poliza","fecha_visita","hora_visita","datecreate","status"},
					null,
					null,
					null,
					null,
					null);
	    	
	    	/*Recorrer BD*/
	    	if(c.moveToFirst()){
		        do{
		        	/*Creo un nuevo objeto caso para desplegar esta informacion*/
			        caso = new caso();
			        caso.setCod_ubicacion(c.getString(0));
			        caso.setCod_ramo(c.getString(1));
			        caso.setGlosa_ramo(c.getString(2));
			        caso.setCod_cia(c.getString(3));
			        caso.setGlosa_cia(c.getString(4));
			        caso.setNom_asegurado(c.getString(5));
			        caso.setNom_contacto(c.getString(6));
			        caso.setTelefono(c.getString(7));
			        caso.setDireccion(c.getString(8));
			        caso.setCod_comuna(c.getString(9));
			        caso.setGlosa_comuna(c.getString(10));
			        caso.setGlosa_marca(c.getString(11));
			        caso.setGlosa_modelo(c.getString(12));
			        caso.setComentario(c.getString(13));
			        caso.setPatente(c.getString(14));
			        caso.setCorredor(c.getString(15));
			        caso.setObservaciones(c.getString(16));
			        caso.setLleva_decla_datos_adic(c.getString(17));
			        caso.setLleva_propuesta(c.getString(18));
			        caso.setLleva_poliza(c.getString(19));
			        caso.setFecha_visita(c.getString(20));
			        caso.setHora_visita(c.getString(21));
			        caso.setStatus(c.getString(23));
			        
			        /*Lo almaceno en una BD local*/
			        
			        
			        /*Lo agrego a la lista*/
			        list.add(caso);
		        }while(c.moveToNext());
	    	}
	    	
			c.close();
			db.close();
        	
			return null;
        }

        protected void onPostExecute(Object result) {
			mListItem = list;
			listview.setAdapter(new ListAdapter(listaCasos.this, R.id.list_view, mListItem));
        	listaCasos.this.pd.dismiss();
        }
	} 
	/**
	 * fin clase AsyncTask
	 * */
	
	
	/**
	 * Existe para evitar conflicto
	 * */
	public void onClick(View v) {
	}
	/**
	 * ListAdapter
	 * */
	public class ListAdapter extends ArrayAdapter<Object>{
		
		/**
		 * Atributos
		 * */
		private ArrayList<caso> mList;
		/**
		 * Constructor
		 * */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ListAdapter(Context context, int textViewResourceId, ArrayList list){
			super(context, textViewResourceId, list);
			this.mList = list;
		}
	
		/**
		 * Metodos
		 * */
		public View getView(int position, View convertView, ViewGroup parent){
			View view = convertView;
			try{
				if(view == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.item_casos, null);
				}
				final caso listItem = (caso) mList.get(position);
				if(listItem != null) {
					// setting list_item views
					((TextView) view.findViewById(R.id.textViewncasotxt)).setText(listItem.getCod_ubicacion());
					TextView status = (TextView) view.findViewById(R.id.textViewEstadotxt);
					switch(Integer.parseInt(listItem.getStatus())){
					case 0:
						status.setText("Pendiente");
						status.setBackgroundColor(Color.parseColor("#F4FA58"));
						break;
					case 1:
						status.setText("Incompleto");
						status.setBackgroundColor(Color.parseColor("#F4FA58"));
						break;
					case 2:
						status.setText("Completo");
						status.setBackgroundColor(Color.parseColor("#01DF01"));
						break;
					default:
						status.setText("Indeterminado");
						status.setBackgroundColor(Color.parseColor("#DF0101"));
						break;
					}
					((TextView) view.findViewById(R.id.textViewnDirecciontxt)).setText(listItem.getDireccion());
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							Intent myIntent = new Intent(listaCasos.this, sectionSelector.class);
							myIntent.putExtra("user", user);
							myIntent.putExtra("pass", pass);
							myIntent.putExtra("cod_ubicacion", listItem.getCod_ubicacion());
							myIntent.putExtra("glosa_marca", listItem.getGlosa_marca());
							myIntent.putExtra("glosa_modelo", listItem.getGlosa_modelo());
							myIntent.putExtra("direccion", listItem.getDireccion());
							startActivity(myIntent);
						}
					});
				}
			} catch (Exception e) {
				Log.i(listaCasos.ListAdapter.class.toString(), e.getMessage());
			}
			return view;
		}
	}
    /**
     * Alerta Online Offline
     * */
    public void alertOnline(){
        isonline.setPadding(15, 0, 0, 0);
        isonline.setTypeface(null, Typeface.BOLD);
        if(connectionOK()){
        	isonline.setText("ONLINE");
        	isonline.setTextColor(Color.parseColor("#00FF00"));
        }else{
        	isonline.setText("OFFLINE");
        	isonline.setTextColor(Color.parseColor("#FF0000"));
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
	/**
	 * Genera Mensaje sin BD cargar
	 * */
	public void mensajeBD(){
		AlertDialog alertDialog = new AlertDialog.Builder(listaCasos.this).create();
		alertDialog.setTitle("importante");
		alertDialog.setMessage("Antes debes cargar los casos desde nuestros servidores.");
		alertDialog.setButton("Cargar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which){
				Intent myIntent = new Intent(listaCasos.this, getNuevosCasos.class);
				myIntent.putExtra("user", user);
				myIntent.putExtra("pass", pass);
				startActivity(myIntent);
				finish();
			}
		});
		alertDialog.setIcon(R.drawable.fail);
		alertDialog.show();
	}
	/**
	 * GET Archivo DB
	 * */
	public File getdDBFile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/bspinspector/"+this.user+"/");
        
        if(dir.exists()==false) {
        	dir.mkdirs();
        }
        File dbfile = new File(dir + "/casos.sqlite");
        return dbfile;
	}
	
}
