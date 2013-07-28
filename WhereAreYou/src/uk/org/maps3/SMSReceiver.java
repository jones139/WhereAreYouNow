/**
 * 
 */
package uk.org.maps3;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Graham Jones
 * Class to respond to SMS messages.  The action taken depends on the 
 * contents of the SMS message.
 * If it contains 'WAYN', the current location is determined, 
 * and an SMS reply sent with details
 * of the current locations.
 * If it contains 'WAY_UPLOAD', the SMS text is parsed assuming it is 
 * of the format:
 *   WAY_UPLOAD?UNAME=xxx&PASSWD=xxx&LON=xxx&LAT=xxx&DATE=xxx.
 * Provided the string parses correctly, a data point is uploaded to 
 * the server based on the data provided.
 */
public class SMSReceiver extends BroadcastReceiver 
    implements LocationReceiver, AddressReceiver {
    
    /**
     * 
     */
    boolean mActive;
    String mPassword;
    LocationFinder lf = null;
    Context mContext = null;
    String smsNumber = null;
    int mTimeOutSec = 60;
    boolean mUseGPS = true;
    
    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
	public void onReceive(Context contextArg, Intent intentArg) {
	//---get the SMS message passed in---
	Bundle bundle = intentArg.getExtras();        
	SmsMessage[] msgs = null;
	mContext = contextArg;
	getPrefs(contextArg);
	if (bundle == null) {
	    Toast.makeText(contextArg,
			   "Empty SMS Message Received - Ignoring",
			   Toast.LENGTH_SHORT).show();
	} else {
	    if (mActive) {
		//---retrieve the SMS message received---
		Object[] pdus = (Object[]) bundle.get("pdus");
		msgs = new SmsMessage[pdus.length];            
		for (int i=0; i<msgs.length; i++){
		    String str = "";
		    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
		    str += "SMS from " + msgs[i].getOriginatingAddress();                     
		    str += " :";
		    str += msgs[i].getMessageBody().toString();
		    str += "\n";        
		    Toast.makeText(contextArg,
				   str,
				   Toast.LENGTH_SHORT).show();
		}
		String msg0 = msgs[0].getMessageBody().toString();
		if (msg0.toUpperCase().contains("WAYN")) {
		    // Start the LocationFinder service if it is not running.
		    if (lf==null) {
			lf = new LocationFinder(contextArg);
		    }
		    Toast.makeText(contextArg,
				   "Found WAYN - getting Location....",
				   Toast.LENGTH_SHORT).show();
		    // Get the location using the LocationFinder.
		    smsNumber = msgs[0].getOriginatingAddress();
		    lf.getLocationLL((LocationReceiver) this,mTimeOutSec,mUseGPS);
		    Toast.makeText(contextArg,
				   "Returned from getLocationLL()",
				   Toast.LENGTH_SHORT).show();
		} else if  (msg0.toUpperCase().contains("WAY_UPLOAD")) {
		    Toast.makeText(contextArg,
				   "Found WAY_UPLOAD - parseing message....",
				   Toast.LENGTH_SHORT).show();
		    String msgParts[] = msg0.split("\\?");
		    if (msgParts.length>1) {
			Toast.makeText(contextArg,"Got here",Toast.LENGTH_SHORT).show();
			String argList[] = msgParts[1].split("\\&");
			List<NameValuePair> paramList = new ArrayList<NameValuePair>(2);
			for(int i =0; i < argList.length ; i++) {
			    String key = argList[i].split("=")[0];
			    String value = argList[i].split("=")[1];
			    paramList.add(new BasicNameValuePair(key, value));
			    LocationUploader lu = new LocationUploader();
			    lu.doUpload(paramList);
			    Toast.makeText(contextArg,
					   key+"="+value,
					   Toast.LENGTH_SHORT).show();
			}
		    } else {
			Toast.makeText(contextArg,
				       "Did not find '?' to signify start of arguments",
				       Toast.LENGTH_SHORT).show();
		    }
		    
		} else {
		    Toast.makeText(contextArg,
				   "Message does not contain 'WAYN' or 'WAY_UPLOAD' - ignoring",
				   Toast.LENGTH_SHORT).show();
		}
	    } else {
		Toast.makeText(contextArg,
			       "WAYN Disabled - Ignoring",
			       Toast.LENGTH_SHORT).show();
	    }
	}
    }
    
    private void getPrefs(Context context) {
	SharedPreferences settings = context.getSharedPreferences(
								  "WhereAreYou", Context.MODE_PRIVATE);
	mActive = settings.getBoolean("Active", true);
	mPassword = settings.getString("Password", "WAYN");
	mTimeOutSec = settings.getInt("TimeOutSec", 60);
	mUseGPS = settings.getBoolean("UseGPS",true);
    }
    
    public void onAddressLookedup(String resultStr) {
	
    }
    
    public void onLocationFound(LonLat ll) {
	Toast.makeText(mContext, "onLocationFound()", Toast.LENGTH_SHORT)
	    .show();
	if (ll != null) {
	    AddressLookup al = new AddressLookup(this,mContext);
	    al.doLookup(ll);
	} else {
	    Toast.makeText(mContext, "Failed to find location - sorry!",
			   Toast.LENGTH_SHORT).show();
	    Log.d("SMSReceiver", "Failed to find location - ll is null");
	}
	
    }
    
    public void onAddressFound(LonLat ll, String addressStr) {
	String resultStr = addressStr + "\n" + ll.toStr();
	Log.d("SMSReceiver", "resultStr=" + resultStr);
	
	// ---display the new SMS message on the screen.---
	Log.d("onReceive", ll.toString());
	Toast.makeText(mContext, " Replying: " + resultStr,
		       Toast.LENGTH_SHORT).show();
	// Now reply to the message with our location.
	SmsManager sm = SmsManager.getDefault();
	sm.sendTextMessage(smsNumber, null, resultStr, null, null);
	
    }
    
    // Callback for debugging info from LocationFinder
    public void msgBox(String msg) {
	// TODO Do nothing - we operate silently...
	
    }
}
