package uk.org.maps3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/* 
 * Networking Class taken from http://androidadvice.blogspot.co.uk/2010/09/check-android-network-connection-sample.html 
 */
public class Networking {
	 /*
	 *@return boolean return true if the application can access the internet
	 */
	 public static boolean isNetworkAvailable(Context context) {
	     ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     if (connectivity != null) {
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();
	        if (info != null) {
	           for (int i = 0; i < info.length; i++) {
	              if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	                 return true;
	              }
	           }
	        }
	     }
	     return false;
	  }
	}

