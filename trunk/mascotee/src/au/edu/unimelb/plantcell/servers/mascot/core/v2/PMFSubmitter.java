package au.edu.unimelb.plantcell.servers.mascot.core.v2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFSearch;

@Deprecated
public class PMFSubmitter implements Submitter {

	public PMFSubmitter(final URL u, final PMFSearch pmf, final URL[] data) {
	}

	@Override
	public String submit(Logger l) throws MalformedURLException, IOException, SOAPException {
		return "";
	}

}
