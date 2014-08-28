/**
 * 
 */
package ngat.astrometry;

/**
 * @author eng
 *
 */
public interface ISite {

	public double getLatitude();
	
	public double getLongitude();
	
	public double getElevation();
	
	public String getSiteName();
	
	public String getSiteDescription();
	
	public String getSiteLocation();
	
}
