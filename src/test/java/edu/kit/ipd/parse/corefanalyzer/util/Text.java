/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.util;

import java.util.List;

/**
 * @author Tobias Hey
 *
 */
public class Text {
	private String text;
	private List<int[]> refs;

	public Text(String text, List<int[]> refs) {
		this.setText(text);
		this.setRefs(refs);
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the refs
	 */
	public List<int[]> getRefs() {
		return refs;
	}

	/**
	 * @param refs
	 *            the refs to set
	 */
	public void setRefs(List<int[]> refs) {
		this.refs = refs;
	}
}