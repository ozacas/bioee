package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Constraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Data;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Identification;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.KeyParameters;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MSMSTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MsMsIonSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Quantitation;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Reporting;
import au.edu.unimelb.plantcell.servers.mascot.html.MSMSIonSearch;

public class IonSearchTests {
	private final static String search_url = "http://mascot.plantcell.unimelb.edu.au/mascot/cgi/search_form.pl?FORMVER=2&SEARCH=MIS";
	
	@Test
	public void constructorTests() {
		try {
			URL u = new URL(search_url);
			MSMSIonSearch s1 = new MSMSIonSearch(u);
			assertEquals(s1.getName(),  MSMSIonSearch.DEFAULT_NAME);
			assertEquals(s1.getEmail(), MSMSIonSearch.DEFAULT_EMAIL);
			assertEquals(s1.getTitle(), MSMSIonSearch.DEFAULT_TITLE);
			assertEquals(s1.getFormPage(), u);
			
		} catch (MalformedURLException e) {
			fail("url is valid - fail!");
		}
	}
	
	@Test
	public void grokTests() {
		try {
			URL           u = new URL(search_url);
			MSMSIonSearch s = new MSMSIonSearch(u);
			try {
				s.grokPage();		// updates internal state to reflect mascot search form
				assertEquals(true, s.hasCorrectFormElements());
			} catch (IOException ioe) {
				fail("must not throw an exception - fail!");
			}
			
		} catch (MalformedURLException e) {
			fail("url is valid - fail!");
		}
	}
	
	@Test
	public void queueTests() {
		MsMsIonSearch msms_search = new MsMsIonSearch();
		// set Parameters as per Yin Ying's search by hand
		Identification id = new Identification();
		id.setUsername("Yin Ying Ho");
		id.setEmail("");
		id.setTitle("test");
		msms_search.setIdentification(id);
		KeyParameters p = new KeyParameters();
		p.setDatabase("lolium_may2013");
		p.getFixedMod().add("Carbamidomethyl (C)");
		p.getVariableMod().add("Oxidation (M)");
		p.setMassType("Monoisotopic");
		msms_search.setParameters(p);
		
		Constraints c = new Constraints();
		c.setAllowedTaxa("All entries");
		c.setEnzyme("Trypsin");
		c.setAllowXMissedCleavages(1);
		c.setAllowedProteinMass("");  // all protein masses allowed
		PeptideTolerance pt = new PeptideTolerance();
		pt.setValue("20");
		pt.setUnit("ppm");
		MSMSTolerance mt = new MSMSTolerance();
		mt.setValue("0.8");
		mt.setValue("Da");
		c.setPeptideCharge("1+, 2+ and 3+");
		c.setPeptideTolerance(pt);
		c.setMsmsTolerance(mt);
		msms_search.setConstraints(c);
		
		Reporting r = new Reporting();
		r.setOverview(false);
		r.setTop("AUTO");
		msms_search.setReporting(r);
		
		Quantitation q = new Quantitation();
		q.setIcat(false);
		msms_search.setQuant(q);
		
		Data d = new Data();
		d.setFormat("Mascot generic");
		d.setInstrument("Default");
		d.setPrecursor("");
		msms_search.setData(d);
	}
}
