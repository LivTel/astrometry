/**
 * 
 */
package ngat.astrometry.test;

import java.util.Date;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.astrometry.SolarCalculator;

/**
 * @author eng
 *
 */
public class SunsetCalc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			AstrometryCalculator astro = new BasicAstrometryCalculator();
			SolarCalculator sun = new SolarCalculator();
			
			ISite site = new BasicSite("Observatory",Math.toRadians(28.7606), Math.toRadians(-17.8816));
			
			long now = System.currentTimeMillis();
			long time = now;
			double timediff = 86400*1000L;
			while (timediff > 10) {
				long tts = astro.getTimeUntilNextSet(sun.getCoordinates(time), site, Math.toRadians(-3.0), time);
				long sunset = time+tts;
				long newtime = time + tts/2;
				timediff = newtime - time;
				
				System.err.println("At "+(new Date(time))+" TTS= "+tts+" Sunset at "+(new Date(sunset)));
				time = newtime;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
