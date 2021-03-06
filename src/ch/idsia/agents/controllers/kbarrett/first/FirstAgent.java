package ch.idsia.agents.controllers.kbarrett.first;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * FirstAgent is an Agent that looks at its environment and decides which action is best using a predetermined list of what to do when.
 * @author Kim Barrett
 * @see ch.idsia.agents.Agent
 */
public class FirstAgent implements Agent 
{	
	String name = "FirstAgent";
	
	//Classes for storing knowledge & making decisions
	ManualJumpMovement movement;
	LevelSceneInvestigator levelSceneInvestigator;

	public FirstAgent()
	{
		movement = new ManualJumpMovement();
		levelSceneInvestigator = new LevelSceneInvestigator();
	}
	
	/**
	 * Used to return the required actions to the game.
	 * @return boolean array of size 6. Each element in the array represents a 
	 * movement: true indicates it is being requested during this frame & false 
	 * means it is not. 
	 * From 0 to 5 the corresponding movements are: left, right, down, jump, speed, up.
	 */
	@Override
	public boolean[] getAction()
	{
		boolean[] array = movement.reset();
		//Store each action made so they can be repeated on the same level for observation purposes.
		FirstAgentRecorder.getAction(array);
		return array;
	}

	/**
	 * Used to update the Agent with information about the environment.
	 * This gets called by the game every frame & passes the updated Environment
	 * to the Agent to allow it to update its knowledge of the world (such as
	 * where remaining enemies, coins and blocks are).
	 * @param environment containing updated knowledge
	 */
	@Override
	public void integrateObservation(Environment environment) {
		
		//Update other classes with this information
			//Give Movement & LevelSceneInvestigator Mario's location
				levelSceneInvestigator.setMarioLoc(environment.getMarioEgoPos());
			
			//Give LevelSceneInvestigator the new LevelScene & Mario's new screen position
				levelSceneInvestigator.setMarioScreenPos(environment.getMarioFloatPos());
				levelSceneInvestigator.updateMapFromLevelScene(environment.getMergedObservationZZ(0, 0));
				levelSceneInvestigator.setMarioMode(environment.getMarioMode());
		
				movement.setMarioMapLoc(levelSceneInvestigator.getMarioMapLoc());
		
		//Use provided information to decide on next move
				if(movement.isJumping() && environment.isMarioOnGround())
				{
					movement.land();
				}
				MapSquare nextLoc = levelSceneInvestigator.getNextLocation(movement.isJumping());
				if(nextLoc == null)
				{
					movement.moveTowards(null);
				}
				else
				{
					movement.moveTowards(nextLoc.getMapLocation());
				}
				movement.isEnemy(levelSceneInvestigator.isEnemy() && environment.isMarioAbleToShoot());
	}

	/**
	 * Used to give the current score to the agent.
	 * Not needed by this agent.
	 */
	@Override
	public void giveIntermediateReward(float intermediateReward) {}

	/**
	 * Used to reset the agent at the end of an episode.
	 */
	@Override
	public void reset()
	{
		movement.reset();
	}

	/**
	 *  Tells {@link #levelSceneInvestigator} to create a new empty map.
	 *  This method is used to tell us how big the area Mario can "see" in the 
	 *  Environment & where initially he is within this area. However we 
	 *  dynamically increase the size of our map of the world, so no benefit is
	 *  gained by doing this before we have any data to put in it. We also get
	 *  the egoRow/egoCol every time we get a new LevelScene, so we don't need 
	 *  to store this initial one.
	 */
	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol)
	{
		levelSceneInvestigator.createEmptyMap();
	}

	/**
	 * Gets the name of the agent.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the agent.
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
}
