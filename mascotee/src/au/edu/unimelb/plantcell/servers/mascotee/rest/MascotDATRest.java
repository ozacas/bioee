package au.edu.unimelb.plantcell.servers.mascotee.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;


@Stateless
@Path("/results")
public class MascotDATRest {
	@EJB
	private MascotConfig mascotConfig;
	
	@GET
	@Path("/dat/{dat}")
	public Response getDatFile(@PathParam("dat") final String dated_dat_file_base64_encoded) {
		if (mascotConfig == null) {
			return Response.serverError().build();
		}
		
		try {
			String dat_file = new String(Base64.getDecoder().decode(dated_dat_file_base64_encoded));
			if (dat_file == null || dat_file.indexOf("..") >= 0 || 
					dat_file.startsWith("/") || !dat_file.matches("^\\d{8}/F\\d+\\.dat$")) {
				throw new SOAPException("Illegal mascot dat file name: must be of the form: YYYYMMDD/F\\d+.dat - got "+dat_file);
			}
			final File f       = new File(mascotConfig.getDataRootFolder(), dat_file);
			
			StreamingOutput so = new StreamingOutput() {

				@Override
				public void write(OutputStream arg0) throws IOException {
					new DataHandler(new FileDataSource(f)).writeTo(arg0);
				}
				
			};
			return Response.ok(so).build();
		} catch (SOAPException e) {
			return Response.serverError().build();
		}
	}

}

