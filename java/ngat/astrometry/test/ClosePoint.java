/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class ClosePoint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			double ra1 = Math.toRadians(Double.parseDouble(args[0]));
			double dec1 = Math.toRadians(Double.parseDouble(args[1]));

			ISite site = new BasicSite("TestSite", Math.toRadians(28.0), Math.toRadians(-17.0));
			
			XExtraSolarTarget target1 = new XExtraSolarTarget("Star1");
			target1.setRa(ra1);
			target1.setDec(dec1);
			TargetTrackCalculator tpc1 = new BasicTargetCalculator(target1, site);
			
			double ra2 = Math.toRadians(Double.parseDouble(args[2]));
			double dec2 = Math.toRadians(Double.parseDouble(args[3]));

			XExtraSolarTarget target2 = new XExtraSolarTarget("Star2");
			target2.setRa(ra2);
			target2.setDec(dec2);
			TargetTrackCalculator tpc2 = new BasicTargetCalculator(target2, site);
		
			BasicAstrometryCalculator astro = new BasicAstrometryCalculator();
			
			long start  = System.currentTimeMillis();
			long finish = System.currentTimeMillis()+2*3600*1000L;
			
			double cpa = astro.getClosestPointOfApproach(tpc1, tpc2, start, finish);
			
			System.err.printf("CPA is: %3.2f \n", Math.toDegrees(cpa));
								
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
