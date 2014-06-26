package au.edu.unimelb.plantcell.servers.mascotee.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
public class MascotEEApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(MascotDATRest.class,MascotDBRest.class));
	}
}
