package ngat.astrometry;

import ngat.astrometry.approximate.BasicAstroLibImpl;
import ngat.phase2.*;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

public class BasicCardinalPointingCalculator implements CardinalPointingCalculator {

	/**
	 * Add to limits as a buffer against uncertain sky->mount conversion near
	 * limits (degs).
	 */
	static final double ROTATOR_LIMIT_BUFFER = 4.0;

    static final double ROTATOR_NEGATIVE_LIMIT = -90.0;

    static final double ROTATOR_POSITIVE_LIMIT = 90.0;


	/**
	 * The value of the rotator correction to get RATCAM mount angle at SKY 0 on
	 * meridian.
	 */
	// public static final double IOFF = Math.toRadians(70.0);

	private ISite site;

	private AstrometrySiteCalculator astro;

	private TargetTrackCalculatorFactory trackFactory;

	private LogGenerator logger;

	public BasicCardinalPointingCalculator(ISite site) {
		this(site, new BasicAstrometrySiteCalculator(site), new BasicTargetTrackCalculatorFactory());
	}

	/**
	 * @param site
	 * @param astro
	 */
	public BasicCardinalPointingCalculator(ISite site, AstrometrySiteCalculator astro,
			TargetTrackCalculatorFactory trackFactory) {
		this.site = site;
		this.astro = astro;
		this.trackFactory = trackFactory;
		Logger alogger = LogManager.getLogger("ASTRO");
		logger = alogger.generate().system("astro").subSystem("calc").srcCompClass("CPCalc").srcCompId(
				this.getClass().getSimpleName());
	}

	/**
	 * Returns true if the nominated cardinal pointing sky angle is feasible for
	 * the given target and instrument offset for the period t1 thro t2.
	 */
	public boolean isFeasibleSkyAngle(double skyAngle, ITarget target, double instrumentOffset, long t1, long t2)
			throws AstrometryException {

		logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
				String.format("Checking sky angle: %4.2f for instOffset: %4.2f", Math.toDegrees(skyAngle), Math
						.toDegrees(instrumentOffset))).send();

		TargetTrackCalculator track = trackFactory.getTrackCalculator(target, site);

		// work out target sky position at t1
		Coordinates c1 = track.getCoordinates(t1);
		double azm1 = astro.getAzimuth(c1, t1);
		double alt1 = astro.getAltitude(c1, t1);

		// work out target sky position at t2
		Coordinates c2 = track.getCoordinates(t2);
		double azm2 = astro.getAzimuth(c2, t2);
		double alt2 = astro.getAltitude(c2, t2);

		// NOTE mount angle is always returned in [-180, 180]
		double p1 = getMountAngle(skyAngle, target, instrumentOffset, t1);
		double p2 = getMountAngle(skyAngle, target, instrumentOffset, t2);

		double maxDtl = -999; // detect maximum of dist-to-limit

		boolean p1ok = false;
		// Check start mount angle p1 is in good zone
		if (ROTATOR_NEGATIVE_LIMIT + ROTATOR_LIMIT_BUFFER < Math.toDegrees(p1) && Math.toDegrees(p1) < ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER)
			p1ok = true;
		logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
				String.format("Start angle: %4.2f is %s", Math.toDegrees(p1), (p1ok ? "pass" : "fail"))).send();

		boolean p2ok = false;
		// Check final mount angle p2 is in good zone
		if (ROTATOR_NEGATIVE_LIMIT + ROTATOR_LIMIT_BUFFER < Math.toDegrees(p2) && Math.toDegrees(p2) < ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER)
			p2ok = true;
		logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
				String.format("Final angle: %4.2f is %s", Math.toDegrees(p2), (p2ok ? "pass" : "fail"))).send();

		if ((p1ok && p2ok)) {

			// work out which end point is nearest to a limit
			double dd = 0.0;
			
			// cunning method to work out dec zone
			long tt = t1 + astro.getTimeUntilNextTransit(c1, t1);
			double aztrans = astro.getAzimuth(c1, tt);

			if (0.5 * Math.PI <= aztrans && aztrans <= 1.5 * Math.PI) {
				// below zenith 
				//dd = Math.abs(90 - ROTATOR_LIMIT_BUFFER - Math.toDegrees(p2));
				logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
						String.format("Target is south, rotator increasing? to +90, dtg: %4.2f", dd)).send();
			} else {
				// above zenith rotator angle decreasing (towards -90)
				//dd = Math.abs(Math.toDegrees(p2) + 90 - ROTATOR_LIMIT_BUFFER);
				logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
						String.format("Target is north, rotator decreasing? to -90, dtg: %4.2f", dd)).send();
			}

			if (p1 < p2) {
				logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
						String.format("Rotator at start: %4.2f, end: %4.2f, Moving towards +90, DTG: %4.2f", Math
								.toDegrees(p1), Math.toDegrees(p2), Math.toDegrees(dd))).send();
				dd = Math.abs(ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER - Math.toDegrees(p2));
			} else {
				logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
						String.format("Rotator at start: %4.2f, end: %4.2f, Moving towards -90, DTG: %4.2f", Math
								.toDegrees(p1), Math.toDegrees(p2), Math.toDegrees(dd))).send();
				dd = Math.abs(Math.toDegrees(p2) - ROTATOR_NEGATIVE_LIMIT - ROTATOR_LIMIT_BUFFER);
			}
						
			if (dd > 0.0)
				return true;

		} else {
			// System.err.println("CP routine: SKY " +
			// Position.toDegrees(skyAngle, 2) + " is NOT usable");

			return false;
		}

		return false;

	}

	public double getBestCardinalAngle(ITarget target, double instrumentOffset, long t1, long t2)
			throws AstrometryException {

		TargetTrackCalculator track = trackFactory.getTrackCalculator(target, site);

		// work out target sky position at t1
		Coordinates c1 = track.getCoordinates(t1);
		double azm1 = astro.getAzimuth(c1, t1);
		double alt1 = astro.getAltitude(c1, t1);

		// work out target sky position at t2
		Coordinates c2 = track.getCoordinates(t2);
		double azm2 = astro.getAzimuth(c2, t2);
		double alt2 = astro.getAltitude(c2, t2);

		/*
		 * System.err.printf("BCC: Instrument offset %4.2f \n",
		 * Math.toDegrees(instrumentOffset));
		 * 
		 * System.err.printf("BCC: (Azm, Alt) -> Start: %tT (%4.2f, %4.2f) \n",
		 * t1, Math.toDegrees(azm1), Math.toDegrees(alt1));
		 * System.err.printf("BCC: (Azm, Alt) -> End:   %tT (%4.2f, %4.2f) \n",
		 * t2, Math.toDegrees(azm2), Math.toDegrees(alt2));
		 */

		double maxDtl = -999; // detect maximum Dist-to-limit
		int cv = 0;
		double sel = -1.0;

		for (int isky = 0; isky < 4; isky++) {

			double sky = Math.toRadians((double) isky * 90.0);

			logger.create().info().level(3).block("getBestCardinal").msg(
					String.format("Check cardinal angle: %4.2f", Math.toDegrees(sky))).send();

			// calculate mount angle at start and end, mount angle is always in (-180, +180)
			double p1 = getMountAngle(sky, target, instrumentOffset, t1);
			double p2 = getMountAngle(sky, target, instrumentOffset, t2);

			boolean pc1ok = false; // start mount angle
			if (ROTATOR_NEGATIVE_LIMIT + ROTATOR_LIMIT_BUFFER < Math.toDegrees(p1) && Math.toDegrees(p1) < ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER)
				pc1ok = true;
			logger.create().info().level(3).block("getBestCardinal").msg(
					String.format("Start angle: %4.2f is %s", Math.toDegrees(p1), (pc1ok ? "pass" : "fail"))).send();

			boolean pc2ok = false; // end mount angle
			if (ROTATOR_NEGATIVE_LIMIT + ROTATOR_LIMIT_BUFFER < Math.toDegrees(p2) && Math.toDegrees(p2) < ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER)
				pc2ok = true;
			logger.create().info().level(3).block("getBestCardinal").msg(
					String.format("End angle: %4.2f is %s", Math.toDegrees(p2), (pc2ok ? "pass" : "fail"))).send();

			// both angles inside good region ?
			if ((pc1ok && pc2ok)) {

				cv++; // count as valid angle
				
				// cunning method to work out dec zone
				long tt = t1 + astro.getTimeUntilNextTransit(c1, t1);
				double aztrans = astro.getAzimuth(c1, tt);

				// work out which end point is nearest to a limit
				double dd = 0.0;
				if (0.5 * Math.PI <= aztrans && aztrans <= 1.5 * Math.PI) {
					// below zenith 
					//dd = Math.abs(90 - ROTATOR_LIMIT_BUFFER - Math.toDegrees(p2));
					logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
							String.format("Target is south, rotator increasing? to +90, dtg: %4.2f", dd)).send();
				} else {
					// above zenith rotator angle decreasing (towards -90)
					//dd = Math.abs(Math.toDegrees(p2) + 90 - ROTATOR_LIMIT_BUFFER);
					logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
							String.format("Target is north, rotator decreasing? to -90, dtg: %4.2f", dd)).send();
				}

				if (p1 < p2) {
					logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
							String.format("Rotator at start: %4.2f, end: %4.2f, Moving towards +90", Math
									.toDegrees(p1), Math.toDegrees(p2))).send();
					dd = Math.abs(ROTATOR_POSITIVE_LIMIT - ROTATOR_LIMIT_BUFFER - Math.toDegrees(p2));
				} else {
					logger.create().info().level(3).block("isFeasibleSkyAngle").msg(
							String.format("Rotator at start: %4.2f, end: %4.2f, Moving towards -90", Math
									.toDegrees(p1), Math.toDegrees(p2))).send();
					dd = Math.abs(Math.toDegrees(p2) - ROTATOR_NEGATIVE_LIMIT - ROTATOR_LIMIT_BUFFER);
				}
							
				if (dd > maxDtl) {
					sel = sky;
					maxDtl = dd;
				}
			}

		} // next angle

		if (cv == 0) {
			throw new AstrometryException(String.format("No valid cardinal solutions for %s ", (target != null ? target
					.getName() : "null")));
		}

		return sel;

	}

	public double getMountAngle(double skyAngle, ITarget target, double instrumentOffset, long time)
			throws AstrometryException {

		// TODO this uses a version of TTC which is not astrolib dependant
		// - and thus wont work on external machines
		TargetTrackCalculator track = trackFactory.getTrackCalculator(target, site);

		// work out target sky position at t
		Coordinates c = track.getCoordinates(time);
		double azm = astro.getAzimuth(c, time);
		double alt = astro.getAltitude(c, time);

		double latitude = site.getLatitude();

		// parallactic angle
		double p = astro.getParalacticAngle(c, time);

		// add instrument rotation correction and sky PA
		// NOTE this may be negative of rjs parallactic angle but we add it, he
		// subtracts it
		// NOTE this may no longer be true since shifting to slapa, may now need
		// to subtract also ?
		// double mountAngle = skyAngle + instrumentOffset + p;
		double mountAngle = skyAngle + instrumentOffset - p;

		// see if we need to bounce it upwards or downwards
		while (Math.toDegrees(mountAngle) < -180.0) {
			mountAngle += 2.0 * Math.PI;
		}

		while (Math.toDegrees(mountAngle) > 180.0) {
			mountAngle -= 2.0 * Math.PI;
		}

		logger.create().info().level(3).block("getMountAngle").msg(
				String.format(
						"For sky: %4.2f using offset: %4.2f at Azm: %4.2f, Alt: %4.2f -> Para: %4.2f Mount: %4.2f\n",
						Math.toDegrees(skyAngle), Math.toDegrees(instrumentOffset), Math.toDegrees(azm), Math
								.toDegrees(alt), Math.toDegrees(p), Math.toDegrees(mountAngle))).send();

		return mountAngle;

	}

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
			throws AstrometryException {

		TargetTrackCalculator track = trackFactory.getTrackCalculator(target, site);

		// work out target sky position at t
		Coordinates c = track.getCoordinates(time);
		double azm = astro.getAzimuth(c, time);
		double alt = astro.getAltitude(c, time);

		double latitude = site.getLatitude();

		// parallactic angle
		double p = astro.getParalacticAngle(c, time);

		// add instrument rotation correction and sky PA
		// double skyAngle = mountAngle - p - instrumentOffset;
		// since slappa may need to add para rather than subtract
		double skyAngle = mountAngle + p - instrumentOffset;

		// see if we need to bounce it upwards or downwards
		while (Math.toDegrees(skyAngle) < -180.0) {
			skyAngle += 2.0 * Math.PI;
		}

		while (Math.toDegrees(skyAngle) > 180.0) {
			skyAngle -= 2.0 * Math.PI;
		}

		logger.create().info().level(3).block("getMountAngle").msg(
				String.format(
						"For mount: %4.2f using offset: %4.2f at Azm: %4.2f, Alt: %4.2f -> Para: %4.2f Sky:%4.2f\n",
						Math.toDegrees(mountAngle), Math.toDegrees(instrumentOffset), Math.toDegrees(azm), Math
								.toDegrees(alt), Math.toDegrees(p), Math.toDegrees(skyAngle))).send();

		return skyAngle;
	}

}
