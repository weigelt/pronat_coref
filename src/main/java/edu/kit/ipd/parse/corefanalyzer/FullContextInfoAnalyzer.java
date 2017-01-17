/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.resolution.AnaphoraResolution;
import edu.kit.ipd.parse.corefanalyzer.resolution.ObjectIdentityResolution;
import edu.kit.ipd.parse.corefanalyzer.resolution.SubjectIdentityResolution;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * @author Tobias Hey
 *
 */
public class FullContextInfoAnalyzer implements ICorefAnalyzer {

	private AnaphoraResolution anaphoraRes;
	private SubjectIdentityResolution subjectRes;
	private ObjectIdentityResolution objectRes;

	private boolean anaphoraResToBePerformed, subjectIdentityResToBePerformed, objectIdentityResToBePerformed;

	public FullContextInfoAnalyzer(Properties props) {

		anaphoraResToBePerformed = Boolean.parseBoolean(props.getProperty("ANAPHORA_RESOLUTION", "true"));
		subjectIdentityResToBePerformed = Boolean.parseBoolean(props.getProperty("SUBJECT_IDENTITY_RESOLUTION", "true"));
		objectIdentityResToBePerformed = Boolean.parseBoolean(props.getProperty("OBJECT_IDENTITY_RESOLUTION", "true"));
		anaphoraRes = new AnaphoraResolution(props);
		subjectRes = new SubjectIdentityResolution(props);
		objectRes = new ObjectIdentityResolution(props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.corefanalyzer.ICorefAnalyzer#analyze(edu.kit.ipd.parse.
	 * luna.graph.IGraph, edu.kit.ipd.parse.corefanalyzer.data.Context)
	 */
	@Override
	public void analyze(IGraph graph, Context context) throws MissingDataException {
		if (anaphoraResToBePerformed) {
			List<PronounEntity> pronouns = getPronounEntities(context);
			for (PronounEntity pronounEntity : pronouns) {
				List<ReferentCandidate> candidates = anaphoraRes.getCandidates(pronounEntity, graph, context);
				for (ReferentCandidate referentCandidate : candidates) {
					for (Entity entity : referentCandidate.getReferents()) {
						if (referentCandidate.getConfidence() > 0.0) {

							ReferentRelation rel = new ReferentRelation("anaphoraReferent", referentCandidate.getConfidence(),
									pronounEntity, entity);
							ReferentRelation alreadyExisting = getMatchingReferentRelation(rel);
							if (alreadyExisting != null) {
								if (!(Math.abs(alreadyExisting.getConfidence() - rel.getConfidence()) < 0.0000001d)) {
									pronounEntity.getRelations().remove(alreadyExisting);
									entity.getRelations().remove(alreadyExisting);
									pronounEntity.addRelation(rel);
									entity.addRelation(rel);
								}
							} else {
								pronounEntity.addRelation(rel);
								entity.addRelation(rel);
							}

						}
					}
				}
			}
		}
		if (subjectIdentityResToBePerformed) {
			List<SubjectEntity> subjects = getSubjectEntities(context);
			for (SubjectEntity subjectEntity : subjects) {
				List<ReferentCandidate> candidates = subjectRes.getCandidates(subjectEntity, graph, context);
				for (ReferentCandidate referentCandidate : candidates) {
					for (Entity entity : referentCandidate.getReferents()) {
						if (referentCandidate.getConfidence() > 0.0) {
							ReferentRelation rel = new ReferentRelation("subjectIdentityReferent", referentCandidate.getConfidence(),
									subjectEntity, entity);
							ReferentRelation alreadyExisting = getMatchingReferentRelation(rel);
							if (alreadyExisting != null) {
								if (!(Math.abs(alreadyExisting.getConfidence() - rel.getConfidence()) < 0.0000001d)) {
									subjectEntity.getRelations().remove(alreadyExisting);
									entity.getRelations().remove(alreadyExisting);
									subjectEntity.addRelation(rel);
									entity.addRelation(rel);
								}
							} else {
								subjectEntity.addRelation(rel);
								entity.addRelation(rel);
							}
						}
					}
				}
			}
		}
		if (objectIdentityResToBePerformed) {
			List<ObjectEntity> objects = getObjectEntities(context);
			for (ObjectEntity objectEntity : objects) {
				List<ReferentCandidate> candidates = objectRes.getCandidates(objectEntity, graph, context);
				for (ReferentCandidate referentCandidate : candidates) {
					for (Entity entity : referentCandidate.getReferents()) {
						if (referentCandidate.getConfidence() > 0.0) {
							ReferentRelation rel = new ReferentRelation("objectIdentityReferent", referentCandidate.getConfidence(),
									objectEntity, entity);
							ReferentRelation alreadyExisting = getMatchingReferentRelation(rel);
							if (alreadyExisting != null) {
								if (!(Math.abs(alreadyExisting.getConfidence() - rel.getConfidence()) < 0.0000001d)) {
									objectEntity.getRelations().remove(alreadyExisting);
									entity.getRelations().remove(alreadyExisting);
									objectEntity.addRelation(rel);
									entity.addRelation(rel);
								}
							} else {
								objectEntity.addRelation(rel);
								entity.addRelation(rel);
							}
						}
					}
				}
			}
		}
		context.printToGraph(graph);

	}

	private ReferentRelation getMatchingReferentRelation(ReferentRelation current) {
		if (current.getStart().hasRelationsOfType(ReferentRelation.class)) {
			List<Relation> relations = current.getStart().getRelationsOfType(ReferentRelation.class);
			for (Relation relation : relations) {
				if (relation instanceof ReferentRelation) {
					ReferentRelation refRel = (ReferentRelation) relation;
					if (refRel.getStart().equals(current.getStart()) && refRel.getEnd().equals(current.getEnd())) {
						return refRel;
					}
				}
			}

		}
		return null;
	}

	private List<PronounEntity> getPronounEntities(Context context) {
		List<PronounEntity> result = new ArrayList<PronounEntity>();
		for (Entity entity : context.getEntities()) {
			if (entity instanceof PronounEntity) {
				result.add((PronounEntity) entity);
			}
		}
		Collections.sort(result);
		return result;
	}

	private List<SubjectEntity> getSubjectEntities(Context context) {
		List<SubjectEntity> result = new ArrayList<SubjectEntity>();
		for (Entity entity : context.getEntities()) {
			if (entity instanceof SubjectEntity) {
				result.add((SubjectEntity) entity);
			}
		}
		Collections.sort(result);
		return result;
	}

	private List<ObjectEntity> getObjectEntities(Context context) {
		List<ObjectEntity> result = new ArrayList<ObjectEntity>();
		for (Entity entity : context.getEntities()) {
			if (entity instanceof ObjectEntity) {
				result.add((ObjectEntity) entity);
			}
		}
		Collections.sort(result);
		return result;
	}

}
