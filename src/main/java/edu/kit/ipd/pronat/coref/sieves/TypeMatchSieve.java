/**
 *
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.ObjectEntity;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;
import edu.kit.ipd.pronat.context.data.entities.SubjectEntity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.pronat.coref.resolution.PronounType;
import edu.kit.ipd.pronat.coref.resolution.SpeakerAnaphora;
import edu.kit.ipd.pronat.coref.resolution.SubjectAnaphora;
import edu.kit.ipd.pronat.coref.util.MatchingUtils;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class TypeMatchSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public TypeMatchSieve() {
		super(Modus.HARD);

	}

	public TypeMatchSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "typeMatch";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		if (current instanceof PronounEntity) {
			PronounType type = PronounType.getType(current.getName());
			boolean pronounMatchFound = false;
			for (Entity entity : candidates) {
				if (pronounMatchFound) {
					result.remove(entity);
					continue;
				}
				if (entity instanceof PronounEntity) {
					PronounType candType = PronounType.getType(entity.getName());
					if (candType == null || !candType.equals(type)) {
						result.remove(entity);
					} else if (candType.equals(type)) {
						if (candType.equals(PronounType.SPEAKER)) {
							if (SpeakerAnaphora.firstPersonPronounsSingular.contains(entity.getName().toLowerCase())
									&& SpeakerAnaphora.firstPersonPronounsSingular.contains(current.getName().toLowerCase())) {
								if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
									pronounMatchFound = true;
								}

							} else if (SpeakerAnaphora.firstPersonPronounsPlural.contains(entity.getName().toLowerCase())
									&& SpeakerAnaphora.firstPersonPronounsPlural.contains(current.getName().toLowerCase())) {
								if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
									pronounMatchFound = true;
								}
							} else {
								result.remove(entity);
							}
						} else if (candType.equals(PronounType.SUBJECT)) {
							if (SubjectAnaphora.secondPersonPronouns.contains(entity.getName().toLowerCase())
									&& SubjectAnaphora.secondPersonPronouns.contains(current.getName().toLowerCase())) {
								if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
									pronounMatchFound = true;
								}

							} else if (SubjectAnaphora.femalePronouns.contains(entity.getName().toLowerCase())
									&& SubjectAnaphora.femalePronouns.contains(current.getName().toLowerCase())) {
								if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
									pronounMatchFound = true;
								}
							} else if (SubjectAnaphora.malePronouns.contains(entity.getName().toLowerCase())
									&& SubjectAnaphora.malePronouns.contains(current.getName().toLowerCase())) {
								if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
									pronounMatchFound = true;
								}
							} else {
								result.remove(entity);
							}
						} else {
							if (!MatchingUtils.isInAlternativeStatementBlock(current, entity)) {
								pronounMatchFound = true;
							}

						}
					}
				} else {
					switch (type) {
					case SPEAKER:
						result.remove(entity);
						break;
					case SUBJECT:
						if (!(entity instanceof SubjectEntity)) {
							result.remove(entity);
						}
						break;
					case GROUP:
						if (!(entity instanceof ObjectEntity) && !(entity instanceof SubjectEntity)) {
							result.remove(entity);
						}
						break;
					case OBJECT:
						if (!(entity instanceof ObjectEntity)) {
							result.remove(entity);
						}
						break;
					default:
						break;
					}
				}
			}
		} else {

			for (Entity entity : candidates) {
				if (!entity.getClass().equals(current.getClass())) {
					result.remove(entity);
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

}
