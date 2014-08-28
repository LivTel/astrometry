/**
 * 
 */
package ngat.astrometry;

/** Calculates the coordinates of a target.
 * @author eng
 *
 */
public interface TargetTrackCalculator {

	/** Calculates the coordinates of a target.
	 * @param time The epoch for which the coordinates are required.
	 * @return The target's coordinates at the specified epoch.
	 * @throws AstrometryException if anything goes wrong.
	 */
	public Coordinates getCoordinates(long time) throws AstrometryException;
	
}
