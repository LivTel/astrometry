/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.LunarCalculator;
import ngat.astrometry.SolarCalculator;
import ngat.astrometry.TargetTrackCalculator;

/**
 * @author eng
 * 
 */
public class MoonPhase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			long start = System.currentTimeMillis();

			BasicSite site = new BasicSite("obs", Math.toRadians(28.0), Math.toRadians(-17.0));

			long time = start;
			while (time < start + 30 * 24 * 3600 * 1000L) {

				TargetTrackCalculator sunCalc = new SolarCalculator();
				Coordinates sun = sunCalc.getCoordinates(time);
				double sra = sun.getRa();
				double sdec = sun.getDec();
				TargetTrackCalculator moonTrack = new LunarCalculator(site);
				Coordinates moon = moonTrack.getCoordinates(time);
				double mra = moon.getRa();
				double mdec = moon.getDec();

				double angle = Math.acos(Math.cos(mdec) * Math.cos(sdec) * Math.cos(mra - sra) + Math.sin(mdec)
						* Math.sin(sdec));
			
				double fraction = 0.5 * (1.0 + Math.cos(Math.PI - angle));
				double phase = (angle < Math.PI ? angle / Math.PI : (2.0 * Math.PI - angle) / Math.PI);

				System.err.printf("%tF %tT  Angle: %3.2f  Phase: %3.2f  Illum: %3.2f \n", 
						time, time, Math.toDegrees(angle), phase, fraction);

				time += 6*3600* 1000L;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
