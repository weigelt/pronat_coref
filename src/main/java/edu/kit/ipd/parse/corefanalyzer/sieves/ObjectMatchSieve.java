/**
 *
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.EntityConcept;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.IStateOwner;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.util.MatchingUtils;
import edu.kit.ipd.parse.luna.graph.INode;
import info.debatty.java.stringsimilarity.JaroWinkler;

/**
 * @author Tobias Hey
 *
 */
public class ObjectMatchSieve extends Sieve {

	private static final double WEIGHT = 0.0;
	private static final double JW_SIMILARITY_THRESHOLD = 0.92;

	private JaroWinkler jaroWinkler;

	public ObjectMatchSieve() {
		super(Modus.HARD);
		jaroWinkler = new JaroWinkler();
	}

	public ObjectMatchSieve(Modus modus) {
		super(modus);
		jaroWinkler = new JaroWinkler();
	}

	static final String ID = "objectMatch";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);

		result = new ArrayList<>(candidates);
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

	private boolean containsAllQuantifier(List<INode> reference) {
		for (INode iNode : reference) {
			if (iNode.getAttributeValue("value").equals("all") || iNode.getAttributeValue("value").equals("every")
					|| iNode.getAttributeValue("value").equals("each")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = new ArrayList<>();
		ReferentCandidate cand = null;
		if (containsAllQuantifier(current.getReference())) {
			if (current.hasAssociatedConcept()) {
				EntityConcept currentConcept = ContextUtils.getMostLikelyEntityConcept(current.getAssociatedConcepts());
				if (!hasDirectCandidate(current, candidates)) {
					for (ReferentCandidate referentCandidate : candidates) {
						if (referentCandidate.getCandidate().hasAssociatedConcept()) {
							EntityConcept candidateConcept = ContextUtils
									.getMostLikelyEntityConcept(referentCandidate.getCandidate().getAssociatedConcepts());
							if (isSubsumed(currentConcept, candidateConcept)) {
								if (matchingStatesExistInCandidates(current, candidates)) {
									if (matchesAllStates(current, referentCandidate.getCandidate())) {
										if (cand == null) {
											cand = new ReferentCandidate(referentCandidate.getCandidate(), 1.0);
										} else {
											cand.addReferent(referentCandidate.getCandidate());
										}
									}
								} else {
									if (cand == null) {
										cand = new ReferentCandidate(referentCandidate.getCandidate(), 1.0);
									} else {
										cand.addReferent(referentCandidate.getCandidate());
									}
								}

							}
						}

					}
				}
			}
			if (cand != null) {
				result.add(cand);
			}

		}
		if (result.isEmpty()) {
			result = deepCopyCandidateList(candidates);
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
		}
		return result;
	}

	private boolean matchesAllStates(Entity current, Entity candidate) {
		boolean result = false;
		if (current instanceof ObjectEntity) {
			ObjectEntity obj = (ObjectEntity) current;
			result = true;
			for (String adjective : obj.getDescribingAdjectives()) {
				boolean match = false;
				if (candidate instanceof IStateOwner) {
					IStateOwner stateOwner = (IStateOwner) candidate;

					for (State state : stateOwner.getStates()) {

						if (jaroWinkler.similarity(adjective.toLowerCase(),
								state.getName().trim().replaceAll(" ", "").toLowerCase()) > JW_SIMILARITY_THRESHOLD) {
							match = true;
						}
					}

				}
				if (!match) {
					result = false;
				}
			}
		}
		return result;
	}

	private boolean matchingStatesExistInCandidates(Entity current, List<ReferentCandidate> candidates) {
		if (current instanceof ObjectEntity) {
			ObjectEntity obj = (ObjectEntity) current;
			for (String adjective : obj.getDescribingAdjectives()) {
				for (ReferentCandidate referentCandidate : candidates) {
					Entity candidate = referentCandidate.getCandidate();
					if (candidate instanceof IStateOwner) {
						IStateOwner stateOwner = (IStateOwner) candidate;
						for (State state : stateOwner.getStates()) {
							if (jaroWinkler.similarity(adjective.toLowerCase(),
									state.getName().trim().replaceAll(" ", "").toLowerCase()) > JW_SIMILARITY_THRESHOLD) {
								return true;

							}
						}

					}

				}
			}
		}
		return false;
	}

	private boolean hasDirectCandidate(Entity current, List<ReferentCandidate> result) {
		for (ReferentCandidate referentCandidate : result) {
			if (current instanceof ObjectEntity && referentCandidate.getCandidate() instanceof ObjectEntity) {
				if (matchesWithoutConcepts((ObjectEntity) current, (ObjectEntity) referentCandidate.getCandidate())) {
					return true;
				}
			}
		}
		return false;
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

	private boolean matchesWithoutConcepts(ObjectEntity current, ObjectEntity candidate) {

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
