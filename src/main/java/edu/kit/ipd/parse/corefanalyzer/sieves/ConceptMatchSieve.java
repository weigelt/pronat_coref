/**
 *
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public class ConceptMatchSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public ConceptMatchSieve() {
		super(Modus.HARD);
	}

	public ConceptMatchSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "conceptMatch";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		if (current instanceof SubjectEntity) {
			if (!((SubjectEntity) current).isSystem()) {
				return result;
			}
		}
		if (current.hasAssociatedConcept()) {
			EntityConcept currentConcept = ContextUtils.getMostLikelyEntityConcept(current.getAssociatedConcepts());
			for (Entity entity : candidates) {
				if (entity.hasAssociatedConcept()) {
					EntityConcept candidateConcept = ContextUtils.getMostLikelyEntityConcept(entity.getAssociatedConcepts());
					if (!currentConcept.equals(candidateConcept)) {
						if (!(currentConcept.getEqualConcepts().contains(candidateConcept)
								|| candidateConcept.getEqualConcepts().contains(currentConcept))) {
							if (!(currentConcept.getSynonyms().contains(candidateConcept.getName())
									|| candidateConcept.getSynonyms().contains(currentConcept.getName()))) {

								if (!isSubsumed(currentConcept, candidateConcept)) {
									result.remove(entity);
								}
							}

						}
					}
				}
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

	private boolean isSubsumed(EntityConcept current, EntityConcept candidate) {
		if (current.getSubConcepts().isEmpty()) {
			return false;
		}
		boolean result = false;
		for (AbstractConcept subConcept : current.getSubConcepts()) {

			if (subConcept instanceof EntityConcept) {
				if (subConcept.equals(candidate)) {
					return true;
				} else {
					result = result || isSubsumed((EntityConcept) subConcept, candidate);
				}
			}
		}
		return result;
	}

}
