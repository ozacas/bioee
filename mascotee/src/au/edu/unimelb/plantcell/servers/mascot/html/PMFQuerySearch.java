package au.edu.unimelb.plantcell.servers.mascot.html;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFData;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFSearch;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SearchType;

/**
 * Performs a mascot v2.0 pmf search
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class PMFQuerySearch extends MSMSIonSearch {
	private final static String[] required_form_values = new String[] {
		"search", "iastol", "ia2tol", "ibtol", "ibstol", "ib2tol", "iytol", "peak", "ltol", "errortolerant",
		"file", "que", "overview", "report", "mass", "charge", "tolu", "tol", "seg", "it_mods", "mods", "pfa", "cle",
		"taxonomy", "db", "com", "useremail", "username", "reptype"
	};
	
	public PMFQuerySearch(final URL form_page) {
		super(form_page);
	}

	@Override
	public boolean hasCorrectFormElements() {
		HashSet<String> required = new HashSet<String>();
		for (String s : required_form_values) {
			required.add(s);
		}
		Map<String,Object> form_input_map = getFormVariables();
		
		boolean ret = true;
		for (String k : required) {
			if (!form_input_map.containsKey(k)) {
				Logger.getLogger("PMFQuerySearch").warning("failed to find form data: "+k);
				ret = false;
			}
		}
		return ret;
	}

	public URL getDataURL() {
		return null;		// not supported for now
	}
	
	@Override
	public String submit(final Object search) throws IOException,MalformedURLException,SOAPException {
		URL form_action_url = getActionURL();
		if (search == null || form_action_url == null || !(search instanceof PMFSearch))
			throw new SOAPException("Invalid input/form parameters for url: "+form_action_url.toString());
		if (!hasCorrectFormElements()) 
			throw new SOAPException("Wrong form elements for url: "+form_action_url.toString());
		
		
		PMFSearch pmf = (PMFSearch) search;
		
		// cant use jsoup for this as it does not support data file uploads at the moment
		MultipartUtility form = new MultipartUtility(form_action_url.toExternalForm(), "UTF-8");
		
		addHiddenValues(form);
		form.addFormField("OVERVIEW",   finaliseOverview(pmf.getReporting().isOverview()));
		form.addFormField("REPORT",     pmf.getReporting().getTop());
		form.addFormField("MASS",       finaliseMassType(pmf.getParameters().getMassType()));
		form.addFormField("USERNAME",   finaliseUsername(pmf.getIdentification().getUsername()));
		form.addFormField("COM",        finaliseTitle(pmf.getIdentification().getTitle()));
		form.addFormField("USEREMAIL",  finaliseEmail(pmf.getIdentification().getEmail()));
		form.addFormField("CLE",        pmf.getConstraints().getEnzyme());
		form.addFormField("PFA",        String.valueOf(pmf.getConstraints().getAllowXMissedCleavages()));
		form.addFormField("SEG",        finaliseProteinMass(pmf.getConstraints().getAllowedProteinMass()));
		form.addFormField("DB",         pmf.getParameters().getDatabase());
		form.addFormField("TAXONOMY",   finaliseTaxonomy(pmf.getConstraints().getAllowedTaxa()));
		form.addFormFieldList("MODS",       pmf.getParameters().getFixedMod());
		form.addFormFieldList("IT_MODS",    pmf.getParameters().getVariableMod());
		form.addFormField("QUE",        finaliseQuery(pmf.getPmfData()));
		finalisePeptideTolerance(form,  pmf.getConstraints().getPeptideTolerance());
		
		if (getDataURL() != null) {
			form.addDataURL("FILE", getDataURL());
		}
		
		form.validateFormParameterCount(SearchType.PMF);
		
		// wait for mascot to begin searching...
		try {
			Thread.sleep(2 * 1000);
			String dat = form.finish(logger);
			return dat;
		} catch (FailedJobException|InterruptedException e) {
			e.printStackTrace();
			throw new SOAPException(e);
		}
		
	}

	private String finaliseQuery(PMFData pmfData) throws SOAPException {
		if (pmfData == null || !pmfData.getSource().equals("FORM"))
			throw new SOAPException("Only form data variable supported for now!");
		
		String que = pmfData.getQuery();
		if (que == null || que.trim().length() < 1)
			throw new SOAPException("PMF data cannot be missing!");
		return que;
	}
}
