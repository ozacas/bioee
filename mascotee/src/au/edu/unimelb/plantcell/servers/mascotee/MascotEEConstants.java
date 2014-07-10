package au.edu.unimelb.plantcell.servers.mascotee;

public class MascotEEConstants {
	/*
	 * All messages in the the completed queue are tagged with this property
	 * which has a value of FINISHED. Messages in the job queue have a value
	 * of "QUEUED" if the job at the head of the q is currently running.
	 */
	public final static String  MASCOTEE_CURRENT_STATUS_PROPERTY = "MASCOTEE_CURRENT_STATUS";
	
	/*
	 * After how many seconds does the completed job message expire in the doneQueue?
	 */
	public final static int EXPIRE_AFTER = 48 * 60 * 60;		// seconds
}
