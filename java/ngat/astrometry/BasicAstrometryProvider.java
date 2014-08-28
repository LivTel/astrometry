/**
 * 
 */
package ngat.astrometry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author eng
 *
 */
public class BasicAstrometryProvider extends UnicastRemoteObject implements AstrometryProvider {

	/**
	 * The Serial Version UID 
	 */
	private static final long serialVersionUID = 1709323991887893950L;
	
	private AstrometryCalculator calculator;
	
	/** Create a new BasicAstrometryProvider using the supplied calculator.
	 * @param calculator An instance of AstrometryCalculator.
	 * @throws RemoteException If anything goes awry.
	 */
	public BasicAstrometryProvider(AstrometryCalculator calculator) throws RemoteException {
		super();	
		this.calculator = calculator;
	}

	
	/** 
	 * @see ngat.astrometry.AstrometryProvider#getCalculator()
	 */
	public AstrometryCalculator getCalculator() throws RemoteException {
		return calculator;
	}

}
