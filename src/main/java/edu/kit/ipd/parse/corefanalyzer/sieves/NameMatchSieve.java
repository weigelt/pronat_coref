/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.util.MatchingUtils;

/**
 * @author Tobias Hey
 *
 */
public class NameMatchSieve extends Sieve {

	private static final double WEIGHT = 0.0;

	static final String ID = "nameMatch";

	private static final Set<String> femaleAddresses = new HashSet<>(
			Arrays.asList(new String[] { "mrs.", "miss", "ms.", "ma'am", "madam", "misses" }));
	private static final Set<String> maleAddresses = new HashSet<>(Arrays.asList(new String[] { "mr.", "mister", "sir" }));

	public NameMatchSieve() {
		super(Modus.HARD);
	}

	public NameMatchSieve(Modus modus) {
		super(modus);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.corefanalyzer.sieves.ISieve#sieve(edu.kit.ipd.parse.
	 * contextanalyzer.data.entities.Entity, java.util.List)
	 */
	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		for (ReferentCandidate candidate : candidates) {
			if (current instanceof SubjectEntity && candidate.getCandidate() instanceof SubjectEntity
					&& !((SubjectEntity) current).isSystem()) {
				if (!MatchingUtils.nameMatches(current.getName(), candidate.getCandidate().getName())) {
					SubjectEntity subjCandidate = (SubjectEntity) candidate.getCandidate();
					SubjectEntity subjCurrent = (SubjectEntity) current;
					if (subjCurrent.getGender().equals(Gender.FEMALE) || subjCurrent.getGender().equals(Gender.MALE)) {
						String[] nameCurrent = subjCurrent.getName().split(" ");
						String[] nameCandidate = subjCurrent.getName().split(" ");
						if (nameCurrent.length > 1) {
							if (nameCurrent.length > 2) { // Mr. John Mayer
								if (isAddress(nameCurrent[0], subjCurrent.getGender())) {
									if (isAddress(nameCandidate[0], subjCandidate.getGender())) { //  Mr. John Mayer -> Mr. Mayer
										if (!MatchingUtils.nameMatches(nameCurrent[nameCurrent.length - 1],
												nameCandidate[nameCandidate.length - 1])) {
											if (modus == Modus.HARD) {
												result.remove(candidate);
											} else {
												result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
											}
										}
									} else { // Mr. John Mayer -> Mayer || -> John Frank
										if (nameCandidate.length < nameCurrent.length) {
											if (!MatchingUtils.nameMatches(nameCurrent[nameCurrent.length - 1],
													nameCandidate[nameCandidate.length - 1])) {
												for (int i = 0; i < nameCandidate.length; i++) {
													if (!MatchingUtils.nameMatches(nameCurrent[i], nameCandidate[i])) {
														if (modus == Modus.HARD) {
															result.remove(candidate);
														} else {
															result.get(result.indexOf(candidate))
																	.setConfidence(candidate.getConfidence() * WEIGHT);
														}

													}
												}

											}
										}
									}

								} else {
									if (isAddress(nameCandidate[0], subjCandidate.getGender())) { //  John Frank Mayer -> Mr. Mayer
										if (!MatchingUtils.nameMatches(nameCurrent[nameCurrent.length - 1],
												nameCandidate[nameCandidate.length - 1])) {
											if (modus == Modus.HARD) {
												result.remove(candidate);
											} else {
												result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
											}
										}
									} else { // John Frank Mayer -> Mayer || -> John Frank
										if (nameCandidate.length < nameCurrent.length) {
											if (!MatchingUtils.nameMatches(nameCurrent[nameCurrent.length - 1],
													nameCandidate[nameCandidate.length - 1])) {
												for (int i = 0; i < nameCandidate.length; i++) {
													if (!MatchingUtils.nameMatches(nameCurrent[i], nameCandidate[i])) {
														if (modus == Modus.HARD) {
															result.remove(candidate);
														} else {
															result.get(result.indexOf(candidate))
																	.setConfidence(candidate.getConfidence() * WEIGHT);
														}

													}
												}

											}
										}
									}
								}

							} else {
								if (isAddress(nameCurrent[0], subjCurrent.getGender())) { // Mr. Mayer -> Mayer || John Mayer || Mr. John Mayer
									if (!MatchingUtils.nameMatches(nameCurrent[1], nameCandidate[nameCandidate.length - 1])) {
										if (modus == Modus.HARD) {
											result.remove(candidate);
										} else {
											result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
										}
									}
								} else { // John Mayer
									if (nameCandidate.length == 1) { // John Mayer -> John || -> Mayer
										if (!MatchingUtils.nameMatches(nameCurrent[0], nameCandidate[0])
												&& !MatchingUtils.nameMatches(nameCurrent[1], nameCandidate[0])) {
											if (modus == Modus.HARD) {
												result.remove(candidate);
											} else {
												result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
											}
										}
									} else if (nameCandidate.length == 2) { // John Mayer -> Mr. Mayer
										if (!isAddress(nameCandidate[0], subjCandidate.getGender())
												|| !MatchingUtils.nameMatches(nameCurrent[1], nameCandidate[1])) {
											if (modus == Modus.HARD) {
												result.remove(candidate);
											} else {
												result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
											}
										}
									} else if (nameCandidate.length > 2) { // John Mayer -> Mr. John Mayer
										if (!isAddress(nameCandidate[0], subjCandidate.getGender())
												|| !MatchingUtils.nameMatches(nameCurrent[0], nameCandidate[1])
												|| !MatchingUtils.nameMatches(nameCurrent[1], nameCandidate[2])) {
											if (modus == Modus.HARD) {
												result.remove(candidate);
											} else {
												result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
											}
										}

									}
								}
							}

						} else {
							if (isAddress(nameCandidate[0], subjCandidate.getGender())) {

								if (nameCandidate.length > 1 && MatchingUtils.nameMatches(nameCurrent[0], nameCandidate[1])) {
									continue;
								}
							}
							if (modus == Modus.HARD) {
								result.remove(candidate);
							} else {
								result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
							}
						}
					}
					if (modus == Modus.HARD) {
						result.remove(candidate);
					} else {
						result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
					}
				}
			} else if (current instanceof SubjectEntity) {
				if (((SubjectEntity) current).isSystem()) {
					if (!current.hasAssociatedConcept() || !candidate.getCandidate().hasAssociatedConcept()) {
						if (!MatchingUtils.nameMatches(current.getName().toLowerCase(), candidate.getCandidate().getName().toLowerCase())) {
							if (modus == Modus.HARD) {
								result.remove(candidate);
							} else {
								result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * WEIGHT);
							}
						}
					}
				}
			}

		}
		return result;
	}

	private boolean isAddress(String string, Gender gender) {
		if (gender.equals(Gender.FEMALE)) {
			if (femaleAddresses.contains(string.toLowerCase())) {
				return true;
			}
		} else if (gender.equals(Gender.MALE)) {
			if (maleAddresses.contains(string.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
