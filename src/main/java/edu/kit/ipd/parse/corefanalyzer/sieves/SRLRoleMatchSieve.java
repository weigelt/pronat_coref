/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.SRLArgumentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public class SRLRoleMatchSieve extends Sieve {

	private static final double weight = 0.7;

	static final String ID = "srlRoleMatch";

	/**
	 * @param modus
	 */
	public SRLRoleMatchSieve(Modus modus) {
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
		List<ReferentCandidate> matches = new ArrayList<ReferentCandidate>();
		for (ReferentCandidate candidate : candidates) {
			if (Math.abs(ContextUtils.getInstructionNumber(candidate.getCandidate()) - ContextUtils.getInstructionNumber(current)) < 2) {
				Entity candidateEntity = candidate.getCandidate();
				if (current.hasRelationsOfType(SRLArgumentRelation.class) && candidateEntity.hasRelationsOfType(SRLArgumentRelation.class)
						&& candidate.getConfidence() > 0.0) {
					List<Relation> currentRels = current.getRelationsOfType(SRLArgumentRelation.class);
					List<Relation> candidateRels = candidateEntity.getRelationsOfType(SRLArgumentRelation.class);
					boolean emptyRoles = true;
					for (Relation currRelation : currentRels) {
						SRLArgumentRelation currRel = (SRLArgumentRelation) currRelation;
						for (Relation candRelation : candidateRels) {
							SRLArgumentRelation candRel = (SRLArgumentRelation) candRelation;
							if (!candRel.getVerbNetRoles().isEmpty() && !currRel.getVerbNetRoles().isEmpty()) {
								if (hasRoleMatch(currRel.getVerbNetRoles(), candRel.getVerbNetRoles())) {

									matches.add(candidate);
								}
								emptyRoles = false;
							}
							if (!candRel.getFrameNetRoles().isEmpty() && !currRel.getFrameNetRoles().isEmpty()) {
								if (hasRoleMatch(currRel.getFrameNetRoles(), candRel.getFrameNetRoles())) {

									matches.add(candidate);
								}
								emptyRoles = false;
							}
							if (emptyRoles) {
								if (candRel.getName().equals(currRel.getName())) {

									matches.add(candidate);
								}
							}
						}
					}
				}
			}
		}
		if (!matches.isEmpty()) {
			for (ReferentCandidate candidate : candidates) {
				if (!matches.contains(candidate)) {
					result.get(result.indexOf(candidate)).setConfidence(candidate.getConfidence() * weight);
				}
			}
		}
		return result;
	}

	private boolean hasRoleMatch(List<String> current, List<String> candidate) {
		boolean result = false;
		for (String string : candidate) {
			if (current.contains(string)) {
				result = true;
			}
		}
		return result;
	}

}
