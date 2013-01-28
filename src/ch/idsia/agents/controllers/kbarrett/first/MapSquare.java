package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.HashSet;

import ch.idsia.agents.controllers.kbarrett.Encoding;
import ch.idsia.benchmark.mario.engine.sprites.Mario;

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
				//If the square below is "Environment" then he can reach the square above
				addToReachableSquares(getSquareAbove());
			}
			{
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
	public ArrayList<MapSquare> getReachableSquares(int currentJumpHeight, int currentJumpWidth, Direction enteredFrom, int marioMode)
	{
		HashSet<MapSquare> newList = new HashSet<MapSquare>(reachableSquares);
		newList.addAll(getAppropriateSquares(currentJumpHeight, currentJumpWidth, enteredFrom));
		checkHeadButt(newList, requireHeadButtBuffer(marioMode));
		return new ArrayList<MapSquare>(newList);
	}
	public boolean isAlwaysReachable(MapSquare square)
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
	
	private boolean requireHeadButtBuffer(int marioMode)
	{
		switch(marioMode)
		{
		case 2 ://fire
		case 1 ://large
		{
			return true;
		}
		case 0 ://small
		default :
		{
			return false;
		}
		}
	}
	private void checkHeadButt(HashSet<MapSquare> newList, boolean headButtBuffer)
	{
		if(headButtBuffer)
		{
			if(getMapLocationY() > 2 && Encoding.isEnvironment(map.get(getMapLocationY() - 2).get(getMapLocationX())))
			{
				System.out.println("Removing head-butted square");
				newList.remove(getSquareAbove());
			}
			if(getMapLocationY() > 1)
			{
				if(getMapLocationX() > 0 && Encoding.isEnvironment(map.get(getMapLocationY() - 1).get(getMapLocationX() - 1)))
				{
					System.out.println("Removing head-butted square to the left");
					newList.remove(getSquareLeft());
				}
				if(getMapLocationX() < map.get(getMapLocationY() - 1).size() - 1 && Encoding.isEnvironment(map.get(getMapLocationY() - 1).get(getMapLocationX() + 1)))
				{
					System.out.println("Removing head-butted to the right");
					newList.remove(getSquareRight());
				}
			}
		}
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
	
	@Override
	public MapSquare clone()
	{
		MapSquare newSquare = new MapSquare(encoding, map, locationInMapX, locationInMapY);
		return newSquare;
	}
	
	@Override
	public int hashCode()
	{
		return (int) (Math.pow(2, getMapLocationX()) * Math.pow(3, getMapLocationY()));
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
