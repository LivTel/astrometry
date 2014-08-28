/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.JAstroSlalib;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class ParalacticCalc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			double latitude = Math.toRadians(28.7624);
			double azm = Math.toRadians(cfg.getDoubleValue("azm"));
			double alt = Math.toRadians(cfg.getDoubleValue("alt"));

			double baz = azm;
			if (azm < Math.PI)
				baz = Math.PI-azm;
				else
					baz = 3.0*Math.PI-azm;
			
			double p = 0.0;//JAstroSlalib.calcBearing(baz, alt, Math.PI, latitude); // parallactic

			System.err.printf("Azm:  %3.2f, Alt:  %3.2f -> Paralactic: %3.2f \n", Math.toDegrees(azm), Math
					.toDegrees(alt), Math.toDegrees(p));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
