package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mascot database definition, as read from a mascot.dat file
 * 
 * @author acassin
 *
 */
public class MascotDatabase {
	public enum  DatabaseType { AA, NA };
	
	private DatabaseType sequenceDatabaseType;
	private String glob;		// remember mascot sequence files must include a wildcard, so we use glob matching to find the files...s
	private String name;
	private boolean memory_map;
	private boolean lock_in_memory;
	private boolean local_reference_file;
	private int number_of_blocks;
	private int number_of_threads;
	private int accession_parse_rule;	// index into MascotConfig.getAccessionRules()
	private int description_parse_rule;	// index into MascotConfig.getDescriptionRules()
	private int ref_accession_parse_rule;	// index into MascotConfig.getAccessionRules() but used for local reference file not FASTA
	private int taxonomy_block;	// index into MascotConfig.getTaxonomyBlocks()
	

	public MascotDatabase() {
		this(DatabaseType.AA, "");
	}
	
	public MascotDatabase(DatabaseType dt, String name) {
		sequenceDatabaseType = dt;
		this.name = name;
		setGlob(null);
		setMemoryMapped(false);
		setNumberOfBlocks(1);
		setNumberOfThreads(1);
		setLockInMemory(false);
		setLocalReferenceFile(false);
		setAccessionParseRule(0);
		setDescriptionParseRule(0);
		setLocalReferenceAccessionRule(0);
		setTaxonomyBlock(0);
	}
	
	public int getLocalReferenceAccessionRule() {
		return ref_accession_parse_rule;
	}
	
	public void setLocalReferenceAccessionRule(int i) {
		ref_accession_parse_rule = i;
	}

	public int getTaxonomyBlock() {
		return taxonomy_block;
	}
	
	public void setTaxonomyBlock(int i) {
		taxonomy_block = i;
	}
	
	public int getDescriptionParseRule() {
		return description_parse_rule;
	}

	public void setDescriptionParseRule(int i) {
		description_parse_rule = i;
	}

	public int getAccessionParseRule() {
		return accession_parse_rule;
	}
	
	public void setAccessionParseRule(int i) {
		accession_parse_rule = i;
	}

	public void setLocalReferenceFile(boolean b) {
		local_reference_file = b;
	}
	
	public boolean hasLocalReferenceFile() {
		return local_reference_file;
	}

	public void setLockInMemory(boolean b) {
		lock_in_memory = b;
	}
	
	@SuppressWarnings("unused")
	private boolean getLockInMemory() {
		return lock_in_memory;
	}

	public void setNumberOfThreads(int i) {
		number_of_threads = i;
	}

	public void setNumberOfBlocks(int i) {
		number_of_blocks = i;
	}

	public int getNumberOfBlocks() {
		return number_of_blocks;
	}
	
	public int getNumberOfThreads() {
		return number_of_threads;
	}
	
	public boolean getMemoryMapped() {
		return memory_map;
	}
	
	public void setMemoryMapped(boolean b) {
		memory_map = b;
	}

	public String getGlob() {
		return glob;
	}
	
	public void setGlob(String new_glob) {
		this.glob = new_glob;
	}

	public DatabaseType getType() {
		return sequenceDatabaseType;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasName() {
		return (name != null && name.length() > 0);
	}
	
	/**
	 * Return the list of sequence files comprising the database. Usually one file, but since there is a wildcard it could be more
	 * eg. one file per chromosome/scaffold/...
	 * 
	 * @return
	 */
	public List<File> getSequenceFiles() throws IOException {
		final ArrayList<File> ret = new ArrayList<File>();
		if (glob != null) {
			File f = new File(glob);
			while (f != null && !(f.exists() && f.isDirectory())) {
				f = f.getParentFile();
			}
			if (f == null)
				throw new IOException("Cannot find "+glob);
			
		    final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:"+glob);
		    Files.walkFileTree(Paths.get(f.toURI()), new SimpleFileVisitor<Path>() {
		        @Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		            if (matcher.matches(file)) {
		                ret.add(file.toFile());
		            }
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		            return FileVisitResult.CONTINUE;
		        }
		    });
		
		}
		return ret;
	}

	public boolean hasName(String databaseName) {
		return (hasName() && getName().equals(databaseName));
	}
}
