package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.util.ArrayList;
import java.util.List;

public class ClusterConfig {
	private List<ConfigParam> params = new ArrayList<ConfigParam>();
	
	public List<ConfigParam> getParameters() {
		return params;
	}
}
