package junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

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

/**
 * Responsible for testing the MS/MS Ion Search integration for mascot v2.0 compatible servers
 * 
 * @author acassin
 *
 */
public class msMsSearchTests {
	private final static String search_url = "http://mascot.plantcell.unimelb.edu.au/mascot/cgi/search_form.pl?FORMVER=2&SEARCH=MIS";
	private final static File   data_file  = new File("c:/work/test files/mascotee/msms_search/140501_botany_yy_f6.mgf");
	
	@Test
	public void submitTests() {
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
		
		// check parameters have been set as per manual inspection of data file
		try {
			MSMSIonSearch test_search = new MSMSIonSearch(new URL(search_url));
			test_search.grokPage();
			assertEquals(true, test_search.hasCorrectFormElements());
			// once grokPage() has successfully completed, the action URL is defined so...
			assertNotEquals(null, test_search.getActionURL());
			
			// add data file to submitted data
			URL data_url = data_file.toURI().toURL();
			test_search.setDataURL(data_url);
			assertEquals(data_url, test_search.getDataURL());
			
			// submit the search
			String  dat = test_search.submit(msms_search);
			Pattern  p2 = Pattern.compile("^F\\d+\\.dat$");
			Matcher  m  = p2.matcher(dat);
			assertEquals(true, m.matches());
			
			
		} catch (IOException|SOAPException e) {
			e.printStackTrace();
			assertEquals(false, true);	// guaranteed to fail since exception should not be thrown by above code
		}
	}
}
