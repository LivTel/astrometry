package ngat.astrometry;

import ngat.phase2.XSkyBrightnessConstraint;

public class SkyBrightnessCalculator {

	/** Polynomial #1 parameters for sjy-brightness criterion DARK. */
	static final double[] l_dark = new double[] { 7.74897, -20.5731, 10.1331 };

	/** Polynomial #1 parameters for sjy-brightness criterion GREY1. */
	static final double[] l_grey1 = new double[] { 25.1250, -50.7912, 25.0327 };

	/** Polynomial #1 parameters for sjy-brightness criterion GREY2. */
	static final double[] l_grey2 = new double[] { 84.3193, -160.531, 78.3882 };

	/** Polynomial #1 parameters for sjy-brightness criterion BRIGHTISH. */
	static final double[] l_brightish = new double[] { 499.314, -946.873, 455.339 };

	/** Polynomial #2 parameters for sjy-brightness criterion GREY1. */
	static final double[] h_grey1 = { 113.903, -284.499 };

	/** Polynomial #2 parameters for sjy-brightness criterion GREY2. */
	static final double[] h_grey2 = { 207.315, -296.539 };

	/** Polynomial #2 parameters for sjy-brightness criterion BRIGHTISH. */
	static final double[] h_brightish = { 282.491, -298.458 };

	/** Site for which this calculator is intended. */
	private ISite site;

	private LunarCalculator moonTrack;

	private SolarCalculator sunTrack;

	private AstrometrySiteCalculator astro;

	/**
	 * @param site
	 */
	public SkyBrightnessCalculator(ISite site) {
		super();
		this.site = site;
		astro = new BasicAstrometrySiteCalculator(site);
		moonTrack = new LunarCalculator(site);
		sunTrack = new SolarCalculator();
	}

	// is it grey1 at mysite on 4th june at 1234UT
	// isGrey1(ISite site, long time) - removes any need to calculate frac and
	// lunar elev

	/**
	 * Determine if the supplied moon parameters satisfy the sky-brightness
	 * criterion DARK.
	 * 
	 * @param fraction
	 *            The lunar illumination fraction.
	 * @param altitude
	 *            The altitude of the moon (radians).
	 * @return
	 */
	public boolean isDark(double fraction, double altitude) {

		double max = poly1(l_dark, fraction);
		return (altitude < max);
	}

	/**
	 * Determine if the supplied moon parameters satisfy the sky-brightness
	 * criterion GREY1.
	 * 
	 * @param fraction
	 *            The lunar illumination fraction.
	 * @param altitude
	 *            The altitude of the moon (radians).
	 * @return
	 */
	public boolean isGrey1(double fraction, double altitude) {

		double max1 = poly1(l_grey1, fraction);
		double max2 = poly2(h_grey1, fraction);
		double max = Math.max(max1, max2);
		return (altitude < max);

	}

	/**
	 * Determine if the supplied moon parameters satisfy the sky-brightness
	 * criterion GREY2.
	 * 
	 * @param fraction
	 *            The lunar illumination fraction.
	 * @param altitude
	 *            The altitude of the moon (radians).
	 * @return
	 */
	public boolean isGrey2(double fraction, double altitude) {

		double max1 = poly1(l_grey2, fraction);
		double max2 = poly2(h_grey2, fraction);
		double max = Math.max(max1, max2);
		return (altitude < max);

	}

	/**
	 * Determine if the supplied moon parameters satisfy the sky-brightness
	 * criterion BRIGHTISH.
	 * 
	 * @param fraction
	 *            The lunar illumination fraction.
	 * @param altitude
	 *            The altitude of the moon (radians).
	 * @return
	 */
	public boolean isBrightish(double fraction, double altitude) {

		double max1 = poly1(l_brightish, fraction);
		double max2 = poly2(h_brightish, fraction);
		double max = Math.max(max1, max2);
		return (altitude < max);

	}

	// public int getLunarSkyBrightnessCriterion(frac, alt): return SKYB_GREY1
	// etc

	public int getSkyBrightnessCriterion(TargetTrackCalculator target, long time) throws AstrometryException {

		// work out target position at time.
		Coordinates c = target.getCoordinates(time);

		// work out the sun position at time at site.
		Coordinates sun = sunTrack.getCoordinates(time);
		double sunAlt = astro.getAltitude(sun, time);

		// work out the moon position and phase at time at site.
		Coordinates moon = moonTrack.getCoordinates(time);
		double moonAlt = astro.getAltitude(moon, time);

		double moonSun = astro.getAngularSeperation(moon, sun);

		double moonFrac = 0.5 * (1.0 + Math.cos(Math.PI - moonSun));

		// work out lunar-target seperation
		double targetLunar = astro.getAngularSeperation(moon, c);

		// finally combine to determine criterion
		if (sunAlt < Math.toRadians(-16.0) && isDark(moonFrac, moonAlt))
			return XSkyBrightnessConstraint.DARK;
		else if (sunAlt < Math.toRadians(-16.0)
				&& ((isGrey1(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0)) || (isDark(moonFrac, moonAlt))))
			return XSkyBrightnessConstraint.MAG_0P75;
		else if (sunAlt < Math.toRadians(-16.0)
				&& ((isGrey1(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0)) || (isGrey2(moonFrac, moonAlt))))
			return XSkyBrightnessConstraint.MAG_1P5;
		else if (sunAlt < Math.toRadians(-12.0)
				&& ((isBrightish(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0))
						|| (isGrey2(moonFrac, moonAlt) && targetLunar > Math.toRadians(20.0)) || (isGrey1(moonFrac,
							moonAlt))))
			return XSkyBrightnessConstraint.MAG_2;
		else if (sunAlt < Math.toRadians(-12.0)
				&& ((targetLunar > Math.toRadians(30.0)) || (isBrightish(moonFrac, moonAlt) && targetLunar > Math
						.toRadians(15.0))))
			return XSkyBrightnessConstraint.MAG_4;
		else if (sunAlt < Math.toRadians(-8.0) && (targetLunar > Math.toRadians(10.0)))
			return XSkyBrightnessConstraint.MAG_6;
		else if (sunAlt < Math.toRadians(-4.0))
			return XSkyBrightnessConstraint.MAG_10;

		return XSkyBrightnessConstraint.DAYTIME;
	}


    public int getSkyBrightnessCriterion(double alt, double az, long time) throws AstrometryException {

	// work out the sun position at time at site.
	Coordinates sun = sunTrack.getCoordinates(time);
	double sunAlt = astro.getAltitude(sun, time);

	// work out the moon position and phase at time at site.
	Coordinates moon = moonTrack.getCoordinates(time);
	double moonAlt = astro.getAltitude(moon, time);

	double moonSun = astro.getAngularSeperation(moon, sun);

	double moonFrac = 0.5 * (1.0 + Math.cos(Math.PI - moonSun));

	// work out lunar-target seperation, convert target and lunar altaz into sphericals
	Coordinates ctarget = new Coordinates(az, alt);
	Coordinates cmoon   = new Coordinates(astro.getAzimuth(moon, time), astro.getAltitude(moon, time));
	double targetLunar = astro.getAngularSeperation(cmoon, ctarget);

	// finally combine to determine criterion
	if (sunAlt < Math.toRadians(-16.0) && isDark(moonFrac, moonAlt))
	    return XSkyBrightnessConstraint.DARK;
	else if (sunAlt < Math.toRadians(-16.0)
		 && ((isGrey1(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0)) || (isDark(moonFrac, moonAlt))))
	    return XSkyBrightnessConstraint.MAG_0P75;
	else if (sunAlt < Math.toRadians(-16.0)
		 && ((isGrey1(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0)) || (isGrey2(moonFrac, moonAlt))))
	    return XSkyBrightnessConstraint.MAG_1P5;
	else if (sunAlt < Math.toRadians(-12.0)
		 && ((isBrightish(moonFrac, moonAlt) && targetLunar > Math.toRadians(30.0))
		     || (isGrey2(moonFrac, moonAlt) && targetLunar > Math.toRadians(20.0)) || (isGrey1(moonFrac,
												       moonAlt))))
	    return XSkyBrightnessConstraint.MAG_2;
	else if (sunAlt < Math.toRadians(-12.0)
		 && ((targetLunar > Math.toRadians(30.0)) || (isBrightish(moonFrac, moonAlt) && targetLunar > Math
							      .toRadians(15.0))))
	    return XSkyBrightnessConstraint.MAG_4;
	else if (sunAlt < Math.toRadians(-8.0) && (targetLunar > Math.toRadians(10.0)))
	    return XSkyBrightnessConstraint.MAG_6;
	else if (sunAlt < Math.toRadians(-4.0))
	    return XSkyBrightnessConstraint.MAG_10;

	return XSkyBrightnessConstraint.DAYTIME;
    }


	public static double getSkyBrightness(int skyb) {
		switch (skyb) {
		case XSkyBrightnessConstraint.DARK:
			return 0.0;
		case XSkyBrightnessConstraint.MAG_0P75:
			return 0.75;
		case XSkyBrightnessConstraint.MAG_1P5:
			return 1.5;
		case XSkyBrightnessConstraint.MAG_2:
			return 2.0;
		case XSkyBrightnessConstraint.MAG_4:
			return 4.0;
		case XSkyBrightnessConstraint.MAG_6:
			return 6.0;
		case XSkyBrightnessConstraint.MAG_10:
			return 10.0;
		default:
			return 50.0; // fudge we dont really know...
		}
	}

	public static int getSkyBrightnessCategory(double sky) {

		if (sky > 10.0)
			return XSkyBrightnessConstraint.DAYTIME;
		else if (sky > 6.0)
			return XSkyBrightnessConstraint.MAG_10;
		else if (sky > 4.0)
			return XSkyBrightnessConstraint.MAG_6;

		else if (sky > 2.0)
			return XSkyBrightnessConstraint.MAG_4;
		else if (sky > 1.5)

			return XSkyBrightnessConstraint.MAG_2;
		else if (sky > 0.75)
			return XSkyBrightnessConstraint.MAG_1P5;
		else if (sky > 0.3)
			return XSkyBrightnessConstraint.MAG_0P75;
		else
			return XSkyBrightnessConstraint.DARK;

	}

	public static String getSkyBrightnessCategoryName(int skyb) {
		switch (skyb) {
		case XSkyBrightnessConstraint.DARK:
			return "DARK";
		case XSkyBrightnessConstraint.MAG_0P75:
			return "0.75 MAG";
		case XSkyBrightnessConstraint.MAG_1P5:
			return "1.5 MAG";
		case XSkyBrightnessConstraint.MAG_2:
			return "2.0 MAG";
		case XSkyBrightnessConstraint.MAG_4:
			return "4.0 MAG";
		case XSkyBrightnessConstraint.MAG_6:
			return "6.0 MAG";
		case XSkyBrightnessConstraint.MAG_10:
			return "10 MAG";
		default:
			return "DAYTIME";
		}
	}

	/**
	 * Calculate value of polynomial(1) in sky-brightness calculation.
	 * 
	 * @param l
	 *            The polynomial parameters.
	 * @param fraction
	 *            Lunar illumination fraction.
	 * @return
	 */
	private double poly1(double[] l, double fraction) {
		return Math.toRadians(l[0] + l[1] * fraction + l[2] * fraction * fraction);
	}

	/**
	 * Calculate value of polynomial(2) in sky-brightness calculation.
	 * 
	 * @param h
	 *            The polynomial parameters.
	 * @param fraction
	 *            Lunar illumination fraction.
	 * @return
	 */
	private double poly2(double[] h, double fraction) {
		return Math.toRadians(h[0] + h[1] * fraction);
	}

}
