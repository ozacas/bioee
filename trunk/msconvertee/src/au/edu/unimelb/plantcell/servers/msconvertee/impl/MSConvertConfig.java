package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.geronimo.mail.util.StringBufferOutputStream;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvertFeature;

/**
 * Solely responsible for all state relating to the msconvert installation. Provides
 * a rich API for clients to use across the service.
 * 
 * @author acassin
 *
 */
@Stateless
@ApplicationScoped
@Lock(LockType.READ)
public class MSConvertConfig {
	// resources provided by TomEE
	@Resource(name="MSCONVERT_PATH")
	private String msconvert_path;
	@Resource
	private String temp_data_folder;
	@Resource(name="MSCONVERT_USAGE")
	private String msconvert_usage;
	
	// fields
	private boolean dirty = true;		// does the msconvert config state need to be loaded?
	private final Set<MSConvertFeature>       supported_features = new HashSet<MSConvertFeature>();
	private final static Map<String,MSConvertFeature> option_map = new HashMap<String,MSConvertFeature>();
	private final static Map<String,MSConvertFeature> plugin_map = new HashMap<String,MSConvertFeature>();
	
	static {
		option_map.put("mzML", MSConvertFeature.OUTPUT_MZML);
		option_map.put("mgf", MSConvertFeature.OUTPUT_MGF);
		option_map.put("mzXML", MSConvertFeature.OUTPUT_MZXML);
		option_map.put("mz5", MSConvertFeature.OUTPUT_MZ5);
		option_map.put("ms1", MSConvertFeature.OUTPUT_MS1);
		option_map.put("ms2", MSConvertFeature.OUTPUT_MS2);
		option_map.put("cms1", MSConvertFeature.OUTPUT_CMS1);
		option_map.put("cms2", MSConvertFeature.OUTPUT_CMS2);
		option_map.put("32", MSConvertFeature.USE_32_BIT_MZ_PRECISION);
		option_map.put("32", MSConvertFeature.USE_32_BIT_INTENSITY_PRECISION);
		option_map.put("64", MSConvertFeature.USE_64_BIT_MZ_PRECISION);
		option_map.put("64", MSConvertFeature.USE_64_BIT_INTENSITY_PRECISION);
		option_map.put("z", MSConvertFeature.COMPRESS_BINARY_DATA);
		option_map.put("g", MSConvertFeature.COMPRESS_ENTIRE_FILE);
		option_map.put("gzip", MSConvertFeature.COMPRESS_ENTIRE_FILE);
		option_map.put("filter", MSConvertFeature.FILTERS_ARE_SUPPORTED);
		option_map.put("merge", MSConvertFeature.MERGE_IS_SUPPORTED);
		
		plugin_map.put("msLevel", MSConvertFeature.FILTER_BY_MSLEVEL);
		plugin_map.put("chargeState", MSConvertFeature.FILTER_BY_CHARGE_STATE);
		//plugin_map.put("precursorRecalculation", MSConvertFeature.PRECURSOR_REFINEMENT);
		plugin_map.put("precursorRefine", MSConvertFeature.PRECURSOR_REFINEMENT);
		plugin_map.put("peakPicking", MSConvertFeature.PEAK_PICKING);
		plugin_map.put("sortByScanTime", MSConvertFeature.SORT_BY_SCAN_TIME);
		plugin_map.put("stripIT", MSConvertFeature.REMOVE_ION_TRAP_MS1_SCANS);
		plugin_map.put("mzWindow", MSConvertFeature.FILTER_BY_MZ);
		plugin_map.put("threshold", MSConvertFeature.FILTER_BY_INTENSITY_THRESHOLD);		// two different plugins with similar features
		plugin_map.put("mzPresent", MSConvertFeature.FILTER_BY_INTENSITY_THRESHOLD);
		plugin_map.put("mzPrecursor", MSConvertFeature.FILTER_BY_PRECURSOR);
		plugin_map.put("zeroSamples", MSConvertFeature.FILTER_BY_ZERO_INTENSITY);
		plugin_map.put("MS2Denoise", MSConvertFeature.MS2_DENOISE);
		plugin_map.put("MS2Deisotope", MSConvertFeature.MS2_DEISOTOPE);
		plugin_map.put("ETDFilter", MSConvertFeature.FILTER_BY_ETD);
		//plugin_map.put("chargeStatePredictor", MSConvertFeature.)
		plugin_map.put("analyzer", MSConvertFeature.FILTER_BY_ANALYZER);
		plugin_map.put("polarity", MSConvertFeature.FILTER_BY_POLARITY);
	}
	
	public MSConvertConfig() {
		dirty = true;
	}
	
	/**
	 * Location of data files during conversion. Since there is only a single thread doing conversions, we dont
	 * worry about conflicting files within the folder. Folder may not exist, so caller must invoke <code>File.mkdir()</code> on it if needed.
	 * 
	 * @return
	 */
	public File getTemporaryFileFolder() {
		if (temp_data_folder != null) {
			return new File(temp_data_folder);
		}
		return MSConvertConstants.MSCONVERT_TEMP_FOLDER;
	}

	/**
	 * We dont give out the location of the msconvert binary directly, but rather return a pre-instantiated
	 * binary here. For which the path corresponds to this objects state.
	 * 
	 * @return
	 */
	public CommandLine getCommandLine() {
		String proteowizard_msconvert = msconvert_path;
		if (proteowizard_msconvert == null) {
			proteowizard_msconvert = "c:/Program Files (x86)/ProteoWizard/ProteoWizard 3.0.4416/msconvert.exe";
		}
		File f = new File(proteowizard_msconvert);
		if (!(f.exists() && f.canExecute())) {
			return null;
		}
		return new CommandLine(f);
	}

	@Lock(LockType.WRITE)
	private void parse() {
		if (!dirty) {
			return;
		}
		supported_features.clear();
		dirty = false;
		
		// we run msconvert to get the supported options into the config state. By doing this at runtime 
		// we can then validate the user-supplied options against the server's capabilities
		CommandLine msconvert = getCommandLine();
		String stderr;
		try {
			if (msconvert != null) {
				stderr = runMSConvertExecutable(msconvert);
			} else {
				stderr = loadLocalTestFile(msconvert_usage);
			}
			
			// stderr should have the supported command line arguments, so we parse that to create the supported feature
			// list. I hate this crappy way to get a programs supported options...
			boolean reached_options = false;
			boolean reached_filter_options = false;
			boolean reached_examples = false;
			for (String line : stderr.toString().split("[\\r\\n]+")) {
				if (line.equals("Options:")) {
					reached_options = true;
					continue;
				} else if (line.equals("Filter options:")) {
					reached_filter_options = true;
					continue;
				} else if (line.equals("Examples:")) {
					reached_examples = true;
					continue;
				}
				if (reached_options && !reached_filter_options) {
					grokOption(line);
				} else if (reached_filter_options && !reached_examples) {
					grokFilterOption(line);
				}
			}
		} catch (Exception e) {
			supported_features.clear();
			e.printStackTrace();
		}
	}
	
	private String loadLocalTestFile(String msconvert_usage_file) {
		StringWriter sw = new StringWriter();
		BufferedReader rdr = null;
		if (msconvert_usage_file == null) {
			msconvert_usage_file = MSConvertConstants.MSCONVERT_USAGE_TEST_FILE;
		}
		try {
			rdr = new BufferedReader(new FileReader(new File(msconvert_usage_file)));
			String line;
			while ((line = rdr.readLine()) != null) {
				sw.append(line);
				sw.append("\n");
			}
			return sw.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return "";
		} finally {
			try {
				if (rdr != null) {
					rdr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String runMSConvertExecutable(final CommandLine msconvert) throws IOException {
		DefaultExecutor exe = new DefaultExecutor();
		
		exe.setExitValues(new int[] {0});
    	exe.setWorkingDirectory(getTemporaryFileFolder());
    	exe.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
    	StringBuffer stdout = new StringBuffer();
    	OutputStream out = new StringBufferOutputStream(stdout);
    	StringBuffer stderr = new StringBuffer();
    	OutputStream err = new StringBufferOutputStream(stderr);
    	exe.setStreamHandler(new PumpStreamHandler(out, err));
    	int exitCode = -1;
		exitCode = exe.execute(msconvert);
		if (exe.isFailure(exitCode)) {
			throw new IOException("Invalid exit code from msconvert: "+exitCode);
    	} 
		return stderr.toString();
	}

	private void grokFilterOption(String line) {
		assert(line != null);
		if (line.trim().length() < 1) {
			return;
		}
		Pattern p = Pattern.compile("^(\\w+)\\s");
		Matcher m = p.matcher(line.trim());
		if (m.find()) {
			String filter = m.group(1);
			if (plugin_map.containsKey(filter)) {
				supported_features.add(plugin_map.get(filter));
			}
		}
	}

	@SuppressWarnings("unused")
	private void grokOption(final String line) {
		assert(line != null);
		Pattern p = Pattern.compile("^\\s*-+(\\w+)\\s+:(.*)$");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			String opt   = m.group(1);
			String descr = m.group(2);
			if (option_map.containsKey(opt)) {
				supported_features.add(option_map.get(opt));
			}
		} else {
			// go looking for key command line options of interest...
			p = Pattern.compile("^\\s+--filter\\s+arg\\s+:");
			m = p.matcher(line);
			if (m.find()) {
				supported_features.add(option_map.get("filter"));
			}
		}
	}

	public List<String> supportedDataFormats() {
		List<String> ret = new ArrayList<String>();
		
		parse();
		if (supported_features.contains(MSConvertFeature.OUTPUT_MGF)) {
			ret.add("mgf");
		}
		if (supported_features.contains(MSConvertFeature.OUTPUT_MZXML)) {
			ret.add("mzxml");
		} 
		if (supported_features.contains(MSConvertFeature.OUTPUT_MZML)) {
			ret.add("mzml");
		}
		if (supported_features.contains(MSConvertFeature.OUTPUT_MZ5)) {
			ret.add("mz5");
		}
		// TODO FIXME... other formats... who uses them?
		return ret;
	}
	
	/**
	 * Tests for a single feature
	 * @param feature
	 * @return true if feature is available with msconvert version, false otherwise
	 */
	public boolean supportsFeature(final MSConvertFeature feature) {
		parse();
		return supported_features.contains(feature);
	}
	
	/**
	 * Tests that all the supplied features are present
	 * @param features must not be null
	 * @return true if all features are available, false otherwise
	 */
	public boolean supportsAllFeatures(final MSConvertFeature[] features) {
		parse();
		for (MSConvertFeature f : features) {
			if (!supported_features.contains(f)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tests if any of the supplied features are supported
	 * 
	 * @param features must not be null
	 * @return true if any of the features are supported, false otherwise
	 */
	public boolean supportsAnyFeature(final MSConvertFeature[] features) {
		parse();
		for (MSConvertFeature f : features) {
			if (supported_features.contains(f)) {
				return true;
			}
		}
		return false;
	}
}
