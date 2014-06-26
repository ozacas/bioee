package au.edu.unimelb.plantcell.servers.mascot.core.v2;

/**
 * Responsible for a single entry in the mascot taxonomy file ie. <mascot root>/config/taxonomy. Note this
 * class is not responsible for the taxonomy record in mascot.dat
 * 
 * @author acassin
 *
 */
public class SpeciesTaxonomy {
	private String title;
	
	public SpeciesTaxonomy(final String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
