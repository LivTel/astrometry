/**
 * 
 */
package ngat.astrometry.test;

import java.rmi.Naming;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometryProvider;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 *
 */
public class GetCalculator {
	
	public static final int DEFAULT_PORT = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			String host = cfg.getProperty("host", "localhost");
			int port = cfg.getIntValue("port", DEFAULT_PORT);

			AstrometryProvider provider = (AstrometryProvider)Naming
			.lookup("rmi://" + host + (port == DEFAULT_PORT ? "" : "" + port) + "/AstrometryProvider");

			System.err.println("Got provider ref: "+provider);
			
			AstrometryCalculator calc = provider.getCalculator();
			
			System.err.println("Retrieved calculator: "+calc);
			
			double ra = Math.toRadians(cfg.getDoubleValue("ra"));
			double dec = Math.toRadians(cfg.getDoubleValue("dec"));

			XExtraSolarTarget target = new XExtraSolarTarget("Star1");
			target.setRa(ra);
			target.setDec(dec);

			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));

			BasicSite site = new BasicSite("Obs", lat, lon);

			TargetTrackCalculator tpc = new BasicTargetCalculator(target, site);
			
			
			long now = System.currentTimeMillis();
			long t = now;
			while (t < now + 24 * 3600 * 1000L) {
				Coordinates c = tpc.getCoordinates(t);
				double alt = calc.getAltitude(c, site, t);
				double azm = calc.getAzimuth(c, site, t);
				System.err.printf("%tT %4.2f %4.2f \n", t, Math.toDegrees(azm), Math.toDegrees(alt));
				t += 60 * 60 * 1000L;
			}
			
			
	} catch (Exception e) {
		e.printStackTrace();
	}

	}

}
