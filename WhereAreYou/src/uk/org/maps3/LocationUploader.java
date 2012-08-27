package uk.org.maps3;

import java.io.IOException;
import java.lang.Object;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class LocationUploader extends Object implements Runnable {
	List<NameValuePair> paramList;
	public void doUpload(List<NameValuePair> paramList) {
		Log.d("LocationUploader","doUpload()");
		this.paramList = paramList;
		this.paramList.add(new BasicNameValuePair("mode","add"));
		this.paramList.add(new BasicNameValuePair("debug","true"));
		Thread trd = new Thread(this);
		trd.run();
	}
	
	@Override
	public void run() {
		    // Create a new HttpClient and Post Header
			// based on http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://maps.webhop.net/wayn/api/points.php");

		    try {
		        // Add your data
		        httppost.setEntity(new UrlEncodedFormEntity(this.paramList));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
				Log.d("LocationUploader()","got response!"+EntityUtils.toString(response.getEntity()));

		        
		    } catch (ClientProtocolException e) {
	    		Log.e("LocationUploader","ClientProtocolException");
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
	    		Log.e("LocationUploader","IOException");
		        // TODO Auto-generated catch block
		    }
	}
}