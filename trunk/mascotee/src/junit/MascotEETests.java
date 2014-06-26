package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Constraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Data;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Identification;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.KeyParameters;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MSMSTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MsMsIonSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFConstraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFData;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Quantitation;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Reporting;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Search;
import au.edu.unimelb.plantcell.servers.mascotee.endpoints.SearchService;

public class MascotEETests {
	private Logger logger = Logger.getLogger("MascotEETests");
	
	private final String SOAP_URL = "http://mascot.plantcell.unimelb.edu.au:8080/mascotee/SearchService?wsdl";
	private final QName SOAP_NAMESPACE = 
			new QName("http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl", "SearchService");
	private final String TEST_MGF = "c:/work/test files/mascotee/msms_search/140501_botany_yy_f6.mgf";
	
	@Test
	public void PMFTest() {
		Service srv = null;
		try {
			srv = Service.create(new URL(SOAP_URL), SOAP_NAMESPACE);
			assertNotNull(srv);
		} catch (MalformedURLException e) {
			fail("Valid URL should not fail!");
		}
	    
        SearchService searchService = srv.getPort(SearchService.class);
        BindingProvider bp = (BindingProvider) searchService;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
        assertEquals(true, binding.isMTOMEnabled());
        
        // make a crap query
        PMFSearch q = new PMFSearch();
		
		PMFData d = new PMFData();
		d.setQuery("764.2\n1231.0\n1284\n1944.8\n2020.2\n2100.35");
		d.setSource("FORM");
		q.setPmfData(d);
		
		PMFConstraints c = new PMFConstraints();
		c.setAllowedProteinMass("");
		c.setAllowedTaxa("All entries");
		c.setAllowXMissedCleavages(0);
		c.setEnzyme("Trypsin");
		c.setMassValues("MH+");
		PeptideTolerance pt = new PeptideTolerance();
		pt.setValue("1.0");
		pt.setUnit("Da");
		c.setPeptideTolerance(pt);
		q.setConstraints(c);
		
		KeyParameters p = new KeyParameters();
		p.setDatabase("green_plants");
		p.setMassType("Monoisotopic");
		q.setParameters(p);
		
		Reporting r = new Reporting();
		r.setOverview(false);
		r.setTop("20");
		q.setReporting(r);
		
		Identification id = new Identification();
		id.setEmail("");
		id.setUsername("acassin");
		id.setTitle("");
		q.setIdentification(id);
		
		// serialise to MascotEE XML
		try {
			// submit it...
			Search s = new Search();
			s.setPMFSearch(q);
			
			String jobID = searchService.validateAndSearch(s);
			assertNotNull(jobID);
			Logger.getLogger("MascotEETests").info("Got PMF job id: "+jobID);
		} catch (SOAPException e) {
			e.printStackTrace();
			fail("No exception should be thrown for this test!");
		}
	}

	@Test
	public void msmsTests() {
		Service srv = null;
		try {
			srv = Service.create(new URL(SOAP_URL), SOAP_NAMESPACE);
			assertNotNull(srv);
		} catch (MalformedURLException e) {
			fail("Valid URL should not fail!");
		}
		SearchService searchService = srv.getPort(SearchService.class);
	    BindingProvider bp = (BindingProvider) searchService;
	    SOAPBinding binding = (SOAPBinding) bp.getBinding();
	    binding.setMTOMEnabled(true);
	    assertEquals(true, binding.isMTOMEnabled());
	        
	    MsMsIonSearch msms_search = new MsMsIonSearch();
		// set Parameters as per Yin Ying's search by hand
		Identification id = new Identification();
		id.setUsername("yyho");
		id.setEmail("");
		id.setTitle("test");
		msms_search.setIdentification(id);
		KeyParameters p = new KeyParameters();
		p.setDatabase("lolium_may2013");
		p.getFixedMod().add("Carbamidomethyl (C)");
		p.getVariableMod().add("Oxidation (M)");
		p.setMassType("Monoisotopic");
		msms_search.setParameters(p);
		
		Constraints c = new Constraints();
		c.setAllowedTaxa("All entries");
		c.setEnzyme("Trypsin");
		c.setAllowXMissedCleavages(1);
		c.setAllowedProteinMass("");  // all protein masses allowed
		PeptideTolerance pt = new PeptideTolerance();
		pt.setValue("20");
		pt.setUnit("ppm");
		MSMSTolerance mt = new MSMSTolerance();
		mt.setValue("0.8");
		mt.setUnit("Da");
		c.setPeptideCharge("1+, 2+ and 3+");
		c.setPeptideTolerance(pt);
		c.setMsmsTolerance(mt);
		msms_search.setConstraints(c);
		
		Reporting r = new Reporting();
		r.setOverview(false);
		r.setTop("AUTO");
		msms_search.setReporting(r);
		
		Quantitation q = new Quantitation();
		q.setIcat(false);
		msms_search.setQuant(q);
		
		Data d = new Data();
		d.setFormat("Mascot generic");
		d.setInstrument("Default");
		d.setPrecursor("");
		msms_search.setData(d);
		
		// serialise to MascotEE XML
		try {
			// submit ten searches all the same
			HashSet<String> ids = new HashSet<String>();
			for (int i=0; i<10; i++) {
				Search s = new Search();
				File f = new File(TEST_MGF);
				msms_search.getData().setSuggestedFileName(f.getName());
				msms_search.getData().setFile(new DataHandler(f.toURI().toURL()));
				s.setMsMsIonSearch(msms_search);
				
				String jobID = searchService.validateAndSearch(s);
				assertNotNull(jobID);
				Logger.getLogger("MascotEETests").info("Got MSMS Ion search job id ("+(1+i)+" of 10): "+jobID);
				ids.add(jobID);
			}
			assertEquals(10, ids.size());
			
			// and wait for them...
			for (String jobID : ids) {
				String lastStatus = waitForJobCompletion(searchService, jobID);
				assertEquals("FINISHED", lastStatus);
			}
			
			// download the data and verify that each download corresponds to the right jobid
			for (String jobID : ids) {
				logger.info("Downloading results for "+jobID);
				String dat_file = searchService.getResultsDatFile(jobID);
				logger.info(dat_file+" is the record of job: "+jobID);
				DataHandler dh = searchService.getResults(jobID);
				assertNotNull(dh);
				InputStream is = dh.getInputStream();
				int cnt = 0;
				long total = 0;
				byte[] buf = new byte[128 * 1024];
				while ((cnt = is.read(buf, 0, buf.length)) >= 0) {
					total += cnt; 
				}
				logger.info("Downloaded "+total+" bytes for "+dat_file);
				
				// mascot does not return EXACTLY the same results each time, so...
				boolean ok_range = (total >= 1576000 && total <= 1577000);
				assertEquals(true, ok_range);
			}
		} catch (SOAPException e) {
			e.printStackTrace();
			fail("No exception should be thrown for this test!");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail("URL is valid for this test! "+e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail("No IOExceptions should occur!");
		}
	}

	private String waitForJobCompletion(final SearchService searchService, final String jobID) throws SOAPException {
		String lastStatus = null;
		logger.info("Checking status of job: "+jobID);
		while (true) {
			lastStatus = searchService.getStatus(jobID);
			if (lastStatus == null) {
				throw new SOAPException("Failed to get status for "+jobID);
			}
			logger.info("Got status "+lastStatus+" for "+jobID);
			
			if (lastStatus.startsWith("QUEUED") || lastStatus.startsWith("RUN") || lastStatus.startsWith("PENDING")) {
				waitFor(60);
			} else {
				return lastStatus;
			}
		}
	}

	private void waitFor(int n_seconds) {
		assert(n_seconds > 0);
		
		try {
			Thread.sleep(n_seconds * 1000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

}
