package ch.idsia.agents.controllers.kbarrett.first;

import java.util.Stack;

import ch.idsia.agents.controllers.kbarrett.first.MapSquare.Direction;

/**
 * @deprecated
 * @author Kim
 *
 */
public class MovementInstruction
{
	MapSquare squareToMoveTo;
	/**
	 * The direction from which Mario enters this square.
	 */
	Direction directionEntered;
	//Direction directionExitted;
	int numberOfStepsInSameDirection = 0;
	
	public MapSquare getSquareToMoveTo()
	{
		return squareToMoveTo;
	}
	public int[] getLocationToMoveTo()
	{
		return squareToMoveTo != null ? squareToMoveTo.getMapLocation() : null;
	}
	public Direction getDirectionEntered()
	{
		return directionEntered;
	}
	public int getNumberOfStepsInSameDirection()
	{
		return numberOfStepsInSameDirection;
	}
	
	public MovementInstruction(MapSquare marioSquare, Stack<MapSquare> plan)
	{
		if(plan == null)
		{
			squareToMoveTo = null;
			directionEntered = Direction.None;
		}
		else
		{
			squareToMoveTo = plan.peek();

			/*if(this.squareToMoveTo.getSquareAbove().equals(plan.get(plan.size() - 2)))
			{
				directionExitted = 
			}
			this.directionExitted = */
			
			calculateDirectionEntered(marioSquare, squareToMoveTo);
			
			setNumberOfStepsInSameDirection(directionEntered, plan);
		}
	}
	
	private void calculateDirectionEntered(MapSquare marioSquare, MapSquare requiredLocation)
	{
		if(marioSquare.getSquareAbove().equals(requiredLocation))
		{
			directionEntered = Direction.Below;
		}
		
	}
	
	private void setNumberOfStepsInSameDirection(Direction directionEntered, Stack<MapSquare> plan)
	{
		int i = plan.size() - 2;
		
		switch(directionEntered)
		{
		case Above :
		{ 
			while(plan.get(i - 1).getSquareAbove().equals(plan.get(i)))
			{
				++numberOfStepsInSameDirection;
				--i;
			}
			break;
		}
		case Below :
		{
			while(plan.get(i - 1).getSquareBelow().equals(plan.get(i)))
			{
				++numberOfStepsInSameDirection;
				--i;
			}
			break;
		}
		case Right :
		{
			while(plan.get(i - 1).getSquareRight().equals(plan.get(i)))
			{
				++numberOfStepsInSameDirection;
				--i;
			}
			break;
		}
		case Left :
		{
			while(plan.get(i - 1).getSquareLeft().equals(plan.get(i)))
			{
				++numberOfStepsInSameDirection;
				--i;
			}
			break;
		}
		}
	}
}
