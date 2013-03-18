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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Downloader {
	/**
	 * DownloadFromUrl Funcion helper para descargar archivos.
	 * @param DownloadUrl
	 * @param fileName
	 */
	public void DownloadFromUrl(String DownloadUrl, String fileName) {
		try{
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
		}catch (IOException e){
			Log.d("DownloadManager", "Error: " + e);
		}
	}

	/**
	 * getVersion funcion que obtiene y compara la version del archivo versionState para el versionado de las BD
	 * @return
	 */
	public String getVersion(){
		this.DownloadFromUrl("http://dl.dropbox.com/u/71778/bspinspector/versionState.txt", "versionState.json");
		File root = android.os.Environment.getExternalStorageDirectory();
		File versionfile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + "versionState.json");
		StringBuilder text = new StringBuilder();
		try{
			BufferedReader br = new BufferedReader(new FileReader(versionfile));
			text.append(br.readLine());
			br.close();
			return text.toString();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Log.i("Error al leer archivo", e.getMessage());
		}catch (IOException e){
			e.printStackTrace();
			Log.i("Error IO", e.getMessage());
		}
		return null;
	}
	
	/**
	 * readFileText
	 */
	public String readFileText(String filename){
		File root = android.os.Environment.getExternalStorageDirectory();
		File datafile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + filename);
		if(datafile.exists()){
			StringBuilder text = new StringBuilder();
			try{
				BufferedReader br = new BufferedReader(new FileReader(datafile));
				text.append(br.readLine());
				br.close();
				return text.toString();
			}catch(FileNotFoundException e){
				e.printStackTrace();
				Log.i("Error al leer archivo", e.getMessage());
				return "";
			}catch (IOException e){
				e.printStackTrace();
				Log.i("Error IO", e.getMessage());
				return "";
			}
		}else{
			return "";
		}
	}
	
	/**
	 * checkVersionforDownload
	 */
	public boolean checkVersionforDownload(){
		File root = android.os.Environment.getExternalStorageDirectory();
		File versionfile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + "versionState.json");
		if(versionfile.exists()){
			String localeVersion = readFileText("versionState.json");
			String remoteVersion = getVersion();
			Log.i("VERSIONSTATElocale",localeVersion);
			Log.i("VERSIONSTATEremoto",remoteVersion);
			try{
				JSONObject localjsonversion = new JSONObject(localeVersion).getJSONObject("version");
				JSONObject remotejsonversion = new JSONObject(remoteVersion).getJSONObject("version");
				if(Integer.parseInt(remotejsonversion.getString("versionCode"))>Integer.parseInt(localjsonversion.getString("versionCode"))){
					return true;
				}
			}catch(JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return true;
			}
		}else{
			getVersion();
			return false;	
		}
		return false;
	}
	
	/**
	 * getDB funcion responsable de obtener ya sea desde la SD o online la BD bspForm.sqlite
	 * @return
	 */
	public File getDB(){
		File root = android.os.Environment.getExternalStorageDirectory();
		File dbfile = new File(root.getAbsolutePath() + "/bspinspector/conf/" + "bspForm.sqlite");
		if(dbfile.exists()){
			if(checkVersionforDownload()){
				Log.i("getDB", "Bajo nueva version.");
				this.DownloadFromUrl("http://dl.dropbox.com/u/71778/bspinspector/bspForm.sqlite", "bspForm.sqlite");
			}else{
				Log.i("getDB", "Archivo local.");
			}
			return dbfile;
		}else{
			this.DownloadFromUrl("http://dl.dropbox.com/u/71778/bspinspector/bspForm.sqlite", "bspForm.sqlite");
			if(dbfile.exists()){
				Log.i("getDB", "Descargo archivo.");
				return dbfile;
			}else{
				Log.i("getDB", "No fue posible obtener el archivo bspForm.sqlite.");
				return null;
			}
		}

	}
}