package edu.kit.ipd.parse.corefanalyzer.resolution;

import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;
import edu.kit.ipd.parse.luna.graph.IGraph;

public interface IResolution {

	public List<ReferentCandidate> getCandidates(Entity entity, IGraph graph, Context context) throws IllegalArgumentException;

}
