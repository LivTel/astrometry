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

/** Check if a specified target can rise and when.
 * @author eng
 * 
 */
public class TargetRising {

	BasicAstrometryCalculator astro;
	
	/**
	 * 
	 */
	public TargetRising() {
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
			BasicSite site = new BasicSite("TestSite",lat,lon);
			
			double ra  = Math.toRadians(cfg.getDoubleValue("ra"));
			double dec = Math.toRadians(cfg.getDoubleValue("dec"));
			
			XExtraSolarTarget star = new XExtraSolarTarget("Star");
			star.setRa(ra);
			star.setDec(dec);
			
			double h = Math.toRadians(cfg.getDoubleValue("horizon"));
			
			long interval = cfg.getLongValue("interval");
			int ns = cfg.getIntValue("ns");
			
			TargetRising test = new TargetRising();
			
			long time = System.currentTimeMillis();
			for (int i = 0; i < ns; i++) {
				test.testCanRise(star, site, h, time+i*interval);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testCanRise(ITarget target, ISite site, double horizon, long time) throws Exception {
		BasicTargetCalculator tc = new BasicTargetCalculator(target, site);
		Coordinates c = tc.getCoordinates(time);
		
		System.err.printf("check if target can rise above: %3.2f at %tT",Math.toDegrees(horizon), time);
		
		boolean can = astro.canRise(c, site, horizon, time);
		
		System.err.printf(" -> %b \n",can);
		
	}

}
