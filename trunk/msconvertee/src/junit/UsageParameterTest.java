package junit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.Context;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertConfig;
import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertFeature;
import static org.junit.Assert.*;

/**
 * Tests that parsing of the msconvert command line options is correct
 * 
 * @author acassin
 *
 */
public class UsageParameterTest extends TestCase {
	private Context container;
	
	@Test
	public void testUsageParser() {
		try {
			container = EJBContainer.createEJBContainer().getContext();
			assertNotEquals(null, container);
			MSConvertConfig conf = (MSConvertConfig) container.lookup("java:global/msconvertee/MSConvertConfig");
			assertNotEquals(null,conf);
			assertEquals(true, conf.supportsFeature(MSConvertFeature.OUTPUT_MGF));
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);		// test must not throw exception: so FAIL!
		}
	}
}
