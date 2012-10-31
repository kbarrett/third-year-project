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
	}
	
	public void setLocInMap(int newX, int newY)
	{
		locationInMapX = newX;
		locationInMapY = newY;
	}
	
	private void addToReachableSquares(MapSquare square)
	{
		if(square!=null && !reachableSquares.contains(square))
		{
			reachableSquares.add(square);
		}
	}
	
	public MapSquare getSquareAbove()
	{
		if(locationInMapY <= 0) {return null;}
		return map[locationInMapY - 1][locationInMapX];
	}
	public MapSquare getSquareBelow()
	{
		if(locationInMapY >= map.length - 1) {return null;}
		return map[locationInMapY + 1][locationInMapX];
	}
	private MapSquare getSquareLeft()
	{
		if(locationInMapX <= 0) {return null;}
		return map[locationInMapY][locationInMapX - 1];
	}
	private MapSquare getSquareRight()
	{
		if(locationInMapX >= map[0].length - 1) {return null;}
		return map[locationInMapY][locationInMapX + 1];
	}
	
	public void workOutReachableSquares()
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
			reachableSquares = new ArrayList<MapSquare>(4);
		}
		else
		{
			reachableSquares.clear();
		}
			//Vertical
				
				if(canJump(map))
				{
					//He can reach the square above
					addToReachableSquares(getSquareAbove());
				}
				if (locationInMapY < map.length - 1 && !Encoding.isEnvironment(getSquareBelow()))
				{
					//He can reach the square below, because it is empty
					addToReachableSquares(getSquareBelow());
				}
			//Horizontal
				//If the square to the left is empty
				if(locationInMapX > 0 && !Encoding.isEnvironment(getSquareLeft()))
				{
					//He can reach the square to the left
					addToReachableSquares(getSquareLeft());
				}
				//If the square to the right is empty
				if(locationInMapX < map[0].length - 1 && !Encoding.isEnvironment(getSquareRight()))
				{
					//He can reach the square to the right
					addToReachableSquares(getSquareRight());
				}
	}
	
	private boolean canJump(MapSquare[][] map)
	{
		/*//If the square below isn't empty, then Mario can jump
		(locationInMapY != 0 && locationInMapY != map.length - 1 && Encoding.isEnvironment(map[locationInMapY + 1][locationInMapX])) ||
		//Or if the squares below him mean this one is jumpable to
		(false)*/
		
		//If the location above isn't empty, then we can't jump
		if(locationInMapY > 0)
		{
			if(getSquareAbove()==null || getSquareAbove().getEncoding()!= Encoding.NOTHING) {return false;}
			if(getSquareBelow()==null || getSquareBelow().getEncoding()== Encoding.NOTHING) {return false;}
			return true;
		}
		return false;
		
	}
	private boolean checkCanJumpHigher(int currentHeight)
	{
		if(currentHeight == 0) {return false;}
		//If there isn't a floor within MAXJUMPHEIGHT of this square, we can't jump
		if(currentHeight<Movement.MAX_JUMP_HEIGHT)
		{
			return getSquareAbove()==null || 
					getSquareAbove().getEncoding()== Encoding.NOTHING;
		}
		return false;
	}
	private ArrayList<MapSquare> checkCanMoveDirection(ArrayList<MapSquare> newList)
	{
		if(getSquareRight() != null && !Encoding.isEnvironment(getSquareRight().getEncoding()))
		{
			if(!newList.contains(getSquareRight()))
			{
				newList.add(getSquareRight());
			}
		}
		if(getSquareLeft() != null && !Encoding.isEnvironment(getSquareLeft().getEncoding()))
		{
			if(!newList.contains(getSquareLeft()))
			{
				newList.add(getSquareLeft());
			}
		}
		return newList;
	}
	
	public byte getEncoding() {
		return encoding;
	}

	public void setEncoding(byte encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * 
	 * @param currentJumpHeight - the current square height we are in the jump
	 * @param enteredFromAbove - whether or not this square was entered from above (i.e. whilst falling)
	 * @return list of all MapSquares that can be entered from this one in the current circumstances
	 */
	public ArrayList<MapSquare> getReachableSquares(int currentJumpHeight, boolean enteredFromAbove)
	{
		ArrayList<MapSquare> newList = (ArrayList<MapSquare>)reachableSquares.clone();
		if(getSquareAbove() != null && checkCanJumpHigher(currentJumpHeight))
		{ 
			newList.add(getSquareAbove());
		}
		if(enteredFromAbove)
		{
			newList = checkCanMoveDirection(newList);
		}
		
		return newList;
	}
	public boolean isReachable(MapSquare square)
	{
		return reachableSquares.contains(square);
	}
	
	@Override
	public String toString()
	{
		return locationInMapY + "," + locationInMapX;
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
