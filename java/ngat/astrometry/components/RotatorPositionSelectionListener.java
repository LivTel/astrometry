package ngat.astrometry.components;

public interface RotatorPositionSelectionListener {

	/** A rotator position was selected at time.*/
	public void rotatorSelection(long time, double rotator);
	
}
