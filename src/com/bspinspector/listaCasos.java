package com.bspinspector;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class listaCasos extends Activity implements OnClickListener{
	
	private static final String SOAP_ACTION = "urn:ServiciosBspAction";
	private static final String URL = "http://w4.bsp.cl/ws_bsp/servicio/BspServices.php";

	private ListView listview;
	private ArrayList<caso> mListItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_casos);

		listview = (ListView) findViewById(R.id.list_view);
        ArrayList<caso> list = new ArrayList<caso>();
        caso caso;
        
        
        /**
         * Cliente SOAP
         * */
        soapClient cliente = new soapClient();
        
        /**
         * Envelope RUTA
         * */
        Bundle bundle = getIntent().getExtras();
        String envelopeRuta = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        				+"<soapenv:Header/>"
        				+"<soapenv:Body>"
        				+"<usr_inspector xsi:type=\"xsd:string\"><![CDATA[<carga><usr_inspector>"+bundle.getString("user")+"</usr_inspector></carga>]]></usr_inspector>"
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
        	/*Creo un nuevo objeto caso para desplegar esta informacion*/
	        caso = new caso();
	        caso.setCod_ubicacion(doc.select("cod_ubicacion").text().trim());
	        caso.setCod_ramo(doc.select("cod_ramo").text().trim());
	        caso.setGlosa_ramo(doc.select("glosa_ramo").text().trim());
	        caso.setCod_cia(doc.select("cod_cia").text().trim());
	        caso.setGlosa_cia(doc.select("glosa_cia").text().trim());
	        caso.setNom_asegurado(doc.select("nom_asegurado").text().trim());
	        caso.setNom_contacto(doc.select("nom_contacto").text().trim());
	        caso.setTelefono(doc.select("telefono").text().trim());
	        caso.setDireccion(doc.select("direccion").text().trim());
	        caso.setCod_comuna(doc.select("cod_comuna").text().trim());
	        caso.setGlosa_comuna(doc.select("glosa_comuna").text().trim());
	        caso.setGlosa_marca(doc.select("glosa_marca").text().trim());
	        caso.setGlosa_modelo(doc.select("glosa_modelo").text().trim());
	        caso.setComentario(doc.select("comentario").text().trim());
	        caso.setPatente(doc.select("patente").text().trim());
	        caso.setCorredor(doc.select("corredor").text().trim());
	        caso.setObservaciones(doc.select("observaciones").text().trim());
	        caso.setLleva_decla_datos_adic(doc.select("lleva_decla_datos_adic").text().trim());
	        caso.setLleva_propuesta(doc.select("lleva_propuesta").text().trim());
	        caso.setLleva_poliza(doc.select("lleva_poliza").text().trim());
	        caso.setFecha_visita(doc.select("fecha_visita").text().trim());
	        caso.setHora_visita(doc.select("hora_visita").text().trim());
	        
	        /*Lo almaceno en una BD local*/
	        
	        
	        /*Lo agrego a la lista*/
	        list.add(caso);
        }
        
		mListItem = list;
		listview.setAdapter(new ListAdapter(listaCasos.this, R.id.list_view, mListItem));

	}

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
					view = vi.inflate(R.layout.list_item, null);
				}
				final caso listItem = (caso) mList.get(position);
				if(listItem != null) {
					// setting list_item views
					((TextView) view.findViewById(R.id.tv_cod)).setText(listItem.getCod_ubicacion());
					((TextView) view.findViewById(R.id.tv_description)).setText(listItem.getGlosa_marca()+" + "+listItem.getGlosa_modelo());
					((TextView) view.findViewById(R.id.tv_address)).setText(listItem.getDireccion());
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) { //--clickOnListItem
							Intent myIntent = new Intent(listaCasos.this, sectionSelector.class);
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
}
