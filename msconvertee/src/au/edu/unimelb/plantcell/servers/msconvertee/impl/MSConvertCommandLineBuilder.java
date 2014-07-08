package au.edu.unimelb.plantcell.servers.msconvertee.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.exec.CommandLine;

import au.edu.unimelb.plantcell.servers.msconvertee.endpoints.ProteowizardJob;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.AbsoluteThresholdType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ActivationFilteringType;
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
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ScanFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ThresholdParametersType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.WindowFilterType;
import au.edu.unimelb.plantcell.servers.msconvertee.jaxb.ZeroesFilterType;

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
	private File output_folder;
	
	public MSConvertCommandLineBuilder(final MSConvertConfig conf) {
		assert(conf != null);
		this.config = conf;
		this.output_folder = null;
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
		
		// output folder is always first in command line args list
		if (output_folder != null) {
			cl.addArgument("-o");
			cl.addArgument(fixPath(output_folder));
		}
		ProteowizardJob conversion = job.asProteowizardJob();
		// then output format
		addOutputFormatOption(cl, conversion.getOutputFormat().toLowerCase());
		// input data files
		addDataFiles(cl, conversion.getInputDataFormat(), conversion.getInputDataNames());
		
		// and any user configuration options
		addFilters(cl, conversion.getFilterParameters());
		addPrecursorCorrection(cl, conversion.getPrecursorCorrection());
		return cl;
	}

	private String fixPath(final File f) throws IOException {
		if (f == null) {
			throw new IOException("File must not be null!");
		}
		String ret = f.getAbsolutePath();
		ret = ret.replaceAll("\\\\", "/");
		return ret;
	}

	private void addPrecursorCorrection(final CommandLine cl, final PrecursorCorrectionType pc) throws IOException {
		if (pc == null) {
			return;
		}
		boolean recalc = (pc.isRecalculate() != null) ? pc.isRecalculate() : Boolean.FALSE;
		boolean refine = (pc.isRefine() != null) ? pc.isRefine() : Boolean.FALSE;
		
		if (recalc) {
			cl.addArgument("--filter");
			cl.addArgument("precursorRecalculation");
		}
		if (refine) {
			cl.addArgument("--filter");
			cl.addArgument("precursorRefine");
		}
	}

	/**
	 * Adds input data files in a suitable format to the specified <code>CommandLine</code>
	 * 
	 * @param cl command line to add data files to
	 * @param dataFormat list of input url's
	 * @param namedFiles input data file format
	 * @throws IOException
	 */
	private void addDataFiles(final CommandLine cl, final String dataFormat, final List<String> namedFiles) throws IOException {
		assert(dataFormat != null);		// NB: job has already been validated by the time we get here
		
		int n = namedFiles.size();
		if (n < 1) {
			throw new IOException("No data files to add to command line!");
		}
		
		if (dataFormat.equals("wiff")) {
			for (String name : namedFiles) {
				if (name.endsWith(".scan")) {
					continue;
				}
				cl.addArgument("../"+name);
			}
		} else {
			for (String name : namedFiles) {
				cl.addArgument("../"+name);
			}
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
			cl.addArgument("peakPicking "+ppt.isPreferVendor()+" "+asIntSet(ppt.getMsLevels(), false), true);
			if (ppt.isFixMetadata() != null && ppt.isFixMetadata()) {
				cl.addArgument("--filter");
				cl.addArgument("metadataFixer");
			}
		}
		
		// other filters are added in no particular order. Client is expected to serialise filters when required.
		if (fpt.getActivationFilter() != null) {
			cl.addArgument("--filter");
			ActivationFilteringType aft = fpt.getActivationFilter();
			cl.addArgument("activation "+asString(aft.getActivationToAccept(), null));
		}
		if (fpt.getAnalyzerFilter() != null) {
			cl.addArgument("--filter");
			cl.addArgument("analyzer "+fpt.getAnalyzerFilter());
		}
		if (fpt.getChargeStateFilter() != null) {
			cl.addArgument("--filter");
			cl.addArgument("chargeState "+asIntSet(fpt.getChargeStateFilter().getAcceptCharge()));
		}
		if (fpt.getDeisotopeFilter() != null) {
			cl.addArgument("--filter");
			DeisotopeFilteringType dft = fpt.getDeisotopeFilter();
			cl.addArgument("MS2Deisotope "+dft.isHires()+" "+dft.getMzTolerance());
		}
		if (fpt.getZeroesFilter() != null) {
			cl.addArgument("--filter");
			ZeroesFilterType zft = fpt.getZeroesFilter();
			cl.addArgument("zeroSamples "+zft.getMode()+" "+asIntSet(zft.getApplyToMsLevel()));
		}
		if (fpt.getEtdFilter() != null) {
			cl.addArgument("--filter");
			EtdFilteringType f = fpt.getEtdFilter();
			cl.addArgument("ETDFilter "+f.isRemovePrecursor()+" "+f.isRemoveChargeReduced()+" "+f.isRemoveNeutralLoss());
		}
		if (fpt.getIntensityFilter() != null) {
			cl.addArgument("--filter");
			ThresholdParametersType f = fpt.getIntensityFilter();
			cl.addArgument("threshold "+addThresholdAsString(f));
		}
		if (fpt.getMs2Denoise() != null) {
			cl.addArgument("--filter");
			MS2DenoiseType dn = fpt.getMs2Denoise();
			Integer peaks_per_window = dn.getPeaksInWindow();
			if (peaks_per_window == null) {
				peaks_per_window = 6;
			}
			Double window_width = dn.getWindowWidth();
			if (window_width == null) {
				window_width = 30.0d;
			}
			Boolean relax = dn.isMultichargeFragmentRelaxation();
			if (relax == null) {
				relax = Boolean.TRUE;
			}
			cl.addArgument("MS2Denoise "+String.valueOf(peaks_per_window)+" "
							+String.valueOf(window_width)+" "+String.valueOf(relax));
		}
		if (fpt.getMsLevelFilter() != null) {
			cl.addArgument("--filter");
			String levels = asIntSet(fpt.getMsLevelFilter().getMsLevel());
			if (levels == null || levels.length() < 1) {
				throw new IOException("No MS levels specified!");
			}
			cl.addArgument("msLevel "+levels);
		}
		if (fpt.getMzPrecursorFilter() != null) {
			cl.addArgument("--filter");
			MzPrecursorFilterType f = fpt.getMzPrecursorFilter();
			cl.addArgument("mzPrecursors "+asString(f.getAcceptMZ(), null));
		}
		if (fpt.getMzWindowFilter() != null) {
			cl.addArgument("--filter");
			WindowFilterType wft = fpt.getMzWindowFilter();
			cl.addArgument("mzWindow ["+wft.getLow()+","+wft.getHigh()+"]");
		}
		if (fpt.getPolarityFilter() != null) {
			cl.addArgument("--filter");
			String pol = fpt.getPolarityFilter();
			cl.addArgument("polarity "+pol);
		}
		if (fpt.getScanFilter() != null) {
			ScanFilterType sft = fpt.getScanFilter();
			addScanFilterArguments(sft, cl);
		}
	}
	
	private void addScanFilterArguments(ScanFilterType sft, final CommandLine cl) throws IOException {
		if (sft == null) {
			throw new IOException("No scan filter!");
		}
		if (sft.getEventFilter() != null) {
			cl.addArgument("--filter");
			cl.addArgument("scanEvent "+asIntSet(sft.getEventFilter().getAcceptEvent()));
		} 
		if (sft.getNumberFilter() != null) {
			cl.addArgument("--filter");
			cl.addArgument("scanNumber "+asIntSet(sft.getNumberFilter().getAcceptScan()));
		} else if (sft.getTimeFilter() != null) {
			cl.addArgument("--filter");
			cl.addArgument("scanTime ["+sft.getTimeFilter().getLower()+","+sft.getTimeFilter().getUpper()+"]");
		}
	}

	private String addThresholdAsString(final ThresholdParametersType f) throws IOException {
		if (f == null) {
			throw new IOException("No threshold!");
		}
		String meth = "count";
		String threshold = "0.0";
		String orientation = "most";
		String mslevels = "";
		if (f.getAbsoluteThreshold() != null) {
			meth = "absolute";
			AbsoluteThresholdType att = f.getAbsoluteThreshold();
			orientation = grokOrientation(att.getWhat());
			threshold = String.valueOf(att.getIntensityThreshold());
			mslevels = asIntSet(att.getMsLevels(), true);
		} else if (f.getBasePeakRelativeThreshold() != null) {
			meth = "bpi-relative";
			RelativeThresholdType rt = f.getBasePeakRelativeThreshold();
			orientation = grokOrientation(rt.getWhat());
			threshold = String.valueOf(rt.getFraction());
			mslevels = asIntSet(rt.getMsLevels(), true);
		} else if (f.getKeepNThreshold() != null) {
			meth = "count";
			KeepNThresholdType kn = f.getKeepNThreshold();
			orientation = grokOrientation(kn.getWhat());
			threshold = String.valueOf(kn.getN());
			mslevels = asIntSet(kn.getMsLevels(), true);
		} else if (f.getKeepNThresholdIncludingTies() != null) {
			meth = "count-after-ties";
			KeepNThresholdType kn = f.getKeepNThresholdIncludingTies();
			orientation = grokOrientation(kn.getWhat());
			threshold = String.valueOf(kn.getN());
			mslevels = asIntSet(kn.getMsLevels(), true);
		} else if (f.getTICRelativeThreshold() != null) {
			meth = "tic-relative";
			RelativeThresholdType rt = f.getTICRelativeThreshold();
			orientation = grokOrientation(rt.getWhat());
			threshold = String.valueOf(rt.getFraction());
			mslevels = asIntSet(rt.getMsLevels(), true);
		} else if (f.getTICAbsoluteThreshold() != null) {
			meth = "tic-cutoff";
			AbsoluteThresholdType t = f.getTICAbsoluteThreshold();
			orientation = grokOrientation(t.getWhat());
			threshold = String.valueOf(t.getIntensityThreshold());
			mslevels = asIntSet(t.getMsLevels(), true);
		} else {
			throw new IOException("Unknown threshold: "+f.toString());
		}
		
		String ret = meth+" "+threshold+" "+orientation+" "+mslevels;
		// if mslevels is not specified we might left with a trailing space. So we remove that prior to returning to caller
		return ret.trim();
	}

	/**
	 * Returns "most-intense" or "least-intense" depending on the input object. Throws if invalid input is encountered.
	 * 
	 * @param what
	 * @return
	 */
	private String grokOrientation(final MostOrLeast what) throws IOException {
		if (what == null) {
			throw new IOException("No orientation of threshold specified!");
		}
		return (what.name().toLowerCase().indexOf("most") >= 0) ? "most-intense" : "least-intense";
	}

	private String asIntSet(List<Integer> vec) {
		StringBuilder sb = new StringBuilder();
		for (Integer i : vec) {
			sb.append(String.valueOf(i));
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * Concatenates the strings in l, separated by sep (except at first/last item)
	 * @param l
	 * @param sep null is assumed to be single-whitespace separated
	 * @return
	 */
	private String asString(final List<String> l, String sep) {
		if (sep == null) {
			sep = " ";
		}
		StringBuilder sb = new StringBuilder();
		int n = l.size();
		for (int i=0; i<n; i++) {
			sb.append(l.get(i));
			if (i<n-1) {
			   sb.append(sep);
			}
		}
		return sb.toString();
	}

	private String asIntSet(final MsLevelType msLevels, boolean accept_empty) throws IOException {
		if (msLevels == null || msLevels.getMsLevel().size() < 1) {
			if (!accept_empty) {
				throw new IOException("No ms-levels specified!");
			} else {
				return "";
			}
		}
		return asIntSet(msLevels.getMsLevel());
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

	public MSConvertCommandLineBuilder setOutputFolder(File out_folder) {
		this.output_folder = out_folder;
		return this;
	}
}
