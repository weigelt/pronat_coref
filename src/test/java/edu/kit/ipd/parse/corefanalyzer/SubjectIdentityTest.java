package edu.kit.ipd.parse.corefanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.parse.contextanalyzer.ContextAnalyzer;
import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.corefanalyzer.util.CorefTestHelper;
import edu.kit.ipd.parse.corefanalyzer.util.Text;
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

public class SubjectIdentityTest {

	ShallowNLP snlp;
	SRLabeler srLabeler;
	NERTagger nerTagger;
	ContextAnalyzer contextAnalyzer;
	CorefAnalyzer coref;
	GraphBuilder graphBuilder;
	PrePipelineData ppd;
	HashMap<String, Text> texts;
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

	}

	@Test
	public void subjectIdentity() {
		ppd = new PrePipelineData();
		String input = "Armar go to the fridge next to Mary and John then go to Mary and afterwards Armar could you open it";
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
			expected.add(new int[] { 12, 7 });
			expected.add(new int[] { 7, 0 });
			expected.add(new int[] { 8, 7 });
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
