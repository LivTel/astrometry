/**
 * 
 */
package ngat.astrometry;

import java.io.Serializable;

/**
 * @author eng
 *
 */
public class BasicSite implements ISite, Serializable {

	private String siteName;
	
	private String siteDescription;
	
	private String siteLocation;
	
	/**
	 * @return the siteDescription
	 */
	public String getSiteDescription() {
		return siteDescription;
	}



	/**
	 * @param siteDescription the siteDescription to set
	 */
	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}



	/**
	 * @return the siteLocation
	 */
	public String getSiteLocation() {
		return siteLocation;
	}



	/**
	 * @param siteLocation the siteLocation to set
	 */
	public void setSiteLocation(String siteLocation) {
		this.siteLocation = siteLocation;
	}

	private double latitude;
	
	private double longitude;
	
	private double elevation;
	
	/**
	 * @param siteName
	 * @param latitude
	 * @param longitude
	 */
	public BasicSite(String siteName, double latitude, double longitude) {
		this.siteName = siteName;
		this.latitude = latitude;
		this.longitude = longitude;
	}



	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}



	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}



	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}



	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}



	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	/**
	 * @return the elevation
	 */
	public double getElevation() {
		return elevation;
	}



	/**
	 * @param elevation the elevation to set
	 */
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

    public String toString() {
	return siteName+" ("+	    
	    String.format("%2.2f", Math.toDegrees(latitude > 0.0 ? latitude:-latitude))+
	    (latitude > 0.0 ? "N":"S")+", "+
	    String.format("%2.2f", Math.toDegrees(longitude > 0.0 ? longitude:-longitude))+
	    (longitude > 0.0 ? "E":"W")+")";
	
		// e.g. La Palma (28.9N, 17.9W)
    }
	
}
