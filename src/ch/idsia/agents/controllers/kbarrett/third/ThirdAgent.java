package ch.idsia.agents.controllers.kbarrett.third;

import java.util.List;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.kbarrett.Evolver;
import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;
import ch.idsia.benchmark.mario.environments.Environment;
/**
 * Agent that uses a genetic algorithm to evolve the environment around Mario to obtain an action array to perform in that environment.
 * @author Kim Barrett
 */
public class ThirdAgent implements Agent
{
	/**
	 * The name associated with this agent.
	 */
	private static String name = "ThirdAgent";
	/**
	 * The {@link Thread} used to perform the search
	 */
	private LevelSceneSearchThread levelSceneSearchThread;
	/**
	 * The {@link LevelSceneMovement} associated with the current frame.
	 */
	private LevelSceneMovement thisMovement = null;
	/**
	 * The {@link LevelSceneMovement} associated with the previous frame.
	 */
	private LevelSceneMovement lastMovement = null;
	/**
	 * The set of actions associated with the previous frame.
	 */
	private boolean[] lastActions = null;
	/**
	 * The reward associated with the previous frame - but is given to the agent in the current frame.
	 */
	private int thisReward = 0;
	/**
	 * The intermediate reward associated with the previous frame.
	 * Used to calculate the change in intermediate reward between frames.
	 * @see #giveIntermediateReward(float)
	 */
	private float lastIntermediateReward = 0;
	/**
	 * The precise location of Mario on the screen in the previous frame.
	 * Used to calculate how far (and in which direction Mario has travelled between frames).
	 * This value is used when creating the reward for {@link #thisReward}.
	 */
	private float[] lastMarioLoc = null;
	/**
	 * An int representing Mario's current mode, where:
	 * 		2 = fire
	 * 		1 = large
	 * 		0 = small
	 */
	private int lastMode = -1;
	/**
	 * Creates a new Agent, which also initialises the search thread
	 */
	public ThirdAgent()
	{
		initialiseThread();
	}
	/**
	 * Create a new search thread.
	 * @see #levelSceneSearchThread
	 */
	private void initialiseThread()
	{
		levelSceneSearchThread = new LevelSceneSearchThread();
		levelSceneSearchThread.setPriority(Thread.MAX_PRIORITY);
	}
	/**
	 * Used to return the required actions to the game.
	 * @return boolean array of size 6. Each element in the array represents a 
	 * movement: true indicates it is being requested during this frame & false 
	 * means it is not. 
	 * From 0 to 5 the corresponding movements are: left, right, down, jump, speed, up.
	 */
	@Override
	public boolean[] getAction()
	{
		try
		{
			//Wait for the search thread to finish computation - normally this should have already completed due to the time limit applied to the search
			levelSceneSearchThread.join();
			//Get the action array calculated by the search thread
			boolean[] actions = levelSceneSearchThread.getNearestMovement().getActions();
			//Get the thread ready for the next frame
			initialiseThread();
			//Store the actions for the next frame
			lastActions = actions;
			//Store the LevelSceneMovement representing this frame
			lastMovement = thisMovement;
			//Return the calculated action array
			return actions;

		} 
		catch (InterruptedException e)
		{
			//If something goes wrong, return an empty action array
			e.printStackTrace();
			return new boolean[Environment.numberOfKeys];
		}
	}
	/**
	 * Used to update the Agent with information about the environment.
	 * This gets called by the game every frame & passes the updated Environment
	 * to the Agent to allow it to update its knowledge of the world (such as
	 * where remaining enemies, coins and blocks are).
	 * @param environment containing updated knowledge
	 */
	@Override
	public void integrateObservation(Environment environment)
	{
		// Get the current levelscene
		byte[][] levelScene = environment.getMergedObservationZZ(0, 0);

		// Store the levelscene as a LevelSceneMovement
		thisMovement = new LevelSceneMovement(levelScene, null, LevelSceneMovement.NO_REWARD_SET);

		// Give this to the genetic algorithm thread
		levelSceneSearchThread.start(thisMovement);

		if (lastMarioLoc == null) // this will only be true on the first iteration
		{
			// Initialise the global variables
			lastMarioLoc = new float[2];
			thisReward = 0;	
		} 
		else 
		{
			// Use change in position for the reward
			thisReward = (int) (environment.getMarioFloatPos()[0] - lastMarioLoc[0]); // Mario's x location
			thisReward += (int) (lastMarioLoc[1] - environment.getMarioFloatPos()[1]); // Mario's y location
			thisReward += 100 * (environment.getMarioMode() - lastMode);
		}

		// Update previous position to be current position
		lastMarioLoc[0] = environment.getMarioFloatPos()[0];
		lastMarioLoc[1] = environment.getMarioFloatPos()[1];
		lastMode = environment.getMarioMode();

	}
	/**
	 * Used to update the Agent with information about the environment.
	 * This gets called by the game every frame & passes the updated Environment
	 * to the Agent to allow it to update its knowledge of the world (such as
	 * where remaining enemies, coins and blocks are).
	 * @param environment containing updated knowledge
	 */
	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		// Add the change in "intermediate reward" to the reward
		thisReward += (int) (intermediateReward - lastIntermediateReward);
		//Store the intermediate reward given this frame, so the change in it can be calculated next frame.
		lastIntermediateReward = intermediateReward;

		if (lastMovement != null) // if we aren't on the first iteration
		{
			// Store the last set of actions and the given reward
			lastMovement.setActions(lastActions, thisReward);
			LevelSceneMovementPopulationStorer.addNew(lastMovement);
		}
	}
	//These methods aren't necessary for this agent
		@Override
		public void reset() {}
		@Override
		public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {}
	/**
	 * Returns the name associated with this agent - for display purposes on the GUI
	 */
	@Override
	public String getName()
	{
		return name;
	}
	/**
	 * Rename this agent.
	 */
	@Override
	public void setName(String name)
	{
		ThirdAgent.name = name;
	}
}
/**
 * Thread responsible for running the Genetic Algorithm.
 * @author Kim Barrett
 */
class LevelSceneSearchThread extends Thread
{
	/**
	 * The {@link Runnable} containing the method to run the Genetic Algorithm.
	 */
	private SearchRunnable search;
	/**
	 * Creates a new instance of this thread & its associated Runnable.
	 */
	public LevelSceneSearchThread()
	{
		super();
		search =  new SearchRunnable();
	}
	/**
	 * Starts this thread, which runs the genetic algorithm to find a LevelSceneMovement similar to the given one.
	 * @param requiredLevelSceneMovement - the LevelSceneMovement to search for.
	 * Note: this LevelSceneMovement shouldn't already have an associated action, as this is what is being searched for.
	 */
	public void start(LevelSceneMovement requiredLevelSceneMovement)
	{
		search.giveRequiredLevelSceneMovement(requiredLevelSceneMovement);
		start();
	}
	/**
	 * Return the LevelSceneMovement that was most similar to the required one.
	 * Must be called after a search has been performed, otherwise null will be returned.
	 * @return the most similar LevelSceneMovement to the given one.
	 */
	public LevelSceneMovement getNearestMovement()
	{
		return search.getNearestMatch();
	}
	/**
	 * Runs the associated {@link Runnable}.
	 */
	@Override
	public void run()
	{
		search.run();
	}
}
/**
 * Responsible for running the {@link GeneticAlgorithm} to find an action array for a given {@link LevelSceneMovement}.
 * @author Kim Barrett
 */
class SearchRunnable implements Runnable
{
	/**
	 * The LevelSceneMovement that was the best match to the {@link #required} one, when the search had been performed.
	 */
	private LevelSceneMovement nearest;
	/**
	 * The LevelSceneMovement for which an action array is being found.
	 */
	private LevelSceneMovement required;
	/**
	 * The {@link Evolver} used to evolve {@link LevelSceneMovement}s in the {@link GeneticAlgorithm}.
	 */
	private LevelSceneMovementEvolver evolver;
	/**
	 * Creates a new instance. Also initialises the {@link LevelSceneMovementPopulationStorer} to store a new 
	 * population, which loads a population from file if required.
	 */
	public SearchRunnable()
	{
		evolver = new LevelSceneMovementEvolver();
		LevelSceneMovementPopulationStorer.initialise();
	}
	/**
	 * Provide this instance with the {@link LevelSceneMovement} for which an action array is being found.
	 * @param required - {@link #required}
	 */
	public void giveRequiredLevelSceneMovement(LevelSceneMovement required)
	{
		this.required = required;
		//Pass this also to the evolver - so it knows which LSM it is searching for.
		evolver.giveRequiredLSM(required);
	}
	/**
	 * @return the {@link LevelSceneMovement} found by the genetic algorithm.
	 */
	public LevelSceneMovement getNearestMatch()
	{
		return nearest;
	}
	/**
	 * Performs the genetic algorithm, using the previously set parameters (i.e. which {@link #required} LSM is being used).
	 */
	@Override
	public void run()
	{
		//Store the time at which this method was begun.
		long startTime = System.currentTimeMillis();
		//Gets a copy of all previously encountered LSMs from the population storer.
		List<LevelSceneMovement> population = LevelSceneMovementPopulationStorer.getPopulationCopy();
		//If we don't have enough previously knowledge to make an informed decision
		if (population.size() < evolver.getSizeOfGeneration())
		{
			nearest = required;
			//Choose a random, valid action array for the LSM
			nearest.changeActions();
			return;
		}
		//Otherwise run the GeneticAlgorithm
		
		//Create a new instance to run the algorithm, using the given population
		GeneticAlgorithm<LevelSceneMovement> algorithm = new GeneticAlgorithm<LevelSceneMovement>(population, evolver);

		//Get new generations for 40ms - this ensures the algorithm always stays within the amount of time allowed per frame
		while (startTime + 40 > System.currentTimeMillis()) 
		{
			//Gets a new generation. Multi-threading only on the first instance (when there are many more LSMs in the population)
			population = algorithm.getNewGeneration(population.size() > evolver.getSizeOfGeneration());
		}
		//If the required LSM has been found by the GA
		if (population.contains(required))
		{
			//Use its associated action array
			nearest = population.get(population.indexOf(required)).clone();
		} 
		else 
		{
			//Stores the fitness of the best LSM found in the population so far
			int bestMatch = -1;
			//For each LSM in the final generation
			for (LevelSceneMovement lsm : population) 
			{
				//Get its similarity with the required LSM
				int thisMatch = lsm.getFitness(required);
				//If lsm is more similar than the previous best
				if (thisMatch > bestMatch) 
				{
					//Update the best to be lsm
					nearest = lsm;
					//Update the best fitness found so far
					bestMatch = thisMatch;
				}
			}
		}
	}

}
