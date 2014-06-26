package au.edu.unimelb.plantcell.servers.proteowizard.impl;

import java.io.File;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.soap.SOAPException;
import javax.xml.ws.soap.MTOM;

import au.edu.unimelb.plantcell.servers.core.AbstractWebService;
import au.edu.unimelb.plantcell.servers.core.SendMessage;
import au.edu.unimelb.plantcell.servers.proteowizard.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.proteowizard.endpoints.MSConvertFeature;
import au.edu.unimelb.plantcell.servers.proteowizard.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertConfig;
import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertConstants;
import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertJob;


@Stateless
@MTOM
@WebService(endpointInterface = "au.edu.unimelb.plantcell.servers.proteowizard.endpoints.MSConvert")
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
	
	@Override
	public String convert(final ProteowizardJob job) throws SOAPException {
		validateJob(job);
		checkFreeStorageIsAvailable();
		
		// if we get here the job is do-able so....
		try {
			MSConvertJob         j = new MSConvertJob(job);
        	File    temp_directory = new File(config.getTemporaryFileFolder(), "msconvert_output.dir");
			j.setDataFolder(temp_directory);
			// add to pending job queue
			new SendMessage(logger, getConnectionFactory(), jobQueue, 0).send(j);
			return j.getJobID();
		} catch (Exception e) {
			throw new SOAPException(e);
		}
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
	public int getResultFileCount(String jobID) throws SOAPException {
		String status = getStatus(jobID);
		if (status != null && status.equals("FINISHED")) {
			return 0;
		}
		throw new SOAPException("Job "+jobID+" is not valid!");
	}

	@Override
	public @XmlMimeType("application/octet-stream") DataHandler getResultFile(String jobID, int file_index) {
		return null;
	}

	@Override
	public String getResultFilename(String jobID, int file_index) {
		return "";
	}

	@Override 
	public long getResultFilesize(String jobID, int file_index) {
		return 0;
	}

	@Override
	public void purgeJobFiles(String jobID) {
		// TODO FIXME...
	}

	@Override
	public void validateJob(final ProteowizardJob job) throws SOAPException {
		throw new SOAPException("Not yet implemented!");
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

}
