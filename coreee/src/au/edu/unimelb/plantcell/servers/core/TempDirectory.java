package au.edu.unimelb.plantcell.servers.core;

import java.io.File;
import java.io.IOException;

/**
 * Abstraction used for a folder which contains only temporary files, as is often the case with 
 * distributed oriented data handling. Contains utility methods surrounding temporary folders.
 * 
 * @author acassin
 *
 */
public class TempDirectory {
    private File m_f;

    /**
     * Creates a temporary folder in the platform-specific location eg. /tmp for linux
     * of the form: temp<id>_directory.dir
     * 
     * @throws IOException
     */
    public TempDirectory() throws IOException {
            this("temp", "_directory.dir", null);
    }

    /**
     * Creates a temporary folder in the specified <code>parent_dir</code> of the specified
     * <code>prefix</code> and <code>suffix</code>
     * 
     * @param prefix
     * @param suffix
     * @param parent_dir
     * @throws IOException
     */
    public TempDirectory(String prefix, String suffix, File parent_dir) throws IOException {
    	 this(File.createTempFile(prefix, suffix, parent_dir));
    }
    
    public TempDirectory(File f) throws IOException {
            assert(f != null);
            f.delete();             // HACK: race condition
            if (!f.mkdir())
                    throw new IOException("Cannot create temporary directory");
            m_f = f;
    }

    public File asFile() {
            return m_f;
    }

    /**
     * @warning WILL DELETE ALL FOLDERS IN THE THIS FOLDER HEIRACHY
     * @throws IOException
     */
    public final void deleteRecursive() throws IOException {
            // safety first: throw if m_f does not end in .dir
            if (!m_f.getName().toLowerCase().endsWith(".dir"))
                    throw new IOException("SAFETY FAILURE: .dir extension not present on temp folder!");
            deleteRecursive(m_f);
    }

    private void deleteRecursive(File file) throws IOException {
            if (file.isDirectory()) {
	            if(file.list().length==0){
	               file.delete();
	            } else {
	               String files[] = file.list();
	               for (String s : files) {
	                            File kid = new File(file, s);
	                            deleteRecursive(kid);
	               }
	               if(file.list().length==0){
	                 file.delete();
	               }
	            }
		    } else {
		            file.delete();
		    }
    }
   
}

