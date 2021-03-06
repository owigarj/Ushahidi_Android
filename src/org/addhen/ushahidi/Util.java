package org.addhen.ushahidi;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.net.Categories;
import org.addhen.ushahidi.net.Incidents;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;


public class Util{

	private static NetworkInfo networkInfo;
	private static List<IncidentsData> mNewIncidents;
	private static List<CategoriesData> mNewCategories;
	private static JSONObject jsonObject;
	
	/**
	 * joins two strings together
	 * @param first
	 * @param second
	 * @return
	 */
	public static String joinString(String first, String second ) {
		return first.concat(second);
	}
	
	/**
	 * Converts a string integer 
	 * @param value
	 * @return
	 */
	public static int toInt( String value){
		return Integer.parseInt(value);
	}
	
	/**
	 * Capitalize any string given to it.
	 * @param text
	 * @return capitalized string
	 */
	public static String capitalizeString( String text ) {
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
	
	/**
	 * Create csv
	 * @param Vector<String> text
	 * 
	 * @return csv
	 */
	public static String implode( Vector<String> text ) {
		String implode = "";
		int i = 0;
		for( String value : text ) {
			implode += i == text.size() -1 ? value : value+",";
			i++;
		}
		
		return implode;
	}
	
	/**
	 * Is there internet connection
	 */
	public static boolean isConnected(Context context )  {
		  
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		networkInfo = connectivity.getActiveNetworkInfo();
		//NetworkInfo info
		
		if(networkInfo == null || !networkInfo.isConnected()){  
	        return false;  
	    } 
	    return true; 
	     
	}
	
	/**
	 * Limit a string to defined length
	 * 
	 * @param int limit - the total length 
	 * @param string limited - the limited string
	 */
	public static String limitString( String value, int length ) {
		StringBuilder buf = new StringBuilder(value);
		if( buf.length() > length ) {
			buf.setLength(length);
			buf.append(" ...");
		}
		return buf.toString();
	}
	
	/**
	 * Fetch reports details from the internet
	 * 
	 * @param context - the activity calling this method.
	 */
	public static void fetchReports( final Context context ) {
		try {
			  if( Util.isConnected(context)) {

				  if(Categories.getAllCategoriesFromWeb() ) {
					  mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
				  }

				  if(Incidents.getAllIncidentsFromWeb()){
					  mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
				  }
				  
				  UshahidiService.total_reports = mNewCategories.size()+" Categories -- " +  mNewIncidents.size()+ " Reports";
				  
				  UshahidiApplication.mDb.addCategories(mNewCategories, false);
				  UshahidiApplication.mDb.addIncidents(mNewIncidents, false);
				  
			  } else {
				  return;
			  }
		  	} catch (IOException e) {
				//means there was a problem getting it
		  	} 
	}
	
	/**
	 * Format date into more readable format.
	 * 
	 * @param  date - the date to be formatted.
	 * @return String
	 */
	public static String formatDate( String date ) {
	
		String formatted = "";
		
		DateFormat formatter = new SimpleDateFormat(
        "MMMMM dd, yyyy 'at' hh:mm:ss aaa");
		
		try {
			
			formatted = formatter.format(formatter.parse(date));
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return formatted;
	}
	
	/**
	 * extrat JSON data
	 * 
	 * @apram json_data - the json data to be formatted.
	 * @return String 
	 */
	public static boolean extractPayloadJSON( String json_data ) {
	
		try {
			jsonObject = new JSONObject(json_data);
			return jsonObject.getJSONObject("payload").getBoolean("success");
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
}
