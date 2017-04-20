package edu.kit.ipd.parse.corefanalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.conditionDetection.ConditionDetector;
import edu.kit.ipd.parse.contextanalyzer.ContextAnalyzer;
import edu.kit.ipd.parse.contextanalyzer.data.AbstractConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Action;
import edu.kit.ipd.parse.contextanalyzer.data.ActionConcept;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.ObjectConcept;
import edu.kit.ipd.parse.contextanalyzer.data.State;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.ObjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.entities.SubjectEntity;
import edu.kit.ipd.parse.contextanalyzer.data.relations.Relation;
import edu.kit.ipd.parse.corefanalyzer.util.CorefTestHelper;
import edu.kit.ipd.parse.corefanalyzer.util.CorefTestHelper.TestResult;
import edu.kit.ipd.parse.corefanalyzer.util.Text;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.kit.ipd.parse.shallownlp.ShallowNLP;
import edu.kit.ipd.parse.srlabeler.SRLabeler;

public class CorpusEvaluation {

	ShallowNLP snlp;
	SRLabeler srLabeler;
	NERTagger nerTagger;
	ContextAnalyzer contextAnalyzer;
	CorefAnalyzer coref;
	ConditionDetector cond;
	GraphBuilder graphBuilder;
	PrePipelineData ppd;
	HashMap<String, Text> texts;
	private static final Logger logger = LoggerFactory.getLogger(CorpusEvaluation.class);
	private static Properties props;

	@Before
	public void setUp() {
		props = ConfigManager.getConfiguration(Domain.class);
		props.setProperty("ONTOLOGY_PATH", "/ontology.owl");
		props.setProperty("SYSTEM", "System");
		props.setProperty("METHOD", "Method");
		props.setProperty("PARAMETER", "Parameter");
		props.setProperty("DATATYPE", "DataType");
		props.setProperty("VALUE", "Value");
		props.setProperty("STATE", "State");
		props.setProperty("OBJECT", "Object");
		props.setProperty("SYSTEM_HAS_METHOD", "hasMethod");
		props.setProperty("STATE_ASSOCIATED_STATE", "associatedState");
		props.setProperty("STATE_ASSOCIATED_OBJECT", "associatedObject");
		props.setProperty("STATE_CHANGING_METHOD", "changingMethod");
		props.setProperty("METHOD_CHANGES_STATE", "changesStateTo");
		props.setProperty("METHOD_HAS_PARAMETER", "hasParameter");
		props.setProperty("OBJECT_HAS_STATE", "hasState");
		props.setProperty("OBJECT_SUB_OBJECT", "subObject");
		props.setProperty("OBJECT_SUPER_OBJECT", "superObject");
		props.setProperty("PARAMETER_OF_DATA_TYPE", "ofDataType");
		props.setProperty("DATATYPE_HAS_VALUE", "hasValue");
		props.setProperty("PRIMITIVE_TYPES", "String,int,double,float,short,char,boolean,long");
		graphBuilder = new GraphBuilder();
		graphBuilder.init();
		nerTagger = new NERTagger();
		nerTagger.init();
		srLabeler = new SRLabeler();
		srLabeler.init();
		snlp = new ShallowNLP();
		snlp.init();

		texts = CorefTestHelper.texts;
		contextAnalyzer = new ContextAnalyzer();
		contextAnalyzer.init();
		coref = new CorefAnalyzer();
		coref.init();
		cond = new ConditionDetector();
		cond.init();

	}

	@Test
	public void oneOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("1.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);

			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void oneTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("1.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);

			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void oneThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("1.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);

			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void twoOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("2.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);

			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void twoTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("2.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);

			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void twoThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("2.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void threeOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("3.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void threeTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("3.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void threeTwoMultiple() {
		ppd = new PrePipelineData();
		Text text = texts.get("3.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			Context prev = new Context();
			Context result = new Context();
			IGraph graph = ppd.getGraph();
			do {
				prev = result;
				contextAnalyzer.setGraph(graph);
				contextAnalyzer.exec();
				graph = contextAnalyzer.getGraph();

				coref.setGraph(graph);
				coref.exec();
				result = coref.getContext();
				System.out.println(result.getEntities());
				System.out.println(result.getActions());
				System.out.println(result.getConcepts());

			} while (!prev.equals(result));
			System.out.println(input);
			CorefTestHelper.printOutRelations(result);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}
	}

	@Test
	public void threeThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("3.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void fourOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("4.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void fourTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("4.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void fourThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("4.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void fiveOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("5.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void fiveTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("5.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void fiveThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("5.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void sixOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("6.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void sixTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("6.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void sixThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("6.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void sevenOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("7.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void sevenTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("7.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void sevenThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("7.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void eightOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("8.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void eightTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("8.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void eightThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("8.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void nineOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("9.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void nineTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("9.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void nineThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("9.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void tenOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("10.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void tenTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("10.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void tenThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("10.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void elevenOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("11.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void elevenTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("11.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void elevenThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("11.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void iffourOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourtwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourFour() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.4");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourFive() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.5");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourSix() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.6");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourSeven() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.7");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourEight() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.8");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffourNine() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.4.9");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	//@Ignore
	@Test
	public void iffiveOne() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.1");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveTwo() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.2");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveThree() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.3");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveFour() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.4");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveFive() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.5");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveSix() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.6");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveSeven() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.7");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveEight() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.8");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void iffiveNine() {
		ppd = new PrePipelineData();
		Text text = texts.get("if.5.9");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			executePrevAgents(ppd.getGraph());
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void multiple() {
		ppd = new PrePipelineData();
		Text text = texts.get("s6p05");
		String input = text.getText();
		List<int[]> expected = text.getRefs();
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
		executePrepipeline(ppd);
		try {
			Context prev = new Context();
			Context result = new Context();
			IGraph graph = ppd.getGraph();

			do {
				prev = result;
				contextAnalyzer.setGraph(graph);
				contextAnalyzer.exec();
				coref.setGraph(contextAnalyzer.getGraph());
				coref.exec();
				result = coref.getContext();
				graph = coref.getGraph();
				System.out.println(input);
				CorefTestHelper.printOutRelations(result);

			} while (!prev.equals(result));
			List<Entity> entities = Arrays.asList(result.getEntities().toArray(new Entity[result.getEntities().size()]));
			Collections.sort(entities);
			List<Action> actions = Arrays.asList(result.getActions().toArray(new Action[result.getActions().size()]));
			List<AbstractConcept> concepts = Arrays.asList(result.getConcepts().toArray(new AbstractConcept[result.getConcepts().size()]));
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void searchEntities() {
		Set<String> actions = new HashSet<>();
		Set<String> objects = new HashSet<>();
		Set<String> subjects = new HashSet<>();

		for (String id : texts.keySet()) {
			System.out.println(id);
			ppd = new PrePipelineData();
			Text text = texts.get(id);
			String input = text.getText();
			ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
			executePrepipeline(ppd);
			try {
				contextAnalyzer.setGraph(ppd.getGraph());
				contextAnalyzer.exec();
				coref.setGraph(contextAnalyzer.getGraph());
				coref.exec();
				Context context = coref.getContext();
				for (Action action : context.getActions()) {
					actions.add(action.getName());
				}
				for (edu.kit.ipd.parse.contextanalyzer.data.entities.Entity entity : context.getEntities()) {
					if (entity instanceof SubjectEntity) {
						subjects.add(entity.getName());
					} else if (entity instanceof ObjectEntity) {
						objects.add(entity.getName());
					}
				}

			} catch (MissingDataException e) {
				e.printStackTrace();
			}

		}

		System.out.println("Subjects: " + subjects.toString());
		System.out.println("Objects: " + objects.toString());
		System.out.println("Actions: " + actions.toString());
	}

	@Ignore
	@Test
	public void printSNLP() {
		for (String id : texts.keySet()) {
			if (id.startsWith("s")) {
				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id + ":");
				try {
					snlp.exec(ppd);
					System.out.println(ppd.getTaggedHypothesis(0));
				} catch (PipelineStageException | MissingDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Ignore
	@Test
	public void evalBasic() {
		int tp = 0;
		int overall = 0;
		int tooMuch = 0;
		int wrong = 0;
		List<String> failures = new ArrayList<>();
		for (String id : texts.keySet()) {
			if ((!id.startsWith("s")) && ((id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[2]) >= 10)
					|| (!id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[0]) >= 11))) {//) {
				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				List<int[]> expected = text.getRefs();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id);
				executePrepipeline(ppd);
				try {
					cond.setGraph(ppd.getGraph());
					cond.exec();
					coref.setGraph(cond.getGraph());
					coref.exec();
					Context context = coref.getContext();
					TestResult result = CorefTestHelper.checkCorefChains(context, expected);
					tp += result.result[0];
					overall += expected.size();
					tooMuch += result.result[1];
					wrong += result.result[2];
					for (String failure : result.failMessages) {
						failures.add(id + ": " + failure);
					}
				} catch (MissingDataException e) {
					e.printStackTrace();
				}

			}
		}

		double fp = tooMuch + wrong;
		double fn = overall - tp;
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + overall + " | Additionally Detected: " + tooMuch + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Ignore
	@Test
	public void evalMultipleNew() {
		int tp = 0;
		int overall = 0;
		int tooMuch = 0;
		int wrong = 0;
		List<String> failures = new ArrayList<>();
		for (String id : texts.keySet()) {
			//if (id.startsWith("s")) {

			ppd = new PrePipelineData();
			Text text = texts.get(id);
			String input = text.getText();
			List<int[]> expected = text.getRefs();
			ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
			System.out.println(id);
			executePrepipeline(ppd);

			try {
				Context prev = new Context();
				Context cResult = new Context();
				IGraph graph = ppd.getGraph();
				cond.setGraph(graph);
				cond.exec();
				graph = cond.getGraph();
				do {
					prev = cResult;
					contextAnalyzer.setGraph(graph);
					contextAnalyzer.exec();
					coref.setGraph(contextAnalyzer.getGraph());
					coref.exec();
					cResult = coref.getContext();
					graph = coref.getGraph();
					System.out.println(input);
					CorefTestHelper.printOutRelations(cResult);

				} while (!prev.equals(cResult));
				TestResult result = CorefTestHelper.checkCorefChains(cResult, expected);
				tp += result.result[0];
				overall += expected.size();
				tooMuch += result.result[1];
				wrong += result.result[2];
				for (String failure : result.failMessages) {
					failures.add(id + ": " + failure);
				}
			} catch (MissingDataException e) {
				e.printStackTrace();
			}

			//}
		}

		double fp = tooMuch + wrong;
		double fn = overall - tp;
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + overall + " | Additionally Detected: " + tooMuch + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Ignore
	@Test
	public void evalMultipleKorpusImpl() {
		int tp = 0;
		int overall = 0;
		int tooMuch = 0;
		int wrong = 0;
		List<String> failures = new ArrayList<>();
		for (String id : texts.keySet()) {

			if ((!id.startsWith("s")) && ((id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[2]) < 10)
					|| (!id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[0]) < 11))) {//) {

				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				List<int[]> expected = text.getRefs();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id);
				executePrepipeline(ppd);

				try {
					Context prev = new Context();
					Context cResult = new Context();
					IGraph graph = ppd.getGraph();
					cond.setGraph(graph);
					cond.exec();
					graph = cond.getGraph();
					do {
						prev = cResult;
						contextAnalyzer.setGraph(graph);
						contextAnalyzer.exec();
						coref.setGraph(contextAnalyzer.getGraph());
						coref.exec();
						cResult = coref.getContext();
						graph = coref.getGraph();
						System.out.println(input);
						CorefTestHelper.printOutRelations(cResult);

					} while (!prev.equals(cResult));
					TestResult result = CorefTestHelper.checkCorefChains(cResult, expected);
					tp += result.result[0];
					overall += expected.size();
					tooMuch += result.result[1];
					wrong += result.result[2];
					for (String failure : result.failMessages) {
						failures.add(id + ": " + failure);
					}
				} catch (MissingDataException e) {
					e.printStackTrace();
				}

			}
		}

		double fp = tooMuch + wrong;
		double fn = overall - tp;
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + overall + " | Additionally Detected: " + tooMuch + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Ignore
	@Test
	public void evalMultipleKorpusElse() {
		int tp = 0;
		int overall = 0;
		int tooMuch = 0;
		int wrong = 0;
		List<String> failures = new ArrayList<>();
		for (String id : texts.keySet()) {
			if ((!id.startsWith("s")) && ((id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[2]) >= 10)
					|| (!id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[0]) >= 11))) {//) {

				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				List<int[]> expected = text.getRefs();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id);
				executePrepipeline(ppd);

				try {
					Context prev = new Context();
					Context cResult = new Context();
					IGraph graph = ppd.getGraph();
					cond.setGraph(graph);
					cond.exec();
					graph = cond.getGraph();
					do {
						prev = cResult;
						contextAnalyzer.setGraph(graph);
						contextAnalyzer.exec();
						coref.setGraph(contextAnalyzer.getGraph());
						coref.exec();
						cResult = coref.getContext();
						graph = coref.getGraph();
						System.out.println(input);
						CorefTestHelper.printOutRelations(cResult);

					} while (!prev.equals(cResult));
					TestResult result = CorefTestHelper.checkCorefChains(cResult, expected);
					tp += result.result[0];
					overall += expected.size();
					tooMuch += result.result[1];
					wrong += result.result[2];
					for (String failure : result.failMessages) {
						failures.add(id + ": " + failure);
					}
				} catch (MissingDataException e) {
					e.printStackTrace();
				}

			}
		}

		double fp = tooMuch + wrong;
		double fn = overall - tp;
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double f1 = (2 * precision * recall) / (precision + recall);
		System.out.println("----------------------------------------------------");
		System.out.println("| Correct Relations: " + tp + "/" + overall + " | Additionally Detected: " + tooMuch + "|");
		System.out.println("| Precision = " + precision + "   Recall = " + recall + "  F1 = " + f1 + "|");
		System.out.println("----------------------------------------------------");
		for (String string : failures) {
			System.out.println(string);
		}
	}

	@Ignore
	@Test
	public void printActions() {

		for (String id : texts.keySet()) {
			if (id.startsWith("s")) {
				//if //((id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[2]) < 10)
				//(!id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[0]) < 22) {//) {

				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id);
				executePrepipeline(ppd);

				try {
					Context prev = new Context();
					Context cResult = new Context();
					IGraph graph = ppd.getGraph();
					cond.setGraph(graph);
					cond.exec();
					graph = cond.getGraph();
					do {
						prev = cResult;
						contextAnalyzer.setGraph(graph);
						contextAnalyzer.exec();
						coref.setGraph(contextAnalyzer.getGraph());
						coref.exec();
						cResult = coref.getContext();
						graph = coref.getGraph();
						System.out.println(input);

					} while (!prev.equals(cResult));
					List<Action> actions = Arrays.asList(cResult.getActions().toArray(new Action[cResult.getActions().size()]));
					Collections.sort(actions, new Comparator<Action>() {

						@Override
						public int compare(Action o1, Action o2) {
							int p1 = (int) o1.getReference().get(0).getAttributeValue("position");
							int p2 = (int) o2.getReference().get(0).getAttributeValue("position");
							return Integer.compare(p1, p2);
						}
					});
					String eol = System.getProperty("line.separator");
					String overrall = id + ":" + eol;
					for (Action action : actions) {
						String output = action.getName() + "[" + action.getReference().get(0).getAttributeValue("position") + "] <-- {";
						for (INode reference : action.getReference()) {
							output += reference.getAttributeValue("value") + "[" + reference.getAttributeValue("position") + "], ";
						}
						output += "} " + action.getPropBankRolesetID() + eol;
						for (Relation rel : action.getRelations()) {
							output += rel.toString() + eol;
						}
						overrall += output;
					}
					overrall += "--------";
					System.out.println(overrall);
					logger.info(overrall);
				} catch (MissingDataException e) {
					e.printStackTrace();
				}

			}
		}

	}

	@Ignore
	@Test
	public void printConcepts() {

		for (String id : texts.keySet()) {
			if (id.startsWith("s")) {
				//if //((id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[2]) < 10)
				//(!id.split("\\.")[0].equalsIgnoreCase("if") && Integer.parseInt(id.split("\\.")[0]) < 22) {//) {

				ppd = new PrePipelineData();
				Text text = texts.get(id);
				String input = text.getText();
				ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
				System.out.println(id);
				executePrepipeline(ppd);

				try {
					Context prev = new Context();
					Context cResult = new Context();
					IGraph graph = ppd.getGraph();
					cond.setGraph(graph);
					cond.exec();
					graph = cond.getGraph();
					do {
						prev = cResult;
						contextAnalyzer.setGraph(graph);
						contextAnalyzer.exec();
						coref.setGraph(contextAnalyzer.getGraph());
						coref.exec();
						cResult = coref.getContext();
						graph = coref.getGraph();
						System.out.println(input);

					} while (!prev.equals(cResult));

					String eol = System.getProperty("line.separator");
					String overrall = id + ":" + eol;
					for (AbstractConcept concept : cResult.getConcepts()) {
						String output = concept.getName() + "|" + concept.getOntologyIndividual() + "|";
						if (concept instanceof ObjectConcept) {
							output += "|" + ((ObjectConcept) concept).getIndexWordLemma();
						}
						if (concept instanceof ActionConcept) {
							output += "|" + ((ActionConcept) concept).getIndexWordLemma();
						}
						output += eol;
						output += "Equal: ";
						for (AbstractConcept e : concept.getEqualConcepts()) {
							output += e.getName() + " ";
						}
						output += eol;
						output += "Part: ";
						for (AbstractConcept e : concept.getPartConcepts()) {
							output += e.getName() + " ";
						}
						output += eol;
						output += "PartOf: ";
						for (AbstractConcept e : concept.getPartOfConcepts()) {
							output += e.getName() + " ";
						}
						output += eol;
						output += "Super: ";
						for (AbstractConcept e : concept.getSuperConcepts()) {
							output += e.getName() + " ";
						}
						output += eol;
						output += "Sub: ";
						for (AbstractConcept e : concept.getSubConcepts()) {
							output += e.getName() + " ";
						}
						output += eol;
						output += "Synonyms: ";
						for (String e : concept.getSynonyms()) {
							output += e + " ";
						}
						output += eol;
						if (concept instanceof State) {
							output += "AssState: ";
							for (AbstractConcept e : ((State) concept).getAssociatedStates()) {
								output += e.getName() + " ";
							}
						}
						if (concept instanceof ObjectConcept) {
							output += "States: ";
							for (AbstractConcept e : ((ObjectConcept) concept).getStates()) {
								output += e.getName() + " ";
							}
						}
						if (concept instanceof ActionConcept) {
							output += "Antonym: ";
							for (AbstractConcept e : ((ActionConcept) concept).getAntonymActions()) {
								output += e.getName() + " ";
							}

							output += eol + "StatesChangedTo: ";
							for (AbstractConcept e : ((ActionConcept) concept).getStatesChangedTo()) {
								output += e.getName() + " ";
							}
						}
						output += eol;
						for (Relation rel : concept.getRelations()) {
							output += rel.toString() + eol;
						}
						output += eol;
						overrall += output;
					}
					overrall += cResult.getConcepts().size() + eol + "--------";
					System.out.println(overrall);
					logger.info(overrall);
				} catch (MissingDataException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private void executePrevAgents(IGraph graph) {
		cond.setGraph(graph);
		cond.exec();
		contextAnalyzer.setGraph(graph);
		contextAnalyzer.exec();
	}

	private void executePrepipeline(PrePipelineData ppd) {
		try {
			snlp.exec(ppd);
			nerTagger.exec(ppd);
			srLabeler.exec(ppd);
			graphBuilder.exec(ppd);

		} catch (PipelineStageException e) {

			e.printStackTrace();
		}

	}

}
