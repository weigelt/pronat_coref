/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.relations.ReferentRelation;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class AlreadyRelatedSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public AlreadyRelatedSieve() {
		super(Modus.HARD);
	}

	public AlreadyRelatedSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "alreadyRelated";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> alreadyRelated = new ArrayList<>();
		for (Entity candidate : candidates) {
			if (candidate.hasRelationsOfType(ReferentRelation.class)) {
				List<Relation> refs = candidate.getRelationsOfType(ReferentRelation.class);
				for (Relation relation : refs) {
					if (!((ReferentRelation) relation).getEnd().equals(candidate)
							&& candidates.contains(((ReferentRelation) relation).getEnd())
							&& candidates.indexOf(((ReferentRelation) relation).getEnd()) > candidates.indexOf(candidate)) {
						alreadyRelated.add(((ReferentRelation) relation).getEnd());
					}
				}
			}

		}
		List<Entity> result = new ArrayList<>(candidates);
		result.removeAll(alreadyRelated);
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
