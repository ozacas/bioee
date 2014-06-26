package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.EnzymeParser;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.FragRulesParser;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.MascotDatParser;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.MassParser;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.TaxonomyParser;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.parse.UniModFileParser;

/**
 * Configuration state (not necessarily just
 * from mascot.dat) which we support for Mascot v2.x (presumably x<4???)
 * 
 * @author acassin
 *
 */
@ApplicationScoped
@Singleton
@Lock(LockType.READ)
public class MascotConfig {
	private final List<MascotDatabase>             databases = new ArrayList<MascotDatabase>();
	private List<ConfigParam>               parameters = new ArrayList<ConfigParam>();
	private List<SpeciesTaxonomy>   species_taxonomies = new ArrayList<SpeciesTaxonomy>();
	private List<Modification> available_modifications = new ArrayList<Modification>();
	private List<Enzyme>	   available_enzymes       = new ArrayList<Enzyme>();
	private List<FragmentationRules> fragmentation_rules = new ArrayList<FragmentationRules>();
	private ClusterConfig      cluster_config;
	private CronConfig         cron_config;
	private Map<String,Double> monoisotopic_masses, average_masses; // for key elements and each amino acid
	private boolean parsed;

	@Resource(name="ConfigFile")
	private String configFile;
	@Resource(name="ModFile")
	private String modFile;
	@Resource(name="RootFolder")
	private String rootFolder;
	@Resource(name="MascotEE_URL", mappedName="MascotEE_URL")
	private String mascotee_url;	// needed to download results

	
	public MascotConfig() {
		reset();
	}

	/**
	 * Initialise all internal state to defaults
	 */
	public void reset() {
		databases.clear();
		parameters.clear();
		available_modifications.clear();
		species_taxonomies.clear();
		fragmentation_rules.clear();
		available_enzymes.clear();
		cluster_config = null;
		cron_config    = null;
		monoisotopic_masses = null;
		average_masses = null;
		parsed         = false;
	}
	
	public String getConfigFile() {
		return (configFile != null) ? configFile : "/home/acassin/test/mascotee/mascot.dat";
	}

	public void setConfigFile(String new_path) {
		configFile = new_path;
	}
	
	public String getModFile() {
		return (modFile != null) ? modFile : "/home/acassin/test/mascotee/mod_file";
	}
	
	public void setModFile(String new_path) {
		modFile = new_path;
	}
	
	/**
	 * Load all datasources registered with this mascot config instance
	 */
	@Lock(LockType.WRITE)
	public void parse() throws Exception {
		if (parsed)
			return;
		parsed = true;		// set to TRUE before the actual parse to avoid infinite recursion
		
		Logger l = Logger.getLogger("MascotConfig");
		File dat_file = new File(getConfigFile());
		File mod_file = new File(getModFile());
		if (dat_file != null) {
			l.info("Parsing mascot.dat file: "+dat_file.getAbsolutePath());
			new MascotDatParser(this, dat_file).parse();
			File enzymes = new File(dat_file.getParentFile(), "enzymes");
			if (enzymes.exists()) {
				l.info("Parsing enzymes file: "+enzymes.getAbsolutePath());
				new EnzymeParser(this, enzymes).parse();
			}
			File frag_rules = new File(dat_file.getParentFile(), "fragmentation_rules");
			if (frag_rules.exists()) {
				l.info("Parsing fragmentation rules: "+frag_rules.getAbsolutePath());
				new FragRulesParser(this, frag_rules).parse();
			}
			File masses = new File(dat_file.getParentFile(), "masses");
			if (masses.exists()) {
				l.info("Parsing mass file: "+masses.getAbsolutePath());
				new MassParser(this, masses).parse();
			}
			File taxonomies = new File(dat_file.getParentFile(), "taxonomy");
			if (taxonomies.exists()) {
				l.info("Parsing taxonomy file: "+taxonomies.getAbsolutePath());
				new TaxonomyParser(this, taxonomies).parse();
				l.info("Successfully parsed "+species_taxonomies.size()+" taxonomy entries.");
			}
		}
		if (mod_file != null) {
			l.info("Parsing modifications file: "+mod_file.getAbsolutePath());
			new UniModFileParser(this, mod_file).parse();
		}
	}
	
	/**
	 * Return a list of available sequence databases installed into the mascot instance
	 * @return
	 * @throws SOAPException 
	 */
	public List<MascotDatabase> getDatabases() throws SOAPException {
		reparseIfNeeded();
		return databases;
	}

	private void reparseIfNeeded() throws SOAPException {
		if (parsed)
			return;
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SOAPException(e.getMessage());
		}
	}

	public MascotDatabase findDatabase(String databaseName) throws SOAPException {
		reparseIfNeeded();
		for (MascotDatabase md : getDatabases()) {
			if (md.hasName(databaseName))
				return md;
		}
		return null;
	}

	public List<Modification> getModifications() throws SOAPException {
		reparseIfNeeded();
		return available_modifications;
	}
	
	public List<ConfigParam> getConfigurationParams() throws SOAPException {
		reparseIfNeeded();
		return parameters;
	}

	public ConfigParam getNamedConfigurationParam(final String name) throws SOAPException {
		List<ConfigParam> params = getConfigurationParams();
		for (ConfigParam cp : params) {
			if (cp.hasName(name))
				return cp;
		}
		return null;
	}
	
	public List<SpeciesTaxonomy> getTaxonomies() throws SOAPException {
		reparseIfNeeded();
		return species_taxonomies;
	}

	public void setCronConfig(final CronConfig cc) {
		cron_config = cc;
	}

	public void setClusterConfig(final ClusterConfig cc) {
		cluster_config = cc;
	}
	
	public ClusterConfig getClusterConfig() throws SOAPException {
		reparseIfNeeded();
		return cluster_config;
	}
	
	public CronConfig getCronConfig() throws SOAPException {
		reparseIfNeeded();
		return cron_config;
	}
	
	public List<String> getInstruments() {
		ArrayList<String> ret = new ArrayList<String>();
		try {
			List<FragmentationRules> rules = getFragmentationRules();
			for (FragmentationRules fr : rules) {
				ret.add(fr.getInstrumentType());
			}
		} catch (SOAPException se) {
			se.printStackTrace();
			ret.clear();
		}
		return ret;
	}

	/**
	 * Returns the folder path of the mascot data folder as found by examination of the mascot configuration data
	 * @return
	 * @throws SOAPException if no such folder can be found
	 */
	public File getDataRootFolder() throws SOAPException {
		File data_root = new File(getRootFolder(), "data");
		if (data_root == null || !(data_root.exists() && data_root.isDirectory())) {
			throw new SOAPException("Non-existant mascot data folder... check your configuration!");
		}
		return data_root;
	}
	
	
	public String getRootFolder() {
		return (rootFolder != null) ? rootFolder : "/main/mascot";
	}
	
	public void setRootFolder(String new_path) {
		rootFolder = new_path;
	}

	public List<Enzyme> getEnzymes() throws SOAPException {
		reparseIfNeeded();
		return available_enzymes;
	}

	public List<FragmentationRules> getFragmentationRules() throws SOAPException {
		reparseIfNeeded();
		return fragmentation_rules;
	}
	
	/**
	 * 
	 * @return may be null if no config data is available
	 * @throws SOAPException
	 */
	public Map<String,Double> getMonisotopicMasses() throws SOAPException {
		reparseIfNeeded();
		return monoisotopic_masses;
	}
	
	public void setMonoisotopicMasses(final Map<String,Double> new_masses) {
		monoisotopic_masses = new_masses;
	}
	
	public void setAverageMasses(final Map<String,Double> new_masses) {
		average_masses = new_masses;
	}
	
	/**
	 * 
	 * @return may be null if no config data is available
	 * @throws SOAPException
	 */
	public Map<String,Double> getAverageMasses() throws SOAPException {
		reparseIfNeeded();
		return average_masses;
	}

	public List<String> getDataFormats() {
		// TODO... hardcoded for now
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Mascot generic (MGF)");
		return ret;
	}

	/**
	 * retrieves the full results URL from the mascot.dat config
	 * 
	 * @return URL as entered by the mascot administrator or null if not present
	 */
	public URL getFullResultsURL() throws SOAPException {
		// 1. retrieve master_results.pl CGI page and return that
		List<ConfigParam> params = getConfigurationParams();
		ConfigParam master_computer_name = null;
		for (ConfigParam cp : params) {
			if (cp.hasName("ResultsFullURL")) {
				try {
					URL u = new URL(cp.getValue());
					return u;
				} catch (MalformedURLException mue) {
					mue.printStackTrace();
					break;		// try to construct the URL from the hostname
				}
			} else if (cp.hasName("MasterComputerName")) {
				master_computer_name = cp;	// dont use this unless ResultsFullURL is not available
			}
		}
		
		// 2. otherwise try to concoct the correct URL via MasterComputerName
		if (master_computer_name != null) {
			try {
				URL u = new URL("http://"+master_computer_name.getValue()+"/mascot/cgi/master_results.pl");
				return u;
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
				// FALL THRU
			}
		}
		
		return null;
	}
	
	/**
	 * returns the search form url appropriate for the specified search type
	 */
	public URL getSearchFormURL(final SearchType st) throws SOAPException {
		if (st == null) {
			return null;
		}
		URL u = getFullResultsURL();
		if (u == null) 
			throw new SOAPException("No master results URL available: check your mascot.dat configuration!");
		
		int form_version = 2;	// HACK TODO FIXME: can we figure this out from mascot.dat?
		int port = u.getPort();
		if (port == -1)
			port = u.getDefaultPort();
		if (port < 0)
			port = 80;
		String path = u.getPath();
		String suffix = "/master_results.pl";
		if (path.endsWith(suffix)) {
			path = path.substring(0, path.length()-suffix.length());
		}
		try {
			switch (st) {
			case PMF:
				return new URL(u.getProtocol()+"://"+u.getHost()+":"+port+path+"/search_form.pl?FORMVER="+form_version+"&SEARCH=PMF");
			case SEQ_QUERY:
				return new URL(u.getProtocol()+"://"+u.getHost()+":"+port+path+"/search_form.pl?FORMVER="+form_version+"&SEARCH=SQ");
			case MSMS:
				return new URL(u.getProtocol()+"://"+u.getHost()+":"+port+path+"/search_form.pl?FORMVER="+form_version+"&SEARCH=MIS");
			default:
				throw new SOAPException("Unknown mascot search type: "+st);
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return null;
		}
	}

	public File getDataFolder() throws SOAPException {
		// get it from the mascot.dat configuration file if possible
		ConfigParam data_cp = getNamedConfigurationParam("InterFileBasePath");
		File data_folder = new File(data_cp.getValue());
		if (data_folder.isDirectory())
			return data_folder;
		
		// fallback to...
		return new File(getRootFolder(), "data");
	}

	public boolean hasEnzyme(String enzyme) throws SOAPException {
		for (Enzyme e : getEnzymes()) {
			if (e.getTitle().equals(enzyme))
				return true;
		}
		return false;
	}

	public boolean hasSpeciesTaxonomy(String taxa) throws SOAPException {
		for (SpeciesTaxonomy st : getTaxonomies()) {
			if (st.getTitle().equals(taxa)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasInstrument(String instr) {
		for (String i : getInstruments()) {
			if (i.equals(instr)) {
				return true;
			}
		}
		return false;
	}

	public String getURL() throws SOAPException {
		if (mascotee_url == null || mascotee_url.length() < 1) {
			throw new SOAPException("MascotEE must have a configured URL: see installation instructions!");
		}
		return mascotee_url;
	}
	
	/**
	 * This is an expensive and slow method, but it is required in order to check that the user is not 
	 * trying to download /etc/passwd or something.
	 * Call sparingly.
	 * 
	 * @param f
	 * @return
	 */
	public boolean hasSequenceFile(final File f) {
		for (MascotDatabase md : databases) {
			List<File> seqs;
			try {
				seqs = md.getSequenceFiles();
				for (File seq : seqs) {
					if (seq.equals(f)) {
						return true;
					}
				}
			} catch (IOException e) {
			}
		}
		return false;
	}
}
