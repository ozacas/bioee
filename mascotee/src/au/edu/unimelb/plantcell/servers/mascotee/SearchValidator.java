package au.edu.unimelb.plantcell.servers.mascotee;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Constraints;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Data;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Identification;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.KeyParameters;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MSMSTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.MsMsIonSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PMFSearch;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.PeptideTolerance;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Quantitation;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Reporting;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.Search;
import au.edu.unimelb.plantcell.servers.jaxb.mascotee.SeqQuerySearch;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotConfig;
import au.edu.unimelb.plantcell.servers.mascot.core.v2.MascotDatabase;

/**
 * Responsible for implementing all checks on search data and throwing an exception if something is
 * wrong. This method is exposed via the web service so that client code can validate user input
 * without needing the logic on the client side.
 * 
 * @author acassin
 *
 */
public class SearchValidator {
	private final MascotConfig config;
	private final Search s;
	
	public SearchValidator(final Search s, final MascotConfig config) {
		this.s = s;
		this.config = config;
	}
	
	public void validate() throws SOAPException {
		if (s == null)
			throw new SOAPException("No document!");
		int n_roots = 0;
		if (s.getMsMsIonSearch() != null) {
			n_roots++;
			validateMsMs(s.getMsMsIonSearch());
		}
		if (s.getPMFSearch() != null) {
			n_roots++;
			validatePMF(s.getPMFSearch());
		}
		if (s.getSequenceQuerySearch() != null) {
			n_roots++;
			validateSequenceQuery(s.getSequenceQuerySearch());
		}
		if (n_roots != 1) {
			throw new SOAPException("Only one of MS/MS, PMF or SequenceQuery search is permitted!");
		}
	}

	private void validateSequenceQuery(final SeqQuerySearch sequenceQuerySearch) throws SOAPException {
		// TODO FIXME
	}

	private void validatePMF(final PMFSearch pmfSearch) throws SOAPException {
		// TODO FIXME
	}

	private void validateMsMs(MsMsIonSearch msms) throws SOAPException {
		assert(msms != null);
		validateConstraints(msms.getConstraints());
		validateData(msms.getData());
		validateIdentification(msms.getIdentification());
		validateQuantitation(msms.getQuant());
		validateKeyParameters(msms.getParameters());
		validateReporting(msms.getReporting());
	}

	private void validateIdentification(final Identification id) throws SOAPException {
		if (id == null)
			throw new SOAPException("Missing identification section");
		String user = id.getUsername();
		Pattern p = Pattern.compile("^\\w*$");
		Matcher m = p.matcher(user);
		if (!m.matches())
			throw new SOAPException("Invalid username: must be letters, digits or underscore only");
		String title = id.getTitle();
		String email = id.getEmail();
		if (title == null || email == null) {
			throw new SOAPException("Email and title must be present, but can be empty");
		}
	}

	private void validateQuantitation(final Quantitation quant) throws SOAPException {
		if (quant == null) 
			throw new SOAPException("Missing quantitation section");
		// JAXB will validate icat for us so... nothing to do here...
	}

	@SuppressWarnings("unused")
	private void validateData(final Data data) throws SOAPException {
		if (data == null) {
			throw new SOAPException("Missing data section!");
		}
		String format = data.getFormat();
		if (format == null || !format.equals("Mascot generic")) {
			throw new SOAPException("Only mascot generic (MGF) format is supported at this time!");
		}
		String instr = data.getInstrument();
		if (instr == null || !config.hasInstrument(instr)) {
			throw new SOAPException("Current mascot configuration has no such instrument: "+instr);
		}
		
		// usually only used for non-mgf formats, so we are lenient here as to what we accept
		String pc = data.getPrecursor();
		if (pc != null && pc.trim().length() > 0) {
			try {
				Double d = Double.valueOf(pc.trim());
			} catch (NumberFormatException nfe) {
				throw new SOAPException("Invalid precursor: "+pc);
			}
		}
		
		String fname = data.getSuggestedFileName();
		if (fname == null)
			throw new SOAPException("Missing suggested filename for data!");
		// dont worry about data.getFile()...
	}

	private void validateConstraints(Constraints constraints) throws SOAPException {
		if (constraints == null) {
			throw new SOAPException("Missing constraints!");
		}
		String enzyme = constraints.getEnzyme();
		if (enzyme == null || enzyme.trim().length() < 1) {
			throw new SOAPException("Missing enzyme!");
		}
		if (!config.hasEnzyme(enzyme)) {
			throw new SOAPException("No such enzyme: "+enzyme+" in current mascot configuration!");
		}
		String taxa = constraints.getAllowedTaxa();
		if (taxa == null || taxa.trim().length() < 1) {
			throw new SOAPException("No taxonomy specified!");
		}
		if (!config.hasSpeciesTaxonomy(taxa)) {
			throw new SOAPException("Taxonomy "+taxa+" is not present in current mascot configuration!");
		}
		validatedAllowedProteinMass(constraints.getAllowedProteinMass());
		int x = constraints.getAllowXMissedCleavages();
		if (x < 0 || x > 9) {
			throw new SOAPException("Allowed missed cleavages must be in the range [0..9]");
		}
		String      pc = constraints.getPeptideCharge();
		Set<String> ok = new HashSet<String>();
		for (String s : new String[] { "1+", "2+", "3+", "4+", "5+", "6+", "7+", "8+", "Mr", "1+, 2+ and 3+", "2+ and 3+" }) {
			ok.add(s);
		}
		if (!ok.contains(pc)) {
			throw new SOAPException("Unknown peptide charge: "+pc);
		}
		validateMsMsTolerance(constraints.getMsmsTolerance());
		validatePeptideTolerance(constraints.getPeptideTolerance());
	}

	private void validatePeptideTolerance(PeptideTolerance tol) throws SOAPException {
		if (tol == null)
			throw new SOAPException("No peptide tolerance!");
		validateTolerance(tol.getValue(), tol.getUnit(), new String[] { "Da", "mmu", "%", "ppm" });
	}

	private void validateMsMsTolerance(MSMSTolerance tol) throws SOAPException {
		if (tol == null)
			throw new SOAPException("No MS/MS tolerance!");
		validateTolerance(tol.getValue(), tol.getUnit(), new String[] { "Da", "mmu"});
	}

	private void validateTolerance(final String value, final String unit, final String[] ok_units) throws SOAPException {
		Set<String> ok = new HashSet<String>();
		for (String s : ok_units) {
			ok.add(s);
		}
		if (unit == null || !ok.contains(unit)) {
			throw new SOAPException("Invalid unit: "+unit);
		}
		if (value == null || value.trim().length() < 1) {
			throw new SOAPException("Invalid missing tolerance value: "+value);
		}
		try {
			@SuppressWarnings("unused")
			Double d = Double.valueOf(value);
		} catch (NumberFormatException nfe) {
			throw new SOAPException("Invalid tolerance value: "+value);
		}
	}

	private void validatedAllowedProteinMass(String allowedProteinMass) throws SOAPException {
		// TODO FIXME
	}

	private void validateKeyParameters(final KeyParameters p) throws SOAPException {
		if (p == null) {
			throw new SOAPException("No key parameters specified for search!");
		}
		MascotDatabase db = config.findDatabase(p.getDatabase());
		if (db == null) {
			throw new SOAPException("No such mascot sequence database: "+p.getDatabase());
		}
		validateModifications(p.getFixedMod(), p.getVariableMod());
		String mt = p.getMassType();
		if (mt == null || !(mt.equals("Monoisotopic") || mt.equals("Average"))) {
			throw new SOAPException("Mass type must be either monoisotopic or average!");
		}
	}

	private void validateModifications(final List<String> fixedMod, final List<String> variableMod) throws SOAPException {
		if (fixedMod == null || variableMod == null) {
			throw new SOAPException("Missing modifications sections: must be present (even if empty)");
		}
		
		Set<String> seen = new HashSet<String>(fixedMod.size());
		seen.addAll(fixedMod);
		for (String s : variableMod) {
			if (seen.contains(s)) {
				throw new SOAPException("Cannot have a modification as fixed and variable: "+s);
			}
		}
		
		if (fixedMod.size() + variableMod.size() > 50) {
			throw new SOAPException("Ridiculous number of modifications specified - results will be meaningless!");
		}
	}

	private void validateReporting(final Reporting reporting) throws SOAPException {
		if (reporting == null) {
			throw new SOAPException("No report settings supplied!");
		}
		String top = reporting.getTop();
		if (top.trim().length() < 1) {
			throw new SOAPException("No top hits reporting specified!");
		}
		if (!top.equals("AUTO")) {
			try {
				Integer i = Integer.valueOf(top.trim());
				if (i<1 || i>200) {
					throw new SOAPException("Top hits must be between [1..200] inclusive");
				}
			} catch (NumberFormatException nfe) {
				throw new SOAPException("Top hits must be a number! (or AUTO)");
			}
		}
		// JAXB will have already thrown if the overview parameter was wrong
	}
}
