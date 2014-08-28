/**
 * 
 */
package ngat.astrometry;

import ngat.phase2.ITarget;

/**
 * @author eng
 *
 */
public class BasicTargetTrackCalculatorFactory implements TargetTrackCalculatorFactory {

	/* (non-Javadoc)
	 * @see ngat.astrometry.TargetTrackCalculatorFactory#getTrackCalculator()
	 */
	public TargetTrackCalculator getTrackCalculator(ITarget target, ISite site) {
		return new BasicTargetCalculator(target, site);
	}

}
