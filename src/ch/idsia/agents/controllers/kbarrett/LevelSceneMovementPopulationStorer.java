package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.Collection;

public class LevelSceneMovementPopulationStorer
{
	private static final ArrayList<LevelSceneMovement> population = new ArrayList<LevelSceneMovement>();
	static Object lock = new Object();
	
	private static Evolver<LevelSceneMovement> evolver;
	
	private static final String levelSceneSaveFile = "src/ch/idsia/agents/controllers/kbarrett/level_scene_save_file.lsmpop.ser";
	
	private static final int saveInterval = 10000;
	
	public static final ArrayList<LevelSceneMovement> getPopulationCopy()
	{
		ArrayList<LevelSceneMovement> copyPop = new ArrayList<LevelSceneMovement>(population.size());
		synchronized(lock)
		{
			for(LevelSceneMovement lsm : population)
			{
				copyPop.add(lsm.clone());
			}
		}
		return copyPop;
	}
	
	private static final boolean checkEmpty()
	{
		synchronized (lock)
		{
			return population.isEmpty();
		}
	}
	
	public static final void addNew(Collection<LevelSceneMovement> collection)
	{
		for(LevelSceneMovement levelSceneMovement : collection)
		{
			addNew(levelSceneMovement);
		}
	}
	
	public static final void addNew(LevelSceneMovement newElement)
	{
		synchronized(lock)
		{
			int previousInstanceIndex = population.indexOf(newElement);
			if(previousInstanceIndex < 0) //we haven't had any information relating to this LevelScene yet
			{
				population.add(newElement.clone());
			}
			else
			{
				LevelSceneMovement previousInstance = population.get(previousInstanceIndex);
				if(previousInstance.getReward() < newElement.getReward())
				{
					previousInstance.setActions(newElement.getActions(), newElement.getReward());
				}
			}
		}
	}
	
	public static final void save()
	{
		final Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					try
					{
						if(!checkEmpty())
						{
							LSMLoadSave.saveToFile(levelSceneSaveFile, getPopulationCopy(), evolver);
						}
						Thread.sleep(saveInterval);
					} catch (Exception e)
					{
						System.err.println("Error saving population.");
						e.printStackTrace();
					}
				}

			}});
		thread.start();
	}
	
	public static final void initialise(Evolver<LevelSceneMovement> evolver)
	{
		if(checkEmpty())
		{
			LevelSceneMovementPopulationStorer.evolver = evolver;
			try
			{
				ArrayList<LevelSceneMovement> list = new ArrayList<LevelSceneMovement>();
				LSMLoadSave.loadFromFile(levelSceneSaveFile, list, evolver);
				addNew(list);
			} catch (Exception e)
			{
				System.err.println("Error loading population: " + e.getMessage());
			}
		}
		
		save();
	}
}
