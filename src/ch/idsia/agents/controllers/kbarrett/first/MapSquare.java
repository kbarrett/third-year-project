package ch.idsia.agents.controllers.kbarrett.first;

import java.util.ArrayList;
import java.util.HashSet;

import ch.idsia.agents.controllers.kbarrett.Encoding;
/**
 * Stores a map location within the map.
 * @author Kim Barrett
 */
public class MapSquare
{
	/**
	 * The encoding represented by this MapSquare.
	 * @see Encoding
	 */
	private byte encoding;
	/**
	 * The squares that are always reachable by this MapSquare.
	 */
	private ArrayList<MapSquare> reachableSquares;
	/**
	 * Where in the map this MapSquare is - x location.
	 */
	private int locationInMapX;
	/**
	 * Where in the map this MapSquare is - y location.
	 */
	private int locationInMapY;
	/** 
	 * The map of which this MapSquare is a member.
	 */
	private ArrayList<ArrayList<MapSquare>> map;
	
	//Data getters & setters
	/** Return the x location in the map where this MapSquare is stored. */
	public int getMapLocationX() {return locationInMapX;}
	/** Return the y location in the map where this MapSquare is stored. */
	public int getMapLocationY() {return locationInMapY;}
	/** 
	 * Gets an int array storing the location in the map, represented by this MapSquare.
	 * Note: [0] stores the y-coordinate and [1] the x-coordinate, but this the same as the map's orientation.
	 */
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
	/** 
	 * Updates knowledge of where this MapSquare represents in the map - used when shifting the map within the array.
	 * @param newX - the new xcoordinate
	 * @param newY - the new ycoordinate
	 * @param map - the new map
	 */
	public void setLocInMap(int newX, int newY, ArrayList<ArrayList<MapSquare>> map)
	{
		locationInMapX = newX;
		locationInMapY = newY;
		this.map = map;
	}
	/**
	 * Adds this square to {@link #reachableSquares} if it's not null.
	 * @param square - the square to be added.
	 */
	private void addToReachableSquares(MapSquare square)
	{
		if(square!=null && !reachableSquares.contains(square))
		{
			reachableSquares.add(square);
		}
	}
	/**
	 * @return the square above this one in the map.
	 */
	public MapSquare getSquareAbove()
	{
		if(locationInMapY <= 0) {return null;}
		return map.get(locationInMapY - 1).get(locationInMapX);
	}
	/**
	 * @return the square below this one in the map.
	 */
	public MapSquare getSquareBelow()
	{
		if(locationInMapY >= map.size() - 1) {return null;}
		return map.get(locationInMapY + 1).get(locationInMapX);
	}
	/**
	 * @return the square to the left of this one in the map.
	 */
	public MapSquare getSquareLeft()
	{
		if(locationInMapX <= 0) {return null;}
		return map.get(locationInMapY).get(locationInMapX - 1);
	}
	/**
	 * @return the square to the right of this one in the map.
	 */
	public MapSquare getSquareRight()
	{
		if(locationInMapX >= map.get(0).size() - 1) {return null;}
		return map.get(locationInMapY).get(locationInMapX + 1);
	}
	/**
	 * Calculate squares that are always reachable from this one.
	 */
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
			//Mario can jump if the square below him is occupied but the one above him is not.
			if(!Encoding.isEnvironment(getSquareAbove()) && Encoding.isEnvironment(getSquareBelow()))
			{
				//If the square below is "Environment" then he can reach the square above
				addToReachableSquares(getSquareAbove());
			}
			
			//Mario can fall if the square below him is empty
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
	
	/**
	 * @return byte representation of what is in this location of the map.
	 * @see Encoding
	 */
	public byte getEncoding() {
		return encoding;
	}
	
	/**
	 * Changes the byte representation of what is in this location of the map.
	 * @param encoding - the new encoding.
	 */
	public void setEncoding(byte encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Calculates environment specific reachable squares and returns them along with ones that are always reachable.
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
	/**
	 * @param square
	 * @return whether it is always possible for Mario to move between this square and the given square.
	 */
	public boolean isAlwaysReachable(MapSquare square)
	{
		return reachableSquares.contains(square);
	}
	
	/**
	 * Calculate scenario specific reachable squares.
	 * @param currentJumpHeight - the height Mario has reached in a jump.
	 * @param currentJumpWidth - the x-distance Mario has currently travelled while jumping.
	 * @param enteredFrom - the direction from which this square was entered.
	 * @return environment-specific reachable squares corresponding to the provided facts about the current scenario.
	 */
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
	
	/**
	 * Whether Mario is large enough to require a buffer to prevent his head from colliding with things when moving.
	 * @param marioMode - the mode Mario is currently in.
	 * @return true if Mario requires a head butt buffer.
	 */
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
	
	/**
	 * Removes squares that Mario can't enter because he would bang his head by doing so.
	 * @param newList - the current list of reachable squares
	 * @param headButtBuffer - whether Mario needs a headbutt buffer.
	 */
	private void checkHeadButt(HashSet<MapSquare> newList, boolean headButtBuffer)
	{
		if(headButtBuffer)
		{
			if(getMapLocationY() > 2 && Encoding.isEnvironment(map.get(getMapLocationY() - 2).get(getMapLocationX())))
			{
				//System.out.println("Removing head-butted square");
				newList.remove(getSquareAbove());
			}
			if(getMapLocationY() > 1)
			{
				if(getMapLocationX() > 0 && Encoding.isEnvironment(map.get(getMapLocationY() - 1).get(getMapLocationX() - 1)))
				{
					//System.out.println("Removing head-butted square to the left");
					newList.remove(getSquareLeft());
				}
				if(getMapLocationX() < map.get(getMapLocationY() - 1).size() - 1 && Encoding.isEnvironment(map.get(getMapLocationY() - 1).get(getMapLocationX() + 1)))
				{
					//System.out.println("Removing head-butted to the right");
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
	/**
	 * Two {@link MapSquare}s are equal if they are in the same location on the map.
	 */
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
	/**
	 * Creates a new MapSquare with the exact same data as this one.
	 */
	@Override
	public MapSquare clone()
	{
		MapSquare newSquare = new MapSquare(encoding, map, locationInMapX, locationInMapY);
		return newSquare;
	}
	/**
	 * Hashes this objects location in the map.
	 */
	@Override
	public int hashCode()
	{
		return (int) (Math.pow(2, getMapLocationX()) * Math.pow(3, getMapLocationY()));
	}
	/**
	 * Represents from which direction Mario entered the current square.
	 * @author Kim Barrett
	 */
	public enum Direction
	{
		Above,
		Below,
		Left,
		Right,
		None
	}
}
