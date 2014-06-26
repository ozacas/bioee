package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 * Responsible for checking periodically to see if any config file has changed
 * and if so, instruct the MascotConfig EJB to reparse the config files.
 * 
 * @author acassin
 *
 */
@Singleton
@Lock(LockType.READ)
public class ConfigChangeDetector {
	private Logger logger = Logger.getLogger("Mascot configChange");
	@EJB
	private MascotConfig         config;
	private final Map<File,Long> modified_timestamps = new HashMap<File,Long>();
	
	public ConfigChangeDetector() {
		if (config != null) {
			File mascot_config_file = new File(config.getConfigFile());
			setTimestampIfExists(mascot_config_file);
			File enzymes = new File(mascot_config_file.getParentFile(), "enzymes");
			setTimestampIfExists(enzymes);
			File frag_rules = new File(mascot_config_file.getParentFile(), "fragmentation_rules");
			setTimestampIfExists(frag_rules);
			File masses = new File(mascot_config_file.getParentFile(), "masses");
			setTimestampIfExists(masses);
			File taxonomies = new File(mascot_config_file.getParentFile(), "taxonomy");
			setTimestampIfExists(taxonomies);
			File mods = new File(config.getModFile());
			setTimestampIfExists(mods);
		}
	}
	
	private void setTimestampIfExists(File f) {
		if (f != null && f.exists()) {
			modified_timestamps.put(f, f.lastModified());
		}
	}

	@Schedule(hour="*", minute="0,15,30,45", second="0")
	public void checkMascotConfigurationForChanges() {
		if (modified_timestamps.size() > 0) {
			int n_changed = 0;
			for (File f : modified_timestamps.keySet()) {
				if (f.lastModified() > modified_timestamps.get(f)) {
					n_changed++;
					modified_timestamps.put(f, f.lastModified());
				}
			}
			if (n_changed > 0) {
				try {
					logger.info("Mascot configuration change detected: reloading");
					config.parse();
				} catch (Exception e) {
					logger.warning(e.toString());
				}
			}
		} else {
			// try to fix missing config? log warning?
		}
	}
}
