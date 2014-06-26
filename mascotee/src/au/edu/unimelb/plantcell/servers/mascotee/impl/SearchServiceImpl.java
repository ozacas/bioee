package au.edu.unimelb.plantcell.servers.mascotee.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jws.WebService;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import au.edu.unimelb.plantcell.servers.core.AbstractWebService;
import au.edu.unimelb.plantcell.servers.core.SendMessage;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Search;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascotee.MascotEEConstants;
import au.edu.unimelb.plantcell.servers.mascotee.MascotJob;
import au.edu.unimelb.plantcell.servers.mascotee.SearchValidator;
import au.edu.unimelb.plantcell.servers.mascotee.endpoints.DatFileService;
import au.edu.unimelb.plantcell.servers.mascotee.endpoints.SearchService;

/**
 * Provides a web-service interface to mascot searches. Search parameters are specified as XML (according
 * to schema as specified by MascotEE)
 * 
 * @author acassin
 *
 */
@Stateless
@WebService(serviceName="SearchService", 
            endpointInterface="au.edu.unimelb.plantcell.servers.mascotee.endpoints.SearchService", 
            targetNamespace="http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl")
@BindingType(value = SOAPBinding.SOAP12HTTP_MTOM_BINDING)
public class SearchServiceImpl extends AbstractWebService implements SearchService {
	private final static Logger logger = Logger.getLogger("Mascot Search Service");

	/*
	 * We inject the current mascot configuration state into this service so we can validate
	 * the users search parameters against the current (known) configuration
	 */
	@EJB
	private MascotConfig config;
	
	@Resource
	private ConnectionFactory connectionFactory;
	@Resource(name="mascotee/MascotJobProcessor")		// will be auto-delivered to this MDB
	private Queue jobQueue;
	@Resource(name="mascotee/runQueue")
	private Queue runQueue;	// waiting to run or currently running
	@Resource(name="mascotee/doneQueue")
	private Queue doneQueue;

	/**
	 * Delete the files associated with the specified MascotEE job. Does not delete the mascot results file (.dat)
	 * although maybe one day it will. This method cannot be used to cancel jobs which have been submitted. No effective
	 * mechanism exists for that. Maybe one day...
	 */
	@Override
	public void purgeJob(String jobID) throws SOAPException {
		TextMessage completedJob = findMessage(doneQueue, jobID);
		if (completedJob == null)
			throw new SOAPException("Job "+jobID+" is either expired is not done!");
		try {
			logger.info("Purging job files for "+jobID);
			MascotJob j = MascotJob.unmarshal(new StringReader(completedJob.getText()));
			j.cleanupInputDataFiles(logger);
			// HACK TODO FIXME: purge j from completedQueue?
		} catch (JMSException|JAXBException e) {
			throw new SOAPException(e);
		}
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
	public String getResultsDatFile(String jobID) throws SOAPException {
		TextMessage completed = findMessage(doneQueue, jobID);
		if (completed == null)
			throw new SOAPException("ERROR: no such job has been completed (it may have expired)!");
		try {
			return extractDatFile(completed);
		} catch (SOAPException e) {
			return null;
		}
	}
	
	private DatFileService getDatFileService() throws SOAPException {
		String dat_service_url = config.getURL() + "DatFileService?wsdl";
		QName  dat_ns = 
				new QName("http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl", "DatFileService");
		Service srv = null;
		logger.info("MascotEE dat file service URL is "+dat_service_url);
		try {
			srv = Service.create(new URL(dat_service_url), dat_ns);
		} catch (MalformedURLException e) {
			throw new SOAPException(e);
		}
		
		if (srv == null) {
			throw new SOAPException("Cannot access datFileService!");
		} 
        DatFileService datFileService = srv.getPort(DatFileService.class);
        BindingProvider bp = (BindingProvider) datFileService;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
        
        return datFileService;
	}
	
	@Override
	public @XmlMimeType("application/octet-stream") DataHandler getResults(String jobID) throws SOAPException {
		DatFileService dfs = getDatFileService();
		logger.info("Downloading results for "+jobID);
        String dat_file     = getResultsDatFile(jobID);
        String datedDatFile = dfs.getDatedDatFilePath(dat_file);
        if (datedDatFile == null) {
        	throw new SOAPException("Could not locate "+dat_file+" in mascot data folder!");
        }
        return dfs.getDatFile(datedDatFile);
	}
	
	/**
	 * Extract the mascot results .dat file name or throw a SOAPException if something went wrong with the job
	 * 
	 * @param completed
	 * @return
	 * @throws SOAPException
	 */
	private String extractDatFile(final TextMessage completed) throws SOAPException {
		final String RESULT_FIELD = "result_file=";
		
		try {
			String text = completed.getText();
			String ret = null;
			for (String line : text.split("\n")) {
				if (line.startsWith(RESULT_FIELD)) {
					ret = line.substring(RESULT_FIELD.length());
				}
			}
			if (ret == null)
				throw new SOAPException("No results data file available for job (perhaps an error?)!");
			
			return ret;
		} catch (JMSException e) {
			throw new SOAPException(e);
		}
	}

	
	
	private static URL[] saveData(final DataHandler dh) throws IOException {
		if (dh == null) {
			return null;
		}
		InputStream is = null;
		FileOutputStream fos = null;
		
		try {
			is  = dh.getInputStream();
			File       out = File.createTempFile("mascot_input", "_data.raw");
			fos = new FileOutputStream(out);
			byte[]           buf = new byte[128 * 1024];
			int cnt;
			while ((cnt = is.read(buf, 0, buf.length)) >= 0) {
				fos.write(buf, 0, cnt);
			}
			return new URL[] { out.toURI().toURL() };
		} finally {
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	@Override
	public String validateAndSearch(final Search s) throws SOAPException {
		checkFreeStorageIsAvailable();
		validateParameters(s);

		try {
			MascotJob job = makeJobForJobQueue(s);
			logger.info("Submitting message for mascot search: "+job.toString());
			new SendMessage(logger, getConnectionFactory(), jobQueue, 0).send(job);
			return job.getJobID();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.warning("Unable to submit search!");
			throw new SOAPException(ioe);
		}
	}

	private MascotJob makeJobForJobQueue(final Search s) throws IOException {
		assert(s != null);
		
		DataHandler dh = null;
		String suggestedFile = null;
		if (s.getMsMsIonSearch() != null && s.getMsMsIonSearch().getData().getFile() != null) {
			dh = s.getMsMsIonSearch().getData().getFile();
			// dh is only reference to data so serialisation to XML is small...
			s.getMsMsIonSearch().getData().setFile(null);
			suggestedFile = s.getMsMsIonSearch().getData().getSuggestedFileName();
		}
		
		if (dh == null) {
			return new MascotJob(s);
		} else {
			logger.info("Got peaklist data: "+suggestedFile);
			URL[] peak_list_urls = saveData(dh);
			logger.info("Got "+peak_list_urls.length+" URLs for user-supplied data.");
			return new MascotJob(s, peak_list_urls);
		}
	}

	@Override
	public void validateParameters(final Search s) throws SOAPException {
		if (s == null ) {
			throw new SOAPException("Bogus search!");
		}
		if (config == null) {
			throw new SOAPException("Cannot get current mascot configuration to validate input!");
		}
		new SearchValidator(s, config).validate();
	}

	@Override
	public String getResultsURL(String jobID) throws SOAPException {
		DatFileService dfs = getDatFileService();
		logger.info("Getting result URL for "+jobID);
        String dat_file     = getResultsDatFile(jobID);
        String datedDatFile = dfs.getDatedDatFilePath(dat_file);
        String url = config.getURL() + "/results/get/" + datedDatFile;
        return url;
	}

	@Override
	public long getExpectedResultsBytes(String jobID) throws SOAPException {
		DatFileService dfs = getDatFileService();
		String dat_file = getResultsDatFile(jobID);
		String datedDatFile = dfs.getDatedDatFilePath(dat_file);
		File dat = new File(config.getDataRootFolder(), datedDatFile);
		if (!dat.exists()) {
			throw new SOAPException("No such data file: "+dat.getAbsolutePath());
		}
		return dat.length();
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
		return MascotEEConstants.MASCOTEE_ID_PROPERTY;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
