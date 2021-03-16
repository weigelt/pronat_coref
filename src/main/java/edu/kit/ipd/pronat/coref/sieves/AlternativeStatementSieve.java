/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.pronat.coref.util.MatchingUtils;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class AlternativeStatementSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public AlternativeStatementSieve() {
		super(Modus.HARD);
	}

	public AlternativeStatementSieve(Modus modus) {
		super(modus);
	}

	static final String ID = "alternativeStatement";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		for (Entity entity : candidates) {
			if (MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
				result.remove(entity);
			}
		}
		return result;
	}

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		List<Entity> sieveResult = passSieve(current, extractEntityListFromCandidates(result));
		for (ReferentCandidate candidate : candidates) {
			if (!sieveResult.contains(candidate.getCandidate())) {
				if (modus == Modus.HARD) {
					result.remove(candidate);
				} else {
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
				}
			}
		}
		return result;
	}

}
