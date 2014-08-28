/**
 * 
 */
package ngat.astrometry;

/** An astrometry calculator for a specified target.
 * @author eng
 *
 */
public interface AstrometryTargetCalculator {

	/** calculate target's altitude at time.*/
	public double getAltitude(ISite site, long time) throws AstrometryException;
	
	/** calculate target's azimuth at time.*/
	public double getAzimuth(ISite site, long time) throws AstrometryException;

	/**can the target rise above H.*/
	public boolean canRise(ISite site, double horizon, long time) throws AstrometryException;
	
	/** is the target already risen above H.*/
	public boolean isRisen(ISite site, double horizon, long ttime) throws AstrometryException;
	
	/** how long till target rises above H.*/
	public long getTimeUntilNextRise(ISite site, double horizon, long time) throws AstrometryException;
	
	/** how long since target rose above H.*/
	public long getTimeSinceLastRise(ISite site, double horizon, long time) throws AstrometryException;
	
	// SETTING 
	
	/** can the target set below H.*/
	public boolean canSet(ISite site, double horizon, long time) throws AstrometryException;
	
	/** is the target already set below H.*/
	public boolean isSet(ISite site, double horizon, long time)throws AstrometryException;
	
	/** how long till target sets below H.*/
	public long getTimeUntilNextSet(ISite site, double horizon, long time) throws AstrometryException;
	
	/** how long since target set below H.*/
	public long getTimeSinceLastSet(ISite site, double horizon, long time) throws AstrometryException;

	// TRANSITS
	
	/** what hight does the target transit.*/
	public double getTransitAltitude(ISite site, long time) throws AstrometryException;
	
	/** how long till next transit.*/
	public long getTimeUntilNextTransit(ISite site, long time) throws AstrometryException;
	
	/** how long since last transit.*/
	public long getTimeSinceLastTransit(ISite site, long time) throws AstrometryException;
	
	
}
