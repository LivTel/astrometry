/**
 * 
 */
package ngat.astrometry.approximate;

import java.util.Calendar;
import java.util.SimpleTimeZone;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometryException;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;

/**
 * @author eng
 *
 */
public class SImpleAstroCalculator implements AstrometryCalculator {

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0,"UTC");
	
	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#canRise(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public boolean canRise(Coordinates coord, ISite site, double horizon, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#canSet(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public boolean canSet(Coordinates coord, ISite site, double horizon, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getAltitude(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public double getAltitude(Coordinates coord, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getAzimuth(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public double getAzimuth(Coordinates coord, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getClosestPointOfApproach(ngat.astrometry.TargetTrackCalculator, ngat.astrometry.TargetTrackCalculator, long, long)
	 */
	public double getClosestPointOfApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1, long t2)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getHourAngle(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public double getHourAngle(Coordinates c, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getMaximumAltitude(ngat.astrometry.TargetTrackCalculator, ngat.astrometry.ISite, long, long)
	 */
	public double getMaximumAltitude(TargetTrackCalculator tgt1, ISite site, long t1, long t2)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getMinimumAltitude(ngat.astrometry.TargetTrackCalculator, ngat.astrometry.ISite, long, long)
	 */
	public double getMinimumAltitude(TargetTrackCalculator tgt1, ISite site, long t1, long t2)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeOfClosestApproach(ngat.astrometry.TargetTrackCalculator, ngat.astrometry.TargetTrackCalculator, long, long)
	 */
	public long getTimeOfClosestApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1, long t2)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeSinceLastRise(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public long getTimeSinceLastRise(Coordinates coord, ISite site, double horizon, long time)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeSinceLastSet(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public long getTimeSinceLastSet(Coordinates coord, ISite site, double horizon, long time)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeSinceLastTransit(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public long getTimeSinceLastTransit(Coordinates coord, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeUntilNextRise(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public long getTimeUntilNextRise(Coordinates coord, ISite site, double horizon, long time)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeUntilNextSet(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public long getTimeUntilNextSet(Coordinates coord, ISite site, double horizon, long time)
			throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTimeUntilNextTransit(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public long getTimeUntilNextTransit(Coordinates coord, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#getTransitAltitude(ngat.astrometry.Coordinates, ngat.astrometry.ISite, long)
	 */
	public double getTransitAltitude(Coordinates coord, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#isRisen(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public boolean isRisen(Coordinates coord, ISite site, double horizon, long ttime) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometryCalculator#isSet(ngat.astrometry.Coordinates, ngat.astrometry.ISite, double, long)
	 */
	public boolean isSet(Coordinates coord, ISite site, double horizon, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return false;
	}
	
	private double getGMST(ISite site, long time) throws AstrometryException {
		if (site == null)
			throw new AstrometryException("getLST(): Site was null");
		
		Calendar c2000 = Calendar.getInstance();
		c2000.setTimeZone(UTC);
		c2000.set(2000, 0, 1, 12, 0, 0);
		System.err.println("Cal2000: "+c2000.getTime());
		// days since 2000-01-01 at 12:00:00, 
		double d = (double)(time-c2000.getTimeInMillis())/86400000.0;
		double gmst = 18.697374558 + 24.06570982441908 * d;
		System.err.println("GMST(H) = "+gmst);
		double gmsth0 = 24.0*Math.floor(gmst / 24.0);
		
		// remove integral part, in hours now
		//double gmst0 = Math.floor(gmst);
		
		// gmst in degs
		double gmstd = 15.0*(gmst - gmsth0);
		
		return Math.toRadians(gmstd);
		
	}
	
	public double getLST(ISite site, long time) throws AstrometryException {
		
		return correct(getGMST(site, time)+site.getLongitude());
	}
	
	private double correct(double angle) {
		double a = angle;
		while (a >= Math.PI * 2.0)
			a -= Math.PI * 2.0;
		while (a < 0.0)
			a += Math.PI * 2.0;
		return a;
	}

	public double getParalacticAngle(Coordinates c1, ISite site, long time) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getAngularSeperation(Coordinates a, Coordinates b) throws AstrometryException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
