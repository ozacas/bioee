package au.edu.unimelb.plantcell.servers.mascotee;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

/**
 * Messages in the completed queue will expire after 48 hours. This MDB listens
 * to the advisory messages from ActiveMQ about such expired messages and cleans up
 * input data files. But only after the expiry period. There will be nothing left to delete
 * if the client has already invoked purgeJob() but thats ok.
 * 
 * @author acassin
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "ActiveMQ.Advisory.Expired.Queue"), 
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		}, 
		mappedName = "ActiveMQ.Advisory.Expired.Queue")
public class ExpiredJobProcessor implements MessageListener {
	private final Logger logger = Logger.getLogger("Expired Job Processor");
	
	@Override
	public void onMessage(Message m) {
		if (m != null && m instanceof TextMessage) {
			try {
				TextMessage tm = (TextMessage) m;
				if (tm.getStringProperty("mascotee") != null) {
					logger.info("*** About to delete expired job");
					MascotJob mj = MascotJob.unmarshal(new StringReader(tm.getText()));
					if (mj != null) {
						mj.cleanupInputDataFiles(logger);
					}
				}
			} catch (JMSException|JAXBException e) {
				e.printStackTrace();
			}
		}
	}

}
