package au.edu.unimelb.plantcell.servers.mascot.html;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.SeqQuerySearch;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SearchType;

/**
 * performs a mascot sequence query, but since it will be very similar to
 * the superclass we just derive from it (the HTML form is nearly identical)
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class SequenceQuerySearch extends MSMSIonSearch {

	public SequenceQuerySearch(final URL u) {
		super(u, DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_TITLE);
	}

	@Override
	public boolean hasCorrectFormElements() {
		HashSet<String>          required = new HashSet<String>();
		required.add("search");
		required.add("iatol");
		required.add("iastol");
		required.add("ia2tol");
		required.add("ibtol");
		required.add("ibstol");
		required.add("ib2tol");
		required.add("iytol");
		required.add("iy2tol");
		required.add("peak");
		required.add("reptype");
		required.add("errortolerant");
		required.add("username");
		required.add("useremail");
		required.add("com");
		required.add("db");
		required.add("taxonomy");
		required.add("cle");
		required.add("pfa");
		required.add("mods");
		required.add("it_mods");
		required.add("seg");
		required.add("icat");
		required.add("tol");
		required.add("tolu");
		required.add("itol");
		required.add("charge");
		required.add("mass");
		required.add("que");
		required.add("instrument");
		required.add("overview");
		required.add("report");

		Map<String,Object> form_input_map = getFormVariables();
		
		boolean ret = true;
		for (String k : required) {
			if (!form_input_map.containsKey(k)) {
				//Logger.getLogger("SequenceQuerySearch").warning("failed to find form data: "+k);
				ret = false;
			}
		}
		return ret;
	}
	
	@Override
	public URL getDataURL() {
		// this type of query does not support file upload
		return null;
	}
	
	@Override
	public String submit(final Object search) throws IOException,MalformedURLException,SOAPException {
		URL form_action_url = getActionURL();
		if (search == null || form_action_url == null || !(search instanceof SeqQuerySearch))
			throw new SOAPException("Invalid input/form parameters for url: "+form_action_url.toString());
		if (!hasCorrectFormElements()) 
			throw new SOAPException("Wrong form elements for url: "+form_action_url.toString());
		SeqQuerySearch q = 
				(SeqQuerySearch) search;
		
		// cant use jsoup for this as it does not support data file uploads at the moment
		MultipartUtility form = new MultipartUtility(form_action_url.toExternalForm(), "UTF-8");
		
		addHiddenValues(form);
		form.addFormField("OVERVIEW",   finaliseOverview(q.getReporting().isOverview()));
		form.addFormField("REPORT",     q.getReporting().getTop());
		form.addFormField("INSTRUMENT", q.getQuery().getInstrument());
		form.addFormField("MASS",       finaliseMassType(q.getParameters().getMassType()));
		form.addFormField("CHARGE",     finalisePeptideCharge(q.getConstraints().getPeptideCharge()));
		form.addFormField("USERNAME",   finaliseUsername(q.getIdentification().getUsername()));
		form.addFormField("COM",        finaliseTitle(q.getIdentification().getTitle()));
		form.addFormField("USEREMAIL",  finaliseEmail(q.getIdentification().getEmail()));
		form.addFormField("CLE",        q.getConstraints().getEnzyme());
		form.addFormField("PFA",        String.valueOf(q.getConstraints().getAllowXMissedCleavages()));
		form.addFormField("ICAT",       String.valueOf(q.getQuantitation().isIcat()));
		form.addFormField("SEG",        finaliseProteinMass(q.getConstraints().getAllowedProteinMass()));
		form.addFormField("DB",         q.getParameters().getDatabase());
		form.addFormField("TAXONOMY",   finaliseTaxonomy(q.getConstraints().getAllowedTaxa()));
		form.addFormFieldList("MODS",       q.getParameters().getFixedMod());
		form.addFormFieldList("IT_MODS",    q.getParameters().getVariableMod());
		form.addFormField("QUE",        q.getQuery().getQuery());
		form.addFormField("INSTRUMENT", q.getQuery().getInstrument());
		finalisePeptideTolerance(form,  q.getConstraints().getPeptideTolerance());
		finaliseMsMsTolerance(form,     q.getConstraints().getMsmsTolerance());
		
		if (getDataURL() != null) {
			form.addDataURL("FILE", getDataURL());
		}
		
		form.validateFormParameterCount(SearchType.SEQ_QUERY);
		
		// wait for mascot to begin searching...
		try {
			Thread.sleep(2 * 1000);
			String dat = form.finish(logger);
			logger.info(dat);
			return dat;
		} catch (FailedJobException|InterruptedException e) {
			e.printStackTrace();
			throw new SOAPException(e);
		}
		
	}
}
