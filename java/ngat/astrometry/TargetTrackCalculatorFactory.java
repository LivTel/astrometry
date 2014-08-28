/**
 * 
 */
package ngat.astrometry;

import ngat.phase2.ITarget;

/**
 * @author eng
 *
 */
public interface TargetTrackCalculatorFactory {

	public TargetTrackCalculator getTrackCalculator(ITarget target, ISite site);
	
}
