package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MapSquare {

	private byte encoding;
	private ArrayList<MapSquare> reachableSquares;
	private int locationInMapX;
	private int locationInMapY;
	private MapSquare[][] map;
	
	//Data getters & setters
	public int getMapLocationX() {return locationInMapX;}
	public int getMapLocationY() {return locationInMapY;}
	
	//Constructor
	public MapSquare(byte encoding, MapSquare[][] map, int locationInMapX, int locationInMapY)
	{
		this.encoding = encoding;
		this.locationInMapX = locationInMapX;
		this.locationInMapY = locationInMapY;
		this.map = map;
		workOutReachableSquares();
	}
	
	public void setLocInMap(int newX, int newY)
	{
		locationInMapX = newX;
		locationInMapY = newY;
	}
	
	private MapSquare getSquareAbove()
	{
		if(locationInMapY <= 0) {return null;}
		return map[locationInMapY - 1][locationInMapX];
	}
	private MapSquare getSquareBelow()
	{
		if(locationInMapY > map.length - 1) {return null;}
		return map[locationInMapY + 1][locationInMapX];
	}
	private MapSquare getSquareLeft()
	{
		if(locationInMapX <= 0) {return null;}
		return map[locationInMapY][locationInMapX - 1];
	}
	private MapSquare getSquareRight()
	{
		if(locationInMapX > map[0].length - 1) {return null;}
		return map[locationInMapY][locationInMapX + 1];
	}
	
	private void workOutReachableSquares()
	{
		//If the current square has an environment piece in it, Mario cannot get to it & therefore can't reach anywhere else from it.
		if(Encoding.isEnvironment((byte) encoding))
		{
			reachableSquares = new ArrayList<MapSquare>(0);
			return;
		}
		//Otherwise work out reachable squares
		if(reachableSquares==null)
		{
			reachableSquares = new ArrayList<MapSquare>();
		}
		else
		{
			reachableSquares.clear();
		}
			//Vertical
				
				if(locationInMapY > 0 && canJump(map))
				{
					//He can reach the square above
					reachableSquares.add(getSquareAbove());
				}
				if (locationInMapY < map.length - 1 && !Encoding.isEnvironment(getSquareBelow()))
				{
					//He can reach the square below, because it is empty
					reachableSquares.add(getSquareBelow());
				}
			//Horizontal
				//If the square to the left is empty
				if(locationInMapX > 0 && !Encoding.isEnvironment(getSquareLeft()))
				{
					//He can reach the square to the left
					reachableSquares.add(getSquareLeft());
				}
				//If the square to the right is empty
				if(locationInMapX < map[0].length - 1 && Encoding.isEnvironment(getSquareRight()))
				{
					//He can reach the square to the right
					reachableSquares.add(getSquareRight());
				}
	}
	
	private boolean canJump(MapSquare[][] map)
	{
		/*//If the square below isn't empty, then Mario can jump
		(locationInMapY != 0 && locationInMapY != map.length - 1 && Encoding.isEnvironment(map[locationInMapY + 1][locationInMapX])) ||
		//Or if the squares below him mean this one is jumpable to
		(false)*/
		
		//If the location above isn't empty, then we can't jump
		if(locationInMapY != 0 && getSquareAbove()!=null && getSquareAbove().getEncoding()!= Encoding.NOTHING) {return false;}
		
		//If there isn't a floor within MAXJUMPHEIGHT of this square, we can't jump
		for(int i = 1; i < Movement.MAX_JUMP_HEIGHT; ++i)
		{
			if(locationInMapY + i < map.length && Encoding.isEnvironment(map[locationInMapY + i][locationInMapX])) {return true;}
		}
		
		return false;
	}
	
	public byte getEncoding() {
		return encoding;
	}

	public void setEncoding(byte encoding) {
		boolean changed = (encoding != this.encoding);
		this.encoding = encoding;
		if(changed)
		{
			workOutReachableSquares();
		}
	}
	
	public ArrayList<MapSquare> getReachableSquares()
	{
		return reachableSquares;
	}
	public boolean isReachable(MapSquare square)
	{
		return reachableSquares.contains(square);
	}
	
	@Override
	public String toString()
	{
		return locationInMapX + "," + locationInMapY;
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		if(otherObject instanceof MapSquare)
		{
			MapSquare otherMapSquare = (MapSquare)otherObject;
			return otherMapSquare.getMapLocationX() == getMapLocationX() && otherMapSquare.getMapLocationY() == getMapLocationY();
		}
		else
		{
			return false;
		}
	}
}
