package au.edu.unimelb.plantcell.servers.mascot.html;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.soap.SOAPException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MSMSTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MsMsIonSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.SearchType;

/**
 * Convenience class wrapping all the HTML crap and CGI invocation...
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public class MSMSIonSearch extends AbstractMascotSearch {
	
	// public constants
	public final static String DEFAULT_NAME = "Andrew Cassin";
	public final static String DEFAULT_EMAIL= "acassin@unimelb.edu.au";
	public final static String DEFAULT_TITLE= "mascotee test";
	
	// internal state for parsing the specified URL 
	private URL    u;
	private URL    action_url;
	private String name;
	private String email;
	private String title;
	private URL    data_file;
	private final  Map<String,Object> form_input_map     = new HashMap<String,Object>();	// form input element name -> value
	private final  Set<String>        form_hidden_values = new HashSet<String>();
	
	public MSMSIonSearch(final URL form_page) {
		this(form_page, DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_TITLE);
	}
	
	public MSMSIonSearch(final URL form_page, final String name, final String email, final String title) {
		setFormPage(form_page);
		setName(name);
		setEmail(email);
		setTitle(title);
		try {
			setDataURL(null);		// no peaklist file as yet...
			setActionURL(null);		// no action url for a form which has not yet been parsed
		} catch (Exception e) {
			e.printStackTrace();
			// nothing to do...
		}
	}
	
	/**
	 * A map to go from form variable name to the set of values for it. If an object in the map is an instance of {@link java.util.Set}
	 * then the values refer to a radio button field: the value must be one of them.
	 * 
	 * @return guaranteed non-null
	 */
	@Override
	protected Map<String,Object> getFormVariables() {
		return form_input_map;
	}
	

	public Collection<String> getVariables() {
		return form_input_map.keySet();
	}
	
	public Object getValues(final String form_variable) {
		if (form_variable == null || !form_input_map.containsKey(form_variable)) {
			return null;
		}
		return form_input_map.get(form_variable);
	}
	
	public void setFormPage(final URL new_url) {
		assert(new_url != null);
		this.u = new_url;
	}

	public URL getFormPage() {
		return u;
	}
	
	public void setName(String new_name) {
		this.name = new_name;
	}
	
	public void setEmail(String new_email) {
		this.email = new_email;
	}
	
	public void setTitle(String new_title) {
		this.title = new_title;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getTitle() {
		return title;
	}
	
	/**
	 * Implementations are free to return null if they do not support this for their search (eg. PMF)
	 * 
	 * @return a URL capable of reading all the bytes of query peak lists or null if not available
	 */
	@Override
	public URL getDataURL() {
		return data_file;
	}
	
	/**
	 * Implementations are free to throw if they do not support this data for their search
	 */
	public void setDataURL(final URL u) {
		data_file = u;
	}
	
	@Override
	public void grokPage() throws IOException {
		String url = u.toString();
		logger.info("Connecting to: "+url);
		Document doc   = Jsoup.connect(url).get();
		Elements forms = doc.select("form");
		for (Element form : forms) {
			form_input_map.clear();
			// hidden values are also only computed for the form that is accepted
			form_hidden_values.clear();
			
			makeFormInputMap((FormElement) form);
			// we've found the right form on the html page when we get the right elements in the map...
			if (hasCorrectFormElements()) {
				FormElement fe = (FormElement) form;
				String action_url = fe.absUrl("action");
				logger.info("Action url is: "+action_url);
				setActionURL(new URL(action_url));
				break;
			}
		}
	}

	/**
	 * The absolute URL
	 * @param absUrl
	 */
	private void setActionURL(URL absUrl) {
		action_url = absUrl;
	}

	/**
	 * Responsible for construction of the map between form variables and allowed/default values. Tricky code.
	 * 
	 * @param form
	 * @throws IOException
	 */
	private void makeFormInputMap(final FormElement form) throws IOException {
		if (form == null)
			return;
		
		for (Element e : form.elements()) {
			if (e.hasAttr("NAME")) {
				String name = e.attr("NAME").toLowerCase();
				boolean is_radio = (e.hasAttr("TYPE") && e.attr("TYPE").equalsIgnoreCase("RADIO"));
				if (form_input_map.containsKey(name)) {
					if (is_radio) {
						@SuppressWarnings("unchecked")
						Set<String> set = (Set<String>) form_input_map.get(name);
						set.add(e.val());
					} else
						throw new IOException("Illegal form variable: "+name+" - multiple elements not allowed!");
				} else {
					if (is_radio) {
						Set<String> values = new HashSet<String>();
						values.add(e.val());
						form_input_map.put(name, values);
					} else {
						form_input_map.put(name, e.val());
					}
				}
				assert(form_input_map.containsKey(name));
				if ("hidden".equalsIgnoreCase(e.attr("TYPE"))) {
					form_hidden_values.add(name);
				}
			}
		}
	}
	
	@Override
	public boolean hasCorrectFormElements() {
		HashSet<String> required = new HashSet<String>();
		required.add("search");
		required.add("iastol");
		required.add("ia2tol");
		required.add("ibtol");
		required.add("ibstol");
		required.add("ib2tol");
		required.add("iytol");
		required.add("iy2tol");
		required.add("peak");
		required.add("ltol");
		required.add("errortolerant");
		required.add("username");
		required.add("useremail");
		required.add("com");
		required.add("db");
		required.add("taxonomy");
		required.add("cle");
		required.add("icat");
		required.add("pfa");
		required.add("mods");
		required.add("it_mods");
		required.add("seg");
		required.add("tol");
		required.add("tolu");
		required.add("itolu");
		
		required.add("charge");
		required.add("mass");
		required.add("file");
		required.add("format");
		required.add("precursor");
		required.add("instrument");
		required.add("overview");
		required.add("report");
		
		boolean ret = true;
		for (String k : required) {
			if (!form_input_map.containsKey(k)) {
				//Logger.getLogger("MSMSIonSearch").warning("failed to find form data: "+k);
				ret = false;
			}
		}
		return ret;
	}
	
	@Override
	public String submit(final Object search) throws IOException,MalformedURLException,SOAPException {
		URL form_action_url = getActionURL();
		if (search == null || form_action_url == null || !(search instanceof MsMsIonSearch))
			throw new SOAPException("Invalid input/form parameters for url: "+form_action_url.toString());
		if (!hasCorrectFormElements()) 
			throw new SOAPException("Wrong form elements for url: "+form_action_url.toString());
		MsMsIonSearch ms_ms = (MsMsIonSearch) search;
		
		// cant use jsoup for this as it does not support data file uploads at the moment
		MultipartUtility form = new MultipartUtility(form_action_url.toExternalForm(), "UTF-8");
		
		addHiddenValues(form);
		form.addFormField("OVERVIEW",   finaliseOverview(ms_ms.getReporting().isOverview()));
		form.addFormField("REPORT",     ms_ms.getReporting().getTop());
		form.addFormField("INSTRUMENT", ms_ms.getData().getInstrument());
		form.addFormField("PRECURSOR",  ms_ms.getData().getPrecursor());
		form.addFormField("FORMAT",     finaliseDataFormat(ms_ms.getData().getFormat()));
		form.addFormField("MASS",       finaliseMassType(ms_ms.getParameters().getMassType()));
		form.addFormField("CHARGE",     finalisePeptideCharge(ms_ms.getConstraints().getPeptideCharge()));
		form.addFormField("USERNAME",   finaliseUsername(ms_ms.getIdentification().getUsername()));
		form.addFormField("COM",        finaliseTitle(ms_ms.getIdentification().getTitle()));
		form.addFormField("USEREMAIL",  finaliseEmail(ms_ms.getIdentification().getEmail()));
		form.addFormField("CLE",        ms_ms.getConstraints().getEnzyme());
		form.addFormField("PFA",        String.valueOf(ms_ms.getConstraints().getAllowXMissedCleavages()));
		form.addFormField("ICAT",       ms_ms.getQuant().isIcat() ? "1" : "0");
		form.addFormField("SEG",        finaliseProteinMass(ms_ms.getConstraints().getAllowedProteinMass()));
		form.addFormField("DB",         ms_ms.getParameters().getDatabase());
		form.addFormField("TAXONOMY",   finaliseTaxonomy(ms_ms.getConstraints().getAllowedTaxa()));
		form.addFormFieldList("MODS",       ms_ms.getParameters().getFixedMod());
		form.addFormFieldList("IT_MODS",    ms_ms.getParameters().getVariableMod());
		finalisePeptideTolerance(form,  ms_ms.getConstraints().getPeptideTolerance());
		finaliseMsMsTolerance(form,     ms_ms.getConstraints().getMsmsTolerance());
		
		if (getDataURL() != null) {
			form.addDataURL("FILE", getDataURL());
		}
		
		form.validateFormParameterCount(SearchType.MSMS);
		
		// wait for mascot to begin searching...
		try {
			Thread.sleep(2 * 1000);
			String dat = form.finish(logger);
			logger.info("Got results file: "+dat);
			return dat;
		} catch (FailedJobException|InterruptedException e) {
			e.printStackTrace();
			throw new SOAPException(e);
		}
		
	}

	protected String finaliseOverview(boolean overview) {
		return overview ? "1" : "0";
	}

	/**
	 * Returns the absolute URL of the action URL for the mascot form submission. Only valid once grok_page() has
	 * been called.
	 * 
	 * @return
	 */
	@Override
	public URL getActionURL() {
		return action_url;
	}

	protected void finaliseMsMsTolerance(final MultipartUtility form, final MSMSTolerance msmsTolerance) {
		assert(form != null && msmsTolerance != null);
		String value = msmsTolerance.getValue();
		String unit  = msmsTolerance.getUnit();
		if (value == null || unit == null || value.length() < 1 || unit.length() < 1) {
			// assume default of 1.0 Da
			value = "1.0";
			unit  = "Da";
			// FALLTHRU...
		}
		form.addFormField("ITOL",  value);
		form.addFormField("ITOLU", unit);
	}

	protected void finalisePeptideTolerance(final MultipartUtility form, final PeptideTolerance peptideTolerance) {
		assert(form != null && peptideTolerance != null);
		String value = peptideTolerance.getValue();
		String unit  = peptideTolerance.getUnit();
		if (value == null || unit == null || value.length() < 1 || unit.length() < 1) {
			// assume default of 1.0 Da
			value = "1.0";
			unit  = "Da";
			// FALLTHRU...
		}
		form.addFormField("TOL",  value);
		form.addFormField("TOLU", unit);
	}

	protected String finaliseTaxonomy(String allowedTaxa) {
		if (allowedTaxa == null || allowedTaxa.length() < 1)
			return "All entries";
		return allowedTaxa;
	}

	protected String finaliseProteinMass(final String allowedProteinMass) {
		if (allowedProteinMass == null)
			return "";
		return allowedProteinMass;
	}

	protected String finaliseEmail(final String email) {
		if (email == null)
			return "";
		return email;
	}

	protected String finaliseUsername(final String username) {
		if (username == null)
			return "";
		return username;
	}

	/**
	 * Title always has a UUID attached so we can verify the submitted job against the UUID in the results file
	 * @param title
	 * @return
	 */
	protected String finaliseTitle(final String title) {
		String uuid = UUID.randomUUID().toString();
		if (title == null) {
			return "uuid="+uuid;
		}
		if (title.indexOf("uuid=") >= 0) {
			return title;
		}
		return title + " uuid="+uuid;
	}

	protected String finalisePeptideCharge(String peptideCharge) {
		return peptideCharge;	// no conversion/defaulting required?
	}

	protected String finaliseMassType(String massType) {
		if (massType != null && massType.equalsIgnoreCase("Average"))
			return "Average";
		else 
			return "Monoisotopic";
	}

	protected String finaliseDataFormat(String format) {
		return format; // no conversion/defaulting required?
	}

	@Override
	protected Collection<String> getFormHiddenValues() {
		return form_hidden_values;
	}
}
