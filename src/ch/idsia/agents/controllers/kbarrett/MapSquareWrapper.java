package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;

public class MapSquareWrapper {
	
	private MapSquare mapSquare;
	private int g = -1;
	private int h = -1;
	
	public MapSquareWrapper(MapSquare mapSquare)
	{
		this.mapSquare = mapSquare;
	}
	
	public MapSquare getMapSquare()
	{
		return mapSquare;
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
