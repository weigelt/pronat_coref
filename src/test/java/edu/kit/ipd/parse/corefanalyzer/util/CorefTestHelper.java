/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.kit.ipd.parse.conditionDetection.CommandType;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SpeakerEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ActionEntityRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.EntityEntityRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.ReferentRelation;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;

/**
 * @author Tobias Hey
 *
 */
public final class CorefTestHelper {

	public static HashMap<String, Text> texts;

	static {
		texts = new HashMap<String, Text>();
		try {
			File file = new File(CorefTestHelper.class.getResource("/korpus.xml").toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nl = doc.getElementsByTagName("text");
			for (int i = 0; i < nl.getLength(); i++) {
				Element node = (Element) nl.item(i);
				String name = node.getAttribute("name");
				String text = node.getTextContent().trim();
				List<int[]> refs = new ArrayList<>();
				NodeList coref = node.getElementsByTagName("coref");
				for (int j = 0; j < coref.getLength(); j++) {
					Element corefNode = (Element) coref.item(j);
					int[] ref = new int[] { Integer.parseInt(corefNode.getAttribute("start")),
							Integer.parseInt(corefNode.getAttribute("end")) };
					refs.add(ref);
				}
				texts.put(name, new Text(text, refs));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static TestResult checkCorefChains(Context context, List<int[]> refs) {
		int[] result = new int[3];
		Set<Relation> rels = new HashSet<>();
		List<String> failMessages = new ArrayList<>();
		int correct = 0;
		int wrong = 0;
		for (int[] relation : refs) {
			Entity start = getContainingEntity(context.getEntities(), relation[0]);
			Entity end = getContainingEntity(context.getEntities(), relation[1]);
			if (start == null) {
				failMessages.add(relation[0] + " is not contained in any Entity");
				continue;
			}
			if (end == null) {
				failMessages.add(relation[1] + " is not contained in any Entity");
				continue;
			}
			if (start.hasRelationsOfType(ReferentRelation.class)) {
				List<Relation> refRels = start.getRelationsOfType(ReferentRelation.class);
				List<ReferentRelation> referents = getMostLikelyRelation(refRels, start);
				boolean failed = true;
				for (ReferentRelation rel : referents) {
					if (rel.getStart().equals(start) && rel.getEnd().equals(end)) {
						failed = false;
					}
				}
				if (failed) {
					failMessages.add("Most likely Relations does not match expected: " + Arrays.deepToString(referents.toArray()) + " = "
							+ Arrays.toString(relation));
					rels.addAll(refRels);
					wrong++;
					continue;

				} else {
					rels.addAll(refRels);
				}

			} else {
				failMessages.add(start + " has no Referent Relations");
				continue;
			}
			correct++;

		}
		int tooMuch = 0;
		for (Entity entity : context.getEntities()) {
			List<Relation> relations = entity.getRelationsOfType(ReferentRelation.class);
			for (Relation relation : relations) {
				if (!(((ReferentRelation) relation).getEnd() instanceof SpeakerEntity)
						&& !((ReferentRelation) relation).getEnd().equals(entity)) {
					if (!rels.contains(relation)) {
						failMessages.add(relation + "was detected but not expected.");
						tooMuch++;
					}
				}
			}
		}
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + correct + "/" + refs.size() + " | Additionally Detected: " + tooMuch + "|");
		System.out.println("----------------------------------------------------");

		result[0] = correct;
		result[1] = tooMuch;
		result[2] = wrong;
		return new TestResult(result, failMessages);
	}

	private static List<ReferentRelation> getMostLikelyRelation(List<Relation> refRelations, Entity current) {
		List<ReferentRelation> result = new ArrayList<>();
		double confidence = 0;
		for (Relation rel : refRelations) {

			if (((ReferentRelation) rel).getConfidence() > confidence && ((ReferentRelation) rel).getStart().equals(current)) {
				confidence = ((ReferentRelation) rel).getConfidence();
			}
		}
		for (Relation rel : refRelations) {

			if (((ReferentRelation) rel).getConfidence() == confidence && ((ReferentRelation) rel).getStart().equals(current)) {
				result.add(((ReferentRelation) rel));
			}
		}
		return result;
	}

	private static Entity getContainingEntity(Set<Entity> entities, int position) {
		for (Entity entity : entities) {
			if (!(entity instanceof SpeakerEntity)) {
				for (INode node : entity.getReference()) {
					if (((int) node.getAttributeValue("position")) == position) {
						return entity;
					}
				}
			}
		}
		return null;
	}

	public static void printResult(TestResult result, List<int[]> expected) {
		System.out.println("----------------------------------------------------");
		System.out.println(
				"| Correct Relations: " + result.result[0] + "/" + expected.size() + " | Additionally Detected: " + result.result[1] + "|");
		System.out.println("----------------------------------------------------");
		if (!result.failMessages.isEmpty()) {
			String fail = "";
			for (String failmsg : result.failMessages) {
				System.out.println(failmsg);
				fail += failmsg + "; ";
			}
			Assert.fail(fail);
		}
		if (result.result[0] != expected.size()) {
			Assert.fail("Not all correct");
		}
	}

	public static void printOutRelations(Context context) {
		List<Relation> list = new ArrayList<>();
		for (Entity entity : context.getEntities()) {
			for (Relation rel : entity.getRelations()) {
				if (!list.contains(rel)) {
					list.add(rel);
				}
			}
		}
		Collections.sort(list, new Comparator<Relation>() {

			@Override
			public int compare(Relation o1, Relation o2) {
				int positionO1 = 0;
				if (o1 instanceof EntityEntityRelation) {
					positionO1 = (int) ((EntityEntityRelation) o1).getStart().getReference().get(0).getAttributeValue("position");
				} else if (o1 instanceof ActionEntityRelation) {
					positionO1 = (int) ((ActionEntityRelation) o1).getAction().getReference().get(0).getAttributeValue("position");

				}
				int positionO2 = 0;
				if (o2 instanceof EntityEntityRelation) {
					positionO2 = (int) ((EntityEntityRelation) o2).getStart().getReference().get(0).getAttributeValue("position");
				} else if (o2 instanceof ActionEntityRelation) {
					positionO2 = (int) ((ActionEntityRelation) o2).getAction().getReference().get(0).getAttributeValue("position");

				}

				return Integer.compare(positionO1, positionO2);
			}
		});
		for (Relation relation : list) {
			System.out.println(relation);
		}
		for (Action action : context.getActions()) {
			for (Relation relation : action.getRelations()) {
				if (!list.contains(relation)) {
					System.out.println(relation);
				}
			}
		}
	}

	public static void setCommands(IGraph graph, List<int[]> ifs, List<int[]> thens, List<int[]> elses) {
		Context context = Context.readFromGraph(graph);
		for (int[] If : ifs) {
			for (Entity entity : context.getEntities()) {
				boolean match = true;
				for (INode node : entity.getReference()) {
					if ((int) node.getAttributeValue("position") < If[0] || (int) node.getAttributeValue("position") > If[1]) {
						match = false;
					}
				}
				if (match) {
					entity.setCommandType(CommandType.IF_STATEMENT);
				}

			}
			for (Action action : context.getActions()) {
				boolean match = true;
				for (INode node : action.getReference()) {
					if ((int) node.getAttributeValue("position") < If[0] || (int) node.getAttributeValue("position") > If[1]) {
						match = false;
					}
				}
				if (match) {
					action.setCommandType(CommandType.IF_STATEMENT);
				}

			}
		}
		for (int[] then : thens) {
			for (Entity entity : context.getEntities()) {
				boolean match = true;
				for (INode node : entity.getReference()) {
					if ((int) node.getAttributeValue("position") < then[0] || (int) node.getAttributeValue("position") > then[1]) {
						match = false;
					}
				}
				if (match) {
					entity.setCommandType(CommandType.THEN_STATEMENT);
				}

			}
			for (Action action : context.getActions()) {
				boolean match = true;
				for (INode node : action.getReference()) {
					if ((int) node.getAttributeValue("position") < then[0] || (int) node.getAttributeValue("position") > then[1]) {
						match = false;
					}
				}
				if (match) {
					action.setCommandType(CommandType.THEN_STATEMENT);
				}

			}
		}
		for (int[] Else : elses) {
			for (Entity entity : context.getEntities()) {
				boolean match = true;
				for (INode node : entity.getReference()) {
					if ((int) node.getAttributeValue("position") < Else[0] || (int) node.getAttributeValue("position") > Else[1]) {
						match = false;
					}
				}
				if (match) {
					entity.setCommandType(CommandType.ELSE_STATEMENT);
				}

			}
			for (Action action : context.getActions()) {
				boolean match = true;
				for (INode node : action.getReference()) {
					if ((int) node.getAttributeValue("position") < Else[0] || (int) node.getAttributeValue("position") > Else[1]) {
						match = false;
					}
				}
				if (match) {
					action.setCommandType(CommandType.ELSE_STATEMENT);
				}

			}
		}
		context.printToGraph(graph);

	}

	public static class TestResult {
		public int[] result;
		public List<String> failMessages;

		public TestResult(int[] result, List<String> failMessages) {
			this.result = result;
			this.failMessages = failMessages;
		}
	}

}
