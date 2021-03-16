/**
 * 
 */
package edu.kit.ipd.pronat.coref;

import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.pronat.context.data.Context;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public interface ICorefAnalyzer {

	public void analyze(IGraph graph, Context context) throws MissingDataException;

}
