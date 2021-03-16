/**
 * 
 */
package edu.kit.ipd.pronat.coref.sieves;

import java.util.HashMap;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class SieveFactory {
	private static HashMap<String, ISieve> sieves = new HashMap<>();

	public static ISieve createSieve(String id) {
		return createSieve(id, ISieve.Modus.HARD);
	}

	public static ISieve createSieve(String id, ISieve.Modus modus) {
		if (sieves.containsKey(id)) {
			return sieves.get(id);
		} else {
			ISieve sieve = null;
			if (id.equals(GeneralDeterminerSieve.ID)) {
				sieve = new GeneralDeterminerSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(AlreadyRelatedSieve.ID)) {
				sieve = new AlreadyRelatedSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(TypeMatchSieve.ID)) {
				sieve = new TypeMatchSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(PrecedingEntitiesSieve.ID)) {
				sieve = new PrecedingEntitiesSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(ObjectMatchSieve.ID)) {
				sieve = new ObjectMatchSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(GrammaticalNumberSieve.ID)) {
				sieve = new GrammaticalNumberSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(InstructionNumberSieve.ID)) {
				sieve = new InstructionNumberSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(SameActionSieve.ID)) {
				sieve = new SameActionSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(ConceptMatchSieve.ID)) {
				sieve = new ConceptMatchSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(AlternativeStatementSieve.ID)) {
				sieve = new AlternativeStatementSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(ActionPossibleSieve.ID)) {
				sieve = new ActionPossibleSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(GenderSieve.ID)) {
				sieve = new GenderSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(SyntacticalDistanceSieve.ID)) {
				sieve = new SyntacticalDistanceSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(NameMatchSieve.ID)) {
				sieve = new NameMatchSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(LocativeSieve.ID)) {
				sieve = new LocativeSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(SRLRoleMatchSieve.ID)) {
				sieve = new SRLRoleMatchSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(ReflexiveSieve.ID)) {
				sieve = new ReflexiveSieve(modus);
				sieves.put(id, sieve);
			} else if (id.equals(PossessiveMeronymSieve.ID)) {
				sieve = new PossessiveMeronymSieve(modus);
				sieves.put(id, sieve);
			}
			return sieve;
		}

	}

}
