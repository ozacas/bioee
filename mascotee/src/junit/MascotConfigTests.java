package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.mascotee.endpoints.ConfigService;

public class MascotConfigTests {
	private final String SOAP_URL = "http://mascot.plantcell.unimelb.edu.au:8080/mascot/ConfigService?wsdl";
	private final QName SOAP_NAMESPACE = 
			new QName("http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl", "ConfigService");
	@SuppressWarnings("unused")
	private final static Logger l = Logger.getLogger("Mascot Config Tests");

	@Test 
	public void test() {
		Service srv;
		try {
			srv = Service.create(new URL(SOAP_URL), SOAP_NAMESPACE);
	        assertNotNull(srv);
	        
	        ConfigService configService = srv.getPort(ConfigService.class);
	        BindingProvider bp = (BindingProvider) configService;
	        SOAPBinding binding = (SOAPBinding) bp.getBinding();
	        binding.setMTOMEnabled(true);
	        assertEquals(true, binding.isMTOMEnabled());
	        
	        String[] databases = configService.availableDatabases();
	        assertNotNull(databases);
	        assertNotEquals("Must have mascot database!", databases.length, 0);
	        HashSet<String> dups = new HashSet<String>();
	        for (String s : databases) {
	        	assertEquals(false, dups.contains(s));
	        	dups.add(s);
	        }
	        assertEquals(36,databases.length);
	        
	        String[] params = configService.availableConfigParameters();
	        assertNotNull(params);
	        boolean found_maxseqlen = false;
	        boolean found_maxacclen = false;
	        for (String s : params) {
	        	if (s.equals("MaxSequenceLen"))
	        		found_maxseqlen = true;
	        	if (s.equals("MaxAccessionLen"))
	        		found_maxacclen = true;
	        }
	        assertEquals(true, found_maxseqlen);
	        assertEquals(true, found_maxacclen);
	        assertNotEquals(0, params.length);
	        
	        String[] enzymes = configService.availableEnzymes();
	        assertNotNull(enzymes);
	        assertNotEquals(0, enzymes.length);
	        boolean found_trypsin = false;
	        boolean found_none = false;
	        for (String e : enzymes) {
	        	String e_lower = e.toLowerCase();
	        	if (e_lower.indexOf("trypsin") >= 0) {
	        		found_trypsin = true;
	        	}
	        	if (e_lower.equals("none")) {
	        		found_none = true;
	        	}
	        }
	        assertEquals(true, found_trypsin);
	        assertEquals(true, found_none);
	        
	        // instruments
	        String[] instruments = configService.availableInstruments();
	        assertNotNull(instruments);
	        assertNotEquals(0, instruments.length);
	        boolean found_default = false;
	        boolean found_esi_trap= false;
	        String[] default_rules= null;
	        for (String i : instruments) {
	        	if (i.equals("Default")) {
	        		found_default = true;
	        		default_rules = configService.getFragmentationRulesForInstrument(i);
	        	}
	        	if (i.equals("ESI-TRAP")) {
	        		found_esi_trap = true;
	        	}
	        }
	        assertEquals(true, found_default);
	        assertEquals(true, found_esi_trap);
	        assertNotNull(default_rules);
	        assertEquals(8, default_rules.length);
	        
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}
}
