package ch.idsia.agents.controllers.kbarrett;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class FirstAgent implements Agent {
	
	//DEBUG VALUES
	boolean printingLevelScene = true;
	public static boolean debug = true;
	
	String name = "FirstAgent";
	
	//Knowledge & decisions
	//byte[][] levelScene;
	
	Movement movement;
	LevelSceneInvestigator levelSceneInvestigator;

	public FirstAgent()
	{
		movement = new Movement();
		levelSceneInvestigator = new LevelSceneInvestigator();
	}
	
	@Override
	public boolean[] getAction() {
		
		if(movement.isJumping())
		{
			movement.jump();
		}
		else if (!movement.actionsHaveBeenDecided());
		{
			movement.goRight();
		}
	
		return movement.reset();
	}

	@Override
	public void integrateObservation(Environment environment) {
		
		int[] marioLoc = environment.getMarioEgoPos();
		movement.setMarioLoc(marioLoc);
		levelSceneInvestigator.setMarioLoc(marioLoc);
		
		levelSceneInvestigator.setLevelScene(environment.getMergedObservationZZ(0, 0));
		
		if(printingLevelScene) {levelSceneInvestigator.printLevelScene();}
		
		if(movement.isJumping() && environment.isMarioOnGround())
		{
			if(debug){System.out.println("LANDED");}
			movement.land();
		}
		
		else
		{
			byte[] locationOfReward = levelSceneInvestigator.getRewardLocation();
			
			if(locationOfReward != null)
			{
				if(debug)
				{
					System.out.println("FOUND REWARD");
				}
				movement.moveTowards(locationOfReward);
			}
			
			else if (!movement.isJumping())
			{	
				byte[] locationOfBlockage = levelSceneInvestigator.getBlockageLocation(movement.isFacingRight());
				if(locationOfBlockage != null)
				{
					if(debug){System.out.println("FOUND BLOCKAGE");}
					byte[] requiredLocation = new byte[2];
					requiredLocation[0] = (byte) (locationOfBlockage[0] - 1);
					if(movement.isFacingRight())
					{
						requiredLocation[1] = (byte) (locationOfBlockage[1] + 1);
					}
					else
					{
						requiredLocation[1] = (byte) (locationOfBlockage[1] - 1);
					}
					movement.moveTowards(requiredLocation);
				}
			}
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
