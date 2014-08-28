/**
 * 
 */
package ngat.astrometry.test;

import java.util.Calendar;

import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.SolarCalculator;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/** Calculate output from various PV array configurations.
 * @author eng
 *
 */
public class SunHeightStats {

	static final double I0 = 1.0; // incident energy per m2 per sec: kW.m-2.s-1
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Stage 1 - Sun elevation binning
		
		try {
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
			String name = cfg.getProperty("name", "Location");
			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));
			
			BasicSite site = new BasicSite(name, lat, lon);
			
			BasicAstrometrySiteCalculator calc = new BasicAstrometrySiteCalculator(site);
			
			Calendar start = Calendar.getInstance();
			// Midnight on Jan 1st of current year
			start.set(Calendar.DAY_OF_YEAR, 1);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			
			SolarCalculator suncalc = new SolarCalculator();
			
			/*int[] bins = new int[10];
			for (int i = 0; i < 10; i++) {
				bins[i] = 0;
			}
			
			
			
			System.err.printf("Starting %tF %tT\n", start, start);
			long startTime = start.getTimeInMillis();
			long time = startTime;
			while (time < startTime+365*86400*1000L) {
				
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(time);
				
				Coordinates sun = suncalc.getCoordinates(time);
				double elev = calc.getAltitude(sun, time);
				double elevd = Math.toDegrees(elev);
				//System.err.printf("At %tF %tT : %4.2f \n", now, now, elevd);
				
				// count the bin for today and year
				if (elevd > 0.0) {
					int ibin = (int)Math.floor(elevd/10.0);
					bins[ibin]++;
				}
				time += 60000L; // 1 minute
			}
			
			for (int i = 0; i < 10; i++) {
				System.err.printf("Bin %3d : %6.2f \n", i, ((double)bins[i]/60.0));
			}*/
		
		
		
		// Stage 2 Calculate PV array output
	
			
			// tracked array total per day
			double[] etrack = new double[365];
			for (int i = 0; i < 365; i++) {
				etrack[i] = 0.0;
			}
			// track array total
			double strack = 0.0;
			
			// fixed tilted array total per day, per angle
			double[][] efixed = new double[365][70];
			for (int i = 0; i < 365; i++) {
				for (int j = 0; j < 70; j++) {
					efixed[i][j] = 0.0;
				}
			}
			
			// tilted array total per angle
			double[] sfixed = new double[70];
			for (int i = 0; i < 70; i++) {
				sfixed[i] = 0.0;
			}
			
			double totalopt = 0.0; // total for optimal fixed tilt
			double[] total = new double[70];
			for (int i = 0; i < 70; i++) {
				total[i] = 0.0;
			}
			
			
			start = Calendar.getInstance();
			// Midnight on Jan 1st of current year
			start.set(Calendar.DAY_OF_YEAR, 1);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			

			
			System.err.printf("Starting %tF %tT\n", start, start);
			
			long sod = start.getTimeInMillis();			
			for (int d = 0; d < 365; d++) {
				
				// start of day
				//System.err.printf("Start of day: %4d %tF %tT \n", d, sod, sod);
				
				double strackday = 0.0; // total for today tracked - baseline		
				
				for (int j = 0; j < 70; j++) {
					sfixed[j] = 0.0;
				}
			
				
				// scan each minute
				for (int k = 0; k < 1440; k++) {
					
					long time = sod + 60*1000*k;	
					
					Coordinates sun = suncalc.getCoordinates(time);
					double elev = calc.getAltitude(sun, time);
					
					// where is the sun
					sun = suncalc.getCoordinates(time);
					double salt= calc.getAltitude(sun, time);
					double sazm = calc.getAzimuth(sun, time);
					double z = 0.5*Math.PI-salt;
					
					if (salt > 0.0) {
						// calculate airmass
						double a2 = Math.pow(96.07995 - z*180.0/Math.PI, -1.6364);
						double airmass = 1.0 / ( Math.cos(z) + 0.5057*a2);
						
						double index = Math.pow(airmass,  0.678);
						double isurf = 1.1*I0*Math.pow(0.7, index);
						//System.err.printf("%tF %tT : Elev: %6.2f  AM %6.2f IS %6.2f \n", 
								//time, time, 
							//	Math.toDegrees(salt), 
							//	airmass,
							//	isurf);
						
						// just add up the e total for the day assuming tracked
						strackday += isurf/60.0;
						
						// now work out the angle for fixed-tilted arrays
						for (int j = 0; j < 70; j++) {
							
							// tilt angle of array normal from ground.efixed zero = aligned to horizon
							double tilt = Math.toRadians((double)j);
							
							Coordinates carray = new Coordinates(Math.PI, tilt); // SOUTH						
							Coordinates csun   = new Coordinates(sazm, salt);
							
							// what is the angular separation
							double sangle = calc.getAngularSeperation(carray, csun);
							
							// only count if abs angle < 90 degs
							if ((-0.5*Math.PI < sangle) && (sangle < 0.5*Math.PI)) {
								efixed[d][j] += isurf * Math.cos(sangle)/60.0;
								sfixed[j] += isurf * Math.cos(sangle)/60.0;
							}
							
						} // next angle (j)
					
					}
				} // next minute (k)
				
				etrack[d] = strackday;
				strack+= strackday; 
				
				// print day totals		
				System.err.printf("%tF %6.2f ", sod, strackday);
			
				for (int j = 0; j < 7; j++) {
					int jj = j*10;
					System.err.printf("%6.2f ", efixed[d][jj]);
				}
				
				// work out optimal fixed angle for day
				// detect optimal fixed angle for the day
				int jbest = 0;
				double sbest = 0.0;
				for (int j = 0; j < 70; j++) {
					if (sfixed[j] > sbest) {
						sbest = sfixed[j];
						jbest = j;
					}
					// add to j overall total
					total[j]+= sfixed[j];
				}
				totalopt += sbest;
				System.err.printf("    %6.2f  at %4.2f \n", sbest, (double)jbest);	
				
				
				sod += 86400*1000L;
			} // next day (d)
			
			// finally print out all totals for year
			System.err.println("Track array total year: "+strack);
			
			// every 1 degs
			for (int j = 0; j < 70; j++) {
				System.err.printf("Tilt angle: %4d: %6.2f \n", j, total[j]);
			}
			
			// optimal tilt
			System.err.println("Optimal tilt angle: "+totalopt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
