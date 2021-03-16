package edu.kit.ipd.pronat.coref.resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.pronat.coref.sieves.ISieve;
import edu.kit.ipd.pronat.coref.sieves.ISieve.Modus;
import edu.kit.ipd.pronat.coref.sieves.SieveFactory;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class AnaphoraResolution implements IResolution {

	private List<ISieve> generalSieves;
	private HashMap<PronounType, IAnaphoraSolver> solvers;
	private static final Logger logger = LoggerFactory.getLogger(AnaphoraResolution.class);

	public AnaphoraResolution(Properties props) {
		generalSieves = new ArrayList<>();
		String[] sieveArray = props.getProperty("GENERAL_SIEVES").trim().split(",");

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
				generalSieves.add(sieve);
			} else {
				logger.error("Specified Sieve is not registered: " + id);
			}
		}
		solvers = new HashMap<>();
		solvers.put(PronounType.SUBJECT, new SubjectAnaphora(props));
		solvers.put(PronounType.SPEAKER, new SpeakerAnaphora(props));
		solvers.put(PronounType.OBJECT, new ObjectAnaphora(props));
		solvers.put(PronounType.GROUP, new GroupAnaphora(props));
	}

	@Override
	public List<ReferentCandidate> getCandidates(Entity entity, IGraph graph, Context context) {
		List<ReferentCandidate> result = new ArrayList<>();

		if (!(entity instanceof PronounEntity)) {
			throw new IllegalArgumentException("PronounEntity expected");
		}
		PronounEntity pronounEntity = (PronounEntity) entity;
		List<ReferentCandidate> candidates = new ArrayList<ReferentCandidate>();
		for (Entity candidate : context.getEntities()) {
			candidates.add(new ReferentCandidate(candidate, 1.0));
		}
		PronounType type = PronounType.getType(pronounEntity.getName());
		if (type != null) {
			for (ISieve sieve : generalSieves) {
				if (!candidates.isEmpty()) {
					candidates = sieve.sieve(entity, candidates);
				}

			}

			result.addAll(solvers.get(type).searchCandidates(pronounEntity, candidates, context));

		}

		return result;
	}

}