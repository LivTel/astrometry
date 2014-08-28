/**
 * 
 */
package ngat.astrometry;

/**
 * @author eng
 *
 */
public interface AstrometrySiteCalculator {
	
	/** get the site used by this calculator.*/
	public ISite getSite() throws AstrometryException;
	
	/** get the altitude of the target.*/
	public double getAltitude(Coordinates coord, long time) throws AstrometryException;
	
	/** get the azimuth of the target.*/
	public double getAzimuth(Coordinates coord, long time) throws AstrometryException;
	
	/** get the target's HA (rads).*/
	public double getHourAngle(Coordinates c, long time) throws AstrometryException;
	
	// RISING
	
	/**can the coord rise above H.*/
	boolean canRise(Coordinates coord, double horizon, long time) throws AstrometryException;
	
	/** is the coord already risen above H.*/
	boolean isRisen(Coordinates coord, double horizon, long ttime) throws AstrometryException;
	
	/** how long till coord rises above H.*/
	long getTimeUntilNextRise(Coordinates coord, double horizon, long time) throws AstrometryException;
	
	/** how long since coord rose above H.*/
	long getTimeSinceLastRise(Coordinates coord, double horizon, long time) throws AstrometryException;
	
	// SETTING 
	
	/** can the coord set below H.*/
	boolean canSet(Coordinates coord, double horizon, long time) throws AstrometryException;
	
	/** is the coord already set below H.*/
	boolean isSet(Coordinates coord, double horizon, long time)throws AstrometryException;
	
	/** how long till coord sets below H.*/
	long getTimeUntilNextSet(Coordinates coord, double horizon, long time) throws AstrometryException;
	
	/** how long since coord set below H.*/
	long getTimeSinceLastSet(Coordinates coord, double horizon, long time) throws AstrometryException;

	// TRANSITS
	
	/** what height does the coord transit.*/
	double getTransitAltitude(Coordinates coord, long time) throws AstrometryException;
	
	/** how long till next transit.*/
	long getTimeUntilNextTransit(Coordinates coord, long time) throws AstrometryException;
	
	/** how long since last transit.*/
	long getTimeSinceLastTransit(Coordinates coord, long time) throws AstrometryException;
	
	//public double getAngularSeperation(Coordinates a, Coordinates b) throws AstrometryException;
	
	// STATISTICS - REALLY SHOULD BE ELSEWHERE !

	public double getMaximumAltitude(TargetTrackCalculator tgt1, long t1, long t2) throws AstrometryException;
	
	public double getMinimumAltitude(TargetTrackCalculator tgt1, long t1, long t2) throws AstrometryException;

	public double getAngularSeperation(Coordinates a, Coordinates b) throws AstrometryException;
	
	public double getParalacticAngle(Coordinates c1, long time) throws AstrometryException;
	
	public double getClosestPointOfApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1, long t2) throws AstrometryException;
	
	
	
}
