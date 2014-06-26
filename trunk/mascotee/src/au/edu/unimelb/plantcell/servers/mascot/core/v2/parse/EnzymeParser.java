package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.Enzyme;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * Parses the mascot enzyme config file (located alongside mascot.dat in mascot v2.1 or earlier)
 * and instantiates enzyme objects with each record
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class EnzymeParser {
	private File         path;
	private MascotConfig config;
	
	public EnzymeParser(final MascotConfig add_to_me, final File enzyme_file) {
		assert(add_to_me != null && enzyme_file != null);
		this.config = add_to_me;		// the mascot config which will have the loaded enzymes added to it
		this.path   = enzyme_file;
	}
	
	public void parse() throws IOException, SOAPException {
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(path));
			String line;
			List<String> vec = new ArrayList<String>();
			String title = null;
			while ((line = rdr.readLine()) != null) {
				if (line.startsWith("*") && title != null) {
					Enzyme e = new Enzyme(title);
					e.setConfig(vec);
					config.getEnzymes().add(e);
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
			if (rdr != null)
				rdr.close();
		}
	}
}
