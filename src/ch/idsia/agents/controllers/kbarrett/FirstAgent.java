package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

/**
 * FirstAgent is an Agent that looks at its environment and decides which action is best using a predetermined list of what to do when.
 * @author Kim Barrett
 */
public class FirstAgent implements Agent {
	
	//DEBUG VALUES
	boolean printingLevelScene = false;
	public static boolean debug = true;
	
	String name = "FirstAgent";
	
	//Knowledge & decisions
	Movement movement;
	LevelSceneInvestigator levelSceneInvestigator;

	public FirstAgent()
	{
		movement = new Movement();
		levelSceneInvestigator = new LevelSceneInvestigator();
	}
	
	@Override
	public boolean[] getAction() {
	
		return movement.reset();
	}

	@Override
	public void integrateObservation(Environment environment) {
		
		//Update other classes with this information
			//Give Movement & LevelSceneInvestigator Mario's location
				int[] marioLoc = environment.getMarioEgoPos();
				movement.setMarioLoc(marioLoc);
				levelSceneInvestigator.setMarioLoc(marioLoc);
			
			//Give LevelSceneInvestigator the new LevelScene
				levelSceneInvestigator.setLevelScene(environment.getMergedObservationZZ(0, 0));
		
			//Find Mario from the list of all Sprites and update knowledge of how many coins he has collected
			for(Sprite s : ((MarioEnvironment)environment).getSprites())
			{
				if(s instanceof Mario)
				{
					levelSceneInvestigator.updateCoins(((Mario) s).coins);
					break;
				}
			}
		
		if(debug && printingLevelScene) {levelSceneInvestigator.printLevelScene();}
		
		//Use provided information to decide on next move
			//If Mario was jumping but has finished, he must now land
			if(movement.isJumping() && environment.isMarioOnGround())
			{
				if(FirstAgent.debug){System.out.println("LANDED");}
				movement.land();
			}
			//Decide next movement and pass this to Movement to be acted upon
			else
			{
				movement.moveTowards(
						levelSceneInvestigator.decideLocation(movement.isFacingRight()));
			}
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
}
