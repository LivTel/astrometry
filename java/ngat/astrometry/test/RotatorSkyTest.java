package ngat.astrometry.test;

import ngat.astrometry.*;
import ngat.util.*;
import ngat.phase2.*;

import java.util.*;
import java.text.*;

public class RotatorSkyTest {

	public static void main(String args[]) {

		double IOFF = Math.toRadians(104.0);
		SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

		TimeZone.setDefault(UTC);
		fdf.setTimeZone(UTC);

		BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

		Map<String, Double> iomap = new HashMap<String, Double>();
		iomap.put("IO:O", Math.toRadians(0.0));
		iomap.put("RISE", Math.toRadians(-44.4));
		iomap.put("RINGO3", Math.toRadians(-87.8));
		iomap.put("FRODO", Math.toRadians(0.0));

		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			TargetTrackCalculatorFactory tcf = null;

			// double instOffset = Math.toRadians(cfg.getDoubleValue("ioff"));
			tcf = new BasicTargetTrackCalculatorFactory();

			long t = (fdf.parse(cfg.getProperty("start"))).getTime();
			double ra = AstroFormatter.parseHMS(cfg.getProperty("ra"), ":");
			double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":");
			XExtraSolarTarget target = new XExtraSolarTarget();
			target.setRa(ra);
			target.setDec(dec);
			double duration = 1000 * cfg.getDoubleValue("duration");

			System.err.printf("Starting %tF %tT \n",t,t);
			System.err.println("Starting at: "+fdf.format(new Date(t)));
			
			String instName = cfg.getProperty("inst");
			if (instName == null)
				throw new IllegalArgumentException("No instrument supplied: IO:O RISE RINGO3 FRODO");

			if (!iomap.containsKey(instName))
				throw new IllegalArgumentException("Unknown instrument: " + instName + " use IO:O RISE RINGO3 FRODO");

			double instOffset = iomap.get(instName);

			System.err.println("Using inst offset: " + Math.toDegrees(instOffset) + " deg for " + instName);

			BasicCardinalPointingCalculator cpc = new BasicCardinalPointingCalculator(site);
			// now work out the rotator angles at that time for the instrument
			double skydeg = 0.0;
			while (skydeg < 360.0) {

				double sky = Math.toRadians(skydeg);

				boolean feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF - instOffset, t, (long) (t + duration));

				System.err.printf("Sky: %4.2f : %b \n", skydeg, feasible);

				skydeg += 5.0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}