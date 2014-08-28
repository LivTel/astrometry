/**
 * 
 */
package ngat.astrometry.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.Position;

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

/**
 * @author eng
 * 
 */
public class RotatorSkyTimePanel extends JPanel {

	public static final double IOFF = Math.toRadians(104.0);
	public static SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static final Color TARGET_SET = new Color(228, 230, 133); // A
	// public static final Color TARGET_SET = new Color(178, 125, 151); // B
	public static final Color SUNRISE = new Color(255, 203, 101); // A
	// public static final Color SUNRISE = new Color(255, 145, 71); // B
	public static final Color SUNRISING = new Color(225, 203, 101); // A
	// public static final Color SUNRISING = new Color(255, 145, 71); // B
	public static final Color INFEASIBLE = new Color(232, 162, 138); // A
	// public static final Color INFEASIBLE = new Color(255, 112, 112); // B
	public static final Color FEASIBLE = new Color(160, 217, 119); // A
	// public static final Color FEASIBLE = new Color(131, 255, 89); // B

	/**
	 * A list of RotatorPositionSelectionListeners to update when user moves
	 * mouse over panel.
	 */
	List<RotatorPositionSelectionListener> listeners;

	AstroLib astroLib;
	CardinalPointingCalculator cpc;
	AstrometrySiteCalculator astro;
	SolarCalculator sunTrack;
	ISite site;

	TargetTrackCalculator track;
	long start;
	long end;
	long duration;

	double instOffset;

	public RotatorSkyTimePanel(ISite site, AstroLib astroLib, TargetTrackCalculatorFactory tcf, double instOffset) {
		super(true);
		this.site = site;
		this.astroLib = astroLib;
		this.instOffset = instOffset;
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
			// ENDENDENDENDNENDNENDNENDNENDNENDN
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				// System.err.println("Mouse at: ["+e.getX()+","+e.getY());

				int h = getSize().height - 80;
				int w = getSize().width - 180;
				long t = start + (long) ((double) (e.getX() - 40) * (double) (end - start) / (double) w);
				double sky = (double) (e.getY() - 40) * 360.0 / (double) h;

				System.err.println("Mouse at: " + sdf.format(new Date(t)) + ", " + sky);

				for (int i = 0; i < listeners.size(); i++) {
					RotatorPositionSelectionListener l = listeners.get(i);
					try {
						l.rotatorSelection(t, sky);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}

		});

	}

	@Override
	public void paint(Graphics g) {
	super.paint(g);
	
	// draw a box inset 
	int h = getSize().height - 80;
	int w = getSize().width - 180;
	g.setColor(Color.black);
	g.drawRect(40, 40, w, h);
	
	g.setFont(new Font("serif", Font.PLAIN, 10));
	// draw time axis - assume for now its in the hours range
	
	// find the most recent hour before end time
	Calendar ecalendar = Calendar.getInstance();
	ecalendar.setTimeInMillis(end);
	int ehour = ecalendar.get(Calendar.HOUR_OF_DAY);
	ecalendar.set(Calendar.MINUTE, 0);
	ecalendar.set(Calendar.SECOND, 0);
	ecalendar.set(Calendar.MILLISECOND, 0);
		
	Calendar scalendar = Calendar.getInstance();
	scalendar.setTimeInMillis(start);
	int shour = scalendar.get(Calendar.HOUR_OF_DAY) + 1;
	scalendar.set(Calendar.HOUR_OF_DAY, shour);
	scalendar.set(Calendar.MINUTE, 0);
	scalendar.set(Calendar.SECOND, 0);
	scalendar.set(Calendar.MILLISECOND, 0);
	
	long range = end - start;
		
	// TODO WE CANNOT COPE WITH DAY WRAPS
	// where shour > ehour
	
	scalendar.set(Calendar.HOUR_OF_DAY, 0);
	long t0 = scalendar.getTimeInMillis();
	
	// draw a base line
	g.setColor(Color.black);
	g.drawLine(0, 3 * h / 4, w, 3 * h / 4);
		
	// stick the month in the middle
	//g.drawString(monthdf.format((start+end)/2), ww/2, hh);
	
	long t1 = 0L;
	long t2 = 0L;
	long t3 = 0L;
	
	if (range >= 24*3600*1000L) {
	    t1 = 6*3600*1000L;
	    t2 = 2*3600*1000L;
	    t3 = 1*3600*1000L;
	} else if
	      (range >= 12*3600*1000L) {
	    t1 = 3*3600*1000L;
	    t2 = 1*3600*1000L;
	    t3 = 1800*1000L;
	} else if
	      (range >= 6*3600*1000L) {
	    t1 = 2*3600*1000L;
	    t2 = 1*3600*1000L;
	    t3 = 900*1000L;
	} else if
	      (range >= 3*3600*1000L) {
	    t1 = 1*3600*1000L;
	    t2 = 1800*1000L;
	    t3 = 300*1000L;
	} else if
	      (range >= 1*3600*1000L) {
	    t1 = 1800*1000L;
	    t2 = 600*1000L;
	    t3 = 120*1000L;
	} else if
	      (range >= 1800*1000L) {
	    t1 = 600*1000L;
	    t2 = 300*1000L;
	    t3 = 60*1000L;
	} else if
	      (range >= 600*1000L) {
	    t1 = 300*1000L;
	    t2 = 120*1000L;
	    t3 = 60*1000L;
	} else {
	    t1 = 120*1000L;
	    t2 = 60*1000L;
	    t3 = 30*1000L;
	}
	
	long t = t0;
	while (t <= end) {
	    // Primary		   
	    double x = 40 + ((double)(t - start)/(double)(end - start)*(w));
	    g.drawString(ddf.format(new Date(t)), (int)x, h + 55);
	    g.drawString(sdf.format(new Date(t)), (int)x, h + 65);
	    t = t + t1;
	}
		
	t = t0;
	while (t <= end) {
	    // Secondary
	    int x = (int) ((double) w * (double) (t - start) / (end - start));
	    g.drawLine(x, h / 2, x, 3 * h / 4);
	    t = t + t2;
	}
	t = t0;
	while (t <= end) {
	    // Tertiary
	    int x = (int) ((double) w * (double) (t - start) / (end - start));
	    g.drawLine(x, 5*h/ 8, x, 3 * h / 4);
	    t = t + t3;
	}
	
	// draw sky angle axis
	double isky = 0.0;
	while (isky <= 360.0) {
	    // y = sky/360*h
	    g.drawString(String.format("%4.1f", isky), 0, 40 + (int) (isky * (double) h / 360.0));
	    isky += 45.0;
	}
		
	// draw key
	g.setFont(new Font("helvetica", Font.PLAIN, 12));
	int bw = Math.min(33, h/9);
	g.setColor(INFEASIBLE);
	g.fillRect(60+w, 40, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40, bw, bw);
	g.drawString("No solution", 80+w+bw, 60);
		
	g.setColor(FEASIBLE);
	g.fillRect(60+w, 40+2*h/9, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+2*h/7, bw, bw);
	g.drawString("Feasible", 80+w+bw, 60+2*h/9);
		
	g.setColor(SUNRISE);
	g.fillRect(60+w, 40+4*h/9, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+4*h/7, bw, bw);
	g.drawString("Daytime", 80+w+bw, 60+4*h/9);
		
	g.setColor(SUNRISING);
	g.fillRect(60+w, 40+6*h/9, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+4*h/7, bw, bw);
	g.drawString("Sun rising", 80+w+bw, 60+6*h/9);
		
		
	g.setColor(TARGET_SET);
	g.fillRect(60+w, 40+8*h/9, bw, bw);
	g.setColor(Color.black);
	//g.drawRect(60+w, 40+6*h/7, bw, bw);
	g.drawString("Set/will set", 80+w+bw, 60+8*h/9);
		
	Color color = null;
	int dw = (int) (w / 100);
	int dh = (int) (h / 100);
	// loop along x axis (time)
	for (int i = 0; i < w; i += dw) {
	
	    t = start + (long) ((double) i * (double) (end - start) / (double) w);
	
	    try {
		Coordinates c = track.getCoordinates(t);
		double minalt = astro.getMinimumAltitude(track, t, t + duration);
		boolean visible = minalt > Math.toRadians(25.0);
	
		Coordinates sun = sunTrack.getCoordinates(t);
		double sunlev = astro.getAltitude(sun, t);
				
		// will the sun have risen before weve completed
		double sunlevdur = astro.getAltitude(sun, t+duration);
				
		Coordinates targetTrack = track.getCoordinates(t);
		XExtraSolarTarget target = new XExtraSolarTarget();
		target.setRa(targetTrack.getRa());
		target.setDec(targetTrack.getDec());
				
		// loop along y axis (rot pos)
		for (int j = 0; j < h; j += dh) {
		  
			// double rot = Math.toRadians(-90.0 + (double) j * 180.0 /
			// (double) h);
	
			double sky = Math.toRadians((double) j * 360.0 / (double) h);
	
		    // check if this is a feasible mount angle
		    // double sky = cpc.getSkyAngle(rot, target, IOFF, t);
		    boolean feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF-instOffset, t, t + duration);
	
		    // System.err.printf("Check [%4d , %4d] : at %tT - %4.2f %s %s \n",
		    // i, j, t, Math.toDegrees(sky),
		    // (visible? "V/":"NV/"), (feasible?"F":"NF"));
	
		    // color selector
	
		    if (sunlev > 0.0) {
			color = SUNRISE;
		    } else {
			if (sunlevdur > 0.0) {
			    color = SUNRISING;
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
		    }
					
					
		    g.setColor(color);
		    // plot a square at (i,j)
		    g.fillRect(40 + i, 40 + j, dw, dh);
	
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	// draw cardinal points
	g.setColor(Color.black);
	g.drawLine(40, 40 + h / 4, 40 + w, 40 + h / 4);
	g.drawLine(40, 40 + h / 2, 40 + w, 40 + h / 2);
	g.drawLine(40, 40 + 3 * h / 4, 40 + w, 40 + 3 * h / 4);
	
	// draw bounding box over everything else
	g.setColor(Color.black);
	g.drawRect(40, 40, w, h);
    }

	public void addRotatorPositionSelectionListener(RotatorPositionSelectionListener rpl) {
		if (listeners.contains(rpl))
			return;
		listeners.add(rpl);
	}

	public void update(TargetTrackCalculator track, long start, long end, long duration) {
		this.track = track;
		this.start = start;
		this.end = end;
		this.duration = duration;

		repaint();

	}

	public static void main(String args[]) {

		TimeZone.setDefault(UTC);
		fdf.setTimeZone(UTC);

		Map<String, Double> iomap = new HashMap<String, Double>();
		iomap.put("IO:O", Math.toRadians(0.0));
		iomap.put("RISE", Math.toRadians(-44.4));
		iomap.put("RINGO3", Math.toRadians(-87.8));
		iomap.put("FRODO", Math.toRadians(0.0));

		BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

		try {
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			boolean alt = (cfg.getProperty("alt") != null);

			TargetTrackCalculatorFactory tcf = null;
			RotatorSkyTimePanel rtp = null;

			// double instOffset = Math.toRadians(cfg.getDoubleValue("ioff"));

			String instName = cfg.getProperty("inst");
			if (instName == null)
				throw new IllegalArgumentException("No instrument supplied: IO:O RISE RINGO3 FRODO");

			if (!iomap.containsKey(instName))
				throw new IllegalArgumentException("Unknown instrument: " + instName + " use IO:O RISE RINGO3 FRODO");

			double instOffset = iomap.get(instName);

			System.err.println("RTP: Using inst offset: " + Math.toDegrees(instOffset) + " deg for " + instName);

			if (alt) {
				tcf = new AlternativeTargetTrackCalculatorFactory();
				rtp = new RotatorSkyTimePanel(site, new BasicAstroLibImpl(), tcf, instOffset);
			} else {
				tcf = new BasicTargetTrackCalculatorFactory();
				rtp = new RotatorSkyTimePanel(site, new JAstroSlalib(), new BasicTargetTrackCalculatorFactory(),
						instOffset);
			}

			long t1 = (fdf.parse(cfg.getProperty("start"))).getTime();
			long t2 = (fdf.parse(cfg.getProperty("end"))).getTime();
			String stra = cfg.getProperty("ra");
			System.err.println("RTP: Read property [ra] : " + stra);
			double ra = AstroFormatter.parseHMS(stra, ":");
			double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":");
			XExtraSolarTarget target = new XExtraSolarTarget();
			target.setRa(ra);
			target.setDec(dec);
			double duration = 1000 * cfg.getDoubleValue("duration");

			JFrame f = new JFrame("Sky angle display");
			JComponent comp = (JComponent) f.getContentPane();
			comp.setLayout(new BorderLayout());

			f.getContentPane().add(rtp);
			f.pack();
			f.setBounds(200, 200, 880, 440);
			f.setVisible(true);

			TargetTrackCalculator track = tcf.getTrackCalculator(target, site);
			rtp.update(track, t1, t2, (long) duration);

			System.err.printf("Target at: [%4.2f h, %4.2f ]", Math.toDegrees(ra) / 15, Math.toDegrees(dec));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
