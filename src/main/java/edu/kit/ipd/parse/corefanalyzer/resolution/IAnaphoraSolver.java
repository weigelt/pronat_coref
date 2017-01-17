/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.resolution;

import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public interface IAnaphoraSolver {

	public List<ReferentCandidate> searchCandidates(PronounEntity current, List<ReferentCandidate> candidates, Context context);

}
