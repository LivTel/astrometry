/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class TransitTiming {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	

		try {
			double lat = Math.toRadians(28.0);
			double lon = Math.toRadians(-17.0);

			BasicSite site = new BasicSite("Obs", lat, lon);

			double ra = Math.toRadians(Double.parseDouble(args[0]));
			double dec = Math.toRadians(Double.parseDouble(args[1]));

			XExtraSolarTarget target = new XExtraSolarTarget("Star1");
			target.setRa(ra);
			target.setDec(dec);

			
			BasicAstrometryCalculator calc = new BasicAstrometryCalculator();
			TargetTrackCalculator tpc = new BasicTargetCalculator(target, site);
			
			long now = System.currentTimeMillis();
			long t = now;
			while (t < now + 24 * 3600 * 1000L) {
				Coordinates c = tpc.getCoordinates(t);
				double alt = calc.getAltitude(c, site, t);
				double azm = calc.getAzimuth(c, site, t);
				double ha = calc.getHourAngle(c, site, t);
				long tst = calc.getTimeSinceLastTransit(c, site, t);
				long tnt = calc.getTimeUntilNextTransit(c, site, t);
				System.err.printf("%tT Azm: %4.2f Alt: %4.2f HA: %4.2fh Last: %6d Next: %6d\n", 
						t, Math.toDegrees(azm), Math.toDegrees(alt), Math.toDegrees(ha)/15, (tst/60000), (tnt/60000));
				t += 15 * 60 * 1000L;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
