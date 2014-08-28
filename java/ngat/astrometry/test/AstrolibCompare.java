/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstroLib;
import ngat.astrometry.JAstroSlalib;
import ngat.astrometry.approximate.BasicAstroLibImpl;

/** Compare the astrolibs.com
 * @author eng
 *
 */
public class AstrolibCompare {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			
			AstroLib lib1 = new JAstroSlalib();
			AstroLib lib2 = new BasicAstroLibImpl();
			
			for (int i = 0; i < 100; i++) {
			
				long time = System.currentTimeMillis()+ (long)(Math.random()*24*3600*1000.0);
				double longitude = Math.toRadians(-17.0);
				
				double lst1 = lib1.getLST(time, longitude);
				double lst2 = lib2.getLST(time, longitude);
		
				System.err.printf("At %tF %tT :: %4.2f %4.2f Diff= %4.2f\n",time,time,lst1, lst2,Math.toDegrees(lst1-lst2));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
