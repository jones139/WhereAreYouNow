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

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author BEUser
 *
 */
public class AddressLookup extends AsyncTask<LonLat, String, String> {
	public String resultStr;
	
	protected String doInBackground(LonLat... ll) {
		return doLookup(ll);
	}
	
	protected void onProgressUpdate() {
		Log.d("AddressLookup","onProgressUpdate()");
		super.onProgressUpdate();
	}
	
	
	@Override
    protected void onPostExecute(String result) {
		Log.d("AddressLookup","onPostExecute() - result="+result);
		resultStr = result;
        return;
    }
	
	public String doLookup(LonLat... ll) {
		Log.d("AddressLookup","doLookup()");
		if (ll[0]!=null) {
			String url = "http://nominatim.openstreetmap.org/reverse?format=xml&" +
					"lon="+ll[0].lon()+"&lat="+ll[0].lat();
			
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
				
					return resultStr;
				} else
					return "Error - no response";
			
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","ClientProtocolException");
				e.printStackTrace();
				return "Error - ClientProtocolException";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","IOException");
				e.printStackTrace();
				return "Error - IOException";
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				Log.d("AddressLookup","XmlPullParserException");
				e.printStackTrace();
				return "Error - XmlPullParserException";
			}
		}
		else {
			Log.d("AddressLookup","ll is null - ignoring");
			return("Error - ll is null - ignoring");
		}
	}
}
