package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.FragmentationRules;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * responsible for parsing &lt;mascot root&gt;/config/fragmentation_rules which is found in mascot v2.1 and earlier versions
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class FragRulesParser {
	private File path;
	private MascotConfig config;
	
	public FragRulesParser(final MascotConfig mascotConfig, final File frag_rules) {
		assert(mascotConfig != null && frag_rules != null);
		
		this.config = mascotConfig;
		this.path   = frag_rules;
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
					FragmentationRules fr = new FragmentationRules(title);
					fr.setRules(vec);
					title = null;
					vec   = new ArrayList<String>();
					config.getFragmentationRules().add(fr);
				} else {
					if (!line.toLowerCase().startsWith("title:")) {
						vec.add(line);
					} else {
						title = line.substring("title:".length());
					}
				}
			}
		} finally {
			if (rdr != null)
				rdr.close();
		}
	}
}
