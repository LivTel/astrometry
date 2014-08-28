/**
 * 
 */
package ngat.astrometry.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.swing.BoxLayout;
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
import ngat.phase2.IRotatorConfig;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class RotatorMountTimePanel2 extends JPanel {

	public static final double IOFF = Math.toRadians(104.0);

	public static SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static final Color TARGET_SET = new Color(228, 230, 133); // A
	// public static final Color TARGET_SET = new Color(178, 125, 151); // B
	public static final Color SUNRISE = new Color(255, 203, 101); // A
	// public static final Color SUNRISE = new Color(255, 145, 71); // B
	public static final Color INFEASIBLE = new Color(232, 162, 138); // A
	// public static final Color INFEASIBLE = new Color(255, 112, 112); // B
	public static final Color FEASIBLE = new Color(160, 217, 119); // A
	// public static final Color FEASIBLE = new Color(131, 255, 89); // B
	public static final Color SUNRISING = new Color(225, 203, 101); // A

	
	AstroLib astroLib;
	CardinalPointingCalculator cpc;
	AstrometrySiteCalculator astro;
	SolarCalculator sunTrack;
	ISite site;
	
	
	/** The rotator mode to display. */
	private int rotatorMode;
	
	/** The instrument offset.*/
	double instOffset;

	TargetTrackCalculator track;
	long start;
	long end;
	long duration;

	/**
     * 
     */
	public RotatorMountTimePanel2() {
		super();

		sdf.setTimeZone(UTC);
		fdf.setTimeZone(UTC);
		ddf.setTimeZone(UTC);

	}

	/**
	 * @return the instOff
	 */
	public double getInstOffset() {
		return instOffset;
	}

	/**
	 * @param instOff
	 *            the instOff to set
	 */
	public void setInstOffset(double instOff) {
		this.instOffset = instOff;
	}

	/**
	 * @return the rotatorMode
	 */
	public int getRotatorMode() {
		return rotatorMode;
	}

	/**
	 * @param rotatorMode
	 *            the rotatorMode to set
	 */
	public void setRotatorMode(int rotatorMode) {
		this.rotatorMode = rotatorMode;
	}

	public void setupCalculator(ISite site, AstroLib astroLib, TargetTrackCalculatorFactory tcf) {
		this.site = site;
		this.astroLib = astroLib;

		AstrometryCalculator ast = new BasicAstrometryCalculator(astroLib);
		astro = new BasicAstrometrySiteCalculator(site, ast);
		cpc = new BasicCardinalPointingCalculator(site, astro, tcf);
		sunTrack = new SolarCalculator(astroLib);

	}

	public void updateTrack(TargetTrackCalculator track, long start, long end, long duration) {
		this.track = track;
		this.start = start;
		this.end = end;
		this.duration = duration;

		repaint();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		System.err.println("Call paint");
		int h = getSize().height;
		int w = getSize().width;

		// special behaviour before astrolib is setup
		if (astroLib == null) {
			g.setColor(Color.black);
			g.drawLine(0, 0, w, h);
			g.drawLine(0, h, w, 0);
			return;
		}
		
		if (rotatorMode == IRotatorConfig.MOUNT)
			paintMount(g);
		else if
			(rotatorMode == IRotatorConfig.SKY)
			paintSky(g);

	}
	
	private void paintSky(Graphics g) {
		System.err.println("Call paintSky");
		// draw a box inset by 25,25
		int h = getSize().height;
		int w = getSize().width;
		h -= 80;
		w -= 180;
		
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
		
		    long t = start + (long) ((double) i * (double) (end - start) / (double) w);
		
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
		
	private void paintMount(Graphics g) {	
		System.err.println("Call paintMount");
		// draw a box inset by 25,25
		int h = getSize().height;
		int w = getSize().width;
		h -= 80;
		w -= 180;

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
		int bw = Math.min(33, h / 7);
		g.setColor(INFEASIBLE);
		g.fillRect(60 + w, 40, bw, bw);
		g.setColor(Color.black);
		// g.drawRect(60+w, 40, bw, bw);
		g.drawString("No solution", 80 + w + bw, 60);

		g.setColor(FEASIBLE);
		g.fillRect(60 + w, 40 + 2 * h / 7, bw, bw);
		g.setColor(Color.black);
		// g.drawRect(60+w, 40+2*h/7, bw, bw);
		g.drawString("Feasible", 80 + w + bw, 60 + 2 * h / 7);

		g.setColor(SUNRISE);
		g.fillRect(60 + w, 40 + 4 * h / 7, bw, bw);
		g.setColor(Color.black);
		// g.drawRect(60+w, 40+4*h/7, bw, bw);
		g.drawString("Daytime", 80 + w + bw, 60 + 4 * h / 7);

		g.setColor(TARGET_SET);
		g.fillRect(60 + w, 40 + 6 * h / 7, bw, bw);
		g.setColor(Color.black);
		// g.drawRect(60+w, 40+6*h/7, bw, bw);
		g.drawString("Set/will set", 80 + w + bw, 60 + 6 * h / 7);

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
					double sky = cpc.getSkyAngle(mou, target, IOFF - instOffset, t);

					// check if this is a feasible mount angle
					boolean feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF - instOffset, t, t + duration);

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


			// double instOffset = Math.toRadians(cfg.getDoubleValue("ioff"));

			String instName = cfg.getProperty("inst");
			if (instName == null)
				throw new IllegalArgumentException("No instrument supplied: IO:O RISE RINGO3 FRODO");

			if (!iomap.containsKey(instName))
				throw new IllegalArgumentException("Unknown instrument: " + instName + " use IO:O RISE RINGO3 FRODO");

			double instOffset = iomap.get(instName);

			System.err.println("RTP: Using inst offset: " + Math.toDegrees(instOffset) + " deg for " + instName);

			String smount = cfg.getProperty("mode", "MOUNT");
			String strmode = "";
			boolean mount = false;
			if (smount.equalsIgnoreCase("mount")) {
				mount = true;	
				strmode = "Mount";
			} else {
				mount = false;
				strmode = "Sky";
			}
			
			RotatorMountTimePanel2 rtp1 = new RotatorMountTimePanel2();
			RotatorMountTimePanel2 rtp2 = new RotatorMountTimePanel2();
		
			rtp1.setRotatorMode(IRotatorConfig.MOUNT);			
			rtp2.setRotatorMode(IRotatorConfig.SKY);
			
			AstroLib lib = null;
			TargetTrackCalculatorFactory tcf = null;
			
			if (alt) {
				tcf = new AlternativeTargetTrackCalculatorFactory();
				lib =  new BasicAstroLibImpl();
			} else {
				tcf = new BasicTargetTrackCalculatorFactory();
				lib = new JAstroSlalib();
			}
			
			rtp1.setupCalculator(site, lib, tcf);			
			rtp1.setInstOffset(instOffset);
			
			rtp2.setupCalculator(site, lib, tcf);			
			rtp2.setInstOffset(instOffset);
			
			long t1 = (fdf.parse(cfg.getProperty("start"))).getTime();
			long t2 = (fdf.parse(cfg.getProperty("end"))).getTime();
			
			double ra  = AstroFormatter.parseHMS(cfg.getProperty("ra"), ":");
			double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":");
			
			XExtraSolarTarget target = new XExtraSolarTarget();
			target.setRa(ra);
			target.setDec(dec);
			
			double duration = 1000 * cfg.getDoubleValue("duration");// seconds -> ms

			JFrame f = new JFrame("Rotator angle display: "+strmode);
			JComponent comp = (JComponent) f.getContentPane();
			comp.setLayout(new BoxLayout(comp, BoxLayout.Y_AXIS));

			f.getContentPane().add(rtp1);
			f.getContentPane().add(rtp2);
			f.pack();
			f.setBounds(200, 200, 880, 900);
			f.setVisible(true);

			TargetTrackCalculator track = tcf.getTrackCalculator(target, site);
			rtp1.updateTrack(track, t1, t2, (long) duration);
			rtp2.updateTrack(track, t1, t2, (long) duration);

			System.err.printf("Target at: [%4.2f , %4.2f ]", Math.toDegrees(ra) / 15, Math.toDegrees(dec));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
