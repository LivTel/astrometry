/**
 * 
 */
package ngat.astrometry;

import javax.swing.text.Position;

/**
 * @author eng
 * 
 */
public class Coordinates {

	public static final int ICRF = 1;

	public static final int FK4 = 4;

	public static final int FK5 = 5;

	/** Right Ascension of coordinate position (rads). */
	private double ra;

	/** Declination of coordinate position (rads). */
	private double dec;

	/** Epoch of position measurement. */
	private double epoch;

	/** Equinox of coordinate system. */
	private double equinox;

	/** Coordinate frame type. */
	private int frame;

	/**
	 * @param ra
	 * @param dec
	 */
	public Coordinates(double ra, double dec) {
		this.ra = ra;
		this.dec = dec;
	}

	/**
	 * @return the ra
	 */
	public double getRa() {
		return ra;
	}

	/**
	 * @param ra
	 *            the ra to set
	 */
	public void setRa(double ra) {
		this.ra = ra;
	}

	/**
	 * @return the dec
	 */
	public double getDec() {
		return dec;
	}

	/**
	 * @param dec
	 *            the dec to set
	 */
	public void setDec(double dec) {
		this.dec = dec;
	}

	/**
	 * @return the epoch
	 */
	public double getEpoch() {
		return epoch;
	}

	/**
	 * @param epoch
	 *            the epoch to set
	 */
	public void setEpoch(double epoch) {
		this.epoch = epoch;
	}

	/**
	 * @return the equinox
	 */
	public double getEquinox() {
		return equinox;
	}

	/**
	 * @param equinox
	 *            the equinox to set
	 */
	public void setEquinox(double equinox) {
		this.equinox = equinox;
	}

	/**
	 * @return the frame
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}

	public String getFrameDesignator(int frame) {
		switch (frame) {
		case FK4:
			return "FK4";
		case FK5:
			return "FK5";
		case ICRF:
			return "ICRF";
		}
		return "A";
	}

	public String getEquinoxDesignator(int frame) {
		switch (frame) {
		case FK4:
			return "B";
		case FK5:
			return "J";
		case ICRF:
			return "J";
		}
		return "A";
	}

	public String toString() {
		return "(" + AstroFormatter.formatHMS(ra, ":")+ "," +AstroFormatter.formatDMS(dec, ":") + ") " + getEquinoxDesignator(frame) + equinox
				+ "; " + getFrameDesignator(frame);
	}

}
