package edu.kit.ipd.parse.corefanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.ContextAnalyzer;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.corefanalyzer.util.CorefTestHelper;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.kit.ipd.parse.shallownlp.ShallowNLP;
import edu.kit.ipd.parse.srlabeler.SRLabeler;

public class AnaphoraTest {

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
		contextAnalyzer = new ContextAnalyzer();
		contextAnalyzer.init();
		coref = new CorefAnalyzer();
		coref.init();

	}

	@Test
	public void groupTest() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge and cupboard and then to the two dishwashers then close them";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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

	@Ignore
	@Test
	public void diffTest() {
		ppd = new PrePipelineData();
		String input = "John close the cupboard then open the dishwasher and get the cup out of the dishwasher then close it again";
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
		ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input));
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
