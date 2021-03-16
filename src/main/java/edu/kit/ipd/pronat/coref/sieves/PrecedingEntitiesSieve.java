/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class PrecedingEntitiesSieve extends Sieve {

	public PrecedingEntitiesSieve() {
		super(Modus.HARD);
	}

	public PrecedingEntitiesSieve(Modus modus) {
		super(modus);
	}

	static final String ID = "precedingEntities";

	/*
	 * (non-Javadoc)
	 * 
	 * @see ISieve#passSieve(edu.kit.ipd.parse
	 * .contextanalyzer.data.entities.Entity, java.util.List)
	 */
	public List<Entity> passSieve(Entity current, List<Entity> candidates) {
		Collections.sort(candidates);
		int currentPosition = candidates.indexOf(current);
		List<Entity> result = new ArrayList<>(candidates.subList(0, currentPosition));
		Collections.reverse(result);
		return result;

	}

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		Collections.sort(candidates);
		int currentPosition = 0;
		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.get(i).getCandidate().getReference().get(0).equals(current.getReference().get(0))) {
				currentPosition = i;
				break;
			}

		}
		List<ReferentCandidate> result;
		if (modus == Modus.HARD) {
			result = new ArrayList<>(candidates.subList(0, currentPosition));

		} else {
			result = deepCopyCandidateList(candidates);
			for (ReferentCandidate candidate : result.subList(currentPosition, candidates.size())) {
				candidate.setConfidence(0.0);
			}
			result = candidates;
		}
		Collections.reverse(result);
		return result;
	}

}
