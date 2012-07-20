package com.bspinspector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class Downloader {

	public void DownloadFromUrl(String DownloadUrl, String fileName) {

		   try {
		           File root = android.os.Environment.getExternalStorageDirectory();               

		           File dir = new File (root.getAbsolutePath() + "/bspinspector/conf");
		           if(dir.exists()==false) {
		                dir.mkdirs();
		           }

		           URL url = new URL(DownloadUrl); //you can write here any link
		           File file = new File(dir, fileName);

		           long startTime = System.currentTimeMillis();
		           Log.d("DownloadManager", "download begining");
		           Log.d("DownloadManager", "download url:" + url);
		           Log.d("DownloadManager", "downloaded file name:" + fileName);

		           /* Open a connection to that URL. */
		           URLConnection ucon = url.openConnection();

		           /*
		            * Define InputStreams to read from the URLConnection.
		            */
		           InputStream is = ucon.getInputStream();
		           BufferedInputStream bis = new BufferedInputStream(is);

		           /*
		            * Read bytes to the Buffer until there is nothing more to read(-1).
		            */
		           ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		           int current = 0;
		           while ((current = bis.read()) != -1) {
		              baf.append((byte) current);
		           }


		           /* Convert the Bytes read to a String. */
		           FileOutputStream fos = new FileOutputStream(file);
		           fos.write(baf.toByteArray());
		           fos.flush();
		           fos.close();
		           Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		   } catch (IOException e) {
		       Log.d("DownloadManager", "Error: " + e);
		   }

	}
	
	public String getVersion(){
		
		this.DownloadFromUrl("http://dl.dropbox.com/u/71778/bspinspector/versionState.txt", "versionState.json");
		File root = android.os.Environment.getExternalStorageDirectory();
        File versionfile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + "versionState.json");
        StringBuilder text = new StringBuilder();
        try {
        	
			BufferedReader br = new BufferedReader(new FileReader(versionfile));
			text.append(br.readLine());
			br.close();
			return text.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("Error al leer archivo", e.getMessage());
		} catch (IOException e) {e.printStackTrace();
			Log.i("Error IO", e.getMessage());
		}
		return null;
		
	}
	
	public File getDB(){
		
        this.DownloadFromUrl("http://dl.dropbox.com/u/71778/bspinspector/bspForm.sqlite", "bspForm.sqlite");

        File root = android.os.Environment.getExternalStorageDirectory();
        File dbfile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + "bspForm.sqlite");
        
        if(dbfile.exists()){
        	return dbfile;
        }else{
        	return null;
        }
		
	}
	
}