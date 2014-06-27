package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;

public class MSConvertConstants {
	/**
	 * This property on each JMS message, is required. It specified the job ID that is used from
	 * the client side to interact with the web service. It is unique until the job is purged from the system.
	 */
	public final static String MSCONVERT_MESSAGE_ID_PROPERTY = "msconvert-job-id";
	
	/**
	 * Temporary folder where conversion files are stored. Only used if MSCONVERT_TEMP_FOLDER resource is not specified.
	 */
	public final static File MSCONVERT_TEMP_FOLDER = new File("c:/temp/msconvert-tmp-files");
	
	/**
	 * Only used if no test file is configured. Otherwise not used
	 */
	public final static String MSCONVERT_USAGE_TEST_FILE="/home/acassin/src/bioee/msconvertee/test-data/msconvert.usage";
}
