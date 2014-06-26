package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.util.ArrayList;
import java.util.List;

/**
 * A mascot.dat cron section consists of a list of parameters and their values to enable cron-scheduled mascot runs
 * @author acassin
 *
 */
public class CronConfig {
	private List<ConfigParam> params = new ArrayList<ConfigParam>();
	
	public List<ConfigParam> getParameters() {
		return params;
	}
	
}
