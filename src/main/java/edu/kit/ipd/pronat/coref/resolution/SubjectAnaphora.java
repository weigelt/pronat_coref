/**
 * 
 */
package edu.kit.ipd.pronat.coref.resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;
import edu.kit.ipd.pronat.context.util.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.pronat.coref.sieves.ISieve;
import edu.kit.ipd.pronat.coref.sieves.ISieve.Modus;
import edu.kit.ipd.pronat.coref.sieves.Sieve;
import edu.kit.ipd.pronat.coref.sieves.SieveFactory;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class SubjectAnaphora implements IAnaphoraSolver {

	private List<ISieve> sieves;
	private static final Logger logger = LoggerFactory.getLogger(SubjectAnaphora.class);

	public static final Set<String> malePronouns = new HashSet<String>(
			Arrays.asList(new String[] { "he", "him", "himself", "his", "he's", "he's", "he'd", "he'll" }));
	public static final Set<String> femalePronouns = new HashSet<String>(
			Arrays.asList(new String[] { "her", "hers", "herself", "she", "she's", "she's", "she'd", "she'll" }));
	public static final Set<String> secondPersonPronouns = new HashSet<String>(
			Arrays.asList(new String[] { "you", "yourself", "yours", "your", "yourselves", "you're", "you've", "you'll", "you'd" }));

	public SubjectAnaphora(Properties props) {
		sieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("SUBJECT_SIEVES").trim().split(",");

		Modus modus = Modus.valueOf(props.getProperty("SIEVE_MODUS", Modus.HARD.toString()));
		for (String id : sieveArray) {
			id = id.trim();
			ISieve sieve;
			if (modus != null) {
				sieve = SieveFactory.createSieve(id, modus);
			} else {
				sieve = SieveFactory.createSieve(id);
			}
			if (sieve != null) {
				sieves.add(sieve);
			} else {
				logger.error("Specified Sieve is not registered: " + id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.ipd.parse.coref.IAnaphoraSolver#searchCandidates(edu.kit.
	 * ipd.parse.contextanalyzer.data.entities.PronounEntity, java.util.List,
	 * edu.kit.ipd.parse.contextanalyzer.data.Context)
	 */
	@Override
	public List<ReferentCandidate> searchCandidates(PronounEntity current, List<ReferentCandidate> inputCandidates, Context context) {

		List<ReferentCandidate> possibleCandidates = new ArrayList<>(inputCandidates);
		List<ReferentCandidate> previousStep;
		for (ISieve sieve : sieves) {
			previousStep = possibleCandidates;
			if (!possibleCandidates.isEmpty()) {
				possibleCandidates = sieve.sieve(current, possibleCandidates);
				if (Sieve.getNumberOfCandidates(possibleCandidates) == 0) {
					possibleCandidates = previousStep;
				}
			}
		}
		logger.debug(current.getName() + "[" + ContextUtils.getPositionInUtterance(current) + "]" + ":" + possibleCandidates.toString());
		return possibleCandidates;
	}

}
