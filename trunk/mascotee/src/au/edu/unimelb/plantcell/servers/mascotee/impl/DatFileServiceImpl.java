package au.edu.unimelb.plantcell.servers.mascotee.impl;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascotee.endpoints.DatFileService;


/**
 * Implementation of the service as defined by {@link DatFileService}. All webservice methods
 * can be accessed by user roles (as defined on the server-side) as either 'MascotWSUser' or
 * 'MascotWSAdmin'.
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
@MTOM
@WebService(serviceName="DatFileService", 
endpointInterface="au.edu.unimelb.plantcell.servers.mascotee.endpoints.DatFileService", 
targetNamespace="http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl")
@BindingType(value=SOAPBinding.SOAP12HTTP_MTOM_BINDING)
public class DatFileServiceImpl implements DatFileService {
	private final static Logger l = Logger.getLogger("DatFileService");
	@EJB private MascotConfig mascot_config;
	private final static SimpleDateFormat accepted_date_format = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	public String[] getDatFilesSince(final String YYYYMMdd) throws SOAPException {
		assert(YYYYMMdd != null);
		ArrayList<String> ret = new ArrayList<String>();
		File data_root = mascot_config.getDataRootFolder();
		
		if (! data_root.isDirectory()) {
			throw new SOAPException(data_root.getAbsolutePath()+" must be a directory with mascot dat files!");
		}
		if (YYYYMMdd == null || YYYYMMdd.length() != 8)
			throw new SOAPException("Date must be in YYYYMMdd (ie. exactly 8 digits)");
	
		final Calendar desired_date;
		try {
			desired_date = makeCalendar(YYYYMMdd);
		} catch (ParseException pe) {
			throw new SOAPException("Invalid date: "+pe.getMessage());
		}
		
		// find available folders within the data root folder
		File[] datfile_folders  = data_root.listFiles(new FileFilter() {

			@Override
			public boolean accept(File d) {
				//l.info("Processing folder: "+d.getAbsolutePath());
				try {
					return (d.isDirectory() && sinceOrEqualsDate(desired_date, d.getName()));
				} catch (ParseException pe) {
					// be silent... will just ignore bad folders like this
					return false;
				}
			}
			
		});
		l.info("Found "+datfile_folders.length+" suitable data folders since "+YYYYMMdd+" when scanning "+data_root.getAbsolutePath());
		
		// find dat files within each dated folder which should be reported to the user
		for (File datfile_folder : datfile_folders) {
			File[] dat_files = datfile_folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File path) {
					return (path.isFile() && path.canRead() && okPath(path.getName()));
				}
				
			});
			l.info("After processing "+datfile_folder+", found "+dat_files.length+ " suitable dat files.");
			
			// this function only reports the portion of the file (without the root)
			for (File f : dat_files) {
				ret.add(convertToRootRelativePath(f, data_root));
			}
		}
		
		l.info("Reporting "+ret.size()+" suitable dat files since "+YYYYMMdd);
		
		return ret.toArray(new String[0]);
	}

	private Calendar makeCalendar(final String YYYYMMdd) throws ParseException {
		Calendar ret = Calendar.getInstance();
		ret.setTime(accepted_date_format.parse(YYYYMMdd));
		ret.set(Calendar.MINUTE, 0);
		ret.set(Calendar.HOUR, 0);
		ret.set(Calendar.SECOND, 0);
		return ret;
	}

	/**
	 * Given a file (eg. /main/mascot/data/20140321/F00010.dat) and a root folder (/mascot/mascot/data) this
	 * method just returns the 20140321/F00010.dat portion as a string to the caller
	 * 
	 * @param f must not be null
	 * @param mascot_root must not be null
	 * @return
	 */
	private String convertToRootRelativePath(final File f, final File data_root) {
		assert(f != null && data_root != null);
		String root = data_root.getAbsolutePath();
		String file = f.getAbsolutePath();
		if (file.startsWith(root)) {
			file = file.substring(root.length());
		}
		while (file.startsWith(File.separator)) {
			file = file.substring(1);
		}
		
		return file;
	}

	/**
	 * Returns true if the specified name is a likely dat file, otherwise false (eg. for log/error files)
	 * @param name
	 * @return
	 */
	protected boolean okPath(final String name) {
		if (!name.endsWith(".dat"))
			return false;
		return (name.matches("^F\\d+\\.dat$"));
	}

	/**
	 * Returns true if the folder_date is newer or equal to the desired date 
	 * @param desired_date may not be null
	 * @param folder_date may not be null and must contain a string of the form YYYYMMDD eg. 20130408
	 * @return
	 */
	protected boolean sinceOrEqualsDate(final Calendar desired_date, final String folder_date) throws ParseException {
		assert(desired_date != null && folder_date != null);
		
		Calendar d = makeCalendar(folder_date);
		return (d.after(desired_date) || d.equals(desired_date));
	}

	/**
	 * Throws if the dat file is not as required for correct web service usage
	 * @param dat_file
	 * @throws SOAPException
	 */
	private void validateDatFile(final String dat_file) throws SOAPException {
		if (dat_file == null || dat_file.indexOf("..") >= 0 || dat_file.startsWith("/") || !dat_file.matches("^\\d{8}/F\\d+\\.dat$")) {
			throw new SOAPException("Illegal mascot dat file name: must be of the form: YYYYMMDD/F\\d+.dat - got "+dat_file);
		}
	}
	
	/**
	 * Given a string as returned by <code>getDatFiles()</code> this function returns the data in
	 * raw mascot format for the DatFile.
	 */
	@Override
	public @XmlMimeType("application/octet-stream") 
				DataHandler getDatFile(final String dat_file) throws SOAPException {
		validateDatFile(dat_file);
		File f         = new File(mascot_config.getDataRootFolder(), dat_file);
		try {
			return new DataHandler(f.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new SOAPException("Unable to read: "+f.getAbsolutePath());
		}
	}

	/**
	 * Like getDatFile(String) but returns a REST-style URL for the dat file to avoid MTOM-out-of-memory problems
	 */
	@Override
	public String getDatFileURL(final String dat_file) throws SOAPException {
		validateDatFile(dat_file);
		String url = mascot_config.getURL() + "rest/results/dat/" + 
						new String(Base64.getEncoder().encode(dat_file.getBytes()));
		return url;
	}
	
	@Override
	public String getDatedDatFilePath(final String datname) throws SOAPException {
		if (datname == null || !datname.matches("^F[\\d\\.]+dat$"))
			throw new SOAPException("Illegal dat file name: "+datname);
		
		File data_root = mascot_config.getDataRootFolder();
		File[] datfile_folders  = data_root.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				File try_me = new File(pathname, datname);
				if (try_me.exists() && try_me.canRead() && try_me.isFile())
					return true;
				return false;
			}
		});
		
		// no such folder?
		if (datfile_folders.length < 1)
			return null;
		
		// dat filename not unique? we throw under this condition as it is not likely a valid mascot installation
		// perhaps administrative error?
		if (datfile_folders.length > 1) 
			throw new SOAPException("More than one dat file matches "+datname+" - problem with mascot installation?");
		
		return convertToRootRelativePath(new File(datfile_folders[0], datname), data_root);
	}
	
	
}
