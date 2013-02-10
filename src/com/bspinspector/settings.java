package com.bspinspector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        
        /*DB*/
        File dbfile = getdDBFile();
        SQLiteDatabase db;
        if(dbfile.exists()){
        	db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            final String CREATE_TABLE_LOGIN = 
            		"CREATE TABLE IF NOT EXISTS tbl_settings ("
            				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            				+ "type TEXT,"
            				+ "value TEXT,"
            				+ "user TEXT,"
            				+ "datecreate INTEGER,"
            				+ "status INTEGER);";
            db.execSQL(CREATE_TABLE_LOGIN);
        }else{
        	db = createTableDB(dbfile);
        }
        
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = s.format(new Date());
        
        String[] args = new String[] {"0",user,"1"};
    	Cursor c = db.query("tbl_settings",
				new String [] {"id", "type", "value", "user", "datecreate", "status"},
				"status = ? AND user = ? AND type = ?",
				args,
				null,
				null,
				null);
    	
    	if(c.moveToFirst()){
       		ContentValues newValues = new ContentValues();
    		newValues.put("value", cantidad);
    		newValues.put("datecreate", date);
    		db.update("tbl_settings", newValues, "id" + "=" + c.getString(0), null);
    	}else{
    		db.execSQL("INSERT INTO tbl_settings (type,value,user,datecreate,status) VALUES ('1','"+cantidad+"','"+user+"','"+date+"','0') ");
    	}
        db.close();
        c.close();
        
        /*FIN DB*/
        
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
        		"CREATE TABLE IF NOT EXISTS tbl_settings ("
        				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        				+ "type TEXT,"
        				+ "value TEXT,"
        				+ "user TEXT,"
        				+ "datecreate INTEGER,"
        				+ "status INTEGER);";
        db.execSQL(CREATE_TABLE_LOGIN);
        
        return db;
	}
	
}
