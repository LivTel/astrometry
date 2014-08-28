/**
 * 
 */
package ngat.astrometry.test;

import java.util.Date;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class CheckHARange {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			double lo = Math.toRadians(15.0*cfg.getDoubleValue("lo"));
			double hi = Math.toRadians(15.0*cfg.getDoubleValue("hi"));
			if (lo < 0.0)
				lo = 2.0 * Math.PI+lo;
			if (hi < 0.0)
				hi = 2.0 * Math.PI+hi;
			
			System.err.printf("lo = %3.2f, Hi= %3.2f \n", Math.toDegrees(lo), Math.toDegrees(hi));
			
			double ra = Math.toRadians(cfg.getDoubleValue("ra"));
			double dec = Math.toRadians(cfg.getDoubleValue("dec"));

			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));

			ISite site = new BasicSite("Obs", lat, lon);
			AstrometryCalculator astro = new BasicAstrometryCalculator();

			long now = System.currentTimeMillis();

			XExtraSolarTarget target = new XExtraSolarTarget("Star");
			target.setRa(ra);
			target.setDec(dec);

			TargetTrackCalculator tgtTrack = new BasicTargetCalculator(target, site);

			long time = now;
			while (time < now + 24 * 3600 * 1000L) {
				Coordinates c = tgtTrack.getCoordinates(time);
				double ha = astro.getHourAngle(c, site, time);

				boolean in = true;
				if (lo < hi) {
					if (ha < lo || ha > hi)
						in = false;
				} else {
					if (ha < lo && ha > hi)
						in = false;
				}

				System.err.printf("At %tT HA=%3.2f %b \n", time, (Math.toDegrees(ha) / 15.0), in);
				time += 10*60*1000L;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
