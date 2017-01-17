/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionEntityRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.SRLArgumentRelation;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public class ActionPossibleSieve extends Sieve {

	private static final double weight = 0.66;

	public ActionPossibleSieve() {
		super(Modus.HARD);
	}

	public ActionPossibleSieve(Modus modus) {
		super(modus);

	}

	static final String ID = "actionPossible";

	@Override
	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates) {
		List<ReferentCandidate> result = deepCopyCandidateList(candidates);
		List<ReferentCandidate> matches = new ArrayList<>();
		for (ReferentCandidate candidate : candidates) {
			if (candidate.getConfidence() > 0.0) {
				Entity entity = candidate.getCandidate();
				List<ActionEntityRelation> currentActions = getActionRelations(current);
				boolean actionsPossible = false;
				for (ActionEntityRelation aeRel : currentActions) {
					if (isProtoPatient(aeRel)) {
						if (entity instanceof ObjectEntity) {
							ObjectEntity objEntity = (ObjectEntity) entity;
							Action action = aeRel.getAction();
							if (objEntity.hasState()) {
								Set<State> objEntityStates = objEntity.getStates();
								if (action.hasRelatedConcept()) {
									ActionConcept actionConcept = action.getRelatedConcept();
									Set<State> statesChangedTo = actionConcept.getStatesChangedTo();
									for (State stateChangedTo : statesChangedTo) {
										if (!objEntityStates.contains(stateChangedTo)) {
											boolean associatedMatch = false;
											for (State associated : stateChangedTo.getAssociatedStates()) {
												if (objEntityStates.contains(associated)) {
													associatedMatch = true;
												}
											}
											if (associatedMatch) {
												actionsPossible = true;
											}
										} else {
											actionsPossible = false;
										}
									}

								}
							}

						} else if (entity instanceof PronounEntity) {
							PronounEntity pronounEntity = (PronounEntity) entity;
							Action action = aeRel.getAction();
							if (pronounEntity.hasState()) {
								Set<State> pronounEntityStates = pronounEntity.getStates();
								if (action.hasRelatedConcept()) {
									ActionConcept actionConcept = action.getRelatedConcept();
									Set<State> statesChangedTo = actionConcept.getStatesChangedTo();
									for (State stateChangedTo : statesChangedTo) {
										if (!pronounEntityStates.contains(stateChangedTo)) {
											boolean associatedMatch = false;
											for (State associated : stateChangedTo.getAssociatedStates()) {
												if (pronounEntityStates.contains(associated)) {
													associatedMatch = true;
												}
											}
											if (associatedMatch) {
												actionsPossible = true;
											}
										} else {
											actionsPossible = false;
										}

									}
								}
							}
						}

					}
				}
				if (actionsPossible) {
					matches.add(candidate);
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

	/*
	 * private Set<State> determineStates(PronounEntity pronounEntity,
	 * Set<State> states) { Set<State> currentStates = new HashSet<>(states); if
	 * (pronounEntity.hasRelationsOfType(ReferentRelation.class)) {
	 * List<Relation> relations =
	 * pronounEntity.getRelationsOfType(ReferentRelation.class); for (Relation
	 * relation : relations) { ReferentRelation refRel = (ReferentRelation)
	 * relation; if (refRel.getEnd().equals(pronounEntity)) { if
	 * (ContextUtils.getMostLikelyReferentRelation(refRel.getStart().
	 * getRelationsOfType(ReferentRelation.class), refRel.getStart()) != null &&
	 * refRel.equals(ContextUtils.getMostLikelyReferentRelation(
	 * refRel.getStart().getRelationsOfType(ReferentRelation.class),
	 * refRel.getStart()))) { if (refRel.getStart() instanceof PronounEntity) {
	 * PronounEntity pEntity = (PronounEntity) refRel.getStart();
	 * List<ActionEntityRelation> aeRels = getActionRelations(pEntity); for
	 * (ActionEntityRelation aeRel : aeRels) { Action action =
	 * aeRel.getAction();
	 * 
	 * if (action.hasRelatedConcept()) { ActionConcept actionConcept =
	 * action.getRelatedConcept(); Set<State> statesChangedTo =
	 * actionConcept.getStatesChangedTo(); for (State stateChangedTo :
	 * statesChangedTo) { for (State associated :
	 * stateChangedTo.getAssociatedStates()) { if
	 * (currentStates.contains(associated)) { currentStates.remove(associated);
	 * currentStates.add(stateChangedTo); } }
	 * 
	 * }
	 * 
	 * } } currentStates = determineStates(pEntity, currentStates); } } } } }
	 * return currentStates; }
	 */

	/*
	 * private Set<State> determineStates(ObjectEntity objEntity, Set<State>
	 * states) { Set<State> currentStates = new HashSet<>(states); if
	 * (objEntity.hasRelationsOfType(ReferentRelation.class)) { List<Relation>
	 * relations = objEntity.getRelationsOfType(ReferentRelation.class); for
	 * (Relation relation : relations) { ReferentRelation refRel =
	 * (ReferentRelation) relation; if (refRel.getEnd().equals(objEntity)) { if
	 * (ContextUtils.getMostLikelyReferentRelation(refRel.getStart().
	 * getRelationsOfType(ReferentRelation.class), refRel.getStart()) != null &&
	 * refRel.equals(ContextUtils.getMostLikelyReferentRelation(
	 * refRel.getStart().getRelationsOfType(ReferentRelation.class),
	 * refRel.getStart()))) { if (refRel.getStart() instanceof PronounEntity) {
	 * PronounEntity pEntity = (PronounEntity) refRel.getStart();
	 * List<ActionEntityRelation> aeRels = getActionRelations(pEntity); for
	 * (ActionEntityRelation aeRel : aeRels) { Action action =
	 * aeRel.getAction();
	 * 
	 * if (action.hasRelatedConcept()) { ActionConcept actionConcept =
	 * action.getRelatedConcept(); Set<State> statesChangedTo =
	 * actionConcept.getStatesChangedTo(); for (State stateChangedTo :
	 * statesChangedTo) { for (State associated :
	 * stateChangedTo.getAssociatedStates()) { if
	 * (currentStates.contains(associated)) { currentStates.remove(associated);
	 * currentStates.add(stateChangedTo); } }
	 * 
	 * }
	 * 
	 * } } currentStates = determineStates(pEntity, currentStates); } } } } }
	 * return currentStates; }
	 */

	private List<ActionEntityRelation> getActionRelations(Entity entity) {
		List<ActionEntityRelation> result = new ArrayList<>();
		Set<Relation> relations = entity.getRelations();
		for (Relation relation : relations) {
			if (relation instanceof ActionEntityRelation) {
				result.add((ActionEntityRelation) relation);
			}
		}
		return result;
	}

	/**
	 * Checks if the {@link Entity} takes a proto patient role in this
	 * {@link SRLArgumentRelation}
	 * 
	 * @param actionEntityRel
	 * @return
	 */
	private boolean isProtoPatient(ActionEntityRelation actionEntityRel) {
		if (actionEntityRel instanceof SRLArgumentRelation) {
			SRLArgumentRelation srl = (SRLArgumentRelation) actionEntityRel;
			List<String> verbNetRoles = srl.getVerbNetRoles();
			if (verbNetRoles.contains("Theme") || verbNetRoles.contains("Patient")) {
				return true;
			}
			List<String> frameNetRoles = srl.getFrameNetRoles();
			if (frameNetRoles.contains("Theme")) {
				return true;
			}

			return false;
		} else {
			//TODO: überprüfung mit anderer Quelle
			return false;
		}

	}

}
