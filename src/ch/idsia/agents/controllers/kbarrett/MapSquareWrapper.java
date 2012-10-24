package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;

public class MapSquareWrapper {
	
	private MapSquare mapSquare;
	private MapSquareWrapper parent;
	private int g = -1;
	private int h = -1;
	
	public MapSquareWrapper(MapSquare mapSquare, MapSquareWrapper parent)
	{
		this.mapSquare = mapSquare;
		this.parent = parent;
	}
	
	public MapSquare getMapSquare()
	{
		return mapSquare;
	}
	
	protected MapSquareWrapper getParent()
	{
		return parent;
	}
	
	public boolean checkParentTreeFor(MapSquare s)
	{
		MapSquareWrapper parent = this.parent;
		while(parent != null)
		{
			if(parent.getMapSquare().equals(s))
			{
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	public MapSquare[] backtrackRouteFromHere()
	{
		MapSquare[] result = new MapSquare[getG()];
		int i = getG();
		MapSquareWrapper parent = this.parent;
		while(parent != null)
		{
			result[i--] = parent.getMapSquare();
			parent = this.parent;
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
					g == mapSquareWrapper.getG()
				&&	h == mapSquareWrapper.getH()
				&&	mapSquare.equals(mapSquareWrapper.getMapSquare());
		}
		if(other instanceof MapSquare)
		{
			return mapSquare.equals(other);
		}
		return false;
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
