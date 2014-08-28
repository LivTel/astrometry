/**
 * 
 */
package ngat.astrometry;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * @author eng
 * 
 */
public class AstroFormatter {

	/**
	 * Format an angle in HMS format.
	 * 
	 * @param angle
	 *            The angle (rads).
	 * @param delim
	 *            A separator character.
	 * @return An HMS formatted representation of the angle. The sign will be
	 *         either space or -.
	 */
	public static String formatHMS(double angle, String delim) {
		double f = Math.toDegrees(angle) / 15;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(2);

		String sign = " ";
		if (f < 0.0f) {
			f = -f;
			sign = "-";
		}

		int hrs = (int) f;
		f = (f - (double) hrs) * 60;

		int mins = (int) f;
		f = (f - (double) mins) * 60;

		return new String(sign + hrs + delim + mins + delim + nf.format(f));

	}

	/**
	 * Format an angle in HMS format.
	 * 
	 * @param angle
	 *            The angle (rads).
	 * @param delim
	 *            A separator character.
	 * @return A DMS formatted representation of the angle. The sign will be
	 *         either space or -.
	 */
	public static String formatDMS(double angle, String delim) {
		double f = Math.toDegrees(angle);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		String sign = " ";
		if (f < 0.0f) {
			f = -f;
			sign = "-";
		}

		int deg = (int) f;
		f = (f - (double) deg) * 60.0;

		int mins = (int) f;
		f = (f - (double) mins) * 60.0;

		return new String(sign + deg + delim + mins + delim + nf.format(f));

	}
	
	 /** Parse a String containing a RA in the format hh:mm:ss.sss 
     * @param str The String to parse. 
     * @param delim The tokenizer delim to use.
     * @exception ParseException if the String is incorrectly formatted.*/
    public static double parseHMS(String str, String delim) throws ParseException {
       
        String arg = str.trim();

        int z = 1;
        arg = arg.trim();

        // Strip leading whitespace and look for a sign.
        if (arg.startsWith("+")) {
            z = 1;
            if (arg.length() > 1)
                arg = arg.substring(1).trim();
        } else if (arg.startsWith("-")) {
            z = -1;
            if (arg.length() > 1)
                arg = arg.substring(1).trim();
        }

        double v = 0.0;
          
        // Split into tokens.
        StringTokenizer tokenizer = new StringTokenizer(arg, delim+" ");
        if (tokenizer.countTokens() < 3)
            throw new ParseException("AstroFormatter: Parsing ["+arg+"] expected [<+/->]<hh>"+delim+"<mm>"+delim+"<ss.sss>", 0);

        try {
            int hh = Integer.parseInt(tokenizer.nextToken());
            if (hh < 0 || hh > 23)
                throw new ParseException("Illegal value (0 <= hours <= 23)",0);
            int mm = Integer.parseInt(tokenizer.nextToken());
            if (mm < 0 || mm > 59)
                throw new ParseException("Illegal value (0 <= minutes <= 59)",0);
            double ss = Double.parseDouble(tokenizer.nextToken());
            if ((int)ss < 0 || (int)ss > 60)
                throw new ParseException("Illegal value (0 <= seconds <= 60)",0);
            return (double)z * ((double)hh*3600.0 + (double)mm*60.0 + ss)/13750.98708;   
        } catch (NumberFormatException ne) {
            throw new ParseException("Illegal format: "+ne,0);
        }

    }

    /** Parse a String containing a Dec in the format zdd:mm:ss.sss 
     * @param str The String to parse.
     * @param delim The tokenizer token to use.
     * @exception ParseException if the String is incorrectly formatted.*/
    public static double parseDMS(String str, String delim) throws ParseException {
    	 String arg = str.trim();

         int z = 1;
         arg = arg.trim();

         // Strip leading whitespace and look for a sign.
         if (arg.startsWith("+")) {
             z = 1;
             if (arg.length() > 1)
                 arg = arg.substring(1).trim();
         } else if (arg.startsWith("-")) {
             z = -1;
             if (arg.length() > 1)
                 arg = arg.substring(1).trim();
         }

         double v = 0.0;

        // Split into tokens.
        StringTokenizer tokenizer = new StringTokenizer(arg, delim+" ");
        if (tokenizer.countTokens() < 3)
            throw new ParseException("AstroFormatter: Parsing ["+arg+"] expected [<+/->]<dd>"+delim+"<mm>"+delim+"<ss.sss>", 0);

        try {
            int dd = Integer.parseInt(tokenizer.nextToken());
            if (dd < -359 || dd > 359 || dd < -359)
                throw new ParseException("Illegal value (-359 <= degrees <= +/-359)",0);
            int mm = Integer.parseInt(tokenizer.nextToken());
            if (mm < 0 || mm > 59)
                throw new ParseException("Illegal value (0 <= minutes <= 59)",0);
            double ss = Double.parseDouble(tokenizer.nextToken());
            if ((int)ss < 0 || ((int)ss > 60))
                throw new ParseException("Illegal value (0 <= seconds <= 60)",0);
            return (double)z * ((double)dd*3600.0+(double)mm*60.0+ss)/206264.8062;     
        } catch (NumberFormatException ne) {
            throw new ParseException("Illegal format: "+ne,0);
        }

    }


}
