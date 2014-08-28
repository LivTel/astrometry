package ngat.astrometry;

import ngat.phase2.*;

public interface CardinalPointingCalculator {

	public boolean isFeasibleSkyAngle(double skyAngle, ITarget target, double instrumentOffset, long t1, long t2)
			throws AstrometryException;

	// public double getBestCardinalAngle(ITarget target,
	// double instrumentOffset,
	// long t1,
	// long t2);

	/**
	 * @param target The target being observed.
	 * @param instrumentOffset Instrument offset.
	 * @param t1 Start time of observation.
	 * @param t2 End time of observaiton.
	 * @return The best cardianl angle for this instrument, target, time.
	 * @throws AstrometryException
	 */
	public double getBestCardinalAngle(ITarget target, double instrumentOffset, long t1, long t2)
	throws AstrometryException;
	
	/**
	 * Calculate the mount angle for a given sky angle.
	 * 
	 * @param skyAngle
	 *            The sky angle.
	 * @param target
	 *            The target being observed.
	 * @param instrumentOffset
	 *            Instrument offset.
	 * @param time
	 *            When the observation is to take place.
	 * @return Mountangle for the supplied sky angle.
	 * @throws AstrometryException
	 */
	public double getMountAngle(double skyAngle, ITarget target, double instrumentOffset, long time)
			throws AstrometryException;

	/**
	 * Calculate the sky angle for a given mount angle.
	 * 
	 * @param mountAngle
	 *            The mount angle.
	 * @param target
	 *            The target being observed.
	 * @param instrumentOffset
	 *            Instrument offset.
	 * @param time
	 *            When the observation is to take place.
	 * @return Skyangle for the supplied mount angle.
	 * @throws AstrometryException
	 */
	public double getSkyAngle(double mountAngle, ITarget target, double instrumentOffset, long time)
			throws AstrometryException;
}
