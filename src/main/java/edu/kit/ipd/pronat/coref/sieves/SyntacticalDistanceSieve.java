/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.SpeakerEntity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class SyntacticalDistanceSieve extends Sieve {

	static final String ID = "syntacticalDistance";

	public SyntacticalDistanceSieve() {
		super(Modus.HARD);
	}

	public SyntacticalDistanceSieve(Modus modus) {
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
		int numberOfCandidates = getNumberOfCandidates(candidates);
		double highestConfidence = getHighestConfidence(candidates);
		int highestConfidenceCandidates = numberOfCandidatesWithConfidence(candidates, highestConfidence);
		// only sieve if multiple Candidates share the highest confidence
		if (highestConfidenceCandidates > 1) {
			int count = 0;
			for (ReferentCandidate candidate : candidates) {

				if (candidate.getConfidence() > 0.0 && !(candidate.getCandidate() instanceof SpeakerEntity)
						&& Math.abs(candidate.getConfidence() - highestConfidence) < 0.00000001d) {
					count++;
					double confidence = 0.0;
					if (numberOfCandidates == 1) {
						confidence = 1.0;
					} else {
						confidence = candidate.getConfidence() * (1.0 - ((1.0 / (numberOfCandidates + 1)) * count));
					}
					if (confidence < 0.0) {
						confidence = 0.0;
					}
					result.get(result.indexOf(candidate)).setConfidence(confidence);
				} else {
					// the candidates which dont belong to highest confidence bucket get maximum reduced confidence
					double confidence = candidate.getConfidence()
							* (1.0 - ((1.0 / (numberOfCandidates + 1)) * highestConfidenceCandidates));
					result.get(result.indexOf(candidate)).setConfidence(confidence);
				}
			}
		}
		return result;
	}

	private double getHighestConfidence(List<ReferentCandidate> candidates) {
		double result = 0.0;
		for (ReferentCandidate candidate : candidates) {
			if (candidate.getConfidence() > result) {
				result = candidate.getConfidence();
			}
		}
		return result;
	}

	private int numberOfCandidatesWithConfidence(List<ReferentCandidate> candidates, double confidence) {
		int result = 0;
		for (ReferentCandidate candidate : candidates) {
			if (Math.abs(confidence - candidate.getConfidence()) < 0.00000001d) {
				result++;
			}
		}
		return result;
	}

}
