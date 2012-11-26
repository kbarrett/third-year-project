package ch.idsia.agents.controllers.kbarrett;

import java.util.Stack;
import java.util.Vector;

public class PlanStorer {
	
	/**
	 * Stores the current plan of movement that Mario is executing.
	 */
	private Stack<MapSquare> plan;

	
	public boolean havePlan()
	{
		return plan!=null && !plan.isEmpty();
	}
	public MapSquare getNextPlanLocation()
	{
		return plan.peek();
	}
	/**
	 * Makes a plan from the current position {@link #marioMapLoc} to the desiredPosition and stores it in {@link #plan}.
	 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
	 */
	public void makePlan(MapSquare desiredPosition, MapSquare startLocation)
	{
		if(plan!=null)
		{
			plan.clear();
		}
		if(desiredPosition == null) {return;}
		//plan = Search.aStar(map.get(desiredPosition[0]).get(desiredPosition[1]), map.get(marioMapLoc[0]).get(marioMapLoc[1]));
		plan = Search.aStar(desiredPosition, startLocation);
		
		if(plan!=null) LevelSceneInvestigator.debugPrint("Made new plan of size " + plan.size());
	}
	/**
	 * Gets the next step in the {@link #plan} (if one exists).
	 * @return int array of size 2 representing the location of the next step of the plan.
	 */
	public MapSquare getLocationToMoveTo(MapSquare marioCurrentLocation, LevelSceneInvestigator levelSceneInvestigator)
	{
		//If we have no plan, we cannot get the next step from it.
		if(plan == null || plan.size() == 0)
		{
			MapSquare loc = levelSceneInvestigator.checkForEnemies(4, 4);
			if(loc!=null)
			{
				makePlan(loc, marioCurrentLocation);
				if(plan != null && plan.size() > 0)
				{
					return getLocationToMoveTo(marioCurrentLocation, levelSceneInvestigator);
				}
			}
			return null;
		}
		
		LevelSceneInvestigator.debugPrint(plan.toString());
		
		//This is the next step of the plan
		MapSquare nextLocation = plan.peek();
		//while(Encoding.isEnvironment(nextLocation.getEncoding()) && nextLocation.getEncoding()!= 0)
		/*if(marioCurrentLocation.equals(nextLocation))
		{
			plan.pop();
			if(plan.size() == 0)
			{
				return levelSceneInvestigator.checkForEnemies(4, 4);
			}
			nextLocation = plan.peek();
		}*/
		
		//FIXME: jumping workaround as currently Movement judges size of jump based on distance from current square
			int i = plan.lastIndexOf(nextLocation);
			MapSquare marioMapLocSquare = marioCurrentLocation.clone();
			//While the next plan square is above this one, get the next one
			while(marioMapLocSquare!=null && marioMapLocSquare.getSquareAbove() == nextLocation)
			{
				//If we get to the end of the plan, stop looking
				if(i < 0) {break;}
				//Replace the current square with the next plan square
				marioMapLocSquare = nextLocation;
				//get the next square in the plan
				nextLocation = plan.elementAt(i--);
			}
			
		MapSquare enemies = levelSceneInvestigator.checkForEnemies(nextLocation.getMapLocationX(), nextLocation.getMapLocationY());
		if(enemies!=null)
		{
			plan.push(levelSceneInvestigator.getMapSquare(enemies.getMapLocationY(),enemies.getMapLocationX()));
			return enemies;
		}
		
		//Return it
		return nextLocation;
	}
	/**
	 * Attempts to find a way for Mario to get back on track with {@link #plan}.
	 * If no way is found, {@link #plan} will be removed.
	 */
	public void replan(MapSquare marioCurrentLocation)
	{
		//How much longer the new plans are allowed to be than the previous plan
		int adjustment = 0;	
		//Stores the best plan to rejoin the previous plan
		Vector<MapSquare> newPlan = new Vector<MapSquare>();
		//Stores the square at which the best plan joins the previous plan
		int rejoinSquare = -1;
		
		FirstAgent.debug = false;
		//For each step in the plan
		for(int i = 0; i<plan.size(); ++i)
		{
			//Find a new plan to this square from the current square, using the same number of steps (+ the adjustment value)
			Stack<MapSquare> thisPlan = Search.aStar(plan.get(i), marioCurrentLocation, plan.size() - i + adjustment);
			if(
					//we have successfully found a route to the required square
					thisPlan != null &&
					(
							//we haven't already made a new plan
							rejoinSquare < 0 || 
							//this plan is shorter than the previous plan
							thisPlan.size() + i < newPlan.size() + rejoinSquare
					)
				)
			{
				//Update the best plan
				rejoinSquare = i;
				newPlan = thisPlan;
			}
		}
		FirstAgent.debug = true;
		
		if(rejoinSquare < 0) //we failed to find a new route
		{
			System.err.println("Plan unachievable. Can't reach " + plan.peek() + " from " + marioCurrentLocation);
			//remove plan as it is unachievable from the current position
			plan.clear();
		}
		else //we have found a new route
		{
			LevelSceneInvestigator.debugPrint("Replanning from square " + rejoinSquare);
			//Remove all steps in the plan before the point at which the new plan joins the old plan
			for(int i = rejoinSquare; i < plan.size(); ++i)
			{
				plan.pop();
			}
			//Add the steps of the new plan to the old plan
			for(int i = newPlan.size() - 1; i >= 0; --i)
			{
				plan.push(newPlan.elementAt(i));
			}
			LevelSceneInvestigator.debugPrint("new plan: " + plan.toString());
		}
	}

	/**
	 * After a move has been made, decides whether we're in the square we thought we would be after that move.
	 * @return boolean indicating whether we're in the expected square or not.
	 */
	public boolean isPlanStepAchieved(MapSquare marioCurrentLocation)
	{
		//If we're in the square of the next step in the plan, then we're in the expected square
		if(marioCurrentLocation.getMapLocationX() == plan.peek().getMapLocationY() && marioCurrentLocation.getMapLocationY() == plan.peek().getMapLocationX())
		{
			//Remove the move we have just made from the stack
			plan.pop();
			return true;
		}
		return false;
	}
}
