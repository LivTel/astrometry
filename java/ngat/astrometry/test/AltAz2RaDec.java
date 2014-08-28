/**
 * 
 */
package ngat.astrometry.test;

import javax.swing.JSlider;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometryException;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.JAstroSlalib;

/**
 * @author eng
 * 
 */
public class AltAz2RaDec {

	private double lat;

	private double lon;

	public AltAz2RaDec(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public Coordinates compute(long time, double alt, double azm) throws AstrometryException {

		// double sindec = Math.sin(lat) * Math.sin(alt) + Math.cos(lat) *
		// Math.cos(alt) * Math.cos(azm);
		// double dec = Math.asin(sindec);
		// double cosha = (Math.sin(alt) - Math.sin(dec)*
		// Math.sin(lat))/(Math.cos(dec)*Math.cos(lat));

		// double ha = Math.atan2(Math.cos(alt)*Math.sin(azm),
		// Math.sin(alt)*Math.cos(lat)+Math.cos(alt)*Math.cos(azm)*Math.sin(lat));

		// double ha = Math.acos(cosha);
		// double lst = (new JAstroSlalib()).getInstance().getLST(time, lon);
		// double ra = lst - ha;
		// return new Coordinates(ra, dec);

		double sa = Math.sin(azm);
		double ca = Math.cos(azm);
		double se = Math.sin(alt);
		double ce = Math.cos(alt);
		double sp = Math.sin(lat);
		double cp = Math.cos(lat);

		/* HA,Dec as x,y,z */
		double x = -ca * ce * sp + se * cp;
		double y = -sa * ce;
		double z = ca * ce * cp + se * sp;

		/* To spherical */
		double r = Math.sqrt(x * x + y * y);
		double ha = (r == 0.0) ? 0.0 : Math.atan2(y, x);
		double dec = Math.atan2(z, r);

		double lst = (new JAstroSlalib()).getInstance().getLST(time, lon);
		double ra = lst - ha;
		return new Coordinates(ra, dec);

	}

}
