/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.Coordinates;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/** Calculate waypoints on a great-circle between 2 points on a sphere.
 * A test for interpolating NS-tracked object positions.
 * @author eng
 *
 */
public class CalculateWaypoints {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
		
			// Positions
			double ra1  = AstroFormatter.parseHMS(cfg.getProperty("ra1"), ":");
			double dec1 = AstroFormatter.parseDMS(cfg.getProperty("dec1"), ":");
			double ra2  = AstroFormatter.parseHMS(cfg.getProperty("ra2"), ":");
			double dec2 = AstroFormatter.parseDMS(cfg.getProperty("dec2"), ":");
			
			Coordinates c1 = new Coordinates(ra1, dec1);
			Coordinates c2 = new Coordinates(ra2, dec2);
			
			AstrometryCalculator astro = new BasicAstrometryCalculator();
			
			// distance between c1 and c2 as angle on great circle
			double dd = astro.getAngularSeperation(c1, c2);
			
			System.err.printf("Start at %s, %s \n",
					AstroFormatter.formatHMS(ra1, ":"), 
					AstroFormatter.formatDMS(dec1, ":"));
			
			System.err.printf("End at %s, %s \n",
					AstroFormatter.formatHMS(ra2, ":"), 
					AstroFormatter.formatDMS(dec2, ":"));
			
			// choose a bunch of fraction values between 0 (c1) and 1 (c2)
			
			// if posn c1 at time t1 and posn c2 at time t2 then at time t such that:
			//  t1 <= t <= t2 then f = (t-t1)/(t2-t1)
			
			double f = 0.0;
			while (f < 1.05) {
				
				double aa = Math.sin((1-f)*dd)/Math.sin(dd);
				double bb = Math.sin(f*dd)/Math.sin(dd);
				
				double x = aa*Math.cos(dec1)*Math.cos(ra1) + bb*Math.cos(dec2)*Math.cos(ra2);
				double y = aa*Math.cos(dec1)*Math.sin(ra1) + bb*Math.cos(dec2)*Math.sin(ra2);	
				double z = aa*Math.sin(dec1) + bb*Math.sin(dec2);
						
				double fdec = Math.atan2(z, Math.sqrt(x*x+y*y));
				double fra  = Math.atan2(y, x);
				
				if (fra < 0.0)
					fra += 2.0*Math.PI;
				
				System.err.printf("F: %4.2f %s, %s \n", f, 
						AstroFormatter.formatHMS(fra, ":"), 
						AstroFormatter.formatDMS(fdec, ":"));
				
				
				f += 0.05; // move 5% along joining circle
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
