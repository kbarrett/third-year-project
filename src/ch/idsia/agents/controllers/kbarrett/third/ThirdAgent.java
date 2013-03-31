package ch.idsia.agents.controllers.kbarrett.third;

import java.util.List;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.kbarrett.GeneticAlgorithm;
import ch.idsia.benchmark.mario.environments.Environment;

public class ThirdAgent implements Agent
{
	private static String name = "ThirdAgent";

	private LevelSceneSearchThread levelSceneSearchThread;

	private LevelSceneMovement thisMovement = null;
	private LevelSceneMovement lastMovement = null;
	private boolean[] lastActions = null;
	private int thisReward = 0;
	private float lastIntermediateReward = 0;
	private float[] lastMarioLoc = null;
	/**
	 * An int representing Mario's current mode, where:
	 * 		2 = fire
	 * 		1 = large
	 * 		0 = small
	 */
	private int lastMode = -1;

	public ThirdAgent()
	{
		initialiseThread();
	}
	
	private void initialiseThread()
	{
		levelSceneSearchThread = new LevelSceneSearchThread();
		levelSceneSearchThread.setPriority(Thread.MAX_PRIORITY);
	}
	
	@Override
	public boolean[] getAction()
	{
		try
		{
			levelSceneSearchThread.join();
			boolean[] actions = levelSceneSearchThread.getNearestMovement().getActions();

			initialiseThread();
			
			lastActions = actions;
			lastMovement = thisMovement;
			return actions;

		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return new boolean[Environment.numberOfKeys];
		}
	}

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

	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		// Add the change in "intermediate reward" to the reward
		thisReward += (int) (intermediateReward - lastIntermediateReward);
		lastIntermediateReward = intermediateReward;

		if (lastMovement != null) // if we aren't on the first iteration
		{
			// Store the last set of actions and the given reward
			lastMovement.setActions(lastActions, thisReward);
			LevelSceneMovementPopulationStorer.addNew(lastMovement);
		}
	}

	@Override
	public void reset() {}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

}

class LevelSceneSearchThread extends Thread
{
	private SearchRunnable search;

	public LevelSceneSearchThread()
	{
		super();
		search =  new SearchRunnable();
	}

	public void start(LevelSceneMovement requiredLevelSceneMovement)
	{
		search.giveRequiredLevelSceneMovement(requiredLevelSceneMovement);
		start();
	}

	public LevelSceneMovement getNearestMovement()
	{
		return search.getNearestMatch();
	}
	
	@Override
	public void run()
	{
		search.run();
	}
}

class SearchRunnable implements Runnable
{

	private LevelSceneMovement nearest;
	private LevelSceneMovement required;
	private LevelSceneMovementEvolver evolver;

	public SearchRunnable()
	{
		evolver = new LevelSceneMovementEvolver();
		LevelSceneMovementPopulationStorer.initialise(evolver);
	}

	public void giveRequiredLevelSceneMovement(LevelSceneMovement required)
	{
		this.required = required;
		evolver.giveRequiredLSM(required);
	}

	public LevelSceneMovement getNearestMatch()
	{
		return nearest;
	}

	@Override
	public void run()
	{
		long startTime = System.currentTimeMillis();

		List<LevelSceneMovement> population = LevelSceneMovementPopulationStorer.getPopulationCopy();
		if (population.size() < evolver.getSizeOfGeneration())
		{
			nearest = required;
			nearest.changeActions();
			return;
		}
		GeneticAlgorithm<LevelSceneMovement> algorithm = new GeneticAlgorithm<LevelSceneMovement>(population, evolver);

		//for(int iteration = 0; iteration < 35; ++iteration)
		while (
				startTime + 40 > System.currentTimeMillis()
				) 
		{
			population = algorithm.getNewGeneration(population.size() > evolver.getSizeOfGeneration());
		}
		//System.out.println("Ran " + iteration + " iterations.");
		
		if (population.contains(required))
		{
			nearest = population.get(population.indexOf(required)).clone();
			if (Math.random() < evolver.getProbabilityOfMutation())
			{
				nearest.changeActions();
			}
		} 
		else 
		{
			int bestMatch = 0;

			for (LevelSceneMovement lsm : population) 
			{
				int thisMatch = lsm.getFitness(required);
				if (thisMatch > bestMatch) 
				{
					nearest = lsm;
					bestMatch = thisMatch;
				}
			}
		}
	}

}
