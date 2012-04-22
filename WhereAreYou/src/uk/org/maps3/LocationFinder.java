/**
 * 
 */
package uk.org.maps3;


import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.location.GpsStatus;;


/**
 * @author Graham Jones
 * Based on various examples from the internet.
 * Call with 
 * 			LocationFinder lf = new LocationFinder(this);
 * 			LonLat ll = lf.getLocationLL();
 *
 */
public class LocationFinder implements LocationListener, Runnable, GpsStatus.Listener {
	LocationManager locMgr;
	LocationReceiver lr;
	String mProvider;
	long mTimeStart;
	Context mContext;
	boolean mTimedOut = false;
	Handler mHandler;
	boolean mUseGPS = true;
	
	public LocationFinder(Context contextArg) {
		mContext = contextArg;
		mHandler = new Handler();
		mUseGPS = false;
		locMgr = (LocationManager)
				contextArg.getSystemService(
							Context.LOCATION_SERVICE);
		
		if (locMgr==null) {
			Log.v("LocationFinder","ERROR - locMgr is null - THIS WILL NOT WORK!!!");
		}
		
		// List all providers to log.:
		List<String> providers = locMgr.getAllProviders();
		for (String provider : providers) {
			Log.v("LocationFinder",provider+","+locMgr.getProvider(provider).toString());
		}
		
	}
	
	
	public void getLocationLL(LocationReceiver lr, int timeOutSec, boolean useGPS) {
		mUseGPS = useGPS;
		if (mUseGPS) {
			mProvider = LocationManager.GPS_PROVIDER;
		} else {
			mProvider = LocationManager.NETWORK_PROVIDER;
		}
		Log.v("mProvider",mProvider);
		this.lr = lr;
		// Ask for location updates to be sent to the onLocationChanged() method of this class.
		locMgr.requestLocationUpdates(mProvider, 0,0, this);
		locMgr.addGpsStatusListener(this);
		
		// Set a timer running to allow us to give up on getting a GPS fix.
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, timeOutSec*1000);
	}
	
	public void startFixSearch() {
		locMgr.requestLocationUpdates(mProvider, 36000,10000, this);		
	}

	
	public void onLocationChanged(Location loc) {
		Log.v("locationListener","onLocationChanged");
		if (loc!=null) {
			msgBox("onLocationChanged - mTimedOut ="+mTimedOut+" Provider="+loc.getProvider());
			if ((loc.getProvider() == mProvider) || mTimedOut) {
				locMgr.removeUpdates(this);
		        mHandler.removeCallbacks(this); // to avoid this function being called again by the timeOut run() function.
				LonLat ll;
				ll = new LonLat(loc.getLongitude(),
						loc.getLatitude(),
						loc.getAccuracy());
				lr.onLocationFound(ll);
			} else {
				msgBox("Skipping location update by "+mProvider);
			}
		} else msgBox("loc==null - waiting...");
	}

	public void onProviderDisabled(String provider) {
		// re-register for updates
		Log.v("locationListener","onProviderDisabled");
	}

	public void onProviderEnabled(String provider) {
		// is provider better than mProvider?
		// is yes, mProvider = provider
		Log.v("locationListener","onProviderEnabled");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("locationListener","onStatusChanged");
	}

	// Called by the mHandler timer to signify timeout.  In which case we give up on GPS and fall back onto NETWORK_PROVIDER.
	public void run() {
		Log.v("WAYN","timeout runnable.run");
		mTimedOut = true;
		msgBox("TimedOut!");
		Location loc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		onLocationChanged(loc);

	}

	
	public void msgBox(String msg) {
		Toast.makeText(mContext,
				msg,
				Toast.LENGTH_SHORT).show();	
	}


	public void onGpsStatusChanged(int eventNo) {
		msgBox("onGpsStatusChanged - eventNo="+eventNo);
		 if (eventNo == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
				GpsStatus gpsStatus = locMgr.getGpsStatus(null);
	            Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
	            Iterator<GpsSatellite> it = sats.iterator();
	            String msg = "Satellites...";
	            while ( it.hasNext() ) 
	            { 
	                    GpsSatellite oSat = (GpsSatellite) it.next() ; 
	                    Log.v("TEST","LocationActivity - onGpsStatusChange: Satellites: " + 
	    oSat.getSnr() ) ; 
	                    msg = msg+"snr="+oSat.getSnr()+", ";
	                    msgBox(msg);
	            } 
		 }
	}
	
}
