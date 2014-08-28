package ngat.astrometry;

public interface NonSiderealTrackingCalculator {

	/** Calculate the NS tracking rate for a track at supplied time.
	 * @param track The track caclualator.
	 * @param time The time.
	 * @return NS Tracking rates.
	 * @throws AstrometryException
	 */
	public TrackingRates getNonSiderealTrackingRates(TargetTrackCalculator track, long time) throws AstrometryException;
	
}
