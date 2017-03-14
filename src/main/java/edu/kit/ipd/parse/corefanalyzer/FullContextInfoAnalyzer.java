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
				putIntoContext(pronounEntity, candidates, "anaphoraReferent");
			}
		}
		if (subjectIdentityResToBePerformed) {
			List<SubjectEntity> subjects = getSubjectEntities(context);
			for (SubjectEntity subjectEntity : subjects) {
				List<ReferentCandidate> candidates = subjectRes.getCandidates(subjectEntity, graph, context);
				putIntoContext(subjectEntity, candidates, "subjectIdentityReferent");
			}
		}
		if (objectIdentityResToBePerformed) {
			List<ObjectEntity> objects = getObjectEntities(context);
			for (ObjectEntity objectEntity : objects) {
				List<ReferentCandidate> candidates = objectRes.getCandidates(objectEntity, graph, context);
				putIntoContext(objectEntity, candidates, "objectIdentityReferent");
			}
		}

		context.printToGraph(graph);

	}

	private void putIntoContext(Entity entity, List<ReferentCandidate> candidates, String relName) {
		boolean isVerified = false;
		List<ReferentRelation> rels = new ArrayList<>();
		for (Relation r : entity.getRelationsOfType(ReferentRelation.class)) {
			ReferentRelation refRel = (ReferentRelation) r;
			rels.add(refRel);
			if (refRel.isVerifiedByDialogAgent()) {
				isVerified = true;
			}
		}
		if (!isVerified) {
			List<ReferentRelation> matched = new ArrayList<>();
			for (ReferentCandidate referentCandidate : candidates) {
				for (Entity referent : referentCandidate.getReferents()) {
					if (referentCandidate.getConfidence() > 0.0) {

						ReferentRelation rel = new ReferentRelation(relName, referentCandidate.getConfidence(), entity, referent);
						ReferentRelation alreadyExisting = getMatchingReferentRelation(rel);
						if (alreadyExisting != null) {
							if (!(Math.abs(alreadyExisting.getConfidence() - rel.getConfidence()) < 0.0000001d)) {
								entity.removeRelation(alreadyExisting);
								referent.getRelations().remove(alreadyExisting);
								entity.addRelation(rel);
								referent.addRelation(rel);
							}
							matched.add(alreadyExisting);
						} else {
							entity.addRelation(rel);
							referent.addRelation(rel);
						}

					}
				}
			}
			if (matched.size() < rels.size()) {
				for (ReferentRelation referentRelation : rels) {
					if (!matched.contains(referentRelation)) {
						referentRelation.getStart().removeRelation(referentRelation);
						referentRelation.getEnd().removeRelation(referentRelation);
					}
				}
			}
		} else {
			for (ReferentRelation referentRelation : rels) {
				if (!referentRelation.isVerifiedByDialogAgent()) {
					referentRelation.getStart().removeRelation(referentRelation);
					referentRelation.getEnd().removeRelation(referentRelation);
				}
			}
		}
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
