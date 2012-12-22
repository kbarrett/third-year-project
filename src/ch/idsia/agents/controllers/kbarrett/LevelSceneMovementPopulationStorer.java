package ch.idsia.agents.controllers.kbarrett;

import java.util.Collection;
import java.util.TreeSet;

public class LevelSceneMovementPopulationStorer
{
	private static final TreeSet<LevelSceneMovement> population = new TreeSet<LevelSceneMovement>();
	
	public static final TreeSet<LevelSceneMovement> getPopulation()
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
}
