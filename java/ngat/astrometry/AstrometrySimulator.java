/**
 * 
 */
package ngat.astrometry;

import java.util.List;
import java.util.Vector;

import sun.font.AdvanceCache;

import ngat.util.ControlThread;

/**
 * @author eng
 *
 */
public class AstrometrySimulator {

    /** Simulation start time.*/
    private long startTime;

    /** Simulation end time.*/
    private long endTime;

    /** Simulation time step.*/
    private long step;

    /** Simulation update delay.*/
    private long delay;
   
    /** Record simulator time.*/
    private long simulationTime;
	
    /** True if simulation is configured and able to run.*/
    private volatile boolean running;
    
    /** List of listeners for simulation events.*/
    private List <AstrometrySimulatorListener> listeners;
	
    /** Simulation execution thread.*/
    private SimulatorThread thread;
    
    /**
     */
    public AstrometrySimulator() {	
	listeners = new Vector<AstrometrySimulatorListener>();
    }

    public void addSimulationListener(AstrometrySimulatorListener l) {
	if (listeners.contains(l))
	    return;
	listeners.add(l);
    }
    
    public void removeSimulationListener(AstrometrySimulatorListener l) {
	if (!listeners.contains(l))
	    return;
	listeners.remove(l);		
    }

    /** Configure the simulation parameters.*/
    public void configure(long startTime, long endTime) {

	System.err.println("ASIM:configure");

	this.startTime = startTime;
	this.endTime = endTime;
	simulationTime = startTime;

	//	state = ENABLED;
    }

    public void reset() {
	System.err.println("ASIM:reset");

	startTime = 0L;
	endTime = 0L;
	simulationTime = startTime;
	//state = IDLE;
    }

    public void runSimulation(long step, long delay) {
	System.err.println("ASIM:runsim:"+step+","+delay);

	if (thread != null)
            return;
        if (thread == null)
            thread = new SimulatorThread(step, delay);
        thread.start();
	//state = RUNNING;
    }

    public void pauseSimulation() {
	System.err.println("ASIM:pausesim");

	if (thread == null)
	    return;
	thread.terminate();
	thread = null;
	//	state = IDLE;
    }

    public void gotoStart() {
	System.err.println("ASIM:gotostart");

	simulationTime = startTime;
	notifyListeners(simulationTime);
    }

    public void gotoEnd() {
	System.err.println("ASIM:gotoend");

        simulationTime = endTime;
	notifyListeners(simulationTime);
    }

    public void stepSimulation(long delta) {
        System.err.println("ASIM:step:"+ delta);

	simulationTime += delta;
        notifyListeners(simulationTime);
    }

	    
    private void notifyListeners(long simulationTime) {
	
	for (int il = 0; il < listeners.size(); il++) {
	    
	    AstrometrySimulatorListener al = listeners.get(il);
	    
	    try {
		System.err.println("ASIM:notifylistener: s="+simulationTime);
		al.simulationTimeUpdated(simulationTime);
	    } catch (Exception e) {
		// TODO for now we just log it, we may want to dump the listener
		e.printStackTrace();
	    }
	    
	}
		
    }

    private void notifyListenersSimulationRunning(boolean running) {
	for (int il = 0; il < listeners.size(); il++) {

            AstrometrySimulatorListener al = listeners.get(il);

            try {
                System.err.println("ASIM:notifylistener: simulation: "+(running ? "running" : "stopping"));
		al.simulationRunning(running);
            } catch (Exception e) {
                // TODO for now we just log it, we may want to dump the listener
                e.printStackTrace();
            }
        }
    }

    /**
     * @author eng
     *
     */
    public class SimulatorThread extends Thread {
	
	private long delay;
	
	private long step;
	
	private boolean quit = false;

	/**
	 * 
	 */
	public SimulatorThread(long step, long delay) {
	    this.step = step;
	    this.delay = delay;
	    System.err.println("ASIM:createsimthread:"+step+", "+delay);
	}	
	
	public void terminate() {
	    System.err.println("ASIM:terminatesimthread");
	    quit = true;
	}

	public void run() {
	    System.err.println("ASIM:runsimthread");
	    notifyListenersSimulationRunning(true);
	    if (step > 0) {
		// forward simulation
		
		while (simulationTime < endTime && ! quit) {
		    
		    // delay interval
		    try{Thread.sleep(delay);} catch (InterruptedException ix) {}

		    // stop at end
		    if ( simulationTime + step > endTime)
			simulationTime = endTime;
		    else
			simulationTime += step;
		    
		    notifyListeners(simulationTime);
		    
		}
		
	    } else {
		
		while (simulationTime > startTime && ! quit) {
		    
		    // delay interval
		    try{Thread.sleep(delay);} catch (InterruptedException ix) {}
		    
		    if ( simulationTime + step < startTime)
                        simulationTime = startTime;
                    else
			simulationTime += step;
		    
		    notifyListeners(simulationTime);
		    
		}
	    }
	    // falling out of loop, either paused or weve hit the buffers
	    notifyListenersSimulationRunning(false);
	}
	
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	
	
    }

}
