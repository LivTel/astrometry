/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XSlaNamedPlanetTarget;

/**
 * @author eng
 *
 */
public class LunarCalcSla {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {

			AstrometryCalculator astro = new BasicAstrometryCalculator();
	
			ISite site = new BasicSite("Observatory",Math.toRadians(28.6), Math.toRadians(-17.9));
			
			XSlaNamedPlanetTarget moon = new XSlaNamedPlanetTarget("themoon");
			moon.setIndex(XSlaNamedPlanetTarget.MOON);
			
			TargetTrackCalculator moontrack = new BasicTargetCalculator(moon, site);
			
			LunarCalculator moon2 = new LunarCalculator(site);
			
			long start = System.currentTimeMillis();
			long time = start;
			while (time < start+24*3600*1000L) {
				Coordinates c = moontrack.getCoordinates(time);
				Coordinates c2 = moon2.getCoordinates(time);
				double alt = astro.getAltitude(c, site, time);
				double alt2 = astro.getAltitude(c2, site, time);
				System.err.printf("Moon elevation at %tT SLA: %3.2f  LCALC: %3.2f\n", time, Math.toDegrees(alt), Math.toDegrees(alt2));
				time += 15*60*1000L;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
