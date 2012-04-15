/**
 * 
 */
package uk.org.maps3;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * @author Graham Jones
 * Based on various examples from the internet.
 * Call with 
 * 			LocationFinder lf = new LocationFinder(this);
 * 			LonLat ll = lf.getLocationLL();
 *
 */
public class LocationFinder implements LocationListener {
	LocationManager locMgr;
	String bestProvider;
	
	public LocationFinder(Context contextArg) {
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
		
		// Select the best provider to use.
		Criteria criteria = new Criteria();
		bestProvider = locMgr.getBestProvider(criteria, false);
		//bestProvider = LocationManager.NETWORK_PROVIDER;
		//LocationProvider info = locMgr.getProvider(bestProvider);

	}
	
	public LonLat getLocationLL() {
		Log.v("bestProvider",bestProvider);
		// Ask for very infrequent updates from the best provider
		// - this seems to be necessary to allow getLastKnownLocation to
		// work.		
		locMgr.requestLocationUpdates(bestProvider, 36000,10000, this);
		Location loc = locMgr.getLastKnownLocation(
				bestProvider);	
		locMgr.removeUpdates(this);
		LonLat ll;
		if (loc!=null)
			ll = new LonLat(loc.getLongitude(),
					loc.getLatitude(),
					loc.getAccuracy());
		else
			ll = null;
		return ll;
	}
	
	public void onLocationChanged(Location location) {
		Log.v("locationListener","onLocationChanged");
	}

	public void onProviderDisabled(String provider) {
		// re-register for updates
		Log.v("locationListener","onProviderDisabled");
	}

	public void onProviderEnabled(String provider) {
		// is provider better than bestProvider?
		// is yes, bestProvider = provider
		Log.v("locationListener","onProviderEnabled");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("locationListener","onStatusChanged");
	}

}
