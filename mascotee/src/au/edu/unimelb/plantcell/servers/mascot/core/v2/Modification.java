package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single modification from unimod file in the mascot folder
 * @author acassin
 *
 */
public class Modification {
	private String             title;
	private boolean            is_hidden;
	private Map<String,Double> mi_mass_delta, average_mass_delta;
	
	public Modification(String title) {
		this(title, null, null);
	}
	
	public Modification(final String title, 
			final Map<String,Double> monoisotopic_mass_deltas, 
			final Map<String,Double> average_mass_deltas) {
		this.title              = title;
		this.mi_mass_delta      = monoisotopic_mass_deltas;
		this.average_mass_delta = average_mass_deltas;
		this.is_hidden          = true;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public boolean isHidden() {
		return is_hidden;
	}
	
	public void setHidden(boolean new_hide_status) {
		is_hidden = new_hide_status;
	}
	
	public Map<String,Double> getMonisotopicMap() {
		return this.mi_mass_delta;
	}
	
	public Map<String,Double> getAverageMap() {
		return this.average_mass_delta;
	}
	
	public void setMonoisotopicMap(final Map<String,Double> new_map) {
		this.mi_mass_delta = new_map;
	}
	
	public void setAverageMap(final Map<String,Double> new_map) {
		this.average_mass_delta = new_map;
	}

	public void setMaps(List<String> vec) {
		Map<String,Double> mi = new HashMap<String,Double>();
		Map<String,Double> avg= new HashMap<String,Double>();
		boolean is_hidden = false;
		Pattern p = Pattern.compile("^([A-Za-z\\:]+)\\s*([0-9\\.-]+)\\s+([0-9\\.-]+)$");
		for (String line : vec) {
			if (line.equals("Hidden")) {
				is_hidden = true;
			} else {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					mi.put(m.group(1),  Double.valueOf(m.group(2)));
					avg.put(m.group(1), Double.valueOf(m.group(3)));
				}
			}
		}
		
		this.setHidden(is_hidden);
		this.setMonoisotopicMap(mi);
		this.setAverageMap(avg);
	}
}
