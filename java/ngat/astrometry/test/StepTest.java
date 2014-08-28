/**
 * 
 */
package ngat.astrometry.test;

import java.util.Iterator;
import java.util.SortedSet;

import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;

/**
 * @author eng
 *
 */
public class StepTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			long start = System.currentTimeMillis();
			double ra0 = Math.random()*Math.PI*2.0;
			double dec0 = (Math.random()-0.5)*Math.PI;
			BasicSite site = new BasicSite("Site", Math.toRadians(28.0), Math.toRadians(-17.0));
			
			XEphemerisTarget ephem = new XEphemerisTarget("Star");
			ephem.addTrackNode(new XEphemerisTrackNode(start, ra0, dec0, 0.0, 0.0));
			ephem.addTrackNode(new XEphemerisTrackNode(start+5*3600*1000L, ra0, dec0, 0.0, 0.0));
			
			double dra = Math.toRadians(10.0);
			double ddec = Math.toRadians(5.0);
			ephem.addTrackNode(new XEphemerisTrackNode(start+24*3600*1000, ra0+dra, dec0+ddec, 0.0, 0.0));
			ephem.addTrackNode(new XEphemerisTrackNode(start+29*3600*1000L, ra0+dra, dec0+ddec, 0.0, 0.0));
			
			ephem.addTrackNode(new XEphemerisTrackNode(start+30*3600*1000, ra0+2*dra, dec0+2*ddec, 0.0, 0.0));
			ephem.addTrackNode(new XEphemerisTrackNode(start+33*3600*1000L, ra0+2*dra, dec0+ddec, 0.0, 0.0));
			
			BasicTargetCalculator btc = new BasicTargetCalculator(ephem, site);
			
			SortedSet track = ephem.getEphemerisTrack();
			
			Iterator is = track.iterator();
			while (is.hasNext()) {
				XEphemerisTrackNode node = (XEphemerisTrackNode)is.next();
				System.err.printf("%tF %tT   ra %4.2f   dec %4.2f \n", node.time, node.time, Math.toDegrees(node.ra), Math.toDegrees(node.dec));
			}
		
			long time = start;
			while (time < start+48*3600*1000L) {
				
				Coordinates c = btc.getCoordinates(time);
				double ra = c.getRa();
				double dec = c.getDec();
				
				System.err.printf("Position at: %tF %tT   ra %4.2f   dec %4.2f \n", time, time, Math.toDegrees(ra), Math.toDegrees(dec));
				
				time += 3600*1000L;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
