package ch.idsia.agents.controllers.kbarrett;

import java.util.ArrayList;
import java.util.HashSet;

public class MapSquare {

	private byte encoding;
	private ArrayList<MapSquare> reachableSquares;
	private int locationInMapX;
	private int locationInMapY;
	private ArrayList<ArrayList<MapSquare>> map;
	
	//Data getters & setters
	public int getMapLocationX() {return locationInMapX;}
	public int getMapLocationY() {return locationInMapY;}
	public int[] getMapLocation() 
	{
		int[] mapLoc = new int[2];
		mapLoc[0] = getMapLocationY();
		mapLoc[1] = getMapLocationX();
		return mapLoc;
	}
	
	//Constructor
	public MapSquare(byte encoding, ArrayList<ArrayList<MapSquare>> map, int locationInMapX, int locationInMapY)
	{
		this.encoding = encoding;
		this.locationInMapX = locationInMapX;
		this.locationInMapY = locationInMapY;
		this.map = map;
	}
	
	public void setLocInMap(int newX, int newY, ArrayList<ArrayList<MapSquare>> map)
	{
		locationInMapX = newX;
		locationInMapY = newY;
		this.map = map;
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
		return map.get(locationInMapY - 1).get(locationInMapX);
	}
	public MapSquare getSquareBelow()
	{
		if(locationInMapY >= map.size() - 1) {return null;}
		return map.get(locationInMapY + 1).get(locationInMapX);
	}
	public MapSquare getSquareLeft()
	{
		if(locationInMapX <= 0) {return null;}
		return map.get(locationInMapY).get(locationInMapX - 1);
	}
	public MapSquare getSquareRight()
	{
		if(locationInMapX >= map.get(0).size() - 1) {return null;}
		return map.get(locationInMapY).get(locationInMapX + 1);
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
			
			if(!Encoding.isEnvironment(getSquareAbove()) && Encoding.isEnvironment(getSquareBelow()))
			{
				//He can reach the square above
				addToReachableSquares(getSquareAbove());
			}
			if (!Encoding.isEnvironment(getSquareBelow()))
			{
				//He can reach the square below, because it is empty
				addToReachableSquares(getSquareBelow());
			}
		//Horizontal
			else //can only move left/right if there's ground below us
			{
				//If the square to the left is empty
				if(!Encoding.isEnvironment(getSquareLeft()))
				{
					//He can reach the square to the left
					addToReachableSquares(getSquareLeft());
				}
				//If the square to the right is empty
				if(!Encoding.isEnvironment(getSquareRight()))
				{
					//He can reach the square to the right
					addToReachableSquares(getSquareRight());
				}
			}
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
	public ArrayList<MapSquare> getReachableSquares(int currentJumpHeight, int currentJumpWidth, Direction enteredFrom)
	{
		HashSet<MapSquare> newList = new HashSet<MapSquare>(reachableSquares);
		newList.addAll(getAppropriateSquares(currentJumpHeight, currentJumpWidth, enteredFrom));
		return new ArrayList<MapSquare>(newList);
	}
	public boolean isReachable(MapSquare square)
	{
		return reachableSquares.contains(square);
	}
	private ArrayList<MapSquare> getAppropriateSquares(int currentJumpHeight, int currentJumpWidth, Direction enteredFrom)
	{
		ArrayList<MapSquare> squares = new ArrayList<MapSquare>();
		//if falling, add left & right
		if(enteredFrom == Direction.Above)
		{
			if(getSquareLeft()!=null && !Encoding.isEnvironment(getSquareLeft()))
			{
				squares.add(getSquareLeft());
			}
			if(getSquareRight()!=null && !Encoding.isEnvironment(getSquareRight()))
			{
				squares.add(getSquareRight());
			}
		}
		//if jumping, add above & left & right
		if(currentJumpHeight > 0)
		{
			if(currentJumpHeight < Movement.MAX_JUMP_HEIGHT && getSquareAbove()!=null && !Encoding.isEnvironment(getSquareAbove()))
			{
				squares.add(getSquareAbove());
			}
			if(enteredFrom == Direction.Below) //can't move left/right indefinitely
			{
				if(getSquareLeft()!=null && !Encoding.isEnvironment(getSquareLeft()))
				{
					squares.add(getSquareLeft());
				}
				if(getSquareRight()!=null && !Encoding.isEnvironment(getSquareRight()))
				{
					squares.add(getSquareRight());
				}
			}
		}
		return squares;
	}
	
	@Override
	public String toString()
	{
		return ""+encoding + " at " + getMapLocationY() + "," + getMapLocationX();
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
	
	public enum Direction
	{
		Above,
		Below,
		Left,
		Right,
		None
	}
}
