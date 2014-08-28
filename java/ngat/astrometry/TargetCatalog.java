/**
 * 
 */
package ngat.astrometry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class TargetCatalog {

	private String catalogName;
	
	private Map<String, XExtraSolarTarget> targets;

	/**
	 * @param catalogName
	 */
	public TargetCatalog(String catalogName) {
		super();
		this.catalogName = catalogName;
		targets = new HashMap<String, XExtraSolarTarget>();
	}

	/**
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		targets.clear();
	}

	/**
	 * @param name
	 * @return The target identified by name or null.
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public XExtraSolarTarget get(String name) {
		return targets.get(name);
	}

	/**
	 * @return The number of targets.
	 * @see java.util.Map#size()
	 */
	public int size() {
		return targets.size();
	}

	/**
	 * @return A List of all targets.
	 * @see java.util.Map#values()
	 */
	public List<XExtraSolarTarget> values() {
	  List list = new Vector<XExtraSolarTarget>();
        Iterator <XExtraSolarTarget>it = targets.values().iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
	}
	
	
	/** Load the list of targets from a file.
	 * @param file The file containing the list of targets in a standard format.
	 * @throws Exception
	 */
	public void load(File file) throws Exception {
		
		BufferedReader bin = new BufferedReader(new FileReader(file));
		
		String line = null;
		while ((line = bin.readLine()) != null) {
			
			StringTokenizer st = new StringTokenizer(line);
		
			// name ra(hh:mm:ss) dec([+]dd:mm:ss.s) 
			String name   = st.nextToken();
			String strRa  = st.nextToken();
			String strDec = st.nextToken();
			double ra  = AstroFormatter.parseHMS(strRa, ":");
			double dec = AstroFormatter.parseDMS(strDec, ":");
			
			XExtraSolarTarget target = new XExtraSolarTarget(name);
			target.setRa(ra);
			target.setDec(dec);
			
			targets.put(name, target);
			
		}
		
	}
	
	/** Convenience method to create and load a catalog.
	 * Usage: TargetCatalog blankSky = TargetCatalog.load("BLANK_FIELDS", "blank_fields_01.cat");
	 * @param catalogName Name of the catalog.
	 * @param file A file to load targets from.
	 * @return A loaded catalog.
	 * @throws Exception
	 */
	public static TargetCatalog load(String catalogName, File file) throws Exception {
		
		TargetCatalog cat = new TargetCatalog(catalogName);
		cat.load(file);
		return cat;
		
	}
	
}
