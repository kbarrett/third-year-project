package ch.idsia.agents.controllers.kbarrett;

import java.util.List;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

public class ThirdAgent implements Agent
{
	private static String name = "ThirdAgent";
	
	private LevelSceneSearchThread levelSceneSearchThread = new LevelSceneSearchThread();
	
	private LevelSceneMovement lastMovement;
	private int lastReward;
	private float lastIntermediateReward = 0;
	
	@Override
	public boolean[] getAction()
	{
		try
		{	
			levelSceneSearchThread.join();
			boolean[] actions = levelSceneSearchThread.getNearestMovement().getActions();
			
			lastMovement.setActions(actions, lastReward);
			LevelSceneMovementPopulationStorer.addNew(lastMovement);
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
		lastMovement = new LevelSceneMovement(levelScene, null, LevelSceneMovement.NO_REWARD_SET);
		
		lastReward = (int)environment.getMarioFloatPos()[0]; //Mario's x location
	}

	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		lastReward += (int)(intermediateReward - lastIntermediateReward);
		lastIntermediateReward = intermediateReward;
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
		LevelSceneMovement requiredLevelSceneMovement = new LevelSceneMovement(levelScene, null, LevelSceneMovement.NO_REWARD_SET);
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
		if(population.size() < evolver.getSizeOfGeneration())
		{
			nearest = required;
			boolean[] actions = new boolean[Environment.numberOfKeys];
			for(int i = 0; i<actions.length; ++i)
			{
				actions[i] = (Math.random() < 0.5f);
			}
			nearest.setActions(actions, LevelSceneMovement.NO_REWARD_SET);
			return;
		}
		GeneticAlgorithm<LevelSceneMovement> algorithm = new GeneticAlgorithm<LevelSceneMovement>(population, evolver);
		
		long now = System.currentTimeMillis();
		while(startTime + 1000 > now)
		{
			population = algorithm.getNewGeneration();
			now = System.currentTimeMillis();
		}
		if(population.contains(required))
		{
			nearest = population.get(population.indexOf(required));
		}
		else
		{	
			int bestMatch = 0;
			
			for(LevelSceneMovement lsm : population)
			{
				int thisMatch = lsm.getFitness(required);
				if(thisMatch > bestMatch)
				{
					nearest = lsm;
					bestMatch = thisMatch;
				}
			}
		}
	}
	
}

