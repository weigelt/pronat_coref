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
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;
import edu.kit.ipd.parse.ner.NERTagger;
import edu.kit.ipd.parse.ontology_connection.Domain;
import edu.kit.ipd.parse.shallownlp.ShallowNLP;
import edu.kit.ipd.parse.srlabeler.SRLabeler;

public class ObjectIdentityTest {

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

	@Ignore
	@Test
	public void actionPossibleTest() {
		ppd = new PrePipelineData();
		String input = "Armar fill the cup and go to the table then pour me from it";
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
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
		} catch (MissingDataException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void leftRight() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge next to the cupboard then open the left dishwasher afterwards open the right dishwasher and close both again open the fridge and close the fridge";
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
			expected.add(new int[] { 14, 4 });
			expected.add(new int[] { 17, 14 });
			CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(context, expected), expected);
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
