package au.edu.unimelb.plantcell.servers.mascot.core.v2.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.ClusterConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.ConfigParam;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.CronConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotDatabase;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotDatabase.DatabaseType;

/**
 * Parses a Mascot 2.0x mascot.dat file (not XML). Not all entries in the mascot.dat file are currently supported.
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class MascotDatParser {
	private File         path;
	private MascotConfig config;
	
	public MascotDatParser(final MascotConfig config, final File mascot_dat_path) {
		assert(config != null && mascot_dat_path != null);
		path        = mascot_dat_path;
		this.config = config;
	}
	
	/**
	 * Process the file specified in the constructor call into the specified (at construct time) config instance.
	 * 
	 * @throws IOException thrown if an error during parse occurs. Config instance will be in a partially initialised state.
	 */
	public void parse() throws Exception {
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new FileReader(path));
			String line;
			Stack<MascotEntryHandler> mascot_entry_stack = new Stack<MascotEntryHandler>();
			
			while ((line = rdr.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				
				if (updateSectionHandlers(line, mascot_entry_stack)) {
					continue;
				}
				if (mascot_entry_stack.size() < 1) {
					continue;
				}
				
				mascot_entry_stack.peek().addLine(line, config);
			}
		} finally {
			if (rdr != null) {
				rdr.close();
			}
		}
	}

	private boolean updateSectionHandlers(final String line, final Stack<MascotEntryHandler> handlers) {
		assert(line != null);
		if (line.equals("Databases")) {
			handlers.push(new DatabaseHandler());
			return true;
		}
		// TODO FIXME: not currently implemented
		/*if (line.startsWith("Taxonomy_")) {
			handlers.push(new TaxonomyHandler());
			return true;
		}*/
		if (line.equals("Cron")) {
			handlers.push(new CronHandler());
			return true;
		}
		if (line.equals("Options")) {
			handlers.push(new OptionsHandler());
			return true;
		}
		if (line.equals("Cluster")) {
			handlers.push(new ClusterHandler());
			return true;
		}
		if (line.equals("PARSE")) {
			handlers.push(new ParseHandler());
			return true;
		}
		if (line.equals("WWW")) {
			handlers.push(new WWWHandler());
			return true;
		}
		if (line.equals("UniGene")) {
			handlers.push(new UniGeneHandler());
			return true;
		}
		if (line.equals("end") && handlers.size() > 0) {
			handlers.peek().endOfSection(config);
			handlers.pop();
			return true;
		}
		
		return false;
	}

	// responsible for parsing the database section lines
	private class DatabaseHandler implements MascotEntryHandler {

		@Override
		public void addLine(String line, MascotConfig config) throws Exception {
			assert(config != null && line != null);
			
			String[] fields = line.split("\\s+");
			if (fields.length < 14) {
				if (line.trim().length() < 1) {
					return;
				}
				throw new IOException("Incorrect minimum number of database fields (at least 14 required): "+line);
			}
			DatabaseType   dt = fields[2].equals("AA") ? DatabaseType.AA : DatabaseType.NA;
			MascotDatabase db = new MascotDatabase(dt, fields[0]);
			db.setGlob(fields[1].trim());
			
			db.setMemoryMapped(Integer.valueOf(fields[5]) > 0 ? true : false);
			db.setNumberOfBlocks(Integer.valueOf(fields[6]));
			db.setNumberOfThreads(Integer.valueOf(fields[7]));
			db.setLockInMemory(Integer.valueOf(fields[8]) > 0 ? true : false);
			db.setLocalReferenceFile(Integer.valueOf(fields[9]) > 0 ? true : false);
			db.setAccessionParseRule(Integer.valueOf(fields[10]));
			db.setDescriptionParseRule(Integer.valueOf(fields[11]));
			db.setLocalReferenceAccessionRule(Integer.valueOf(fields[12]));
			db.setTaxonomyBlock(Integer.valueOf(fields[13]));
			config.getDatabases().add(db);
		}

		@Override
		public void endOfSection(MascotConfig c) {
			// NO-OP
		}

	}

	public class CronHandler implements MascotEntryHandler {
		private CronConfig cc = new CronConfig();
		
		@Override
		public void addLine(String line, MascotConfig add_to_me)
				throws Exception {
			String[] fields = line.trim().split("\\s+");
			if (fields.length == 2) {
				cc.getParameters().add(new ConfigParam(fields[0], fields[1]));
			}
		}

		@Override
		public void endOfSection(MascotConfig c) {
			c.setCronConfig(cc);
		}

	}

	public class OptionsHandler implements MascotEntryHandler {

		@Override
		public void addLine(String line, MascotConfig add_to_me)
				throws Exception {
			String[] fields = line.trim().split("\\s+");
			if (fields.length == 2) {
				add_to_me.getConfigurationParams().add(new ConfigParam(fields[0], fields[1]));
			}
		}

		@Override
		public void endOfSection(MascotConfig c) {
			// NO-OP
		}

	}
	

	public class ClusterHandler implements MascotEntryHandler {
		private final ClusterConfig cc = new ClusterConfig();
		
		@Override
		public void addLine(String line, MascotConfig add_to_me)
				throws Exception {
			String[] fields = line.trim().split("\\s+");
			if (fields.length == 2) {
				cc.getParameters().add(new ConfigParam(fields[0], fields[1]));
			}
		}

		@Override
		public void endOfSection(MascotConfig c) {
			c.setClusterConfig(cc);
		}

	}

}
