package uk.org.maps3;

public class LonLat {
	public final double _lon;  //longitude in degrees
	public final double _lat;  // latitude in degrees
	public final float _acc;  // fix accuracy in metres.
	public double lon() {return _lon;};
	public double lat() {return _lat;};
	public LonLat(double llon,double llat, float lacc) {
		_lon = llon;
		_lat = llat;
		_acc = lacc;
	}

	String toStr() {
		return ("lon="+_lon+", lat="+_lat+":  accuracy="+_acc+" m");
	}

}
