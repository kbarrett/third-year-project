package ch.idsia.agents.controllers.kbarrett;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.JDOMException;

public class LevelSceneMovementPopulationStorer
{
	private static final ArrayList<LevelSceneMovement> population = new ArrayList<LevelSceneMovement>();
	
	private static Evolver<LevelSceneMovement> evolver;
	
	private static final String levelSceneSaveFile = "src/ch/idsia/agents/controllers/kbarrett/level_scene_save_file.txt";
	
	public static final ArrayList<LevelSceneMovement> getPopulation()
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
		//removes any other LevelSceneMovements with the same levelScene and actions
		population.remove(newElement);
		//adds the new one - with updated fitness value
		population.add(newElement);
	}
	
	public static final void save()
	{
		final Object lock = new Object();
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
						synchronized (lock)
						{
							lock.wait(10000);
						}
					} catch (IOException e)
					{
						System.err.println("Error saving population.");
						e.printStackTrace();
					} catch (InterruptedException e)
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
				System.err.println("Error loading population");
				e.printStackTrace();
			}
			save();
		}
	}
}
