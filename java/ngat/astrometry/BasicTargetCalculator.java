/**
 * 
 */
package ngat.astrometry;

import java.util.Iterator;
import java.util.SortedSet;

import ngat.phase2.ITarget;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XSlaNamedPlanetTarget;

/**
 * Calculates a generic target position.
 * 
 * @author eng
 * 
 */
public class BasicTargetCalculator implements TargetTrackCalculator {
	
	AstroLib astroLib;
	
	/** The target for which to calculate coordinates. */
	ITarget target;

	/** The site from which observing takes place. */
	ISite site;

	/**
	 * @param target
	 *            The target for which to calculate coordinates.
	 * @see ngat.astrometry.TargetTrackCalculator#getCoordinates(long)
	 */
	public BasicTargetCalculator(ITarget target, ISite site) {
		this.target = target;
		this.site = site;
		astroLib = new JAstroSlalib();
	}
	
	/**
	 * @param astroLib
	 * @param target
	 * @param site
	 */
	public BasicTargetCalculator(AstroLib astroLib, ITarget target, ISite site) {
		super();
		this.astroLib = astroLib;
		this.target = target;
		this.site = site;
	}



	public Coordinates getCoordinates(long time) throws AstrometryException {
		if (target == null)
			throw new AstrometryException("getCoordinates(): No target specified");

		if (target instanceof XExtraSolarTarget) {
			XExtraSolarTarget xstar = (XExtraSolarTarget) target;
			double ra = xstar.getRa();
			double dec = xstar.getDec();
			Coordinates c = new Coordinates(ra, dec);
			// TODOc.setEquinox(TODO)
			return c;
		} else if (target instanceof XEphemerisTarget) {
			// TODO implement some sort of clever spline using the nodes and
			// rates of change
			XEphemerisTarget xephem = (XEphemerisTarget) target;
			SortedSet positionSet = xephem.getEphemerisTrack();

			// just incase something goes awry in the interpolation
			try {
				// System.err.println("TargetCalculator: calling interpolateTrack...");
				return interpolateTrack(time, positionSet);
			} catch (Exception e) {
				throw new AstrometryException("getCoordinates(): Error processing ephemeris track: " + e);
			}
		} else if (target instanceof XSlaNamedPlanetTarget) {

			// TODO which JAstroSlalib function do we call here ?
			XSlaNamedPlanetTarget catalogTarget = (XSlaNamedPlanetTarget) target;

			// just incase something goes awry
			try {
				Coordinates c = astroLib.getCatalogCoordinates(catalogTarget, site, time);
				return c;
			} catch (Exception e) {
				throw new AstrometryException("getCoordinates(): Error processing catalog target: " + e);
			}

		}
		throw new AstrometryException("getCoordinates(): Unknown target type: " + target.getClass().getName());
	}

	private Coordinates interpolateTrack(long time, SortedSet positionSet) throws Exception {
		// System.err.println("TargetCalculator: interpolateTrack(): First="+positionSet.first()+" Last="+positionSet.last());

	    if (positionSet == null)
	    	throw new Exception("interpolateTrack(): Track list is null");

	    if (positionSet.size() == 0)
	    	throw new Exception("interpolateTrack(): Track list is empty");

		XEphemerisTrackNode first = (XEphemerisTrackNode) positionSet.first();
		XEphemerisTrackNode last = (XEphemerisTrackNode) positionSet.last();

		// record the 2 nearest nodes
		XEphemerisTrackNode a = null;
		XEphemerisTrackNode b = null;

		// turn into an array for simplicity of handling...
		XEphemerisTrackNode[] track = new XEphemerisTrackNode[positionSet.size()];
		
		int index = 0;
		Iterator itrack = positionSet.iterator();
		while (itrack.hasNext()) {		
			XEphemerisTrackNode xtn = (XEphemerisTrackNode) itrack.next();
			track[index] = xtn;
			index++;
		}

		// case 1 - before first node.
		if (time < first.time) {
			// an alternative is to throw an exception and not try to extrapolate
			//throw new Exception(String.format("Interpolation time %tc is before first node at %tc",time, first.time));
			a = first;
			if (positionSet.size() >= 2) {
				b = track[1]; // (0, 1)
			} else
				return new Coordinates(a.ra, a.dec);
		}

		// case 2 - after last node.
		if (time > last.time) {			
			// an alternative is to throw an exception and not try to extrapolate
			//throw new Exception(String.format("Interpolation time %tc is after last node at %tc",time, last.time));
			b = last;
			if (positionSet.size() >= 2) {
				a = track[positionSet.size() - 2]; // (n-2, n-1)
			} else
				return new Coordinates(b.ra, b.dec);
		}

		// case 3 - somewhere between first and last
		// - there are at least 2 points or we would have dropped out by now
		if (time >= first.time && time <= last.time) {
			//System.err.println("TargetCalculator: interpolateTrack():between first and last");
			for (int i = 0; i < positionSet.size() - 1; i++) {
				
				// cubic - extract 4 points either side
				// linear - extract 2 points either side
				
				// find interval containing time
				if (track[i].time <= time && track[i + 1].time >= time) {
					//System.err.println("TargetCalculator: interpolateTrack():found in interval");
					a = track[i];
					b = track[i + 1];
					break;
				}

			}

		}
		return interpolate(a, b, time);
	}

	private Coordinates interpolate(XEphemerisTrackNode a1, XEphemerisTrackNode a2, long time) throws Exception {

		//double ra = (double) (time - a.time) * (b.ra - a.ra) / (double) (b.time - a.time) + a.ra;
		//double dc = (double) (time - a.time) * (b.dec - a.dec) / ((double) b.time - a.time) + a.dec;

		//return new Coordinates(ra, dc);

		// times at a1,a2
		double t1 = (double)(a1.time);
		double t2 = (double)(a2.time);

		// t1 <= epoch <= t2 hopefully
		// calculate fraction of interval
		double f = (time-t1)/(t2-t1);

		double ra1 = a1.ra;
		double ra2 = a2.ra;
		double dec1 = a1.dec;
		double dec2 = a2.dec;
		
		// angular seperation - distance to travel - TODO do we need to check wraps ??? or does it just work anyway ???
		double dd = Math.acos(Math.cos(dec1)*Math.cos(dec2)*Math.cos(ra1-ra2) + Math.sin(dec1)*Math.sin(dec2));
		
		// calculations....
		double aa = Math.sin((1-f)*dd)/Math.sin(dd);
		double bb = Math.sin(f*dd)/Math.sin(dd);

		double x = aa*Math.cos(dec1)*Math.cos(ra1) + bb*Math.cos(dec2)*Math.cos(ra2);
		double y = aa*Math.cos(dec1)*Math.sin(ra1) + bb*Math.cos(dec2)*Math.sin(ra2);	
		double z = aa*Math.sin(dec1) + bb*Math.sin(dec2);

		double fdec = Math.atan2(z, Math.sqrt(x*x+y*y));
		double fra  = Math.atan2(y, x);

	
		System.err.println("**Ephem Target at: "+AstroFormatter.formatHMS(fra, ":")+","+AstroFormatter.formatDMS(fdec, ":")); 
		return new Coordinates(fra,fdec);

	}

}
