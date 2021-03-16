/**
 *
 */
package edu.kit.ipd.pronat.coref.data;

import edu.kit.ipd.pronat.context.data.entities.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class ReferentCandidate implements Comparable<ReferentCandidate>, Cloneable {

	private List<Entity> referents;
	private double confidence;

	/**
	 * @param referent
	 * @param confidence
	 */
	public ReferentCandidate(Entity referent, double confidence) {
		referents = new ArrayList<Entity>();
		referents.add(referent);
		this.confidence = confidence;
	}

	/**
	 * @param referents
	 * @param confidence
	 */
	public ReferentCandidate(List<Entity> referents, double confidence) {
		this.referents = referents;
		this.confidence = confidence;
	}

	/**
	 * @return the referent
	 */
	public List<Entity> getReferents() {
		return referents;
	}

	/**
	 * @param referent
	 *            the referent to set
	 */
	public void addReferent(Entity referent) {
		referents.add(referent);
	}

	/**
	 *
	 * @return
	 */
	public Entity getCandidate() {
		return referents.get(0);
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	@Override
	public int compareTo(ReferentCandidate o) {
		return getCandidate().compareTo(o.getCandidate());
	}

	@Override
	public ReferentCandidate clone() {
		ReferentCandidate c = new ReferentCandidate(getCandidate(), getConfidence());
		for (Entity entity : referents) {
			if (!c.getReferents().contains(entity)) {
				c.addReferent(entity);
			}
		}
		return c;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof ReferentCandidate) {
			return referents.equals(((ReferentCandidate) arg0).getReferents())
					&& getConfidence() == ((ReferentCandidate) arg0).getConfidence();
		} else if (arg0 instanceof Entity) {
			return getReferents().contains(arg0);
		}
		return false;
	}

	@Override
	public String toString() {
		String result = "[";
		for (Entity entity : referents) {
			result += entity.getName() + ",";
		}
		result += ":" + getConfidence() + "]";
		return result;
	}

}
