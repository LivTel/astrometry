/**
 * 
 */
package ngat.astrometry;

import java.io.Serializable;

import javax.swing.JSlider;

import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 * 
 */
public class BasicAstrometryCalculator implements AstrometryCalculator, Serializable {

	AstroLib astroLib;
	
	/**
	 * Create a BasicAstrometryCalculator using JAstroSlalib linked to native slalib.
	 */
	public BasicAstrometryCalculator() {
		astroLib = new JAstroSlalib();
	}

	
	
	/** Create a BasicAstrometryCalculator using the supplied AstroLib implementation.
	 * @param astroLib
	 */
	public BasicAstrometryCalculator(AstroLib astroLib) {
		super();
		this.astroLib = astroLib;
	}



	/**
	 * Calculate the altitude of a target from a given site at specified time.
	 * 
	 * @param target
	 *            The specified target.
	 * @param site
	 *            The site from where it is to be observed.
	 * @param time
	 *            Time of observation.
	 * @see ngat.astrometry.AstrometryCalculator#getAltitude(ngat.phase2.Coordinates,
	 *      ngat.astrometry.ISite, long)
	 * @return The altitude in radians.
	 */
	public double getAltitude(Coordinates c, ISite site, long time) throws AstrometryException {

		double ha = getHourAngle(c, site, time);

		double sphi = Math.sin(site.getLatitude());
		double cphi = Math.cos(site.getLatitude());

		double arg = sphi * Math.sin(c.getDec()) + cphi * Math.cos(c.getDec()) * Math.cos(ha);

		return Math.asin(arg);
	
	}

	/**
	 * Calculate the azimuth of a target from a given site at specified time.
	 * 
	 * @param target
	 *            The specified target.
	 * @param site
	 *            The site from where it is to be observed.
	 * @param time
	 *            Time of observation.
	 * @see ngat.astrometry.AstrometryCalculator#getAzimuth(ngat.phase2.Coordinates,
	 *      ngat.astrometry.ISite, long)
	 * @return The azimuth corrected to [0, 2*PI] in radians.
	 */
	public double getAzimuth(Coordinates c, ISite site, long time) throws AstrometryException {

		double sphi = Math.sin(site.getLatitude());
		double cphi = Math.cos(site.getLatitude());

		double alt = getAltitude(c, site, time);

		double arg = (Math.sin(c.getDec()) - sphi * Math.sin(alt)) / (cphi * Math.cos(alt));
		double az = Math.acos(arg);

		double ha = getHourAngle(c, site, time);
		if (Math.sin(ha) < 0.0)
			return az;
		
		return 2.0*Math.PI-az;
					
	}

	/** Can the target rise above horizon H. */
	public boolean canRise(Coordinates c, ISite site, double horizon, long time) throws AstrometryException {

		double phi = site.getLatitude();
		double dec = c.getDec();

		if (phi > 0.0) {
			// Northern Site
			if (horizon < phi) {
				// Low horizon
				if (dec < -Math.PI / 2 + phi + horizon)
					return false;
			} else {
				// High horizon
				if (dec < -Math.PI / 2 + phi + horizon)
					return false;
				if (dec > Math.PI / 2 + phi - horizon)
					return false;
			}

		} else {
			// Southern Site
			if (horizon < -phi) {
				// Low horizon
				if (dec > Math.PI / 2 + phi - horizon)
					return false;
			} else {
				// High horizon
				if (dec < -Math.PI / 2 + phi + horizon)
					return false;
				if (dec > Math.PI / 2 + phi - horizon)
					return false;

			}

		}

		return true;

	}

	public boolean canSet(Coordinates c, ISite site, double horizon, long time) throws AstrometryException {

		double phi = site.getLatitude();
		double dec = c.getDec();

		if (phi > 0.0) {
			// Northern Site
			if (horizon < phi) {
				// Low horizon
				if (dec > Math.PI / 2 - phi + horizon)
					return false;
			}
			// No target will NOT set if horizon > phi
		} else {
			// Southern Site
			if (horizon < -phi) {
				// Low horizon
				if (dec < -Math.PI / 2 - phi - horizon)
					return false;
			}
			// No target will NOT set if horizon > -phi
		}

		return true;

	}

	public long getTimeSinceLastRise(Coordinates c, ISite site, double horizon, long time)
			throws AstrometryException {

	    double ff = 43200000.0 / Math.PI;

	    double lst = getLST(site, time);
            double ha = getHA(c, lst);

            double hset = hset(c, site, horizon, time);


	    if (2.0*Math.PI - hset < ha && ha < 2.0*Math.PI)
                return (long) ((ha + hset - 2.0*Math.PI) * ff);

	    if (ha < hset)
		return (long) ((ha + hset) * ff);

	    throw new AstrometryException("getTimeSinceLastSet(): Target is already set: " +
	    " lst="+Math.toDegrees(lst)+", ha="+Math.toDegrees(ha)+", hset="+Math.toDegrees(hset));

	}

	public long getTimeSinceLastSet(Coordinates c, ISite site, double horizon, long time)
			throws AstrometryException {

	    double ff = 43200000.0 / Math.PI;

	    double lst = getLST(site, time);
	    double ha = getHA(c, lst);

	    double hset = hset(c, site, horizon, time);

	    if (hset < ha && ha < 2.0*Math.PI - hset)
		return (long) ((ha - hset) * ff);

	    // may need to allow when risen but more complex.
	    throw new AstrometryException("getTimeSinceLastSet(): Target is already risen: "+
	    		" lst="+Math.toDegrees(lst)+", ha="+Math.toDegrees(ha)+", hset="+Math.toDegrees(hset));
	}

	public long getTimeSinceLastTransit(Coordinates c, ISite site, long time) throws AstrometryException {
		double ff = 43200000.0 / Math.PI;

		double lst = getLST(site, time);
		double ha = getHA(c, lst);

		return (long)(ha* ff);
		
	}

	public long getTimeUntilNextRise(Coordinates c, ISite site, double horizon, long time)
			throws AstrometryException {

	    double ff = 43200000.0 / Math.PI;

	    double lst = getLST(site, time);
	    double ha = getHA(c, lst);

	    double hset = hset(c, site, horizon, time);

	    if (hset < ha && ha < 2.0*Math.PI - hset)
		return (long) ((2.0 * Math.PI - ha - hset) * ff);

	    throw new AstrometryException("getTimeUntilNextRise(): Target is already risen");

	}

	public long getTimeUntilNextSet(Coordinates c, ISite site, double horizon, long time) throws AstrometryException {

		double ff = 43200000.0 / Math.PI;

		double lst = getLST(site, time);
		double ha = getHA(c, lst);
		
		double hset = hset(c, site, horizon, time);
		
		// snf  4-10-10 make leq
		if (2.0 * Math.PI - hset < ha && ha <= 2.0 * Math.PI)
			return (long) ((2.0 * Math.PI - ha + hset) * ff);
		// snf  4-10-10 make leq
		if (0.0 <= ha && ha < hset)
			return (long) ((hset - ha) * ff);

		throw new AstrometryException("getTimeUntilNextSet(): Target is already set");

	}

	public long getTimeUntilNextTransit(Coordinates c, ISite site, long time) throws AstrometryException {
		
		double ff = 43200000.0 / Math.PI;

		double lst = getLST(site, time);
		double ha = getHA(c, lst);

		return (long)((2.0 * Math.PI - ha)* ff);
	}

	public double getTransitAltitude(Coordinates c, ISite site, long time) throws AstrometryException {

		return Math.PI / 2 - Math.abs(site.getLatitude() - c.getDec());

	}

	public boolean isRisen(Coordinates target, ISite site, double horizon, long ttime) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSet(Coordinates sgt, ISite site, double horizon, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Computes the local sidereal time at a given observatory site.
	 * 
	 * @param site
	 *            The observatory site.
	 * @param time
	 *            When we want the LST for.
	 * @return The LST at the specified site at the given time.
	 */
	public double getLST(ISite site, long time) throws AstrometryException {
		if (site == null)
			throw new AstrometryException("getLST(): Site was null");

		return astroLib.getLST(time, site.getLongitude());
	}

	/**
	 * Computes the Hour Angle of a target at a given LST.
	 * 
	 * @param c
	 *            The coordinates of the target to calculate HA for.
	 * @param lst
	 *            The LST at which the HA is required.
	 * @return The HA for target at the specified site at the given LST.
	 */
	public double getHA(Coordinates c, double lst) throws AstrometryException {
		if (c == null)
			throw new AstrometryException("getHA(): Coordinates were null");

		return correct(lst - c.getRa());
	}

	/** A convenience method - not in current interface. */
	public double getHourAngle(Coordinates c, ISite site, long time) throws AstrometryException {
		double lst = getLST(site, time);
		return getHA(c, lst);
	}

	/**
	 * Correct an angle to the range [0,2*pi].
	 * 
	 * @param angle
	 *            The angle to correct.
	 * @return
	 */
	private double correct(double angle) {
		double a = angle;
		while (a >= Math.PI * 2.0)
			a -= Math.PI * 2.0;
		while (a < 0.0)
			a += Math.PI * 2.0;
		return a;
	}

	/**
	 * Caclulates the HA of setting (and 2 * PI - rising) at a given horizon for
	 * a target of specified dec at a given site.
	 * 
	 * @param dec
	 *            The declination of the target (rads).
	 * @param site
	 *            The observing site.
	 * @param horizon
	 *            The horizon elevation (rads).
	 * @param time
	 *            The time at which to check.
	 * @return The HA of setting (rads).
	 * @throws AstrometryException
	 *             If anything is amiss.
	 */
	private double hset(Coordinates c, ISite site, double horizon, long time) throws AstrometryException {

		double dec = c.getDec();
		double sphi = Math.sin(site.getLatitude());
		double cphi = Math.cos(site.getLatitude());

		double arg = (Math.sin(horizon) - Math.sin(dec) * sphi) / (Math.cos(dec) * cphi);

		// check if the target is circumpolar
		if (Math.abs(arg) > 1.0)
			throw new AstrometryException(String.format("hset(): target is circumpolar for horizon: %2.2f", Math
					.toDegrees(horizon)));

		return Math.acos(arg);

	}

	/** Calculate (approximately) the CPA between 2 target tracks. */
	public double getClosestPointOfApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1,
			long t2) throws AstrometryException {

		// Times: a = start, b = middle, c= end

		long tm = (t1 + t2) / 2;

		// Get all the coordinates together
		Coordinates t1a = tgt1.getCoordinates(t1);
		//System.err.printf("T1 Coords at: %tT %3.2f %3.2f \n", t1, Math.toDegrees(t1a.getRa()), Math.toDegrees(t1a
			//	.getDec()));
		Coordinates t1b = tgt1.getCoordinates(tm);
		Coordinates t1c = tgt1.getCoordinates(t2);

		Coordinates t2a = tgt2.getCoordinates(t1);
		//System.err.printf("T2 Coords at: %tT %3.2f %3.2f \n", t1, Math.toDegrees(t2a.getRa()), Math.toDegrees(t2a
				//.getDec()));
		Coordinates t2b = tgt2.getCoordinates(tm);
		Coordinates t2c = tgt2.getCoordinates(t2);

		// Calculate the bearings at each time.

		double da = getAngularSeperation(t1a, t2a);
		double db = getAngularSeperation(t1b, t2b);
		double dc = getAngularSeperation(t1c, t2c);

		//System.err.printf("Target distance at: %tT : %3.2f \n", t1, Math.toDegrees(da));
		//System.err.printf("Target distance at: %tT : %3.2f \n", tm, Math.toDegrees(db));
		//System.err.printf("Target distance at: %tT : %3.2f \n", t2, Math.toDegrees(dc));

		// Now work out whats going on wrt tracks.

		// opening
		if (da <= db && db <= dc) {
			//System.err.println("Opening");
			return da;
		}

		// closing
		if (da >= db && db >= dc) {
			//System.err.println("Closing");
			return dc;
		}

		// near miss
		if (da >= db && dc >= db) {
			//System.err.println("Near miss");
			return db;
		}

		// weird
		return Math.min(da, dc);
	}

	/** How low does the target get between the specified times. */
	public double getMinimumAltitude(TargetTrackCalculator tgt, ISite site, long fromTime, long untilTime)
			throws AstrometryException {
		if (untilTime < fromTime)
			throw new AstrometryException("getMaximumAltitude(): Times reversed ?");
		
		Coordinates c1 = tgt.getCoordinates(fromTime);
		double lst1 = getLST(site, fromTime);
		double ha1 = getHA(c1, lst1);

		Coordinates c2 = tgt.getCoordinates(untilTime);
		double lst2 = getLST(site, untilTime);
		double ha2 = getHA(c2, lst2);
		
		if (untilTime - fromTime > 86164090L) {
			// its more than one SD so use transit_elev		
			return -(0.5*Math.PI - Math.abs(c1.getDec() + site.getLatitude()));
		} else {
			// shift origin of HA system by 180 degrees
			double hp1 = correct(ha1+Math.PI);
			double hp2 = correct(ha2+Math.PI);
			
			if (hp2 < hp1)				
				return -(0.5*Math.PI - Math.abs(c1.getDec() + site.getLatitude()));
			else
				return Math.min(getAltitude(c1, site, fromTime), getAltitude(c2, site, untilTime));		
		}
	}

	
	/** How high can the target get between the specified times. */
	public double getMaximumAltitude(TargetTrackCalculator tgt, ISite site, long fromTime, long untilTime)
			throws AstrometryException {

		if (untilTime < fromTime)
			throw new AstrometryException("getMaximumAltitude(): Times reversed ?");

		Coordinates c1 = tgt.getCoordinates(fromTime);
		double lst1 = getLST(site, fromTime);
		double ha1 = getHA(c1, lst1);

		Coordinates c2 = tgt.getCoordinates(untilTime);
		double lst2 = getLST(site, untilTime);
		double ha2 = getHA(c2, lst2);

		if (untilTime - fromTime > 86164090L) {
			// its more than one SD so use transit_elev
			return getTransitAltitude(c1, site, fromTime);

		} else {

			// rising, setting, high-transit, low-transit
			// all we really need is: high-transit or not...
			if (ha2 < ha1)
				return getTransitAltitude(c1, site, fromTime);
			else
				return Math.max(getAltitude(c1, site, fromTime), getAltitude(c2, site, untilTime));
			//
		}
		//
	}

	public long getTimeOfClosestApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1, long t2)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getAngularSeperation(Coordinates a, Coordinates b) throws AstrometryException {
		return Math.acos(Math.cos(a.getDec()) * Math.cos(b.getDec()) * Math.cos(a.getRa() - b.getRa())
				+ Math.sin(a.getDec()) * Math.sin(b.getDec()));
	}



	public double getParalacticAngle(Coordinates c, ISite site, long time) throws AstrometryException {

	    // method 1 using sladbear
	    /*double az1 = getAzimuth(c1, site, time);
		double alt1 = getAltitude(c1, site, time);
		//double az2 = Math.PI;
 		double az2 = 0.0;
		double alt2 = site.getLatitude();
	 	return astroLib.calcBearing(az1, alt1, az2, alt2);
	    */

	    // method 2 using slapa
	    double ha = getHourAngle(c, site, time);
	    double lat = site.getLatitude();
	    double cp = Math.cos(lat);
	    double sp = Math.sin(lat);
	    double dec = c.getDec();
	    double sqsz = cp * Math.sin(ha);
	    double cqsz = sp * Math.cos(dec) - cp * Math.sin(dec) * Math.cos(ha);
	    double p3 = (( sqsz != 0.0 || cqsz != 0.0 ) ? Math.atan2 (sqsz, cqsz) : 0.0);
	    
	    return p3;

	    //double azm = astro.getAzimuth(c, site, time);
	    //double alt = astro.getAltitude(c, site, time);

	}

}
