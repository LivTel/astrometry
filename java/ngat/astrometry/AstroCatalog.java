package ngat.astrometry;

import java.io.*;
import java.util.*;

import ngat.phase2.*;

public class AstroCatalog {

	/** Global list of catalogs. */
	private static Map<String, AstroCatalog> catalogs;

	/** Catalog name. */
	protected String catalogName;

	/** Mapping from name to target. */
	protected HashMap<String, XExtraSolarTarget> targets;

	/** List of targets. Targets are added in arrival order*/
	protected List<XExtraSolarTarget> targetList;
	
	
	/** Create a Catalog. */
	public AstroCatalog(String catalogName) {
		this.catalogName = catalogName;
		targets = new HashMap<String, XExtraSolarTarget>();
		targetList = new Vector<XExtraSolarTarget>();
	}

	/**
	 * @return the catalogName
	 */
	public String getCatalogName() {
		return catalogName;
	}


	/**
	 * @param catalogName the catalogName to set
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}


	/** Add a target to list and mapping.*/
	public void addTarget(XExtraSolarTarget target) {
		if (target == null)
			return;
		
		if (target.getName() == null)
			return;
		// better if we threw an exception here !
		
		if (! (targets.containsKey(target.getName()))) {
			targets.put(target.getName(), target);
			targetList.add(target);
		}
	}

	/** Remove the named target (if it exists) from list and mapping.*/
	public void removeTarget(String tname) {
		if (! (targets.containsKey(tname)))
			return;
		
		XExtraSolarTarget target = targets.get(tname);
		targets.remove(tname);
		targetList.remove(target);
	}

	public XExtraSolarTarget getTarget(String tname) {
		return (XExtraSolarTarget) targets.get(tname);
	}

	public List<XExtraSolarTarget> listTargets() {
		return targetList;
	}

	public void clear() {
		targets.clear();
	}

	public int size() {
		return targets.size();
	}

	public static AstroCatalog loadCatalog(String catalogName, File file) throws Exception {

		// Create the global list of catalogs.
		if (catalogs == null)
			catalogs = new HashMap<String, AstroCatalog>();

		AstroCatalog cat = new AstroCatalog(catalogName);

		BufferedReader bin = new BufferedReader(new FileReader(file));

		int il = 0;
		String line = null;
		while ((line = bin.readLine()) != null) {
			il++;
			// skip blanks or comments
			if (line.trim().startsWith("#") || line.trim().equals(""))
				continue;

			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() < 4)
				throw new IllegalArgumentException("Catalog [" + catalogName + "] Missing tokens in line: " + il);

			String tgtname = st.nextToken();
			String stra = st.nextToken();
			String stdec = st.nextToken();

			double ra = AstroFormatter.parseHMS(stra, ":");
			double dec = AstroFormatter.parseDMS(stdec, ":");

			XExtraSolarTarget src = new XExtraSolarTarget(tgtname);
			src.setRa(ra);
			src.setDec(dec);
			src.setFrame(AstrometryReferenceFrame.FK5);
			src.setEpoch(2000.0);
			src.setPmRA(0.0);
			src.setPmDec(0.0);
			src.setParallax(0.0);
			src.setRadialVelocity(0.0);

			cat.addTarget(src);
			System.err.println("Astrometry:LoadCat(" + catalogName + "): Adding target: " + src);

		}

		catalogs.put(catalogName, cat);

		return cat;
	}

	/** Returns the named catalog or null if not available. */
	public static AstroCatalog getCatalog(String name) {

		if (catalogs == null)
			return null;

		return catalogs.get(name);

	}
	
	public static List<AstroCatalog> listCatalogs() {
	
		if (catalogs == null)
			return null;
		
		List<AstroCatalog> catlist = new Vector<AstroCatalog>();
		Iterator<AstroCatalog> ia = catalogs.values().iterator();
		while (ia.hasNext()) {
			catlist.add(ia.next());
		}
		
		return catlist;
		
	}
	

}
