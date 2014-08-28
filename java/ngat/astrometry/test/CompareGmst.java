/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.astrometry.approximate.SImpleAstroCalculator;

/**
 * @author eng
 *
 */
public class CompareGmst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			SImpleAstroCalculator sac = new SImpleAstroCalculator();
			BasicAstrometryCalculator bac = new BasicAstrometryCalculator();
			
			ISite obs = new BasicSite("obs", Math.toRadians(28.0), Math.toRadians(-17.0));
			
			long t = (long)((double)System.currentTimeMillis()+ Math.random()*86400.0*200.0);
			
			double ls = sac.getLST(obs, t);
			double lb = bac.getLST(obs, t);
			
			System.err.printf("AT %tF %tT  LST(s) = %4.2f LST(b) = %4.2f ", t,t,Math.toDegrees(ls),Math.toDegrees(lb));
			
		} catch (Exception e) {
			e.printStackTrace();			
		}

	}

}
