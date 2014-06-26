package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Identification;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.KeyParameters;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFConstraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFData;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Reporting;
import au.edu.unimelb.plantcell.servers.mascot.html.PMFQuerySearch;


public class PMFQueryTests {
	private final static String search_url = "http://mascot.plantcell.unimelb.edu.au/mascot/cgi/search_form.pl?FORMVER=2&SEARCH=PMF";
	@SuppressWarnings("unused")
	private final static Logger l = Logger.getLogger("PMFQueryTests");
	
	private boolean validateSetValues(final String[] required, final Set<String> actual) {
		Set<String> test_me = new HashSet<String>(required.length);
		for (String s : required) {
			test_me.add(s);
		}
		for (String s : actual) {
			//l.info("Validating "+s);
			if (!test_me.contains(s))
				return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void pmfTests() {
		try {
			URL            u = new URL(search_url);
			PMFQuerySearch s = new PMFQuerySearch(u);
			s.grokPage();
			assertEquals(true, s.hasCorrectFormElements());
			Object values = s.getValues("charge");
			assertNotEquals(null, values);
			assertEquals(true, (values instanceof Set));
			Set<String> radio_values = (Set<String>) values;
			assertEquals(2, radio_values.size());
			assertEquals(true, validateSetValues(new String[] { "Mr", "1+" }, radio_values));
			
			values = s.getValues("mass");
			assertNotEquals(null, values);
			assertEquals(true, (values instanceof Set));
			radio_values = (Set<String>) values;
			assertEquals(2, radio_values.size());
			assertEquals(true, validateSetValues(new String[] { "Monoisotopic", "Average" }, radio_values));
			
			// submit a query... its a crap query so dont take the results seriously: need better test at some stage...
			PMFSearch q = new PMFSearch();
			
			PMFData d = new PMFData();
			d.setQuery("764.2\n1231.0\n1284\n1944.8\n2020.2\n2100.35");
			d.setSource("FORM");
			q.setPmfData(d);
			
			PMFConstraints c = new PMFConstraints();
			c.setAllowedProteinMass("");
			c.setAllowedTaxa("All entries");
			c.setAllowXMissedCleavages(0);
			c.setEnzyme("Trypsin");
			c.setMassValues("MH+");
			PeptideTolerance pt = new PeptideTolerance();
			pt.setValue("1.0");
			pt.setUnit("Da");
			c.setPeptideTolerance(pt);
			q.setConstraints(c);
			
			KeyParameters p = new KeyParameters();
			p.setDatabase("green_plants");
			p.setMassType("Monoisotopic");
			q.setParameters(p);
			
			Reporting r = new Reporting();
			r.setOverview(false);
			r.setTop("20");
			q.setReporting(r);
			
			Identification id = new Identification();
			id.setEmail("");
			id.setUsername("acassin");
			id.setTitle("");
			q.setIdentification(id);
			
			assertNotEquals(null, q.getConstraints());
			assertNotEquals(null, q.getIdentification());
			assertNotEquals(null, q.getParameters());
			assertNotEquals(null, q.getPmfData());
			assertNotEquals(null, q.getReporting());

			String dat_file = s.submit(q);
			assertNotEquals(null, dat_file);
			assertEquals(true, dat_file.matches("^F[\\d\\.]+dat$"));
			
			// TODO... check the results of the PMF search?
			Logger.getLogger("PMFQueryTests").info(dat_file);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// fail the test since the URL is real and should work
			assertEquals(true, false);
		}
	}
}
