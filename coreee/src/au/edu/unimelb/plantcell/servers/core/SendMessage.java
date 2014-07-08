package au.edu.unimelb.plantcell.servers.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import au.edu.unimelb.plantcell.servers.core.jaxb.JobMessageType;
import au.edu.unimelb.plantcell.servers.core.jaxb.ObjectFactory;

/**
 * Abstraction to avoid all the ugly exception handling elsewhere in the code
 * 
 * @author acassin
 *
 */
public class SendMessage {
	private final ConnectionFactory connectionFactory;
	private final Logger logger;
	private final Queue q;
	private final int expire;
	private MyMessageInterceptor mi;
	
	public SendMessage(final Logger logger, final ConnectionFactory cf, final Queue q) {
		this(logger, cf, q, 0);
	}
	
	public SendMessage(final Logger logger, final ConnectionFactory cf, final Queue q, final int expire_after_seconds) {
		this.logger = logger;
		this.connectionFactory = cf;
		this.q = q;
		this.expire = expire_after_seconds;
		this.mi = null;
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	public void setMessageInterceptor(final MyMessageInterceptor mi) {
		this.mi = mi;
	}
	
	/**
     * Sends message to the specified queue
     * 
     * @param input_job_msg MUST be a textual message to send
     * @throws JMSException 
     */
	public void send(final JobMessageType msg_to_send) {
		assert(msg_to_send != null);
		
		Connection connection = null;
		Session session = null;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, getAcknowledgementMethod());
			MessageProducer producer = session.createProducer(q);
			producer.setDeliveryMode(getDeliveryMethod());
			if (expire > 0) {
				producer.setTimeToLive(expire * 1000); // all messages sent to queue expire after constructor set limit
			}
			TextMessage tm = session.createTextMessage(marshal_to_text(msg_to_send));
			if (mi != null) {
				mi.interceptBeforeSend(msg_to_send, tm);
			}
			producer.send(tm);
			logger.info("Queued message: "+q.getQueueName() + " msgid="+ tm.getJMSMessageID());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected String marshal_to_text(final JobMessageType msg) throws JAXBException, IOException {
		StringWriter sw = new StringWriter();
		Class<? extends JobMessageType> clz = msg.getClass();
		JAXBContext   jc = JAXBContext.newInstance(clz); 
		Marshaller     m = jc.createMarshaller();
		ObjectFactory of = new ObjectFactory();
		Object      root = of.createJobMessage(msg);
		m.marshal(root, sw);
		sw.close();
		return sw.toString();
	}

	public int getDeliveryMethod() {
		return DeliveryMode.NON_PERSISTENT;
	}

	public int getAcknowledgementMethod() {
		return Session.AUTO_ACKNOWLEDGE;
	}
}
