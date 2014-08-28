/**
 * 
 */
package ngat.astrometry;

import java.io.Serializable;

/**
 * @author eng
 *
 */
public class BasicAstrometrySiteCalculator implements AstrometrySiteCalculator, Serializable {

	private ISite site;
	
	private AstrometryCalculator astro;
	
	/** Create a BasicAstrometrySiteCalculator for the speciifed site.
	 * @param site The site for which this calculator is defined.
	 */
	public BasicAstrometrySiteCalculator(ISite site) {
		this.site = site;
		astro = new BasicAstrometryCalculator();
	}

	/**
	 * @param site
	 * @param astro
	 */
	public BasicAstrometrySiteCalculator(ISite site, AstrometryCalculator astro) {
		super();
		this.site = site;
		this.astro = astro;
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#canRise(ngat.astrometry.Coordinates, double, long)
	 */
	public boolean canRise(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.canRise(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#canSet(ngat.astrometry.Coordinates, double, long)
	 */
	public boolean canSet(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.canSet(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getAltitude(ngat.astrometry.Coordinates, long)
	 */
	public double getAltitude(Coordinates coord, long time) throws AstrometryException {
		return astro.getAltitude(coord, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getAzimuth(ngat.astrometry.Coordinates, long)
	 */
	public double getAzimuth(Coordinates coord, long time) throws AstrometryException {
	return astro.getAzimuth(coord, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getHourAngle(ngat.astrometry.Coordinates, long)
	 */
	public double getHourAngle(Coordinates c, long time) throws AstrometryException {
		return astro.getHourAngle(c, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getMaximumAltitude(ngat.astrometry.TargetTrackCalculator, long, long)
	 */
	public double getMaximumAltitude(TargetTrackCalculator tgt1, long t1, long t2) throws AstrometryException {
		return astro.getMaximumAltitude(tgt1, site, t1, t2);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getMinimumAltitude(ngat.astrometry.TargetTrackCalculator, long, long)
	 */
	public double getMinimumAltitude(TargetTrackCalculator tgt1, long t1, long t2) throws AstrometryException {
		return astro.getMinimumAltitude(tgt1, site, t1, t2);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeSinceLastRise(ngat.astrometry.Coordinates, double, long)
	 */
	public long getTimeSinceLastRise(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.getTimeSinceLastRise(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeSinceLastSet(ngat.astrometry.Coordinates, double, long)
	 */
	public long getTimeSinceLastSet(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.getTimeSinceLastSet(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeSinceLastTransit(ngat.astrometry.Coordinates, long)
	 */
	public long getTimeSinceLastTransit(Coordinates coord, long time) throws AstrometryException {
		return astro.getTimeSinceLastTransit(coord, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeUntilNextRise(ngat.astrometry.Coordinates, double, long)
	 */
	public long getTimeUntilNextRise(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.getTimeUntilNextRise(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeUntilNextSet(ngat.astrometry.Coordinates, double, long)
	 */
	public long getTimeUntilNextSet(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.getTimeUntilNextSet(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTimeUntilNextTransit(ngat.astrometry.Coordinates, long)
	 */
	public long getTimeUntilNextTransit(Coordinates coord, long time) throws AstrometryException {
		return astro.getTimeUntilNextTransit(coord, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#getTransitAltitude(ngat.astrometry.Coordinates, long)
	 */
	public double getTransitAltitude(Coordinates coord, long time) throws AstrometryException {
		return astro.getTransitAltitude(coord, site, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#isRisen(ngat.astrometry.Coordinates, double, long)
	 */
	public boolean isRisen(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.isRisen(coord, site, horizon, time);
	}

	/* (non-Javadoc)
	 * @see ngat.astrometry.AstrometrySiteCalculator#isSet(ngat.astrometry.Coordinates, double, long)
	 */
	public boolean isSet(Coordinates coord, double horizon, long time) throws AstrometryException {
		return astro.isSet(coord, site, horizon, time);
	}

	public ISite getSite() throws AstrometryException {
		return site;
	}

	public double getParalacticAngle(Coordinates c1, long time) throws AstrometryException {
		return astro.getParalacticAngle(c1, site, time);
	}

	public double getAngularSeperation(Coordinates a, Coordinates b) throws AstrometryException {
		return astro.getAngularSeperation(a, b);
	}

	public double getClosestPointOfApproach(TargetTrackCalculator tgt1, TargetTrackCalculator tgt2, long t1, long t2)
			throws AstrometryException {
		return astro.getClosestPointOfApproach(tgt1, tgt2, t1, t2);
	}

}
