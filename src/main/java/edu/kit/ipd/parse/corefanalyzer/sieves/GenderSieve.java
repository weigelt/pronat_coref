/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.resolution.SubjectAnaphora;
import edu.kit.ipd.parse.corefanalyzer.util.MatchingUtils;

/**
 * @author Tobias Hey
 *
 */
public class GenderSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	public GenderSieve() {
		super(Modus.HARD);
	}

	public GenderSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "genderMatch";

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		Gender gender = Gender.UNKNOWN;
		if (current instanceof PronounEntity) {
			String pronoun = current.getName().toLowerCase();

			if (SubjectAnaphora.malePronouns.contains(pronoun)) {
				gender = Gender.MALE;
			} else if (SubjectAnaphora.femalePronouns.contains(pronoun)) {
				gender = Gender.FEMALE;
			}
		} else if (current instanceof SubjectEntity) {
			gender = ((SubjectEntity) current).getGender();
		}
		for (Entity entity : candidates) {
			if (entity instanceof PronounEntity) {
				String pronounName = entity.getName();

				if (gender.equals(Gender.FEMALE)) {
					if (!SubjectAnaphora.femalePronouns.contains(pronounName)) {
						result.remove(entity);
						continue;
					}
				} else if (gender.equals(Gender.MALE)) {
					if (!SubjectAnaphora.malePronouns.contains(pronounName)) {
						result.remove(entity);
						continue;
					}
				} else if (gender.equals(Gender.UNKNOWN)) {
					if (!SubjectAnaphora.secondPersonPronouns.contains(pronounName)) {
						result.remove(entity);
						continue;
					}
				}
			} else if (entity instanceof SubjectEntity) {
				if (!MatchingUtils.genderMatches(gender, ((SubjectEntity) entity).getGender())) {
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
