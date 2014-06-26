package junit;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Constraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Identification;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.KeyParameters;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MSMSTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Quantitation;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Reporting;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.SeqQuery;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.SeqQuerySearch;
import au.edu.unimelb.plantcell.servers.mascot.html.SequenceQuerySearch;

public class SequenceQueryTests {
	private final static String search_url = "http://mascot.plantcell.unimelb.edu.au/mascot/cgi/search_form.pl?FORMVER=2&SEARCH=SQ";
	
	@Test
	public void sqTests() {
		try {
			URL                 u = new URL(search_url);
			SequenceQuerySearch s = new SequenceQuerySearch(u);
			s.grokPage();
			assertEquals(true, s.hasCorrectFormElements());
			
			SeqQuerySearch q = new SeqQuerySearch();
			Constraints c = new Constraints();
			c.setAllowedProteinMass("");
			c.setAllowedTaxa("All entries");
			c.setAllowXMissedCleavages(0);
			c.setEnzyme("Trypsin");
			MSMSTolerance mst = new MSMSTolerance();
			mst.setValue("0.8");
			mst.setUnit("Da");
			c.setMsmsTolerance(mst);
			c.setPeptideCharge("Mr");
			PeptideTolerance pt = new PeptideTolerance();
			pt.setValue("2.0");
			pt.setUnit("Da");
			c.setPeptideTolerance(pt);
			q.setConstraints(c);
			
			Identification id = new Identification();
			id.setEmail("");
			id.setTitle("");
			id.setUsername("acassin");
			q.setIdentification(id);
			
			Quantitation quant = new Quantitation();
			quant.setIcat(false);
			q.setQuantitation(quant);
			
			Reporting r = new Reporting();
			r.setOverview(false);
			r.setTop("20");
			q.setReporting(r);
			
			SeqQuery sq = new SeqQuery();
			sq.setInstrument("Default");
			sq.setQuery("2321 seq(n-ACTL) comp(2[C])");
			q.setQuery(sq);
			
			KeyParameters kp = new KeyParameters();
			kp.setDatabase("green_plants");
			kp.setMassType("Monoisotopic");
			q.setParameters(kp);
			
			String datname = s.submit(q);
			assertNotEquals(null, datname);
			
		} catch (Exception e) {
			// fail the test since the URL is real and should work
			assertEquals(true, false);
		}
	}
}
