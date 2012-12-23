package ch.idsia.agents.controllers.kbarrett;

import java.util.Date;
import java.util.LinkedList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

public class ThirdAgent implements Agent
{
	private static String name = "ThirdAgent";
	
	LevelSceneSearchThread levelSceneSearchThread = new LevelSceneSearchThread();
	
	LevelSceneMovement lastMovement;
	
	@Override
	public boolean[] getAction()
	{
		try
		{
			levelSceneSearchThread.join();
			boolean[] actions = levelSceneSearchThread.getNearestMovement().getActions();
			lastMovement.setActions(actions);
			return actions;
			
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			return new boolean[Environment.numberOfKeys];
		}
	}

	@Override
	public void integrateObservation(Environment environment)
	{
		byte[][] levelScene = environment.getMergedObservationZZ(0, 0);
		levelSceneSearchThread.start(levelScene);
		lastMovement = new LevelSceneMovement(levelScene, null, LevelSceneMovement.NO_FITNESS_SET);
	}

	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		lastMovement.updateFitness((int)intermediateReward);
		LevelSceneMovementPopulationStorer.addNew(lastMovement);
	}

	@Override
	public void reset()
	{
	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol)
	{
	}

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
	private final static SearchRunnable search = new SearchRunnable();
	
	public LevelSceneSearchThread()
	{
		super(search);
	}
	
	public void start(byte[][] levelScene)
	{
		LevelSceneMovement requiredLevelSceneMovement = new LevelSceneMovement(levelScene, null, LevelSceneMovement.NO_FITNESS_SET);
		search.giveRequiredLevelSceneMovement(requiredLevelSceneMovement);
		run();
	}
	
	public static LevelSceneMovement getNearestMovement()
	{
		return search.getNearestMatch();
	}
}

class SearchRunnable implements Runnable
{
	
	private LevelSceneMovement nearest;
	private LevelSceneMovement required;
	LevelSceneMovementEvolver evolver;

	public SearchRunnable()
	{
		evolver = new LevelSceneMovementEvolver();
		LevelSceneMovementPopulationStorer.initialise(evolver);
	}
	
	public void giveRequiredLevelSceneMovement(LevelSceneMovement required)
	{
		this.required = required;
	}
	public LevelSceneMovement getNearestMatch()
	{
		return nearest;
	}
	
	@Override
	public void run()
	{
		long startTime = System.currentTimeMillis();
		
		LinkedList<LevelSceneMovement> population = new LinkedList<LevelSceneMovement>(LevelSceneMovementPopulationStorer.getPopulation());
		if(population.isEmpty())
		{
			nearest = required;
			boolean[] actions = new boolean[Environment.numberOfKeys];
			for(int i = 0; i<actions.length; ++i)
			{
				actions[i] = (Math.random() < 0.5f);
			}
			nearest.setActions(actions);
			return;
		}
		GeneticAlgorithm<LevelSceneMovement> algorithm = new GeneticAlgorithm<LevelSceneMovement>(population, evolver);
		while(startTime + 1000 < System.currentTimeMillis() 
				&&		(!population.contains(required) 
						|| population.get(population.indexOf(required)).getFitness() < 0))
		{
			population = algorithm.getNewGeneration();
			
			int bestMatch = 0;
			
			for(LevelSceneMovement lsm : population)
			{
				int thisMatch = lsm.checkSimilarity(required);
				if(thisMatch > bestMatch)
				{
					nearest = lsm;
					bestMatch = thisMatch;
				}
			}
		}
		
		nearest = population.get(population.indexOf(required));
	}
	
}

