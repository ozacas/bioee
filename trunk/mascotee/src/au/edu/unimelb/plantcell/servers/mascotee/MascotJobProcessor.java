package au.edu.unimelb.plantcell.servers.mascotee;

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
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;

/**
 * Responsible for processing each job submitted to the job queue from the web service. At this time
 * we dont not support clustering or more than one job at a time since that might overload available system resources.
 * We also dont have any way of integrating use of system resources with mascot-website users. Sigh.
 * 
 * @author acassin
 *
 */
@MessageDriven
public class MascotJobProcessor implements MessageListener {
	private final static Logger logger = Logger.getLogger("Mascot Job Processor");

	/*
	 * TomEE will load the MascotConfig instance for us
	 */
	@EJB private MascotConfig mascot_config;
	
	/*
	 * We also need the completion queue once a job has been done 
	 * so that the web service can identify the results to report to a caller.
	 * Completion queue entries expire: currently 48 hours and at this time
	 * another MDB will cleanup the input data files (but not the .dat file computed by mascot)
	 */
	@Resource(name="runQueue")
	private Queue runQueue;	// waiting to run or currently running
	@Resource(name="doneQueue")
	private Queue doneQueue;
	@Resource
	private ConnectionFactory connectionFactory;
	
	/**
	 * given that each mascot job needs quite a bit of state to run, we must be careful which ExecutorService we
	 * use so that parallel access to state is carefully managed. For now we avoid this problem by using a single thread executor
	 */
	private static final ExecutorService mes = Executors.newSingleThreadExecutor();
	
    /**
     * Default constructor. 
     */
    public MascotJobProcessor() {
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	/*
    	 * All mascotee JMS messages are text messages. So we handle that here...
    	 */
    	if (message instanceof TextMessage) {
    		/*
    		 * The message should be a serialised MascotJob so...
    		 */
    		try {
    			MascotJob job = MascotJob.unmarshal(new StringReader(((TextMessage)message).getText()));
				/*
				 * Note we start a separate thread since we must return from this function quickly to acknowledge delivery of the message.
				 * If we dont, we could incur a transaction timeout and lose messages...
				 */
				logger.info("Adding mascot job to run queue: "+job.getJobID());
				/*
	    		 * must put the message in the runQueue BEFORE we return from this or the web service wont know about the job.
	    		 * Message does not expire after a set period in the runQueue (0)
	    		 */
	    		new SendMessage(logger, connectionFactory, runQueue, 0).send(job);
	    		mes.execute(new MascotRunThread(logger, job, mascot_config, doneQueue, connectionFactory));
			} catch (JMSException|JAXBException e) {
				logger.warning("Rejecting message from mascot run queue (improperly formatted)");
				e.printStackTrace();
			}
    	} else {
    		logger.warning("Got unknown message: "+message);
    	}
    }

}
