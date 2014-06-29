package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.util.HashMap;
import java.util.List;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.CompressionType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DataFileType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.FilterParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.PrecursorCorrectionType;

/**
 * 
 * @author acassin
 *
 */
public class ProteowizardJobValidator {
	
	public void validate(final ProteowizardJob job) throws SOAPException {
		if (job == null) {
			throw new SOAPException("No job!");
		}
		validateOutputFormat(job.getOutputFormat());
		validateInputData(job.getDataFile());
		validateCompression(job.getCompression());
		validatePrecursor(job.getPrecursorCorrection());
		validateFilters(job.getFilterParameters());
	}

	private void validateFilters(final FilterParametersType fp) {
		// may be omitted if filtering not required so...
		if (fp == null) {
			return;
		}
		
	}

	/**
	 * 
	 * @param pc
	 */
	private void validatePrecursor(final PrecursorCorrectionType pc) {
		// optional and only boolean fields so nothing to validate
	}

	/**
	 * Check required compression settings. The server may reject these settings if it cannot afford the CPU time.
	 * 
	 * @param compression
	 */
	private void validateCompression(final CompressionType compression) throws SOAPException {
		if (compression == null) {		// optional element
			return;
		}
		// but if specified it must be correct... but since all the fields are currently boolean there is nothing to do.
	}

	/**
	 * Check the data associated with the input data files to be converted
	 * @param dataFiles
	 * @throws SOAPException
	 */
	private void validateInputData(final List<DataFileType> dataFiles) throws SOAPException {
		// only one conversion may be done per job. But one conversion may require several files (eg. wiff & wiff.scan)
		// for some AB SciEx conversions. This is done to prevent someone hogging the queue by creating a job with 1000 files in it...
		if (dataFiles == null || dataFiles.size() < 1) {
			throw new SOAPException("No input data files!");
		}
		HashMap<String,Integer> format_counts = new HashMap<String,Integer>();
		for (DataFileType df : dataFiles) {
			String fmt = df.getFormat().toLowerCase().trim();
			validateInputDataFormat(fmt);
			if (df.getSuggestedName() == null || df.getSuggestedName().length() < 1) {
				throw new SOAPException("No suggested name!");
			}
			Integer i = format_counts.get(fmt);
			if (i == null) {
				i = Integer.valueOf(1);
			} else {
				i = Integer.valueOf(i+1);
			}
			format_counts.put(fmt, i);
		}
		
		if (format_counts.size() != 1) {
			throw new SOAPException("One file format must only be used per job!");
		}
		for (String s : format_counts.keySet()) {
			if (s.equals("wiff") && format_counts.get(s).intValue() > 3) {
				throw new SOAPException("Wiff conversions must have either 1 or 2 files");
			} else if (format_counts.get(s).intValue() > 1) {
				throw new SOAPException("Conversions from "+s+" must have exactly one input file!");
			}
		}
	}

	/**
	 * check the input data file format for one of the supported types
	 * @param fmt must be in lowercase
	 * @throws SOAPException
	 */
	private void validateInputDataFormat(final String fmt) throws SOAPException {
		if (fmt == null) {
			throw new SOAPException("No input file format!");
		}
		if (fmt.equals("mgf") || fmt.equals("wiff") || fmt.equals("raw") || fmt.equals("mgf") ||
				fmt.equals("mzxml") || fmt.equals("mzml") || fmt.equals("mz5")) {
			return;
		}
		throw new SOAPException("Unknown/unsupported input data format: "+fmt);
	}

	private void validateOutputFormat(final String outFormat) throws SOAPException {
		if (outFormat == null) {
			throw new SOAPException("Must specify output format: MGF, MzXML, MzML or MZ5");
		}
		String lc = outFormat.toLowerCase().trim();
		if (lc.equals("mgf") || lc.equals("mzxml") || lc.equals("mzml") || lc.equals("mz5")) {
			return;
		}
		throw new SOAPException("Unknown data format: "+lc);
	}
	
}
