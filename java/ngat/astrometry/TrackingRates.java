/**
 * 
 */
package ngat.astrometry;

/**
 * @author eng
 *
 */
public class TrackingRates {

	/** Tracking rate in RA (rad/sec).*/
	private double nsTrackingRateRA;
	
	/** Tracking rate in dec (rad/sec).*/
	private double nsTrackingRateDec;

	/**
	 * @param nsTrackingRateRA
	 * @param nsTrackingRateDec
	 */
	public TrackingRates(double nsTrackingRateRA, double nsTrackingRateDec) {
		super();
		this.nsTrackingRateRA = nsTrackingRateRA;
		this.nsTrackingRateDec = nsTrackingRateDec;
	}

	/**
	 * @return the nsTrackingRateRA
	 */
	public double getNsTrackingRateRA() {
		return nsTrackingRateRA;
	}

	/**
	 * @param nsTrackingRateRA the nsTrackingRateRA to set
	 */
	public void setNsTrackingRateRA(double nsTrackingRateRA) {
		this.nsTrackingRateRA = nsTrackingRateRA;
	}

	/**
	 * @return the nsTrackingRateDec
	 */
	public double getNsTrackingRateDec() {
		return nsTrackingRateDec;
	}

	/**
	 * @param nsTrackingRateDec the nsTrackingRateDec to set
	 */
	public void setNsTrackingRateDec(double nsTrackingRateDec) {
		this.nsTrackingRateDec = nsTrackingRateDec;
	}
	
	
	
}
