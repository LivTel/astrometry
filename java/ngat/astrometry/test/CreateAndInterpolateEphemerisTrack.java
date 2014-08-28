/**
 * 
 */
package ngat.astrometry.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;

/**
 * @author eng
 * 
 */
public class CreateAndInterpolateEphemerisTrack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			ISite site = new BasicSite("here", Math.toRadians(27.0), Math.toRadians(-17.0));

			XEphemerisTarget target = new XEphemerisTarget("Asteroid");

			long now = System.currentTimeMillis();
			long t = now;
			double ra = Math.PI;
			double dec = 0.25 * Math.PI;
			while (t < now + 24 * 3600 * 1000L) {
				// add +/- 0.5 degs to the track
				ra += Math.toRadians(Math.random() - 0.5);
				dec += Math.toRadians(Math.random() - 0.5);
				XEphemerisTrackNode node = new XEphemerisTrackNode(t, ra, dec, 0.0, 0.0);
				target.addTrackNode(node);

				t += 3600000L; // 1 hour
			}

			// now got a full track.

			BasicTargetCalculator btc = new BasicTargetCalculator(target, site);
			
			long at = now - 6*3600*1000L;
			while (at < now + 30*3600*1000L) {
				
			
			Coordinates c = btc.getCoordinates(at);
			c.setFrame(Coordinates.ICRF);
			c.setEquinox(2000.0);
			
			//System.err.println("Coordinates at: "+(new Date(at))+" = "+c);

			System.err.println(sdf.format(new Date(at))+ " "+at+" "+c);
			
			at += 3600*1000L;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
