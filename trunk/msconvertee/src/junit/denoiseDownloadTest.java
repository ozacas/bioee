package junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.activation.DataHandler;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.core.jaxb.results.DataFileType;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.ListOfDataFile;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;

/**
 * Tests downloading and analysis of results
 * 
 * @author acassin
 *
 */
public class denoiseDownloadTest extends testServerCommandLines {
	public final static String PRODUCTION_SERVER = "http://localhost:7070/msconvertee/webservices/MSConvertImpl?wsdl";
	
	@Test
	public void getResultsTest() {
		try {
			ProteowizardJob j = makeDenoiserTest();
			MSConvert msc = makeServiceProxy(LOCALHOST_SERVER, LOCALHOST_QNAME);
			File f = getBasicDataFile();
			DataHandler    dh = new DataHandler(f.toURI().toURL());
			String      jobID = msc.convert(j, new DataHandler[] {dh});
			assertNotNull(jobID);
			assertTrue(waitForCompletion(msc, jobID, "FINISHED"));
			
			ListOfDataFile l = msc.getResults(jobID);
			assertNotNull(l);
			assertTrue(l.getDataFile().size() > 0);		// at least the converted mgf should be available
			for (DataFileType df : l.getDataFile()) {
				assertNotNull(df.getRequiredLength());
				assertTrue(df.getRequiredLength() > 0);
				assertNotNull(df.getSuggestedName());
				// noise removal should make the results shorter than the input, so check that...
				assertTrue(df.getRequiredLength() < f.length());
			}
		} catch (Exception e) {
			fail("Must not throw!");
		}
	}
}
