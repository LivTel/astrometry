/**
 * 
 */
package ngat.astrometry;

import java.util.Date;

/**
 * @author eng
 * 
 */
public class BasicNonSiderealTrackingCalculator implements
		NonSiderealTrackingCalculator {

	/** Tracking delta time - we may need to use an adaptive scheme. */
	public static final long TRACK_DELTA = 24 * 3600 * 1000L;

	/**
	 * Maximum ns track diff allowed in ns track calculation (rads) corresponds
	 * 1/2 deg/sec which is massive.
	 */
	public static final double MAX_DD = Math.toRadians(0.01); // 1/100 deg is
																// about 40 asec

	/**
	 * Compute NS tracking rate as rads/sec.
	 * 
	 * @see ngat.astrometry.NonSiderealTrackingCalculator#getNonSiderealTrackingRates(ngat.astrometry.TargetTrackCalculator,
	 *      long)
	 */
	public TrackingRates getNonSiderealTrackingRates(
			TargetTrackCalculator track, long time) throws AstrometryException {

		long dt = TRACK_DELTA;

		// start with a guess of 180degs
		double dd = Math.toRadians(Math.PI);

		Coordinates c0 = null;
		Coordinates c1 = null;

		int ic = 0;
		while ((dd > MAX_DD) && dt > 1L) {
			ic++;
			dt /= 2;

			// work out 2 positions, now and now plus dt
			c0 = track.getCoordinates(time);
			c1 = track.getCoordinates(time + dt);

			double ra1 = c0.getRa();
			double dec1 = c0.getDec();
			double ra2 = c1.getRa();
			double dec2 = c1.getDec();

			// angular seperation - distance to travel - TODO do we need to
			// check wraps ??? or does it just work anyway ???

			dd = Math.acos(Math.cos(dec1) * Math.cos(dec2)
					* Math.cos(ra1 - ra2) + Math.sin(dec1) * Math.sin(dec2));

			System.err.println("NS Track iteration [" + ic + "] S = "
					+ new Date(time) + ", E = " + new Date(time + dt));
			System.err.println("NS Track iteration [" + ic + "] DT = " + dt
					+ "ms, DD = " + (Math.toDegrees(dd) * 3600.0) + "asec");

		}

		System.err.println("NS Track calculated after [" + ic + "] iterations");

		// we need to calculate rates
		double ra1 = c0.getRa();
		double dec1 = c0.getDec();
		double ra2 = c1.getRa();
		double dec2 = c1.getDec();

		// check if ra1 and ra2 are in different quadrants ie 0-90 and 270-360
		// NOTE:in theory we could have one or other in 180-270 and one in 0-90
		// but that would mean an enormous
		// tracking length which we will ignore as being ridiculous

		boolean ra1neg = (ra1 >= 1.5 * Math.PI && ra1 < 2.0 * Math.PI);
		boolean ra2neg = (ra2 >= 1.5 * Math.PI && ra2 < 2.0 * Math.PI);

		boolean ra1pos = (ra1 >= 0.0 && ra1 < 0.5 * Math.PI);
		boolean ra2pos = (ra2 >= 0.0 && ra2 < 0.5 * Math.PI);

		if ((ra1neg && ra2pos) || (ra1pos && ra2neg)) {

			// whichever one is neg needs shifting
			if (ra1neg)
				ra1 = ra1 - 2.0 * Math.PI;
			else
				ra2 = ra2 - 2.0 * Math.PI;

		}

		// the rates, mult by 1k as want per second...
		double rarate = 1000.0 * (ra2 - ra1) / (double) dt;
		double decrate = 1000.0 * (dec2 - dec1) / (double) dt;

		System.err
				.println("NS Returning rates: " + Math.toDegrees(rarate)
						* 3600.0 + "as/s, " + Math.toDegrees(decrate) * 3600.0
						+ "as/s");

		return new TrackingRates(rarate, decrate);

	}

}
