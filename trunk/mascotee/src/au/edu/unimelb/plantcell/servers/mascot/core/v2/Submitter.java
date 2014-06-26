package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

/**
 * Responsible for submitting a job to a particular mascot search form. Uses jsoup to pass all
 * the parameters and monitor the submission for success. Once submission is complete, the code will
 * return success to the caller.
 * 
 * @author pcbrc.admin
 *
 */
@Deprecated
public interface Submitter {

	/**
	 * Runs the job using CGI POST to upload the data as specified to the constructor of the Submitter instance.
	 * This method will return once the job is successfully submitted, but usually before the mascot run has completed
	 * 
	 * @return the dat file ID for the results (iff job is successful)
	 * @param l the logger to log messages to (may be null)
	 */
	public String submit(final Logger l) throws MalformedURLException, IOException, SOAPException;
	
	
}
