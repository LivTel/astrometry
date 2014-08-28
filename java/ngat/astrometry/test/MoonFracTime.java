/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;
import ngat.astrometry.SolarCalculator;

/**
 * @author eng
 *
 */
public class MoonFracTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		double lat = Math.toRadians(28.0);
		double lon = Math.toRadians(-17.0);
		
		ISite site = new BasicSite("telescope", lat, lon);
		LunarCalculator moonTrack = new LunarCalculator(site);
		SolarCalculator sunTrack = new SolarCalculator();
		BasicAstrometrySiteCalculator astro = new BasicAstrometrySiteCalculator(site);
		
		try {
			long start = System.currentTimeMillis();
			long time = start;
			while (time < start + 120*24*3600*1000L) {
				
				// work out the sun position at time at site.
				Coordinates sun = sunTrack.getCoordinates(time);
				double sunAlt = astro.getAltitude(sun, time);
				
				// work out the moon position and phase at time at site.
				Coordinates moon = moonTrack.getCoordinates(time);
				double moonAlt = astro.getAltitude(moon, time);
				
				double moonSun = astro.getAngularSeperation(moon, sun);
				
				double moonFrac = 0.5*(1.0 + Math.cos(Math.PI-moonSun));
						
				System.err.printf("%tF %tT %4.2f %4.2f \n ", time, time, moonSun, moonFrac);
				
				time += 3600*1000L;			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
