package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.commons.exec.CommandLine;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DataFileType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.FilterParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MsLevelType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.PeakPickingType;

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
		addDataFiles(cl, conversion.getDataFile());
		addFilters(cl, conversion.getFilterParameters());
		return cl;
	}

	private void addDataFiles(final CommandLine cl, final List<DataFileType> input_files) throws IOException {
		assert(input_files != null);		// NB: job has already been validated by the time we get here
		
		// if there is only one file to be converted (the normal case) we just handle that here...
		if (input_files.size() == 1) {
			cl.addArgument(job.getDataURI(UUID.fromString(input_files.get(0).getUUID())));
		} else if (input_files.size() > 1) {
			String fmt = input_files.get(0).getFormat().trim().toLowerCase();
			if (!fmt.equals("wiff")) {
				throw new IOException("Only one data file may be supplied per job!");
			}
			
		} else {
			throw new IOException("Only WIFF file conversion permits more than one input file");
		}
	}

	private void addFilters(final CommandLine cl, final FilterParametersType fpt) throws IOException {
		assert(cl != null);
		if (fpt == null) {
			return;
		}
		// peak picking filter MUST ALWAYS BE FIRST according to msconvert filter documentation
		// http://proteowizard.sourceforge.net/tools/filters.html
		if (fpt.getPeakPicking() != null) {
			cl.addArgument("--filter");
			PeakPickingType ppt = fpt.getPeakPicking();
			cl.addArgument("peakPicking "+ppt.isPreferVendor()+" "+asIntSet(ppt.getMsLevels()), true);
		}
		
		// other filters are added in no particular order. Client is expected to serialise filters when required.
		// TODO FIXME
	}
	
	private String asIntSet(final MsLevelType msLevels) throws IOException {
		StringBuilder sb = new StringBuilder();
		if (msLevels.getMsLevel().size() < 1) {
			throw new IOException("No ms-levels specified!");
		}
		for (Integer i : msLevels.getMsLevel()) {
			sb.append(String.valueOf(i));
			sb.append(' ');
		}
		return sb.toString();
	}

	private void addOutputFormatOption(final CommandLine cl, final String outFormat) throws IOException {
		assert(cl != null && outFormat != null);
		if (outFormat.equals("mgf")) {
			cl.addArgument("--mgf");
		} else if (outFormat.equals("mzml")) {
			cl.addArgument("--mzML");
		} else if (outFormat.equals("mz5")) {
			cl.addArgument("--mz5");
		} else if (outFormat.equals("mzxml")) {
			cl.addArgument("--mzXML");
		} else {
			throw new IOException("Unsupported output format: "+outFormat);
		}
	}
}
