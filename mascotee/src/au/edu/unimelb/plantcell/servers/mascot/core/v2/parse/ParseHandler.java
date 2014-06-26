package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * Responsible for handling the "PARSE" section in a mascot.dat file (mascot v2.1 or earlier). Currently a NO-OP.
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class ParseHandler implements MascotEntryHandler {

	@Override
	public void addLine(String line, MascotConfig add_to_me) throws Exception {
	}

	@Override
	public void endOfSection(MascotConfig add_to_me) {
	}

}
