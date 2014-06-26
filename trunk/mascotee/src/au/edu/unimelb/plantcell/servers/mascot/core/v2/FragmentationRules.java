package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FragmentationRules {
	private String instrument_type;
	private final Map<Integer,String> rules = new HashMap<Integer,String>();
	
	public FragmentationRules(final String title) {
		setInstrumentType(title);
	}

	private void setInstrumentType(String title) {
		assert(title != null);
		this.instrument_type = title;
	}

	public String getInstrumentType() {
		return instrument_type;
	}

	public void setRules(List<String> vec) {
		Pattern p = Pattern.compile("^(\\d+)\\s*(#.*)?$");
		int matched = 0;
		rules.clear();
		for (String line : vec) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				String comment = m.group(2);
				if (comment != null) {
					while (comment.startsWith("#")) {
						comment = comment.substring(1);
					}
				} else {
					comment = "";
				}
				rules.put(Integer.valueOf(m.group(1)), comment);
				matched++;
			}
		}
		if (matched < 1) {
			Logger.getLogger("Fragmentation rules").warning("no text describing each numeric rule - fix your mascot server configuration");
		}
	}

	public boolean hasTitle(final String instrument_type) {
		if (instrument_type == null)
			return false;
		return getInstrumentType().equals(instrument_type);
	}
	
	/**
	 * Returns the current rules as text strings. This may not work if the input file has been stripped of all comments
	 * but hopefully the user has not done that...
	 * 
	 * @return strings as created by <code>setRules()</code> during the parse of the fragmentation_rules file
	 */
	public List<String> asRuleText() {
		ArrayList<String> ret = new ArrayList<String>();
		
		for (Integer k : rules.keySet()) {
			String v = String.valueOf(k) + ": "+rules.get(k);
			if (v == null || v.length() < 1) {
				v = String.valueOf(k);
			}
			ret.add(v);
		}
		return ret;
	}
}
