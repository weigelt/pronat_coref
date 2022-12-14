/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;
import edu.kit.ipd.pronat.context.data.relations.LocativeRelation;
import edu.kit.ipd.pronat.context.data.relations.Relation;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class LocativeSieve extends Sieve {

	static final String ID = "locativeSieve";

	private static final double weight = 0.25;

	public LocativeSieve() {
		super(Modus.HARD);
	}

	/**
	 * @param modus
	 */
	public LocativeSieve(Modus modus) {
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
		for (ReferentCandidate candidate : candidates) {
			Entity candidateEntity = candidate.getCandidate();
			if (!(candidateEntity instanceof PronounEntity) && candidateEntity.hasRelationsOfType(LocativeRelation.class)) {
				List<Relation> relList = candidateEntity.getRelationsOfType(LocativeRelation.class);
				boolean isLocative = false;
				for (Relation relation : relList) {
					if (((LocativeRelation) relation).getEnd().equals(candidateEntity)) {
						isLocative = true;
						break;
					}
				}
				if (isLocative) {
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
				}
			}
		}
		return result;
	}

}
