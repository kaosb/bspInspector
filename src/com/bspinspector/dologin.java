package com.bspinspector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class dologin extends Activity {

	private static final String SOAP_ACTION = "urn:ServiciosBspAction";
	private static final String URL = "http://w4.bsp.cl/ws_bsp/servicio/BspServices.php";
	private TextView isonline;
	private login login;
	private EditText txtUser;
	private EditText txtPass;
	private LinearLayout PanelMensaje;
	private CheckBox cbRemember;
	private ProgressDialog pd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dologin);
        
        /*Respuesta*/
        PanelMensaje = (LinearLayout) findViewById(R.id.LinearLayoutMensaje);
        isonline = (TextView) findViewById(R.id.textViewOnline);
        
        /*Campos*/
        txtUser = (EditText) findViewById(R.id.editText1);
        txtPass = (EditText) findViewById(R.id.editText2);
        cbRemember = (CheckBox)findViewById(R.id.checkBoxRecordar);
        
        /*Button Aceptar*/
        final ImageView btnEntrar = (ImageView) findViewById(R.id.imageViewBtnEntrar);
        checkIfRemember();
        alertOnline();
        
        
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /**
                 * LOGIN
                 * */
            	// ronald.arias y bsp2008
            		alertOnline();
            	    dologin.this.pd = ProgressDialog.show(dologin.this, "", "Cargando...", true, false);
                    new DownloadTask().execute("");
	                
            }
        });
        
	}
	
	
	/**
	 * Clase AsyncTask
	 * */
	private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
        	login = new login(URL,SOAP_ACTION,txtUser.getText().toString(),txtPass.getText().toString(),connectionOK());
			return null;
        	// Ejecucion
        }

        protected void onPostExecute(Object result) {
        	// Pos ejecucion
        	dologin.this.pd.dismiss();
        	if(login.getCodigo().equals("SI")){
            	PanelMensaje.removeAllViewsInLayout();
            	ImageView icono = new ImageView(dologin.this);
            	icono.setImageResource(R.drawable.ok);
            	TextView mensaje = new TextView(dologin.this);
            	mensaje.setTextColor(Color.parseColor("#00FF00"));
            	mensaje.setText("Login correcto.");
            	mensaje.setPadding(5, 0, 0, 0);
            	PanelMensaje.addView(icono);
            	PanelMensaje.addView(mensaje);
            	
            	String rememberString;
            	if(cbRemember.isChecked()){
            		rememberString = "true";
            	}else{
            		rememberString = "false";
            	}

            	rememberMe(txtUser.getText().toString(),txtPass.getText().toString(),rememberString);           
                
            	Intent i = new Intent(dologin.this, dashboard.class);
                i.putExtra("user", txtUser.getText().toString());
                i.putExtra("pass", txtPass.getText().toString());
                
            	if(connectionOK()){
            		Log.i("INTERNET", "conectado");
            	    /*this.pd = ProgressDialog.show(this, "", "Cargando datos..", true, false);
                    new DownloadTask().execute("");*/
            		startActivity(i);
            	}else{
                	Log.i("INTERNET", "no conectado");
                	mensajeCON();
            	}
                
            	
            }else{
            	PanelMensaje.removeAllViewsInLayout();
            	ImageView icono = new ImageView(dologin.this);
            	icono.setImageResource(R.drawable.fail);
            	TextView mensaje = new TextView(dologin.this);
            	mensaje.setTextColor(Color.parseColor("#FF0000"));
            	mensaje.setText("Login incorrecto.");
            	mensaje.setPadding(5, 0, 0, 0);
            	PanelMensaje.addView(icono);
            	PanelMensaje.addView(mensaje);
            	Log.i("LOGIN", login.getCodigo());
            	Log.i("LOGIN", login.getGlosa());
            }
        }
	} 
	/**
	 * fin clase AsyncTask
	 * */
	
	
	/**
	 * Funciones para guardar y chequear si guardo las preferencias.
	 * */
    public void rememberMe(String user,String pass, String check){
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", user);
        editor.putString("pass", pass);
        editor.putString("recordar", check);
        editor.commit();
    }
    
    public void checkIfRemember(){
    	SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String user = sp.getString("user", null);
        String pass = sp.getString("pass", null);
        String recordar = sp.getString("recordar", null);
        if(user != null && pass!= null && recordar!= null){
                EditText etUser = (EditText)findViewById(R.id.editText1);
                EditText etPass = (EditText)findViewById(R.id.editText2);
                etUser.setText(user);
                etPass.setText(pass);
                Intent i = new Intent(dologin.this, dashboard.class);
                i.putExtra("user", user);
                i.putExtra("pass", pass);
                if(recordar.compareTo("true")==0){
	                CheckBox cbRemember = (CheckBox)findViewById(R.id.checkBoxRecordar);
	                cbRemember.setChecked(true);
                	if(connectionOK()){
                		Log.i("INTERNET", "conectado");
                		startActivity(i);
                	}else{
                    	Log.i("INTERNET", "no conectado");
                    	mensajeCON();
                	}
                }
                
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
	 * Genera Mensaje sin conexion
	 * */
	public void mensajeCON(){
		AlertDialog alertDialog = new AlertDialog.Builder(dologin.this).create();
		alertDialog.setTitle("importante");
		alertDialog.setMessage("Para usar esta aplicación, es necesario tener pre-cargados los casos o estar conectado a internet para cargalos.\n\n La aplicacion se cerrara.");
		alertDialog.setButton("ACEPTAR", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which){
			   finish();
		   }
		});
		alertDialog.setIcon(R.drawable.fail);
		alertDialog.show();
	}
	
}
