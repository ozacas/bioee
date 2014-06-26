package au.edu.unimelb.plantcell.servers.mascotee;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.stream.StreamSource;

import au.edu.unimelb.plantcell.servers.core.jaxb.JobMessageType;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.ObjectFactory;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Search;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SearchType;
import au.edu.unimelb.plantcell.servers.mascot.html.MSMSIonSearch;
import au.edu.unimelb.plantcell.servers.mascot.html.PMFQuerySearch;
import au.edu.unimelb.plantcell.servers.mascot.html.SequenceQuerySearch;

/**
 * An string'ified instance of this is put into the ActiveMQ job queue.
 * All the data is persisted to disk and only references to the data are
 * kept in the message itself. It is up to the caller to make sure that all supplied data is
 * valid regardless of where the job is actually run (ie. which node in the cluster).
 * 
 * We assume the XML is valid: the web-service front-end has already done this by the time
 * we get it from the queue. So we dont validate it again here in the interests of speed.
 * 
 * @author acassin
 *
 */
@XmlRootElement
public class MascotJob extends JobMessageType {
	
	public MascotJob(final Search mascotee_search) {
		assert(mascotee_search != null);
		setJobID(makeRandomUUID());
		setSearch(mascotee_search);
	}

	public MascotJob(final Search mascotee_search, final URL[] data_urls) {
		this(mascotee_search);
		List<String> list = getInputData().getUrl();
		for (URL u : data_urls) {
			list.add(u.toExternalForm());
		}
	}

	/**
	 * Return a suitable value for the ID member of the specified instance. The instance is not modified in any way.
	 * The current implementation uses a type 4 randomly computed UUID
	 * 
	 * @return
	 */
	private String makeRandomUUID() {
		return "mascotee-" + UUID.randomUUID().toString();
	}
	
	private Search unmarshalXML() throws SOAPException, IOException {
		URL u = new URL(this.getInputParameters());
		return unmarshalXML(u.openStream());
	}

	/**
	 * Persists the search object into XML form using JAXB. It is recommended that the caller
	 * remove any large data from the object state before calling for performance reasons (the data files should
	 * be kept in separate files anyway)
	 * 
	 * @param mascotee_search
	 */
	private void setSearch(final Search mascotee_search) {
		try {
			File out       = File.createTempFile("mascotee_search", ".params.xml");
			JAXBContext jc = JAXBContext.newInstance(Search.class);
		    Marshaller   m = jc.createMarshaller();
		    JAXBElement<Search> mascotee = new ObjectFactory().createMascotEE(mascotee_search);
		    m.marshal(mascotee, out);
			setInputParameters(out.toURI().toURL().toExternalForm());
		} catch (IOException|JAXBException e) {
			e.printStackTrace();
			setInputParameters(null);
		} 
	}
	
	/**
	 * load the XML associated with the specified (file) URL and return it as a String instance
	 * @param mascotee_xml_url must not be null
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String loadXML(URL mascotee_xml_url) throws IOException {
		StringWriter    sw = new StringWriter();
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new InputStreamReader(mascotee_xml_url.openStream()));
			String line;
			while ((line = rdr.readLine()) != null) {
				sw.append(line);
			}
			sw.close();
			return sw.toString();
		} finally {
			if (rdr != null) {
				rdr.close();
			}
		}
	}

	private static Search unmarshalXML(final InputStream mascotee_stream) throws SOAPException {
		if (mascotee_stream == null) {
			throw new SOAPException("Unable to load MascotEE xml!");
		}
		
		try {
			 JAXBContext jc = JAXBContext.newInstance(Search.class);
		     Unmarshaller unmarshaller = jc.createUnmarshaller();
		     JAXBElement<Search> mascotee = unmarshaller.unmarshal(new StreamSource(mascotee_stream), Search.class);
		     if (mascotee == null)
		    	 throw new SOAPException("Invalid MascotEE XML!");
		     return mascotee.getValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SOAPException(e.getMessage());
		}
	}

	private boolean hasDataURLs() {
		return (getInputData().getUrl().size() > 0);
	}
	
	private URL getInputDataURL(int idx) throws SOAPException {
		try {
			return new URL(getInputData().getUrl().get(idx));
		} catch (MalformedURLException e) {
			throw new SOAPException(e);
		}
	}
	
	/**
	 * Add all URLs representing files in the filesystem which should be cleaned up now that the job is finished.
	 * @param cleanup_list
	 */
	private void addAllInputDataURLs(List<URL> cleanup_list) {
		try {
			URL u = new URL(this.getInputParameters());
			cleanup_list.add(u);
			for (String s : this.getInputData().getUrl()) {
				cleanup_list.add(new URL(s));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			cleanup_list.clear();		// dont clean anything up, we assume the operators will want it
		}
	}
	
	@Override
	public String toString() {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext ctx = JAXBContext.newInstance(MascotJob.class);
			Marshaller m = ctx.createMarshaller();
			m.marshal(this, writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Submits the specified job to the specified mascot server. Will block the calling thread until mascot
	 * issues an error or the search completes.
	 * @return the dat file number
	 * @param mascot_config must not be null
	 */
	public String searchAndWaitForMascot(final MascotConfig mascot_config, final Logger logger) throws IOException,SOAPException {
		assert(mascot_config != null);
		
		Search mascotee = unmarshalXML();
		SearchType   st = inferSearchTypeFromSearch(mascotee);
		switch (st) {
		case MSMS:
			logger.info("Submitting ms/ms ion search");
			MSMSIonSearch msms = new MSMSIonSearch(mascot_config.getSearchFormURL(st));
			if (hasDataURLs()) {
				msms.setDataURL(getInputDataURL(0));
			} else {
				throw new SOAPException("Data URL required for MS/MS Ion Search!");
			}
			msms.grokPage();
			return msms.submit(mascotee.getMsMsIonSearch());
		case PMF:
			logger.info("Submitting PMF search");
			PMFQuerySearch pmf = new PMFQuerySearch(mascot_config.getSearchFormURL(st));
			if (hasDataURLs()) {
				pmf.setDataURL(getInputDataURL(0));
			}
			pmf.grokPage();
			return pmf.submit(mascotee.getPMFSearch());
		case SEQ_QUERY:
			logger.info("Submitting Sequence Query search");
			SequenceQuerySearch sqs = new SequenceQuerySearch(mascot_config.getSearchFormURL(st));
			sqs.grokPage();
			return sqs.submit(mascotee.getSequenceQuerySearch());
		default:
			throw new SOAPException("Unknown mascot search! "+st);
		}
	}
	
	private SearchType inferSearchTypeFromSearch(final Search s) throws SOAPException {
		if (s == null) {
			throw new SOAPException("No mascot search!");
		}
		if (s.getMsMsIonSearch() != null) {
			return SearchType.MSMS;
		} else if (s.getPMFSearch() != null) {
			return SearchType.PMF;
		} else if (s.getSequenceQuerySearch() != null) {
			return SearchType.SEQ_QUERY;
		} else {
			throw new SOAPException("Unknown mascot search!");
		}
	}

	public void cleanupInputDataFiles(final Logger logger) {
		assert(logger != null);
		
		List<URL> cleanup_list = new ArrayList<URL>();
		addAllInputDataURLs(cleanup_list);
		
		int done = 0;
		int failed = 0;
		for (URL u : cleanup_list) {
			try {
				File f = new File( URLDecoder.decode(u.getFile(), "UTF-8"));
				if (f.delete()) {
					done++;
				} else {
					failed++;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				failed++;
			}
		}
		logger.info("Cleaned up "+done+" files for job "+getJobID()+", failed for "+failed+" files, total: "+cleanup_list.size());
	}

	public static MascotJob unmarshal(final Reader rdr) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(MascotJob.class);
		Unmarshaller um = jc.createUnmarshaller();
		MascotJob mj = (MascotJob) um.unmarshal(rdr);
		return mj;
	}
}
