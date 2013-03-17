package com.bspinspector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class formMaker extends Activity {
	
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	
	protected static final String PHOTO_TAKEN	= "photo_taken";
	
	private String user;
	private String cod_ubicacion;
	private String sectionId;
	
	/*Progress Dialog*/
	private ProgressDialog pd = null;
	
	/**
	 * Elementos necesarios para generar la vista.
	 * */
	/**Linear Layout general*/
	LinearLayout ll;
	/**Linear Layout que donde se despliegan los campos por pagina*/
	LinearLayout llcont;
	
	/**
	 * Campos necesarios para la paginacion
	 * */
	/**Contador mantiene la referencia a la posicion en el total de items por formulario*/
	int itemcount = 0;
	/**Items por pagina*/
	int itemspp = 0;
	
	/**Mantiene visible el archivo con la BD para generar los formularios*/
	File dbfile;
	/**Trae los parametros desde la actividad anterior*/
	Bundle bundle;
	ScrollView sv;

	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bundle = getIntent().getExtras();
		this.user = bundle.getString("user");
		this.cod_ubicacion = bundle.getString("cod_ubicacion");
		this.sectionId = bundle.getString("sectionId");
		this.sv = new ScrollView(this);
		/*Lanzamos mensaje y tarea async*/
	    this.pd = ProgressDialog.show(this, "", "Cargando...", true, false);
        new DownloadTask().execute("");
	}
	
	/**
	 * Clase AsyncTask
	 * */
	private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
        	// Descargamos la BD con el form si es distinta a la version que tenemos.
            Downloader dw = new Downloader();
            dbfile = dw.getDB();
            
            //Consulta que obtiene los settings
            File dbConfFile = getdDBFile();
            if(dbConfFile.exists()){
            	SQLiteDatabase dbConf = SQLiteDatabase.openOrCreateDatabase(dbConfFile, null);
            	//Obtener conf de la BD
            	try{
            		String[] argConf = new String[] {"0",formMaker.this.user};
                	Cursor b = dbConf.query("tbl_settings",
    						new String [] {"type", "value"},
    						"status = ? AND user = ?",
    						argConf,
    						null,
    						null,
    						null);
                	if(b.moveToFirst() && (b.getString(1) != null)){
                		// La configuracion guardada
                		itemspp = b.getInt(1);
                		Log.i("Cantidad conf", b.getString(1));
                	}
                	b.close();
        		} catch (Exception e) {
        			itemspp = 4;
        			Log.i("No se pudo conseguir el dato desde la BD", e.getMessage());
        		}
            	// Cerramos la BD
    	        dbConf.close();
            }
            
            
            // Si tenemos la bd maestra para los forms entramos a consultarla
            if(dbfile.exists()){
                /*Crear Vista*/
    	            ll = new LinearLayout(formMaker.this);
    	            ll.setOrientation(LinearLayout.VERTICAL);
    	            formMaker.this.sv.addView(ll);
    	            
    	            LinearLayout cabecera = new LinearLayout(formMaker.this);
    	            cabecera.setBackgroundColor(Color.parseColor("#0A0C29"));
    	            cabecera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
    	            cabecera.setPadding(10, 0, 10, 0);
    	            cabecera.setGravity(Gravity.CENTER);
    	            
    	            TextView tituloCabecera = new TextView(formMaker.this);
    	            tituloCabecera.setText("Secci—n "+formMaker.this.sectionId);
    	            tituloCabecera.setTextSize(18);
    	            tituloCabecera.setTextColor(Color.parseColor("#FFFFFF"));
    	            tituloCabecera.setTypeface(null, Typeface.BOLD);
    	            
    	            cabecera.addView(tituloCabecera);
    	            
    	            ll.addView(cabecera);
    	            
    	            llcont = new LinearLayout(formMaker.this);
    	            llcont.setOrientation(LinearLayout.VERTICAL);
    	            ll.addView(llcont);                
                
                /*BOTONES*/            
                Button siguiente = new Button(formMaker.this);
                siguiente.setText("Siguiente");
                siguiente.setOnClickListener(new View.OnClickListener(){
                	public void onClick(View v) {
                		String Data = "";
                		int childcount = llcont.getChildCount();
                		
                		for (int i=0; i < childcount; i++){
                		      View vista = llcont.getChildAt(i);
                		      LinearLayout lltemp = (LinearLayout) vista;
                		      Log.i("TAG:", "PreguntaID->"+llcont.getChildAt(i).getTag()+" FieldType:"+llcont.getChildAt(1).getTag());
                		      
                		      String id = (String) llcont.getChildAt(i).getTag();
                		      // Que pasa cuando viene un checkbox??????
                		      // Este id al parecer no viene
                		      int type = 0;
                		      try{
                		    	  type = (Integer) lltemp.getChildAt(1).getTag();
                		      }catch(Exception e){
                		    	  type = 3;
                		      }
                		      TextView txtTemp = (TextView) lltemp.getChildAt(0);
                		      String label = (String) txtTemp.getText();
                		      
                		      switch(type){
                  			
    	               			case 1:
    	               				EditText redtt = (EditText) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+redtt.getText()+"\n";
    	               				if(redtt.getText().toString().length()>0){
    	               					saveFieldValue(Integer.parseInt(id), redtt.getText().toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
                   				
    	               			case 2:
    	               				Spinner spinner = (Spinner) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+spinner.getSelectedItem()+"\n";
    	               				if(!spinner.getSelectedItem().toString().equals("")){
    	               					saveFieldValue(Integer.parseInt(id), spinner.getSelectedItem().toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
    	               				
    	               			case 3:
    	               				final CheckBox checkbox = (CheckBox) findViewById(Integer.parseInt(id));
    	               				if(checkbox != null && checkbox.isChecked()){
    	               					Data= Data+label+": "+checkbox.getId()+"\n";
    	               					saveFieldValue(Integer.parseInt(id), "chek".toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
    	               				
    	               			case 4:
    	               				// TODO Hay que hacer que esta shit funcione 
    	               				RadioGroup rbg = (RadioGroup) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+rbg.getCheckedRadioButtonId()/100+"\n";
    	               				//saveFieldValue(Integer.parseInt(id),"");
    	               				break;
    	               				
    	               			case 5:
    	               				EditText redtn = (EditText) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+redtn.getText()+"\n";
    	               				if(redtn.getText().toString().length()>0){
    	               					saveFieldValue(Integer.parseInt(id), redtn.getText().toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
    	               				
    	               			case 6:
    	               				EditText redte = (EditText) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+redte.getText()+"\n";
    	               				if(redte.getText().toString().length()>0){
    	               					saveFieldValue(Integer.parseInt(id), redte.getText().toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
    	               				
    	               			case 7:
    	               				EditText redtr = (EditText) findViewById(Integer.parseInt(id));
    	               				Data= Data+label+": "+redtr.getText()+"\n";
    	               				if(redtr.getText().toString().length()>0){
    	               					saveFieldValue(Integer.parseInt(id), redtr.getText().toString());
    	               				}else{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}
    	               				break;
    	
    	               			default:
    	               				try{
    	               					saveFieldValue(Integer.parseInt(id), "NULL");
    	               				}catch(Exception e){
    	               					Log.i("Error al capturar", e.getMessage());
    	               				}
    	               				break;
                		      }
       
                		}
                		
                		Toast.makeText(formMaker.this, "Se acaban de guardar en tu equipo los siguientes datos:\n"+Data, Toast.LENGTH_SHORT).show();
        	        	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        	            //Trabajar con la BD
        	            	String[] args = new String[] {"0", sectionId};
        	            	Cursor c = db.query("input",
        	            						new String [] {"id", "section", "name", "type", "dep", "status"},
        	            						"status = ? AND section = ?",
        	            						args,
        	            						null,
        	            						null,
        	            						null);
        	            llcont.removeAllViews();
        	            crearFormulario(c,db,itemspp,itemcount);
        	            
        	        c.close();
                    db.close();
                	}
                });
                
                Button atras = new Button(formMaker.this);
                atras.setText("Atras");
                atras.setOnClickListener(new View.OnClickListener() {
                	public void onClick(View v) {
                		finish();
                	}
                });
                
           	 	LinearLayout botonera = new LinearLayout(formMaker.this);
           	 	botonera.setOrientation(LinearLayout.HORIZONTAL);
           	 	botonera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
           	 	botonera.setBackgroundColor(Color.parseColor("#0A0C29"));
           	 	
           	 	botonera.addView(atras);
           	 	botonera.addView(siguiente);

                ll.addView(botonera);
                
                View pie = new View(formMaker.this);
                pie.setBackgroundColor(Color.parseColor("#0A0C29"));
                pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
                ll.addView(pie);
                
                /*FIN BOTONERA*/

            }
            return false;
        }

        protected void onPostExecute(Object result) {
        	// Pos ejecucion
        	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            //Trabajar con la BD
        	Log.i("seccion", formMaker.this.sectionId);
        	String[] argus = new String[] {"0",formMaker.this.sectionId};
        	Cursor c = db.query("input",
        						new String [] {"id", "section", "name", "type", "dep", "status"},
        						"status = ? AND section = ?",
        						argus,
        						null,
        						null,
        						null);
        	crearFormulario(c,db,itemspp,itemcount);

	        c.close();
            db.close();
        	
        	formMaker.this.setContentView(formMaker.this.sv);
        	formMaker.this.pd.dismiss();
        }
	} 
	/**
	 * fin clase AsyncTask
	 * */
	
	
	/**
	 * GET Archivo DB
	 * Funcion encargada de obtener el archivo con la BD que contiene los settings
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
	 * Get Index of value from spinner adapter
	 */
	private int indexOf(final Adapter adapter, Object value)
	{
	    for (int index = 0, count = adapter.getCount(); index < count; ++index)
	    {
	        if (adapter.getItem(index).equals(value))
	        {
	            return index;
	        }
	    }
	    return -1;
	}
	
	/**
	 * Crear formulario
	 * Funcion encargada de crear la vista del formulario a partir 
	 */
	public void crearFormulario(Cursor c,SQLiteDatabase db,int itemspp, int index){
        /*Fin crear vista*/
        int item = 0;
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToPosition(index) && index < c.getCount()){
             //Recorremos el cursor hasta que no haya mas registros
             do {
            	 Log.i("Pregunta", c.getString(2));
            	 
            	 LinearLayout cont = new LinearLayout(this);
            	 cont.setOrientation(LinearLayout.VERTICAL);
            	 
            	 cont.setTag(c.getString(0));
            	 cont.setPadding(10, 0, 10, 0);
            	 cont.setBackgroundColor(Color.parseColor("#D2DFEC"));
            	 
            	 /*Titulo*/
            	 TextView tv = new TextView(this);
            	 tv.setTextSize(14);
            	 tv.setTextColor(Color.parseColor("#080A1D"));
            	 cont.addView(tv);
            	 
            	 String value;
            	 /*Campo*/
            	 Log.i("Aqui",""+Integer.parseInt(c.getString(3)));
            	 
            	/* if(c.getString(4)!=null){
            		 Log.i("Este es dependiente", c.getString(4));
            		 EditText instancia = (EditText) cont.findViewById(Integer.parseInt(c.getString(4)));
            		 final String tag = c.getString(0);
            		 instancia.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							// TODO Es necesario hacer los campos que poseen cantidades grandes de datos dependientes.
							Toast.makeText(formMaker.this, "Aqui deberia ocurrir algo en: "+ tag, Toast.LENGTH_LONG).show();
						}
            			 
            		 });
            	 }*/
            	 
            	 switch(Integer.parseInt(c.getString(3))){
            	 
            	 case 1:
            		 //Texto
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edt = new EditText(this);
            		 edt.setId(Integer.parseInt(c.getString(0)));
            		 edt.setTag(1);
            		 edt.setInputType(InputType.TYPE_CLASS_TEXT);
            		 cont.addView(edt);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edt.setText(value.toString());
            		 }
            		 break;
            	 case 2:
            		 		// Mantiene las opciones que seran entragadas al spinner
            		 		Cursor options = null;
            		 		// Contador auxiliar que se usa en los bucles para paginar segun lo configurado.
            		 		int count = 0;
            		 		switch(Integer.parseInt(c.getString(0))){
            		 		case 3:
            		 			options = getCustomFieldDataContent(db, "region", "nombreRegion");
            		 			count = options.getCount();
            		 			break;
            		 		case 4:
            		 			options = getCustomFieldDataContent(db, "comuna", "nombreComuna");
            		 			count = options.getCount();
            		 			break;
            		 		case 19:
            		 			options = getCustomFieldDataContent(db, "tipos_vehiculo", "tipo");
            		 			count = options.getCount();
            		 			break;
            		 		default:
            		 			// Consulto BD Con options
	            		 			// Consultamos las opciones asociadas al input
	            		 			String[] args1 = new String[] {"0",c.getString(0)};
	            		 			options = db.query("option",
	            		 					new String [] {"name"},
	            		 					"status = ? AND input = ?",
	            		 					args1,
	            		 					null,
	            		 					null,
	            		 					null);
	            		 			count = options.getCount();
            		 			break;
            		 		}
		            		// creo pero no inicializo el array items
		                	String[] items;
		                	
		                	// Aqui se traspasa los objetos en el Cursor con opciones al array items el cual es entregado al spinner adapter.
		                	if(options.moveToFirst()){
		                		items = new String[count];
		                		int temp = 0;
		                		do{
		                			items[temp] = options.getString(0);
		                			temp++;
		                		}while(options.moveToNext() && options.getPosition() < options.getCount() );
		                	}else{
		                		items = new String[] {"1","2","3","4"};
		                	}
		                	
		                	// Cerramos el Cursor que contenia las opciones
		                	options.close();
		                	// Texto titulo
		                	tv.setText(tv.getText()+"\nSelecciona "+c.getString(2));
		                	
		                	
		                	
		                	// Setting del spinner y el adaptador al cual le paso un array con el contendio.
		                	Spinner select = new Spinner(this);
		                	select.setId(Integer.parseInt(c.getString(0)));
		                	select.setTag(2);
		                	select.setPrompt("Selecciona "+c.getString(2));
		                	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		                	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		                	select.setAdapter(adapter);
		                	//Get saved value
		                	value = getFieldValue(c.getInt(0));
		                	if(value != null){
		                		//int pos = Arrays.binarySearch(items, value);
		                		int pos = this.indexOf(adapter, value);
		                		Log.i("posicion seleccionado", String.valueOf(pos));
		                		select.setSelection(pos);
		                	}
		                	// Lo agrego a la vista
		                	cont.addView(select);
		                	
		                	
		                	break;
            	 case 3:
            		 //checkbox
            		 tv.setText(tv.getText()+"\n"+c.getString(2));
            		 CheckBox checkBox = new CheckBox(this);
            		 checkBox.setId(Integer.parseInt(c.getString(0)));
            		 checkBox.setTag(3);
            		 // Obtenemos el valor guardado en la Base de datos
            		 value = getFieldValue(c.getInt(0));
            		 // Validamos que el valor corresponda a un check.
            		 if(value != null && value.equals("chek")){
            			 checkBox.setChecked(true);
            		 }
            		 cont.addView(checkBox);
            		 break;
            	 case 4:
            		 //RadioGroup // radiobutton
            		 tv.setText(tv.getText()+"\nSelecciona "+c.getString(2));
            		 final RadioButton[] rb = new RadioButton[3];
            		 RadioGroup rbg = new RadioGroup(this);
            		 rbg.setId(Integer.parseInt(c.getString(0)));
            		 rbg.setTag(4);
            		 rbg.setOrientation(RadioGroup.HORIZONTAL);
            		 rbg.clearCheck();
            		 
            		    for(int i=0; i<3; i++){
            		        rb[i]  = new RadioButton(this);
            		        rb[i].setId(Integer.parseInt(c.getString(0))*100);
            		        rbg.addView(rb[i]);
            		        rb[i].setText("Test "+i);
            		        rb[i].setTextSize(14);
            		        rb[i].setTextColor(Color.parseColor("#080A1D"));
            		    }

            		 cont.addView(rbg);
            		 
            		 break;
            	 case 5:
            		 // textedit numero
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edtn = new EditText(this);
            		 edtn.setId(Integer.parseInt(c.getString(0)));
            		 edtn.setTag(5);
            		 edtn.setInputType(InputType.TYPE_CLASS_NUMBER);
            		 cont.addView(edtn);
            		 value = getFieldValue(c.getInt(0));
            		 Log.i("Clase 5", String.valueOf(value));
            		 if(value != null){
            			 edtn.setText(value.toString());
            		 }
            		 
            		 break;
            	 case 6:
            		 // textedit email
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edtm = new EditText(this);
            		 edtm.setId(Integer.parseInt(c.getString(0)));
            		 edtm.setTag(6);
            		 edtm.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
            		 cont.addView(edtm);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edtm.setText(value.toString());
            		 }
            		 
            		 break;
            	 case 7:
            		// textedit rut
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edtr = new EditText(this);
            		 edtr.setTag(7);
            		 edtr.setId(Integer.parseInt(c.getString(0)));
            		 edtr.setInputType(InputType.TYPE_CLASS_NUMBER);
            		 edtr.setKeyListener(DigitsKeyListener.getInstance("0123456789.-"));;
            		 cont.addView(edtr);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edtr .setText(value.toString());
            		 }
            		 
            		 break;
            	 case 8:
            		 // Mantiene las opciones que seran entragadas al spinner
            		 Cursor optionsauto = null;
            		 int countauto = 0;
            		 switch(Integer.parseInt(c.getString(0))){
	            		 case 23:
	            			 optionsauto = getCustomFieldDataContent(db, "marcas", "nombreMarca");
	            			 countauto = optionsauto.getCount();
	            			 break;
	            		 case 24:
	            			 optionsauto = getCustomFieldDataContent(db, "modelos", "nombreModelo");
	            			 countauto = optionsauto.getCount();
	            			 break;
            		 }
            		 if(countauto != 0){
	        			 // creo pero no inicializo el array items
	        			 String[] itemsauto;
	        			 // Aqui se traspasa los objetos en el Cursor con opciones al array items el cual es entregado al spinner adapter.
	        			 if(optionsauto.moveToFirst()){
	        				 itemsauto = new String[countauto];
	        				 int temp = 0;
	        				 do{
	        					 itemsauto[temp] = optionsauto.getString(0);
	        					 temp++;
	        				 }while(optionsauto.moveToNext() && optionsauto.getPosition() < optionsauto.getCount() );
	        			 }else{
	        				 itemsauto = new String[] {"1","2","3","4"};
	        			 }
	        			 // Cerramos el Cursor que contenia las opciones
	        			 optionsauto.close();
	        			 // Texto titulo
	        			 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	        			 // Autocomplete
	        			 AutoCompleteTextView txtmarcaauto = new AutoCompleteTextView(this);
	        			 txtmarcaauto.setId(Integer.parseInt(c.getString(0)));
	        			 // Seteamos el tag como flag para identificar el tipo de campo durante el barrido al momento de almacenar datos.
	        			 txtmarcaauto.setTag(8);
	        			 // Adaptador para el contenido de la consulta pasarlo al autocomplete.
	        			 ArrayAdapter<String> adapterauto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsauto);
	        			 // Incorporo el adaptador
	        			 txtmarcaauto.setAdapter(adapterauto);
	        			 // Obtengo el valor almacenado en la BD
	        			 value = getFieldValue(c.getInt(0));
	        			 // Si el valor es distinto de null setea el valor almacenado en la BD
	        			 if(value != null){
	        				 txtmarcaauto.setText(value);
	        			 }
	        			 // Agrga el elemento a la vista
	        			 cont.addView(txtmarcaauto);
            		 }
            		 break;
            	 case 9:
            		 // textedit fecha
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 final EditText edtf = new EditText(this);
            		 edtf.setId(Integer.parseInt(c.getString(0)));
            		 edtf.setTag(9);
            		 edtf.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
            		 edtf.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
					        // Use the current date as the default date in the picker
					        final Calendar c = Calendar.getInstance();
					        int year = c.get(Calendar.YEAR);
					        int month = c.get(Calendar.MONTH);
					        int day = c.get(Calendar.DAY_OF_MONTH);
					        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener(){
					        	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
					        		edtf.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(monthOfYear)+"-"+String.valueOf(year));
					        	}
					        };
							// Create a new instance of DatePickerDialog and return it
					        DatePickerDialog fecha = new DatePickerDialog(formMaker.this, mDateSetListener, year, month, day);
					        fecha.show();
						}
            		 });
            		 
            		 cont.addView(edtf);
            		 
            		 break;
            	 case 10:
            		 // textedit imagen
            		 tv.setText(tv.getText()+"\nToma "+c.getString(2));
            		 final EditText edti = new EditText(this);
            		 edti.setId(Integer.parseInt(c.getString(0)));
            		 edti.setTag(10);
            		 edti.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            		 edti.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v){
							Toast.makeText(formMaker.this, "Aproach preliminar de la funcionalidad de captura de imagenes.", Toast.LENGTH_LONG).show();
							_path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
							_field = edti;
					    	File file = new File( _path );
					    	Uri outputFileUri = Uri.fromFile( file );
					    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
					    	
					    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
					    	startActivityForResult( intent, 0 );
						}
            		 });
            		 cont.addView(edti);
            		 
            		 break;
            		 default:
            			 break;

            	 }
            	 
            	 llcont.addView(cont);
            	 item++;
            	 Log.i("item", item+"<"+itemspp+"-->"+c.getPosition()+" de "+c.getCount());
            	 
             }while(c.moveToNext() && item < itemspp);
             //Mantiene el index y posicion
             itemcount = c.getPosition();
             Log.i("Index cursor:", ""+itemcount);
             
        }else{
        	finish();
        }
	}
	
	/**
	 * Funcion para obtener el valor de un campo de formulario
	 * */
	public String getFieldValue(int fieldID){
		/**
		 * Bd donde se consultaran los datos
		 * */
		/*Verificar Archivo*/
        File dbfileSaveData = getdDBSaveDataFile();
        SQLiteDatabase dbTarget;
        if(dbfileSaveData.exists()){
        	dbTarget = SQLiteDatabase.openOrCreateDatabase(dbfileSaveData, null);
        }else{
        	dbTarget = createTableDB(dbfileSaveData);
        }
        Cursor count = dbTarget.rawQuery("SELECT value FROM dataInProgress WHERE idInput =" + fieldID + " AND idCase=" + cod_ubicacion, null);
		
        if(count.moveToFirst()){
			Log.i("return->",count.getString(0));
			String valor = count.getString(0);
			count.close();
			dbTarget.close();
			return valor;
		}else{
			count.close();
			dbTarget.close();
			return null;
		}
        
	}
	
	/**
	 * Funcion para guardar el valor de un campo de formulario
	 * */
	public void saveFieldValue(int fieldID, String value){
		
		/**
		 * Bd donde se guardaran los datos
		 * */
		/*Verificar Archivo*/
        File dbfileSaveData = getdDBSaveDataFile();
        SQLiteDatabase dbTarget;
        if(dbfileSaveData.exists()){
        	dbTarget = SQLiteDatabase.openOrCreateDatabase(dbfileSaveData, null);
        }else{
        	dbTarget = createTableDB(dbfileSaveData);
        }
        
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        String date = s.format(new Date());
        
        Cursor count = dbTarget.rawQuery("SELECT COUNT(*) FROM dataInProgress WHERE idInput =" + fieldID + " AND idCase=" + cod_ubicacion, null);
        
        ContentValues newValues = new ContentValues();
        count.moveToFirst();
        if(count.getInt(0)>0){
        	if(value.equals("NULL")){
        		// elimino el row para mantener la consistencia de la persistencia de datos.
        		dbTarget.delete("dataInProgress","idInput =" + fieldID + " AND idCase=" + cod_ubicacion, null);
        	}else{
        		// actualizamos el row para mantener la consistencia de la persistencia de datos.
        		newValues.put("value", value);
        		newValues.put("update_at", date);
        		dbTarget.update("dataInProgress", newValues, "idInput =" + fieldID + " AND idCase=" + cod_ubicacion, null);
        	}
        }else{
        	// insertamos un nuevo row para mantener persistencia de datos.
        	if(!value.equals("NULL")){
        		newValues.put("idCase", cod_ubicacion);
        		newValues.put("idInput", fieldID);
        		newValues.put("value", value);
        		newValues.put("update_at", date);
            	dbTarget.insert("dataInProgress", null, newValues);
        	}
        }
        count.close();
        dbTarget.close();
	}
	
	/**
	 * getdDBSaveDataFile
	 * function encargada de crear y obtener la referencia al archivo donde se almacenan los valores ingresados en el formulario
	 * */
	public File getdDBSaveDataFile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/bspinspector/"+this.user+"/");
        
        if(dir.exists()==false) {
        	dir.mkdirs();
        }
        File dbfile = new File(dir + "/datos.sqlite");
        return dbfile;
	}
	
	/**
	 * createTableDB
	 * Funcion que crea la Tabla si esta no existe.
	 * */
	public SQLiteDatabase createTableDB(File dbfile){
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        Date date = new Date();
        db.setVersion(date.getDate());
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        String SQL1 = "CREATE TABLE 'dataInProgress' ('idData' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 'idCase' INTEGER NOT NULL , 'idInput' INTEGER NOT NULL , 'value' VARCHAR NOT NULL , 'update_at' DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, 'create_at' DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, 'status'  NOT NULL  DEFAULT 0);";
        db.execSQL(SQL1);
        String SQL2 = "CREATE TABLE 'caseInProgress' ('idcase' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 'create_at' DATETIME DEFAULT CURRENT_TIMESTAMP, 'update_at' DATETIME DEFAULT CURRENT_TIMESTAMP, 'sended_at' DATETIME DEFAULT NULL, 'status' INTEGER DEFAULT 0);";
        db.execSQL(SQL2);
        return db;
	}
	
	/**
	 * getCustomDataContent
	 * funcion que obtiene datos de un campo especifico de la tabla especificada y retorna un cursos con la informacion respectiva.
	 * @return 
	 */
	public Cursor getCustomFieldDataContent(SQLiteDatabase db, String tableName, String fieldName){
		Cursor data = null;
		data = db.rawQuery("SELECT "+fieldName+" FROM "+tableName+" WHERE "+fieldName+" NOTNULL",null);
		return data;
	}
	
	/**
	 * getTableDataContent
	 * funcion que obtiene toda la informacion de la tabla especificada.
	 * @return 
	 */
	public Cursor getTableDataContent(SQLiteDatabase db, String tableName){
		Cursor data = null;
		data = db.rawQuery("SELECT * FROM "+tableName, null);
		return data;
	}
	
	/**
	 * photoPicker
	 * Funciones para capturar imagenes.
	 * */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			break;
    			
    		case -1:
    			onPhotoTaken();
    			break;
    	}
    }
    protected void onPhotoTaken()
    {
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
    	
    	_image.setImageBitmap(bitmap);
    	
    	_field.setVisibility( View.GONE );
    }
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( formMaker.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( formMaker.PHOTO_TAKEN, _taken );
    }
	
}
