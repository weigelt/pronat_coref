/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.util.MatchingUtils;

/**
 * @author Tobias Hey
 *
 */
public class ObjectMatchSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public ObjectMatchSieve() {
		super(Modus.HARD);
	}

	public ObjectMatchSieve(Modus modus) {
		super(modus);
	}

	static final String ID = "objectMatch";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		for (Entity entity : candidates) {
			if (entity instanceof ObjectEntity && current instanceof ObjectEntity) {
				if (!matches((ObjectEntity) current, (ObjectEntity) entity)) {
					result.remove(entity);
				}
			}
		}
		if (result.isEmpty()) {
			result = new ArrayList<>(candidates);
			for (Entity entity : candidates) {
				if (entity instanceof ObjectEntity && current instanceof ObjectEntity) {
					if (!matchesHypernym((ObjectEntity) current, (ObjectEntity) entity)) {
						result.remove(entity);
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

	private boolean matchesHypernym(ObjectEntity current, ObjectEntity candidate) {
		boolean isMatch = false;
		for (String hypernym : candidate.getDirectHypernyms()) {
			if (MatchingUtils.nameMatches(hypernym, current.getName())) {
				isMatch = true;
			}

		}
		if (!isMatch) {
			return false;
		}
		if (!MatchingUtils.quantityMatches(current.getQuantity(), candidate.getQuantity())) {
			return false;
		} else if (!current.getDescribingAdjectives().isEmpty()) {
			if (!candidate.getDescribingAdjectives().containsAll(current.getDescribingAdjectives())) {
				return false;
			}
		}
		return true;
	}

	private boolean matches(ObjectEntity current, ObjectEntity candidate) {
		if (!current.hasAssociatedConcept() || !candidate.hasAssociatedConcept()) {
			if (!current.getName().toLowerCase().equals(candidate.getName().toLowerCase())) {
				boolean isMatch = false;
				for (String synonym : candidate.getSynonyms()) {
					if (MatchingUtils.nameMatches(synonym, current.getName())) {
						isMatch = true;
					}
				}
				if (!isMatch) {
					return false;
				}
			}
		}
		if (!MatchingUtils.quantityMatches(current.getQuantity(), candidate.getQuantity())) {
			return false;
		} else if (!current.getDescribingAdjectives().isEmpty()) {
			if (!hasMatchingReferent(current, candidate)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasMatchingReferent(ObjectEntity current, ObjectEntity candidate) {
		if (!candidate.getDescribingAdjectives().containsAll(current.getDescribingAdjectives())) {
			if (candidate.hasRelationsOfType(ReferentRelation.class)) {
				ReferentRelation mostLikelyReferent = ContextUtils
						.getMostLikelyReferentRelation(candidate.getRelationsOfType(ReferentRelation.class), candidate);
				if (mostLikelyReferent != null && mostLikelyReferent.getEnd() instanceof ObjectEntity) {
					ObjectEntity referent = (ObjectEntity) mostLikelyReferent.getEnd();
					return hasMatchingReferent(current, referent);
				}
			}
			return false;

		}
		return true;
	}

}
