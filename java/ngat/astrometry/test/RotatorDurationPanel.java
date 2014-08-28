/**
 * 
 */
package ngat.astrometry.test;

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
public class RotatorDurationPanel extends JPanel {

	public static final double IOFF = Math.toRadians(71.778);
	public static SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	// public static final Color TARGET_SET = new Color(228,230,133);
	public static final Color TARGET_SET = Color.yellow.darker();
	// public static final Color SUNRISE = new Color(255,203,101);
	public static final Color SUNRISE = Color.yellow;
	// public static final Color INFEASIBLE = new Color(232,162,138);
	public static final Color INFEASIBLE = Color.red;
	// public static final Color FEASIBLE = new Color(160,217,119);
	public static final Color FEASIBLE = Color.green;

	public static final int UNKNOWN_STATE    = 0;
	public static final int OKAY_STATE       = 1;
	public static final int INFEASIBLE_STATE = 2;
	
	
	public static final long MAX_DURATION = 4 * 3600 * 1000L; // 4 hours

	// RotatorPositionSelectionListener

	/**
	 * A list of RotatorPositionSelectionListeners to update when user moves
	 * mouse over panel.
	 */
	// List listeners;

	AstroLib astroLib;
	CardinalPointingCalculator cpc;
	AstrometrySiteCalculator astro;
	SolarCalculator sunTrack;
	ISite site;

	TargetTrackCalculator track;
	long start;
	long end;

	public RotatorDurationPanel(ISite site, AstroLib astroLib, TargetTrackCalculatorFactory tcf) {
		super(true);
		this.site = site;
		this.astroLib = astroLib;
		AstrometryCalculator ast = new BasicAstrometryCalculator(astroLib);
		astro = new BasicAstrometrySiteCalculator(site, ast);
		cpc = new BasicCardinalPointingCalculator(site, astro, tcf);
		sunTrack = new SolarCalculator(astroLib);

		sdf.setTimeZone(UTC);
		fdf.setTimeZone(UTC);
		ddf.setTimeZone(UTC);

		// listeners = new Vector<RotatorPositionSelectionListener>();

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
				int w = getSize().width - 80;
				long t = start + (long) ((double) (e.getX() - 40) * (double) (end - start) / (double) w);
				double mount = (double) (e.getY() - 40) * 360.0 / (double) h;

				System.err.println("Mouse at: " + sdf.format(new Date(t)) + ", " + mount);

				// notify any lsiteners
				/*
				 * Iterator<RotatorPositionSelectionListener> il =
				 * listeners.iterator(); while (il.hasNext()) { try {
				 * RotatorPositionSelectionListener l = il.next();
				 * l.rotatorSelection(t, mount); } catch (Exception ex) {
				 * ex.printStackTrace(); } }
				 */
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
		int w = getSize().width - 140;
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

		// draw key, split into n colors
		g.drawRect(w + 60, 40, 20, h);
		for (int i = 0; i < h; i++) {
			// calculate duration as frac of height
			long duration = (long) ((double) i * (double) MAX_DURATION / (double) h);
			Color color = selectColor(duration);
			g.setColor(color);
			g.fillRect(w + 60, 40 + h - i, 20, 1);
		}
		// key labels, 10 over full range
		g.setColor(Color.black);
		long kduration = 0L;
		int dt = (int) ((double) MAX_DURATION / 10.0);
		while (kduration < MAX_DURATION) {
			// calculate height fraction
			int y = 40 + h - (int) ((double) kduration * (double) h / (double) MAX_DURATION);
			String label = String.format("%4.1f", ((double) kduration / 60000.0));
			g.drawString(label, w + 90, y);
			kduration += (long) dt;
		}

		Color color = null;
		int dw = (int) (w / 100);
		int dh = (int) (h / 100);
		long dtw = (end - start)/100;

		// loop along y axis (rot pos)
		for (int j = 0; j < h; j += dh) {

			double mou = Math.toRadians(-90.0 + (double) j * 180.0 / (double) h);

			// loop along x axis (time)
			
			int state = INFEASIBLE_STATE; // we dont know yet if the first time is feasible
			long feasibleFor = 0L;
			
			long t = start;
		//	for (int i = 0; i < w; i += dw) {

			while (t < end) {
				
				//long t = start + (long) ((double) i * (double) (end - start) / (double) w);

				try {
					Coordinates c = track.getCoordinates(t);

					Coordinates sun = sunTrack.getCoordinates(t);
					double sunlev = astro.getAltitude(sun, t);

					Coordinates targetTrack = track.getCoordinates(t);
					XExtraSolarTarget target = new XExtraSolarTarget();
					target.setRa(targetTrack.getRa());
					target.setDec(targetTrack.getDec());

					double sky = cpc.getSkyAngle(mou, target, IOFF, t);
					
					// lets see how long we can observe this target starting at
					// 0 minutes

					boolean canStillObserve = true;
					boolean visible = true;
					double minalt = 0.0;
					boolean sunset = (sunlev < 0.0);

					long last = 0L;
					long duration = 0L;
					long dd = 60000L;
					while (canStillObserve) {

						minalt = astro.getMinimumAltitude(track, t, t + duration);
						visible = minalt > Math.toRadians(25.0);

						// check if this is a feasible mount angle for tmax
						boolean feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF, t, t + duration);

						if ((!visible) || (!feasible) || (!sunset)) {
							// cant observe at duration

							// if we fell over near here were done
							if (Math.abs(duration - last) < 300000L) {
								canStillObserve = false;
							} else {
								// record where we fell-over
								last = duration;
								// backoff search offset
								duration -= dd;
								dd = 60000L;
							}

						} else {
							// not found limit yet, bump up search offset
							dd *= 2;
							duration += dd;
						}

					}

					System.err.printf("At: (%tT %tF,  Mou: %4.2f) MinAlt: %4.2f Vis: %b Sunset: %b Max = %6.2f m \n",
							t, t, mou, Math.toDegrees(minalt), visible, sunset, ((double) duration / 60000.0));
					
					while (duration > dtw) {
					
						color = selectColor(duration);

						g.setColor(color);
						// plot a square at (i,j)					
						int i = (int)((double)(t -start)* (double) w / (double) (end - start));
				
						g.fillRect(40 + i, 40 + j, dw, dh);
				
						duration -= dtw;
						t += dtw;
					}
					t += dtw;
				} catch (Exception e) {
					e.printStackTrace();
				}

			} // next time

		} // next rot position

		// draw mou points
		g.setColor(Color.black);
		g.drawLine(40, 40 + h / 4, 40 + w, 40 + h / 4);
		g.drawLine(40, 40 + h / 2, 40 + w, 40 + h / 2);
		g.drawLine(40, 40 + 3 * h / 4, 40 + w, 40 + 3 * h / 4);

	}

	public void update(TargetTrackCalculator track, long start, long end) {
		this.track = track;
		this.start = start;
		this.end = end;

		// repaint();

	}

	/*
	 * public void
	 * addRotatorPositionSelectionListener(RotatorPositionSelectionListener rpl)
	 * { if (listeners.contains(rpl)) return; listeners.add(rpl); }
	 */

	private Color selectColor(long duration) {
		int c = Math.min((int) (255.0 * (double) duration / ((double) MAX_DURATION)), 255);
		int d = Math.max(240 - c, 0);
		Color color = new Color(d, c, 0);
		// System.err.println("Duration: "+((double)duration/(3600.0*1000.0))+"h, Col: "+c);
		return color;
	}

	public static void main(String args[]) {

		BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

		TargetTrackCalculatorFactory tcf = new AlternativeTargetTrackCalculatorFactory();
		RotatorDurationPanel rtp = new RotatorDurationPanel(site, new BasicAstroLibImpl(), tcf);

		// TargetTrackCalculatorFactory tcf = new
		// BasicTargetTrackCalculatorFactory();
		// RotatorMountTimePanel rtp = new RotatorMountTimePanel(site, new
		// JAstroSlalib(), new BasicTargetTrackCalculatorFactory());
		try {
			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
			long t1 = (fdf.parse(cfg.getProperty("start"))).getTime();
			long t2 = (fdf.parse(cfg.getProperty("end"))).getTime();
			double ra = AstroFormatter.parseHMS(cfg.getProperty("ra"), ":");
			double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":");
			XExtraSolarTarget target = new XExtraSolarTarget();
			target.setRa(ra);
			target.setDec(dec);

			JFrame f = new JFrame("Mount angle display");
			JComponent comp = (JComponent) f.getContentPane();
			comp.setLayout(new BorderLayout());

			f.getContentPane().add(rtp);
			f.pack();
			f.setBounds(200, 200, 880, 440);
			f.setVisible(true);

			TargetTrackCalculator track = tcf.getTrackCalculator(target, site);
			rtp.update(track, t1, t2);

			System.err.printf("Target at: [%4.2f h, %4.2f ]", Math.toDegrees(ra) / 15, Math.toDegrees(dec));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
