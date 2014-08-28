/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class MaxElevationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			double ra = Math.toRadians(cfg.getDoubleValue("ra"));
			double dec = Math.toRadians(cfg.getDoubleValue("dec"));

			XExtraSolarTarget star = new XExtraSolarTarget("star1");
			star.setRa(ra);
			star.setDec(dec);

			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));

			ISite site = new BasicSite("obs", lat, lon);
			
			AstrometryCalculator astro = new BasicAstrometryCalculator();
			TargetTrackCalculator track = new BasicTargetCalculator(star, site);

			// pick 2 times
			long now = System.currentTimeMillis();
			long t1 = now + (long) ((Math.random() - 0.5) * 86400000.0);
			long t2 = t1 + (long) (Math.random() * 86400000.0);

			double maxel = -99.0;
			long maxtime = 0L;
			long time = t1;
			while (time < t2) {
				Coordinates c = track.getCoordinates(time);
				double elev = astro.getAltitude(c, site, time);
				System.err.printf("Star alt: %tT %3.4f \n", time, Math.toDegrees(elev));
			
				if (elev > maxel) {
					maxel = elev;
					maxtime = time;
				}	
				time += 15 * 60 * 1000L;
			}
			
			double maxel2 = astro.getMaximumAltitude(track, site, t1, t2);
			System.err.printf("Star: MaxElev between %tT %tT was %3.2f or %3.2f at %tT",t1,t2,Math.toDegrees(maxel2), Math.toDegrees(maxel), maxtime);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
