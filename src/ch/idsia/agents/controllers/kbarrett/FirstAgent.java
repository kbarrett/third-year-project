package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

/**
 * FirstAgent is an Agent that looks at its environment and decides which action is best using a predetermined list of what to do when.
 * @author Kim Barrett
 * @see ch.idsia.agents.Agent
 */
public class FirstAgent implements Agent {
	
	//DEBUG VALUES
	/**
	 * Remove this to find everywhere where a debug instruction has been entered.
	 */
	public static boolean debug = true;
	
	String name = "FirstAgent";
	
	//Classes for storing knowledge & making decisions
	Movement movement;
	LevelSceneInvestigator levelSceneInvestigator;

	public FirstAgent()
	{
		movement = new Movement();
		levelSceneInvestigator = new LevelSceneInvestigator();
	}
	
	/**
	 * Used to return the required actions to the game.
	 * @return boolean array of size 6. Each element in the array represents a 
	 * movement: true indicates it is being requested during this frame & false 
	 * means it is not. 
	 * From 0 to 5 the corresponding movements are: left, right, down, jump, speed, up.
	 * @see ch.idsia.agents.Agent#getAction()
	 */
	@Override
	public boolean[] getAction() {
	
		return movement.reset();
	}

	/**
	 * Used to update the Agent with information about the environment.
	 * This gets called by the game every frame & passes the updated Environment
	 * to the Agent to allow it to update its knowledge of the world (such as
	 * where remaining enemies, coins and blocks are).
	 * @param environment containing updated knowledge 
	 * @see ch.idsia.agents.Agent#integrateObservation(ch.idsia.benchmark.mario.environments.Environment)
	 */
	@Override
	public void integrateObservation(Environment environment) {
		
		//Update other classes with this information
			//Give Movement & LevelSceneInvestigator Mario's location
				int[] marioLoc = environment.getMarioEgoPos();
				levelSceneInvestigator.setMarioLoc(environment.getMarioEgoPos(), movement);
			
			//Give LevelSceneInvestigator the new LevelScene
				levelSceneInvestigator.setLevelScene(environment.getMergedObservationZZ(0, 0), environment.getMarioFloatPos());
		
			//Update knowledge of how many coins Mario has collected
				levelSceneInvestigator.updateCoins(Mario.coins);
		
		//Use provided information to decide on next move
			//If Mario was jumping but has landed, pass this information to Movement
			if(movement.isJumping() && environment.isMarioOnGround())
			{
				movement.land();
			}
			//Decide next movement and pass this to Movement to be acted upon
			else
			{
				movement.moveTowards(
						levelSceneInvestigator.decideLocation(movement.isFacingRight()));
			}
	}

	/**
	 * Used to tell Mario when he has done a good move & when he hasn't.
	 * Examples:
	 * Increases if coins, mushrooms or hidden blocks are collected; or if 
	 * enemies are stomped on. Decreases if you collide with an enemy.
	 * @param intermediateReward gives the new value of the reward
	 * @see ch.idsia.agents.Agent#giveIntermediateReward(float)
	 */
	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	/**
	 * Used to reset the agent at the end of an episode.
	 * @see ch.idsia.agents.Agent#reset()
	 */
	@Override
	public void reset() {
		movement.reset();
	}

	/**
	 * Used to pass information about how large the area is that Mario can perceive.
	 * @see ch.idsia.agents.Agent#setObservationDetails(int, int, int, int)
	 */
	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {
		//Pass this information to the levelSceneInvestigator, so it can create an initial map of the correct size.
		levelSceneInvestigator.giveMapSize(rfWidth, rfHeight);
	}

	/**
	 * Gets the name of the agent.
	 * @see ch.idsia.agents.Agent#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the agent.
	 * @see ch.idsia.agents.Agent#getName(String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
}
