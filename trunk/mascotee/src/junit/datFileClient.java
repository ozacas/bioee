package junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.mascotee.endpoints.DatFileService;


@SuppressWarnings("unused")
public class datFileClient {
	private final String SOAP_URL = "http://mascot.plantcell.unimelb.edu.au:8080/mascot/DatFileService?wsdl";
	private final QName SOAP_NAMESPACE = 
			new QName("http://www.plantcell.unimelb.edu.au/bioinformatics/wsdl", "DatFileService");

    @Test
    public void test() throws Exception {
    	/*Authenticator.setDefault(new Authenticator() {
    		@Override
    		protected PasswordAuthentication getPasswordAuthentication() {
    			return new PasswordAuthentication("testuser", "TestMascotWS1".toCharArray());
    		}
    	});*/
        Service srv = Service.create(new URL(SOAP_URL), SOAP_NAMESPACE);
        assertNotNull(srv);
        
        DatFileService datFileService = srv.getPort(DatFileService.class);
        BindingProvider bp = (BindingProvider) datFileService;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
        assertEquals(true, binding.isMTOMEnabled());
        
        String[] results = datFileService.getDatFilesSince("20140406");
        assertNotNull(results);
       
        int expected_on_20140406 = 4;
        int cnt_from_20140406 = 0;
        HashSet<String> fullnames_from_20140406 = new HashSet<String>();
        for (String s : results) {
        	if (s.startsWith("20140406")) {
        		cnt_from_20140406++;
        		fullnames_from_20140406.add(s);
        	}
        }
        assertEquals(expected_on_20140406, cnt_from_20140406);
        
        // validate that getDatedDatFilePath() works as expected using data from fullnames_from_20140406
        assertEquals(null, datFileService.getDatedDatFilePath("F9999999.dat"));		// bogus dat file so null should be returned
        assertEquals("20140406/F003505.dat", datFileService.getDatedDatFilePath("F003505.dat"));
        assertEquals("20140406/F003504.dat", datFileService.getDatedDatFilePath("F003504.dat"));
        
    }
}
