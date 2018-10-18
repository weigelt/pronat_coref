/**
 *
 */
package edu.kit.ipd.parse.corefanalyzer.data;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;

/**
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
		this.referents = new ArrayList<Entity>();
		this.referents.add(referent);
		this.confidence = confidence;
	}

	/**
	 * @param referent
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
		this.referents.add(referent);
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
		return this.getCandidate().compareTo(o.getCandidate());
	}

	@Override
	public ReferentCandidate clone() {
		ReferentCandidate c = new ReferentCandidate(this.getCandidate(), this.getConfidence());
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
			return this.referents.equals(((ReferentCandidate) arg0).getReferents())
					&& this.getConfidence() == ((ReferentCandidate) arg0).getConfidence();
		} else if (arg0 instanceof Entity) {
			return this.getReferents().contains(arg0);
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
