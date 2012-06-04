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
		setContentView(R.layout.home);

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
        
        String cod_ubicacion = null;
        String cod_ramo = null;
        String glosa_ramo = null;
        String cod_cia = null;
        String glosa_cia = null;
        String nom_asegurado = null;
        String nom_contacto = null;
        String telefono = null;
        String direccion = null;
        String cod_comuna = null;
        String glosa_comuna = null;
        String glosa_marca = null;
        String glosa_modelo = null;
        String comentario = null;
        String patente = null;
        String corredor = null;
        String observaciones = null;
        String lleva_decla_datos_adic = null;
        String lleva_propuesta = null;
        String lleva_poliza = null;
        String fecha_visita = null;
        String hora_visita = null;
        Document doc;
        		
        for(int i = 0;i+1<=ruta.inspecciones.length;i++){
        	
        	doc = Jsoup.parse(ruta.inspecciones[i]);
        	
        	cod_ubicacion = doc.select("cod_ubicacion").text().trim();
        	/*cod_ubicacion = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<cod_ubicacion>"), ruta.inspecciones[i].indexOf("</cod_ubicacion>")).toString().substring(15);*/
        	cod_ramo = doc.select("cod_ramo").text().trim();
        	/*cod_ramo = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<cod_ramo>"), ruta.inspecciones[i].indexOf("</cod_ramo>")).toString().substring(10);*/
        	glosa_ramo = doc.select("glosa_ramo").text().trim();
        	/*glosa_ramo = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<glosa_ramo>"), ruta.inspecciones[i].indexOf("</glosa_ramo>")).toString().substring(12);*/
        	cod_cia = doc.select("cod_cia").text().trim();
        	/*cod_cia = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<cod_cia>"), ruta.inspecciones[i].indexOf("</cod_cia>")).toString().substring(10);*/
        	glosa_cia = doc.select("glosa_cia").text().trim();
        	/*glosa_cia = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<glosa_cia>"), ruta.inspecciones[i].indexOf("</glosa_cia>")).toString().substring(11);*/
        	nom_asegurado = doc.select("nom_asegurado").text().trim();
        	/*nom_asegurado = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<nom_asegurado>"), ruta.inspecciones[i].indexOf("</nom_asegurado>")).toString().substring(15)*/;
        	nom_contacto = doc.select("nom_contacto").text().trim();
        	/*nom_contacto = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<nom_contacto>"), ruta.inspecciones[i].indexOf("</nom_contacto>")).toString().substring(14);*/
        	telefono = doc.select("telefono").text().trim();
        	/*telefono = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<telefono>"), ruta.inspecciones[i].indexOf("</telefono>")).toString().substring(10);*/
        	direccion = doc.select("direccion").text().trim();
        	/*direccion = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<direccion>"), ruta.inspecciones[i].indexOf("</direccion>")).toString().substring(15)+"/"+nom_asegurado;*/
        	cod_comuna = doc.select("cod_comuna").text().trim();
        	/*cod_comuna = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<cod_comuna>"), ruta.inspecciones[i].indexOf("</cod_comuna>")).toString().substring(12);*/
        	glosa_comuna = doc.select("glosa_comuna").text().trim();
        	/*glosa_comuna = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<glosa_comuna>"), ruta.inspecciones[i].indexOf("</glosa_comuna>")).toString().substring(14);*/
        	glosa_marca = doc.select("glosa_marca").text().trim();
        	/*glosa_marca = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<glosa_marca>"), ruta.inspecciones[i].indexOf("</glosa_marca>")).toString().substring(13);*/
        	glosa_modelo = doc.select("glosa_modelo").text().trim();
        	/*glosa_modelo = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<glosa_modelo>"), ruta.inspecciones[i].indexOf("</glosa_modelo>")).toString().substring(14);*/
        	comentario = doc.select("comentario").text().trim();
        	/*comentario = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<comentario>"), ruta.inspecciones[i].indexOf("</comentario>")).toString().substring(12);*/
        	patente = doc.select("patente").text().trim();
        	/*patente = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<patente>"), ruta.inspecciones[i].indexOf("</patente>")).toString().substring(9);*/
        	corredor = doc.select("corredor").text().trim();
        	/*corredor = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<corredor>"), ruta.inspecciones[i].indexOf("</corredor>")).toString().substring(10);*/
        	observaciones = doc.select("observaciones").text().trim();
        	/*observaciones = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<observaciones>"), ruta.inspecciones[i].indexOf("</observaciones>")).toString().substring(15);*/
        	lleva_decla_datos_adic = doc.select("lleva_decla_datos_adic").text().trim();
        	/*lleva_decla_datos_adic = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<lleva_decla_datos_adic>"), ruta.inspecciones[i].indexOf("</lleva_decla_datos_adic>")).toString().substring(24);*/
        	lleva_propuesta = doc.select("lleva_propuesta").text().trim();
        	/*lleva_propuesta = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<lleva_propuesta>"), ruta.inspecciones[i].indexOf("</lleva_propuesta>")).toString().substring(17);*/
        	lleva_poliza = doc.select("lleva_poliza").text().trim();
        	/*lleva_poliza = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<lleva_poliza>"), ruta.inspecciones[i].indexOf("</lleva_poliza>")).toString().substring(14);*/
        	fecha_visita = doc.select("fecha_visita").text().trim();
        	/*fecha_visita = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<fecha_visita>"), ruta.inspecciones[i].indexOf("</fecha_visita>")).toString().substring(14);*/
        	hora_visita = doc.select("hora_visita").text().trim();
        	/*hora_visita = ruta.inspecciones[i].subSequence(ruta.inspecciones[i].indexOf("<hora_visita>"), ruta.inspecciones[i].indexOf("</hora_visita>")).toString().substring(14);*/
	        caso = new caso();
	        caso.setCod_ubicacion(cod_ubicacion);
	        caso.setCod_ramo(cod_ramo);
	        caso.setGlosa_ramo(glosa_ramo);
	        caso.setCod_cia(cod_cia);
	        caso.setGlosa_cia(glosa_cia);
	        caso.setNom_asegurado(nom_asegurado);
	        caso.setNom_contacto(nom_contacto);
	        caso.setTelefono(telefono);
	        caso.setDireccion(direccion);
	        caso.setCod_comuna(cod_comuna);
	        caso.setGlosa_comuna(glosa_comuna);
	        caso.setGlosa_marca(glosa_marca);
	        caso.setGlosa_modelo(glosa_modelo);
	        caso.setComentario(comentario);
	        caso.setPatente(patente);
	        caso.setCorredor(corredor);
	        caso.setObservaciones(observaciones);
	        caso.setLleva_decla_datos_adic(lleva_decla_datos_adic);
	        caso.setLleva_propuesta(lleva_propuesta);
	        caso.setLleva_poliza(lleva_poliza);
	        caso.setFecha_visita(fecha_visita);
	        caso.setHora_visita(hora_visita);
	        caso.setCod_comuna(cod_comuna);
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
