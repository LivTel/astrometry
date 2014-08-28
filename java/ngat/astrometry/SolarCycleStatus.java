/**
 * 
 */
package ngat.astrometry;

import java.io.Serializable;

import ngat.net.telemetry.StatusCategory;

/**
 * @author eng
 *
 */
public class SolarCycleStatus implements Serializable, StatusCategory {

	public static final int DAY_TIME = 1;
	
	public static final int NIGHT_TIME = 2;
	
	/** Timestamp.*/
	private long timeStamp;
	
	/** Current solar-cycle state.*/
	private int state;

	/**
	 * @param timeStamp
	 */
	public SolarCycleStatus(long timeStamp) {
		super();
		this.timeStamp = timeStamp;
	}

	/* (non-Javadoc)
	 * @see ngat.net.telemetry.StatusCategory#getCategoryName()
	 */
	public String getCategoryName() {
		return "SOL";
	}

	/* (non-Javadoc)
	 * @see ngat.net.telemetry.StatusCategory#getStatusTimeStamp()
	 */
	public long getStatusTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

}
