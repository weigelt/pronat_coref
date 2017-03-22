/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.IContextAnalyzer;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.GrammaticalNumber;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity.DeterminerType;
import edu.kit.ipd.parse.contextanalyzer.data.entities.PronounEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity.Gender;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ConjunctionRelation;
import edu.kit.ipd.parse.contextanalyzer.util.ContextUtils;
import edu.kit.ipd.parse.contextanalyzer.util.GraphUtils;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.stanford.nlp.dcoref.Dictionaries;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * This {@link IContextAnalyzer} analyzes the current {@link IGraph} and
 * {@link Context} for occurring {@link Entity}s without building further
 * Context from WN
 * 
 * @author Tobias Hey
 *
 */
public class BasicEntityRecognizer {

	/**
	 * This enum represents the possible {@link Entity} Types
	 * 
	 * @author Tobias Hey
	 *
	 */
	private enum EntityType {
		PRONOUN, OBJECT, SUBJECT, SYSTEM, UNKNOWN
	}

	private IGraph graph;
	private Context currentContext;
	private Dictionaries stanfordDict;
	private static final Logger logger = LoggerFactory.getLogger(BasicEntityRecognizer.class);

	/**
	 * Constructs new {@link BasicEntityRecognizer}
	 * 
	 * @param dictionary
	 *            The WordNet {@link Dictionary}
	 * @param stanfordDict
	 *            The Stanford {@link Dictionaries}
	 * @param domain
	 *            The domainknowledge {@link Domain}
	 */
	public BasicEntityRecognizer(Dictionaries stanfordDict) {

		this.stanfordDict = stanfordDict;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.ipd.parse.contextanalyzer.IContextAnalyzer#analyze(edu.kit.ipd.
	 * parse.luna.graph.IGraph)
	 */
	public void analyze(IGraph graph, Context context) throws MissingDataException {
		logger.info("Starting Entity detection...");
		this.graph = graph;
		currentContext = context;
		List<INode> utteranceNodes = GraphUtils.getNodesOfUtterance(graph);
		for (Entity entity : getEntities(utteranceNodes)) {
			context.addEntity(entity);
		}
	}

	/**
	 * Returns all {@link Entity}s occurring in the specified utterance
	 * {@link INode}s and integrates new Information into possibly already
	 * detected {@link Entity}s in the specified {@link Context}
	 * 
	 * @param utteranceNodes
	 * @param context
	 * @return
	 */
	private List<Entity> getEntities(List<INode> utteranceNodes) {
		ArrayList<Entity> entities = new ArrayList<>();
		List<List<INode>> nounPhrases = getNounPhrases(utteranceNodes);
		nounPhrases.addAll(getPronounsNotInNP(utteranceNodes));

		for (List<INode> list : nounPhrases) {
			List<List<INode>> splittedNounPhrases = splitAtConjunction(list);
			List<String> conjunctions = getConjunctions(list);
			List<Entity> entitiesOfNounPhrase = new ArrayList<>();
			for (List<INode> phrase : splittedNounPhrases) {
				switch (getEntityType(phrase)) {
				case SYSTEM:
					entitiesOfNounPhrase.add(getSystemEntity(phrase));
					break;
				case SUBJECT:
					entitiesOfNounPhrase.add(getSubjectEntity(phrase));
					break;
				case PRONOUN:
					entitiesOfNounPhrase.add(getPronounEntity(phrase));
					break;
				case OBJECT:
					entitiesOfNounPhrase.add(getObjectEntity(phrase));
					break;
				default:
					break;
				}

			}
			List<Entity> entitiesForConjunction = new ArrayList<>();

			for (Entity npEntity : entitiesOfNounPhrase) {
				String name = npEntity.getName();
				List<INode> reference = npEntity.getReference();
				if (!currentContext.hasEntity(name, reference)) {
					entities.add(npEntity);
					entitiesForConjunction.add(npEntity);
				} else {

					Entity old = currentContext.getEntity(name, reference);
					if (old.getClass().isInstance(npEntity)) {
						if (old.integrateEntityInformation(npEntity)) {
							logger.debug("Already existing Entity found: Updating old Entity!");
						}
						entitiesForConjunction.add(old);
					} else {
						logger.debug("Already existing Entity found: Types doesn't match! Deleting old entity and copy references!");
						List<INode> ref = old.getReference();
						if (!npEntity.getReference().containsAll(ref)) {
							npEntity.setReference(ref);
						}
						currentContext.getEntities().remove(old);
						entities.add(npEntity);
						entitiesForConjunction.add(npEntity);
					}

				}
			}
			if (entitiesForConjunction.size() > 1) {
				setConjunctionRelations(entitiesForConjunction, conjunctions);
			}
		}
		Collections.sort(entities);
		logger.info(entities.size() + " new Entities detected.");
		return entities;

	}

	/**
	 * Returns the {@link ObjectEntity} contained in the specified phrase
	 * 
	 * @param phrase
	 * @return
	 */
	private Entity getObjectEntity(List<INode> phrase) {
		int mostLikelyConditionNumber = ContextUtils.getMostLikelyConditionNumber(phrase);
		String name = "";
		List<String> describingAdjectives = new ArrayList<>();
		List<String> possessivePronoun = new ArrayList<>();
		GrammaticalNumber gNumber = null;
		DeterminerType det = DeterminerType.UNKNOWN;
		String quantity = "UNKNOWN";
		List<INode> reference = new ArrayList<>();
		for (INode node : phrase) {
			String pos = (String) node.getAttributeValue("pos");
			if (isNoun(node)) {
				reference.add(node);
				name = determineName(name, node);
				gNumber = determineGrammaticalNumber(gNumber, quantity, pos);
			} else if (isAdjective(pos)) {
				reference.add(node);
				describingAdjectives.add((String) node.getAttributeValue("value"));
			} else if (isPossessivePronoun(pos)) {
				reference.add(node);
				possessivePronoun.add((String) node.getAttributeValue("value"));
				det = DeterminerType.SPECIFIC;
			} else if (isDeterminer(pos)) {
				reference.add(node);
				String lemma = (String) node.getAttributeValue("value");
				det = determineDeterminer(lemma);
			} else if (isCardinalNumber(pos)) {
				reference.add(node);
				String lemma = (String) node.getAttributeValue("value");
				// TODO lemmatize
				quantity = lemma;

			}
		}
		ObjectEntity entity = new ObjectEntity(name, gNumber, det, quantity, possessivePronoun, reference, describingAdjectives);
		entity.setCommandType(ContextUtils.getMostLikelyCmdType(phrase));
		entity.setStatement(mostLikelyConditionNumber);
		return entity;
	}

	/**
	 * Returns the {@link PronounEntity} contained in the specified phrase
	 * 
	 * @param phrase
	 * @return
	 */
	private Entity getPronounEntity(List<INode> phrase) {
		int mostLikelyConditionNumber = ContextUtils.getMostLikelyConditionNumber(phrase);
		String name = "";
		List<INode> reference = new ArrayList<>();
		for (INode node : phrase) {
			String pos = (String) node.getAttributeValue("pos");
			if (isPersonalPronoun(pos)) {
				name = determineName(name, node);
				reference.add(node);
			}
		}
		GrammaticalNumber gNumber = GrammaticalNumber.UNKNOWN;
		if (isSingularPronoun(name)) {
			gNumber = GrammaticalNumber.SINGULAR;
		} else {
			gNumber = GrammaticalNumber.PLURAL;
		}
		PronounEntity entity = new PronounEntity(name, gNumber, reference);
		entity.setCommandType(ContextUtils.getMostLikelyCmdType(phrase));
		entity.setStatement(mostLikelyConditionNumber);
		return entity;
	}

	/**
	 * Returns the {@link SubjectEntity} contained in the specified phrase
	 * 
	 * @param phrase
	 * @return
	 */
	private Entity getSubjectEntity(List<INode> phrase) {
		List<INode> nouns = new ArrayList<>();
		for (INode node : phrase) {
			if (isNoun(node) || isActingSubject(node)) {
				nouns.add(node);
			}
		}
		String name = "";
		int mostLikelyConditionNumber = ContextUtils.getMostLikelyConditionNumber(phrase);
		GrammaticalNumber gNumber = GrammaticalNumber.UNKNOWN;
		for (INode node : nouns) {
			if (isActingSubject(node)) {
				String pos = (String) node.getAttributeValue("pos");
				name = determineName(name, node);
				switch (pos) {
				case "NNP":
					gNumber = GrammaticalNumber.SINGULAR;
					break;
				case "NNPS":
					gNumber = GrammaticalNumber.PLURAL;

					break;
				case "NN":

					gNumber = GrammaticalNumber.MASS_OR_SINGULAR;
					break;
				case "NNS":
					gNumber = GrammaticalNumber.PLURAL;

					break;
				}
			}
		}
		SubjectEntity entity = new SubjectEntity(name, gNumber, phrase, getGender(name), false);
		entity.setCommandType(ContextUtils.getMostLikelyCmdType(phrase));
		entity.setStatement(mostLikelyConditionNumber);
		return entity;
	}

	/**
	 * Returns the {@link SubjectEntity} contained in the specified phrase
	 * 
	 * @param phrase
	 * @return
	 */
	private Entity getSystemEntity(List<INode> phrase) {
		Entity entity = null;
		String name = "";
		int mostLikelyConditionNumber = ContextUtils.getMostLikelyConditionNumber(phrase);
		for (INode node : phrase) {

			name = determineName(name, node);

		}
		entity = new SubjectEntity(name, GrammaticalNumber.SINGULAR, phrase, getGender(name), true);
		entity.setCommandType(ContextUtils.getMostLikelyCmdType(phrase));
		entity.setStatement(mostLikelyConditionNumber);
		return entity;
	}

	/**
	 * Returns the possible {@link EntityType} of the {@link Entity} in the
	 * specified Noun Phrase
	 * 
	 * @param phrase
	 * @return
	 */
	private EntityType getEntityType(List<INode> phrase) {
		if (containsPronoun(phrase)) {

			return EntityType.PRONOUN;

		}
		if (containsSubject(phrase)) {
			return EntityType.SUBJECT;
		} else if (containsNoun(phrase)) {
			return EntityType.OBJECT;
		}
		return EntityType.UNKNOWN;

	}

	private boolean containsPronoun(List<INode> phrase) {
		for (INode node : phrase) {
			if (isPersonalPronoun((String) node.getAttributeValue("pos"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the specified phrase contains a Noun
	 * 
	 * @param phrase
	 * @return
	 */
	private boolean containsNoun(List<INode> phrase) {
		for (INode node : phrase) {
			if (isNoun(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns all Conjunction in specified {@link INode}s
	 * 
	 * @param list
	 * @return
	 */
	private List<String> getConjunctions(List<INode> list) {
		List<String> result = new ArrayList<>();
		for (INode iNode : list) {
			if (((String) iNode.getAttributeValue("pos")).equals("CC")) {
				result.add((String) iNode.getAttributeValue("value"));
			}
		}
		return result;
	}

	/**
	 * Sets the existing {@link ConjunctionRelation}s between {@link Entity}s
	 * next to each other
	 * 
	 * @param entitiesOfNounPhrase
	 * @param conjunctions
	 */
	private void setConjunctionRelations(List<Entity> entitiesOfNounPhrase, List<String> conjunctions) {
		for (int i = 0; i < entitiesOfNounPhrase.size(); i++) {
			Entity entity = entitiesOfNounPhrase.get(i);
			if (i < entitiesOfNounPhrase.size() - 1) {
				Entity next = entitiesOfNounPhrase.get(i + 1);
				ConjunctionRelation rel = new ConjunctionRelation(conjunctions.get(i), entity, next);
				entity.addRelation(rel);
				next.addRelation(rel);
			}
		}

	}

	/**
	 * splits the specified Noun Phrase at any occurring conjunctions
	 * 
	 * @param phrase
	 * @return
	 */
	private List<List<INode>> splitAtConjunction(List<INode> phrase) {
		List<List<INode>> result = new ArrayList<>();
		int start = 0;
		for (int i = 0; i < phrase.size(); i++) {
			INode node = phrase.get(i);
			if (((String) node.getAttributeValue("pos")).equals("CC")) {
				result.add(phrase.subList(start, i));
				start = i + 1;
			}
		}
		result.add(phrase.subList(start, phrase.size()));
		return result;
	}

	private boolean isPossessivePronoun(String pos) {
		return pos.equals("PRP$");
	}

	/**
	 * Retruns the {@link DeterminerType} of the specified lemma
	 * 
	 * @param lemma
	 */
	private DeterminerType determineDeterminer(String lemma) {
		DeterminerType det;
		if (isGeneralDeterminer(lemma)) {
			det = DeterminerType.GENERAL;
		} else {
			det = DeterminerType.SPECIFIC;
		}
		return det;
	}

	/**
	 * @param lemma
	 * @return
	 */
	private boolean isGeneralDeterminer(String lemma) {
		return lemma.equalsIgnoreCase("a") || lemma.equalsIgnoreCase("an") || lemma.equalsIgnoreCase("any")
				|| lemma.equalsIgnoreCase("another") || lemma.equalsIgnoreCase("other") || lemma.equalsIgnoreCase("some")
				|| lemma.equalsIgnoreCase("whatever") || lemma.equalsIgnoreCase("whichever") || lemma.equalsIgnoreCase("many");
	}

	/**
	 * @param pos
	 * @return
	 */
	private boolean isPersonalPronoun(String pos) {
		return pos.equals("PRP");
	}

	/**
	 * Concatenates the specified name with the name of the specified
	 * {@link INode}
	 * 
	 * @param name
	 * @param node
	 * @return
	 */
	private String determineName(String name, INode node) {
		if (!name.isEmpty()) {
			name += " " + (String) node.getAttributeValue("value");

		} else {
			name += (String) node.getAttributeValue("value");

		}
		return name;
	}

	/**
	 * Retruns the {@link GrammaticalNumber} of the specified pos tag
	 * 
	 * @param gNumber
	 * @param quantity
	 * @param pos
	 * @return
	 */
	private GrammaticalNumber determineGrammaticalNumber(GrammaticalNumber gNumber, String quantity, String pos) {
		switch (pos) {
		case "NNP":
			gNumber = GrammaticalNumber.SINGULAR;
			break;
		case "NNPS":
			gNumber = GrammaticalNumber.PLURAL;
			if (quantity.equalsIgnoreCase("one")) {
				quantity = "MANY";
			}
			break;
		case "NN":
			if (quantity.equalsIgnoreCase("one")) {
				gNumber = GrammaticalNumber.SINGULAR;
			} else {
				gNumber = GrammaticalNumber.MASS_OR_SINGULAR;
			}
			break;
		case "NNS":
			gNumber = GrammaticalNumber.PLURAL;
			if (quantity.equalsIgnoreCase("one")) {
				quantity = "MANY";
			}
			break;
		}
		return gNumber;
	}

	/**
	 * Returns all {@link INode}s belonging to all noun phrases of the specified
	 * utterance {@link INode}s
	 * 
	 * @param utteranceNodes
	 * @return
	 */
	private List<List<INode>> getNounPhrases(List<INode> utteranceNodes) {
		ArrayList<List<INode>> result = new ArrayList<>();
		for (INode node : utteranceNodes) {
			String chunkIOB = (String) node.getAttributeValue("chunkIOB");
			if (chunkIOB.startsWith("B-NP")) {
				ArrayList<INode> phrase = new ArrayList<>();
				phrase.add(node);
				INode current = node;
				while ((current = GraphUtils.getNextNode(current, graph)) != null) {
					String chunkIOBNext = (String) current.getAttributeValue("chunkIOB");
					if (chunkIOBNext.startsWith("I-NP")) {
						phrase.add(current);
					} else {
						break;
					}
				}
				result.add(phrase);
			}
		}
		return result;
	}

	/**
	 * Returns all Pronouns not contained in any noun phrase
	 * 
	 * @param utteranceNodes
	 * @return
	 */
	private List<List<INode>> getPronounsNotInNP(List<INode> utteranceNodes) {
		ArrayList<List<INode>> result = new ArrayList<>();
		for (INode node : utteranceNodes) {
			String chunkIOB = (String) node.getAttributeValue("chunkIOB");

			if (!chunkIOB.endsWith("NP")) {
				String pos = (String) node.getAttributeValue("pos");
				if (pos.equals("PRP")) {
					ArrayList<INode> phrase = new ArrayList<>();
					phrase.add(node);
					result.add(phrase);
				}
			}
		}
		return result;
	}

	private boolean isCardinalNumber(String pos) {
		return pos.equals("CD");
	}

	private boolean isDeterminer(String pos) {
		return pos.equals("DT");
	}

	private boolean isSingularPronoun(String name) {
		return name.toLowerCase().startsWith("i") || name.toLowerCase().startsWith("you") || name.toLowerCase().startsWith("he")
				|| name.toLowerCase().startsWith("she") || name.toLowerCase().startsWith("it") || name.toLowerCase().startsWith("me")
				|| name.toLowerCase().startsWith("him") || name.toLowerCase().startsWith("her");
	}

	private boolean isAdjective(String pos) {
		return pos.equals("JJ") || pos.equals("JJS") || pos.equals("JJR") || pos.equals("VBN");
	}

	private boolean isActingSubject(INode node) {
		String ner = (String) node.getAttributeValue("ner");
		/*
		 * SRL produces more failures
		 * 
		 * Set<? extends IArc> srlArcs =
		 * node.getIncomingArcsOfType(graph.getArcType(SRLabeler.
		 * SRL_ARCTYPE_NAME)); for (IArc arc : srlArcs) { String
		 * verbNetRoleString = (String)
		 * arc.getAttributeValue(SRLabeler.VN_ROLE_NAME); List<String>
		 * verbNetRoles =
		 * GraphUtils.getListFromArrayToString(verbNetRoleString); if
		 * (verbNetRoles.contains("Agent") || verbNetRoles.contains("Actor")) {
		 * return true; } }
		 */
		if (ner != null) {
			return ner.equals("S-PER");
		} else {
			return false;
		}
	}

	private boolean containsSubject(List<INode> phrase) {
		for (INode node : phrase) {
			if (isActingSubject(node)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNoun(INode node) {
		String pos = (String) node.getAttributeValue("pos");
		return pos.equals("NNP") || pos.equals("NNPS") || pos.equals("NN") || pos.equals("NNS");
	}

	/**
	 * Analyzes the specified name of any hints for {@link Gender}
	 * 
	 * @param name
	 * @return
	 */
	private Gender getGender(String name) {
		List<String> split = Arrays.asList(name.split("\\s+"));
		char firstLetter = split.get(0).charAt(0);
		List<String> words = new ArrayList<>();
		for (String string : split) {
			words.add(string.toLowerCase());
		}
		int len = words.size();
		if (len > 1 && Character.isUpperCase(firstLetter)) {
			int firstNameIdx = len - 2;
			String secondToLast = words.get(firstNameIdx);
			if (firstNameIdx > 1 && (secondToLast.length() == 1 || (secondToLast.length() == 2 && secondToLast.endsWith(".")))) {
				firstNameIdx--;
			}

			for (int i = 0; i <= firstNameIdx; i++) {
				if (stanfordDict.genderNumber.containsKey(words.subList(i, len))) {
					return convertGender(stanfordDict.genderNumber.get(words.subList(i, len)));
				}
			}

			// find converted string with ! (e.g., "dr. martin luther king jr.
			// boulevard" -> "dr. !")
			List<String> convertedStr = new ArrayList<String>(2);
			convertedStr.add(words.get(firstNameIdx));
			convertedStr.add("!");
			if (stanfordDict.genderNumber.containsKey(convertedStr)) {
				return convertGender(stanfordDict.genderNumber.get(convertedStr));
			}

			if (stanfordDict.genderNumber.containsKey(words.subList(firstNameIdx, firstNameIdx + 1))) {
				return convertGender(stanfordDict.genderNumber.get(words.subList(firstNameIdx, firstNameIdx + 1)));
			}
		}

		if (words.size() > 0 && stanfordDict.genderNumber.containsKey(words.subList(len - 1, len))) {
			return convertGender(stanfordDict.genderNumber.get(words.subList(len - 1, len)));
		}
		return Gender.UNKNOWN;

	}

	private Gender convertGender(edu.stanford.nlp.dcoref.Dictionaries.Gender gender) {
		if (Objects.equals(gender.name(), Gender.MALE.name())) {
			return Gender.MALE;
		} else if (Objects.equals(gender.name(), Gender.FEMALE.name())) {
			return Gender.FEMALE;
		} else if (Objects.equals(gender.name(), Gender.NEUTRAL.name())) {
			return Gender.NEUTRAL;
		} else {
			return Gender.UNKNOWN;
		}
	}
}
