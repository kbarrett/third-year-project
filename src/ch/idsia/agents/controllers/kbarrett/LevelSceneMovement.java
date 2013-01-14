package ch.idsia.agents.controllers.kbarrett;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import sun.security.action.GetLongAction;

import ch.idsia.benchmark.mario.engine.sprites.Enemy;
import ch.idsia.benchmark.mario.environments.Environment;

public class LevelSceneMovement implements Cloneable
{
	public static final int LevelSceneSize = 19;
	public static final int NO_REWARD_SET = Integer.MIN_VALUE;
	
	private byte[][] levelScene;
	private final static String LevelSceneName = "LevelScene";
	private boolean[] actions;
	private static final String ActionsName = "Actions";
	private int reward;
	private static final String RewardName= "Reward";
	
	public LevelSceneMovement(byte[][] levelScene, boolean[] actions, int reward)
	{
		this.levelScene = levelScene;
		this.actions = actions;
		this.reward = reward;
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
	
	public int getFitness(LevelSceneMovement other)
	{
		return checkWeightedSimilarity(other);
	}
	
	public boolean[] getActions()
	{
		return actions;
	}
	public void setActions(boolean[] actions, int reward)
	{
		this.actions = actions;
		this.reward = reward;
	}
	
	public void changeAction(int action)
	{
		actions[action] = !actions[action];
	}

	public void setReward(int reward)
	{
		this.reward = reward;
	}
	
	public int getReward()
	{
		return reward;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if(!(otherObject instanceof LevelSceneMovement))
		{
			return false;
		}
		
		return sameLevelScene((LevelSceneMovement) otherObject);
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
	
	public int checkWeightedSimilarity(LevelSceneMovement lsm)
	{
		int similarity = 0;
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				if(get(i,j) == lsm.get(i, j))
				{
					similarity += (getWeighting(i) + getWeighting(j));
				}
			}
			
		}
		return similarity;
	}
	
	private int getWeighting(int i)
	{
		if(8<=i && i<=10) //8<=i<=10
		{
			return 3;
		}
		if((4<=i && i<=14)) //4<=i<=7 and 11<=i<=14
		{
			return 2;
		}
		return 1; //0<=3 and 15<=18
	}
	
	@Override
	public LevelSceneMovement clone()
	{
		byte[][] newLevelScene = new byte[LevelSceneSize][LevelSceneSize];
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				newLevelScene[i][j] = levelScene[i][j];
			}
		}
		boolean[] newActions = new boolean[actions.length];
		for(int i = 0; i < actions.length; ++i)
		{
			newActions[i] = actions[i];
		}
		return new LevelSceneMovement(newLevelScene, newActions, reward);
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
		
		reward = Integer.parseInt(element.getChildText(RewardName));
		
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
		
		Element rewardElement = new Element(RewardName);
		rewardElement.setText(""+reward);
		
		element.addContent(levelSceneElement);
		element.addContent(actionsElement);
		element.addContent(rewardElement);
		
		return element;
	}
	
	@Override
	public String toString()
	{
		return "Level Scene: " + printLevelScene() + "Actions: " + actions + " Reward: " + reward + "\n";
	}
	
	private String printLevelScene()
	{
		String s = "";
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				s += get(i,j);
			}
			s+="\n";
		}
		return s;
	}
	
}

class LevelSceneMovementEvolver implements Evolver<LevelSceneMovement>
{
	private LevelSceneMovement requiredLSM;

	public void giveRequiredLSM(LevelSceneMovement requiredLSM)
	{
		this.requiredLSM = requiredLSM;
	}

	@Override
	public int fitnessFunction(LevelSceneMovement element)
	{
		return element.getFitness(requiredLSM);
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
		int reward1 = (int)(element1.getReward() * probability + element2.getReward() * (1-probability));
		int reward2 = (int)(element2.getReward() * probability + element1.getReward() * (1-probability));

		/* 
		* Note: this reward currently doesn't affect anything as it isn't used in the fitness function and doesn't get saved.
		* It's left here incase of use in the fitness function in the future.
		*/
		
		LinkedList<LevelSceneMovement> result = new LinkedList<LevelSceneMovement>();
		result.add(new LevelSceneMovement(levelScene1, actions1, reward1));
		result.add(new LevelSceneMovement(levelScene2, actions2, reward2));
		
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
