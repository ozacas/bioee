package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SpeciesTaxonomy;

/**
 * Responsible for parsing a <mascot root>/config/taxonomy file into the specified MascotConfig instance
 * @author acassin
 *
 */
public class TaxonomyParser {
	private final MascotConfig config;
	private final File         taxonomy_file;
	
	public TaxonomyParser(final MascotConfig config, final File input_file) {
		this.config = config;
		this.taxonomy_file = input_file;
	}
	
	public void parse() {
		// subtle pattern: be careful to test it!
		Pattern p = Pattern.compile("^Title:[.\\s]*?(.*)$");
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(taxonomy_file));
			String line;
			SpeciesTaxonomy st = null;
			while ((line = rdr.readLine()) != null) {
				if (line.startsWith("*") || line.equals("end")) {
					if (st != null) {
						config.getTaxonomies().add(st);
					}
					st = null;
					continue;
				}
				Matcher m = p.matcher(line);
				if (m.matches()) {
					st = new SpeciesTaxonomy(m.group(1));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		} finally {
			if (rdr != null) {
				try {
					rdr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
