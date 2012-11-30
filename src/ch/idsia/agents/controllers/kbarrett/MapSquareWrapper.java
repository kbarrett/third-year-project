package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;
import java.util.Stack;

import ch.idsia.agents.controllers.kbarrett.MapSquare.Direction;

public class MapSquareWrapper {
	
	private MapSquare mapSquare;
	private MapSquareWrapper parent;
	private Direction direction;
	private int g = -1;
	private int h = -1;
	private int levelInJump = 0;
	private int widthOfJump = 0;
	
	public MapSquareWrapper(MapSquare mapSquare, MapSquareWrapper parent, int levelInJump, int widthOfJump, Direction direction)
	{
		this.mapSquare = mapSquare;
		this.parent = parent;
		this.levelInJump = levelInJump;
		this.direction = direction;
	}
	
	public MapSquare getMapSquare()
	{
		return mapSquare;
	}
	
	protected MapSquareWrapper getParent()
	{
		return parent;
	}
	
	public int getLevelInJump()
	{
		return levelInJump;
	}

	public int getWidthOfJump()
	{
		return widthOfJump;
	}
	
	public boolean wasFalling()
	{
		return direction == Direction.Above;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	public boolean checkParentTreeFor(MapSquare s)
	{
		MapSquareWrapper parent = this.parent;
		while(parent != null)
		{
			if(parent.getMapSquare().equals(s) && !wasFalling())
			{
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	public Stack<MapSquare> backtrackRouteFromHere()
	{
		Stack<MapSquare> result = new Stack<MapSquare>();
		MapSquareWrapper parent = this;
		while(parent.getParent() != null)
		{
			result.push(parent.getMapSquare());
			parent = parent.getParent();
		}
		return result;
	}
	
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

	//A* Stuff
		
			public int calculateH(MapSquare otherSquare)
			{
				h = (int)Point2D.distance(mapSquare.getMapLocationX(), mapSquare.getMapLocationY(), otherSquare.getMapLocationX(), otherSquare.getMapLocationY());
				return h;
			}
			public int getH()
			{
				return h;
			}
			public void setH(int h)
			{
				this.h = h;
			}
			public void setG(int g)
			{
				this.g = g;
			}
			public int getG()
			{
				return g;
			}
}
