package ch.idsia.agents.controllers.kbarrett.first;

import java.awt.geom.Point2D;
import java.util.Stack;

import ch.idsia.agents.controllers.kbarrett.first.MapSquare.Direction;
/**
 * Used by {@link Search} to store the heuristic values for an encountered {@link MapSquare}.
 * @author Kim Barrett
 */
public class MapSquareWrapper
{
	/** The MapSquare represented by this instance. */
	private MapSquare mapSquare;
	/** The parent of this MapSquareWrapper in the search tree. */
	private MapSquareWrapper parent;
	/** The direction the MapSquare was entered from. */
	private Direction direction;
	/** The value of the g-heuristic (i.e. distance travelled so far) of this instance. */
	private float g = -1;
	/** The value of the h-heuristic (i.e. estimated distance to travel to goal) of this instance. */
	private int h = -1;
	/** The height in a jump that Mario is currently. **/
	private int levelInJump = 0;
	/** The width in a jump that Mario is currently. **/
	private int widthOfJump = 0;
	/**
	 * Creates a new MapSquareWrapper.
	 * @param mapSquare - {@link #mapSquare}
	 * @param parent - {@link #parent}
	 * @param levelInJump - {@link #levelInJump}
	 * @param widthOfJump - {@link #widthOfJump}
	 * @param direction - {@link #direction}
	 */
	public MapSquareWrapper(MapSquare mapSquare, MapSquareWrapper parent, int levelInJump, int widthOfJump, Direction direction)
	{
		this.mapSquare = mapSquare;
		this.parent = parent;
		this.levelInJump = levelInJump;
		this.direction = direction;
	}
	/** Gets the MapSquare represented by this MapSquareWrapper. */
	public MapSquare getMapSquare()
	{
		return mapSquare;
	}
	/** Gets the parent of this MapSquareWrapper in the search tree. */
	protected MapSquareWrapper getParent()
	{
		return parent;
	}
	/** Gets the height that Mario is currently in a jump. */
	public int getLevelInJump()
	{
		return levelInJump;
	}
	/** Gets the width that Mario is currently in a jump.*/
	public int getWidthOfJump()
	{
		return widthOfJump;
	}
	/** Whether Mario was falling when he entered this MapSquare. */
	public boolean wasFalling()
	{
		return direction == Direction.Above;
	}
	/** The direction Mario entered this MapSquare. */
	public Direction getDirection()
	{
		return direction;
	}
	/** Checks whether the given {@link MapSquare} is an ancestor of this in this search tree.*/
	public boolean checkParentTreeFor(MapSquare s)
	{
		MapSquareWrapper parent = this.parent;
		while(parent != null)
		{
			if(parent.getMapSquare().equals(s) && !wasFalling())
				/*
				 * If Mario was falling, we ignore the ancestry. This is because Mario may want to 
				 * re-enter squares that he fell through, for example falling from the top of the 
				 * screen to the bottom and then jumping.
				 */
			{
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	/**
	 * @return the {@link MapSquare}s passed through to reach this square.
	 */
	public Stack<MapSquare> backtrackRouteFromHere()
	{
		//Traverses the search tree using the parent variable.
		Stack<MapSquare> result = new Stack<MapSquare>();
		MapSquareWrapper parent = this;
		while(parent.getParent() != null)
		{
			result.push(parent.getMapSquare());
			parent = parent.getParent();
		}
		return result;
	}
	/**
	 * Two {@link MapSquareWrapper}s are equal only if they represent exactly the same data.
	 * A {@link MapSquareWrapper} is equal to a {@link MapSquare} if the {@link MapSquare} 
	 * represented by this instance is equal to the given one.
	 */
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof MapSquareWrapper)
		{
			MapSquareWrapper mapSquareWrapper = (MapSquareWrapper)other;
			return 
			(
					mapSquare.equals(mapSquareWrapper.getMapSquare())
				&&  (parent == null || parent.equals(mapSquareWrapper.getParent().getMapSquare()))
				&&	g == mapSquareWrapper.getG()
				&&	h == mapSquareWrapper.getH()
				&&  wasFalling() == mapSquareWrapper.wasFalling()
			);
		}
		if(other instanceof MapSquare)
		{
			return mapSquare.equals(other);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "{" + mapSquare.toString() + ", " + getG() + getH() + "}";
	}

	//Methods used for running the A* Algorithm
			/**
			 * Calculates the Euclidean distance between this square and a given one.
			 * @param otherSquare - the square to which the distance should be calculated.
			 * @return the (rounded) distance
			 */
			public int calculateH(MapSquare otherSquare)
			{
				h = (int)Point2D.distance(mapSquare.getMapLocationX(), mapSquare.getMapLocationY(), otherSquare.getMapLocationX(), otherSquare.getMapLocationY());
				return h;
			}
			/**
			 * @see #h
			 * @return the estimated distance to a goal
			 */
			public int getH()
			{
				return h;
			}
			/**
			 * @see #h
			 * @param h - the estimated distance to a goal
			 */
			public void setH(int h)
			{
				this.h = h;
			}
			/**
			 * @see #g
			 * @param g - the new distance travelled 
			 */
			public void setG(float g)
			{
				this.g = g;
			}
			/**
			 * @see #g
			 * @return the distance travelled to this MapSquareWrapper in the search tree.
			 */
			public float getG()
			{
				return g;
			}
}
