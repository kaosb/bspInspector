package com.bspinspector;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class soapClient {
	
	public String url;
	public String envelope;
	public String soapAction;
	
	
	/** Constructor de la clase
	 * 
	 * @param theurl URL del servicio
	 * @param theenvelope XML enviado al servicio
	 * @param theaction Action del servicio
	 */
	public soapClient(){
		
	}
	
	public String CallWebService(String url, String soapAction, String envelope) {
	    final DefaultHttpClient httpClient = new DefaultHttpClient();
	    // request parameters
	    HttpParams params = httpClient.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 15000);
	    // set parameter
	    HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

	    // POST the envelope
	    HttpPost httppost = new HttpPost(url);
	    // add headers
	    httppost.setHeader("soapaction", soapAction);
	    httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

	    String responseString = "Nothingggg";
	    try {

	        // the entity holds the request
	        HttpEntity entity = new StringEntity(envelope);
	        httppost.setEntity(entity);

	        // Response handler
	        ResponseHandler<String> rh = new ResponseHandler<String>() {
	            // invoked when client receives response
	            public String handleResponse(HttpResponse response)
	                    throws ClientProtocolException, IOException {

	                // get response entity
	                HttpEntity entity = response.getEntity();

	                // read the response as byte array
	                StringBuffer out = new StringBuffer();
	                byte[] b = EntityUtils.toByteArray(entity);

	                // write the response byte array to a string buffer
	                out.append(new String(b, 0, b.length));
	                return out.toString();
	            }
	        };

	         responseString = httpClient.execute(httppost, rh);

	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.d("me","Exc : "+ e.toString());

	    }

	    // close the connection
	    httpClient.getConnectionManager().shutdown();
	    return responseString;
	}
}
