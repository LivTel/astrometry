/**
 * 
 */
package ngat.astrometry;

import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

/** Calculates Solar position.
 * @author eng
 *
 */
public class SolarCalculator implements TargetTrackCalculator {
	
	AstroLib astroLib;
	
	/**
	 * Create a Solar calculator.
	 */
	public SolarCalculator() {
		astroLib = new JAstroSlalib();
	}

	/**
	 * @param astroLib
	 */
	public SolarCalculator(AstroLib astroLib) {
		super();
		this.astroLib = astroLib;
	}

	/**
	 * @return The coordinates of the Sun at time.
	 */
	public Coordinates getCoordinates(long time) throws AstrometryException {
		return astroLib.getSolarCoordinates(time);
	}

}
