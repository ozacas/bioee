package junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;

import javax.activation.DataHandler;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.core.jaxb.results.DataFileType;
import au.edu.unimelb.plantcell.servers.core.jaxb.results.ListOfDataFile;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.FilterParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MsLevelType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ObjectFactory;

public class MSLevelsTest extends testServerCommandLines {

	@Test
	public void testKeepAllMSLevels() {
		ProteowizardJob j;
		try {
			j = this.makeBasicTest();
			// since the basic test uses an MGF file, we know this must be level >= 2 so...
			ObjectFactory of = new ObjectFactory();
			FilterParametersType fpt = of.createFilterParametersType();
			MsLevelType levels = of.createMsLevelType();
			levels.getMsLevel().add(new Integer(2));
			fpt.setMsLevelFilter(levels);
			j.setFilterParameters(fpt);
			
			File expected = this.getBasicDataFile();
			MSConvert msc = makeServiceProxy(LOCALHOST_SERVER, LOCALHOST_QNAME);
			DataHandler dh = new DataHandler(expected.toURI().toURL());
			String jobID = msc.convert(j, new DataHandler[] {dh});
			if (!waitForCompletion(msc, jobID, "FINISHED")) {
				fail("Expected job to complete successfully");
			}
			ListOfDataFile results = msc.getResults(jobID);
			assertNotNull(results);
			assertNotNull(results.getDataFile());
			assertTrue(results.getDataFile().size() > 0);
			File results_file = null;
			long required_length = -1L;
			for (DataFileType df : results.getDataFile()) {
				assertNotNull(df);
				assertNotNull(df.getSuggestedName());
				if (df.getSuggestedName().endsWith(".mgf")) {
					System.err.println("expected size: "+expected.length()+" actual "+df.getRequiredLength());
					assertTrue(df.getRequiredLength() > 0);
					if (results_file == null) {
						results_file = File.createTempFile("results", "_output.mgf");
						FileOutputStream fos = new FileOutputStream(results_file);
						df.getData().writeTo(fos);
						fos.close();
						required_length = df.getRequiredLength();
					}
				}
			}
			if (results_file != null) {
				// check results conform to input...
				assertTrue(required_length > 0);
				checkAllInputSpectraPresent("mgf", expected, results_file);
				
				// delete downloaded copy
				results_file.delete();
			} else {
				fail("Could not obtain results file!");
			}
		} catch (Exception e) {
			fail("Must not throw!");
		}
	}
}
