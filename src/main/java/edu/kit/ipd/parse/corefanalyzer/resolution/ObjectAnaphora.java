/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.Sieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.SieveFactory;

/**
 * @author Tobias Hey
 *
 */
public class ObjectAnaphora implements IAnaphoraSolver {

	private List<ISieve> sieves;
	private static final Logger logger = LoggerFactory.getLogger(ObjectAnaphora.class);

	public static final Set<String> objectPronouns = new HashSet<>(
			Arrays.asList(new String[] { "it", "its", "itself", "it's", "it'd", "it'll" }));

	public ObjectAnaphora(Properties props) {
		sieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("OBJECT_SIEVES").trim().split(",");

		for (String id : sieveArray) {
			id = id.trim();
			ISieve sieve = SieveFactory.createSieve(id);
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
	 * @see
	 * edu.kit.ipd.parse.corefanalyzer.IAnaphoraSolver#searchCandidates(edu.kit.
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
