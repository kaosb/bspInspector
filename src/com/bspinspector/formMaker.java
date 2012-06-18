package com.bspinspector;

import java.io.File;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class formMaker extends Activity {
	
	int maxpp = 0;
	int marcador = 0;
	String[][] Llaves;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        Downloader dw = new Downloader();
        File dbfile = dw.getDB();
        
        File dbConfFile = getdDBFile();
        if(dbConfFile.exists()){
        	//SQLiteDatabase dbConf = SQLiteDatabase.openOrCreateDatabase(dbConfFile, null);
        }
        
        if(dbfile.exists()){
        	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            //Trabajar con la BD
            	Bundle bundle = getIntent().getExtras();
            	String[] args = new String[] {"0",bundle.getString("sectionId")};
            	Cursor c = db.query("input",
            						new String [] {"id", "section", "name", "type", "dep", "status"},
            						"status = ? AND section = ?",
            						args,
            						null,
            						null,
            						null);
            	
            /*Crear Vista*/
            
            	Toast.makeText(formMaker.this, ""+c.getCount()+" items", Toast.LENGTH_SHORT).show();
            	Llaves = new String[c.getCount()][2];
            	int indice = 0;
            	
	            ScrollView sv = new ScrollView(this);
	            final LinearLayout ll = new LinearLayout(this);
	            ll.setOrientation(LinearLayout.VERTICAL);
	            sv.addView(ll);
	            
	            LinearLayout cabecera = new LinearLayout(this);
	            cabecera.setBackgroundColor(Color.parseColor("#0A0C29"));
	            cabecera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
	            cabecera.setPadding(10, 0, 10, 0);
	            cabecera.setGravity(Gravity.CENTER);
	            
	            TextView tituloCabecera = new TextView(this);
	            tituloCabecera.setText("Sección "+bundle.getString("sectionId"));
	            tituloCabecera.setTextSize(18);
	            tituloCabecera.setTextColor(Color.parseColor("#FFFFFF"));
	            tituloCabecera.setTypeface(null, Typeface.BOLD);
	            
	            cabecera.addView(tituloCabecera);
	            
	            ll.addView(cabecera);
            
            /*Fin crear vista*/
            
	            //Nos aseguramos de que existe al menos un registro
	            if (c.moveToFirst()){
	                 //Recorremos el cursor hasta que no haya más registros
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
	                	 
	                	 Llaves[indice][0] = c.getString(0);
	                	 Llaves[indice][1] = c.getString(3);
	                	 indice++;
	                	 
	                	 /*Campo*/
	                	 
	                	 switch(Integer.parseInt(c.getString(3))){
	                	 
	                	 case 1:
	                		 //Texto
	                		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	                		 EditText edt = new EditText(this);
	                		 edt.setId(Integer.parseInt(c.getString(0)));
	                		 edt.setTag(1);
	                		 edt.setInputType(InputType.TYPE_CLASS_TEXT);
	                		 cont.addView(edt);
	                		 
	                		 break;
	                	 case 2:
	                		 String[] items = new String[] {"1", "2", "3", "4", "5"};
	                		 tv.setText(tv.getText()+"\nSelecciona "+c.getString(2));
	                		 Spinner spinner = new Spinner(this);
	                		 spinner.setId(Integer.parseInt(c.getString(0)));
	                		 spinner.setTag(2);
	                		 spinner.setPrompt("Selecciona "+c.getString(2));
	                		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
	                		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	                		 spinner.setAdapter(adapter);
	                		 cont.addView(spinner);
	                		 
	                		 break;
	                	 case 3:
	                		 //checkbox
	                		 tv.setText(tv.getText()+"\nEs "+c.getString(2));
	                		 CheckBox checkBox = new CheckBox(this);
	                		 checkBox.setId(Integer.parseInt(c.getString(0)));
	                		 checkBox.setTag(3);
	                		 //Todo el codigo para manejar el checkbox
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
	                		 edtn.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	                		 cont.addView(edtn);
	                		 
	                		 break;
	                	 case 6:
	                		 // textedit email
	                		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	                		 EditText edtm = new EditText(this);
	                		 edtm.setId(Integer.parseInt(c.getString(0)));
	                		 edtm.setTag(6);
	                		 edtm.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	                		 cont.addView(edtm);
	                		 
	                		 break;
	                	 case 7:
	                		// textedit rut
	                		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	                		 EditText edtr = new EditText(this);
	                		 edtr.setTag(7);
	                		 edtr.setId(Integer.parseInt(c.getString(0)));
	                		 edtr.setInputType(InputType.TYPE_CLASS_TEXT);
	                		 cont.addView(edtr);
	                		 
	                		 break;
	                	 case 8:
	                		 // textedit autocompletar
	                		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	                		 EditText edta = new EditText(this);
	                		 edta.setId(Integer.parseInt(c.getString(0)));
	                		 edta.setTag(8);
	                		 edta.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	                		 cont.addView(edta);
	                		 
	                		 break;
	                	 case 9:
	                		 // textedit fecha
	                		 tv.setText(tv.getText()+"\nIngresa "+c.getString(2));
	                		 EditText edtf = new EditText(this);
	                		 edtf.setId(Integer.parseInt(c.getString(0)));
	                		 edtf.setTag(9);
	                		 edtf.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
	                		 cont.addView(edtf);
	                		 
	                		 break;
	                	 case 10:
	                		 // textedit imagen
	                		 tv.setText(tv.getText()+"\nToma "+c.getString(2));
	                		 EditText edti = new EditText(this);
	                		 edti.setId(Integer.parseInt(c.getString(0)));
	                		 edti.setTag(10);
	                		 edti.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	                		 cont.addView(edti);
	                		 
	                		 break;
	                		 default:
	                			 break;

	                	 }
	                	 
	                	 ll.addView(cont);
	                	 
	                 } while(c.moveToNext());
	                 
	                marcador = indice;
	                 
	            }

	        c.close();
            db.close();
            
            
            
            Button siguiente = new Button(this);
            siguiente.setText("Siguiente");
            siguiente.setOnClickListener(new View.OnClickListener() {
            	public void onClick(View v) {
            		
            		
            		String prueba = "";

   	                 for(int i =0;i<marcador;i++){
   	                	 
             			switch(Integer.parseInt(Llaves[i][1])){
            			
             			case 1:
             				EditText redtt = (EditText) findViewById(Integer.parseInt(Llaves[i][0]));
             				prueba= prueba+"/"+redtt.getText();
             				break;
             				
             			case 2:
             				Spinner spinner = (Spinner) findViewById(Integer.parseInt(Llaves[i][0]));
             				prueba= prueba+"/"+spinner.getSelectedItem();
             				break;
             				
             			case 3:
             				CheckBox checkbox = (CheckBox) findViewById(Integer.parseInt(Llaves[i][0]));
             				if(checkbox.isChecked()){
             					prueba= prueba+"/"+checkbox.getId();
             				}
             				break;
             				
             			case 4:
             				RadioGroup rbg = (RadioGroup) findViewById(Integer.parseInt(Llaves[i][0]));
             				prueba= prueba+"/"+rbg.getCheckedRadioButtonId()/100;
             				break;

             			default:
             				EditText redtd = (EditText) findViewById(Integer.parseInt(Llaves[i][0]));
             				try{
             				prueba= prueba+"/"+redtd.getText();
             				}catch(Exception e){
             					prueba= prueba+"/"+"cuack";
             				}
             				break;
             			}
   	                 }
   	                 
            		Toast.makeText(formMaker.this, ""+prueba, Toast.LENGTH_SHORT).show();
            	}
            });
            
            Button atras = new Button(this);
            atras.setText("Atras");
            atras.setOnClickListener(new View.OnClickListener() {
            	public void onClick(View v) {
            		finish();
            	}
            });
            
       	 	LinearLayout botonera = new LinearLayout(this);
       	 	botonera.setOrientation(LinearLayout.HORIZONTAL);
       	 	botonera.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
       	 	botonera.setBackgroundColor(Color.parseColor("#0A0C29"));
       	 	
       	 	botonera.addView(atras);
       	 	botonera.addView(siguiente);

            ll.addView(botonera);
            
            View pie = new View(this);
            pie.setBackgroundColor(Color.parseColor("#0A0C29"));
            pie.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 40));
            ll.addView(pie);
            
            this.setContentView(sv);

        }
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
}
