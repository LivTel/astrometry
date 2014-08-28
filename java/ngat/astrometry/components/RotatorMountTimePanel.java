/**
 * 
 */
package ngat.astrometry.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.AstroLib;
import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.BasicTargetTrackCalculatorFactory;
import ngat.astrometry.CardinalPointingCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.JAstroSlalib;
import ngat.astrometry.SolarCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.TargetTrackCalculatorFactory;
import ngat.astrometry.approximate.AlternativeTargetTrackCalculatorFactory;
import ngat.astrometry.approximate.BasicAstroLibImpl;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;
import ngat.util.logging.MysqlLogHandler;

/**
 * @author eng
 * 
 */
public class RotatorMountTimePanel extends JPanel {

    public static final double IOFF = Math.toRadians(104.0);
    public static SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

    public static final Color TARGET_SET = new Color(228,230,133); // A
    //public static final Color TARGET_SET = new Color(178, 125, 151); // B
    public static final Color SUNRISE = new Color(255,203,101); // A
    //public static final Color SUNRISE = new Color(255, 145, 71); // B
    public static final Color INFEASIBLE = new Color(232,162,138); // A
    //public static final Color INFEASIBLE = new Color(255, 112, 112); // B
    public static final Color FEASIBLE = new Color(160,217,119); // A
    //public static final Color FEASIBLE = new Color(131, 255, 89); // B

    // RotatorPositionSelectionListener

    /**
     * A list of RotatorPositionSelectionListeners to update when user moves
     * mouse over panel.
     */
    List listeners;

    AstroLib astroLib;
    CardinalPointingCalculator cpc;
    AstrometrySiteCalculator astro;
    SolarCalculator sunTrack;
    ISite site;

    double instOff;
	
    TargetTrackCalculator track;
    long start;
    long end;
    long duration;

    public RotatorMountTimePanel(ISite site, AstroLib astroLib, TargetTrackCalculatorFactory tcf, double instOff) {
	super(true);
	this.site = site;
	this.astroLib = astroLib;
	this.instOff = instOff;
	AstrometryCalculator ast = new BasicAstrometryCalculator(astroLib);
	astro = new BasicAstrometrySiteCalculator(site, ast);
	cpc = new BasicCardinalPointingCalculator(site, astro, tcf);
	sunTrack = new SolarCalculator(astroLib);

	sdf.setTimeZone(UTC);
	fdf.setTimeZone(UTC);
	ddf.setTimeZone(UTC);

	listeners = new Vector<RotatorPositionSelectionListener>();

	addMouseMotionListener(new MouseMotionAdapter() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.Mousevent
		 * )
		 */
		@Override
		    public void mouseMoved(MouseEvent e) {
		    super.mouseMoved(e);

		    int h = getSize().height - 80;
		    int w = getSize().width - 180;
		    long t = start + (long) ((double) (e.getX() - 40) * (double) (end - start) / (double) w);
		    double mount = (double) (e.getY() - 40) * 360.0 / (double) h;

		    System.err.println("Mouse at: " + sdf.format(new Date(t)) + ", " + mount);

		    // notify any registered listeners
		    Iterator<RotatorPositionSelectionListener> il = listeners.iterator();
		    while (il.hasNext()) {
			try {
			    RotatorPositionSelectionListener l = il.next();
			    l.rotatorSelection(t, mount);
			} catch (Exception ex) {
			    ex.printStackTrace();
			}
		    }
		}

	    });

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
	super.paint(g);

	// draw a box inset by 25,25
	int h = getSize().height - 80;
	int w = getSize().width - 180;
	g.setColor(Color.black);
	g.drawRect(40, 40, w, h);

	g.setFont(new Font("serif", Font.PLAIN, 10));
	// draw time axis
	for (int i = 0; i < w; i += w / 10) {
	    long t = start + (long) ((double) i * (double) (end - start) / (double) w);
	    g.drawString(ddf.format(new Date(t)), i + 40, h + 55);
	    g.drawString(sdf.format(new Date(t)), i + 40, h + 65);
	}

	// draw mount angle axis
	double imou = -90.0;
	while (imou <= 90.0) {
	    g.drawString(String.format("%4.1f", imou), 0, 40 + (int) ((imou + 90) * (double) h / 180.0));
	    imou += 45.0;
	}
		
		
	// draw key
	g.setFont(new Font("helvetica", Font.PLAIN, 12));
	int bw = Math.min(33, h/7);
	g.setColor(INFEASIBLE);
	g.fillRect(60+w, 40, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40, bw, bw);
	g.drawString("No solution", 80+w+bw, 60);
		
	g.setColor(FEASIBLE);
	g.fillRect(60+w, 40+2*h/7, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+2*h/7, bw, bw);
	g.drawString("Feasible", 80+w+bw, 60+2*h/7);
		
	g.setColor(SUNRISE);
	g.fillRect(60+w, 40+4*h/7, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+4*h/7, bw, bw);
	g.drawString("Daytime", 80+w+bw, 60+4*h/7);
		
	g.setColor(TARGET_SET);
	g.fillRect(60+w, 40+6*h/7, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+6*h/7, bw, bw);
	g.drawString("Set/will set", 80+w+bw, 60+6*h/7);
		
		

	Color color = null;
	int dw = (int) (w / 100);
	int dh = (int) (h / 100);
	// loop along x axis (time)
	for (int i = 0; i < w; i += dw) {

	    long t = start + (long) ((double) i * (double) (end - start) / (double) w);

	    try {
		Coordinates c = track.getCoordinates(t);
		double minalt = astro.getMinimumAltitude(track, t, t + duration);
		boolean visible = minalt > Math.toRadians(25.0);
		// System.err.printf("Min alt at: %tF %tT for %10d is %4.2f \n",t,
		// t, duration, Math.toDegrees(minalt));

		Coordinates sun = sunTrack.getCoordinates(t);
		double sunlev = astro.getAltitude(sun, t);

		Coordinates targetTrack = track.getCoordinates(t);
		XExtraSolarTarget target = new XExtraSolarTarget();
		target.setRa(targetTrack.getRa());
		target.setDec(targetTrack.getDec());

		// loop along y axis (rot pos)
		for (int j = 0; j < h; j += dh) {

		    double mou = Math.toRadians(-90.0 + (double) j * 180.0 / (double) h);
		    double sky = cpc.getSkyAngle(mou, target, IOFF-instOff, t);

		    // check if this is a feasible mount angle
		    boolean feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF-instOff, t, t + duration);

		    //

		    if (sunlev > 0.0) {
			color = SUNRISE;
		    } else {
			if (!visible) {
			    color = TARGET_SET;
			} else {
			    if (feasible)
				color = FEASIBLE;
			    else
				color = INFEASIBLE;
			}
		    }
		    g.setColor(color);
		    // plot a square at (i,j)
		    g.fillRect(40 + i, 40 + j, dw, dh);

		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	// draw mou points
	g.setColor(Color.black);
	g.drawLine(40, 40 + h / 4, 40 + w, 40 + h / 4);
	g.drawLine(40, 40 + h / 2, 40 + w, 40 + h / 2);
	g.drawLine(40, 40 + 3 * h / 4, 40 + w, 40 + 3 * h / 4);
		
	// draw bounding box over everything else
	g.setColor(Color.black);
	g.drawRect(40, 40, w, h);
    }

    public void update(TargetTrackCalculator track, long start, long end, long duration) {
	this.track = track;
	this.start = start;
	this.end = end;
	this.duration = duration;

	repaint();

    }

    public void addRotatorPositionSelectionListener(RotatorPositionSelectionListener rpl) {
	if (listeners.contains(rpl))
	    return;
	listeners.add(rpl);
    }

    public static void main(String args[]) {

	BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

	try {
	    ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

	    boolean alt = (cfg.getProperty("alt") != null);

	    TargetTrackCalculatorFactory tcf = null;
	    RotatorMountTimePanel rtp = null;

	    double ioff = Math.toRadians(cfg.getDoubleValue("ioff"));
			
	    if (alt) {
		tcf = new AlternativeTargetTrackCalculatorFactory();
		rtp = new RotatorMountTimePanel(site, new BasicAstroLibImpl(), tcf, ioff);
	    } else {
		tcf = new BasicTargetTrackCalculatorFactory();
		rtp = new RotatorMountTimePanel(site, new JAstroSlalib(), new BasicTargetTrackCalculatorFactory(), ioff);
	    }

	    long t1 = (fdf.parse(cfg.getProperty("start"))).getTime();
	    long t2 = (fdf.parse(cfg.getProperty("end"))).getTime();
	    double ra = AstroFormatter.parseHMS(cfg.getProperty("ra"), ":"); // hh:mm:ss
	    double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":"); // dd:mm:ss
	    XExtraSolarTarget target = new XExtraSolarTarget();
	    target.setRa(ra);
	    target.setDec(dec);
	    double duration = 3600 * 1000 * cfg.getDoubleValue("duration"); // hours

	    JFrame f = new JFrame("Mount angle display");
	    JComponent comp = (JComponent) f.getContentPane();
	    comp.setLayout(new BorderLayout());

	    f.getContentPane().add(rtp);
	    f.pack();
	    f.setBounds(200, 200, 880, 440);
	    f.setVisible(true);

	    TargetTrackCalculator track = tcf.getTrackCalculator(target, site);
	    rtp.update(track, t1, t2, (long) duration);

	    System.err.printf("Target at: [%4.2f h, %4.2f ]", Math.toDegrees(ra) / 15, Math.toDegrees(dec));
	    System.err.printf("Duration %10.2f ms \n", duration);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
