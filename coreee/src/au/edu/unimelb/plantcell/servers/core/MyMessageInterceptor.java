package au.edu.unimelb.plantcell.servers.core;

import javax.jms.TextMessage;

import au.edu.unimelb.plantcell.servers.core.jaxb.JobMessageType;

public interface MyMessageInterceptor {
	/**
	 * Called if an interceptor is defined on an instance of {@link SendMessage}, just prior to sending the message.
	 * If the implementation throws, sending will be aborted. Typically an implementation will add message properties prior to sending.
	 * 
	 * @param msg provided for convenience, but changing this state will have no impact on the message sent
	 * @param msg_as_txt making changes on this message will be sent to the specified queue
	 * @throws if an Exception is thrown, sending will be aborted. Can be used to reject incorrectly formatted messages.
	 */
	public void interceptBeforeSend(final JobMessageType msg, final TextMessage msg_as_txt) throws Exception;
}
