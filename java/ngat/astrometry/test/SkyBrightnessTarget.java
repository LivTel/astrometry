/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.SkyBrightnessCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class SkyBrightnessTarget {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			double ra = Math.toRadians(Double.parseDouble(args[0]));
			double dec = Math.toRadians(Double.parseDouble(args[1]));
			
			XExtraSolarTarget target = new XExtraSolarTarget();
			target.setRa(ra);
			target.setDec(dec);

			double lat = Math.toRadians(28.0);
			double lon = Math.toRadians(-17.0);
			
			ISite site = new BasicSite("telescope", lat, lon);
			
			TargetTrackCalculator track = new BasicTargetCalculator(target, site);
			
			AstrometrySiteCalculator astro = new BasicAstrometrySiteCalculator(site);
			SkyBrightnessCalculator sbc = new SkyBrightnessCalculator(site);
			long start = System.currentTimeMillis();
			long time = start;
			while (time < start + 120*24*3600*1000L) {
							
				double skyb = sbc.getSkyBrightnessCriterion(track, time);
				
				System.err.printf("%tF %tT skyb= %4.2f \n", time, time, skyb);
				
				time += 3600*1000L;
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
