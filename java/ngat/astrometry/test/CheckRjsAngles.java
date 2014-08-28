/**
 * 
 */
package ngat.astrometry.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicSite;
import ngat.phase2.XExtraSolarTarget;

/** Read RJS angles from a file and see if we agree with them.
 * @author eng
 *
 */
public class CheckRjsAngles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
		sdf.setTimeZone(UTC);
		
		int il = 0;
		
		double LAT = Math.toRadians(28.7624);
		double LON = Math.toRadians(-17.8792);
		BasicSite site = new BasicSite("test", LAT, LON);
		BasicCardinalPointingCalculator cpc = new BasicCardinalPointingCalculator(site);
		
		
		try {
		
			String fileName = args[0];
			BufferedReader bin = new BufferedReader(new FileReader(fileName));
			
			
			String line = null;
			while ((line = bin.readLine()) != null) {
				il++;
				StringTokenizer t = new StringTokenizer(line);
				
				String stra = t.nextToken();
				double ra = AstroFormatter.parseHMS(stra, ":");
				
				String strdec = t.nextToken();
				double dec = AstroFormatter.parseDMS(strdec, ":");
				
				t.nextToken(); // skip j2k
				
				String strut = t.nextToken();
				long time = (sdf.parse(strut)).getTime();
				
				String strha = t.nextToken();
				
				String strpa = t.nextToken();				
				double pa = Math.toRadians(Double.parseDouble(strpa));
				
				String strIOFF = t.nextToken();
				double IOFF = Math.toRadians(Double.parseDouble(strIOFF));
				
				String strioff = t.nextToken();
				double ioff = Math.toRadians(Double.parseDouble(strioff));
				
				String strsky = t.nextToken();
				double sky = Math.toRadians(Double.parseDouble(strsky));
				
				String strmou = t.nextToken();
				double mount = Math.toRadians(Double.parseDouble(strmou));
				
				// correct to +-180
				while (mount < -Math.PI)
					mount += Math.PI*2.0;
				
				while (mount > Math.PI)
					mount -= Math.PI*2.0;
				
			
				XExtraSolarTarget target = new XExtraSolarTarget("star");
				target.setRa(ra);
				target.setDec(dec);
				double cpcmount = cpc.getMountAngle(sky, target, IOFF-ioff, time);
				while (cpcmount < -Math.PI)
					cpcmount += Math.PI*2.0;
				
				while (cpcmount > Math.PI)
					cpcmount -= Math.PI*2.0;
				
				double diff = mount-cpcmount;
				
				System.err.printf("Read: a %4.2f d %4.2f %tF %tT pa %4.2f ii %4.2f sky %4.2f mo %4.2f cp %4.2f    DIFF %4.2f\n", 
						ra, dec, time, time, pa, IOFF-ioff, sky, Math.toDegrees(mount), Math.toDegrees(cpcmount), Math.toDegrees(diff));			
				
				if (Math.abs(mount-cpcmount) > Math.toRadians(1.0))
					System.err.println(" Warning - angles are different");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error on line: "+il);
		}		

	}

}
