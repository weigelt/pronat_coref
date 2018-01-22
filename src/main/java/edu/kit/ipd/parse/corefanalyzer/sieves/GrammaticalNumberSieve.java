/**
 *
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.resolution.PronounType;
import edu.kit.ipd.parse.corefanalyzer.util.MatchingUtils;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public class GrammaticalNumberSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public GrammaticalNumberSieve() {
		super(Modus.HARD);
	}

	public GrammaticalNumberSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "grammaticalNumber";

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		for (ReferentCandidate candidate : candidates) {
			if (candidate.getReferents().size() == 1) {
				Entity entity = candidate.getCandidate();

				if (!(current instanceof ObjectEntity) || !containsAllQuantifier(current.getReference())) {
					if (!MatchingUtils.grammaticalNumberMatches(current.getGrammaticalNumber(), entity.getGrammaticalNumber())) {
						removeCandidate(result, candidate);
					}
				}

			} else {
				if (current instanceof PronounEntity) {
					if (!PronounType.getType(current.getName()).equals(PronounType.GROUP)) {
						removeCandidate(result, candidate);
					}

				}
			}
		}
		return result;
	}

	private boolean containsAllQuantifier(List<INode> reference) {
		for (INode iNode : reference) {
			if (iNode.getAttributeValue("value").equals("all") || iNode.getAttributeValue("value").equals("every")
					|| iNode.getAttributeValue("value").equals("each")) {
				return true;
			}
		}
		return false;
	}

	private void removeCandidate(List<ReferentCandidate> result, ReferentCandidate candidate) {
		if (modus == Modus.HARD) {
			result.remove(candidate);
		} else {
			result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
		}
	}
}
