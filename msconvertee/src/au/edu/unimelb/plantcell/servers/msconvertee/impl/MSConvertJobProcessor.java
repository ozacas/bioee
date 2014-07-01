package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import au.edu.unimelb.plantcell.servers.core.SendMessage;

/**
 * Responsible for managing the job queue and processing jobs.
 * 
 * 
 * 
 * @author http://www.plantcell.unimelb.edu.au/bioinformatics
 *
 */
@MessageDriven
public class MSConvertJobProcessor implements MessageListener {
	private final static Logger logger = Logger.getLogger("MSConvert Job Processor");

	/*
	 * TomEE will load the MascotConfig instance for us
	 */
	@EJB private MSConvertConfig msconvert_config;
	
	/*
	 * We also need the completion queue once a job has been done 
	 * so that the web service can identify the results to report to a caller.
	 * Completion queue entries expire: currently 48 hours and at this time
	 * another MDB will cleanup the input data files (but not the .dat file computed by mascot)
	 */
	@Resource(name="MSConvertRunQueue")
	private Queue runQueue;	// waiting to run or currently running
	@Resource(name="MSConvertDoneQueue")
	private Queue doneQueue;
	@Resource
	private ConnectionFactory connectionFactory;
	
	/**
	 * given that each msconvert job needs quite a bit of state to run, we must be careful which ExecutorService we
	 * use so that parallel access to state is carefully managed. For now we avoid this problem by using a single thread executor
	 */
	private static final ExecutorService mes = Executors.newSingleThreadExecutor();
	
	
	public MSConvertJobProcessor() {
	}
	
	/*
	 * Note we start a separate thread since we must return from this function quickly to acknowledge delivery of the message.
	 * If we dont, we could incur a transaction timeout and cause mayhem...
	 */
	@Override
	public void onMessage(final Message message) {
		try {
			if (message instanceof TextMessage) {
				MSConvertJob job = MSConvertJob.unmarshal(new StringReader(((TextMessage)message).getText()));
				logger.info("Adding "+job.getJobID()+" to msconvert run q");
	    		
				/*
	    		 * must put the message in the runQueue BEFORE we return from this or the web service wont know about the job.
	    		 * Message does not expire after a set period in the runQueue (0)
	    		 */
	    		new SendMessage(logger, connectionFactory, runQueue, 0).send(job);
	    		mes.execute(new ConversionJobThread(logger, job, msconvert_config, doneQueue, connectionFactory));
	    	} else {
	    		logger.warning("Got unknown message: "+message);
	    	}
		} catch (JMSException|IOException|JAXBException ex) {
			ex.printStackTrace();
		}
	}

}
