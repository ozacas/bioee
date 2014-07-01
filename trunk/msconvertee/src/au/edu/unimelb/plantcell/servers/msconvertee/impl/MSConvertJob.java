package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import au.edu.unimelb.plantcell.servers.core.jaxb.JobMessageType;
import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DataFileType;
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
	private File data_folder;
	
	public MSConvertJob() {
		setDataFolder(null);
	}
	
	public MSConvertJob(final ProteowizardJob job) throws IOException {
		this();
		assert(job != null);
		saveData(job);
		this.setJobID(makeRandomUUID());
		marshal(job);
		this.setResults(null);
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
		return (ProteowizardJob) m.unmarshal(new File(this.getInputParameters()));
	}
	
	/**
	 * Save the data and update this to reflect the URLs where the data has been saved.
	 * 
	 * @param job is modified so that the data files are removed after it is persisted (WARNING: SIDE-EFFECT!)
	 * @throws IOException
	 */
	private void saveData(final ProteowizardJob job) {
		final List<String> urls = this.getInputData().getUrl();
		urls.clear();
		for (DataFileType dft : job.getDataFile()) {
			FileOutputStream os = null;
			try {
				File f = File.createTempFile("msconvert_input_data", "file.raw", getDataFolder());
				setDataFolder(f.getParentFile());
				os = new FileOutputStream(f);
				dft.getData().writeTo(os);
				urls.add(f.toURI().toURL().toExternalForm());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}
		}
		job.getDataFile().clear();		// dont want to persist the original XML related to the input data now that it has been successfully copied to this
	}
	
	public File getDataFolder() {
		return data_folder;
	}
	
	public void setDataFolder(File data_folder) {
		this.data_folder = data_folder;
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
	 * Return the local data URI for the specified input data file's UUID. In this way code is isolated from
	 * having to know where/how the data is stored (only msconvert code needs to know for the purposes of the correct command line)
	 * 
	 * @param uuid
	 * @return
	 */
	public String getDataURI(final UUID uuid) {
		assert(uuid != null);
		
		List<String> uris = inputData.getUrl();
		List<String> ids  = inputData.getUuid();
		if (uris.size() != ids.size()) {
			// vectors must be the same size ie. every data file has a corresponding id...
			return null;
		}
		for (int i=0; i<uris.size(); i++) {
			UUID u = UUID.fromString(ids.get(i));
			if (u.equals(uuid)) {
				return uris.get(i);
			}
		}
		return null;
	}

}
