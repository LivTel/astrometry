/**
 * 
 */
package ngat.astrometry.test;

import java.rmi.Naming;

import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicAstrometryProvider;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class RegisterBasicProvider {

	public static final int DEFAULT_PORT = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			String host = cfg.getProperty("host", "localhost");
			int port = cfg.getIntValue("port", DEFAULT_PORT);

			BasicAstrometryCalculator calc = new BasicAstrometryCalculator();
			BasicAstrometryProvider provider = new BasicAstrometryProvider(calc);

			Naming.rebind("rmi://" + host + (port == DEFAULT_PORT ? "" : "" + port) + "/AstrometryProvider", provider);

			System.err.println("Bound AstroProvider");
			
			while (true) {
				try {
					Thread.sleep(6000L);
					//System.err.print(".");
				} catch (InterruptedException ix) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
