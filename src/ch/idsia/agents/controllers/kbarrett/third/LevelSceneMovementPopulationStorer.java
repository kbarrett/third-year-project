package ch.idsia.agents.controllers.kbarrett.third;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ch.idsia.agents.controllers.kbarrett.Evolver;
/**
 * Stores all encountered {@link LevelSceneMovement}s.
 * Also responsible for saving this population to a file.
 * @author Kim Barrett
 */
public class LevelSceneMovementPopulationStorer
{
	/**
	 * Stores all encountered {@link LevelSceneMovement}s.
	 */
	private static final ArrayList<LevelSceneMovement> population = new ArrayList<LevelSceneMovement>();
	/**
	 * Lock for the population, so only one thread at a time can access it.
	 */
	static Object lock = new Object();
	/**
	 * The limit on the size of the population - if this value is exceeded, old {@link LevelSceneMovement}s are removed.
	 */
	private static final int MaxPopulationSize = 20000;
	/**
	 * The file in which the population is saved.
	 */
	private static final String levelSceneSaveFile = "level_scene_save_file.lsmpop.ser";
	/**
	 * How frequently the population is saved into the file.
	 * Note: if the program ends normally, the file is also saved when the program exits.
	 */
	private static final int saveInterval = 10000;
	/**
	 * For thread-safety, the actual list {@link #population} isn't accessible outside {@link LevelSceneMovementPopulationStorer}. 
	 * This method returns a copy of the originial list.
	 * @return a copy of {@link #population}.
	 */
	public static final ArrayList<LevelSceneMovement> getPopulationCopy()
	{
		//Create new list
		ArrayList<LevelSceneMovement> copyPop = new ArrayList<LevelSceneMovement>(population.size());
		synchronized(lock)
		{
			//Make copies of all the data and add to new list
			for(LevelSceneMovement lsm : population)
			{
				copyPop.add(lsm.clone());
			}
		}
		return copyPop;
	}
	/**
	 * Checks whether {@link #population} is currently empty.
	 * Note: this method is thread-safe.
	 * @return true if the list is empty.
	 */
	private static final boolean checkEmpty()
	{
		synchronized (lock)
		{
			return population.isEmpty();
		}
	}
	/**
	 * Adds the members of the given Collection to {@link #population}.
	 * @param collection - Collection containing {@link LevelSceneMovement}s to add to the list.
	 * @see #addNew(LevelSceneMovement)
	 */
	public static final void addNew(Collection<LevelSceneMovement> collection)
	{
		//For each member of the collection, add it to the list
		for(LevelSceneMovement levelSceneMovement : collection)
		{
			addNew(levelSceneMovement);
		}
	}
	/**
	 * Add the given element to the list {@link #population}.
	 * Note: this method is thread-safe.
	 * @param newElement - the {@link LevelSceneMovement} to be added.
	 */
	public static final void addNew(LevelSceneMovement newElement)
	{
		synchronized(lock)
		{
			//Find whether we have encountered this element before.
			int previousInstanceIndex = population.indexOf(newElement);
			//If we haven't had any information relating to this LevelScene yet
			if(previousInstanceIndex < 0)
			{
				//Add a copy of it to population
				population.add(newElement.clone());
			}
			//If we have encountered this LSM before
			else
			{
				//Remove the previous instance from the list
				//This ensures the list is in order of most recently encountered to accessed least recently
				LevelSceneMovement previousInstance = population.remove(previousInstanceIndex);
				if(
						Arrays.equals(previousInstance.getActions(),newElement.getActions())			   //If the actions are the same, update the reward
						|| (previousInstance.getReward() == newElement.getReward() && Math.random() < 0.5) //Take equal rewarded actions with prob 0.5
						|| (previousInstance.getReward() < newElement.getReward())						   //Always take better rewarded actions
						)
				{
					//Update the action and reward associated with this LevelScene
					previousInstance.setActions(newElement.getActions(), newElement.getReward());
				}
				//Add this (possibly updated) LSM to the list
				population.add(previousInstance);
			}
			//If the list is too big
			while(population.size() > MaxPopulationSize)
			{
				//Remove the element that was last accessed the longest ago.
				population.remove(0);
			}
		}
	}
	/**
	 * Begins a thread that saves the population every {@value #saveInterval} milliseconds.
	 * Note: although access to {@link #population} is thread-safe, access to the save file: 
	 * {@value #levelSceneSaveFile} is not - so multiple calls of this method may corrupt the data.
	 */
	public static final void save()
	{
		//Create a new thread that will be responsible for the saving.
		final Thread thread = new Thread(new Runnable(){

			@Override
			public void run()
			{
				//While the program is running
				while(true)
				{
					try
					{
						//If the population list is not empty (otherwise there is nothing to do)
						if(!checkEmpty())
						{
							//Take a copy of the list & save it to the given file
							LSMLoadSave.saveToFile(levelSceneSaveFile, getPopulationCopy());
						}
						//Wait for some time before saving again
						Thread.sleep(saveInterval);
					}
					catch (Exception e)
					{
						//If something goes wrong
						System.err.println("Error saving population.");
						e.printStackTrace();
					}
				}

			}});
		//It is less important that others as a delayed save is acceptable to give more important threads a chance to perform their actions.
		thread.setPriority(Thread.MIN_PRIORITY);
		//Start the thread
		thread.start();
	}
	/**
	 * Set up this class to be ready to use.
	 * Includes loading the population from the file into the internal {@link #population} list.
	 */
	public static final void initialise()
	{
		//If the list is empty, then this is the first call to this method during this run of the program, so some initialisation is needed
		if(checkEmpty())
		{
			try
			{
				//Load the saved population into the list
				System.out.println("Loading population...");
				//Temporary list
				ArrayList<LevelSceneMovement> list = new ArrayList<LevelSceneMovement>();
				LSMLoadSave.loadFromFile(levelSceneSaveFile, list);
				//Use the thread-safe method to add these LSMs to the global list
				addNew(list);
			} 
			catch (Exception e)
			{
				System.err.println("Error loading population: " + e.getMessage());
			}
			//Begin the save loop
			save();
		}
		//Otherwise this the class has been used before, so no initialisation is necessary.
	}
}
