package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import ch.idsia.agents.controllers.kbarrett.Encoding;

public class PlanStorer {
	
	/**
	 * Stores the current plan of movement that Mario is executing.
	 */
	public Stack<MapSquare> plan;
	/**
	 * Whether the current plan is important.
	 * I.e. whether it is heading towards a coin (or other "good" location) as opposed to just the RHS of the screen.
	 */
	private boolean importantPlan = true;
	/**
	 * @return whether the current plan is important.
	 * I.e. whether it is heading towards a coin (or other "good" location) as opposed to just the RHS of the screen.
	 */
	public boolean isImportant()
	{
		return importantPlan;
	}
	/**
	 * @return whether a plan is currently being stored.
	 */
	public boolean havePlan()
	{
		return plan!=null && !plan.isEmpty();
	}
	/**
	 * @return the next location in the plan.
	 */
	public MapSquare getNextPlanLocation()
	{
		if(plan == null || plan.size() == 0) {return null;}
		return plan.peek();
	}
	/**
	 * Makes a plan from the current position {@link #marioMapLoc} to the desiredPosition and stores it in {@link #plan}.
	 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
	 * @param startLocation - the location the plan should start at - generally this should be Mario's current location
	 * @param important - whether the given plan is important
	 * @param marioMode - Mario's current mode.
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
	/**
	 * Makes a plan from the current position {@link #marioMapLoc} to the desiredPosition and stores it in {@link #plan}.
	 * Assumes the plan created will be an {@link #importantPlan}.
	 * @param desiredPosition - a int array of size 2 representing the location that the plan should head towards
	 * @param startLocation - the location the plan should start at - generally this should be Mario's current location
	 * @param marioMode - Mario's current mode.
	 */
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
	 * @return MapSquare representing the location of the next step of the plan.
	 */
	public MapSquare getLocationToMoveTo(MapSquare marioCurrentLocation, LevelSceneInvestigator levelSceneInvestigator)
	{
		//If we have no plan, we cannot get the next step from it.
		if(plan == null || plan.size() == 0)
		{
			return null;
		}
		//Otherwise return the next step of the plan
		return plan.peek();
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
		for(int i = plan.size() - 1; i >= Math.max(0, plan.size() - adjustment) ; --i)
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
			//remove plan as it is unachievable from the current position
			plan.clear();
		}
		else //we have found a new route
		{
			//Remove all steps in the plan before the point at which the new plan joins the old plan
			while(plan.size() > rejoinSquare)
			{
				plan.pop();
			}
			//Add the steps of the new plan to the old plan
			for(int i = 0; i < newPlan.size(); ++i)
			{
				plan.push(newPlan.elementAt(i));
			}
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
	 * @param squares - the squares to avoid
	 * @param map - the map in which the plan should be made
	 * @param marioMode - the mode Mario is currently in
	 * @param checkLength - the number of steps in the plan that should be checked whether they are affected.
	 */
	public void avoid(List<MapSquare> squares, ArrayList<ArrayList<MapSquare>> map, int marioMode, int checkLength)
	{
		for(int i = plan.size() - 1; i >= plan.size() - (1 + checkLength); ++i)
		{
			MapSquare planSquare = plan.get(i);
			//If the step in the plan is one to be avoiding
			if(squares.contains(planSquare))
			{
				MapSquare squaresSquare = squares.get(squares.indexOf(planSquare)); //get the correct square out of squares
				
				MapSquare sq = new MapSquare(squaresSquare.getEncoding(), map, squaresSquare.getMapLocationX(), squaresSquare.getMapLocationY() - 1); //square above enemy
				Stack<MapSquare> newPlan = Search.aStar(sq, plan.peek(), marioMode);
				if(newPlan != null) //if a plan was found
				{
					//Update the global plan to avoid this square.
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
