package com.bspinspector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class asyncSnippet extends Activity {

	private ProgressDialog pd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*Lanzamos mensaje y tarea async*/
	    this.pd = ProgressDialog.show(this, "", "Cargando...", true, false);
        new DownloadTask().execute("");
		
	}
	
	/**
	 * Clase AsyncTask
	 * */
	private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
			return null;
        	// Ejecucion
        }

        protected void onPostExecute(Object result) {
        	// Pos ejecucion
        	asyncSnippet.this.pd.dismiss();
        }
	} 
	/**
	 * fin clase AsyncTask
	 * */
}
