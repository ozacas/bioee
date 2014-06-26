package au.edu.unimelb.plantcell.servers.mascotee.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;


@Stateless
@Path("/database")
public class MascotDBRest {
	private Logger logger = Logger.getLogger("Mascot DB download");
	@EJB
	private MascotConfig mascotConfig;
	
	@GET
	@Path("/get/{file}")
	public Response getDatabase(@PathParam("file") final String file) {
		if (mascotConfig == null) {
			logger.warning("No available mascot configuration!");
			return Response.serverError().build();
		}
		
		String decoded_db = new String(Base64.getDecoder().decode(file));
		if (!mascotConfig.hasSequenceFile(new File(decoded_db))) {
			logger.warning("No such mascot file: "+decoded_db);
			return Response.serverError().build();
		}
		final File final_file = new File(decoded_db);
		StreamingOutput so = new StreamingOutput() {

			@Override
			public void write(OutputStream arg0) throws IOException {
				new DataHandler(new FileDataSource(final_file)).writeTo(arg0);
			}
			
		};
		return Response.ok(so).build();
	}

}
