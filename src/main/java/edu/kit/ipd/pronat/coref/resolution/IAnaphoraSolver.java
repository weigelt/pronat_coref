/**
 * 
 */
package edu.kit.ipd.pronat.coref.resolution;

import java.util.List;

import edu.kit.ipd.pronat.coref.data.ReferentCandidate;
import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public interface IAnaphoraSolver {

	public List<ReferentCandidate> searchCandidates(PronounEntity current, List<ReferentCandidate> candidates, Context context);

}
