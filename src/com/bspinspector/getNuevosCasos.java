package com.bspinspector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class getNuevosCasos extends Activity {
	
	private static final String SOAP_ACTION = "urn:ServiciosBspAction";
	private static final String URL = "http://w4.bsp.cl/ws_bsp/servicio/BspServices.php";
	private String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_nuevos_casos);
		
		Bundle bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		
		int agregados = 0;
		int actualizados = 0;
		
		/*Verifico la conexion*/
		if(connectionOK()){
			
			/*Verificar Archivo*/
	        File dbfile = getdDBFile();
	        SQLiteDatabase db;
	        if(dbfile.exists()){
	        	db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
	        }else{
	        	db = createTableDB(dbfile);
	        }
	        
	        /*Get datos*/
	        /**
	         * Cliente SOAP
	         * */
	        soapClient cliente = new soapClient();
	        
	        /**
	         * Envelope RUTA
	         * */
	        String envelopeRuta = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
	        				+"<soapenv:Header/>"
	        				+"<soapenv:Body>"
	        				+"<usr_inspector xsi:type=\"xsd:string\"><![CDATA[<carga><usr_inspector>"+this.user+"</usr_inspector></carga>]]></usr_inspector>"
	        				+"</soapenv:Body>"
	        				+"</soapenv:Envelope>";
	        
	        String responseRuta = cliente.CallWebService(URL,SOAP_ACTION,envelopeRuta);
	        
	        /**
	         * Parser RUTA
	         * */
	        casos ruta = new casos();
	        ruta.parseRuta(responseRuta);
	        Document doc;
	        		
	        for(int i = 0;i+1<=ruta.inspecciones.length;i++){
	        	
	        	doc = Jsoup.parse(ruta.inspecciones[i]);
		        
		        /*Lo almaceno en una BD local*/
	            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	            String date = s.format(new Date());
		        String[] args = new String[] {doc.select("cod_ubicacion").text().trim()};
		    	Cursor c = db.query("tbl_casos",
						new String [] {"cod_ubicacion"},
						"cod_ubicacion = ?",
						args,
						null,
						null,
						null);
		    	
		    	/*Recorrer BD*/
		    	if(c.moveToFirst()){
		    		/*Actualiza datos*/
		       		ContentValues newValues = new ContentValues();
		    		newValues.put("cod_ubicacion",doc.select("cod_ubicacion").text().trim());
		    		newValues.put("cod_ramo", doc.select("cod_ramo").text().trim());
		    		newValues.put("glosa_ramo", doc.select("glosa_ramo").text().trim());
		    		newValues.put("cod_cia", doc.select("cod_cia").text().trim());
		    		newValues.put("nom_asegurado", doc.select("nom_asegurado").text().trim());
		    		newValues.put("nom_contacto", doc.select("nom_contacto").text().trim());
		    		newValues.put("telefono", doc.select("telefono").text().trim());
		    		newValues.put("direccion", doc.select("direccion").text().trim());
		    		newValues.put("cod_comuna", doc.select("cod_comuna").text().trim());
		    		newValues.put("glosa_comuna", doc.select("glosa_comuna").text().trim());
		    		newValues.put("glosa_marca", doc.select("glosa_marca").text().trim());
		    		newValues.put("glosa_modelo", doc.select("glosa_modelo").text().trim());
		    		newValues.put("comentario", doc.select("comentario").text().trim());
		    		newValues.put("patente", doc.select("patente").text().trim());
		    		newValues.put("corredor", doc.select("corredor").text().trim());
		    		newValues.put("observaciones", doc.select("observaciones").text().trim());
		    		newValues.put("lleva_decla_datos_adic", doc.select("lleva_decla_datos_adic").text().trim());
		    		newValues.put("lleva_propuesta", doc.select("lleva_propuesta").text().trim());
		    		newValues.put("lleva_poliza", doc.select("lleva_poliza").text().trim());
		    		newValues.put("fecha_visita", doc.select("fecha_visita").text().trim());
		    		newValues.put("hora_visita", doc.select("hora_visita").text().trim());
		    		newValues.put("datecreate", date);
		    		newValues.put("status", "0");
		    		db.update("tbl_casos", newValues, "cod_ubicacion" + "=" + c.getString(0), null);
		    		actualizados++;
		    	}else{
		    		/*Inserta datos*/
		    		db.execSQL("INSERT INTO tbl_casos ("
		    											+ "cod_ubicacion,"
		    											+ "cod_ramo,"
		    											+ "glosa_ramo,"
		    											+ "cod_cia,"
		    					        				+ "glosa_cia,"
		    					        				+ "nom_asegurado,"
		    					        				+ "nom_contacto,"
		    					        				+ "telefono,"
		    					        				+ "direccion,"
		    					        				+ "cod_comuna,"
		    					        				+ "glosa_comuna,"
		    					        				+ "glosa_marca,"
		    					        				+ "glosa_modelo,"
		    					        				+ "comentario,"
		    					        				+ "patente,"
		    					        				+ "corredor,"
		    					        				+ "observaciones,"
		    					        				+ "lleva_decla_datos_adic,"
		    					        				+ "lleva_propuesta,"
		    					        				+ "lleva_poliza,"
		    					        				+ "fecha_visita,"
		    					        				+ "hora_visita,"
		    					        				+ "datecreate,"
		    					        				+ "status)"
		    											+ "VALUES ('"
		    					        					+doc.select("cod_ubicacion").text().trim()+"','"
		    					        					+doc.select("cod_ramo").text().trim()+"','"
		    					        					+doc.select("glosa_ramo").text().trim()+"','"
		    					        					+doc.select("cod_cia").text().trim()+"','"
		    					        					+doc.select("glosa_cia").text().trim()+"','"
		    					        					+doc.select("nom_asegurado").text().trim()+"','"
		    					        					+doc.select("nom_contacto").text().trim()+"','"
		    					        					+doc.select("telefono").text().trim()+"','"
		    					        					+doc.select("direccion").text().trim()+"','"
		    					        					+doc.select("cod_comuna").text().trim()+"','"
		    					        					+doc.select("glosa_comuna").text().trim()+"','"
		    					        					+doc.select("glosa_marca").text().trim()+"','"
		    					        					+doc.select("glosa_modelo").text().trim()+"','"
		    					        					+doc.select("comentario").text().trim()+"','"
		    					        					+doc.select("patente").text().trim()+"','"
		    					        					+doc.select("corredor").text().trim()+"','"
		    					        					+doc.select("observaciones").text().trim()+"','"
		    					        					+doc.select("lleva_decla_datos_adic").text().trim()+"','"
		    					        					+doc.select("lleva_propuesta").text().trim()+"','"
		    					        					+doc.select("lleva_poliza").text().trim()+"','"
		    					        					+doc.select("fecha_visita").text().trim()+"','"
		    					        					+doc.select("hora_visita").text().trim()+"','"
		    					        					+date+"','0') ");
		    		agregados++;
		    	}
		    	/*Fin bucle cierro cursor*/
		        c.close();
	        }
	    	/*Cerrar conexion y cursor*/
	        db.close();
			
		/*Instanciar elementos mensaje*/
	        TextView mensaje = (TextView) findViewById(R.id.textView1);
	        
	        if(agregados>0){
	        	mensaje.setText("Se cargaron " + agregados + " casos.\n");
	        }else{
	        	mensaje.setText("No hay casos nuevos.\n");
	        }
	        
	        if(actualizados>0){
	        	mensaje.setText(mensaje.getText()+"Se actualizaron " + actualizados + " casos.\n");
	        }else{
	        	mensaje.setText(mensaje.getText()+"No hay casos actualizados.\n");
	        }
	        
	        Button ok = (Button) findViewById(R.id.button1);
	        ok.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					finish();
				}
			});
			
		}else{
			/*Alerta sin conexion*/
			mensajeCON();
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
	 * Genera Mensaje sin conexion
	 * */
	public void mensajeCON(){
		AlertDialog alertDialog = new AlertDialog.Builder(getNuevosCasos.this).create();
		alertDialog.setTitle("importante");
		alertDialog.setMessage("Para realizar esta accion, es necesario estar conectado a internet.");
		alertDialog.setButton("ACEPTAR", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which){
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
	
	/**
	 * GET FILE DB
	 * */
	public SQLiteDatabase createTableDB(File dbfile){
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        Date date = new Date();
        db.setVersion(date.getDate());
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        
        final String CREATE_TABLE_CASOS = 
        		"CREATE TABLE tbl_casos ("
        				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        				+ "cod_ubicacion TEXT,"
        				+ "cod_ramo TEXT,"
        				+ "glosa_ramo TEXT,"
        				+ "cod_cia TEXT,"
        				+ "glosa_cia TEXT,"
        				+ "nom_asegurado TEXT,"
        				+ "nom_contacto TEXT,"
        				+ "telefono TEXT,"
        				+ "direccion TEXT,"
        				+ "cod_comuna TEXT,"
        				+ "glosa_comuna TEXT,"
        				+ "glosa_marca TEXT,"
        				+ "glosa_modelo TEXT,"
        				+ "comentario TEXT,"
        				+ "patente TEXT,"
        				+ "corredor TEXT,"
        				+ "observaciones TEXT,"
        				+ "lleva_decla_datos_adic TEXT,"
        				+ "lleva_propuesta TEXT,"
        				+ "lleva_poliza TEXT,"
        				+ "fecha_visita TEXT,"
        				+ "hora_visita TEXT,"
        				+ "datecreate INTEGER,"
        				+ "status INTEGER);";
        db.execSQL(CREATE_TABLE_CASOS);
        
        return db;
	}
	
}
