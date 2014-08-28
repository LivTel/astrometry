/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;

/**
 * @author eng
 * 
 */
public class LunarElevationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			AstrometryCalculator astro = new BasicAstrometryCalculator();
	
			ISite site = new BasicSite("Observatory",Math.toRadians(28.6), Math.toRadians(-17.9));
			LunarCalculator moon = new LunarCalculator(site);
			
			long start = System.currentTimeMillis();
			long time = start;
			while (time < start+24*3600*1000L) {
				Coordinates c = moon.getCoordinates(time);
				double alt = astro.getAltitude(c, site, time);
				System.err.printf("Moon elevation at %tT %3.2f \n", time, Math.toDegrees(alt));
				time += 15*60*1000L;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
