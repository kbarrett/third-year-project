package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import ch.idsia.benchmark.mario.engine.sprites.Enemy;
import ch.idsia.benchmark.mario.environments.Environment;

public class LevelSceneMovement
{
	public static final int LevelSceneSize = 18;
	private byte[][] levelScene;
	private boolean[] actions;
	private int fitness;
	
	public LevelSceneMovement(byte[][] levelScene, boolean[] actions, int fitness)
	{
		this.levelScene = levelScene;
		this.actions = actions;
		this.fitness = fitness;
	}
	
	public void setLevelScene(byte[][] levelScene)
	{
		this.levelScene = levelScene;
	}
	
	public byte get(int y, int x)
	{
		return levelScene[y][x];
	}
	
	public int getFitness()
	{
		return fitness;
	}
	
	public boolean[] getActions()
	{
		return actions;
	}
	
	public void changeAction(int action)
	{
		actions[action] = !actions[action];
	}
	
	public void updateFitness(int fitness)
	{
		this.fitness = fitness;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if(!(otherObject instanceof LevelSceneMovement))
		{
			return false;
		}
		
		LevelSceneMovement otherLevelSceneMovement = (LevelSceneMovement) otherObject;
		
		return sameActions(otherLevelSceneMovement) && sameLevelScene(otherLevelSceneMovement);
	}
	public boolean sameLevelScene(LevelSceneMovement otherLevelSceneMovement)
	{
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				if(get(i,j) != otherLevelSceneMovement.get(i, j))
				{
					return false;
				}
			}
			
		}
		return true;
	}
	public boolean sameActions(LevelSceneMovement otherLevelSceneMovement)
	{
		boolean[] otherActions = otherLevelSceneMovement.getActions();
		for(int i = 0; i < actions.length; ++i)
		{
			if(actions[i] != otherActions[i])
			{
				return false;
			}
		}
		return true;
	}
	
}

class LevelSceneMovementEvolver implements Evolver<LevelSceneMovement>
{

	@Override
	public int fitnessFunction(LevelSceneMovement element)
	{
		return element.getFitness();
	}

	@Override
	public List<LevelSceneMovement> crossover(LevelSceneMovement element1, LevelSceneMovement element2)
	{
		int crossOverX = (int)(Math.random() * LevelSceneMovement.LevelSceneSize);
		int crossOverY = (int)(Math.random() * LevelSceneMovement.LevelSceneSize);
		int crossOver = (int)(Math.random() * Environment.numberOfKeys);
		
		byte[][] levelScene1 = new byte[LevelSceneMovement.LevelSceneSize][LevelSceneMovement.LevelSceneSize];
		byte[][] levelScene2 = new byte[LevelSceneMovement.LevelSceneSize][LevelSceneMovement.LevelSceneSize];
		
		for(int i = 0 ; i < LevelSceneMovement.LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneMovement.LevelSceneSize; ++j)
			{
				if(i < crossOverY || i == crossOverY && j < crossOverX)
				{
					levelScene1[i][j] = element1.get(i, j);
					levelScene2[i][j] = element2.get(i, j);
				}
				else
				{
					levelScene1[i][j] = element2.get(i, j);
					levelScene2[i][j] = element1.get(i, j);
				}
			}
		}
		
		boolean[] actions1 = new boolean[Environment.numberOfKeys];
		boolean[] actions2 = new boolean[Environment.numberOfKeys];
		for(int i = 0; i<Environment.numberOfKeys; ++i)
		{
			if(i < crossOver)
			{
				actions1[i] = element1.getActions()[i];
				actions2[i] = element2.getActions()[i];
			}
			else
			{
				actions1[i] = element2.getActions()[i];
				actions2[i] = element1.getActions()[i];
			}
		}
		
		float probability = crossOver / (float)Environment.numberOfKeys ;
		int fitness1 = (int)(element1.getFitness() * probability + element2.getFitness() * (1-probability));
		int fitness2 = (int)(element2.getFitness() * probability + element1.getFitness() * (1-probability));
		
		LinkedList<LevelSceneMovement> result = new LinkedList<LevelSceneMovement>();
		result.add(new LevelSceneMovement(levelScene1, actions1, fitness1));
		result.add(new LevelSceneMovement(levelScene2, actions2, fitness2));
		
		return result;
	}

	@Override
	public void mutate(LevelSceneMovement element)
	{
		element.changeAction((int)(element.getActions().length * Math.random()));
	}

	@Override
	public float getProbabilityOfMutation()
	{
		return 0.01f;
	}

	@Override
	public int getSizeOfGeneration()
	{
		return 20;
	}

	@Override
	public Element toSaveFormat(LevelSceneMovement element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LevelSceneMovement fromSaveFormat(Element element)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}