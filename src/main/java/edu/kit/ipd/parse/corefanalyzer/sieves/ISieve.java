/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.sieves;

import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.corefanalyzer.data.ReferentCandidate;

/**
 * @author Tobias Hey
 *
 */
public interface ISieve {

	public enum Modus {
		HARD, SOFT
	}

	public List<ReferentCandidate> sieve(Entity current, List<ReferentCandidate> candidates);

}
