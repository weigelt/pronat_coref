package edu.kit.ipd.pronat.coref.resolution;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public enum PronounType {
	SPEAKER, SUBJECT, OBJECT, GROUP;

	public static PronounType getType(String pronoun) {
		if (SpeakerAnaphora.firstPersonPronounsSingular.contains(pronoun.toLowerCase())
				|| SpeakerAnaphora.firstPersonPronounsPlural.contains(pronoun.toLowerCase())) {
			return SPEAKER;
		} else if (SubjectAnaphora.secondPersonPronouns.contains(pronoun.toLowerCase())
				|| SubjectAnaphora.femalePronouns.contains(pronoun.toLowerCase())
				|| SubjectAnaphora.malePronouns.contains(pronoun.toLowerCase())) {
			return SUBJECT;
		} else if (GroupAnaphora.groupPronouns.contains(pronoun.toLowerCase())) {
			return GROUP;
		} else if (ObjectAnaphora.objectPronouns.contains(pronoun.toLowerCase())) {
			return OBJECT;
		}
		return null;
	}
}