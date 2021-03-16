/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.EntityConcept;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.ObjectEntity;
import edu.kit.ipd.pronat.context.util.ContextUtils;
import edu.kit.ipd.pronat.coref.data.PossessivePronoun;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class PossessiveMeronymSieve extends Sieve {

	static final String ID = "possessiveMeronym";
	private static final double weight = 0.75;

	public PossessiveMeronymSieve() {
		super(Modus.HARD);
	}

	public PossessiveMeronymSieve(Modus modus) {
		super(modus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISieve#sieve(edu.kit.ipd.parse. contextanalyzer.data.entities.Entity,
	 * java.util.List)
	 */
	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		List<ReferentCandidate> matches = new ArrayList<ReferentCandidate>();
		if (current instanceof PossessivePronoun) {
			PossessivePronoun possessive = (PossessivePronoun) current;
			for (ReferentCandidate candidate : candidates) {
				if (possessive.getDescribedEntity().hasAssociatedConcept() && candidate.getCandidate().hasAssociatedConcept()) {
					EntityConcept currentConcept = ContextUtils
							.getMostLikelyEntityConcept(possessive.getDescribedEntity().getAssociatedConcepts());
					if (currentConcept != null) {
						EntityConcept candidateConcept = ContextUtils
								.getMostLikelyEntityConcept(candidate.getCandidate().getAssociatedConcepts());
						if (candidateConcept != null) {
							if (candidateConcept.hasPartConcepts()) {
								if (candidateConcept.getPartConcepts().contains(currentConcept)) {
									matches.add(candidate);
								}
							}
						}

					}
				} else {
					if (candidate.getCandidate() instanceof ObjectEntity) {
						ObjectEntity obj = (ObjectEntity) candidate.getCandidate();
						if (obj.getMeronyms().contains(possessive.getDescribedEntity().getName())) {
							matches.add(candidate);
						}
					}
				}
			}
		}
		if (!matches.isEmpty()) {
			for (ReferentCandidate candidate : candidates) {
				if (!matches.contains(candidate)) {
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
				}
			}
		}
		return result;
	}

}
