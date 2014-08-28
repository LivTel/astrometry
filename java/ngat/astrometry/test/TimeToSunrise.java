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
public class TimeToSunrise {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			double lat = Math.toRadians(Double.parseDouble(args[0]));
			double lon = Math.toRadians(Double.parseDouble(args[1]));

			BasicSite site = new BasicSite("Obs", lat, lon);
			SolarCalculator sun = new SolarCalculator();

			BasicAstrometryCalculator calc = new BasicAstrometryCalculator();

			double horizon = Math.toRadians(Double.parseDouble(args[2]));

			long now = System.currentTimeMillis();
			long t = now;

			System.err.printf("Time now: %tT \n", now);

			// how long to set at specified time
			while (t < now + 24 * 3600 * 1000L) {

				Coordinates c = sun.getCoordinates(t);
				double ha = calc.getHourAngle(c, site, t);

				try {
					long tts = calc.getTimeUntilNextRise(c, site, horizon, t);
					System.err.printf("Time to set at: %tT %3.2f : %8d \n", t, Math.toDegrees(ha), (tts / 1000));
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
