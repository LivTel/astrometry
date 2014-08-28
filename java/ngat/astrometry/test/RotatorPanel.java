package ngat.astrometry.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ngat.astrometry.AstrometryException;
import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.CardinalPointingCalculator;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

public class RotatorPanel extends JPanel {

	public static final double IOFF = Math.toRadians(71.778);

	CardinalPointingCalculator cpc;
	AstrometrySiteCalculator astro;
	ISite site;

	double ra;
	double dec;
	long start;
	long duration;

	/**
	 * 
	 */
	public RotatorPanel(ISite site) {
		super(true);
		cpc = new BasicCardinalPointingCalculator(site);
		astro  =new BasicAstrometrySiteCalculator(site);
		this.site = site;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		
		boolean visible = false;
		try{	
			double minelev = astro.getMinimumAltitude(track, start, start+duration);			
			System.err.printf("Target min elev: %3.2f \n", Math.toDegrees(minelev));
		} catch (AstrometryException as2) {
			as2.printStackTrace();
		}
		int cv = 0;
		// run from 0 to 360
		for (int isky = 0; isky < 360; isky++) {

			double sky = Math.toRadians((double) isky);
			
			// see if the sky angle isky is feasible
			boolean feasible = false;
			try {
				System.err.printf("Check angle %3d from %tF %tT -> %tF %tT \n", 
						isky, start, start, (start+duration), (start+duration));
				feasible = cpc.isFeasibleSkyAngle(sky, target, IOFF, start, start + duration);			
			} catch (AstrometryException as) {
				as.printStackTrace();
			}
			
			visible = false;
			try{	
				double minelev = astro.getMinimumAltitude(track, start, start+duration);			
				visible = (minelev > Math.toRadians(25.0));
			} catch (AstrometryException as2) {
				as2.printStackTrace();
			}
			
			
			int iplot = isky;
			if (isky < 90)
				iplot = 90-isky;
			else
				iplot = 450-isky;
			
			if (feasible && visible) {
				g.setColor(Color.green);	
				cv++;
			} else {
				if (visible)
					g.setColor(Color.red);
				else
					g.setColor(Color.blue);
			}
			
			g.fillArc(0, 0, w, h, iplot, -1);
		}
		System.err.printf("Total feasible angles: %3d \n",cv);

	}

	public void update(double ra, double dec, long start, long duration) {
		this.ra = ra;
		this.dec = dec;
		this.start = start;
		this.duration = duration;

		repaint();

	}

	public static void main(String args[]) {

		BasicSite site = new BasicSite("obs", Math.toRadians(28.0), Math.toRadians(-17.0));
		RotatorPanel rp = new RotatorPanel(site);
		JFrame f = new JFrame("Cardinal pointing display");
		JComponent comp = (JComponent)f.getContentPane();
		comp.setLayout(new BorderLayout());
		
		
		
		
		
		f.getContentPane().add(rp);
		f.pack();
		f.setBounds(200,200,600,600);
		f.setVisible(true);
		
		//try {Thread.sleep(3000L);} catch (InterruptedException ix) {}
		rp.update(1.4, 0.4, System.currentTimeMillis(), 3600*1000L);
		

	}

}
