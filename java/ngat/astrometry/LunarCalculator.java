/**
 * 
 */
package ngat.astrometry;

import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

/** Calculates Lunar position.
 * @author eng
 *
 */
public class LunarCalculator implements TargetTrackCalculator {

	AstroLib astroLib;
	
	/** The Site for which this calculator is valid.*/
	ISite site;
	
	/** Create a new LunarCalculator.
	 * @param site The site for which the calculator is valid.
	 */
	public LunarCalculator(ISite site) {
		this.site = site;	
		astroLib = new JAstroSlalib();
	}
	
	/**
	 * @param astroLib
	 * @param site
	 */
	public LunarCalculator(AstroLib astroLib, ISite site) {
		super();
		this.astroLib = astroLib;
		this.site = site;
	}

	/**
	 * @return The coordinates of the Moon at time.
	 */
	public Coordinates getCoordinates(long time) throws AstrometryException {
		return astroLib.getLunarCoordinates(site, time);
	}
	

}
