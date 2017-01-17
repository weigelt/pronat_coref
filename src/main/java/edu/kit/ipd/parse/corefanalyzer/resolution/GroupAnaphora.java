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
import edu.kit.ipd.parse.contextanalyzer.data.relations.ConjunctionRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.Sieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.SieveFactory;

/**
 * @author Tobias Hey
 *
 */
public class GroupAnaphora implements IAnaphoraSolver {

	private List<ISieve> sieves;
	private static final Logger logger = LoggerFactory.getLogger(GroupAnaphora.class);

	public static final Set<String> groupPronouns = new HashSet<>(Arrays.asList(new String[] { "they", "them", "themself", "themselves",
			"theirs", "their", "'em", "they're", "they've", "they'd", "they'll" }));

	public GroupAnaphora(Properties props) {
		this.sieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("GROUP_SIEVES").trim().split(",");

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
		//TODO determiner check (both, all, these x)

		List<ReferentCandidate> possibleCandidates = new ArrayList<>(inputCandidates);
		List<ReferentCandidate> previousStep;
		for (ReferentCandidate candidate : inputCandidates) {
			if (candidate.getCandidate().hasRelationsOfType(ConjunctionRelation.class)) {
				List<Relation> relations = candidate.getCandidate().getRelationsOfType(ConjunctionRelation.class);
				for (Relation relation : relations) {
					ConjunctionRelation rel = (ConjunctionRelation) relation;
					if (relation.getName().equalsIgnoreCase("and") && rel.getStart().equals(candidate.getCandidate())) {
						ReferentCandidate prev = null;
						for (ReferentCandidate refCand : possibleCandidates) {
							if (refCand.getReferents().contains(rel.getEnd())) {
								candidate.getReferents().addAll(refCand.getReferents());
								prev = refCand;
								break;

							}
						}
						if (prev != null) {
							possibleCandidates.remove(prev);
						}
						break;
					}
				}
			}
		}
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
