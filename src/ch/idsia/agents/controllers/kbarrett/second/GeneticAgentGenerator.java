package ch.idsia.agents.controllers.kbarrett.second;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import ch.idsia.agents.controllers.kbarrett.Evolver;
import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;
import ch.idsia.scenarios.Play;

/**
 * Runs the genetic algorithm on a population of SecondAgents.
 * @author Kim Barrett
 * @see SecondAgent
 * @see GeneticAlgorithm
 */
public class GeneticAgentGenerator
{
	/**
	 * The file in which the {@link SecondAgent} population is saved.
	 */
	private static final String savefilename = "src/ch/idsia/agents/controllers/kbarrett/savedfile.saf";
	/**
	 * The number of the agent that is currently being evaluated.
	 */
	public static int currentAgent = 0;
	/**
	 * The number of generations run by each call to main.
	 */
	private static int numberOfGenerations = 5;
	
	/**
	 * Runs the genetic algorithm by loading in the data in the file ({@value #savefilename}).
	 * If no data is present a hard-coded initial population is used. Once 
	 * {@value #numberOfGenerations} generations have been completed the new generation is 
	 * saved back into the given file.
	 * @param args - this must contain "-ag ch.idsia.agents.controllers.kbarrett.second.SecondAgent"
	 */
	public static void main(final String[] args)
	{
		SecondAgentEvolver evolver = new SecondAgentEvolver(args);
		
		//This prepares SecondAgentManager to be used.
		SecondAgentManager.initialise(evolver.getSizeOfGeneration());
		
		//The current generation will be stored here.
		List<SecondAgent> population = new LinkedList<SecondAgent>();
		
		GeneticAlgorithm<SecondAgent> algorithm = new GeneticAlgorithm<SecondAgent>(evolver);
		
		//Either load in a file, or create an initial population.
		try
		{
			//Use the algorithm's method to load the file
			algorithm.loadThisGeneration(savefilename);
			//Get the initial population from the algorithm - used to give the values to SecondAgentManager
			population = algorithm.getCurrentGeneration();
		}
		catch(Exception e)
		//If the file doesn't exist, is empty or an error occurs reading it.
		{
			System.out.println("File empty, creating initial population.");
			//Create initial population
			population.add(new SecondAgent(0, 0.6f, 0.6f, 0.6f, 0.6f));
			population.add(new SecondAgent(1, 1f, 0.6f, 0.1f, 0.4f));
			population.add(new SecondAgent(2, 1f, 0.1f, 0.2f, 0.3f));
			population.add(new SecondAgent(3, 0.1f, 1f, 1f, 1f));
			population.add(new SecondAgent(4, 0.5f, 0.5f, 0.5f, 0.5f));
			//Give this to the algorithm to use for its next iteration.
			algorithm.giveInitialPopulation(population);
		}
		
		//Runs the algorithm for the correct number of generations
		for(int i = 0; i < numberOfGenerations; ++i)
		{
			System.out.println("Iteration " + i);
			/*
			 * Give the values of the population to the SecondAgentManager - these are used to create
			 * the agents for evaluation (as Agents cannot be directly passed to Play.main()).
			 */
			SecondAgentManager.setValues(population);
			//Run the genetic algorithm. False => no multi-threading is required, as Play.main() isn't thread-safe.
			population = algorithm.getNewGeneration(false);
			//Zero the values that SecondAgentManager contains ready for the next generation.
			SecondAgentManager.resetNumbers();
		}
		
		//Save the final generation.
		algorithm.saveThisGeneration(savefilename);
		
		//Exit the program - used to close the window created by Play.main().
		System.exit(0);
	}
	/**
	 * Returns {@value #savefilename}.
	 * @return string representation of the file into which the SecondAgent population is saved.
	 */
	public static String getSavedLocation()
	{
		return savefilename;
	}

}
/**
 * The Evolver used by {@link GeneticAgentGenerator} to evolve {@link SecondAgent}s using the {@link GeneticAlgorithm}.
 * @author Kim Barrett
 */
class SecondAgentEvolver implements Evolver<SecondAgent>
{
	/**	
	 * Used by {@link Play#main} to load the correct agent. 
	 * Must contain "-ag ch.idsia.agents.controllers.kbarrett.second.SecondAgent".
	 */
	private String[] args;
	/**
	 * The weight used crossover function when taking the weighted average.
	 */
	private static final float weight = 0.3f;
	/**
	 * The agent currently being crossed over.
	 */
	private int currentAgent = 0;
	/**
	 * @param args - must contain "-ag ch.idsia.agents.controllers.kbarrett.second.SecondAgent".
	 * @see #args
	 */
	public SecondAgentEvolver(String[] args)
	{
		this.args = args;
	}
	/**
	 * Runs {@link Play#main} using the current agent. This is done by taking the probabilities 
	 * associated with this element's number in {@link SecondAgentManager}, as values cannot be
	 * directly passed to or retrieved from {@link Play}.
	 */
	@Override
	public int fitnessFunction(SecondAgent element)
	{
		//Runs the program with the given agent 
		Play.main(args);
		/*
		 * Gets the score achieved by this agent on the level back from SecondAgentManager and 
		 * returns it to the algorithm.
		 */
		return (int)SecondAgentManager.getFitness(element.getAgentNumber());
	}
	/**
	 * Takes a weighted average (using weight of {@value #weight}) of each of the probabilities 
	 * and using them as the probabilities for the children.
	 */
	@Override
	public List<SecondAgent> crossover(SecondAgent element1, SecondAgent element2)
	{
		System.out.println("Crossing over " + element1 + " & " + element2);
		/*
		 * Takes each of the weighted probabilities.
		 */
		float probJump1 = (element1.getProbabilityJump() * weight + element2.getProbabilityJump() * (1-weight));
		float probJump2 = (element2.getProbabilityJump() * weight + element1.getProbabilityJump() * (1-weight));
		float probRight1 = (element1.getProbabilityMoveRight() * weight + element2.getProbabilityMoveRight() * (1-weight));
		float probRight2 = (element2.getProbabilityMoveRight() * weight + element1.getProbabilityMoveRight() * (1-weight));
		float probRun1 = (element1.getProbabilityRun() * weight + element2.getProbabilityRun() * (1-weight));
		float probRun2 = (element2.getProbabilityRun() * weight + element1.getProbabilityRun() * (1-weight));
		float probShoot1 = (element1.getProbabilityShoot() * weight + element2.getProbabilityShoot() * (1-weight));
		float probShoot2 = (element2.getProbabilityShoot() * weight + element1.getProbabilityShoot() * (1-weight));
		/*
		 * Creates the children. Iterates through the value for currentAgent - so each agent in the new generation has a unique ID.
		 */
		SecondAgent newAgent1 = new SecondAgent(currentAgent++, probJump1, probRight1, probShoot1, probRun1);
		SecondAgent newAgent2 = new SecondAgent(currentAgent++, probJump2, probRight2, probShoot2, probRun2);
		//If this is the last pair, reset the value of currentAgent for the next generation.
		if(currentAgent == getSizeOfGeneration())
		{
			currentAgent = 0;
		}
		//Return the children.
		ArrayList<SecondAgent> returnValue = new ArrayList<SecondAgent>(2);
		returnValue.add(newAgent1);
		returnValue.add(newAgent2);
		return returnValue;
	}
	/**
	 * Adds or subtracts a random number from each of the probabilities of the given agent.
	 */
	@Override
	public void mutate(SecondAgent element)
	{
		System.out.println("Mutating " + element);
		//Choosing whether to add or subtract for jump probability
		if(Math.random() < 0.5)
		{
			element.setProbabilityJump(element.getProbabilityJump() + (float)Math.random());
		}
		else
		{
			element.setProbabilityJump(element.getProbabilityJump() - (float)Math.random());
		}
		//Choosing whether to add or subtract for move right probability
		if(Math.random() < 0.5)
		{
			element.setProbabilityMoveRight(element.getProbabilityMoveRight() + (float)Math.random());
		}
		else
		{
			element.setProbabilityMoveRight(element.getProbabilityMoveRight() - (float)Math.random());
		}
		//Choosing whether to add or subtract for shoot probability
		if(Math.random() < 0.5)
		{
			element.setProbabilityShoot(element.getProbabilityShoot() + (float)Math.random());
		}
		else
		{
			element.setProbabilityShoot(element.getProbabilityShoot() - (float)Math.random());
		}
		//Choosing whether to add or subtract for run probability
		if(Math.random() < 0.5)
		{
			element.setProbabilityRun(element.getProbabilityRun() + (float)Math.random());
		}
		else
		{
			element.setProbabilityRun(element.getProbabilityRun() - (float)Math.random());
		}
	}

	@Override
	public float getProbabilityOfMutation()
	{
		return 0.001f;
	}

	@Override
	public int getSizeOfGeneration()
	{
		return 20;
	}

	@Override
	public Element toSaveFormat(SecondAgent element)
	{
		//Uses the SecondAgent's method to create an Element object
		return element.toSaveFormat();
	}

	@Override
	public SecondAgent fromSaveFormat(Element element)
	{
		//Uses the SecondAgent's method to create an instance from an Element object
		return new SecondAgent(element);
	}
}
