package ngat.astrometry.test;
import ngat.astrometry.*;
import ngat.phase2.*;
import ngat.util.*;
import ngat.util.logging.*;

import java.util.*;
import java.text.*;

public class CardinalTest {

    public static final double IOFF = Math.toRadians(68.0);

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleTimeZone UTC = new SimpleTimeZone(0,"UTC");

    public static void main(String args[]) {
    
    
    try {

	sdf.setTimeZone(UTC);

	// Astro log.
	ConsoleLogHandler console = new ConsoleLogHandler(new BasicLogFormatter(120));
	console.setLogLevel(3);
	Logger astroLog = LogManager.getLogger("ASTRO");
	//astroLog.addHandler(console);
	astroLog.addExtendedHandler((ExtendedLogHandler) console);
	astroLog.setLogLevel(3);


	ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
	
	double ra  = AstroFormatter.parseHMS(cfg.getProperty("ra"),":");
	double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"),":");
	long time = (sdf.parse(cfg.getProperty("start"))).getTime();
	long duration = 60000*(cfg.getIntValue("duration")); // minutes

	double ioff = Math.toRadians(cfg.getDoubleValue("ioff"));

	double lat = Math.toRadians(cfg.getDoubleValue("lat"));
	double lon = Math.toRadians(cfg.getDoubleValue("long"));

	BasicSite site = new BasicSite("obs", lat, lon);
	AstrometrySiteCalculator astro  =new BasicAstrometrySiteCalculator(site);
	CardinalPointingCalculator cpc = new BasicCardinalPointingCalculator(site);

	XExtraSolarTarget star = new XExtraSolarTarget("star");
	star.setRa(ra);
	star.setDec(dec);

	TargetTrackCalculator track = new BasicTargetCalculator(star, site);
	System.err.printf("Determine Min elev from %tF %tT to %tF %tT \n",time,time, time+duration, time+duration);

	double minelev = astro.getMinimumAltitude(track, time, time+duration);
	boolean visible = (minelev > Math.toRadians(25.0));
	System.err.printf("Target is %s during the period specified with minimum altitude %3.2f \n",
			  (visible ? "visible" : "NOT visible"), Math.toDegrees(minelev));
	
	Coordinates c = track.getCoordinates(time);
	double alt = astro.getAltitude(c, time);
	double azm = astro.getAzimuth(c, time);

	Coordinates c2 = track.getCoordinates(time+duration);
	double alt2 = astro.getAltitude(c2, time);
	double azm2 = astro.getAzimuth(c2, time);
	
	System.err.printf("Start:  Azm: %3.2f, Alt: %3.2f \n", Math.toDegrees(azm), Math.toDegrees(alt));
	System.err.printf("Finish: Azm: %3.2f, Alt: %3.2f \n", Math.toDegrees(azm2), Math.toDegrees(alt2));
		
	if (visible) {
	    
	    for (int isky = 0; isky < 360; isky += 90) {
		double sky = Math.toRadians((double)isky);
	    
		double mount = cpc.getMountAngle(sky, star, IOFF - ioff, time);
		System.err.printf("Mount angle for sky %4.2f is %4.2f\n", Math.toDegrees(sky), Math.toDegrees(mount));
		boolean feasible = cpc.isFeasibleSkyAngle(sky, star, IOFF-ioff, time, time + duration);
		
		System.err.printf("Sky: %3.2f %s \n",Math.toDegrees(sky), (feasible ? "FEASIBLE" : "NOT FEAS"));
	    }
	    
	}

    } catch (Exception e) {
	e.printStackTrace();
    }
    }

}