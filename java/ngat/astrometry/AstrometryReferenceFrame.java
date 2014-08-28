package ngat.astrometry;

public class AstrometryReferenceFrame {

    public static final int FK4 = 4;

    public static final int FK5 = 5;

    public static final int ICRF = 1;

    public static String toString(int frame) {
	switch (frame) {
	case FK4:
	    return "FK4";
	case FK5:
	    return "FK5";
	case ICRF:
	    return "ICRF";
	default:
	    return "UNKNOWN("+frame+")";
	}

    }

}
