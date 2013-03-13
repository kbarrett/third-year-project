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

public class FirstAgentRecorder implements Agent
{
	private static LinkedList<boolean[]> actions = new LinkedList<boolean[]>();
	private static String filename = "firstagent.act";
	
	public static void getAction(boolean[] array)
	{
		actions.add(array);
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

	@Override
	public boolean[] getAction()
	{
		return actions.remove();
	}

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
		return "";
	}

	@Override
	public void setName(String name)
	{
	}
	
	

}
