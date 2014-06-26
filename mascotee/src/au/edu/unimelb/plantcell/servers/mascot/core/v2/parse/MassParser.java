package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

public class MassParser {
	private final File path;
	private final MascotConfig config;
	
	public MassParser(final MascotConfig config, final File path) {
		this.config = config;
		this.path   = path;
	}
	
	public void parse() throws IOException,NumberFormatException {
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(path));
			String line;
			Map<String,Double> mi_masses = new HashMap<String,Double>();
			Map<String,Double> avg_masses= new HashMap<String,Double>();
			
			while ((line = rdr.readLine()) != null) {
				Pattern p = Pattern.compile("^([A-Z]+):\\s+([0-9\\.]+)\\s*,\\s*([0-9]\\.)\\s*$");
				Matcher m = p.matcher(line);
				if (m.matches()) {
					mi_masses.put(m.group(1),  Double.valueOf(m.group(2)));
					avg_masses.put(m.group(1), Double.valueOf(m.group(3)));
				}
			}
			config.setMonoisotopicMasses(mi_masses);
			config.setAverageMasses(avg_masses);
		} finally {
			if (rdr != null)
				rdr.close();
		}
	}
}
