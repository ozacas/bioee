package au.edu.unimelb.plantcell.servers.mascot.core.v2;

/**
 * Represents a field=value setting from mascot.dat
 * 
 * @author acassin
 *
 */
public class ConfigParam {
	private String name, value;
	
	public ConfigParam(final String name, final String value) {
		this.name = name;
		this.value= value;
	}

	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return (name != null && name.length() > 0);
	}
	
	public String getValue() {
		return value;
	}

	public boolean hasName(String paramName) {
		if (paramName == null)
			return false;
		
		String n = getName();
		return paramName.equals(n);
	}

}
