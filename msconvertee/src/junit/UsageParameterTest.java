package junit;

import static org.junit.Assert.assertNotEquals;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import junit.framework.TestCase;

import org.junit.Test;

import au.edu.unimelb.plantcell.servers.proteowizard.endpoints.MSConvertFeature;
import au.edu.unimelb.plantcell.servers.proteowizard.msconvertee.MSConvertConfig;

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
			
			// check basic output format support
			assertEquals(true, conf.supportsFeature(MSConvertFeature.OUTPUT_MGF));
			assertEquals(true, conf.supportsFeature(MSConvertFeature.OUTPUT_MZML));
			assertEquals(true, conf.supportsFeature(MSConvertFeature.OUTPUT_MZ5));
			
			// basic filtering supported?
			assertEquals(true, conf.supportsFeature(MSConvertFeature.FILTERS_ARE_SUPPORTED));
			assertEquals(true, conf.supportsFeature(MSConvertFeature.FILTER_BY_MSLEVEL));
			assertEquals(true, conf.supportsFeature(MSConvertFeature.FILTER_BY_INTENSITY_THRESHOLD));

			// other basic features available?
			assertEquals(true, conf.supportsFeature(MSConvertFeature.MS2_DEISOTOPE));
			assertEquals(true, conf.supportsFeature(MSConvertFeature.MS2_DENOISE));
			
			MSConvertFeature[] feature_vector = new MSConvertFeature[] {
					MSConvertFeature.FILTERS_ARE_SUPPORTED, MSConvertFeature.FILTER_BY_MSLEVEL, 
					MSConvertFeature.FILTER_BY_INTENSITY_THRESHOLD
			};
			assertEquals(true, conf.supportsAllFeatures(feature_vector));
			assertEquals(true, conf.supportsAnyFeature(feature_vector));
			MSConvertFeature[] unsupported_vector = new MSConvertFeature[] { MSConvertFeature.UNSUPPORTED_FEATURE };
			assertEquals(false, conf.supportsAllFeatures(unsupported_vector));
			assertEquals(false, conf.supportsAnyFeature(unsupported_vector));
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);		// test must not throw exception: so FAIL!
		}
	}
}
