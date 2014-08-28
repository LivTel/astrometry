package ngat.astrometry;

import ngat.phase2.XSlaNamedPlanetTarget;

public interface AstroLib {

	/**
	 * Wrapper method. Calculates the ET-UT difference for epoch. WARNING This
	 * needs slalib to be kept up2date with leap-seconds.
	 * 
	 * @param time
	 *            The time (millis since 1970). return ET-UT (secs).
	 */
	public double getEtUt(long time);

	/**
	 * Local method for determining ET-UT using coded values - avoids updating
	 * slalib each LS.
	 * */
	public double getEtUt2(long time);

	/**
	 * Wrapper method. Calculates the MJD at 00.00h UT of the date specified by
	 * time.
	 * 
	 * @param time
	 *            The time (millis since 1970).
	 * @return The MJD at 00.00h UT of the date specified by time.
	 */
	public double getMJD(long time);

	/**
	 * Wrapper method. Calculates the UT day fraction at time, on the date
	 * specified.
	 * 
	 * @param time
	 *            The time (millis since 1970).
	 * @return The UT day fraction at time.
	 */
	public double getUT(long time);

	/**
	 * Wrapper method. Calculates the local sidereal time.
	 * 
	 * @param time
	 *            The time (millis since 1970).
	 * @param longitude
	 *            The observer's longitude (East = +ve).
	 * @return The LST at the observer's longitude at time.
	 */
	public double getLST(long time, double longitude);

	/**
	 * Wrapper method. Works out the Equation of the Equinoxes at specified
	 * time.
	 * 
	 * @param time
	 *            The date/time of required calculation.
	 * @return The Equation of the Equinoxes in radians.
	 */
	public double getEquationOfEquinoxes(long time);

	/**
	 * Wrapper method. Calculates the Local Apparent Sidereal Time.
	 * 
	 * @param time
	 *            The time (millis since 1970).
	 * @param longitude
	 *            The observer's longitude (East = +ve).
	 * @return The LAST at the observer's longitude at time.
	 */
	public double getLAST(long time, double longitude);

	/** Calculates the position angle between 2 points on a sphere. */
	public double calcBearing(double az1, double alt1, double az2, double alt2);

	/**
	 * Wrapper method. Calculates the geocentric equatorial coordinates of the
	 * Moon.
	 * 
	 * @param time
	 *            The date/time of observation.
	 * @return The geocentric equatorial coordinates of the Moon as an
	 *         ngat.astrometry.Position.
	 */
	public Coordinates getLunarCoordinates(ISite site, long time);

	/**
	 * Wrapper method. Calculates the geocentric equatorial coordinates of the
	 * Sun.
	 * 
	 * @param time
	 *            The date/time of observation.
	 * @return The geocentric equatorial coordinates of the Sun as an
	 *         ngat.astrometry.Position.
	 */
	public Coordinates getSolarCoordinates(long time);

	public Coordinates getCatalogCoordinates(XSlaNamedPlanetTarget catalogTarget, ISite site, long time);

}