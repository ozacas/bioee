package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MsMsIonSearch;
import au.edu.unimelb.plantcell.servers.mascot.html.MSMSIonSearch;

/**
 * Responsible for submitting a job to the mascot MS/MS search engine with all the parameters
 * as specified by the <code>msMsIonSearch</code> constructor parameter and chosen peak list data.
 * 
 * @author acassin
 *
 */
@Deprecated
public class MSMSSubmitter implements Submitter {
	private URL u;
	private MsMsIonSearch search;
	private List<URL> data_files = new ArrayList<URL>();
	
	public MSMSSubmitter(final URL form_url, final MsMsIonSearch msMsIonSearch, final URL[] data) throws SOAPException {
		if (form_url == null || msMsIonSearch == null || data == null || data.length < 1) 
			throw new SOAPException("Invalid ms/ms ion search parameters!");
		u      = form_url;
		search = msMsIonSearch;
		for (URL d : data) {
			data_files.add(d);
		}
	}

	@Override
	public String submit(final Logger l) throws MalformedURLException, IOException, SOAPException {
		MSMSIonSearch msms = new MSMSIonSearch(u);
		if (data_files.size() > 0) {
			msms.setDataURL(data_files.get(0));
		}
		return msms.submit(search);
	}

}
