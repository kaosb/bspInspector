package com.bspinspector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

public class login {
	
	private String codigo;
	private String glosa;
	
	login(String URL,String SOAP_ACTION, String user, String pass, Boolean online){
		
		if(online){
		
        /**
         * Cliente SOAP
         * */
        soapClient cliente = new soapClient();
        
        /**
         * Envelope LOGIN
         * */
        String envelopeLogin = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        				+"<soapenv:Header/>"
        				+"<soapenv:Body>"
        				+"<loginpass xsi:type=\"xsd:string\">"+"<![CDATA[<dato_inspector><usuario>"+user+"</usuario><pass>"+pass+"</pass></dato_inspector>]]>"+"</loginpass>"
        				+"</soapenv:Body>"
        				+"</soapenv:Envelope>";
        String responseLogin = cliente.CallWebService(URL,SOAP_ACTION,envelopeLogin);
        this.parseLogin(responseLogin);
        File dbfile = getdDBFile();
        SQLiteDatabase db;
        if(dbfile.exists()){
        	db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        }else{
        	db = createTableDB(dbfile);
        }
        Date date = new Date();
        String[] args = new String[] {"0",user,pass};
    	Cursor c = db.query("tbl_login",
				new String [] {"id", "user", "pass", "datecreate", "status"},
				"status = ? AND user = ? AND pass = ?",
				args,
				null,
				null,
				null);
    	if(c.moveToFirst()){
       		ContentValues newValues = new ContentValues();
    		newValues.put("user",user);
    		newValues.put("pass", pass);
    		newValues.put("datecreate", DateFormat.getDateTimeInstance().format(new Date()));
    		db.update("tbl_login", newValues, "id" + "=" + c.getString(0), null);
    	}else{
    		db.execSQL("INSERT INTO tbl_login (user,pass,datecreate,status) VALUES ('"+user+"','"+pass+"','"+date.getDay()+"/"+date.getMonth()+"/"+date.getYear()+"','0') ");
    	}
        db.close();
        c.close();
		}else{
			
			File dbfile = getdDBFile();
	        if(dbfile.exists()){
		        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
	        	String[] args = new String[] {"0",user,pass};
	        	Cursor c = db.query("tbl_login",
						new String [] {"id", "user", "pass", "datecreate", "status"},
						"status = ? AND user = ? AND pass = ?",
						args,
						null,
						null,
						null);
	        	c.moveToFirst();
	        	if(c.isFirst() && !c.isNull(1) && !c.isNull(2) && c.getString(1).compareTo(user) == 0 && c.getString(2).compareTo(pass) == 0){
	        	this.setCodigo("SI");
	        	this.setGlosa("EXISTE EN LA BD");
	        	Log.i("LOGINBD", "User: "+c.getString(1)+" - "+"Password: "+c.getString(2));
	        	}else{
		        	this.setCodigo("NO");
		        	this.setGlosa("NO EXISTE EN LA BD");
	        	}
		        c.close();
	            db.close();
	        }else{
	        	createTableDB(dbfile);
				this.setCodigo("NO");
				this.setGlosa("NO DB OFFLINE");
	        }
	        
		}
		
	}
	
	/**
	 * Getter & Setter
	 * */
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getGlosa() {
		return glosa;
	}
	public void setGlosa(String glosa) {
		this.glosa = glosa;
	}
	
	/**
	 * Metodos
	 * */
	public void parseLogin(String response){
        /**
         * Codigo para agregar a la clase login
         */
        
    	ByteArrayInputStream xmlStream = new ByteArrayInputStream(response.getBytes());

    	XmlPullParser parser = Xml.newPullParser();
    	try {
    		parser.setInput(xmlStream, "UTF-8");

    		int event = parser.next();
    		
    		while(event != XmlPullParser.END_DOCUMENT) {
    			if(event == XmlPullParser.START_TAG) {
    				Log.d("XML", "<" + parser.getName() + ">");
    				for(int i = 0; i < parser.getAttributeCount(); i++) {
    					Log.d("XML", "\t" + parser.getAttributeName(i) + " = " + parser.getAttributeValue(i));
    				}
    			}
    			
    			if(event == XmlPullParser.TEXT && parser.getText().trim().length() != 0){
    				Log.d("XML", "\t\t" + parser.getText());
    				
    				int iniResp = parser.getText().indexOf("<cod_respuesta>");
    				int endResp = parser.getText().indexOf("</cod_respuesta>");
    				
    				CharSequence cod_resupuestaCH = parser.getText().subSequence(iniResp, endResp);
    				String cod_respuesta = cod_resupuestaCH.toString();
    				
    				int iniGResp = parser.getText().indexOf("<glosa_respuesta>");
    				int endGResp = parser.getText().indexOf("</glosa_respuesta>");
    				
    				CharSequence glosa_resupuestaCH = parser.getText().subSequence(iniGResp, endGResp);
    				String glosa_respuesta = glosa_resupuestaCH.toString();
    				
    				this.setCodigo(cod_respuesta.substring(15));
    				this.setGlosa(glosa_respuesta.substring(17));
    			}

    			if(event == XmlPullParser.END_TAG)
    				Log.d("XML", "</" + parser.getName() + ">");

    			event = parser.next();
    		}
    		
    		xmlStream.close();
    		
    		Log.d("OK","Leido correctamenteee.");
    		
    	} catch (Exception e) {
    		Log.d("Error","Error al leer el XML.");
    	}
        
        /**
         * Fin codigo para agregar a la clase login
         */
	}
	
	/**
	 * GET Archivo DB
	 * */
	public File getdDBFile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/bspinspector/conf/");
        
        if(dir.exists()==false) {
        	dir.mkdirs();
        }
        File dbfile = new File(dir + "/BSP.sqlite");
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
        
        final String CREATE_TABLE_LOGIN = 
        		"CREATE TABLE tbl_login ("
        				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        				+ "user TEXT,"
        				+ "pass TEXT,"
        				+ "datecreate INTEGER,"
        				+ "status INTEGER);";
        db.execSQL(CREATE_TABLE_LOGIN);
        
        return db;
	}

}
