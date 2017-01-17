/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.util;

import edu.kit.ipd.parse.conditionDetection.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;
import info.debatty.java.stringsimilarity.JaroWinkler;

/**
 * @author Tobias Hey
 *
 */
public final class MatchingUtils {

	private static final Double jwSimilarityThreshold = 0.92;
	private static final JaroWinkler jaroWinkler = new JaroWinkler();

	public static boolean genderMatches(Gender current, Gender candidate) {
		boolean result = false;
		if (current.equals(candidate)) {
			result = true;
		} else if (current.equals(Gender.UNKNOWN) || candidate.equals(Gender.UNKNOWN)) {
			result = true;
		}
		return result;
	}

	public static boolean grammaticalNumberMatches(GrammaticalNumber current, GrammaticalNumber candidate) {
		boolean result = false;
		if (current.equals(candidate)) {
			result = true;
		} else if (current.equals(GrammaticalNumber.UNKNOWN) || candidate.equals(GrammaticalNumber.UNKNOWN)) {
			result = true;
		} else if (current.equals(GrammaticalNumber.MASS_OR_SINGULAR) && candidate.equals(GrammaticalNumber.SINGULAR)) {
			result = true;
		} else if (current.equals(GrammaticalNumber.SINGULAR) && candidate.equals(GrammaticalNumber.MASS_OR_SINGULAR)) {
			result = true;
		}
		return result;
	}

	public static boolean isInAlternativeStatementBlock(Entity current, Entity candidate) {
		if (current.getStatement() == candidate.getStatement()) {
			if (current.getCommandType().equals(CommandType.ELSE_STATEMENT)
					&& candidate.getCommandType().equals(CommandType.THEN_STATEMENT)) {
				return true;
			} else if (current.getCommandType().equals(CommandType.THEN_STATEMENT)
					&& candidate.getCommandType().equals(CommandType.ELSE_STATEMENT)) {
				return true;
			}
		}
		return false;
	}

	public static boolean nameMatches(String current, String candidate) {
		double similarity = jaroWinkler.similarity(current, candidate);
		if (similarity > jwSimilarityThreshold) {
			return true;
		}
		return false;
	}

	public static boolean quantityMatches(String current, String candidate) {
		boolean result = false;
		if (current.equals(candidate)) {
			result = true;
		} else if (current.equals("UNKNOWN") || candidate.equals("UNKNOWN")) {
			result = true;
		}
		return result;
	}

}
