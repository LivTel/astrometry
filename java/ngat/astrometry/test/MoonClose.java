/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.LunarCalculator;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class MoonClose {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
try {
			
			double ra1 = Math.toRadians(Double.parseDouble(args[0]));
			double dec1 = Math.toRadians(Double.parseDouble(args[1]));

			XExtraSolarTarget target1 = new XExtraSolarTarget("Star1");
			target1.setRa(ra1);
			target1.setDec(dec1);
		
			double lat = Math.toRadians(Double.parseDouble(args[2]));
			double lon = Math.toRadians(Double.parseDouble(args[3]));

			BasicSite site = new BasicSite("Obs", lat, lon);
			TargetTrackCalculator tpc1 = new BasicTargetCalculator(target1, site);
			
			TargetTrackCalculator tpc2 = new LunarCalculator(site);
			
			BasicAstrometryCalculator astro = new BasicAstrometryCalculator();
			
			long start  = System.currentTimeMillis();
			long finish = System.currentTimeMillis()+12*3600*1000L;
			
			double cpa = astro.getClosestPointOfApproach(tpc1, tpc2, start, finish);
			
			System.err.printf("CPA Moon is: %3.2f \n", Math.toDegrees(cpa));
								
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
