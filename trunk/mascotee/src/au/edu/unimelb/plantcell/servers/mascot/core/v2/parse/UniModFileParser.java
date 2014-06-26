package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.Modification;

/**
 * Responsible for loading a unimod file into the specified model state. Both unimod XML and older mod_file formats
 * are supported.
 * 
 * @author acassin
 *
 */
public class UniModFileParser {
	private final File unimod_path;
	private final MascotConfig config;
	
	
	public UniModFileParser(final MascotConfig c, final File unimod_path) {
		assert(c != null && unimod_path != null);
		this.unimod_path = unimod_path;
		this.config      = c;
	}
	
	public void parse() throws Exception {
		if (unimod_path == null || !unimod_path.exists() || !unimod_path.canRead() || !unimod_path.isFile())
			return;
		
		// Either the file is a unimod.xml (newer mascot versions) or mod_file (mascot 2.1 or older)
		// so we must invoke the appropriate parse and execute it...
		if (isLikelyUniModXML()) {
			// perform JAXB load of the xml and then traverse the objects to update MascotConfig
		} else {
			parseModFile();
		}
	}

	private void parseModFile() throws IOException, SOAPException {
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(unimod_path));
			String line;
			
			List<String> vec = new ArrayList<String>();
			String title = null;
			while ((line = rdr.readLine()) != null) {
				if (line.startsWith("*") && title != null) {
					Modification m = new Modification(title);
					m.setMaps(vec);
					config.getModifications().add(m);
					title = null;
					vec   = new ArrayList<String>();
				} else {
					if (!line.startsWith("Title:")) {
						vec.add(line);
					} else {
						title = line.substring("Title:".length());
					}
				}
			}
		} finally {
			if (rdr != null) {
				rdr.close();
			}
		}
	}

	private boolean isLikelyUniModXML() {
		return (unimod_path.getName().toLowerCase().indexOf("xml") >= 0);
	}
}
