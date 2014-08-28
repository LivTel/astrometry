/**
 * 
 */
package ngat.astrometry.test;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.ContourPlotUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class RotatorGraphPanel extends JPanel {
	
	public static final double IOFF = Math.toRadians(77.118);
	
	XExtraSolarTarget star;
	ISite site;
	
	TargetTrackCalculator track;
	XYSeries data;
	AstrometrySiteCalculator astro;
	BasicCardinalPointingCalculator cpc;
	
	/**
	 * @param site
	 * @param star
	 * @param start
	 * @param duration
	 */
	public RotatorGraphPanel(ISite site) {
		super(true);
		this.site = site;
		star = new XExtraSolarTarget();
		track = new BasicTargetCalculator(star, site);	
		astro  =new BasicAstrometrySiteCalculator(site);
		cpc = new BasicCardinalPointingCalculator(site);
		
		// set up a graph thingy
		data = new XYSeries(new Double(0.0));
		XYSeriesCollection xys = new XYSeriesCollection(data);
		JFreeChart chart = ChartFactory.createXYLineChart("Rotator Angle", "Time", "Mount PA", xys, PlotOrientation.HORIZONTAL, false, false, false); 
		ChartPanel cp = new ChartPanel(chart);
				
		add(cp);
	}
	
	public void update(double ra, double dec, long start, long duration, double skyAngle) {
		star.setRa(ra);
		star.setDec(dec);
		
		data.clear();
		long t = start;
		while (t < start + duration) {
		
			try {
				Coordinates c = track.getCoordinates(t);
				double alt = astro.getAltitude(c, t);
				double azm = astro.getAzimuth(c, t);
				double mount = cpc.getMountAngle(skyAngle, star, IOFF, t);
				double r = (double)(t-start)/(double)duration;
				
				data.add(r, Math.toDegrees(mount));				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			t += (long)(duration/100.0);
		}

		
	}
}
