package ch.idsia.agents.controllers.kbarrett.third;

import java.lang.reflect.Field;
import java.util.Arrays;
/**
 * Stores all valid action arrays.
 * @author Kim Barrett
 */
public class ActionsIndex
{
	// Note: each element represents the following actions: left, right, down, jump, speed, up
	
	//No horizontal movement
	public static boolean[] ai0 = {false, false, false, false, false, false};
	public static boolean[] ai1 = {false, false, true, false, false, false};
	public static boolean[] ai2 = {false, false, false, true, false, false};
	public static boolean[] ai3 = {false, false, false, false, false, true};
	
	//Moving left
	public static boolean[] ai4 = {true, false, false, false, false, false};
	public static boolean[] ai5 = {true, false, true, false, false, false};
	public static boolean[] ai6 = {true, false, false, true, false, false};
	public static boolean[] ai7 = {true, false, false, false, false, true};
	
	//Moving right
	public static boolean[] ai8 = {false, true, false, false, false, false};
	public static boolean[] ai9 = {false, true, true, false, false, false};
	public static boolean[] ai10 = {false, true, false, true, false, false};
	public static boolean[] ai11 = {false, true, false, false, false, true};
	
	//Speed
		//No horizontal movement
		public static boolean[] ai12 = {false, false, false, false, true, false};
		public static boolean[] ai13 = {false, false, true, false, true, false};
		public static boolean[] ai14 = {false, false, false, true, true, false};
		public static boolean[] ai15 = {false, false, false, false, true, true};
		
		//Moving left fast
		public static boolean[] ai16 = {true, false, false, false, true, false};
		public static boolean[] ai17 = {true, false, true, false, true, false};
		public static boolean[] ai18 = {true, false, false, true, true, false};
		public static boolean[] ai19 = {true, false, false, false, true, true};
		
		//Moving right fast
		public static boolean[] ai20 = {false, true, false, false, true, false};
		public static boolean[] ai21 = {false, true, true, false, true, false};
		public static boolean[] ai22 = {false, true, false, true, true, false};
		public static boolean[] ai23 = {false, true, false, false, true, true};
	
	/**
	 * Total number of possible valid arrays.
	 */
	public static final int NumberOfArrayPossibilities = 24;
	
	/**
	 * Finds the numbers associated with the given array.
	 * Note: -1 is returned if the array isn't found.
	 */
	public static int getMatch(boolean[] array)
	{
		//For each of the hard-coded arrays
		for(int i = 0; i < NumberOfArrayPossibilities; ++i)
		{
			try
			{
				//Use reflection to get the correct array
				Field f = ActionsIndex.class.getField("ai" + i);
				boolean[] o = (boolean[])f.get(null);
				//If this is the required array				
				if(Arrays.equals(array,o))
				{
					//Return its index
					return i;
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		//If the array isn't found.
		return -1;
	}
	/**
	 * Finds the array associated with the given number.
	 * @param number - the number of the required array.
	 * @return boolean[] corresponding to the number
	 */
	public static boolean[] getArray(int number)
	{
		try
		{
			//Use reflection to get the corresponding array.
			return (boolean[]) ActionsIndex.class.getField("ai" + number).get(null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	/**
	 * Mutates the array corresponding to the given number.
	 * @param number - the index of the array to mutate.
	 * @param probability - the probability by which each attribute of the array is mutated.
	 * @return the mutated array.
	 */
	public static boolean[] getMutatedArray(int number, float probability)
	{
		//Chose whether to change the speed
		boolean changeSpeed = Math.random() < probability;
		//Chose whether to change the direction
		boolean changeDirection = Math.random() < probability;
		//Chose whether to change whether to jump
		boolean changeJumping = Math.random() < probability;
		
		if(changeSpeed)
		{
			//Changes the index to be the one with opposing speed
			number = (number + 12) % 24;
		}
		if(changeDirection)
		{
			//If currently "speed" is true
			boolean speed = number >= 12;
			//Shift to change direction
			number = (number + 4) % 12;
			if(speed)
			{
				//Add speed offset if required
				number += 12;
			}
		}
		if(changeJumping)
		{
			//If currently "speed" is true
			boolean speed = number >= 12;
			//Stores direction currently facing
			int slr = (int) (speed ? (number - 12) / 3.0 : number / 3.0);
			
			//Shift to change whether jumping or not
			number = (number + 1) % 4;
			//Add direction offset
			number += slr;
			if(speed)
			{
				//Add speed offset
				number += 12;
			}
		}
		//Gets the array corresponding to the new index
		return getArray(number);
	}

}
