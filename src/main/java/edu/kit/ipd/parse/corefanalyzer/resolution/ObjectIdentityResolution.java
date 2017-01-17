/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.resolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.PossessivePronoun;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve.Modus;
import edu.kit.ipd.parse.corefanalyzer.sieves.SieveFactory;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * @author Tobias Hey
 *
 */
public class ObjectIdentityResolution implements IResolution {

	private List<ISieve> sieves;

	private AnaphoraResolution anaphoraResolution;
	private boolean possAnaphoraToBePerformed = false;

	private static final Logger logger = LoggerFactory.getLogger(ObjectIdentityResolution.class);

	public ObjectIdentityResolution(Properties props) {
		sieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("OBJECT_IDENTITY_SIEVES").trim().split(",");

		Modus modus = Modus.valueOf(props.getProperty("SIEVE_MODUS", Modus.HARD.toString()));
		for (String id : sieveArray) {
			id = id.trim();
			ISieve sieve;
			if (modus != null) {
				sieve = SieveFactory.createSieve(id, modus);
			} else {
				sieve = SieveFactory.createSieve(id);
			}
			if (sieve != null) {
				sieves.add(sieve);
			} else {
				logger.error("Specified Sieve is not registered: " + id);
			}
		}
		Properties possProProps = new Properties(props);
		possAnaphoraToBePerformed = Boolean.parseBoolean(possProProps.getProperty("POSS_ANAPHORA_RESOLUTION"));
		possProProps.setProperty("GENERAL_SIEVES", possProProps.getProperty("POSS_GENERAL_SIEVES"));
		possProProps.setProperty("OBJECT_SIEVES", possProProps.getProperty("POSS_OBJECT_SIEVES"));
		possProProps.setProperty("GROUP_SIEVES", possProProps.getProperty("POSS_GROUP_SIEVES"));
		possProProps.setProperty("SPEAKER_SIEVES", possProProps.getProperty("POSS_SPEAKER_SIEVES"));
		possProProps.setProperty("SUBJECT_SIEVES", possProProps.getProperty("POSS_SUBJECT_SIEVES"));
		anaphoraResolution = new AnaphoraResolution(possProProps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.corefanalyzer.IResolution#getCandidates(edu.kit.ipd.
	 * parse.contextanalyzer.data.Entity, edu.kit.ipd.parse.luna.graph.IGraph,
	 * edu.kit.ipd.parse.contextanalyzer.data.Context)
	 */
	@Override
	public List<ReferentCandidate> getCandidates(Entity entity, IGraph graph, Context context) throws IllegalArgumentException {

		if (!(entity instanceof ObjectEntity)) {
			throw new IllegalArgumentException("ObjectEntity expected");
		}
		ObjectEntity objectEntity = (ObjectEntity) entity;
		if (!objectEntity.getPossessivePronouns().isEmpty()) {
			if (possAnaphoraToBePerformed) {
				possessivePronounResolution(graph, context, objectEntity);
			}
		}
		List<ReferentCandidate> candidates = new ArrayList<ReferentCandidate>();
		for (Entity candidate : context.getEntities()) {
			candidates.add(new ReferentCandidate(candidate, 1.0));
		}
		for (ISieve sieve : sieves) {
			if (!candidates.isEmpty()) {

				candidates = sieve.sieve(objectEntity, candidates);

			}
		}
		logger.debug(entity.getName() + "[" + ContextUtils.getPositionInUtterance(entity) + "]" + ":" + candidates.toString());
		return candidates;
	}

	private void possessivePronounResolution(IGraph graph, Context context, ObjectEntity objectEntity) {
		for (String poss : objectEntity.getPossessivePronouns()) {
			PronounType type = PronounType.getType(poss);
			if (type != null) {
				GrammaticalNumber number = GrammaticalNumber.SINGULAR;
				if (type.equals(PronounType.GROUP)) {
					number = GrammaticalNumber.PLURAL;
				}
				PronounEntity pEntity = new PossessivePronoun(poss, number, objectEntity);
				List<ReferentCandidate> candidates = anaphoraResolution.getCandidates(pEntity, graph, context);
				for (ReferentCandidate referentCandidate : candidates) {
					if (referentCandidate.getConfidence() > 0.0) {
						for (Entity candidate : referentCandidate.getReferents()) {
							ReferentRelation rel = new ReferentRelation("possessivePronounReferent", referentCandidate.getConfidence(),
									objectEntity, candidate);
							objectEntity.addRelation(rel);
							candidate.addRelation(rel);
						}

					}
				}
			}
		}
	}

}
