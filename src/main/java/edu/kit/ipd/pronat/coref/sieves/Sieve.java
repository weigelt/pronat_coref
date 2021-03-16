/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.SpeakerEntity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public abstract class Sieve implements ISieve {

	protected Modus modus;

	public Sieve(Modus modus) {
		this.modus = modus;
	}

	protected List<ReferentCandidate> deepCopyCandidateList(List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = new ArrayList<>();
		for (ReferentCandidate candidate : candidates) {
			result.add(candidate.clone());
		}
		return result;
	}

	protected List<Entity> extractEntityListFromCandidates(List<ReferentCandidate> candidates) {
		List<Entity> result = new ArrayList<>();
		for (ReferentCandidate candidate : candidates) {
			result.add(candidate.getCandidate());
		}
		return result;
	}

	public static int getNumberOfCandidates(List<ReferentCandidate> candidates) {
		int number = 0;
		for (ReferentCandidate candidate : candidates) {
			if (candidate.getConfidence() > 0.0 && !(candidate.getCandidate() instanceof SpeakerEntity)) {
				number++;
			}
		}
		return number;
	}

}
