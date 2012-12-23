package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import ch.idsia.benchmark.mario.engine.sprites.Enemy;
import ch.idsia.benchmark.mario.environments.Environment;

public class LevelSceneMovement
{
	public static final int LevelSceneSize = 18;
	public static final int NO_FITNESS_SET = Integer.MIN_VALUE;
	
	private byte[][] levelScene;
	private final static String LevelSceneName = "LevelScene";
	private boolean[] actions;
	private static final String ActionsName = "Actions";
	private int fitness;
	private static final String FitnessName= "Fitness";
	
	public LevelSceneMovement(byte[][] levelScene, boolean[] actions, int fitness)
	{
		this.levelScene = levelScene;
		this.actions = actions;
		this.fitness = fitness;
	}
	public LevelSceneMovement(Element element)
	{
		fromSaveFormat(element);
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
	public void setActions(boolean[] actions)
	{
		this.actions = actions;
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
		
		return sameLevelScene(otherLevelSceneMovement);
	}
	private boolean sameLevelScene(LevelSceneMovement otherLevelSceneMovement)
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
	
	public int checkSimilarity(LevelSceneMovement lsm)
	{
		int similarity = 0;
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				if(get(i,j) == lsm.get(i, j))
				{
					similarity++;
				}
			}
			
		}
		return similarity;
	}
	
	public void fromSaveFormat(Element element)
	{
		Element levelSceneElement = element.getChild(LevelSceneName);
		levelScene = new byte[LevelSceneSize][LevelSceneSize];
		for(Element iElement : (List<Element>)levelSceneElement.getChildren())
		{
			int i = Integer.parseInt(iElement.getName().substring(1));
			for(Element jElement : (List<Element>)iElement.getChildren())
			{
				int j = Integer.parseInt(jElement.getName().substring(1));
				levelScene[i][j] = Byte.parseByte(jElement.getText());
			}
		}
		
		Element actionsElement = element.getChild(ActionsName);
		actions = new boolean[Environment.numberOfKeys];
		for(Element iElement : (List<Element>)actionsElement.getChildren())
		{
			int i = Integer.parseInt(iElement.getName().substring(1));
			actions[i] = Boolean.parseBoolean(iElement.getText());
		}
		
		fitness = Integer.parseInt(element.getChildText(FitnessName));
		
	}
	public Element toSaveFormat()
	{
		Element element = new Element(getClass().getSimpleName());
		
		Element levelSceneElement = new Element(LevelSceneName);
		for(int i = 0; i < levelScene.length; ++i)
		{
			Element ithElement = new Element("i" + i);
			for(int j = 0; j <  levelScene[i].length; ++j)
			{
				Element jElement = new Element("j" + j);
				jElement.setText("" + levelScene[i][j]);
				ithElement.addContent(jElement);
			}
			levelSceneElement.addContent(ithElement);
		}
		
		Element actionsElement = new Element(ActionsName);
		for(int i = 0; i < actions.length; ++i)
		{
			actionsElement.setAttribute("i" + i, "" + actions[i]);
		}
		
		Element fitnessElement = new Element(FitnessName);
		fitnessElement.setText(""+fitness);
		
		element.addContent(levelSceneElement);
		element.addContent(actionsElement);
		element.addContent(fitnessElement);
		
		return element;
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
		return element.toSaveFormat();
	}

	@Override
	public LevelSceneMovement fromSaveFormat(Element element)
	{
		return new LevelSceneMovement(element);
	}
	
}