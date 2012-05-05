/**
 * 
 */
package uk.org.maps3;

/**
 * @author graham
 *
 */
class XY {
	int x;
	int y;
}

public class TileUtils {
		public static XY ll2TileXy(LonLat ll) {
			int z = 12;  // tile numbers refer to zoom level 12;
			XY xy = new XY();
			double lon = ll.lon();
			double lat = ll.lat();
			xy.x = (int)Math.floor( (lon + 180) / 360 * (1<<z) ) ;
			xy.y = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) 
					+ 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<z) ) ;
			return xy;
		}
		 public static String getTileNumber(final double lat, final double lon, final int zoom) {
		   int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
		   int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
		    return("" + zoom + "/" + xtile + "/" + ytile);
		   }
}
