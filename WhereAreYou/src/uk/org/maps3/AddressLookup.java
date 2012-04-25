/**
 * 
 */
package uk.org.maps3;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.Log;


interface AddressReceiver {
	/** The function to be called once we have found the location */
	public void onAddressFound(LonLat ll, String addressStr);
	/** A function to be called with debug messages */
	public void msgBox(String msg);
}

/**
 * @author BEUser
 *
 */
public class AddressLookup implements Runnable {
	public String resultStr;
	LonLat ll;
	AddressReceiver ar;
		
	public AddressLookup(AddressReceiver ar) {
		this.ar = ar;
	}
	
	public void doLookup(LonLat ll) {
		Log.d("AddressLookup","doLookup()");
		this.ll = ll;
		Thread trd = new Thread(this);
		trd.run();
	}
	
		public void run() {
		if (ll!=null) {
			String url = "http://nominatim.openstreetmap.org/reverse?format=xml&" +
					"lon="+ll.lon()+"&lat="+ll.lat();
			
			HttpGet request = new HttpGet(url);
			ResponseHandler<String> responseHandler =
					new BasicResponseHandler();
			HttpClient client = new DefaultHttpClient();
			String responseBody = null;
			try {
				responseBody = client.execute(request,responseHandler);
			
				if (responseBody != null && responseBody.length() >0) {
					Log.d("AddressLookup","responseBody="+responseBody);
				
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
	
					xpp.setInput( new StringReader ( responseBody ) );
					int eventType = xpp.getEventType();
					Boolean foundResult = false;
					resultStr = null;
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if(eventType == XmlPullParser.START_DOCUMENT) {
							Log.d("AddressLookup","Start document");
						} else if(eventType == XmlPullParser.START_TAG) {
							if (xpp.getName().equals("result")) 
								foundResult = true;
							//Log.d("AddressLookup","Start tag "+xpp.getName());
						} else if(eventType == XmlPullParser.END_TAG) {
							if (xpp.getName().equals("result")) 
								foundResult = false;
							//Log.d("AddressLookup","End tag "+xpp.getName());
						} else if(eventType == XmlPullParser.TEXT) {
							if (foundResult) {
								resultStr = xpp.getText();
								Log.d("AddressLookup","result: "+xpp.getText());
							}
						}
						eventType = xpp.next();
					}
					Log.d("AddressLookup","End document");
					Log.d("AddressLookup","resultStr="+resultStr);
				
					ar.onAddressFound(this.ll,resultStr);
				} else
					ar.onAddressFound(this.ll,"Error - no response");
			
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","ClientProtocolException");
				e.printStackTrace();
				ar.onAddressFound( this.ll,"Error - ClientProtocolException");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","IOException");
				e.printStackTrace();
				ar.onAddressFound(this.ll,"Error - IOException");
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","XmlPullParserException");
				e.printStackTrace();
				ar.onAddressFound(this.ll,"Error - XmlPullParserException");
			}
		}
		else {
			Log.d("AddressLookup","ll is null - ignoring");
			ar.onAddressFound(this.ll,"Error - ll is null - ignoring");
		}
	}
}
