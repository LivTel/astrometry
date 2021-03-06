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
import javax.swing.JTextField;

import ngat.astrometry.*;
/**
 * @author eng
 *
 */
public class CardinalPointingDisplay extends JFrame {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	JTextField raf;
	JTextField def;
	JTextField tf;
	JTextField df;
	
	RotatorPanel rp;
	
	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public CardinalPointingDisplay(String title) throws HeadlessException {
		super(title);
		getContentPane().add(createContent());		
		pack();		
	}

	private Component createContent() {
		JPanel p = new JPanel(true);
		p.setLayout(new BorderLayout());
		
		BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));
		
		rp = new RotatorPanel(site);
		p.add(rp,BorderLayout.CENTER);
		
		JPanel top = new JPanel(true);
		top.setLayout(new GridLayout(2, 4));
		raf = new JTextField(12);
		def = new JTextField(12);
		tf = new JTextField(12);
		df = new JTextField(12);
		
		top.add(new JLabel("RA"));
		top.add(raf);
		top.add(new JLabel("Dec"));
		top.add(def);
		top.add(new JLabel("Start"));
		top.add(tf);
		top.add(new JLabel("Duration(m)"));
		top.add(df);
		
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
				long duration = 60000*Long.parseLong(df.getText());
				
				rp.update(ra, dec, start, duration);
				
			} catch (Exception ax) {
				ax.printStackTrace();
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CardinalPointingDisplay disp = new CardinalPointingDisplay("Cardinal Pointing Display");
		disp.setBounds(200,200,500,500);
		disp.setVisible(true);
	}

}
