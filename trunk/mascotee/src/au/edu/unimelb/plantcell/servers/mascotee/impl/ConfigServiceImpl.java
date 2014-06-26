package au.edu.unimelb.plantcell.servers.mascotee.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.ConfigParam;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.Enzyme;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.FragmentationRules;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotDatabase;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.Modification;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SpeciesTaxonomy;
import au.edu.unimelb.plantcell.servers.mascotee.endpoints.ConfigService;


/**
 * Implementation of the service as defined by {@link ConfigService}. All webservice methods
 * can be accessed by user roles (as defined on the server-side) as either 'MascotWSUser' or
 * 'MascotWSAdmin'.
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
@WebService(serviceName="ConfigService", 
endpointInterface="au.edu.unimelb.plantcell.servers.mascotee.endpoints.ConfigService", 
targetNamespace="http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl")
@BindingType(value=SOAPBinding.SOAP12HTTP_MTOM_BINDING)
public class ConfigServiceImpl implements ConfigService {
	@EJB private MascotConfig mascotConfiguration;
		
	public String[] availableInstruments() throws SOAPException {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(mascotConfiguration.getInstruments());
		return ret.toArray(new String[0]);
	}
	
	@Override
	public String[] availableDatabases() throws SOAPException {
		ArrayList<String> ret = new ArrayList<String>();
		
		for (MascotDatabase md : mascotConfiguration.getDatabases()) {
			ret.add(md.getName());
		}
		return ret.toArray(new String[0]);
	}

	/**
	 * Support method for webservice API. Hence it is private so WS clients cannot use it directly.
	 * 
	 * @param dbName
	 * @return
	 * @throws SOAPException
	 */
	private List<File> getFiles(final String dbName) throws SOAPException {
		MascotDatabase db = mascotConfiguration.findDatabase(dbName);
		if (db == null)
			throw new SOAPException("No such mascot database: "+dbName);
		try {
			return db.getSequenceFiles();
		} catch (IOException ioe) {
			throw new SOAPException(ioe);
		}
	}
	
	@Override
	public int countDatabaseSequenceFiles(final String dbName) throws SOAPException {
		List<File> sequenceFiles = getFiles(dbName);
		return sequenceFiles.size();
	}
	
	@Override
	public DataHandler getDatabaseSequenceFile(final String databaseName, int idx) throws SOAPException {
		List<File> sequenceFiles = getFiles(databaseName);
		if (idx < 0 || idx > sequenceFiles.size()-1) {
			throw new SOAPException("Invalid sequence file index: must be in the range [0.."+(sequenceFiles.size()-1)+"]");
		}
		File seqFile = sequenceFiles.get(idx);
		try {
			return new DataHandler(seqFile.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new SOAPException(e);
		}
	}

	@Override
	public String[] availableConfigParameters() throws SOAPException {
		List<ConfigParam> parameters = mascotConfiguration.getConfigurationParams();
		ArrayList<String> ret = new ArrayList<String>();
		for (ConfigParam cp : parameters) {
			ret.add(cp.getName());
		}
		return ret.toArray(new String[0]);
	}
	
	@Override
	public String getParamValue(String paramName) throws SOAPException {
		List<ConfigParam> params = mascotConfiguration.getConfigurationParams();
		for (ConfigParam cp : params) {
			if (cp.hasName(paramName))
				return cp.getValue();
		}
		
		return null;
	}

	@Override
	public String[] availableEnzymes() throws SOAPException {
		List<Enzyme> enzymes = mascotConfiguration.getEnzymes();
		List<String> ret = new ArrayList<String>(enzymes.size());
		for (Enzyme e : enzymes) {
			ret.add(e.getTitle());
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public String[] availableDataFormats() throws SOAPException {
		List<String> formats = mascotConfiguration.getDataFormats();
		return formats.toArray(new String[0]);
	}

	@Override
	public String[] availableModifications() throws SOAPException {
		List<Modification> mods = mascotConfiguration.getModifications();
		ArrayList<String> ret = new ArrayList<String>();
		for (Modification m : mods) {
			ret.add(m.getTitle());
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public String[] getFragmentationRulesForInstrument(final String instrument) throws SOAPException {
		List<FragmentationRules> instrument_types = mascotConfiguration.getFragmentationRules();
		ArrayList<String> ret = new ArrayList<String>();
		for (FragmentationRules fr : instrument_types) {
			if (fr.hasTitle(instrument)) {
				ret.addAll(fr.asRuleText());
			}
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public String[] availableTaxa() throws SOAPException {
		ArrayList<String> ret = new ArrayList<String>();
		for (SpeciesTaxonomy st : mascotConfiguration.getTaxonomies()) {
			ret.add(st.getTitle());
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public String[] availableTopHits() throws SOAPException {
		ArrayList<String> ret = new ArrayList<String>();
		String val    = getParamValue("ReportNumberChoices");
		String vals[] = new String[] {"AUTO", "5", "10", "20", "30", "50", "100", "200" };
		if (val == null || val.length() < 1 || val.indexOf(',') < 0) {
			vals = val.split(",\\s*");
		}
		for (String s : vals) {
			ret.add(s);
		}
		return ret.toArray(new String[0]);
	}
	
	@Override
	public String[] availablePeptideChargeStates() throws SOAPException {
		return new String[] { "1+, 2+ and 3+", "2+ and 3+", "Mr", "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+"};
	}

	@Override
	public String getDetailedEnzymeRecord(String enzyme) throws SOAPException {
		try {
			for (Enzyme e : mascotConfiguration.getEnzymes()) {
				if (enzyme.equals(e.getTitle())) {
					StringBuilder sb = new StringBuilder();
					for (String s : e.getConfig()) {
						sb.append(s);
						sb.append('\n');
					}
					return sb.toString();
				}
			}
			throw new SOAPException("No such enzyme: "+enzyme);
		} catch (Exception e) {
			throw new SOAPException(e);
		}
	}

	@Override
	public String getDetailedDatabaseRecord(String db) throws SOAPException {
		MascotDatabase d = mascotConfiguration.findDatabase(db);
		if (d == null) {
			throw new SOAPException("No such mascot search database: "+db);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Database type: "+d.getType().toString());
		sb.append('\n');
		sb.append("Memory mapped: "+d.getMemoryMapped());
		sb.append('\n');
		sb.append("Sequence file glob: "+d.getGlob());
		sb.append('\n');
		sb.append("Number of threads: "+d.getNumberOfThreads());
		sb.append('\n');
		sb.append("Number of blocks: "+d.getNumberOfBlocks());
		return sb.toString();
	}
	
	@Override
	public String getDetailedModificationRecord(final String mod) throws SOAPException {
		try {
			for (Modification m : mascotConfiguration.getModifications()) {
				if (mod.equals(m.getTitle())) {
					Map<String,Double> mi = m.getMonisotopicMap();
					Map<String,Double> av = m.getAverageMap();
					Set<String> keys = mi.keySet();
					StringBuilder sb = new StringBuilder();
					for (String k : keys) {
						sb.append(k+" "+mi.get(k)+", "+av.get(k));
						sb.append('\n');
					}
					return sb.toString();
				}
			}
			throw new SOAPException("No such modification: "+mod);
		} catch (Exception e) {
			throw new SOAPException(e);
		}
	}

	@Override
	public String getURL() throws SOAPException {
		return mascotConfiguration.getURL();
	}

	@Override
	public String getDatabaseSequenceURL(String dbName, int idx) throws SOAPException {
		List<File> sequenceFiles = getFiles(dbName);
		if (idx < 0 || idx > sequenceFiles.size()-1) {
			throw new SOAPException("Invalid sequence file index: must be in the range [0.."+(sequenceFiles.size()-1)+"]");
		}
		File seqFile   = sequenceFiles.get(idx);
		String url     = getURL();
		String encoded = Base64.getEncoder().encodeToString(seqFile.getAbsolutePath().toString().getBytes());
		return url + "rest/database/get/" + encoded;
	}

	@Override
	public boolean isDatabaseAA(String dbName) throws SOAPException {
		if (mascotConfiguration == null) {
			throw new SOAPException("No mascot configuration!");
		}
		MascotDatabase db = mascotConfiguration.findDatabase(dbName);
		if (db == null) {
			throw new SOAPException("No such mascot database: "+dbName);
		}
		return (db.getType().equals(MascotDatabase.DatabaseType.AA));
	}
}
