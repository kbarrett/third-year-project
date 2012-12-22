package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

public class ThirdAgent implements Agent
{
	LevelSceneSearchThread levelSceneSearchThread = new LevelSceneSearchThread();
	
	@Override
	public boolean[] getAction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void integrateObservation(Environment environment)
	{
		// TODO Auto-generated method stub
		levelSceneSearchThread.start(environment.getMergedObservationZZ(0, 0));
	}

	@Override
	public void giveIntermediateReward(float intermediateReward)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub

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
		LevelSceneMovement requiredLevelSceneMovement = new LevelSceneMovement(levelScene, null /*this is gonna break shit*/, 0);
		search.giveRequiredLevelSceneMovement(requiredLevelSceneMovement);
		run();
	}
}

class SearchRunnable implements Runnable
{
	private LevelSceneMovement nearest = new LevelSceneMovement(null, null, 0);
	private LevelSceneMovement required;
	
	public void giveRequiredLevelSceneMovement(LevelSceneMovement required)
	{
		this.required = required;
	}
	public LevelSceneMovement getNearestMatch()
	{
		return required;
	}
	
	@Override
	public void run()
	{
		LinkedList<LevelSceneMovement> population = new LinkedList<LevelSceneMovement>(LevelSceneMovementPopulationStorer.getPopulation());
		GeneticAlgorithm<LevelSceneMovement> algorithm = new GeneticAlgorithm<LevelSceneMovement>(population, new LevelSceneMovementEvolver());
		while(!population.contains(required)) //FIXME: should only consider levelScene - done by overloading equals???? (or not...)
		{
			population = algorithm.getNewGeneration();
			LevelSceneMovementPopulationStorer.addNew(population);
			
			/*
			 * foreach LSM in pop
			 * 		check similarity with original
			 * save most similar as nearest
			*/
		}
	}
	
}

