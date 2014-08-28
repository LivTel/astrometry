/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.*;
import ngat.phase2.*;

/**
 * @author eng
 * 
 */
public class TimeToRise {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			double ra = Math.toRadians(Double.parseDouble(args[0]));
			double dec = Math.toRadians(Double.parseDouble(args[1]));

			XExtraSolarTarget target = new XExtraSolarTarget("Star1");
			target.setRa(ra);
			target.setDec(dec);
		
			double lat = Math.toRadians(Double.parseDouble(args[2]));
			double lon = Math.toRadians(Double.parseDouble(args[3]));

			BasicSite site = new BasicSite("Obs", lat, lon);
			BasicTargetCalculator tc = new BasicTargetCalculator(target, site);
			
			BasicAstrometryCalculator calc = new BasicAstrometryCalculator();

			double horizon =  Math.toRadians(Double.parseDouble(args[4]));
			
			long now = System.currentTimeMillis();
			long t = now;
			
			// how long to set at specified time
			while (t < now + 24 * 3600 * 1000L) {

				Coordinates c = tc.getCoordinates(t);
			    double ha = calc.getHourAngle(c, site, t);

			    try {
				long tts = calc.getTimeUntilNextRise(c, site, horizon, t);
				System.err.printf("Time to set at: %tT %3.2f : %8d \n", t, Math.toDegrees(ha), (tts/1000));
			    } catch (AstrometryException ax) {
				System.err.printf("Time to set at: %tT %3.2f : %s \n", t, Math.toDegrees(ha), ax.getMessage());
			    }
			    t += 15 * 60 * 1000L;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
