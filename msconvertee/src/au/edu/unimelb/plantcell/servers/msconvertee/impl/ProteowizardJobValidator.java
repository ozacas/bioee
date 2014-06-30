package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.AbsoluteThresholdType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ActivationFilteringType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ChargeStateFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.CompressionType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DataFileType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.DeisotopeFilteringType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.EtdFilteringType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.FilterParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.KeepNThresholdType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MS2DenoiseType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MostOrLeast;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MsLevelType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.MzPrecursorFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.PeakPickingType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.PrecursorCorrectionType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.RelativeThresholdType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ScanEventFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ScanFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ScanNumberFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ScanTimeFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ThresholdParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.WindowFilterType;

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

	private void validateFilters(final FilterParametersType fp) throws SOAPException {
		// may be omitted if filtering not required so...
		if (fp == null) {
			return;
		}
		int n_filters = 0;
		if (fp.getActivationFilter() != null) {
			validateActivationFilter(fp.getActivationFilter());
			n_filters++;
		}
		if (fp.getAnalyzerFilter() != null) {
			validateAnalyzerFilter(fp.getAnalyzerFilter());
			n_filters++;
		}
		if (fp.getChargeStateFilter() != null) {
			validateChargeStateFilter(fp.getChargeStateFilter());
			n_filters++;
		}
		if (fp.getDeisotopeFilter() != null) {
			validateDeisotopeFilter(fp.getDeisotopeFilter());
			n_filters++;
		}
		if (fp.getEtdFilter() != null) {
			validateETDFilter(fp.getEtdFilter());
			n_filters++;
		}
		if (fp.getIntensityFilter() != null) {
			validateIntensityFilter(fp.getIntensityFilter());
			n_filters++;
		}
		if (fp.getMs2Denoise() != null) {
			validateDenoiseFilter(fp.getMs2Denoise());
			n_filters++;
		}
		if (fp.getMsLevelFilter() != null) {
			validateMsLevels(fp.getMsLevelFilter());
			n_filters++;
		}
		if (fp.getMzPrecursorFilter() != null) {
			validatePrecursorFilter(fp.getMzPrecursorFilter());
			n_filters++;
		}
		if (fp.getMzWindowFilter() != null) {
			validateWindowFilter(fp.getMzWindowFilter());
			n_filters++;
		}
		if (fp.getPeakPicking() != null) {
			validatePeakPicking(fp.getPeakPicking());
			n_filters++;
		}
		if (fp.getPolarityFilter() != null) {
			validatePolarityFilter(fp.getPolarityFilter());
			n_filters++;
		}
		if (fp.getScanFilter() != null) {
			validateScanFilter(fp.getScanFilter());
			n_filters++;
		}
		if (n_filters > 1) {
			throw new SOAPException("Only one filter may be specified per job!");
		}
	}

	private void validateScanFilter(final ScanFilterType sf) throws SOAPException {
		assert(sf != null);
		int n_filters = 0;
		if (sf.getEventFilter() != null) {
			validateScanEventFilter(sf.getEventFilter());
			n_filters++;
		}
		if (sf.getNumberFilter() != null) {
			validateScanNumberFilter(sf.getNumberFilter());
			n_filters++;
		}
		if (sf.getTimeFilter() != null) {
			validateScanTimeFilter(sf.getTimeFilter());
			n_filters++;
		}
		
		if (n_filters != 1) {
			throw new SOAPException("Only one scan filter can be chosen per job: one of time, event or number filter");
		}
	}

	private void validateScanTimeFilter(final ScanTimeFilterType tf) throws SOAPException {
		double lower = tf.getLower();
		double upper = tf.getUpper();
		if (lower < 0.0 || upper < 0.0) {
			throw new SOAPException("Bogus scan time: "+lower+"-"+upper);
		}
		if (upper < lower) {
			throw new SOAPException("Upper scan time filter cannot be before lower time: "+lower+"<"+upper);
		}
	}

	private void validateScanNumberFilter(final ScanNumberFilterType nf) throws SOAPException {
		assert(nf != null);
		List<Integer> l = nf.getAcceptScan();
		if (l == null || l.size() < 1) {
			throw new SOAPException("No scan numbers provided for filtering!");
		}
		for (Integer i : l) {
			if (i.intValue() < 0) {
				throw new SOAPException("Bogus scan number: "+i);
			}
		}
	}

	private void validateScanEventFilter(final ScanEventFilterType ef) throws SOAPException {
		assert(ef != null);
		List<Integer> l = ef.getAcceptEvent();
		if (l == null || l.size() < 1) {
			throw new SOAPException("No scan events provided for filtering!");
		}
		for (Integer i : l) {
			if (i.intValue() < 0) {
				throw new SOAPException("Bogus scan event: "+i);
			}
		}
	}

	private void validatePolarityFilter(final String pf) throws SOAPException {
		if (pf == null || pf.length() < 1) {
			throw new SOAPException("No polarity!");
		}
		if (pf.equals("+") || pf.equals("-") || pf.equalsIgnoreCase("positive") || pf.equalsIgnoreCase("negative")) {
			return;
		}
		throw new SOAPException("Unknown polarity: "+pf);
	}

	private void validatePeakPicking(final PeakPickingType pp) throws SOAPException {
		assert(pp != null);
		validateMsLevels(pp.getMsLevels());
		// preferVendor is just boolean so we ignore...
	}

	private void validateWindowFilter(final WindowFilterType w) throws SOAPException {
		assert(w != null);
		double low = w.getLow();
		double high= w.getHigh();
		if (low < 0.0 || high < 0.0) {
			throw new SOAPException("Window boundaries must be positive!");
		}
		if (high < low) {
			throw new SOAPException("Higher bound must be greater than lower bound!");
		}
	}

	private void validatePrecursorFilter(final MzPrecursorFilterType pf) throws SOAPException {
		assert(pf != null);
		if (pf.getAcceptMZ() == null || pf.getAcceptMZ().size() < 1)  {
			throw new SOAPException("No precursor masses specified for precursor filter!");
		}
		try {
			for (String s : pf.getAcceptMZ()) {
				Double d = Double.valueOf(s);
				if (d.doubleValue() < 0.0d) {
					throw new SOAPException("Negative precursor mass!");
				}
			}
		} catch (NumberFormatException nfe) {
			throw new SOAPException("Invalid precursor mass!", nfe);
		}
	}

	private void validateDenoiseFilter(final MS2DenoiseType ms2dn) throws SOAPException {
		assert(ms2dn != null);
		Integer n = ms2dn.getPeaksInWindow();
		if (n.intValue() < 1) {
			throw new SOAPException("Must be at least one peak per window!");
		}
		Double window_width_da = ms2dn.getWindowWidth();
		if (window_width_da <= 0.0d) {
			throw new SOAPException("Window width must be positive!");
		}
	}

	private void validateIntensityFilter(final ThresholdParametersType i) throws SOAPException {
		assert(i != null);
		int n_filters = 0;
		if (i.getAbsoluteThreshold() != null) {
			validateAbsoluteThreshold(i.getAbsoluteThreshold());
			n_filters++;
		}
		if (i.getBasePeakRelativeThreshold() != null) {
			validateRelativeThreshold(i.getBasePeakRelativeThreshold());
			n_filters++;
		}
		if (i.getKeepNThreshold() != null) {
			validateNThreshold(i.getKeepNThreshold());
			n_filters++;
		}
		if (i.getKeepNThresholdIncludingTies() != null) {
			validateNThreshold(i.getKeepNThresholdIncludingTies());
			n_filters++;
		}
		if (i.getTICAbsoluteThreshold() != null) {
			validateAbsoluteThreshold(i.getTICAbsoluteThreshold());
			n_filters++;
		}
		if (i.getTICRelativeThreshold() != null) {
			validateRelativeThreshold(i.getTICRelativeThreshold());
			n_filters++;
		}
		if (n_filters != 1) {
			throw new SOAPException("Only one intensity threshold method may be used per job!");
		}
	}

	private void validateRelativeThreshold(final RelativeThresholdType rt) throws SOAPException {
		assert(rt != null);
		validateMostOrLeast(rt.getWhat());
		if (rt.getFraction() < 0.0 || rt.getFraction() > 1.0) {
			throw new SOAPException("Fraction must be in the range [0..1.0]");
		}
	}

	private void validateNThreshold(final KeepNThresholdType kn) throws SOAPException {
		assert(kn != null);
		if (kn.getN() <= 0) {
			throw new SOAPException("N must be greater than zero!");
		}
		validateMostOrLeast(kn.getWhat());
		validateMsLevels(kn.getMsLevels());
	}

	
	private void validateMsLevels(MsLevelType msLevels) throws SOAPException {
		if (msLevels == null || msLevels.getMsLevel() == null || msLevels.getMsLevel().size() < 1) {
			throw new SOAPException("No MS levels specified!");
		}
		for (Integer i : msLevels.getMsLevel()) {
			if (i.intValue() < 1) {
				throw new SOAPException("Bogus ms level: "+i);
			}
		}
	}

	private void validateAbsoluteThreshold(final AbsoluteThresholdType abs) throws SOAPException {
		assert(abs != null);
		validateMostOrLeast(abs.getWhat());
		if (abs.getIntensityThreshold() <= 0.0) {
			throw new SOAPException("Bogus intensity threshold: "+abs.getIntensityThreshold());
		}
	}

	private void validateMostOrLeast(final MostOrLeast ml) throws SOAPException {
		if (ml == null) {
			throw new SOAPException("Most or least missing!");
		}
		String name = ml.name().toLowerCase().trim();
		if (name.indexOf("most") >= 0 || name.indexOf("least") >= 0) {
			return;
		} else {
			throw new SOAPException("Expected either 'most' or 'least'!");
		}
	}

	private void validateETDFilter(final EtdFilteringType etd) throws SOAPException {
		assert(etd != null);
		String tol = etd.getTolerance();
		Pattern p = Pattern.compile("^[\\d\\.]+\\s*((PPM)|(MZ))$");
		Matcher m = p.matcher(tol);
		if (m.matches()) {
			return;
		} else {
			throw new SOAPException("Invalid ETD tolerance: "+tol);
		}
	}

	private void validateDeisotopeFilter(final DeisotopeFilteringType df) throws SOAPException {
		assert(df != null);
		if (df.getMzTolerance() <= 0.0d) {
			throw new SOAPException("Invalid deisotoping tolerance (typically in the range [0.01, 0.5])");
		}
	}

	private void validateChargeStateFilter(final ChargeStateFilterType csf) throws SOAPException {
		assert(csf != null);
		if (csf.getAcceptCharge() == null || csf.getAcceptCharge().size() < 1) {
			throw new SOAPException("No charges to accept!");
		}
		for (Integer i : csf.getAcceptCharge()) {
			int mag = Math.abs(i.intValue());
			if (mag > 10 || mag < 1) {
				throw new SOAPException("Charge must be in the range [1..10] - got "+i);
			}
		}
	}

	private void validateAnalyzerFilter(String analyzerFilter) throws SOAPException {
		if (analyzerFilter == null || analyzerFilter.length() < 1) {
			throw new SOAPException("No analyzer filter!");
		}
		String a = analyzerFilter.toLowerCase().trim();
		if (a.equals("quad") || a.equals("orbi") || a.equals("ft") || a.equals("it") || a.equals("tof")) {
			return;
		}
		throw new SOAPException("Unknown analyzer: "+a);
	}

	private void validateActivationFilter(final ActivationFilteringType f) throws SOAPException {
		assert(f != null);
		List<String> l = f.getActivationToAccept();
		if (l == null || l.size() < 1) { 
			throw new SOAPException("No activation methods specified!");
		}
		for (String s : l) {
			s = s.toLowerCase().trim();
			if (!( s.equals("cid") || s.equals("etd") || s.equals("SA") ||
					s.equals("hcd") || s.equals("bird") || s.equals("ecd") || 
					s.equals("irmpd") || s.equals("pd") || s.equals("psd") || 
					s.equals("pqd") || s.equals("sid") || s.equals("sori") 
					)) {
				throw new SOAPException("Unknown activation method: "+s);
			}
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
