package au.edu.unimelb.plantcell.servers.mascot.html;

import java.io.IOException;

/**
 * When a mascot CGI form submission fails (usually bad data) an object of this type will be thrown
 * 
 * @author acassin
 *
 */
public class FailedJobException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7259343525387461402L;

	public FailedJobException() {
		super("Mascot job submission FAILED!");
	}
	
	public FailedJobException(final String msg) {
		super(msg);
	}

	public FailedJobException(IOException ioe) {
		super(ioe);
	}
}
