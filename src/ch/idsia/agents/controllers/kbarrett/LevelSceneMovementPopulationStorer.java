package ch.idsia.agents.controllers.kbarrett;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LevelSceneMovementPopulationStorer
{
	//FIXME: Change me to use synchronized (probably.. who knows?)
	private static final CopyOnWriteArrayList<LevelSceneMovement> population = new CopyOnWriteArrayList<LevelSceneMovement>();
	
	private static Evolver<LevelSceneMovement> evolver;
	
	private static final String levelSceneSaveFile = "src/ch/idsia/agents/controllers/kbarrett/level_scene_save_file.lsmpop";
	
	public static final int saveInterval = 10000;
	
	public static final CopyOnWriteArrayList<LevelSceneMovement> getPopulation()
	{
		return population;
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
	
	public static final void save()
	{
		final Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					try
					{
						if(!population.isEmpty())
						{
							LoadSave.saveToFile(levelSceneSaveFile, population, evolver);
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
		if(population.isEmpty())
		{
			LevelSceneMovementPopulationStorer.evolver = evolver;
			try
			{
				LoadSave.loadFromFile(levelSceneSaveFile, population, evolver);
			} catch (Exception e)
			{
				System.err.println("Error loading population: " + e.getMessage());
			}
		}
		
		save();
	}
}
