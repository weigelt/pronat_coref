package edu.kit.ipd.pronat.coref.sieves;

import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;
import edu.kit.ipd.pronat.context.util.ContextUtils;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class ReflexiveSieve extends Sieve {

	static final String ID = "reflexive";
	private static final double weight = 0.0;

	public ReflexiveSieve() {
		super(Modus.HARD);
	}

	public ReflexiveSieve(Modus modus) {
		super(modus);
	}

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		if (isReflexiv(current)) {
			for (ReferentCandidate candidate : candidates) {
				if (!ContextUtils.belongToSameAction(candidate.getCandidate(), current)) {
					if (ContextUtils.getInstructionNumber(current) != ContextUtils.getInstructionNumber(candidate.getCandidate())) {
						if (modus.equals(Modus.HARD)) {
							result.remove(candidate);
						} else {
							result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
						}
					}
				}
			}
		}
		return result;
	}

	public static boolean isReflexiv(Entity entity) {
		if (entity instanceof PronounEntity) {
			if (entity.getName().endsWith("self") || entity.getName().endsWith("selves")) {
				return true;
			}
		}
		return false;
	}

}
