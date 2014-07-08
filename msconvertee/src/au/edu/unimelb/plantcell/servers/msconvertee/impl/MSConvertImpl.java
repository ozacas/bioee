package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.exec.CommandLine;

import au.edu.unimelb.plantcell.servers.core.AbstractWebService;
import au.edu.unimelb.plantcell.servers.core.SendMessage;
import au.edu.unimelb.plantcell.servers.core.TempDirectory;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.DataFileType;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.ListOfDataFile;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.ObjectFactory;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvertFeature;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;


@Stateless
@MTOM			// this is preferred way to request MTOM
@BindingType(value=SOAPBinding.SOAP11HTTP_MTOM_BINDING)		// but some old app servers need this instead
@WebService(endpointInterface = "au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert")
public class MSConvertImpl extends AbstractWebService implements MSConvert {
	private final static Logger logger = Logger.getLogger("MSConvert Service");

	/*
	 * We inject the current mascot configuration state into this service so we can validate
	 * the users search parameters against the current (known) configuration
	 */
	@EJB
	private MSConvertConfig config;
	
	@Resource
	private ConnectionFactory connectionFactory;
	@Resource(name="MSConvertJobProcessor")		// will be auto-delivered to this MDB
	private Queue jobQueue;
	@Resource(name="MSConvertRunQueue")
	private Queue runQueue;	// waiting to run or currently running
	@Resource(name="MSConvertDoneQueue")
	private Queue doneQueue;
	@Resource(name="TEMP_FOLDER")
	private String temp_folder;
	
	/**
	 * Checks internal and job state and throws if something is wrong with the input job specification
	 * @param job must not be null
	 * @throws SOAPException
	 */
	private void checkOkToRunJob(final ProteowizardJob job) throws SOAPException {
		if (config == null) {
			throw new SOAPException("No msconvert configuration!");
		}
		if (job == null) {
			throw new SOAPException("No job to run!");
		}
		validateJob(job);
		checkFreeStorageIsAvailable();
	}
	
	@Override
	public String convert(final ProteowizardJob job, @XmlMimeType("application/octet-stream")
							final DataHandler[] input_data_files) throws SOAPException {
		checkOkToRunJob(job);
		
		// if we get here the job is do-able so....
		try {
			MSConvertJob         j = new MSConvertJob(job, input_data_files, getTempDirectory());
			// add to pending job queue
			new SendMessage(logger, getConnectionFactory(), jobQueue, 0).send(j);
			return j.getJobID();
		} catch (Exception e) {
			throw new SOAPException(e);
		}
	}

	@Override
	public String debugConvert(final ProteowizardJob j, 
						@XmlMimeType("application/octet-stream") final DataHandler[] input_data_files) 
								throws SOAPException {
		checkOkToRunJob(j);
		
		try {
			// this code must match ConversionJobThread.run() for the tests to be accurate... ;)
			MSConvertJob         job = new MSConvertJob(j, input_data_files, getTempDirectory());
	    	CommandLine      cmdLine = new MSConvertCommandLineBuilder(config).
	    								fromJob(job).
	    								setOutputFolder(job.getOutputFolder()).build();
	    	return cmdLine.toString();
		} catch (IOException|JAXBException ioe) {
			throw new SOAPException(ioe);
		}
	}
	
	/**
	 * Synchronized to prevent multiple threads trying to create the folder at the same time
	 * @return
	 * @throws IOException
	 */
	private synchronized File getTempDirectory() throws IOException {
    	File f = config.getTemporaryFileFolder();
    	if (f.exists() && f.isDirectory()) {
    		return f;
    	}
    	if (!f.mkdir()) {
    		throw new IOException("Cannot create temporary data directory: "+f.getAbsolutePath());
    	}
    	return f;
	}
	@Override
	public MSConvertFeature[] allFeatures() throws SOAPException {
		return MSConvertFeature.values();
	}
	
	@Override
	public boolean supportsAllFeatures(final MSConvertFeature[] features) throws SOAPException {
		if (config == null)
			throw new SOAPException("No MSConvert configuration - check your installation!");
		return config.supportsAllFeatures(features);
	}

	@Override
	public boolean supportsAnyFeature(final MSConvertFeature[] features)
			throws SOAPException {
		if (config == null)
			throw new SOAPException("No MSConvert configuration - check your installation!");
		return config.supportsAnyFeature(features);
	}
	
	@Override
	public List<MSConvertFeature> supportedFeatures() throws SOAPException {
		if (config == null)
			throw new SOAPException("No MSConvert configuration - check your installation!");
		ArrayList<MSConvertFeature> ret = new ArrayList<MSConvertFeature>();
		for (MSConvertFeature f : MSConvertFeature.values()) {
			if (config.supportsFeature(f)) {
				ret.add(f);
			}
		}
		return ret;
	}
	
	@Override
	public String getStatus(String jobID) throws SOAPException {
		if (jobQueue == null || doneQueue == null || runQueue == null) {
			throw new SOAPException("ERROR: NO QUEUE!");
		}
		try {
			TextMessage pending = findMessage(jobQueue, jobID);
			if (pending != null) {
				return "QUEUED";
			}
			TextMessage done = findMessage(doneQueue, jobID);
			if (done != null) {
				return "FINISHED";
			}
			TextMessage run = findMessage(runQueue, jobID);
			if (run != null) {
				return "RUNNING";
			}
			
		} catch (SOAPException e) {
			e.printStackTrace();
			// fallthru...
		}
		
		return "UNKNOWN";
	}

	@Override
	public void purgeJobFiles(final String jobID) throws SOAPException {
		String status = getStatus(jobID);
		if (! status.equals("FINISHED")) {
			throw new SOAPException("Can only purge finished jobs: got status "+status+" for "+jobID);
		}
		try {
			File td = getTempDirectory();
			String td_path = td.getAbsolutePath();
			File f = MSConvertJob.getJobDirectory(jobID, td);
			if (f == null || !f.isDirectory() || f.getAbsolutePath().indexOf("..") >= 0 || !f.getAbsolutePath().startsWith(td_path)) {
				String msg = "Refusing to delete suspicious folder: "+f.getAbsolutePath();
				logger.warning(msg);
				throw new SOAPException(msg);
			}
			logger.info("deleting job folder: "+f.getAbsolutePath());
			TempDirectory.deleteRecursive(f);
		} catch (IOException ioe) {
			throw new SOAPException(ioe);
		}
	}

	@Override
	public void validateJob(final ProteowizardJob job) throws SOAPException {
		new ProteowizardJobValidator().validate(job);
	}

	@Override
	protected ConnectionFactory getConnectionFactory() throws SOAPException {
		if (connectionFactory == null) {
			throw new SOAPException("No connection to broker!");
		}
		return connectionFactory;
	}

	@Override
	protected String getMessageIDPropertyName() {
		return MSConvertConstants.MSCONVERT_MESSAGE_ID_PROPERTY;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public ListOfDataFile getResults(final String jobID) throws SOAPException {
		String status = getStatus(jobID);
		if (!status.startsWith("FINISH")) {
			throw new SOAPException("Can only retrieve results for finished jobs: "+status+" "+jobID);
		}
		try {
			File folder = MSConvertJob.getJobDirectory(jobID, getTempDirectory());
			if (folder.isDirectory()) {
				final ObjectFactory of = new ObjectFactory();
				final ListOfDataFile l = of.createListOfDataFile();
				folder.listFiles(new FileFilter() {

					@Override
					public boolean accept(final File p) {
						boolean accept = (p.isFile() && p.canRead());
						if (accept) {
							DataFileType df = of.createDataFileType();
							df.setSuggestedName(p.getName());
							df.setRequiredLength((int)p.length());
							df.setIsOutputLog(p.getName().equals("stdout"));
							df.setIsErrorLog(p.getName().equals("stderr"));
							try {
								df.setData(new DataHandler(p.toURI().toURL()));
								l.getDataFile().add(df);
							} catch (MalformedURLException e) {
								e.printStackTrace();
								return false;
							}
						}
						return accept;
					}
					
				});
				
				logger.info("Returning result comprising "+l.getDataFile().size()+ " data files.");
				return l;
			} else {
				throw new SOAPException("No such job folder: "+folder.getAbsolutePath());
			}
		} catch (IOException ioe) {
			throw new SOAPException(ioe);
		}
	}
}
