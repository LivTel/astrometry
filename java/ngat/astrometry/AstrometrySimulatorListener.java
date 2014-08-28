/**
 * 
 */
package ngat.astrometry;

/**
 * @author eng
 *
 */
public interface AstrometrySimulatorListener {

    /** Override to notify listeners that the simulation is running or not.
     * @param running True if the simulation is running.
     */
    public void simulationRunning(boolean running);

    /** Override to handle a model time update from the simulator. 
     * @param simulationTime The new simulation time.
     */
    public void simulationTimeUpdated(long simulationTime);
	
}
