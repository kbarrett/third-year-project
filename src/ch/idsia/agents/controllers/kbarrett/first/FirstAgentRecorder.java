package ch.idsia.agents.controllers.kbarrett.first;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Stores the actions made by {@link FirstAgent} when playing a level, so they can be repeated without calculation.
 * Only used for recording videos.
 * @author Kim
 *
 */
public class FirstAgentRecorder implements Agent
{
	/**
	 * The actions made by Mario.
	 */
	private static LinkedList<boolean[]> actions = new LinkedList<boolean[]>();
	/**
	 * Where to store the actions.
	 */
	private static String filename = "firstagent.act";
	/**
	 * Used by {@link FirstAgent} to store the actions made.
	 * @param array
	 */
	public static void getAction(boolean[] array)
	{
		//Store the action.
		actions.add(array);
		//Save the array - we don't know when the level will end, so cannot just save the list at the end.
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
			oos.writeObject(actions);
			oos.close();
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * Loads previously made actions from the file ready to be used by the Agent.
	 */
	public FirstAgentRecorder()
	{
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
			actions = ((LinkedList<boolean[]>)ois.readObject());
			ois.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Returns and removes the next action in the array.
	 */
	@Override
	public boolean[] getAction()
	{
		return actions.remove();
	}
	
	//The following methods aren't needed by this agent as it only repeats pre-calculated moves.
		@Override
		public void integrateObservation(Environment environment) {}
		@Override
		public void giveIntermediateReward(float intermediateReward) {}
		@Override
		public void reset() {}
		@Override
		public void setObservationDetails(int rfWidth, int rfHeight, int egoRow,int egoCol) {}

	@Override
	public String getName()
	{
		return "FirstAgentRecorder";
	}
	@Override
	public void setName(String name)
	{
	}
	
	

}
