package ngat.astrometry;

import java.util.*;

import ngat.phase2.XSlaNamedPlanetTarget;

/**
 * A set of native method signatures and some constants for astrometric calculations.
 * The values of TAI-UTC and UT1-UTC are issued weekly by USNO:
 * To receive this information electronically, contact:                  
 *   ser7@maia.usno.navy.mil or use                                     
 *   http://maia.usno.navy.mil/docrequest.html             
 */
public class JAstroSlalib implements AstroLib {

    /*
      2 July 2015                                        Vol. XXVIII No. 027   
      ______________________________________________________________________   
      GENERAL INFORMATION:                                                     
         To receive this information electronically, contact:                  
            ser7@maia.usno.navy.mil or use                                     
            <http://maia.usno.navy.mil/docrequest.html>                        
         MJD = Julian Date - 2 400 000.5 days                                  
         UT2-UT1 = 0.022 sin(2*pi*T) - 0.012 cos(2*pi*T)                       
                                     - 0.006 sin(4*pi*T) + 0.007 cos(4*pi*T)   
            where pi = 3.14159265... and T is the date in Besselian years.     
         TT = TAI + 32.184 seconds                                             
         DUT1= (UT1-UTC) transmitted with time signals                         
             =  +0.3 seconds beginning 01 Jul 2015 at 0000 UTC                 
         Beginning 1 July 2015:                                                
            TAI-UTC = 36.000 000 seconds                                       
     */

    /** The UTC timezone. */
    static final TimeZone UTC = new SimpleTimeZone(0, "UTC");
       
    /*
     * TAI-UTC correction = no of leapseconds since datum.
     * Beginning 1 July 2012:                                                
     * TAI-UTC = 35.000 000 seconds 
     * Beginning 1 July 2015:                                                
     * TAI-UTC = 36.000 000 seconds 
     * Beginning 1 Jan 2016:                                                
     * TAI-UTC = 37.000 000 seconds 
     */              
    static final double TAIUTC = 37.0;

    /*
     *  DUT1= (UT1-UTC) transmitted with time signals                         
     *   = -0.5 seconds beginning 25 Dec 2014 at 0000 UTC 
     *   = -0.6 seconds beginning 19 Mar 2015 at 0000 UTC
     *   = +0.3 seconds beginning 1 Jul 2015 at 0000 UTC
     *   = +0.2 seconds beginning 17 Sep 2015 at 0000 UTC                 
     *   = +0.1 seconds beginning 26 Nov 2015 at 0000 UTC 
     *   = +0.0 seconds beginning 31 Jan 2016 at 0000 UTC      
     *   = -0.1 seconds beginning 24 Mar 2016 at 0000 UTC    
     *   = -0.2 seconds beginning 19 May 2016 at 0000 UTC    
     *   =  -0.3 seconds beginning 01 Sep 2016 at 0000 UTC         
     *   =  -0.4 seconds beginning 17 Nov 2016 at 0000 UTC                 
     *   =  +0.6 seconds beginning 01 Jan 2017 at 0000 UTC
     */
    static final double UT1UTC = +0.6;

    /**
     * Calls slaCldj with the specified parameters.
     * 
     * @param year
     *            The year part of the date.
     * @param month
     *            The month part of the date.
     * @param date
     *            The day-in-month part of the date.
     * @return The MJD of the specified date.
     */
    private static native double ncallSlaCldj(int year, int month, int date);

    /**
     * Calls slaDbear with specified parameters.
     * 
     * @param az1
     *            Azimuth of source.
     * @param alt1
     *            Elevation of source.
     * @param az2
     *            Azimuth of target.
     * @param alt2
     *            Elevation of target.
     */
    private static native double ncallSlaDbear(double az1, double alt1, double az2, double alt2);

    /**
     * Calls slaGmsta with the specified parameters.
     * 
     * @param mjd
     *            The date as MJD.
     * @param ut1
     *            The time in day UT1 as fraction in [0,1].
     * @return The GMST of the time.
     */
    private static native double ncallSlaGmsta(double mjd, double ut1);

    /**
     * Calls slaDtt with the specified parameters.
     * 
     * @param dju
     *            The date as MJD.
     * @return The difference between ET and UT in secs.
     */
    private static native double ncallSlaDtt(double dju);

    /**
     * Calls slaEqeqex with the spcified MJD Date.
     * 
     * @param mjd
     *            The date as MJD.
     */
    private static native double ncallSlaEqeqx(double mjd);

    /**
     * Calls slaRdplan with specified parameters.
     * 
     * @param mjd
     *            The date of observation (mjd) in ET not UT.
     * @param iplanet
     *            The planet code.
     * @param elong
     *            Longitude East +ve.
     * @param phi
     *            Latitiude.
     */
    private static native Coordinates ncallSlaRdplan(double mjd, int iplanet, double elong, double phi);

    /**
     * Calls slaEvp with specified parameters.
     * 
     * @param mjd
     *            The date of observation (mjd).
     * @retuan The Solar position relative to Earth.
     */
    private static native Coordinates ncallSlaEvp(double mjd);

    public AstroLib getInstance() {
	return new JAstroSlalib();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getEtUt(long)
     */
    public double getEtUt(long time) {
	double mjd = getMJD(time);
	double ut1 = getUT(time);
	return ncallSlaDtt(mjd + ut1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getEtUt2(long)
     */
    public double getEtUt2(long time) {
	// we can only use this for "now" so we know what the value of 'time' is
	return 32.184 + TAIUTC - UT1UTC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getMJD(long)
     */
    public double getMJD(long time) {
	GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
	calendar.setTimeZone(UTC);
	calendar.setTime(new Date(time));

	return ncallSlaCldj(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
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

    /**
     * Actually returns the LST rather than GMST in rads.
     * @see ngat.astrometry.AstroLib#getLST(long, double)
     */
    public double getLST(long time, double longitude) {
	GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
	calendar.setTimeZone(UTC);
	calendar.setTime(new Date(time));

	// mjd as a day number since MJD epoch
	double mjd = ncallSlaCldj(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar
				  .get(Calendar.DATE));
		
	// utc as day fraction
	double utc = (calendar.get(Calendar.HOUR_OF_DAY) * 3600000.0 + calendar.get(Calendar.MINUTE) * 60000.0
		      + calendar.get(Calendar.SECOND) * 1000.0 + calendar.get(Calendar.MILLISECOND)) / 86400000.0;
	
	// add UT1-UTC correction i (as day fraction)
	double ut1 = utc + (UT1UTC/86400.0);
	double lst = ncallSlaGmsta(mjd, ut1) + longitude;
	double PI2 = Math.PI * 2.0;
	if (lst > PI2)
	    lst -= PI2;
	if (lst < 0.0)
	    lst += PI2;
	return lst;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getEquationOfEquinoxes(long)
     */
    public double getEquationOfEquinoxes(long time) {
	double mjd = getMJD(time);
	double ut1 = getUT(time);
	return ncallSlaEqeqx(mjd + ut1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getLAST(long, double)
     */
    public double getLAST(long time, double longitude) {
	double lst = getLST(time, longitude);
	double eq = getEquationOfEquinoxes(time);
	return lst + eq;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#calcBearing(double, double, double, double)
     */
    public double calcBearing(double az1, double alt1, double az2, double alt2) {
	return ncallSlaDbear(az1, alt1, az2, alt2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getLunarCoordinates(ngat.astrometry.ISite,
     * long)
     */
    public Coordinates getLunarCoordinates(ISite site, long time) {
	double mjd = getMJD(time);
	double ut1 = getUT(time);

	double mjdut = mjd + ut1;

	// WARNING use etut2 with specified TAI-UTC and UT1_UTC values
	double etut = ncallSlaDtt(mjdut);
	double mjdet = mjdut + (etut / 86400.0);

	double elong = site.getLongitude();
	double phi = site.getLatitude();

	Coordinates coord = ncallSlaRdplan(mjdet, 3, elong, phi);

	return coord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ngat.astrometry.AstroLib#getSolarCoordinates(long)
     */
    public Coordinates getSolarCoordinates(long time) {

	double mjd = getMJD(time);
	double ut1 = getUT(time);

	Coordinates coord = ncallSlaEvp(mjd + ut1);
	return coord;

    }

    /*
     * (non-Javadoc)
     * 
     * @seengat.astrometry.AstroLib#getCatalogCoordinates(ngat.phase2.
     * XSlaNamedPlanetTarget, ngat.astrometry.ISite, long)
     */
    public Coordinates getCatalogCoordinates(XSlaNamedPlanetTarget catalogTarget, ISite site, long time) {

	double mjd = getMJD(time);
	double ut1 = getUT(time);

	double mjdut = mjd + ut1;

	double etut = ncallSlaDtt(mjdut);
	double mjdet = mjdut + (etut / 86400.0);

	// System.err.println("J:Calling rdplan with mjdut = "+mjdut+" mjdet = "+mjdet);

	double elong = site.getLongitude();
	double phi = site.getLatitude();

	Coordinates coord = ncallSlaRdplan(mjdet, catalogTarget.getIndex(), elong, phi);
	// System.err.println("J:Called rdplan");
	return coord;

    }

    static {
	//System.err.println("Load new slalib");
	System.loadLibrary("newjslalib");
    }

}
