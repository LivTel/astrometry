/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.*;
import ngat.phase2.*;
import java.text.*;
import java.util.*;

/**
 * @author eng
 * 
 */
public class TimeToSet {

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

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleTimeZone utc = new SimpleTimeZone(0,"UTC");
			sdf.setTimeZone(utc);

			long time = (sdf.parse("2009-09-08 20:21:00")).getTime();
			
			long now = System.currentTimeMillis();
			long t = now;
			
			// how long to set at specified time
			while (t < now + 24 * 3600 * 1000L) {

			    Coordinates c = tc.getCoordinates(t);
			    double ha = calc.getHourAngle(c, site, t);
			    double alt = calc.getAltitude(c, site, t);

			    try {
				long tts = calc.getTimeUntilNextSet(c, site, horizon, t);
				System.err.printf("At: %s HA: %3.2f Alt: %3.2f : %8d \n", 
						  sdf.format(new Date(t)), Math.toDegrees(ha), Math.toDegrees(alt), (tts/1000));
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
