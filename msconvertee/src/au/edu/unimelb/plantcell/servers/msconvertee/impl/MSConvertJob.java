package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.edu.unimelb.plantcell.servers.core.jaxb.JobMessageType;
import au.edu.unimelb.plantcell.servers.core.jaxb.ResultsType;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ProteowizardJobType;

/**
 * Responsible for the managing the state associated with a web-service job throughout the
 * entire lifetime of the job.
 * 
 * @author acassin
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MSConvertJob extends JobMessageType {
	/*
	 * the data folder is associated with the job, so that when the job is purged it knows what to delete.
	 * All files associated with the job are relative to this folder
	 */
	@XmlElement
	private File data_folder;		// input data files are located within this folder
	@XmlElement
	private File output_folder;		// but the output_folder (for results of conversion/filtering) is also directly beneath data_folder
	
	// only for use by JAXB
	protected MSConvertJob() {
	}
	
	public MSConvertJob(final ProteowizardJob job, final DataHandler[] input_data_files, 
			final File data_directory) throws IOException {
		
		this.setJobID(makeRandomUUID());
		File temp_data_directory = new File(data_directory, getJobID()+"_data.d");
		setandCreateDataFolder(temp_data_directory);
		ResultsType rt = new ResultsType();
		rt.setStatus("UNFINISHED");
		setResults(rt);
		assert(job != null);
		saveData(job.getInputDataFormat(), job.getInputDataNames(), input_data_files);
		marshal(job);		// invokes setInputParameters()
		setResults(null);	// no results yet
		// and finally make sure the output folder is created and ready for use...
		setAndCreateOutputFolder(new File(temp_data_directory, "results"));
	}
	
	/**
	 * Return a suitable value for the ID member of the specified instance. The instance is not modified in any way.
	 * The current implementation uses a type 4 randomly computed UUID
	 * 
	 * @return
	 */
	private String makeRandomUUID() {
		return "msconvertee-" + UUID.randomUUID().toString();
	}
	
	/**
	 * {@link #saveData(ProteowizardJobType)} must have already completed by the time of this call
	 * or JAXB will persist all the data to the xml file... dont do that! ;-)
	 * @param job
	 */
	private void marshal(final ProteowizardJob job) {
		try {
			JAXBContext jc = JAXBContext.newInstance(job.getClass());
			Marshaller m = jc.createMarshaller();
			File param_file = File.createTempFile("msconvert_", "parameters.xml", getDataFolder());
			m.marshal(job, param_file);
			this.setInputParameters(param_file.toURI().toURL().toExternalForm());
		} catch (JAXBException|IOException e) {
			e.printStackTrace();
		}
	}
	
	private ProteowizardJob unmarshal() throws JAXBException,IOException {
		JAXBContext jc = JAXBContext.newInstance(ProteowizardJob.class);
		Unmarshaller m = jc.createUnmarshaller();
		return (ProteowizardJob) m.unmarshal(new URL(this.getInputParameters()));
	}
	
	/**
	 * Save the data and update this to reflect the URLs where the data has been saved.
	 * 
	 * @param job is modified so that the data files are removed after it is persisted (WARNING: SIDE-EFFECT!)
	 * @throws IOException
	 */
	private void saveData(final String data_format, final List<String> names, final DataHandler[] data) throws IOException {
		if (data_format == null || names == null || data == null || data.length != names.size()) {
			throw new IOException("Illegal/missing parameters to saveData()");
		}
		File data_folder = getDataFolder();
		for (int i=0; i<names.size(); i++) {
			String name = names.get(i);
			DataHandler dh = data[i];
			FileOutputStream fos = new FileOutputStream(new File(data_folder, name));
			try {
				dh.writeTo(fos);
			} finally {
				fos.close();
			}
		}
	}
	
	private File getDataFolder() {
		return data_folder;
	}
	
	private synchronized void setandCreateDataFolder(File data_folder) throws IOException {
		this.data_folder = data_folder;
		if (data_folder.exists() && data_folder.isDirectory()) {
			return;
		}
		if (!data_folder.mkdir()) {
			throw new IOException("Cannot create data folder: "+data_folder.getAbsolutePath());
		}
	}

	public static MSConvertJob unmarshal(final Reader rdr) throws JAXBException {
		JAXBContext  jc = JAXBContext.newInstance(MSConvertJob.class);
		Unmarshaller um = jc.createUnmarshaller();
		MSConvertJob mj = (MSConvertJob) um.unmarshal(rdr);
		return mj;
	}

	/**
	 * Must add arguments relevant to job parameters
	 * @param cl
	 */
	public ProteowizardJob asProteowizardJob() throws JAXBException,IOException {
		return unmarshal();
	}

	/**
	 * Creates an output folder suitable for running msconvert. Must create it in the location
	 * required by msconvert (see ConversionJobThread)
	 */
	private void setAndCreateOutputFolder(final File out) throws IOException {
		output_folder = out;
		out.mkdir();
	}
	
	/**
	 * MSConvert works best when the output folder is one-level buried relative to the input
	 * data files. So we return a suitable folder (which has been created) for use. The caller
	 * is responsible for deleting it at a suitable time
	 * 
	 * @return the output folder configured at construction time
	 */
	public File getOutputFolder() {
		return output_folder;
	}

}
