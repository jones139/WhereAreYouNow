/**
 * 
 */
package uk.org.maps3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
/**
 * @author BEUser
 *
 */
public class SMSReceiver extends BroadcastReceiver {

	/**
	 * 
	 */
	LocationFinder lf = null;
	//public SMSReceiver(Context contextArg) {
		//Log.d("SMSReceiver","Constructor Called");
		//lf = new LocationFinder(contextArg);
	//}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context contextArg, Intent intentArg) {
        //---get the SMS message passed in---
        Bundle bundle = intentArg.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";            
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += "SMS from " + msgs[i].getOriginatingAddress();                     
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";        
            }
            String msg0 = msgs[0].getMessageBody().toString();
            if (msg0.toUpperCase().contains("WAYN")) {
            	// Start the LocationFinder service if it is not running.
            	if (lf==null) {
            		lf = new LocationFinder(contextArg);
            	}
            	// Get the location using the LocationFinder.
            	LonLat ll = lf.getLocationLL();
            	if (ll!=null) {
	            	AddressLookup al = new AddressLookup();
	            	al.doLookup(ll);
	            	String resultStr = al.resultStr + "\n" + ll.toStr();
	        		Log.d("SMSReceiver","resultStr="+resultStr);
	
	            	//---display the new SMS message on the screen.---
	            	Log.d("onReceive",ll.toString());
	            	Toast.makeText(contextArg, 
	            			str + " Replying: "+resultStr, 
	            			Toast.LENGTH_SHORT).show();
	            	//Toast.makeText(contextArg, locStr, Toast.LENGTH_SHORT).show();
	
	
	            	// Now reply to the message with our location.
	            	SmsManager sm = SmsManager.getDefault();
	            	String number = msgs[0].getOriginatingAddress();
	            	sm.sendTextMessage(number, null, resultStr, null,null);
            	}
            	else
            	{
                	Toast.makeText(contextArg,
                			"Failed to find location - sorry!",
                			Toast.LENGTH_SHORT).show();
                	Log.d("SMSReceiver","Failed to find location - ll is null");
            	}
            }
            else
            {
            	Toast.makeText(contextArg,
            			"Message does not contain 'WAYN' - ignoring",
            			Toast.LENGTH_SHORT).show();
            }
        }                         
    }
	
	public void onAddressLookedup(String resultStr) {
    	
	}
}
