package au.edu.unimelb.plantcell.servers.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;


/**
 * This class cannot be exposed via the web services API or streaming of MTOM data will not work correctly. So the SEI is
 * written in terms of {@link DataHandler} rather than this class. But its convenient, so we use it internally for the server side.
 * 
 * @author andrew.cassin
 *
 */
public class RawFile {
	private File m_raw_file;
	
	public RawFile(File f) {
		assert(f != null);
		m_raw_file = f;
	}

	public RawFile(DataHandler raw_file, long expected_size_bytes, String extension) throws IOException {
		m_raw_file = File.createTempFile("input_raw_data", extension);
		raw_file.writeTo(new FileOutputStream(m_raw_file));
		long len = m_raw_file.length();
		
		if (len < expected_size_bytes)
			throw new IOException("Expected "+expected_size_bytes+" of data, but got only "+len+" bytes.");
	}
	
	public File getFile() {
		return m_raw_file;
	}

	public String getName() {
		return m_raw_file.getName();
	}
	
	public long getLength() {
		return m_raw_file.length();
	}

	public DataHandler getDH() {
		return new DataHandler(new FileDataSource(getFile()));
	}
}
