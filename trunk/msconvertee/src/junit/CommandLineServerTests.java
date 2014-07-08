package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.MSConvert;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DeisotopeFilteringType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.FilterParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MS2DenoiseType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ZeroesFilterType;

/**
 * Tests that the expected commandlines are produced from many msconvert invocations, but does
 * not actually perform the conversions via the server.
 * 
 * @author acassin
 *
 */
public class CommandLineServerTests {
	protected final static String LOCALHOST_SERVER = "http://localhost:8080/msconvertee/webservices/MSConvertImpl?wsdl";
	protected final static QName LOCALHOST_QNAME = 
			new QName("http://impl.msconvertee.servers.plantcell.unimelb.edu.au/", "MSConvertImplService");
	
	private String runJob(ProteowizardJob j) throws Exception {
		Service s =Service.create(new URL(LOCALHOST_SERVER), LOCALHOST_QNAME);
		assertNotNull(s);
		MSConvert            msc = s.getPort(MSConvert.class);
		DataHandler dh = new DataHandler(getBasicDataFile().toURI().toURL());
		String           cmdLine = msc.debugConvert(j, new DataHandler[] {dh});
		assertNotNull(cmdLine);
		return cmdLine;
	}
	
	protected File getBasicDataFile() {
		return new File("/home/acassin/test/proteomics/PM12.mgf");
	}

	@Test
	public void serverConnectivityTest() {
		try {
			ProteowizardJob job = makeBasicTest();
			assertEquals("mgf", job.getInputDataFormat());
			assertEquals("mgf", job.getOutputFormat());
			String cmdLine = runJob(job);
			assertEquals(true, cmdLine.startsWith("/usr/local/msconvert/msconvert")
					|| cmdLine.startsWith("c:\\Program Files (x86)\\ProteoWizard\\ProteoWizard 3.0.4416\\msconvert.exe"));
			assertEquals(true, cmdLine.endsWith("--mgf ../PM12.mgf"));
		} catch (Exception e) {
			fail("Must not throw exception!");
		}
	}

	protected ProteowizardJob makeBasicTest() throws MalformedURLException {
		ProteowizardJob job = new ProteowizardJob();
		job.setOutputFormat("mgf");
		job.setInputDataFormat("mgf");
		File f = getBasicDataFile();
		job.getInputDataNames().add(f.getName());
		return job;
	}
	
	protected ProteowizardJob makeDenoiserTest() throws MalformedURLException {
		ProteowizardJob job = makeBasicTest();
		FilterParametersType fpt = new FilterParametersType();
		MS2DenoiseType denoiser = new MS2DenoiseType();
		denoiser.setMultichargeFragmentRelaxation(true);
		denoiser.setPeaksInWindow(6);
		denoiser.setWindowWidth(30.0d);
		fpt.setMs2Denoise(denoiser);
		job.setFilterParameters(fpt);
		return job;
	}
	
	private ProteowizardJob makeDeisotoperTest() throws MalformedURLException {
		ProteowizardJob j = makeBasicTest();
		FilterParametersType fpt = new FilterParametersType();
		DeisotopeFilteringType deisotoper = new DeisotopeFilteringType();
		deisotoper.setHires(false);
		deisotoper.setMzTolerance(0.25d);
		fpt.setDeisotopeFilter(deisotoper);
		j.setFilterParameters(fpt);
		return j;
	}
	
	@Test
	public void filterTests() {
		try {
			ProteowizardJob job = makeDenoiserTest();
			assertNotNull(job);
			FilterParametersType fpt = job.getFilterParameters();
			assertNotNull(fpt);
			assertEquals(new Integer(6), fpt.getMs2Denoise().getPeaksInWindow());
			assertEquals(new Double(30.0d), fpt.getMs2Denoise().getWindowWidth());
			assertEquals(true, fpt.getMs2Denoise().isMultichargeFragmentRelaxation());
			String cmdLine = runJob(job);
			assertNotNull(cmdLine);
			String denoiser_expected = "--filter \"MS2Denoise 6 30.0 true\"";
			assertEquals(true, cmdLine.endsWith(denoiser_expected));
				
			// perform a basic deisotope
			job = makeDeisotoperTest();
			assertNotNull(job);
			fpt = job.getFilterParameters();
			assertNotNull(fpt);
			DeisotopeFilteringType dft = fpt.getDeisotopeFilter();
			assertEquals(false, dft.isHires());
			assertEquals(new Double(0.25d), dft.getMzTolerance());
			job.setFilterParameters(fpt);
			cmdLine = runJob(job);
			String deisotoper_expected = "--filter \"MS2Deisotope false 0.25\"";
			//System.err.println(cmdLine);
			assertEquals(true, cmdLine.endsWith(deisotoper_expected));
			
			job = makeBasicTest();
			FilterParametersType zero_samples_filter = new FilterParametersType();
			ZeroesFilterType zft = new ZeroesFilterType();
			zft.getApplyToMsLevel().add(new Integer(2));		// MS2 only
			zft.setMode("removeExtra");
			zero_samples_filter.setZeroesFilter(zft);
			job.setFilterParameters(zero_samples_filter);
			cmdLine = runJob(job);
			//System.err.println(cmdLine);
			assertEquals(true, cmdLine.endsWith("--filter \"zeroSamples removeExtra 2\""));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Must not throw!");
		}
	}
}
