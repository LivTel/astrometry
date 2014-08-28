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
		// XEphemerisTrackNode[] track = (XEphemerisTrackNode[])
		// (positionSet.toArray());
		// System.err.println("TargetCalculator: interpolateTrack():Creating track array size: "+positionSet.size());
		XEphemerisTrackNode[] track = new XEphemerisTrackNode[positionSet.size()];
		// System.err.println("TargetCalculator: interpolateTrack():Track array initialized: "+track);

		int index = 0;
		Iterator itrack = positionSet.iterator();
		while (itrack.hasNext()) {
			// System.err.println("TargetCalculator: interpolateTrack(): copying index: "+index);
			XEphemerisTrackNode xtn = (XEphemerisTrackNode) itrack.next();
			track[index] = xtn;
			index++;
		}

		// case 1 - before first node.
		if (time < first.time) {
			//System.err.println("TargetCalculator: interpolateTrack():before first");
			a = first;
			if (positionSet.size() >= 2) {
				b = track[1]; // (0, 1)
			} else
				return new Coordinates(a.ra, a.dec);
		}

		// case 2 - after last node.
		if (time > last.time) {
			//System.err.println("TargetCalculator: interpolateTrack():after last");
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
				// extract coord at time using the set of points...
				// point.ra point.dec point.raDot point.decDot

				// cubic - extract 4 points either side
				// linear - extract 2 points either side
				//System.err.println("TargetCalculator: interpolateTrack():tas=" + (time - first.time) 
						//	+ " tbf="+(last.time-time));
				//System.err.println("TargetCalculator: interpolateTrack():interval:" + i + " -> " + (i + 1) + " of "
						//+ positionSet.size());

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

	private Coordinates interpolate(XEphemerisTrackNode a, XEphemerisTrackNode b, long time) throws Exception {

		double ra = (double) (time - a.time) * (b.ra - a.ra) / (double) (b.time - a.time) + a.ra;
		double dc = (double) (time - a.time) * (b.dec - a.dec) / ((double) b.time - a.time) + a.dec;

		return new Coordinates(ra, dc);

	}

}
