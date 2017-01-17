/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.data;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;

/**
 * @author Tobias Hey
 *
 */
public class PossessivePronoun extends PronounEntity {

	private Entity describedEntity;

	/**
	 * @param name
	 * @param grammaticalNumber
	 * @param reference
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
