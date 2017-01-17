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
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve;
import edu.kit.ipd.parse.corefanalyzer.sieves.ISieve.Modus;
import edu.kit.ipd.parse.corefanalyzer.sieves.SieveFactory;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * @author Tobias Hey
 *
 */
public class SubjectIdentityResolution implements IResolution {

	private List<ISieve> sieves;

	private static final Logger logger = LoggerFactory.getLogger(SubjectIdentityResolution.class);

	public SubjectIdentityResolution(Properties props) {
		sieves = new ArrayList<>();

		String[] sieveArray = props.getProperty("SUBJECT_IDENTITY_SIEVES").trim().split(",");

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
	}

	@Override
	public List<ReferentCandidate> getCandidates(Entity entity, IGraph graph, Context context) {
		if (!(entity instanceof SubjectEntity)) {
			throw new IllegalArgumentException("SubjectEntity expected");
		}
		SubjectEntity subjectEntity = (SubjectEntity) entity;

		List<ReferentCandidate> candidates = new ArrayList<ReferentCandidate>();
		for (Entity candidate : context.getEntities()) {
			candidates.add(new ReferentCandidate(candidate, 1.0));
		}
		for (ISieve sieve : sieves) {
			if (!candidates.isEmpty()) {
				candidates = sieve.sieve(subjectEntity, candidates);
			}
		}
		logger.debug(entity.getName() + "[" + ContextUtils.getPositionInUtterance(entity) + "]" + ":" + candidates.toString());

		return candidates;
	}

}
