package com.bspinspector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class formMaker extends Activity {
	
	public static final int CAMERA_REQUEST = 1000;
	public Uri mFileUri = null;
	public TextView filesUploaded;
	
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
	int itemspp = 4;
	
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
                
    	        /**
    	         * ########### BOTONES
    	         */           
                Button siguiente = new Button(formMaker.this);
                siguiente.setText("Siguiente");
                siguiente.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                    	Boolean passport = true;
                    	String Data = "";
                    	int childcount = llcont.getChildCount();
                    	for (int i=0; i < childcount; i++){
                    		View vista = llcont.getChildAt(i);
                    		LinearLayout lltemp = (LinearLayout) vista;
                    		//Log.i("TAG:", "PreguntaID->"+llcont.getChildAt(i).getTag()+" FieldType:"+ lltemp.getChildAt(1).getTag());
                    		String id = (String) llcont.getChildAt(i).getTag();
                    		Log.i("BLEBLE", (String) llcont.getChildAt(i).getTag());
                    		// Este id al parecer no viene
                    		int type = 0;
                    		try{
                    			type = (Integer) lltemp.getChildAt(1).getTag();
                    		}catch(Exception e){
                    			type = 1;
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
                    			RadioGroup rbg = (RadioGroup) findViewById(Integer.parseInt(id));
                    			if(rbg.getCheckedRadioButtonId() != -1){
	                    			RadioButton rb = (RadioButton) findViewById(rbg.getCheckedRadioButtonId());
	                    			Data= Data+label+": "+rb.getText()+"\n";
	                    			if(rb.getText().toString().length()>0){
	                    				saveFieldValue(Integer.parseInt(id), String.valueOf(rb.getText()));
	                    			}else{
	                    				saveFieldValue(Integer.parseInt(id), "NULL");
	                    			}
                    			}
                    			
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
                    			// Correo electronico
                    			EditText redte = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redte.getText()+"\n";
                    			if(redte.getText().toString().length()>0){
                    				if(validateEmailAddress(redte.getText().toString())){
                    				saveFieldValue(Integer.parseInt(id), redte.getText().toString());
                    				}else{
                        				passport = false;
                        				redte.setError("Correo electronico invalido.");
                    				}
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			break;

                    		case 7:
                    			EditText redtr = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redtr.getText()+"\n";
                    			if(redtr.getText().toString().length()>0){
                    				// Creamos un arreglo con el rut y el digito verificador
                    				String[] rut_dv = redtr.getText().toString().split("-");
                    				// Las partes del rut (numero y dv) deben tener una longitud positiva
                    				if(rut_dv.length == 2){
                    					int rut = Integer.parseInt( rut_dv[0]);
                    					char dv = rut_dv[1].charAt(0);
                    					// Validamos que sea un rut valido según la norma
                    					if (ValidarRut(rut, dv)){
                    						saveFieldValue(Integer.parseInt(id), redtr.getText().toString());
                    					}else{
                    						passport = false;
                    						redtr.setError("RUT invalido.");
                    					}
                    				}else{
                						passport = false;
                						redtr.setError("Formato RUT invalido.\n Ej: 11222333-4");
                    				}
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			break;
                    			
                    		case 8:
                    			EditText edtauto = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+edtauto.getText()+"\n";
                    			if(edtauto.getText().toString().length()>0){
                    				saveFieldValue(Integer.parseInt(id), edtauto.getText().toString());
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			break;
                    			
                    		case 9:
                    			EditText redtd = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redtd.getText()+"\n";
                    			if(redtd.getText().toString().length()>0){
                    				saveFieldValue(Integer.parseInt(id), redtd.getText().toString());
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			
                    			break;
                    			
                    		case 10:
                    			Data= Data+label+"Imagenes: "+filesUploaded.getText()+"";
                    			if(filesUploaded.getText().toString().length()>0){
                    				saveFieldValue(Integer.parseInt(id), filesUploaded.getText().toString());
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			
                    			break;
                    			
                    		case 11:
                    			// TODO Hay que hacer el metodo para este tipo de datos FIRMA
                    			break;
                    			
                    		case 12:
                    			// telefono
                    			EditText redtph = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redtph.getText()+"\n";
                    			if(redtph.getText().toString().length()>0){
                    				if(redtph.getText().toString().length()>=6 && redtph.getText().toString().length()<=12){
                    					saveFieldValue(Integer.parseInt(id), redtph.getText().toString());
                    				}else if(redtph.getText().toString().length()<6){
                						passport = false;
                						redtph.setError("Telefono invalido. (muy corto)");
                    				}else if(redtph.getText().toString().length()>12){
                						passport = false;
                						redtph.setError("Telefono invalido. (muy largo)");
                    				}
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			break;
                    			
                    		case 13:
                    			// patente
                    			EditText redtpa = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redtpa.getText()+"\n";
                    			if(redtpa.getText().toString().length()>0){
                    				if(ValidarPatente(redtpa.getText().toString())){
                    					saveFieldValue(Integer.parseInt(id), redtpa.getText().toString());	
                    				}else{
                						passport = false;
                						redtpa.setError("Formato patente invalido.\n Ej: LL0000 o LLLL00");
                    				}                    					
                    			}else{
                    				saveFieldValue(Integer.parseInt(id), "NULL");
                    			}
                    			break;
                    			
                    		case 14:
                    			EditText redtti = (EditText) findViewById(Integer.parseInt(id));
                    			Data= Data+label+": "+redtti.getText()+"\n";
                    			if(redtti.getText().toString().length()>0){
                    				saveFieldValue(Integer.parseInt(id), redtti.getText().toString());
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
                    	if(passport){
	                    	Toast.makeText(formMaker.this, "Se acaban de guardar en tu equipo los siguientes datos:\n"+Data, Toast.LENGTH_SHORT).show();
	                    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
	                    	//Trabajar con la BD
	                    	String[] args = new String[] {"1", sectionId};
	                    	String orderBy =   "orden ASC";
	                    	Cursor c = db.query("input",
	                    			new String [] {"id", "section", "name", "type", "dep", "status"},
	                    			"status = ? AND section = ?",
	                    			args,
	                    			null,
	                    			null,
	                    			orderBy);
	                    	llcont.removeAllViews();
	                    	crearFormulario(c,db,itemspp,itemcount);
	                    	c.close();
	                    	db.close();
                    	}
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
        	String[] argus = new String[] {"1",formMaker.this.sectionId};
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
            		 case 4334:
            			 options = getCustomFieldDataContent(db, "tipos_vehiculo", "tipo");
            			 count = options.getCount();
            			 break;
            		 default:
            			 // Consulto BD Con options
            			 // Consultamos las opciones asociadas al input
            			 String[] args1 = new String[] {"1",c.getString(0)};
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
            		 // Consultamos las opciones asociadas al input
            		 String[] args1 = new String[] {"1",c.getString(0)};
            		 options = db.query("option",
            				 new String [] {"id","name"},
            				 "status = ? AND input = ?",
            				 args1,
            				 null,
            				 null,
            				 null);
            		 // Obtenemos el numero de elementos.
            		 count = options.getCount();
            		 // Creamos el dario group que contendra los radiobuttons.
            		 final RadioButton[] rb = new RadioButton[3];
            		 RadioGroup rbg = new RadioGroup(this);
            		 // Seteamos algunos elementos del radio group
            		 rbg.setId(Integer.parseInt(c.getString(0)));
            		 rbg.setTag(4);
            		 rbg.setOrientation(RadioGroup.HORIZONTAL);
            		 rbg.clearCheck();
            		 // Verificamos si tenemos data almacenada
            		 value = getFieldValue(c.getInt(0));
            		 // Creamos el indice
            		 int i = 0;
            		 // Determino si existen opciones para agregar al radiogroup
            		 if(count > 0){
            		 //Si el cursor puede continuar el bucle continuara.
            		 while(options.moveToNext() && options.getPosition() < count){
            			 rb[i]  = new RadioButton(this);
            			 rb[i].setId(Integer.parseInt(options.getString(0)));
            			 rb[i].setText(options.getString(1));
            			 rb[i].setTextSize(14);
            			 rb[i].setTextColor(Color.parseColor("#080A1D"));
            			 if(value != null && rb[i].getText().equals(value.toString())){
            				 rb[i].setChecked(true);
            			 }
            			 rbg.addView(rb[i]);
            			 i++;
            		 }
            		 }else{
            			 // Si no existen opciones en la BD agrego 3 numeros como opciones a modo de pruebas.
            			 for(int j=0;j<3;j++){
                			 rb[j]  = new RadioButton(this);
                			 int randInt = new Random().nextInt(2);
                			 rb[j].setId(j+randInt);
                			 rb[j].setText(String.valueOf(j+1));
                			 rb[j].setTextSize(14);
                			 rb[j].setTextColor(Color.parseColor("#080A1D"));
                			 rb[j].setEnabled(false);
                			 rbg.addView(rb[j]);
            			 }
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
            		 final EditText edtm = new EditText(this);
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
            		 edtr.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));;
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
	            		 case 4329:
	            			 optionsauto = getCustomFieldDataContent(db, "marcas", "nombreMarca");
	            			 countauto = optionsauto.getCount();
	            			 break;
	            		 case 4330:
	            			 optionsauto = getCustomFieldDataContent(db, "modelos", "nombreModelo");
	            			 countauto = optionsauto.getCount();
	            			 break;
	            		 case 4327:
	            			 optionsauto = getCustomFieldDataContent(db, "colores", "color");
	            			 countauto = optionsauto.getCount();
	            			 break;
         		 		case 4325:
         		 			optionsauto = getCustomFieldDataContent(db, "comuna", "nombreComuna");
        		 			countauto = optionsauto.getCount();
        		 			break;
            		 }
            		 // TODO NO ESTA FUNCIONANDO
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
						public void onClick(View v){
					        final Calendar c = Calendar.getInstance();
					        int year = c.get(Calendar.YEAR);
					        int month = c.get(Calendar.MONTH);
					        int day = c.get(Calendar.DAY_OF_MONTH);
					        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener(){
					        	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
					        		edtf.setText(String.valueOf(dayOfMonth)+"-"+String.valueOf(monthOfYear)+"-"+String.valueOf(year));
					        	}
					        };
					        DatePickerDialog fecha = new DatePickerDialog(formMaker.this, mDateSetListener, year, month, day);
					        fecha.show();
						}
            		 });
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edtf.setText(value.toString());
            		 }
            		 
            		 cont.addView(edtf);
            		 
            		 break;
            	 case 10:
            		 // textedit imagen
            		 // Esto podria ser un boton o otro elemento de la interfaz.
            		 /*EditText edti = new EditText(this);
            		 edti.setId(Integer.parseInt(c.getString(0)));
            		 edti.setTag(10);
            		 edti.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);*/
            		 Button aimg = new Button(this);
            		 aimg.setText(c.getString(2));
            		 aimg.setTag(10);
            		 aimg.setId(Integer.parseInt(c.getString(0)));
            		 filesUploaded = new TextView(this);
            		 filesUploaded.setTextColor(Color.BLUE);
            		 // Esto podria ser un booton o otro tipo de elemento de la interfaz.
            		 aimg.setOnClickListener(new View.OnClickListener(){
            			 public void onClick(View v){
            				 // Alerta que indica que se trata de una prueba de la camara.
            				 Toast.makeText(formMaker.this, "Aproach preliminar de la funcionalidad de captura de imagenes.", Toast.LENGTH_LONG).show();
            				 // Obtenemos el path principal para almacenar la imagen que capturemos.
            				 File root = android.os.Environment.getExternalStorageDirectory();
            				 File dir = new File (root.getAbsolutePath() + "/bspinspector/"+user+"/images");
            				 if(dir.exists()==false) {
            					 dir.mkdirs();
            				 }
            				 // Obtenemos un timestamp para formar el nombre de la imagen a capturar.
            				 Long tsLong = System.currentTimeMillis()/1000;
            				 String ts = tsLong.toString();
            				 // Creamos el archivo en el cual guardaremos la imagen que capturemos.
            				 File file = new File (dir, cod_ubicacion + "_" + ts + ".jpg");
            				 //Uri outputFileUri = Uri.fromFile(file);
            				 mFileUri = Uri.fromFile(file);
            				 // El intent que nos permite utilizar la camara.
            				 Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            				 // Le entrego donde guardar la imagen
            				 intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            				 // Lanzamos la actividad
            				 startActivityForResult(intent, CAMERA_REQUEST); 
            			 }
            		 });
            		 cont.addView(aimg);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 filesUploaded.setText(value.toString());
            		 }
            		 cont.addView(filesUploaded);
            		 break;
            		 
            	 case 11:
            		 // TODO Hay que hacer el metodo para este tipo de datos FIRMA
            		 break;
            		 
            	 case 12:
            		// textedit telefono
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edtph = new EditText(this);
            		 edtph.setTag(12);
            		 edtph.setId(Integer.parseInt(c.getString(0)));
            		 edtph.setInputType(InputType.TYPE_CLASS_PHONE);
            		 edtph.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
            		 cont.addView(edtph);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edtph.setText(value.toString());
            		 }
            		 break;
            		 
            	 case 13:
            		// textedit patente
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 EditText edtpa = new EditText(this);
            		 edtpa.setTag(13);
            		 edtpa.setId(Integer.parseInt(c.getString(0)));
            		 edtpa.setInputType(InputType.TYPE_CLASS_TEXT);
            		 cont.addView(edtpa);
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edtpa .setText(value.toString());
            		 }
            		 break;
            		 
            	 case 14:
            		 // textedit hora/minuto
            		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
            		 final EditText edth = new EditText(this);
            		 edth.setId(Integer.parseInt(c.getString(0)));
            		 edth.setTag(9);
            		 edth.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
            		 edth.setOnClickListener(new View.OnClickListener() {
            			 public void onClick(View v) {
            				 final Calendar c = Calendar.getInstance();
            				 int hourOfDay = c.get(Calendar.HOUR);
            				 int minute = c.get(Calendar.MINUTE);
            				 TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
								public void onTimeSet(TimePicker view, int hourOfDay, int minute){
									edth.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
								}
            				 };
            				 TimePickerDialog hora = new TimePickerDialog(formMaker.this, mTimeSetListener, hourOfDay, minute, false);
            				 hora.show();
            			 }
            		 });
            		 value = getFieldValue(c.getInt(0));
            		 if(value != null){
            			 edth.setText(value.toString());
            		 }
            		 cont.addView(edth);
            		 
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
	 */
	public Cursor getCustomFieldDataContent(SQLiteDatabase db, String tableName, String fieldName){
		Cursor data = null;
		data = db.rawQuery("SELECT "+fieldName+" FROM "+tableName+" WHERE "+fieldName+" NOTNULL",null);
		return data;
	}
	
	/**
	 * getTableDataContent
	 * funcion que obtiene toda la informacion de la tabla especificada.
	 */
	public Cursor getTableDataContent(SQLiteDatabase db, String tableName){
		Cursor data = null;
		data = db.rawQuery("SELECT * FROM "+tableName, null);
		return data;
	}

    /**
     * Funciones para validar Imputs
     */
    private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
           CharSequence inputStr = emailAddress;  
           Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
           Matcher matcher = pattern.matcher(inputStr);  
           return matcher.matches();
    }
    public boolean ValidarRut(int rut, char dv){
    	int m = 0, s = 1;
        for (; rut != 0; rut /= 10)
        {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }
    public boolean ValidarPatente(String patente){
    	String PATRON1 = "^[a-z]{2}[0-9a-z]{2}[0-9]{2}$";
    	String PATRON2 = "^[b-d,f-h,j-l,p,r-t,v-z]{2}[-]?[b-d,f-h,j-l,p,r-t,v-z]{2}[-]?[0-9]{2}$";
        Pattern pattern1 = Pattern.compile(PATRON1);
        Pattern pattern2 = Pattern.compile(PATRON2);
        Matcher matcher1 = pattern1.matcher(patente);
        Matcher matcher2 = pattern2.matcher(patente);
        boolean valid1 = matcher1.matches();
        boolean valid2 = matcher2.matches();
        if(valid1 || valid2){
            return true;
        }else{
        	return false;
        }
    }
    
	/**
	 * photoPicker
	 * Funciones para capturar imagenes.
	 * */
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST){
			if (resultCode == RESULT_OK){
				Log.v("mylog","camera OK" + formMaker.this.mFileUri);
				String[] filepatharr = formMaker.this.mFileUri.toString().split("/");
				filesUploaded.setText(filesUploaded.getText()+"\n"+filepatharr[filepatharr.length-1]);
			}
			else if (resultCode == RESULT_CANCELED) {
				Log.v("mylog","camera canceled");
			}
			else {
				Log.v("mylog","camera error");
			}
		}
	}

}
