/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.ObjectEntity;
import edu.kit.ipd.pronat.context.data.entities.ObjectEntity.DeterminerType;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class GeneralDeterminerSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public GeneralDeterminerSieve() {
		super(Modus.HARD);
	}

	public GeneralDeterminerSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "generalDet";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		if (current instanceof ObjectEntity) {
			if (((ObjectEntity) current).getDeterminer().equals(DeterminerType.GENERAL)) {
				result = new ArrayList<>();
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
