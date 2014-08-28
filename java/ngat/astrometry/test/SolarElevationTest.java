/**
 * 
 */
package ngat.astrometry.test;

import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.astrometry.SolarCalculator;

/**
 * @author eng
 * 
 */
public class SolarElevationTest {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		sdf.setTimeZone(UTC);
		
		try {

			//long time = (sdf.parse(args[2])).getTime();
			
			
			double lat = Math.toRadians(Double.parseDouble(args[0]));
			double lon = Math.toRadians(Double.parseDouble(args[1]));
			
			AstrometryCalculator astro = new BasicAstrometryCalculator();
			SolarCalculator sun = new SolarCalculator();
			
			ISite site = new BasicSite("Observatory", lat, lon);
			
			long start = System.currentTimeMillis();
			
			System.err.printf("START NOW: %tF %tT \n", start, start);
			
			long time = start;
			while (time < start+24*3600*1000L) {
				double alt = astro.getAltitude(sun.getCoordinates(time), site, time);
				System.err.printf("%tF %tT %3.2f \n", time, time, Math.toDegrees(alt));
				time += 60*1000L;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
