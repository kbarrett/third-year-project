package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import ch.idsia.agents.controllers.kbarrett.Encoding;

public class PlanStorer {
	
	/**
	 * Stores the current plan of movement that Mario is executing.
	 * FIXME: should be private!!!!!!!!!
	 */
	public Stack<MapSquare> plan;
	
	private boolean importantPlan = true;

	public boolean isImportant()
	{
		return importantPlan;
	}
	public boolean havePlan()
	{
		return plan!=null && !plan.isEmpty();
	}
	public MapSquare getNextPlanLocation()
	{
		if(plan == null || plan.size() == 0) {return null;}
		return plan.peek();
	}
	/**
	 * Makes a plan from the current position {@link #marioMapLoc} to the desiredPosition and stores it in {@link #plan}.
	 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
	 */
	public void makePlan(MapSquare desiredPosition, MapSquare startLocation, boolean important, int marioMode)
	{	
		if(plan!=null)
		{
			plan.clear();
		}
		if(desiredPosition == null) {return;}

		Stack<MapSquare> newPlan = Search.aStar(desiredPosition, startLocation, marioMode);
		
		if(newPlan != null)
		{
			importantPlan = important;
			plan = newPlan;
		}
	}
	public void makePlan(MapSquare desiredPosition, MapSquare startLocation, int marioMode)
	{
		makePlan(desiredPosition, startLocation, true, marioMode);
	}
	/**
	 * Checks whether any MapSquare in the plan has become an Environment piece.
	 * If so the plan is invalid, so it removes it.
	 */
	public void checkPlan()
	{
		if(havePlan())
		{
			for(int i = 0; i < plan.size(); ++i)
			{
				if(Encoding.isEnvironment(plan.get(i)))
				{
					plan.clear();
					return;
				}
			}
		}
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
			/*MapSquare loc = levelSceneInvestigator.checkForEnemies(4, 4);
			if(loc!=null)
			{
				makePlan(loc, marioCurrentLocation);
				if(plan != null && plan.size() > 0)
				{
					return getLocationToMoveTo(marioCurrentLocation, levelSceneInvestigator);
				}
			}*/
			return null;
		}
		
		//This is the next step of the plan
		MapSquare nextLocation = plan.peek();
		/*if(plan.size() == 0)
		{
			return levelSceneInvestigator.checkForEnemies(4, 4);
		}
			
		MapSquare enemies = levelSceneInvestigator.checkForEnemies(nextLocation.getMapLocationX(), nextLocation.getMapLocationY());
		if(enemies!=null)
		{
			plan.push(levelSceneInvestigator.getMapSquare(enemies.getMapLocationY(),enemies.getMapLocationX()));
			return enemies;
		}*/
		
		//Return it
		return shiftLoc(nextLocation);
	}
	private MapSquare shiftLoc(MapSquare nextLocation)
	{
		if(false && Encoding.isEnvironment(nextLocation.getSquareRight()) && Encoding.isEnvironment(nextLocation.getSquareLeft()))
		{
			if(plan.size() > 2)
			{
				int i = plan.size() - 2;
				int distance = 0;
				while(i >= 0)
				{
					if(!Encoding.isEnvironment(plan.get(i).getSquareRight()) && Encoding.isEnvironment(plan.get(i).getSquareLeft()))
					{
						break;
					}
					distance = nextLocation.getMapLocationX() - plan.get(i).getMapLocationX();
					if(distance != 0)
					{
						MapSquare shiftedSquare = new MapSquare(nextLocation.getEncoding(), null, nextLocation.getMapLocationX() + distance, nextLocation.getMapLocationY());
						return shiftedSquare;
					}
					--i;
				}
			}
		}
		return nextLocation;
	}
	/**
	 * Attempts to find a way for Mario to get back on track with {@link #plan}.
	 * If no way is found, {@link #plan} will be removed.
	 */
	public void replan(MapSquare marioCurrentLocation, int marioMode)
	{
		//How much longer the new plans are allowed to be than the previous plan
		int adjustment = 5;	
		//Stores the best plan to rejoin the previous plan
		Vector<MapSquare> newPlan = new Vector<MapSquare>();
		//Stores the square at which the best plan joins the previous plan
		int rejoinSquare = -1;
		
		//For each step in the plan
		for(int i = plan.size() - 1; i >= 0 ; --i)
		{
			//Find a new plan to this square from the current square, using the same number of steps (+ the adjustment value)
			Stack<MapSquare> thisPlan = Search.aStar(plan.get(i), marioCurrentLocation, plan.size() - i + adjustment, marioMode);
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
				break;
			}
		}
		
		if(rejoinSquare < 0) //we failed to find a new route
		{
			//System.err.println("Plan unachievable. Can't reach " + plan.peek() + " from " + marioCurrentLocation);
			//remove plan as it is unachievable from the current position
			plan.clear();
		}
		else //we have found a new route
		{
			//LevelSceneInvestigator.debugPrint("Replanning from square " + rejoinSquare);
			//Remove all steps in the plan before the point at which the new plan joins the old plan

			boolean d = FirstAgent.debug;
			int i0=0;
			while(plan.size() > rejoinSquare)
			{
				plan.pop();
			}
			//Add the steps of the new plan to the old plan
			for(int i = 0; i < newPlan.size(); ++i)
			{
				plan.push(newPlan.elementAt(i));
			}
			//LevelSceneInvestigator.debugPrint("new plan: " + plan.toString());
		}
	}

	/**
	 * After a move has been made, decides whether we're in the square we thought we would be after that move.
	 * If true, we remove that step from the plan.
	 * @return boolean indicating whether we're in the expected square or not.
	 */
	public boolean isPlanStepAchieved(MapSquare marioCurrentLocation)
	{
		if(!havePlan())
		{
			return true;
		}
		MapSquare nextPlanSquare = plan.peek();
		//If we're in the square of the next step in the plan, then we're in the expected square
		if(marioCurrentLocation.getMapLocationX() == nextPlanSquare.getMapLocationX() && marioCurrentLocation.getMapLocationY() == nextPlanSquare.getMapLocationY())
		{
			//Remove the move we have just made from the stack
			plan.pop();
			return true;
		}
		return false;
	}
	
	/**
	 * Avoids the given squares - generally used to avoid squares containing enemies
	 */
	public void avoid(List<MapSquare> squares, ArrayList<ArrayList<MapSquare>> map, int marioMode)
	{
		for(MapSquare planSquare : plan)
		{
			if(squares.contains(planSquare))
			{
				MapSquare squaresSquare = squares.get(squares.indexOf(planSquare)); //get the correct square out of squares
				
				MapSquare sq = new MapSquare(squaresSquare.getEncoding(), map, squaresSquare.getMapLocationX(), squaresSquare.getMapLocationY() - 1); //square above enemy
				Stack<MapSquare> newPlan = Search.aStar(sq, plan.peek(), marioMode);
				if(newPlan != null) //if a plan was found
				{
					MapSquare cur;
					do
					{
						cur = plan.pop();
					}
					while(!cur.equals(squaresSquare));
					
					while(newPlan.size() > 0)
					{
						plan.push(newPlan.remove(newPlan.size() - 1));
					}
					
					return;
				}
			}
		}
	}
}
