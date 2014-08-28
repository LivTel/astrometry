/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class TargetHset {
	
	BasicAstrometryCalculator astro;
	
	/**
	 * 
	 */
	public TargetHset() {
		astro = new BasicAstrometryCalculator();
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));
			BasicSite site = new BasicSite("TestSite", lat, lon);

			double ra = Math.toRadians(cfg.getDoubleValue("ra"));
			double dec = Math.toRadians(cfg.getDoubleValue("dec"));

			XExtraSolarTarget star = new XExtraSolarTarget("Star");
			star.setRa(ra);
			star.setDec(dec);

			double h = Math.toRadians(cfg.getDoubleValue("horizon"));


			TargetHset test = new TargetHset();
			
			test.testHset(star,site,h,System.currentTimeMillis());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testHset(ITarget target, ISite site, double horizon, long time) throws Exception {
		BasicTargetCalculator tc = new BasicTargetCalculator(target, site);
		Coordinates c = tc.getCoordinates(time);
		//double hset = astro.hset(c, site, horizon, time);
		double lst = astro.getLST(site, time);
		
		double ha = astro.getHA(c, lst);
		double alt = astro.getAltitude(c, site, time);
		
		System.err.printf("Target HA at set below %3.2f from Site: %s  HA= %3.4f H, Alt= %3.4f \n", 
				Math.toDegrees(horizon), site, (ha*12/Math.PI), Math.toDegrees(alt));
		
	}

}
