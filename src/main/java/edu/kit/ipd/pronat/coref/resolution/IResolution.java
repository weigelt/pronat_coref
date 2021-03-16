package edu.kit.ipd.pronat.coref.resolution;

import java.util.List;

import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.context.data.entities.Entity;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public interface IResolution {

	public List<ReferentCandidate> getCandidates(Entity entity, IGraph graph, Context context) throws IllegalArgumentException;

}
