/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public class InstructionNumberSieve extends Sieve {

	public InstructionNumberSieve() {
		super(Modus.HARD);
	}

	public InstructionNumberSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "instructionNumber";

	private static final int identityMax = 18;
	private static final int anaphoraMax = 6;
	private static final double weight = 0.0;

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		int currentInstr = (int) current.getReference().get(0).getAttributeValue("instructionNumber");
		int max = identityMax;
		if (current instanceof PronounEntity) {
			max = anaphoraMax;
		}
		List<Entity> result = new ArrayList<>(candidates);
		for (Entity entity : candidates) {
			int instructionNumber = (int) entity.getReference().get(0).getAttributeValue("instructionNumber");
			if (instructionNumber <= currentInstr - max) {
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
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
				}
			}
		}
		return result;
	}

}
