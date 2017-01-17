/**
 * 
 */
package edu.kit.ipd.parse.corefanalyzer;

import java.util.Properties;

import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.stanford.nlp.dcoref.Constants;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.pipeline.DefaultPaths;

/**
 * @author Tobias Hey
 *
 */
@MetaInfServices(AbstractAgent.class)
public class CorefAnalyzer extends AbstractAgent {

	private static final String ID = "corefAnalyzer";

	private BasicEntityRecognizer basicEntityRecog;
	Properties props;

	private Context context;

	private static final Logger logger = LoggerFactory.getLogger(CorefAnalyzer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.ipd.parse.luna.agent.LunaObserver#init()
	 */
	@Override
	public void init() {
		props = new Properties();
		props.setProperty(Constants.GENDER_NUMBER_PROP, DefaultPaths.DEFAULT_DCOREF_GENDER_NUMBER);
		Dictionaries stanfordDict = new Dictionaries(props);
		basicEntityRecog = new BasicEntityRecognizer(stanfordDict);
		props = ConfigManager.getConfiguration(CorefAnalyzer.class);
		setId(ID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.ipd.parse.luna.agent.AbstractAgent#exec()
	 */
	@Override
	protected void exec() {
		context = readContextFromGraph();
		ICorefAnalyzer analyzer;
		if (!context.isEmpty()) {
			logger.debug("Analyzing Coref with Context!");
			analyzer = new FullContextInfoAnalyzer(props);
			try {
				analyzer.analyze(graph, context);
			} catch (MissingDataException e) {
				// TODO exception handling
				e.printStackTrace();
			}
		} else {
			logger.debug("No Context existing using BasicEntityRecocnizer");
			try {
				basicEntityRecog.analyze(graph, context);
				analyzer = new FullContextInfoAnalyzer(props);
				analyzer.analyze(graph, context);
			} catch (MissingDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	/**
	 * Get the current context from the {@link IGraph}
	 * 
	 * @return the current context from the {@link IGraph}
	 */
	Context readContextFromGraph() {
		this.context = Context.readFromGraph(graph);
		return context;
	}

	/**
	 * Returns the current context representation
	 * 
	 * @return the current context representation
	 */
	Context getContext() {
		return context;
	}

}
