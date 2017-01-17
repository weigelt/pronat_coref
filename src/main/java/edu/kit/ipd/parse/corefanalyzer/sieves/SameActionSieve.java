/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.resolution.PronounType;

/**
 * @author Tobias Hey
 *
 */
public class SameActionSieve extends Sieve {

	static final String ID = "sameAction";
	private static final double weight = 0.0;

	public SameActionSieve() {
		super(Modus.HARD);
	}

	public SameActionSieve(Modus modus) {
		super(modus);

	}

	private List<Entity> passSieve(Entity current, List<Entity> candidates) {
		List<Entity> result = new ArrayList<>(candidates);
		if (current instanceof PronounEntity && !ReflexiveSieve.isReflexiv(current)) {
			for (Entity entity : candidates) {
				if (ContextUtils.belongToSameAction(current, entity)) {

					// deal with some srl failures
					PronounType type = PronounType.getType(current.getName());
					if (type != null) {
						Set<String> verbNetRoles = ContextUtils.getVerbNetRoles(current);
						switch (type) {
						case OBJECT:
							if (!verbNetRoles.contains("Agent") && !verbNetRoles.contains("Actor")
									&& !verbNetRoles.contains("Experiencer")) {
								result.remove(entity);
							}
							break;
						case SPEAKER:
						case SUBJECT:
							if (!verbNetRoles.contains("Instrument") && !verbNetRoles.contains("Material")
									&& !verbNetRoles.contains("Product")) {
								result.remove(entity);
							}
							break;
						default:
							result.remove(entity);
							break;
						}
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
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
				}
			}
		}
		return result;
	}

}
