package com.bspinspector;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class casos {
	
	public String text;
	public String [] inspecciones;
	
	casos(){
	}
	
	public void parseRuta(String response){
        
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
    				this.text = parser.getText();
    				this.inspecciones = parser.getText().split("</inspeccion><inspeccion>");
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
        
	}

}
