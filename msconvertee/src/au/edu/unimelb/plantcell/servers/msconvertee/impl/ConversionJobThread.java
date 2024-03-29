package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import au.edu.unimelb.plantcell.servers.core.SendMessage;
import au.edu.unimelb.plantcell.servers.core.jaxb.ObjectFactory;
import au.edu.unimelb.plantcell.servers.core.jaxb.ResultsType;

/**
 * Perform the conversion using ProteoWizard's msconvert utility using the input data
 * provided by the caller. 
 * 
 * @author andrew.cassin
 *
 * @param <T>
 */
public class ConversionJobThread implements Runnable {
	private Logger                   logger;
	private final MSConvertConfig    msconvert_config;
	private final Queue              doneQueue;	// when conversion done (even if failed)
	private final MSConvertJob       job;
	private final ConnectionFactory  connectionFactory;
	
	/**
	 * Contains all state given to perform an msconvert conversion using the XML and input data files
	 * as specified by <code>job_message_details</code>. Error messages are logged to the specified logger.
	 * Once completed, the job message with results is placed into <code>doneQ</code> using the specified <code>connectionFactory</code>
	 * 
	 * @param logger		must not be null
	 * @param job			must not be null
	 * @param config
	 * @param doneQ
	 * @param connectionFactory
	 * @throws IOException
	 */
	public ConversionJobThread(final Logger logger, final MSConvertJob job, 
			final MSConvertConfig config, final Queue doneQ, final ConnectionFactory connectionFactory) throws IOException {
		this.msconvert_config = config;
		this.doneQueue        = doneQ;
		this.job              = job;
		this.connectionFactory= connectionFactory;
		this.logger           = logger;
		if (job.getInputData().getUrl().size() < 1) {
			throw new IOException("No input data files to convert!");
		}
	}

	public void run() {
    	try {
    		// msconvert has control over output files (and indeed whether multiple files are
    		// produced) so we must cope with that.
        	File out_folder = job.getOutputFolder();
        	CommandLine cmdLine = new MSConvertCommandLineBuilder(msconvert_config).
        								fromJob(job).
        								setOutputFolder(out_folder).build();
        	
	    	logger.info("Storing msconvert results in: "+out_folder.getAbsolutePath());
	    	DefaultExecutor exe = new DefaultExecutor();
	    	exe.setExitValues(new int[] {0});
	    	exe.setWorkingDirectory(out_folder);		// MUST be this for msconvert to work...
	    	exe.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
	    	int exitCode = -1;
			
	    	logger.info(cmdLine.toString());
			exitCode = exe.execute(cmdLine);
			
			if (exe.isFailure(exitCode)) {
				throw new IOException("msconvert may have failed due to exit code: "+exitCode);
	    	} 
		
			File[] out_files = out_folder.listFiles();
			if (out_files.length < 1) {
				throw new IOException("No output files created by msconvert!");
			}
			
			// add job into completed queue
			addResultsAndSend(job, out_files);
    	} catch (Exception e) {
    		try {
    			e.printStackTrace();
    			addResultsAndSend(job, null);		// mark job as failed
    		} catch (Exception e2) {
    			e2.printStackTrace();
    		}
    	}
	}
	
	/**
	 * Add the specified job into the completed queue
	 * @param job
	 * @param out_files
	 * @throws IOException
	 */
	private void addResultsAndSend(final MSConvertJob job, final File[] out_files) throws IOException {
		SendMessage   sm = new SendMessage(logger, connectionFactory, doneQueue, 48 * 60 * 60);		// keep in completed queue for a maximum of 2 days
		ObjectFactory of = new ObjectFactory();
		ResultsType   rt = of.createResultsType();
		rt.setOutputData(of.createOutputDataType());
		if (out_files != null) {
			rt.setStatus("OK");
			for (File f : out_files) {
				rt.getOutputData().getUrl().add(f.toURI().toURL().toExternalForm());
			}
		} else {
			rt.setStatus("FAILED");
			rt.getOutputData().getUrl().clear();
		}
		job.setResults(rt);
		sm.send(job);
	}
}
