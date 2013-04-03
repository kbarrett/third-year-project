package ch.idsia.agents.controllers.kbarrett.second;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;
import ch.idsia.scenarios.Play;
/**
 * Loads a {@link SecondAgent} population and runs a randomly chosen member to play through a level.
 * @author Kim Barrett
 */
public class SecondAgentRunner
{
	/**
	 * The file in which the population is stored.
	 */
	private static final String savefilename = "src/ch/idsia/agents/controllers/kbarrett/savedfile.saf";
	/**
	 * @param args - must contain "-ag ch.idsia.agents.controllers.kbarrett.second.Second".
	 * @throws JDOMException - thrown when an error occurs reading in the file.
	 * @throws IOException - thrown when an error occurs reading in the file.
	 */
	public static void main(String[] args) throws JDOMException, IOException
	{
		//These are necessary to load in the population.
		SecondAgentEvolver evolver = new SecondAgentEvolver(args);
		GeneticAlgorithm<SecondAgent> algorithm = new GeneticAlgorithm<SecondAgent>(evolver);
		
		//Load the population and store it
		algorithm.loadThisGeneration(savefilename);
		List<SecondAgent> population = algorithm.getCurrentGeneration();
		
		//Analyse the given population - this prints out the average probability.
		Analyser.main(args);
		
		//Give the SecondAgentManager all of the data.
		SecondAgentManager.initialise(evolver.getSizeOfGeneration());
		SecondAgentManager.setValues(population);
		
		//Choose which agent to run.
		int chosen = (int)(evolver.getSizeOfGeneration() * Math.random());
		
		//Iterate until SecondAgentManager is set up to run the chosen agent.
		for(int i = 0; i < chosen; ++i)
		{
			SecondAgentManager.getNextAgentNumber();
		}
		
		//Print out the probabilities used by the chosen agent.
		System.out.println("jump: " + SecondAgentManager.getProbabilityJump(chosen));
		System.out.println("right: " + SecondAgentManager.getProbabilityMoveRight(chosen));
		System.out.println("shoot: " + SecondAgentManager.getProbabilityShoot(chosen));
		System.out.println("run: " + SecondAgentManager.getProbabilityRun(chosen));
		
		//Run the agent on the given level.
		Play.main(args);
	}

}
