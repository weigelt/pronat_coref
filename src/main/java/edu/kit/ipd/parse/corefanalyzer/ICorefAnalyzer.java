/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * @author Tobias Hey
 *
 */
public interface ICorefAnalyzer {

	public void analyze(IGraph graph, Context context) throws MissingDataException;

}
