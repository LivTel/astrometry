/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstroLib;
import ngat.astrometry.JAstroSlalib;
import ngat.astrometry.approximate.BasicAstroLibImpl;

/** Comparetheastrolibs.com
 * @author eng
 *
 */
public class AstrolibTimeDiff {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			
			AstroLib lib1 = new JAstroSlalib();
			AstroLib lib2 = new BasicAstroLibImpl();

			double longitude = Math.toRadians(-17.0);
		
			long start = System.currentTimeMillis()+ (long)(Math.random()*24*3600*1000.0);
			long time = start;

			while (time < start + 365*24*2600*1000L) {
							
				double lst1 = lib1.getLST(time, longitude);
				double lst2 = lib2.getLST(time, longitude);
		
				System.err.printf("%tF %tT :: %4.2f %4.2f Diff= %4.2f\n",time,time,lst1, lst2,Math.toDegrees(lst1-lst2));
				
				time += 24*3600*1000L; // next day

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
