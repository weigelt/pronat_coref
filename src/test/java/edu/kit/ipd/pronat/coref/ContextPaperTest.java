/**
 *
 */
package edu.kit.ipd.pronat.coref;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.kit.ipd.parse.ontology_connection.Domain;
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
import org.junit.Test;

import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * @author Sebastian Weigelt
 * @author Tobias Hey
 *
 */
public class ContextPaperTest {

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

	@Test
	public void example1() {
		String input = "Open the cupboard Take the cup and close it";
		List<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 8, 2 });

		Context result = execute(input);

		CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);

	}

	@Test
	public void example2() {

		String input = "There is a tumbler on the table Take the glass and bring it to me";
		List<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 9, 3 });
		expected.add(new int[] { 12, 9 });

		Context result = execute(input);

		CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);

	}

	@Test
	public void example3() {

		String input = "To prepare meringue take egg white and powdered sugar and lemon extract Put all the ingredients into the bowl";
		List<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 15, 5 });
		expected.add(new int[] { 15, 8 });
		expected.add(new int[] { 15, 11 });

		Context result = execute(input);

		CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);

	}

	@Test
	public void example4() {

		String input = "Close the fridge open the dishwasher Then close all open appliances";
		List<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 10, 5 });

		Context result = execute(input);

		CorefTestHelper.printResult(CorefTestHelper.checkCorefChains(result, expected), expected);

	}

	private Context execute(String input) {
		ppd = new PrePipelineData();

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
			return result;
		} catch (MissingDataException e) {
			e.printStackTrace();
		}
		return null;
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
