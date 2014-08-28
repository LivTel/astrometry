/**
 * 
 */
package ngat.astrometry.test;

import ngat.astrometry.AstroFormatter;

/**
 * @author eng
 *
 */
public class ReadDMS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String dms = args[0];
		
		System.err.println("Reading: ["+dms+"]");
		
		try {
			double angle = AstroFormatter.parseDMS(dms, " ");
		
			System.err.println("Parsed to: "+angle);
			
			double angdeg = Math.toDegrees(angle);
			
			System.err.println("In deg: "+angdeg);
				
			String todms = AstroFormatter.formatDMS(angle, ":");	
				
			System.err.println("Formatted to: ["+todms+"]");
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
