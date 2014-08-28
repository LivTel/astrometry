/**
 * 
 */
package ngat.astrometry;

/**
 * @author eng
 * 
 */
public class BasicNonSiderealTrackingCalculator implements NonSiderealTrackingCalculator {

	/** Tracking delta time - we may need to use an adaptive scheme. */
	public static final long TRACK_DELTA = 24 * 3600 * 1000L;

	/**
	 * Maximum ns track diff allowed in ns track calculation (rads) corresponds
	 * 1/2 deg/sec which is massive.
	 */
	public static final double MAX_DD = Math.toRadians(0.01); // 1/100 deg is
																// about 40 asec

	/** Compute NS tracking rate as rads/sec.
	 * @see ngat.astrometry.NonSiderealTrackingCalculator#getNonSiderealTrackingRates(ngat.astrometry.TargetTrackCalculator,
	 *      long)
	 */
	public TrackingRates getNonSiderealTrackingRates(TargetTrackCalculator track, long time) throws AstrometryException {
		double d_ra = 999.0;
		double d_dec = 999.0;
		long dt = TRACK_DELTA;
		int ic = 0;
		while ((Math.abs(d_ra) + Math.abs(d_dec) > MAX_DD)) {
			ic++;
			dt /= 2;

			Coordinates p0 = track.getCoordinates(time);
			Coordinates p1 = track.getCoordinates(time + dt);

			d_ra = (p1.getRa() - p0.getRa());
			d_dec = (p1.getDec() - p0.getDec());
			System.err.println("NS Track iteration [" + ic + "] D_t = " + (dt / 1000) + "s, D_ra = "
					+ (Math.toDegrees(d_ra) * 3600.0) + "asec,  D_dec = " + (Math.toDegrees(d_dec) * 3600.0) + "asec");
		}
		System.err.println("NS Track calculated after [" + ic + "] iterations");
		// Ok we can use these they are small enough now... mult by 1k as want
		// rads per second
		return new TrackingRates(1000.0 * d_ra / (double) dt, 1000.0 * d_dec / (double) dt);

	}

}
