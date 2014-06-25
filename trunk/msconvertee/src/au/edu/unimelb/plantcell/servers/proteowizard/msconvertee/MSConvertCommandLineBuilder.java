package au.edu.unimelb.plantcell.servers.proteowizard.msconvertee;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.exec.CommandLine;

import au.edu.unimelb.plantcell.servers.proteowizard.endpoints.ProteowizardJob;

/**
 * Uses the builder design pattern to construct an instance of the CommandLine
 * which reflects the data files and required conversion based on the supplied
 * 
 * @author acassin
 *
 */
public class MSConvertCommandLineBuilder {
	private MSConvertJob    job;
	private MSConvertConfig config;
	
	public MSConvertCommandLineBuilder(final MSConvertConfig conf) {
		assert(conf != null);
		this.config = conf;
	}
	
	/**
	 * Obtain the parameters from the specified message
	 * @param j must not be null
	 */
	public MSConvertCommandLineBuilder fromJob(final MSConvertJob j) {
		assert(j != null);
		this.job = j;
		return this;
	}
	
	public CommandLine build() throws JAXBException,IOException {
		CommandLine cl = config.getCommandLine();
		if (cl == null) {
			throw new IOException("No msconvert program available!");
		}
		ProteowizardJob conversion = job.asProteowizardJob();
		addOutputFormatOption(cl, conversion.getOutputFormat().toLowerCase());
		
		return cl;
	}

	private void addOutputFormatOption(final CommandLine cl, final String outFormat) {
		assert(cl != null && outFormat != null);
		if (outFormat.equals("mgf")) {
			cl.addArgument("--mgf");
		} else if (outFormat.equals("mzml")) {
			cl.addArgument("--mzML");
		} else if (outFormat.equals("mz5")) {
			cl.addArgument("--mz5");
		} else if (outFormat.equals("mzxml")) {
			cl.addArgument("--mzXML");
		}
	}
}
