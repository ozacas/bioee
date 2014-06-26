package au.edu.unimelb.plantcell.servers.mascotee;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import au.edu.unimelb.plantcell.servers.core.SendMessage;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * responsible for running a single job and sending the completion status to the specified Queue
 * when done.
 * 
 * @author acassin
 *
 */
public class MascotRunThread implements Runnable {
	private MascotJob         job;
	private Logger            logger;
	private MascotConfig      config;
	private Queue             q;
	private ConnectionFactory connectionFactory;
	
	public MascotRunThread(final Logger logger, final MascotJob job, 
			final MascotConfig config, final Queue q, final ConnectionFactory connectionFactory) {
		this.logger            = logger;
		this.job               = job;
		this.config            = config;
		this.q                 = q;
		this.connectionFactory = connectionFactory;
	}
	
	@Override
	public void run() {
		int expire = MascotEEConstants.EXPIRE_AFTER;
		try {
			logger.info("Running mascot job: \n"+job.getJobID());
			String         dat_file_results = job.searchAndWaitForMascot(config, logger);
			logger.info("Search completed. Got DAT file: "+dat_file_results);
			// add job into completed queue
			SendMessage sm = new SendMessage(logger, connectionFactory, q, expire);
			job.getResults().setStatus(dat_file_results != null ? "OK" : "FAILED");
			job.getResults().getOutputData().getUrl().add(dat_file_results);
			sm.send(job);
		} catch (Exception e) {
			e.printStackTrace();
			new SendMessage(logger, connectionFactory, q, expire).send(job);	// mark failures in completion queue too
		}
	}


   
}
