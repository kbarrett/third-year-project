package ch.idsia.agents.controllers.kbarrett;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MapSquare {

	private byte encoding;
	private ArrayList<MapSquare> reachableSquares;
	private int locationInMapX;
	private int locationInMapY;
	
	//Data getters & setters
	public int getMapLocationX() {return locationInMapX;}
	public int getMapLocationY() {return locationInMapY;}
	
	//Constructor
	public MapSquare(byte encoding, MapSquare[][] map, int locationInMapX, int locationInMapY)
	{
		this.encoding = encoding;
		this.locationInMapX = locationInMapX;
		this.locationInMapY = locationInMapY;
		workOutReachableSquares(map);
	}
	
	private void workOutReachableSquares(MapSquare[][] map)
	{
		//If the current square has an environment piece in it, Mario cannot get to it & therefore can't reach anywhere else from it.
		if(Encoding.isEnvironment((byte) encoding))
		{
			reachableSquares = new ArrayList<MapSquare>(0);
			return;
		}
		//Otherwise work out reachable squares
		reachableSquares = new ArrayList<MapSquare>();
			//Vertical
				
				if(canJump(map))
				{
					//He can reach the square above
					reachableSquares.add(map[locationInMapY - 1][locationInMapX]);
				}
				else if (locationInMapY != map.length - 1)
				{
					//He can reach the square below, because it is empty
					reachableSquares.add(map[locationInMapY + 1][locationInMapX]);
				}
			//Horizontal
				//If the square to the left is empty
				if(locationInMapX != map[0].length - 1 && !Encoding.isEnvironment(map[locationInMapY][locationInMapX + 1]))
				{
					//He can reach the square to the left
					reachableSquares.add(map[locationInMapY][locationInMapX + 1]);
				}
				//If the square to the right is empty
				if(locationInMapX != 0 && Encoding.isEnvironment(map[locationInMapY][locationInMapX - 1]))
				{
					//He can reach the square to the right
					reachableSquares.add(map[locationInMapY][locationInMapX - 1]);
				}
		if(FirstAgent.debug){System.out.println(this + ":" + reachableSquares);}
	}
	
	private boolean canJump(MapSquare[][] map)
	{
		/*//If the square below isn't empty, then Mario can jump
		(locationInMapY != 0 && locationInMapY != map.length - 1 && Encoding.isEnvironment(map[locationInMapY + 1][locationInMapX])) ||
		//Or if the squares below him mean this one is jumpable to
		(false)*/
		
		//If the location above isn't empty, then we can't jump
		if(locationInMapY != 0 && map[locationInMapY - 1][locationInMapX]!=null && map[locationInMapY - 1][locationInMapX].getEncoding()!= Encoding.NOTHING) {return false;}
		
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
		this.encoding = encoding;
		//TODO: update reachable squares if this changes
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
	
	//A* Stuff
	private int g = 0;
	private int h;
	
		public int calculateH(MapSquare otherSquare)
		{
			h = (int)Point2D.distance(getMapLocationX(), getMapLocationY(), otherSquare.getMapLocationX(), otherSquare.getMapLocationY());
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
