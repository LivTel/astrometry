/**
 * 
 */
package ngat.astrometry;

import java.rmi.Remote;
import java.rmi.RemoteException;


/** 
 * @author eng
 *
 */
public interface AstrometryProvider extends Remote {

	public AstrometryCalculator getCalculator() throws RemoteException;
	
	//public AstrometrySiteCalculator getObservatoryCalculator() throws RemoteException;
	
	//public AstrometryTargetCalculator getTargetCalculator() throws RemoteException;
	
	//public AstrometryTargetSiteCalculator getTargetSiteCalculator() throws RemoteException;
	
	
}
