/**
 * 
 */
package ngat.astrometry.approximate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import ngat.astrometry.AstroLib;
import ngat.astrometry.AstrometryException;
import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XSlaNamedPlanetTarget;

/**
 * @author eng
 * 
 */
public class BasicAstroLibImpl implements AstroLib {

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	/** TAI-UTC correction = no of leapseconds since datum. */
	static final double TAIUTC = 34; // last LS was 2008-12-31

	/**
	 * UT1-UTC correction from USNO Bulletin-A - this is never more than +-1.0
	 * sec.
	 */
	static final double UT1UTC = 0.0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#calcBearing(double, double, double, double)
	 */
	public double calcBearing(double az1, double alt1, double az2, double alt2) {

		double aa = Math.sin(az2 - az1) * Math.cos(alt2);
		double bb = Math.cos(alt1) * Math.sin(alt2) - Math.sin(alt1) * Math.cos(alt2) * Math.cos(alt2 - alt1);

		return Math.atan2(bb, aa);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seengat.astrometry.AstroLib#getCatalogCoordinates(ngat.phase2.
	 * XSlaNamedPlanetTarget, ngat.astrometry.ISite, long)
	 */
	public Coordinates getCatalogCoordinates(XSlaNamedPlanetTarget catalogTarget, ISite site, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getEquationOfEquinoxes(long)
	 */
	public double getEquationOfEquinoxes(long time) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getEtUt(long)
	 */
	public double getEtUt(long time) {
		// we are only using this for "now" so we know the time
		return 32.184 + TAIUTC - UT1UTC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getEtUt2(long)
	 */
	public double getEtUt2(long time) {
		// we are only using this for "now" so we know the time
		return 32.184 + TAIUTC - UT1UTC;

	}

    public double getLST(long time, double longitude) {

	return getLST4(time, longitude);

    }
	
    private double getLST4(long time, double longitude) {

	Calendar vcal = Calendar.getInstance();
	vcal.setTimeZone(UTC);
	vcal.setTime(new Date(time));

	int y = vcal.get(Calendar.YEAR);
	int m = vcal.get(Calendar.MONTH);
	int d = vcal.get(Calendar.DAY_OF_MONTH);

	// before VE of year
	if ((m < 2) || ((m == 2) && (d < 21))) {
	    y -= 1; // use last years VE
	}

	vcal.set(y, 2, 21, 12, 0, 0); // march 21 at 12:00:00 UT
	
	long veqtime = vcal.getTimeInMillis();

	double tdiff = (double)(time - veqtime); // time since vernal equinox
	
	double nd = tdiff/86400000.0; // days since VE

	// sid day = 23 hours, 56 minutes, 4.091s
	double nds = nd*(86400/(86164.091)); // number of sid days since VE
			 
	double sidrot = nds*Math.PI*2.0; // total sidereal rotation angle

	double sidrem = sidrot - 2.0*Math.PI*Math.floor(sidrot/(2.0*Math.PI)); //sid rot remainder

	double lst = sidrem + longitude;
	
	// trim off excess
	lst = lst - 2.0*Math.PI*Math.floor(lst/(2.0*Math.PI));

	return lst;

    }

/*	private double getLST3(long time, double longitude) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(UTC);
		cal.setTime(new Date(time));
		
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		if (m == 1 || m == 2) {
			y-=1;
			m+=12;
		}
	
		int a = (y/100);
		int b = 2-a+(a/4);
		int c = (int)(365.25*(double)y);
		int e = (int)(30.6001*(double)(m-1));
		double jd = b+c+d+e+1720994.5;
		
		double ut = getUT(time)*24.0; // this is in hours
		
		double t = (jd-2451545.0)/36525.0;
		double t0 = 6.697374558 + (2400.051336*t) + (0.000025862*t*t) + (ut*1.0027379093);
		
		double gst = t0 - 24.0*Math.floor(t0/24.0);
		
		System.err.println("GST: "+t0);
		
		// reducing to 24h
		
		double londeg = Math.toDegrees(longitude);
		
		double lst = t0 + londeg/15.0;
		
		System.err.println("LST: "+lst);
		
		return Math.toRadians(lst);
		
	}
*/
/*	*//** Calcualtes the Local Sidereal Time at site.*//*
	private double getLST2(long time, double longitude) {

		Calendar c2000 = Calendar.getInstance();
		c2000.setTimeZone(UTC);
		c2000.set(2000, 0, 1, 12, 0, 0);

		// days since 2000-01-01 at 12:00:00,

		Calendar today = Calendar.getInstance();
		today.setTimeZone(UTC);
		today.setTime(new Date(time));
		
		
		double d = (double) (today.getTimeInMillis() - c2000.getTimeInMillis()) / 86400000.0;
		d += 2451545.0;//2400000.5
		double gmst = 18.697374558 + 24.06570982441908 * d;
		
		today.set(Calendar.HOUR_OF_DAY,0);
		today.set(Calendar.MINUTE,0);
		today.set(Calendar.SECOND,0);
		
		double d0 = (double) (today.getTimeInMillis() - c2000.getTimeInMillis()) / 86400000.0;
		d0 += 2451545.0;//2400000.5
		double gmst0 = 18.697374558 + 24.06570982441908 * d0;
		
		// System.err.println("GMST(H) = "+gmst+" GMSTO = "+gmsth0);
		// gmst in degs
		double gmstd = 15.0 * (gmst-gmst0);
		gmst = Math.toRadians(gmstd);
		
		double lst = gmst+longitude;
		double PI2 = Math.PI * 2.0;
		while (lst > PI2)
			lst -= PI2;
		while (lst < 0.0)
			lst += PI2;
		return lst;
		
	}*/

	private double getLST1(long time, double longitude) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(UTC);
		cal.setTime(new Date(time));
		
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH); // borrowed code already does the m+1 thing?
		int d = cal.get(Calendar.DAY_OF_MONTH);
		
		double ut = getUT(time)*24.0; // hours
		
		double glst = CalcLST(y, m, d, ut, Math.toDegrees(longitude)); // hours
		
		return Math.toRadians(glst*15.0);
	
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getLAST(long, double)
	 */
	public double getLAST(long time, double longitude) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getLunarCoordinates(ngat.astrometry.ISite,
	 * long)
	 */
	public Coordinates getLunarCoordinates(ISite site, long time) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getMJD(long)
	 */
	public double getMJD(long time) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getSolarCoordinates(long)
	 */
	public Coordinates getSolarCoordinates(long t) {
		Calendar cal = Calendar.getInstance();
		// cal.setTimeInMillis(t);
		cal.setTime(new Date(t));
		cal.setTimeZone(UTC);

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);

		double hh = (double) cal.get(Calendar.HOUR_OF_DAY);
		double mm = (double) cal.get(Calendar.MINUTE);
		double ss = (double) cal.get(Calendar.SECOND);

		// System.err.printf("Date: %tF %tT: Y(%4d) M(%2d) D(%2d) at %2d:%2d:%2d\n",
		// t,t,y,m,d,(int)hh,(int)mm,(int)ss);

		double ut = (hh + mm / 60.0 + ss / 3600.0);

		// System.err.printf("UT(h) at %tT %4.2f \n", t, ut);

		Calendar d21 = Calendar.getInstance();
		d21.setTimeZone(UTC);
		d21.set(Calendar.YEAR, y);
		d21.set(Calendar.MONTH, 2);
		d21.set(Calendar.DATE, 21);
		d21.set(Calendar.HOUR_OF_DAY, 12);
		d21.set(Calendar.MINUTE, 0);
		d21.set(Calendar.SECOND, 0);
		long t21 = d21.getTimeInMillis();

		double dd = (double) (t - t21) / 86400000.0;

		// System.err.printf("Days since 21m: %6.2f \n",dd);

		double lamd = dd * 360.0 / 365.0; // lambda degs

		double rad = lamd + 2.458 * Math.sin(2.0 * Math.toRadians(lamd));
		while (rad > 360.0)
			rad -= 360.0;
		while (rad < 0.0)
			rad += 360.0;

		double ra = Math.toRadians(rad);

		double decd = 23.5 * Math.sin(Math.toRadians(lamd));
		double dec = Math.toRadians(decd);

		// System.err.printf("RA: %4.2f Dec: %4.2f \n", rad, decd);

		return new Coordinates(ra, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.astrometry.AstroLib#getUT(long)
	 */
	public double getUT(long time) {
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
		calendar.setTimeZone(UTC);
		calendar.setTime(new Date(time));

		return (calendar.get(Calendar.HOUR_OF_DAY) * 3600000.0 + calendar.get(Calendar.MINUTE) * 60000.0
				+ calendar.get(Calendar.SECOND) * 1000.0 + calendar.get(Calendar.MILLISECOND)) / 86400000.0;

	}

	// ut is hours others are ints
	private double CalcJD(int ny, int nm, int nd, double ut)
	{
	 double A, B, C, D, jd, day;

	 day = nd + ut / 24.0;
	 if ((nm == 1) || (nm == 2)) {
	   ny = ny - 1;
	   nm = nm + 12;
	 }

	 if (((double) ny + nm / 12.0 + day / 365.25) >=
	   (1582.0 + 10.0 / 12.0 + 15.0 / 365.25))
	 {
	   A = ((int) (ny / 100.0));
	   B = 2.0 - A + (int) (A / 4.0);
	 }
	 else
	 {
	   B = 0.0;
	 }

	 if (ny < 0.0)
	 {
	   C = (int) ((365.25 * (double) ny) - 0.75);
	 }
	   else
	 {
	   C = (int) (365.25 * (double) ny);
	 }

	 D = (int) (30.6001 * (double) (nm + 1));
	 jd = B + C + D + day + 1720994.5;
	 return (jd);
	}

	
	private double Map24(double hour)
	{
	 int n;
	 
	 if (hour < 0.0)
	 {
	    n = (int) (hour / 24.0) - 1;
	    return (hour - n * 24.0);
	 }
	 else if (hour > 24.0)
	 {
	    n = (int) (hour / 24.0);
	    return (hour - n * 24.0);
	 }
	 else
	 {
	    return (hour);
	 }
	}
	
	
	/*  Compute Greenwich Mean Sidereal Time (gmst) */
	/*  TU is number of Julian centuries since 2000 January 1.5 */
	/*  Expression for gmst from the Astronomical Almanac Supplement */

	private double CalcLST(int year, int month, int day, double ut, double glongdeg)
	{
	 double TU, TU2, TU3, T0;
	 double gmst,lmst;

	 TU = (CalcJD(year, month, day, 0.0) - 2451545.0) / 36525.0;
	 TU2 = TU * TU;
	 TU3 = TU2 * TU;
	 T0 =
	     (24110.54841 / 3600.0) +
	     8640184.812866 / 3600.0 * TU + 0.093104 / 3600.0 * TU2 -
	     6.2e-6 / 3600.0 * TU3;
	 //T0 = Map24(T0);

	 gmst = Map24(T0 + ut * 1.002737909);
	 lmst = 24.0 * frac((gmst - glongdeg / 15.0) / 24.0);
	 return (lmst);
	}
	/* Fractional part */

	private double frac(double x)
	{
	 x -= (int) x;
	 return ((x < 0) ? x + 1.0 : x);
	}
	

	
	
}
