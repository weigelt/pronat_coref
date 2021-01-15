/**
 *
 */
package edu.kit.ipd.parse.corefanalyzer;

import java.util.Properties;

import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.contextanalyzer.data.Context;
import edu.kit.ipd.parse.contextanalyzer.data.entities.Entity;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * @author Tobias Hey
 *
 */
@MetaInfServices(AbstractAgent.class)
public class CorefAnalyzer extends AbstractAgent {

	private static final String ID = "corefAnalyzer";
	Properties props;

	private Context context;

	private static final Logger logger = LoggerFactory.getLogger(CorefAnalyzer.class);

	public CorefAnalyzer() {
		setId(ID);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.kit.ipd.parse.luna.agent.LunaObserver#init()
	 */
	@Override
	public void init() {
		props = new Properties();
		props = ConfigManager.getConfiguration(CorefAnalyzer.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.kit.ipd.parse.luna.agent.AbstractAgent#exec()
	 */
	@Override
	public void exec() {
		if (!checkMandatoryPreconditions()) {
			return;
		}
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
		}
	}

	private boolean checkMandatoryPreconditions() {
		if (graph.hasNodeType(Entity.ENTITY_NODE_TYPE)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the current context from the {@link IGraph}
	 *
	 * @return the current context from the {@link IGraph}
	 */
	Context readContextFromGraph() {
		context = Context.readFromGraph(graph);
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
