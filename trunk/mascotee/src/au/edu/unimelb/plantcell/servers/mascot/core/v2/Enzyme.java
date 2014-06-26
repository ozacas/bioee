package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single record within <mascot root>/config/enzymes. Dumb implementation for now.
 * @author pcbrc.admin
 *
 */
public class Enzyme implements Comparable<Enzyme> {
	private List<String> config = new ArrayList<String>();
	private String title;
	
	public Enzyme(final String title) {
		assert(title != null);
		this.title = title;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Enzyme))
			return false;
		return this.getTitle().equals(((Enzyme)o).getTitle());
	}
	
	public int hashCode() {
		return title.hashCode() ^ config.hashCode();
	}
	
	@Override
	public int compareTo(Enzyme o) {
		return getTitle().compareTo(o.getTitle());
	}
	
	public String getTitle() {
		return title;
	}
	
	public String[] getConfig() {
		return config.toArray(new String[0]);
	}
	
	public void setConfig(List<String> config_lines) {
		assert(config_lines != null);
		config.clear();
		config.addAll(config_lines);
	}
}
