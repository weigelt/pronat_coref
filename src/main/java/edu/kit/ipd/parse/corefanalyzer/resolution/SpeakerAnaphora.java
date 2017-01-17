/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SpeakerEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.Sieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.SieveFactory;

/**
 * @author Tobias Hey
 *
 */
public class SpeakerAnaphora implements IAnaphoraSolver {

	private List<ISieve> sieves;
	private static final Logger logger = LoggerFactory.getLogger(SpeakerAnaphora.class);

	public static final Set<String> firstPersonPronounsSingular = new HashSet<>(
			Arrays.asList(new String[] { "i", "me", "myself", "mine", "my", "i'm", "i've", "i'll", "i'd" }));
	public static final Set<String> firstPersonPronounsPlural = new HashSet<>(
			Arrays.asList(new String[] { "we", "us", "ourself", "ourselves", "ours", "our", "we're", "we've", "we'd", "we'll" }));

	public SpeakerAnaphora(Properties props) {
		sieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("SPEAKER_SIEVES").trim().split(",");

		for (String id : sieveArray) {
			id = id.trim();
			ISieve sieve = SieveFactory.createSieve(id);
			if (sieve != null) {
				sieves.add(sieve);
			} else {
				logger.error("Specified Sieve is not registered: " + id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.corefanalyzer.IAnaphoraSolver#searchCandidates(edu.kit.
	 * ipd.parse.contextanalyzer.data.entities.PronounEntity, java.util.List,
	 * edu.kit.ipd.parse.contextanalyzer.data.Context)
	 */
	@Override
	public List<ReferentCandidate> searchCandidates(PronounEntity current, List<ReferentCandidate> inputCandidates, Context context) {
		List<ReferentCandidate> result = new ArrayList<ReferentCandidate>(1);
		Entity referent = null;

		List<ReferentCandidate> possibleCandidates = new ArrayList<>(inputCandidates);
		for (ISieve sieve : sieves) {
			if (!possibleCandidates.isEmpty()) {
				possibleCandidates = sieve.sieve(current, possibleCandidates);
			}
		}
		if (Sieve.getNumberOfCandidates(possibleCandidates) == 0 && !hasSpeakerEntity(possibleCandidates)) {
			SpeakerEntity speaker = getSpeakerFromContext(context);
			if (speaker != null) {
				referent = speaker;
			} else {
				if (firstPersonPronounsSingular.contains(current.getName().toLowerCase())) {
					referent = new SpeakerEntity("SPEAKER", GrammaticalNumber.SINGULAR, current.getReference());
				} else {
					referent = new SpeakerEntity("SPEAKER_GROUP", GrammaticalNumber.PLURAL, current.getReference());

				}
				context.addEntity(referent);
			}

		} else {
			referent = getFirstNotSpeakerEntity(possibleCandidates);
		}
		result.add(new ReferentCandidate(referent, 1.0));
		logger.debug(current.getName() + "[" + ContextUtils.getPositionInUtterance(current) + "]" + ":" + result.toString());
		return result;
	}

	private SpeakerEntity getSpeakerFromContext(Context context) {
		for (Entity entity : context.getEntities()) {
			if (entity instanceof SpeakerEntity) {
				return (SpeakerEntity) entity;
			}
		}
		return null;
	}

	private boolean hasSpeakerEntity(List<ReferentCandidate> candidates) {
		for (ReferentCandidate candidate : candidates) {
			if (candidate.getCandidate() instanceof SpeakerEntity) {
				return true;
			}
		}
		return false;
	}

	private Entity getFirstNotSpeakerEntity(List<ReferentCandidate> candidates) {
		for (ReferentCandidate candidate : candidates) {
			if (!(candidate.getCandidate() instanceof SpeakerEntity)) {
				return candidate.getCandidate();
			}
		}
		return null;
	}

}
