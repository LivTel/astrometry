/**
 * 
 */
package ngat.astrometry.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.BasicSite;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class RotatorGraphDisplay extends JFrame {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	JTextField raf;
	JTextField def;
	JTextField tf;
	JSpinner df;
	JTextField sf;
	RotatorGraphPanel rgp;
	
	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public RotatorGraphDisplay(String title) throws HeadlessException {
		super(title);
		getContentPane().add(createContent());		
		pack();		
	}
	
	private Component createContent() {
		JPanel p = new JPanel(true);
		p.setLayout(new BorderLayout());
		
		BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));
		
		rgp = new RotatorGraphPanel(site);
		p.add(rgp,BorderLayout.CENTER);
		
		JPanel top = new JPanel(true);
		top.setLayout(new GridLayout(3, 4));
		raf = new JTextField(12);
		def = new JTextField(12);
		tf = new JTextField(12);
		df = new JSpinner(new SpinnerNumberModel(5, 5, 360, 5));
		sf = new JTextField(12);
		
		top.add(new JLabel("RA"));
		top.add(raf);
		top.add(new JLabel("Dec"));
		top.add(def);
		top.add(new JLabel("Start"));
		top.add(tf);
		top.add(new JLabel("Duration(m)"));
		top.add(df);
		top.add(new JLabel("Sky angle"));
		top.add(sf);
		
		p.add(top, BorderLayout.NORTH);
		
		JButton b = new JButton("Update");
		b.setForeground(Color.blue);
		b.setBackground(Color.orange);
		b.addActionListener(new AListner());
		
		p.add(b, BorderLayout.SOUTH);
		
		return p;
	}
	private class AListner implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		
		try {
			double ra = AstroFormatter.parseHMS(raf.getText(),":");
			double dec = AstroFormatter.parseDMS(def.getText(),":");
			long start = (sdf.parse(tf.getText())).getTime();
			long duration = (long)(60000*((Integer)df.getValue()).longValue());
			double sky = Math.toRadians(Double.parseDouble(sf.getText()));
			rgp.update(ra, dec, start, duration, sky);
			
		} catch (Exception ax) {
			ax.printStackTrace();
		}
	}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RotatorGraphDisplay disp = new RotatorGraphDisplay("Rotator Graph Display");
		disp.setBounds(200,200,500,500);
		disp.setVisible(true);
	}

}
