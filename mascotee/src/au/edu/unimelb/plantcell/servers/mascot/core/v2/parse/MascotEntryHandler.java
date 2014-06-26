package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * Called to handle a new line from a mascot related data source (eg. mascot.dat or unimod.txt)
 * @author acassin
 *
 */
public interface MascotEntryHandler {
	/**
	 * Callback for each line from the input source
	 * @param line
	 * @param add_to_me
	 * @throws Exception
	 */
	public void addLine(final String line, final MascotConfig add_to_me) throws Exception;
	
	/**
	 * Called at the end of the data section eg. database section of mascot.dat
	 */
	public void endOfSection(final MascotConfig add_to_me);
}
