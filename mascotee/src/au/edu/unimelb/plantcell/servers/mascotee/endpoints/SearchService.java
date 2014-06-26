package au.edu.unimelb.plantcell.servers.mascotee.endpoints;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Search;


/**
 * Web service interface for Mascot searches.
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
@WebService
public interface SearchService {
	/**
	 * Validates the search parameters, but does not perform the search. Will throw if something is wrong.
	 */
	public void validateParameters(final Search mascotee_xml) throws SOAPException;
	
	/**
	 * Like simpleSearch() but this mandates that a single file is part of the search
	 * @param query_data	data file (byte stream)
	 * @param mascotee_xml  remainder of mascot search parameters
	 * @return
	 * @throws SOAPException
	 */
	public String validateAndSearch(final Search mascotee_xml) throws SOAPException;
	
	/**
	 * Get status on a current job
	 * @throws SOAPException 
	 */
	public String getStatus(String jobID) throws SOAPException;
	
	/**
	 * Remove all trace of a current job (server is free to ignore)
	 */
	public void purgeJob(String jobID) throws SOAPException;
	
	/**
	 * Get Mascot results (.dat) file
	 */
	public @XmlMimeType("application/octet-stream") DataHandler getResults(String jobID) throws SOAPException;
	
	/**
	 * Returns a REST GET-style URL to retrieve the results for the specified job, but
	 * similar in spirit to getResults(). REST seems to have no OutOfMemory errors which plague
	 * MTOM-SOAP style services, so this is a workaround for that.
	 */
	public String getResultsURL(String jobID) throws SOAPException;
	
	/**
	 * Returns the expected results length (in bytes) for the specified jobID.
	 * Used to verify that the expected bytes have been downloaded, although perhaps a signature
	 * would be better...
	 */
	public long getExpectedResultsBytes(String jobID) throws SOAPException;
	
	/**
	 * Returns the dat file name for the specified job or null if the job failed for some reason.
	 * This webmethod enables the caller to use the same filename as stored on the mascot server for record-keeping purposes.
	 */ 
	public String getResultsDatFile(String jobID) throws SOAPException;
}
