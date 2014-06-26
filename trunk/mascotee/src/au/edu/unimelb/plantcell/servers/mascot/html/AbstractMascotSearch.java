package au.edu.unimelb.plantcell.servers.mascot.html;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

/**
 * A class to implement a mascot search must support the following operations to operate correctly in the framework.
 * See {@link MSMSIonSearch} for more details.
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
public abstract class AbstractMascotSearch {
	protected final static Logger logger = Logger.getLogger("Mascot Search");

	public AbstractMascotSearch() {
	}
	
	/**
	 * Must not return null
	 * @return
	 */
	protected abstract Map<String,Object> getFormVariables();
	/**
	 * Must not return null
	 */
	protected abstract Collection<String> getFormHiddenValues();		// names of variables which are hidden on the form

	public abstract URL getFormPage();
	
	public abstract URL getDataURL();
	
	public abstract boolean hasCorrectFormElements();
	
	public abstract void grokPage() throws IOException;
	
	public abstract URL getActionURL();
	
	/**
	 * 
	 * @param search must be an instanceof the jaxb mascotee classes eg. MsMsIonSearch or PMFQuerySearch or SeqQuerySearch
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws SOAPException
	 */
	public abstract String submit(final Object search) throws IOException,MalformedURLException,SOAPException;
	
	/**
	 * The mascot search form has hidden values which must be passed thru for correct operation.
	 * 
	 * @param form must not be null
	 * @throws SOAPException if key hidden form values are missing/illegal (which are v2 mascot specific)
	 */
	protected void addHiddenValues(final MultipartUtility form) throws SOAPException {
		assert(form != null);
		Set<String> done = new HashSet<String>();
		
		Map<String,Object> form_input_map = getFormVariables();
		for (String key : getFormHiddenValues()) {
			Object o = form_input_map.get(key);
			if (o == null)
				throw new SOAPException("Known hidden value is not in form: "+key);
			String val = o.toString();
			form.addFormField(key.toUpperCase(), val);
			
			if (val != null)
				done.add(key.toLowerCase());
		}
		
		for (String check_me : new String[] { "INTERMEDIATE", "FORMVER", "SEARCH", "IATOL", "IASTOL", 
												"IA2TOL", "IBTOL", "IBSTOL", "IB2TOL", "IYTOL", "IYSTOL", 
												"IY2TOL", "PEAK", "ERRORTOLERANT", "LTOL" }) {
			if (!done.contains(check_me.toLowerCase()))
				throw new SOAPException("Missing critical form value: "+check_me);
		}
	}

}
