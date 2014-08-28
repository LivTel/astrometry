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
public class MinElevationTest {

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
			TargetTrackCalculator tpc = new BasicTargetCalculator(star, site);

			// pick 2 times
			long now = System.currentTimeMillis();
			long t1 = now + (long) ((Math.random() - 0.5) * 86400000.0);
			long t2 = t1 + (long) (Math.random() * 86400000.0);

			double minel = 99.0;
			long mintime = 0L;
			long time = t1;
			while (time < t2) {
				Coordinates c = tpc.getCoordinates(time);
				double elev = astro.getAltitude(c, site, time);
				System.err.printf("Star alt: %tT %3.4f \n", time, Math.toDegrees(elev));
			
				if (elev < minel) {
					minel = elev;
					mintime = time;
				}	
				time += 15 * 60 * 1000L;
			}
			
			double minel2 = astro.getMinimumAltitude(tpc, site, t1, t2);
			System.err.printf("Star: MINElev between %tT %tT was %3.2f or %3.2f at %tT",t1,t2,Math.toDegrees(minel2), Math.toDegrees(minel), mintime);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
