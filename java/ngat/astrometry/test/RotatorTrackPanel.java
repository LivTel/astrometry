/**
 * 
 */
package ngat.astrometry.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.CardinalPointingCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class RotatorTrackPanel extends JPanel {
	
	public static final double IOFF = Math.toRadians(77.118);

	BasicCardinalPointingCalculator cpc;
	AstrometrySiteCalculator astro;
	ISite site;

	double ra;
	double dec;
	long start;
	long duration;

	double skyAngle;
	
	/**
	 * 
	 */
	public RotatorTrackPanel(ISite site) {
		super(true);
		cpc = new BasicCardinalPointingCalculator(site);
		astro  =new BasicAstrometrySiteCalculator(site);
		this.site = site;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {	
		super.paint(g);
		
		XExtraSolarTarget target = new XExtraSolarTarget("Star");
		target.setRa(ra);
		target.setDec(dec);
		
		TargetTrackCalculator track = new BasicTargetCalculator(target, site);
		
		int w = getSize().height-50;
		int h = getSize().width-50;

		// outer ring color
		g.setColor(Color.black);
		g.drawOval(0, 0, w, h);
		
		try {
			// start
			double mount1 = cpc.getMountAngle(skyAngle, target, IOFF, start);
			double mount2 = cpc.getMountAngle(skyAngle, target, IOFF, start+duration);
			g.setColor(Color.magenta);
			g.drawLine(w/2, h/2, (int)((double)w*(1.0+Math.cos(mount1))/2), (int)((double)h*(1.0+Math.sin(mount1))/2));
			g.drawLine(w/2, h/2, (int)((double)w*(1.0+Math.cos(mount2))/2), (int)((double)h*(1.0+Math.sin(mount2))/2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		g.setColor(Color.green);
		long t = start;
		while (t < start + duration) {
		
			try {
				Coordinates c = track.getCoordinates(t);
				double alt = astro.getAltitude(c, t);
				double azm = astro.getAzimuth(c, t);
				double mount = cpc.getMountAngle(skyAngle, target, IOFF, t);
				double r = (double)(t-start)/(double)duration;
				double x = r*Math.cos(mount);
				double y = r*Math.sin(mount);
				
				g.drawOval((int)((double)w*(1.0+x)/2), (int)((double)h*(1.0+y)/2),2 ,2);
				System.err.printf("T %3.2f Azm: %3.2f Alt: %3.2f Mount: %3.2f \n", r, Math.toDegrees(azm), Math.toDegrees(alt), Math.toDegrees(mount));
			} catch (Exception e) {
				e.printStackTrace();
			}
			t += (long)(duration/100.0);
		}
		
	}
	
	public void update(double ra, double dec, long start, long duration, double sky) {
		this.ra = ra;
		this.dec = dec;
		this.start = start;
		this.duration = duration;
		this.skyAngle = sky;
		repaint();
	}
	
	public static void main(String args[]) {

		BasicSite site = new BasicSite("obs", Math.toRadians(28.0), Math.toRadians(-17.0));
		RotatorTrackPanel rp = new RotatorTrackPanel(site);
		JFrame f = new JFrame("Cardinal pointing display");
		JComponent comp = (JComponent)f.getContentPane();
		comp.setLayout(new BorderLayout());
		
		
		
		
		
		f.getContentPane().add(rp);
		f.pack();
		f.setBounds(200,200,600,600);
		f.setVisible(true);
		
		//try {Thread.sleep(3000L);} catch (InterruptedException ix) {}
		rp.update(Math.toRadians(180.0), Math.toRadians(27.9), System.currentTimeMillis(), 24400*1000L, 0.0);
		

	}

	
}
