package au.edu.unimelb.plantcell.servers.core;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.soap.SOAPException;

/**
 * Provides common methods useful to all web service server implementations
 * 
 * @author acassin
 *
 */
public abstract class AbstractWebService {
	
	public AbstractWebService() {
	}
	
	@SuppressWarnings("unchecked")
	protected TextMessage findMessage(final Queue q, String msgID) throws SOAPException {
		assert(q != null && msgID != null && msgID.length() > 0);
		
		Connection connection = null;
		Session       session = null;
		try {
			connection = getConnectionFactory().createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueBrowser        qb = session.createBrowser(q);
			Enumeration<Message> e = qb.getEnumeration();
			while (e.hasMoreElements()) {
				Message m = e.nextElement();
				String id = m.getStringProperty(getMessageIDPropertyName());
				//logger.info(id+ " : "+msgID);
				if (msgID.equals(id) && m instanceof TextMessage) {
					Logger logger = getLogger();
					if (logger != null) {
						logger.info("Found message for "+msgID+" in q: "+q.getQueueName());
					}
					return (TextMessage) m;
				}
			}
			//logger.warning("Failed to find mascot job for: "+msgID);
			return null;
		} catch (JMSException e) {
			throw new SOAPException(e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					throw new SOAPException(e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					throw new SOAPException(e);
				}
			}
		}
	}


	/**
	 * Throws an exception if there is not free temp space available to continue queueing jobs. For now
	 * this is a hardcoded 500MB limit. Requires java 1.6 or later for its use of the newer File.getFreeSpace() API
	 * 
	 * @throws SOAPException
	 */
	protected void checkFreeStorageIsAvailable() throws SOAPException {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("test", "for_free_storage.fasta");
			if (tmpFile.getFreeSpace() < 500 * 1024 * 1024) {
				throw new SOAPException("Insufficient disk space to queue job: aborting!");
			}
		} catch (IOException ioe) {
			throw new SOAPException("Unable to check for free space for new search, aborting: "+ioe.getMessage());
		} finally {
			if (tmpFile != null) {
				tmpFile.delete();
			}
		}
	}

	
	/**
	 * Returns a ConnectionFactory which is used by {@link #findMessage(Queue, String)}
	 * 
	 * @return must not be null, unless {@link findMessage} is also overriden
	 * @throws SOAPException
	 */
	protected abstract ConnectionFactory getConnectionFactory() throws SOAPException;
	
	/**
	 * Returns a property name which must be present in the message which provides the ID to be used
	 * for comparison in {@link #findMessage(Queue, String)}
	 * @return
	 */
	protected abstract String getMessageIDPropertyName();
	
	/**
	 * A logger instance which is used by {@link #findMessage(Queue, String)}
	 * @return may be null
	 */
	protected abstract Logger getLogger();
}
