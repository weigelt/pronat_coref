/**
 * 
 */
package edu.kit.ipd.pronat.coref.data;

import edu.kit.ipd.pronat.context.data.entities.Entity;
import edu.kit.ipd.pronat.context.data.entities.GrammaticalNumber;
import edu.kit.ipd.pronat.context.data.entities.PronounEntity;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class PossessivePronoun extends PronounEntity {

	private Entity describedEntity;

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param describedEntity
	 */
	public PossessivePronoun(String name, GrammaticalNumber grammaticalNumber, Entity describedEntity) {

		super(name, grammaticalNumber, describedEntity.getReference());
		this.describedEntity = describedEntity;

	}

	public Entity getDescribedEntity() {
		return describedEntity;
	}

	public void setDescribedEntity(Entity describedEntity) {
		this.describedEntity = describedEntity;
	}

}
