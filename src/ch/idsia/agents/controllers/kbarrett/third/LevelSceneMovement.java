package ch.idsia.agents.controllers.kbarrett.third;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import ch.idsia.agents.controllers.kbarrett.Encoding;
import ch.idsia.agents.controllers.kbarrett.Evolver;
import ch.idsia.benchmark.mario.environments.Environment;
/**
 * Stores a LevelScene, its corresponding action array and the reward value given to it.
 * @author Kim Barrett
 */
public class LevelSceneMovement implements Cloneable, Serializable
{
	/**
	 * The size of the level scene when stored by this class.
	 * Note: LevelScenes larger than this will be trimmed when given to this class.
	 */
	public static final int LevelSceneSize = 11;
	/**
	 * The value denoting that this LevelSceneMovement hasn't yet been assigned a reward.
	 */
	public static final int NO_REWARD_SET = Integer.MIN_VALUE;
	/**
	 * The weighting used when comparing the similarity of two LevelSceneMovements.
	 */
	private static final int breadthWeighting = 5;
	/**
	 * The 2D-array representing Mario's surroundings.
	 */
	private byte[][] levelScene;
	/**
	 * The name given to the {@link Element} storing {@link #levelScene} when creating an xml version of this class.
	 */
	private final static String LevelSceneName = "LevelScene";
	/**
	 * The array representing the action corresponding to this instance's {@link #levelScene}.
	 */
	private boolean[] actions;
	/**
	 * The name given to the {@link Element} storing {@link #actions} when creating an xml version of this class.
	 */
	private static final String ActionsName = "Actions";
	/**
	 * The value given to the {@link #actions} array when performed for this {@link #levelScene}.
	 * This allows better actions to replace the current ones.
	 */
	private int reward;
	/**
	 * The name given to the {@link Element} storing {@link #reward} when creating an xml version of this class.
	 */
	private static final String RewardName= "Reward";
	/**
	 * Associates the given LevelScene with the given action array and reward.
	 * @param levelScene - {@link #levelScene}
	 * @param actions - {@link #actions}
	 * @param reward - {@link #reward}
	 */
	public LevelSceneMovement(byte[][] levelScene, boolean[] actions, int reward)
	{
		//Reduces the size of the levelScene if required
		this.levelScene = clipLevelScene(levelScene);
		//Sets the given actions and reward
		setActions(actions, reward);
	}
	/**
	 * Reduces the size of the given LevelScene about the centre.
	 * @param levelScene - the levelScene to trim
	 * @return the levelScene of size {@link #LevelSceneSize}.
	 */
	public byte[][] clipLevelScene(byte[][] levelScene)
	{
		byte[][] clippedLevelScene = new byte[LevelSceneSize][LevelSceneSize];
		//The difference in size between required size and that of the given levelScene.
		int shift = (int)(levelScene.length/2) - (int)(LevelSceneSize/2);
		//Populate the new array with the same elements as the central square of the given levelScene
		for(int i = 0; i < clippedLevelScene.length; ++i)
		{
			for(int j = 0; j < clippedLevelScene[i].length; ++j)
			{
				//Simplify the given levelScene to map similar grid elements to the same value.
				clippedLevelScene[i][j] = Encoding.simplify(levelScene[i + shift][j + shift]);
			}
		}
		return clippedLevelScene;
	}
	/**
	 * Creates a LevelSceneMovement from a xml format.
	 * @param element - the xml format from which the object should be created.
	 */
	public LevelSceneMovement(Element element)
	{
		fromSaveFormat(element);
	}
	/**
	 * Updates the {@link #levelScene}.
	 * @param levelScene - the new levelScene.
	 */
	public void setLevelScene(byte[][] levelScene)
	{
		this.levelScene = levelScene;
	}
	/**
	 * Gets an element from the levelScene corresponding to this instance.
	 * @param y - the y value of the required element.
	 * @param x - the x value of the required element.
	 * @return the element at the position [y][x] in the levelScene.
	 */
	public byte get(int y, int x)
	{
		return levelScene[y][x];
	}
	/**
	 * Compares this instance with the given one.
	 * @param other - the LevelSceneMovement to compare with.
	 * @return value representing how similar they are.
	 */
	public int getFitness(LevelSceneMovement other)
	{
		return checkWeightedSimilarity(other);
	}
	/**
	 * @return the action array associated with this instance.
	 */
	public boolean[] getActions()
	{
		return actions;
	}
	/**
	 * Updates the action array and corresponding reward value.
	 * @param actions - the new action array.
	 * @param reward - the new reward value.
	 */
	public void setActions(boolean[] actions, int reward)
	{
		this.actions = actions;
		this.reward = reward;
	}
	/**
	 * Randomly choose a different action array to associate with this {@link #levelScene}.
	 */
	public void changeActions()
	{
		//Makes a random choice of valid action array.
		int choice = (int) (Math.random() * ActionsIndex.NumberOfArrayPossibilities);
		//Gets the array corresponding to this index & stores it.
		setActions(ActionsIndex.getArray(choice), NO_REWARD_SET);
	}
	/**
	 * Mutate the current action array.
	 */
	public void mutateActions()
	{
		//Get the index of the current array
		int match = ActionsIndex.getMatch(actions);
		//If the current array is not valid, select a random array
		if(match < 0)
		{
			match = (int) (Math.random() * ActionsIndex.NumberOfArrayPossibilities);
		}
		//Mutate the array
		ActionsIndex.getMutatedArray(match, 0.6f);
	}
	/**
	 * Update reward.
	 * @param reward - new reward
	 */
	public void setReward(int reward)
	{
		this.reward = reward;
	}
	/**
	 * @return reward associated with this instance
	 */
	public int getReward()
	{
		return reward;
	}
	/**
	 * This LevelSceneMovement is equal to another if they have the same {@link #levelScene}.
	 */
	@Override
	public boolean equals(Object otherObject)
	{
		if(!(otherObject instanceof LevelSceneMovement))
		{
			return false;
		}
		return sameLevelScene((LevelSceneMovement) otherObject);
	}
	/**
	 * Compare the {@link #levelScene} associated with this instance and the given one.
	 * @param otherLevelSceneMovement - the LevelSceneMovement to compare with.
	 * @return whether the two {@link #levelScene}s are equal.
	 */
	private boolean sameLevelScene(LevelSceneMovement otherLevelSceneMovement)
	{
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				//If any of the elements are different, then the arrays aren't equal
				if(get(i,j) != otherLevelSceneMovement.get(i, j))
				{
					return false;
				}
			}
			
		}
		return true;
	}
	/**
	 * Compare the {@link #levelScene}s of this instance and a given LevelSceneMovement, with higher weight given to the central elements.
	 * @param lsm - the LevelSceneMovement to compare against.
	 * @return int representation of how similar the two {@link #levelScene}s are.
	 */
	public int checkWeightedSimilarity(LevelSceneMovement lsm)
	{
		int similarity = 0;
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				//If the values are equal, add the weight associated with this element
				if(get(i,j) == lsm.get(i, j))
				{
					//Get the weighting of this element by adding the weight of the column & row
					similarity += (getWeighting(i) + getWeighting(j));
				}
			}
			
		}
		return similarity;
	}
	/**
	 * Returns the weighting of the given row/column of the {@link #levelScene}.
	 * @param i - the required row/column number
	 * @return int corresponding with the weight
	 */
	private int getWeighting(int i)
	{
		if(i == 0 || i == LevelSceneSize) {return 1;}
		return (int) (Math.floor(LevelSceneSize/breadthWeighting) - Math.floor(Math.abs(Math.floor(LevelSceneSize/(breadthWeighting/2)) - i)/3));
	}
	/**
	 * Create a clone of this instance.
	 */
	@Override
	public LevelSceneMovement clone()
	{
		//Create new levelScene & copy the old levelScene into it
		byte[][] newLevelScene = new byte[LevelSceneSize][LevelSceneSize];
		for(int i = 0; i < LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneSize; ++j)
			{
				newLevelScene[i][j] = levelScene[i][j];
			}
		}
		//Create a new action array & copy the old action array into it
		boolean[] newActions;
		if(actions == null)
		{
			newActions = null;
		}
		else
		{
			newActions = new boolean[actions.length];
			for(int i = 0; i < actions.length; ++i)
			{
				newActions[i] = actions[i];
			}
		}
		//Create new instance from these copies of data
		return new LevelSceneMovement(newLevelScene, newActions, reward);
	}
	/**
	 * Gets the data from an xml format & creates a LevelSceneMovement from it.
	 * @param element - the xml format to use
	 */
	public void fromSaveFormat(Element element)
	{
		//Gets the levelScene data from the xml format
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
		//Gets the action array data from the xml format
		Element actionsElement = element.getChild(ActionsName);
		actions = new boolean[Environment.numberOfKeys];
		for(Element iElement : (List<Element>)actionsElement.getChildren())
		{
			int i = Integer.parseInt(iElement.getName().substring(1));
			actions[i] = Boolean.parseBoolean(iElement.getText());
		}
		//Gets the reward value from the xml format
		reward = Integer.parseInt(element.getChildText(RewardName));
	}
	/**
	 * Creates an xml format representing this element.
	 * @return Element storing the created xml format.
	 */
	public Element toSaveFormat()
	{
		//Create root Element
		Element element = new Element(getClass().getSimpleName());
		//Create Element for the levelScene & populate it with the data
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
		//Create Element for the action array & populate it with the data
		Element actionsElement = new Element(ActionsName);
		for(int i = 0; i < actions.length; ++i)
		{
			actionsElement.setAttribute("i" + i, "" + actions[i]);
		}
		//Create Element for the reward & give it the reward 
		Element rewardElement = new Element(RewardName);
		rewardElement.setText(""+reward);
		//Add the child Elements to the root Element
		element.addContent(levelSceneElement);
		element.addContent(actionsElement);
		element.addContent(rewardElement);
		//Return the root element
		return element;
	}
	/**
	 * Prints out the data associated with this LevelSceneMovement.
	 */
	@Override
	public String toString()
	{
		String a;
		if(actions != null)
		{ 
			a ="[";
			for(int i = 0; i< actions.length; ++i)
			{
				if(i == actions.length - 1)
				{
					a += actions[i];
				}
				else
				{
					a += actions[i] + ",";
				}
			}
			a += "]";
		}
		else
		{
			a = "null";
		}
		return "Level Scene: \n" + printLevelScene() + "Actions: " + a + " Reward: " + reward + "\n";
	}
	/**
	 * @return a String representation of the {@link #levelScene}.
	 */
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
/**
 * Evolver used to evolve a {@link LevelSceneMovement}.
 * @author Kim Barrett
 */
class LevelSceneMovementEvolver implements Evolver<LevelSceneMovement>
{
	/**
	 * The {@link LevelSceneMovement} that is being compared against.
	 * I.e. this is the one that we are trying to find an action array for.
	 */
	private LevelSceneMovement requiredLSM;
	/**
	 * Update the {@link #requiredLSM}.
	 * @param requiredLSM - the new {@link LevelSceneMovement}.
	 */
	public void giveRequiredLSM(LevelSceneMovement requiredLSM)
	{
		this.requiredLSM = requiredLSM;
	}
	/**
	 * Compares for similarity with the {@link #requiredLSM}.
	 */
	@Override
	public int fitnessFunction(LevelSceneMovement element)
	{
		/*
		 * Compares the requiredLSM with the given one, assigning a weighting
		 * representing how similar their levelscenes are. 
		 */
		return element.getFitness(requiredLSM);
	}
	/**
	 * Treats the LevelScenes & action arrays as a String and chooses a random crossover point in both.
	 * The reward is crossed over using a weighted average of the two elements rewards.
	 */
	@Override
	public List<LevelSceneMovement> crossover(LevelSceneMovement element1, LevelSceneMovement element2)
	{
		//Crossover points for the levelScene
		int crossOverX = (int)(Math.random() * LevelSceneMovement.LevelSceneSize);
		int crossOverY = (int)(Math.random() * LevelSceneMovement.LevelSceneSize);
		//Crossover point for the action array
		int crossOver = (int)(Math.random() * Environment.numberOfKeys);
		//LevelScenes for the child LevelSceneMovements
		byte[][] levelScene1 = new byte[LevelSceneMovement.LevelSceneSize][LevelSceneMovement.LevelSceneSize];
		byte[][] levelScene2 = new byte[LevelSceneMovement.LevelSceneSize][LevelSceneMovement.LevelSceneSize];
		//Populate these new arrays
		for(int i = 0 ; i < LevelSceneMovement.LevelSceneSize; ++i)
		{
			for(int j = 0; j < LevelSceneMovement.LevelSceneSize; ++j)
			{
				//If prior to the element [crossOverY][crossOverX], take the element from one LevelSceneMovement
				if(i < crossOverY || i == crossOverY && j < crossOverX)
				{
					levelScene1[i][j] = element1.get(i, j);
					levelScene2[i][j] = element2.get(i, j);
				}
				else //Otherwise take it from the other LevelSceneMovement
				{
					levelScene1[i][j] = element2.get(i, j);
					levelScene2[i][j] = element1.get(i, j);
				}
			}
		}
		//Create action arrays for the child LevelSceneMovements
		boolean[] actions1 = new boolean[Environment.numberOfKeys];
		boolean[] actions2 = new boolean[Environment.numberOfKeys];
		//Populate these new arrays
		for(int i = 0; i<Environment.numberOfKeys; ++i)
		{
			//If prior to element [crossOver], take the element from one LevelSceneMovement
			if(i < crossOver)
			{
				actions1[i] = element1.getActions()[i];
				actions2[i] = element2.getActions()[i];
			}
			else //Otherwise take it from the other LevelSceneMovement
			{
				actions1[i] = element2.getActions()[i];
				actions2[i] = element1.getActions()[i];
			}
		}
		//Choose a weighting for crossing over the reward
		float probability = crossOver / (float)Environment.numberOfKeys ;
		//Take the weighted average of the two LevelSceneMovements rewards.
		int reward1 = (int)(element1.getReward() * probability + element2.getReward() * (1-probability));
		int reward2 = (int)(element2.getReward() * probability + element1.getReward() * (1-probability));
		/* 
		* Note: this reward currently doesn't affect anything as it isn't used in the fitness function and doesn't get saved.
		* It's left here in case of use in the fitness function in the future.
		*/
		//Create child LevelSceneMovements from the new data.
		LinkedList<LevelSceneMovement> result = new LinkedList<LevelSceneMovement>();
		result.add(new LevelSceneMovement(levelScene1, actions1, reward1));
		result.add(new LevelSceneMovement(levelScene2, actions2, reward2));
		return result;
	}
	/**
	 * Choose a random valid new action array for the given LevelSceneMovement.
	 */
	@Override
	public void mutate(LevelSceneMovement element)
	{
		element.mutateActions();
	}
	@Override
	public float getProbabilityOfMutation()
	{
		//How likely the mutate function is to be called on an element of a population
		return 0.01f;
	}
	@Override
	public int getSizeOfGeneration()
	{
		//How many elements are in each generation
		return 100;
	}
	@Override
	public Element toSaveFormat(LevelSceneMovement element)
	{
		//Creates an xml representation of the given LevelSceneMovement
		return element.toSaveFormat();
	}
	@Override
	public LevelSceneMovement fromSaveFormat(Element element)
	{
		//Creates a LevelSceneMovement from the given xml representation.
		return new LevelSceneMovement(element);
	}
}
