package edu.kit.ipd.pronat.coref;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.kit.ipd.pronat.context.ContextAnalyzer;
import edu.kit.ipd.pronat.context.data.Context;
import edu.kit.ipd.pronat.coref.util.CorefTestHelper;
import edu.kit.ipd.pronat.graph_builder.GraphBuilder;
import edu.kit.ipd.pronat.ner.NERTagger;
import edu.kit.ipd.pronat.prepipedatamodel.PrePipelineData;
import edu.kit.ipd.pronat.prepipedatamodel.tools.StringToHypothesis;
import edu.kit.ipd.pronat.shallow_nlp.ShallowNLP;
import edu.kit.ipd.pronat.srl.SRLabeler;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.ontology_connection.Domain;

public class DevelopmentTests {

	private static ShallowNLP snlp;
	private static SRLabeler srLabeler;
	private static NERTagger nerTagger;
	private static ContextAnalyzer contextAnalyzer;
	private static CorefAnalyzer coref;
	private static GraphBuilder graphBuilder;
	private PrePipelineData ppd;
	private static Properties props;

	@BeforeClass
	public static void setUpClass() {
		props = ConfigManager.getConfiguration(Domain.class);
		props.setProperty("ONTOLOGY_PATH", "/vamos_ontology.owl");
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
		contextAnalyzer = new ContextAnalyzer();
		contextAnalyzer.init();
		coref = new CorefAnalyzer();
		coref.init();

	}

	// --------------------------------------------------
	// Object Identity Tests:
	// --------------------------------------------------

	@Ignore
	@Test
	public void actionPossibleTest() {
		ppd = new PrePipelineData();
		String input = "Armar fill the cup and go to the table then pour me from it";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 13, 3 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void hypernymDerivationTest() {
		ppd = new PrePipelineData();
		String input = "Armar go to the dishwasher next to the fridge";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void leftRight() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge next to the cupboard then open the left dishwasher afterwards open the right dishwasher and close both again open the fridge and close the fridge";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 25, 3 });
			expected.add(new int[] { 29, 25 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void hypernym() {
		ppd = new PrePipelineData();
		String input = "Armar bring me the lemon juice in front of the green cup then take the cup and fill it with the juice";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 21, 5 });
			expected.add(new int[] { 15, 11 });
			expected.add(new int[] { 18, 15 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void synonym() {
		ppd = new PrePipelineData();
		String input = "Armar bring me the water in front of the green cup then take the cup and fill it with the H2O";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 20, 4 });
			expected.add(new int[] { 14, 10 });
			expected.add(new int[] { 17, 14 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void possessivePronoun() {
		ppd = new PrePipelineData();
		String input = "John go to the fridge next to the two cupboards and open it then close its door again afterwards clean their doors and bring me your cup";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 12, 4 });
			expected.add(new int[] { 15, 12 });
			expected.add(new int[] { 25, 0 });
			expected.add(new int[] { 20, 7 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void relClause() {
		ppd = new PrePipelineData();
		String input = "John go to the fridge which is next to the two cupboards and open it then close its door again";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 14, 4 });
			expected.add(new int[] { 17, 14 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	// --------------------------------------------------
	// Subject Identity Tests:
	// --------------------------------------------------

	@Test
	public void subjectIdentity() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge next to Mary and John then go to Mary and afterwards Armar could you open it";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 13, 7 });
			expected.add(new int[] { 16, 0 });
			expected.add(new int[] { 18, 16 });
			expected.add(new int[] { 20, 4 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	//TODO resolve smith entity
	@Ignore
	@Test
	public void diffIdentity() {
		ppd = new PrePipelineData();
		String input = "Mary stands next to Mr. Smith tell her she should go to John Smith ";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 12, 7 });
			expected.add(new int[] { 7, 0 });
			expected.add(new int[] { 8, 7 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void groupTest() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge and cupboard and then to the two dishwashers then close them";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 15, 10 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void superObject() {
		ppd = new PrePipelineData();
		String input = "John go to the small fridge next to the two cupboards and open it then close the door again.";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 13, 5 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void groupTest2() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge and cupboard and then to the dishwasher then close them";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 14, 3 });
			expected.add(new int[] { 14, 6 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void groupTest3() {
		ppd = new PrePipelineData();
		String input = "Armar bring me the orange juice and the cup then put them both on the table";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 11, 5 });
			expected.add(new int[] { 11, 8 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void subjectGroup() {
		ppd = new PrePipelineData();
		String input = "John and Mary are in the kitchen bring them the dishes";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 8, 0 });
			expected.add(new int[] { 8, 2 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void subjectGroupLocative() {
		ppd = new PrePipelineData();
		String input = "John and Mary next to the fridge and cupboard want the plate bring them the plate";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 13, 0 });
			expected.add(new int[] { 13, 2 });
			expected.add(new int[] { 15, 11 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	// --------------------------------------------------
	// Anaphora Tests:
	// --------------------------------------------------

	@Ignore
	@Test
	public void diffTest() {
		ppd = new PrePipelineData();
		String input = "John close the cupboard then open the dishwasher and get the cup out of the dishwasher then close it again";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 11, 4 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);

		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void diffTest2() {
		ppd = new PrePipelineData();
		String input = "Armar please go to the dishwasher and open it then go to the table take the glass and put it into the dishwasher after you have closed its door again go to the fridge";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
		try {
			contextAnalyzer.setGraph(ppd.getGraph());
			contextAnalyzer.exec();
			coref.setGraph(contextAnalyzer.getGraph());
			coref.exec();
			Context context = coref.getContext();
			System.out.println(input);
			CorefTestHelper.printOutRelations(context);
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 8, 5 });
			expected.add(new int[] { 19, 16 });
			expected.add(new int[] { 22, 5 });
			expected.add(new int[] { 24, 0 });
			expected.add(new int[] { 28, 22 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void multiple() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge and open it and close it again then go to the dishwasher and open the fridge";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
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
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 7, 4 });
			expected.add(new int[] { 10, 7 });
			expected.add(new int[] { 20, 4 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void multiplePossessive() {
		ppd = new PrePipelineData();
		String input = "Please go to the fridge and open its door and take the cup out then close its door again";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
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
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 7, 4 });
			expected.add(new int[] { 10, 7 });
			expected.add(new int[] { 20, 4 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void multipleAdjective() {
		ppd = new PrePipelineData();
		String input = "Take the dirty cup fill it and bring it to me then take the empty cup";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
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
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 5, 3 });
			expected.add(new int[] { 8, 5 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void multipleScenario1() {
		ppd = new PrePipelineData();
		String input = "Armar get the green cup from the table next to the popcorn and go to the fridge then open the fridge and take the water out of it afterwards fill the cup with water and bring it to me then take the red cups out of the dishwasher and put them in the cupboard";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
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
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 20, 16 });
			expected.add(new int[] { 27, 20 });
			expected.add(new int[] { 31, 4 });
			expected.add(new int[] { 33, 24 });
			expected.add(new int[] { 36, 31 });
			expected.add(new int[] { 50, 43 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	@Ignore
	@Test
	public void multipleScenario2() {
		ppd = new PrePipelineData();
		String input = "Armar take a plate out of the dishwasher and put it on the table then open the fridge and take the instant meal out of it afterwards put the meal on the plate and put it into the microwave when it is finished put the plate on the table";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, false));
		executeSNLPandSRLandNER(ppd);
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
			List<int[]> expected = new ArrayList<>();
			expected.add(new int[] { 10, 3 });
			expected.add(new int[] { 25, 17 });
			expected.add(new int[] { 29, 22 });
			expected.add(new int[] { 32, 3 });
			expected.add(new int[] { 35, 28 });
			expected.add(new int[] { 40, 38 });
			expected.add(new int[] { 45, 32 });
			expected.add(new int[] { 48, 13 });

			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);
		} catch (MissingDataException e) {
			e.printStackTrace();
		}

	}

	private void executeSNLPandSRLandNER(PrePipelineData ppd) {
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
