/**
 * 
 */
package ngat.astrometry.approximate;

import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.TargetTrackCalculatorFactory;
import ngat.phase2.ITarget;

/**
 * @author eng
 *
 */
public class AlternativeTargetTrackCalculatorFactory implements TargetTrackCalculatorFactory {

	private BasicAstroLibImpl astrolib;
	
	
	/**
	 * 
	 */
	public AlternativeTargetTrackCalculatorFactory() {
		super();
		this.astrolib = new BasicAstroLibImpl();
	}



	/* (non-Javadoc)
	 * @see ngat.astrometry.TargetTrackCalculatorFactory#getTrackCalculator(ngat.phase2.ITarget, ngat.astrometry.ISite)
	 */
	public TargetTrackCalculator getTrackCalculator(ITarget target, ISite site) {
		return new BasicTargetCalculator(astrolib, target, site);
	}

}
