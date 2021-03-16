/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.List;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.coref.data.ReferentCandidate;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public interface ISieve {

	public enum Modus {
		HARD, SOFT
	}

	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates);

}
