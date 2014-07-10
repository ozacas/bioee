package junit;

import static org.junit.Assert.*;

import javax.activation.DataHandler;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.core.jaxb.results.DataFileType;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.ListOfDataFile;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;

/**
 * Tests data format conversions using the basic data files accessible from the superclass.
 * 
 * @author acassin
 *
 */
public class formatConversionTest extends testServerCommandLines {

	@Test
	public void mgf2mzml() {
		try {
			ProteowizardJob j = makeBasicTest();
			assertNotNull(j);
			j.setOutputFormat("mzml");
			MSConvert msc = makeServiceProxy(LOCALHOST_SERVER, LOCALHOST_QNAME);
			DataHandler dh = new DataHandler(getBasicDataFile().toURI().toURL());
			String jobID = msc.convert(j, new DataHandler[] {dh});
			if (!waitForCompletion(msc, jobID, "FINISHED")) {
				fail("Conversion did not succeed!");
			}
			ListOfDataFile l = msc.getResults(jobID);
			assertNotNull(l);
			assertNotNull(l.getDataFile());
			assertTrue(l.getDataFile().size() >= 1);
			DataFileType dft = l.getDataFile().get(0);
			assertNotNull(dft.getSuggestedName());
			assertNotNull(dft.getSuggestedName().endsWith("mzml"));
			assertTrue(dft.getRequiredLength() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw!");
		}
	}
	
	@Test
	public void mgf2mzxml() {
		try {
			ProteowizardJob j = makeBasicTest();
			assertNotNull(j);
			j.setOutputFormat("mzxml");
			MSConvert msc = makeServiceProxy(LOCALHOST_SERVER, LOCALHOST_QNAME);
			DataHandler dh = new DataHandler(getBasicDataFile().toURI().toURL());
			String jobID = msc.convert(j, new DataHandler[] {dh});
			if (!waitForCompletion(msc, jobID, "FINISHED")) {
				fail("Conversion did not succeed!");
			}
			ListOfDataFile l = msc.getResults(jobID);
			assertNotNull(l);
			assertNotNull(l.getDataFile());
			assertTrue(l.getDataFile().size() >= 1);
			DataFileType dft = l.getDataFile().get(0);
			assertNotNull(dft.getSuggestedName());
			assertNotNull(dft.getSuggestedName().endsWith("mzxml") || dft.getSuggestedName().endsWith(".xml"));
			assertTrue(dft.getRequiredLength() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw!");
		}
	}
	
	@Test
	public void mgf2mz5() {
		try {
			ProteowizardJob j = makeBasicTest();
			assertNotNull(j);
			j.setOutputFormat("mz5");
			MSConvert msc = makeServiceProxy(LOCALHOST_SERVER, LOCALHOST_QNAME);
			DataHandler dh = new DataHandler(getBasicDataFile().toURI().toURL());
			String jobID = msc.convert(j, new DataHandler[] {dh});
			if (!waitForCompletion(msc, jobID, "FINISHED")) {
				fail("Conversion did not succeed!");
			}
			ListOfDataFile l = msc.getResults(jobID);
			assertNotNull(l);
			assertNotNull(l.getDataFile());
			assertTrue(l.getDataFile().size() >= 1);
			DataFileType dft = l.getDataFile().get(0);
			assertNotNull(dft.getSuggestedName());
			assertNotNull(dft.getSuggestedName().endsWith("mz5"));
			assertTrue(dft.getRequiredLength() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw!");
		}
	}
}
